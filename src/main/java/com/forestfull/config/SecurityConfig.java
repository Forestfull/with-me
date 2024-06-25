package com.forestfull.config;

import com.forestfull.service.SecureService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * https://sjh9708.tistory.com/170
 * https://velog.io/@wnso-kim/Spring-Boot-%EC%B9%B4%EC%B9%B4%EC%98%A4-OAuth2.0-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
 * 나랑 같은 환경이 있어서 차용함
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecureService secureService;

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
                        .userInfoEndpoint(uCon -> uCon.userService(secureService))
                )
                .build();
    }
}
