package com.sparta.springpersonaltaskv2.util;

import com.sparta.springpersonaltaskv2.entity.RefreshToken;
import com.sparta.springpersonaltaskv2.dto.TokenDto;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.repository.RefreshTokenRedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {
    // Header KEY 값
    public static final String AUTH_ACCESS_HEADER = "X-AUTH-ACCESS-TOKEN";
    public static final String AUTH_REFRESH_HEADER = "X-AUTH-REFRESH-TOKEN";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;               //30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;     //7일

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public TokenDto createToken(String username, UserRoleType role) {
        Date date = new Date();

        String accessToken = Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                .claim("type", TYPE_ACCESS)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                .claim("type", TYPE_REFRESH)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_EXPIRE_TIME)) // 만료 시간
                .setIssuedAt(date) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpirationTime(ACCESS_TOKEN_EXPIRE_TIME)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    // header 에서 JWT 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTH_ACCESS_HEADER);
        if (StringUtils.isEmpty(bearerToken)) {
            bearerToken = request.getHeader(AUTH_REFRESH_HEADER);
        }
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean isRefreshToken(String tokenValue) {
        String type = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(tokenValue).getBody().get("type").toString();
        return TYPE_REFRESH.equals(type);
    }

    public TokenDto reissuanceToken(String tokenValue, HttpServletRequest req) {
        Claims claims = getUserInfoFromToken(tokenValue);
        RefreshToken refreshToken = refreshTokenRedisRepository.findById(claims.getSubject()).orElse(null);
        if (refreshToken != null) {
            String ipAddress = IpUtil.getClientIp(req);
            if (refreshToken.getIp().equals(ipAddress)) {
                TokenDto token = createToken(refreshToken.getId(), refreshToken.getRole());

                addRefreshTokenInRedis(ipAddress, refreshToken.getId(), refreshToken.getRole(), token);
                return token;
            }
        }
        return null;
    }

    public void addRefreshTokenInRedis(String ip, String username, UserRoleType role,  TokenDto tokenDto) {
        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(username)
                .ip(ip)
                .role(role)
                .refreshToken(tokenDto.getRefreshToken())
                .build());
    }
}