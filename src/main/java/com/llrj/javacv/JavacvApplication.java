package com.llrj.javacv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavacvApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavacvApplication.class, args);
    }

}


