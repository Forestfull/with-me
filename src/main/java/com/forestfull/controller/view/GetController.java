package com.forestfull.controller.view;

import com.forestfull.service.SecureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;

@Controller
@RequiredArgsConstructor
public class GetController {

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/login/kakao")
    String loginViaKakao(HttpServletRequest request) {

        final Iterator<String> iterator = request.getAttributeNames().asIterator();
        while (iterator.hasNext()) {
            System.out.println(
                    request.getAttribute(iterator.next())
            );
        }

        return "hi";
    }

}