package com.forestfull.service;

import com.forestfull.mapper.SecureMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecureService {

    private final SecureMapper secureMapper;

    @Lazy
    @PostConstruct
    public String test(){
        final String test = secureMapper.test();
        System.out.println(test);
        return test;
    }

}
