package com.sparta.springpersonaltaskv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringPersonalTaskV2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringPersonalTaskV2Application.class, args);
    }

}
