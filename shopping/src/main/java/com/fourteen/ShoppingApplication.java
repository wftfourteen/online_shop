package com.fourteen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
@MapperScan("com.fourteen.mapper")
@SpringBootApplication
@ServletComponentScan  // 启用@WebFilter、@WebServlet等注解的扫描
public class ShoppingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingApplication.class, args);
    }

}
