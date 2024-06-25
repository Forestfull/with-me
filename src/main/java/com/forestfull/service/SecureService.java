package com.forestfull.service;

import com.forestfull.config.SecurityConfig;
import com.forestfull.entity.UserEntity;
import com.forestfull.mapper.SecureMapper;
import com.forestfull.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecureService extends DefaultOAuth2UserService {

    private final SecureMapper secureMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public Optional<String> getToken(UserEntity entity) {
        if (!StringUtils.hasText(entity.getEmail())) return Optional.empty();
        if (!StringUtils.hasText(entity.getPassword())) return Optional.empty();
        UserEntity userEntity = secureMapper.getUserEntity(entity.getEmail());

        return Optional.empty();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);

        // Role generate
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN");

        // nameAttributeKey
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // DB 저장로직이 필요하면 추가

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
    }
}
