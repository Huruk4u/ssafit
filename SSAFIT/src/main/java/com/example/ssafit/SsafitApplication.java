package com.example.ssafit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.example.ssafit.model.dao")
@SpringBootApplication
public class SsafitApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsafitApplication.class, args);
    }

}
