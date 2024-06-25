package com.forestfull.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AccessConfig {

    @Bean
    WebClient webClient(){
        return WebClient.builder()
                .codecs(con -> con.defaultCodecs().maxInMemorySize(-1))
                .defaultHeader("server-name", "with-me")
                .build();
    }
}
