package com.react.demo.service;

import com.react.demo.entity.RefreshToken;
import com.react.demo.entity.User;
import com.react.demo.config.jwt.TokenProvider;
import com.react.demo.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    public RefreshToken findByUser(User user) {
        return refreshTokenRepository.findByUser(user).orElse(null);
    }

    public String createNewToken(User user) {
        String newRefreshToken = tokenProvider.createRefreshToken(Duration.ofDays(1));
        RefreshToken existRefreshToken = findByUser(user);
        if(existRefreshToken == null) {
            refreshTokenRepository.save(new RefreshToken(user, newRefreshToken));
        }else {
            existRefreshToken.update(newRefreshToken);
        }
        return newRefreshToken;
    }

    public void removeToken(String refreshToken) {
        RefreshToken findToken = refreshTokenRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(EntityNotFoundException::new);
        refreshTokenRepository.delete(findToken);
    }
}
