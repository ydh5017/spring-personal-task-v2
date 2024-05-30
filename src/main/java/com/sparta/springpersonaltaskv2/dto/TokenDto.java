package com.sparta.springpersonaltaskv2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenDto {
    private String grantType;                   // Token 식별자
    private String accessToken;                 // accessToken
    private Long accessTokenExpirationTime;     // accessToken 만료시간
    private String refreshToken;                // refreshToken
    private Long refreshTokenExpirationTime;    // refreshToken 만료시간
}
