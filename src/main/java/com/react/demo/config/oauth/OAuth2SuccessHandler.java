package com.react.demo.config.oauth;

import com.react.demo.constant.SocialType;
import com.react.demo.dto.TokenResponse;
import com.react.demo.entity.User;
import com.react.demo.config.jwt.TokenProvider;
import com.react.demo.repository.UserRepository;
import com.react.demo.service.RefreshTokenService;
import com.react.demo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.react.demo.constant.TokenConstant.*;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${frontDomain}")
    private String frontDomain;

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        SocialType type = getOAuthType(Objects.requireNonNull(WebUtils.getCookie(request, TYPE)).getValue());
        String userId = getUserId(type, attributes);
        User user = userService.findById(userId);
        String refreshToken = refreshTokenService.createNewToken(user);
        addRefreshTokenToCookie(request, response, refreshToken);
        String accessToken = tokenProvider.createAccessToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(request, new TokenResponse(accessToken, user.getRole().getKey()));
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request,
                                               HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private SocialType getOAuthType(String type) {
        if(type.equals("kakao"))
            return SocialType.KAKAO;
        if(type.equals("google"))
            return SocialType.GOOGLE;
        return null;
    }

    private String getUserId(SocialType type, Map<String, Object> attributes) {
        if(type== SocialType.GOOGLE) {
            return attributes.get("email") + "_" + SocialType.GOOGLE.getKey();

        }else if(type== SocialType.KAKAO) {
            if(attributes.get("kakao_account") instanceof Map<?, ?> kakaoAccount)
                return kakaoAccount.get("email") + "_" + SocialType.KAKAO.getKey();
        }
        return null;
    }

    private String getTargetUrl(HttpServletRequest request, TokenResponse response) {
        Cookie cookie = WebUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME);
        String redirectPath = frontDomain + (cookie==null ? "/" : cookie.getValue());

        return UriComponentsBuilder.fromUriString(redirectPath)
                .queryParam("access_token", response.getAccessToken())
                .queryParam("authority", response.getAuthority())
                .build()
                .toUriString();
    }


}
