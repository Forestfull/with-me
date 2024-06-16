package com.forestfull.controller;

import com.forestfull.entity.UserEntity;
import com.forestfull.service.SecureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("/auth")
@RequiredArgsConstructor
public class SignController {

    private final SecureService secureService;

    @PostMapping("/sign-in")
    ResponseEntity<String> getUserEntity(@RequestBody UserEntity userEntity) {
        Optional<String> optionalToken = secureService.getToken(userEntity);
        return optionalToken.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }


}