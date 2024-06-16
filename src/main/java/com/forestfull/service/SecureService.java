package com.forestfull.service;

import com.forestfull.config.SecurityConfig;
import com.forestfull.entity.UserEntity;
import com.forestfull.mapper.SecureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecureService {

    private final SecureMapper secureMapper;
    private final SecurityConfig.JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public Optional<String> getToken(UserEntity entity) {
        if (!StringUtils.hasText(entity.getEmail())) return Optional.empty();
        if (!StringUtils.hasText(entity.getPassword())) return Optional.empty();
        UserEntity userEntity = secureMapper.getUserEntity(entity.getEmail());

        return Optional.empty();
    }
}
