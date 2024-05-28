package com.sparta.springpersonaltaskv2.service;

import com.sparta.springpersonaltaskv2.dto.SignupRequestDto;
import com.sparta.springpersonaltaskv2.entity.User;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import com.sparta.springpersonaltaskv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

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
}