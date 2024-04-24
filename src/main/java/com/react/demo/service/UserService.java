package com.react.demo.service;

import com.react.demo.dto.LoginDto;
import com.react.demo.dto.TokenRequest;
import com.react.demo.dto.TokenResponse;
import com.react.demo.dto.UserFormDto;
import com.react.demo.entity.RefreshToken;
import com.react.demo.entity.User;
import com.react.demo.jwt.TokenProvider;
import com.react.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public boolean validateUser(UserFormDto dto) {
        return userRepository.findById(dto.getId()).orElse(null) == null;
    }

    public void signUp(UserFormDto dto) {
        if(!validateUser(dto)) throw new RuntimeException("이미 존재하는 아이디 입니다");
        User saveUser = User.createUser(dto, passwordEncoder);
        userRepository.save(saveUser);
    }

    public TokenResponse login(LoginDto dto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getId(), dto.getPassword());
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        User user = userRepository.findById(authentication.getName())
                .orElseThrow(EntityNotFoundException::new);
        String newRefreshToken = tokenProvider.createRefreshToken(Duration.ofDays(1));
        RefreshToken existRefreshToken = refreshTokenService.findByUser(user);
        if(existRefreshToken == null) {
            refreshTokenService.saveToken(new RefreshToken(user, newRefreshToken));
        }else {
            existRefreshToken.update(newRefreshToken);
        }
        String accessToken = tokenProvider.createAccessToken(user, Duration.ofHours(2));

        return new TokenResponse(accessToken, newRefreshToken, user.getRole().getKey());
    }

    public void logout(TokenRequest request) {
        refreshTokenService.removeToken(request.getRefreshToken());
    }

    public TokenResponse tokenRefresh(TokenRequest request) throws Exception {
        if(!tokenProvider.validateToken(request.getRefreshToken()))
            throw new IllegalAccessException("Unexpected token");

        RefreshToken refreshToken = refreshTokenService
                .findByRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();

        String accessToken = tokenProvider.createAccessToken(user, Duration.ofHours(2));
        String newRefreshToken = refreshToken
                        .update(tokenProvider.createRefreshToken(Duration.ofDays(1)))
                        .getRefreshToken();

        return new TokenResponse(accessToken, newRefreshToken, user.getRole().getKey());

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다"));
    }
}
