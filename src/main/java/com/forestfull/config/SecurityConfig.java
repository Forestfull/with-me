package com.forestfull.config;

import com.forestfull.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * https://sjh9708.tistory.com/170
 * https://velog.io/@wnso-kim/Spring-Boot-%EC%B9%B4%EC%B9%B4%EC%98%A4-OAuth2.0-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
 * 나랑 같은 환경이 있어서 차용함
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(con -> con.anyRequest().permitAll())
                .oauth2Login(con -> con.loginPage("/login")
                        .successHandler(((request, response, authentication) -> {
                            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

                            String id = defaultOAuth2User.getAttributes().get("id").toString();
                            String body = """
                                    {"id":"%s"}
                                    """.formatted(id);

                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                            PrintWriter writer = response.getWriter();
                            writer.println(body);
                            writer.flush();
                        }))
                        .userInfoEndpoint(uCon -> uCon.userService(oAuth2UserService))
                )
                .build();
    }

    @Slf4j
    @Component
    public static class JwtUtil {

        private final Key key;
        private final long accessTokenExpTime;

        public JwtUtil(
                @Value("${jwt.secret}") String secretKey,
                @Value("${jwt.interval.days}") long accessTokenExpTime
        ) {
            byte[] keyBytes = (secretKey + System.currentTimeMillis() + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            this.accessTokenExpTime = 60 * 60 * 24 * accessTokenExpTime;
        }

        /**
         * Access Token 생성
         *
         * @param userEntity
         * @return Access Token String
         */
        public String createAccessToken(UserEntity userEntity) {
            return createToken(userEntity, accessTokenExpTime);
        }

        /**
         * JWT 생성
         *
         * @param userEntity
         * @param expireTime
         * @return JWT String
         */
        private String createToken(UserEntity userEntity, long expireTime) {
            Claims claims = Jwts.claims();
            claims.put("id", userEntity.getId());
            claims.put("email", userEntity.getEmail());
            claims.put("role", userEntity.getRole());

            LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
            LocalDateTime tokenValidity = now.plusSeconds(expireTime);


            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
                    .setExpiration(Date.from(tokenValidity.toInstant(ZoneOffset.UTC)))
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
            return parseClaims(token).get("id", Long.class);
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
