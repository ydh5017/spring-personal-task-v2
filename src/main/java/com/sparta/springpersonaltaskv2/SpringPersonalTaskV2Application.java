package com.sparta.springpersonaltaskv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // security 기능 꺼두기
public class SpringPersonalTaskV2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringPersonalTaskV2Application.class, args);
    }

}
