package com.example.CafeAutoSystem.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // uploads/qrcodes 폴더에 저장된 QR 이미지를 /qrcodes/** URL로 접근 가능하게 한다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/qrcodes/**")
                .addResourceLocations("file:uploads/qrcodes/");
        // 업로드된 이미지(메뉴 사진 등)를 /uploads/** URL로 접근 가능하게 한다.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
