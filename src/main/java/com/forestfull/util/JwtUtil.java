package com.forestfull.util;

import com.forestfull.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

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
