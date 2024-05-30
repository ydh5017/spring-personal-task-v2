package com.sparta.springpersonaltaskv2.entity;

import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = 1209600)
public class RefreshToken {

    @Id
    private String id;              // 회원 이름

    private String ip;              // 로그인 시도한 IP

    private UserRoleType role;      // 회원 권한

    @Indexed
    private String refreshToken;    // refreshToken
}
