package com.larksuite.lark.backend.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 根路径重定向到 Swagger UI。
 * <p>
 * springdoc 默认 Swagger UI 路径为 {@code /swagger-ui/index.html}。
 */
@Controller
public class SwaggerRedirectController {

    /**
     * 浏览器访问 {@code /} 时跳转 Swagger UI。
     * <p>
     * @return 重定向目标视图名
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/swagger-ui/index.html";
    }
}
