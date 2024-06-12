package com.forestfull.service;

import com.forestfull.mapper.SecureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecureService {

    private final SecureMapper secureMapper;

    public String test(){
        final String test = secureMapper.test();
        System.out.println(test);
        return test;
    }

}
