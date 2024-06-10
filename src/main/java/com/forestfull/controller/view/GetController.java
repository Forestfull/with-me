package com.forestfull.controller.view;

import com.forestfull.service.SecureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class GetController {

    private final SecureService secureService;

    @GetMapping("/")
    @ResponseBody
    String test(){
        return secureService.test();
    }

}