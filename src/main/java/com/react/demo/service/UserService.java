package com.react.demo.service;

import com.react.demo.dto.LoginDto;
import com.react.demo.dto.TokenResponse;
import com.react.demo.dto.UserFormDto;
import com.react.demo.entity.User;
import com.react.demo.config.jwt.TokenProvider;
import com.react.demo.repository.UserRepository;
import com.react.demo.util.CookieUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.time.Duration;

import static com.react.demo.constant.TokenConstant.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean validateUser(UserFormDto dto) {
        return userRepository.findById(dto.getId()).orElse(null) == null;
    }

    public void signUp(UserFormDto dto) {
        if(!validateUser(dto)) throw new RuntimeException("이미 존재하는 아이디 입니다");
        User saveUser = User.createUser(dto, passwordEncoder);
        userRepository.save(saveUser);
    }

    public TokenResponse login(LoginDto dto, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getId(), dto.getPassword());
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        User user = userRepository.findById(authentication.getName())
                .orElseThrow(EntityNotFoundException::new);

        String refreshToken = refreshTokenService.createNewToken(user);
        addRefreshTokenToCookie(request, response, refreshToken);
        String accessToken = tokenProvider.createAccessToken(user, Duration.ofHours(2));

        return new TokenResponse(accessToken, user.getRole().getKey());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
        if(refreshTokenCookie==null) return;
        String refreshToken = refreshTokenCookie.getValue();
        refreshTokenService.removeToken(refreshToken);
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
    }

    public TokenResponse tokenRefresh(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
        if(refreshTokenCookie==null)
            throw new RuntimeException("토큰 정보가 존재하지 않습니다");
        String refreshToken = refreshTokenCookie.getValue();
        if(!tokenProvider.validateToken(refreshToken))
            throw new IllegalAccessException("Unexpected token");
        User user = refreshTokenService.findByRefreshToken(refreshToken).getUser();
        String accessToken = tokenProvider.createAccessToken(user, Duration.ofHours(2));
        String newRefreshToken = refreshTokenService.createNewToken(user);
        addRefreshTokenToCookie(request, response, newRefreshToken);

        return new TokenResponse(accessToken, user.getRole().getKey());
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다"));
    }

}
