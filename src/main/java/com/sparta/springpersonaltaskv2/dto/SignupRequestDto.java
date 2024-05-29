package com.sparta.springpersonaltaskv2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @NotEmpty(message = "아이디를 입력해주세요.")
    private String username;

    @NotEmpty(message = "비밀번호를 입력해주세요.")
    private String password;

    @Email
    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;

    private boolean admin = false;

    private String adminToken = "";
}