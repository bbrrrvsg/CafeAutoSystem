package com.example.CafeAutoSystem.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS SDK v2 설정
 * - EB 배포: IAM 인스턴스 프로파일(역할)로 자동 인증
 * - 로컬 개발: ~/.aws/credentials 또는 환경변수(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
 */
@Configuration
public class AwsConfig {

    @Value("${spring.cloud.aws.region.static:ap-northeast-2}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}
