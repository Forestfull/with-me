    package com.forestfull.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .build();
    }

    @Slf4j
    @Component
    public static class JwtUtil {

        private final Key key;
        private final long accessTokenExpTime;

        public JwtUtil(
                @Value("${jwt.secret}") String secretKey,
                @Value("${jwt.interval.seconds}") long accessTokenExpTime
        ) {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            this.accessTokenExpTime = accessTokenExpTime;
        }

        /**
         * Access Token 생성
         *
         * @param member
         * @return Access Token String
         */
        public String createAccessToken(CustomUserInfoDto member) {
            return createToken(member, accessTokenExpTime);
        }


        /**
         * JWT 생성
         *
         * @param member
         * @param expireTime
         * @return JWT String
         */
        private String createToken(CustomUserInfoDto member, long expireTime) {
            Claims claims = Jwts.claims();
            claims.put("memberId", member.getMemberId());
            claims.put("email", member.getEmail());
            claims.put("role", member.getRole());

            LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
            LocalDateTime tokenValidity = now.plusSeconds(expireTime);


            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(Date.from(now.toInstant()))
                    .setExpiration(Date.from(tokenValidity.toInstant()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }


        /**
         * Token에서 User ID 추출
         *
         * @param token
         * @return User ID
         */
        public Long getUserId(String token) {
            return parseClaims(token).get("memberId", Long.class);
        }


        /**
         * JWT 검증
         *
         * @param token
         * @return IsValidate
         */
        public boolean validateToken(String token) {
            try {
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
                return true;
            } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
                log.info("Invalid JWT Token", e);
            } catch (ExpiredJwtException e) {
                log.info("Expired JWT Token", e);
            } catch (UnsupportedJwtException e) {
                log.info("Unsupported JWT Token", e);
            } catch (IllegalArgumentException e) {
                log.info("JWT claims string is empty.", e);
            }
            return false;
        }


        /**
         * JWT Claims 추출
         *
         * @param accessToken
         * @return JWT Claims
         */
        public Claims parseClaims(String accessToken) {
            try {
                return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
            } catch (ExpiredJwtException e) {
                return e.getClaims();
            }
        }
    }
}
