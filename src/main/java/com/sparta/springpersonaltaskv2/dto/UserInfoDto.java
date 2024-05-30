package com.sparta.springpersonaltaskv2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    String username; // 회원 이름
    boolean isAdmin; // 관리자인지
}