package com.larksuite.lark.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类放在 {@code com.larksuite.lark.backend}，默认仅扫描本包及子包，
 * 避免与 {@code lark-spring-boot-starter} 内 {@code com.larksuite.lark.web} 重复注册。
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        loadDotenvToSystemProperties();
        SpringApplication.run(App.class, args);
    }

    private static void loadDotenvToSystemProperties() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        dotenv.entries().forEach(e -> {
            if (System.getProperty(e.getKey()) == null) {
                System.setProperty(e.getKey(), e.getValue());
            }
        });
    }
}
