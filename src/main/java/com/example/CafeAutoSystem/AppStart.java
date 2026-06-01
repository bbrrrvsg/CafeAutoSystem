package com.example.CafeAutoSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // BaseTime 의 @CreatedDate / @LastModifiedDate 자동 주입 활성화
@SpringBootApplication
@EnableJpaAuditing
public class AppStart {
    public static void main(String[] args) {
        SpringApplication.run(AppStart.class);
    }
}
