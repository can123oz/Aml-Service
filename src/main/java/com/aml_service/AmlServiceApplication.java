package com.aml_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AmlServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AmlServiceApplication.class, args);
    }
}