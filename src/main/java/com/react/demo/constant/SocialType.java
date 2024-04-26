package com.react.demo.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SocialType {
    KAKAO("kakao", "카카오 소셜 유저"),
    GOOGLE("google", "구글 소셜 유저"),
    OWN("own", "자체 유저");

    private final String key;
    private final String title;
}
