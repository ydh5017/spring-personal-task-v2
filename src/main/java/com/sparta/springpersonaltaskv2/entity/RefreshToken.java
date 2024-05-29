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
    private String id;

    private String ip;

    private UserRoleType role;

    @Indexed
    private String refreshToken;
}
