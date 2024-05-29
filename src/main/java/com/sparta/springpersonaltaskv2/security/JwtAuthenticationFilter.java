package com.sparta.springpersonaltaskv2.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springpersonaltaskv2.dto.LoginRequestDto;
import com.sparta.springpersonaltaskv2.dto.TokenDto;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.repository.RefreshTokenRedisRepository;
import com.sparta.springpersonaltaskv2.util.IpUtil;
import com.sparta.springpersonaltaskv2.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleType role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        TokenDto token = jwtUtil.createToken(username, role);

        String ipAddress = IpUtil.getClientIp(request);

        // Redis에 RefreshToken 저장
        jwtUtil.addRefreshTokenInRedis(ipAddress, username, role, token);

        response.addHeader(JwtUtil.AUTH_ACCESS_HEADER, token.getGrantType() + token.getAccessToken());
        response.addHeader(JwtUtil.AUTH_REFRESH_HEADER, token.getGrantType() + token.getRefreshToken());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

}