package com.larksuite.lark.backend.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** 浏览器访问根路径时跳转 Swagger UI。 */
@Controller
public class SwaggerRedirectController {

    @GetMapping("/")
    public String index() {
        // springdoc default swagger-ui path is /swagger-ui/index.html
        return "redirect:/swagger-ui/index.html";
    }
}
