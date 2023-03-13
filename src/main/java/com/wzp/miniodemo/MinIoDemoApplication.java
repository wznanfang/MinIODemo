package com.wzp.miniodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MinIoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinIoDemoApplication.class, args);
    }

}
