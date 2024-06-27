package com.forestfull.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GetController {

    @GetMapping("/login")
    String login() {
        return "login";
    }
}