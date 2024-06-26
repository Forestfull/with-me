package com.forestfull.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Configuration
public class JasyptConfig {

    private static class EncryptionSetting {
        public static void main(String[] args) {
enc.accept("project_vigfoot", "b1dd6ed7ca9d4f3a7547c96593fb1217");
        }

        private static StringEncryptor initEncryptor(String key) {
            final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            encryptor.setPassword(key);
            encryptor.setKeyObtentionIterations(1000);
            encryptor.setPoolSize(1);
            return encryptor;
        }

        private static final BiConsumer<String, String> enc = (key, str)
                -> System.out.println(str + " : ENC(" + initEncryptor(key).encrypt(str) + ")");
        private static final BiConsumer<String, String> dec = (key, str)
                -> System.out.println(str + " : " + initEncryptor(key).decrypt(str));
    }

    @Value("${spring.profiles.active}")
    private String key;

    @Bean("jasyptStringEncryptor")
    StringEncryptor stringEncryptor() {
        return EncryptionSetting.initEncryptor(key);
    }
}
