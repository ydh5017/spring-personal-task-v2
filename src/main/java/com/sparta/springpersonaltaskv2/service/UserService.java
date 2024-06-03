package com.sparta.springpersonaltaskv2.service;

import com.sparta.springpersonaltaskv2.dto.SignupRequestDto;
import com.sparta.springpersonaltaskv2.dto.TokenDto;
import com.sparta.springpersonaltaskv2.entity.User;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import com.sparta.springpersonaltaskv2.repository.UserRepository;
import com.sparta.springpersonaltaskv2.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    /**
     * 회원가입
     * @param requestDto 회원가입 정보
     */
    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new ScheduleException(ErrorCodeType.DUPLICATED_USER);
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new ScheduleException(ErrorCodeType.DUPLICATED_EMAIL);
        }

        // 사용자 ROLE 확인
        UserRoleType role = UserRoleType.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new ScheduleException(ErrorCodeType.INVALID_ADMINCODE);
            }
            role = UserRoleType.ADMIN;
        }

        // 사용자 등록
        User user = new User(username, password, email, role);
        userRepository.save(user);
    }

    public TokenDto tokenReissuance(String refreshToken, HttpServletRequest req) {
        String tokenValue = refreshToken.substring(7);
        if (jwtUtil.isRefreshToken(tokenValue)) {
            return jwtUtil.reissuanceToken(tokenValue, req);
        }
        return null;
    }
}