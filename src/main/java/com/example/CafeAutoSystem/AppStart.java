package com.example.CafeAutoSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.*;

@SpringBootApplication
@EnableJpaAuditing      // BaseTime 자동 주입
@EnableScheduling       // @Scheduled 스케줄러 활성화 (장민서 AI 스케줄러용)
public class AppStart {
    public static void main(String[] args) {
        SpringApplication.run(AppStart.class);
    }

    // JAR 내부에 숨은 JSP 자원들을 리눅스 임시 디렉토리로 강제 해제 후 톰캣 웹 루트로 바인딩합니다.
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer() {
        return factory -> {
            try {
                // 1. 리눅스 서버 가상 메모리 내 임시 작업 경로 확보
                String tmpDir = System.getProperty("java.io.tmpdir");
                File targetDocRoot = new File(tmpDir, "tomcat-jsp-root");

                // 2. JAR 내부에 숨겨진 WEB-INF 하위의 모든 JSP 자원 검색
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources("classpath:/META-INF/resources/WEB-INF/**/*.*");

                for (Resource resource : resources) {
                    String uriString = resource.getURI().toString();
                    // 클래스패스 내부 상대 경로 추출
                    String relativePath = uriString.substring(uriString.indexOf("WEB-INF/"));
                    File destFile = new File(targetDocRoot, relativePath);

                    // 디렉토리 구성 및 파일 복사 실행 (Unpack)
                    destFile.getParentFile().mkdirs();
                    try (InputStream is = resource.getInputStream();
                         OutputStream os = new FileOutputStream(destFile)) {
                        is.transferTo(os);
                    }
                }

                // 3. 압축이 해제된 가상 폴더를 내장 톰캣의 실제 웹 루트(Document Root)로 매핑
                if (targetDocRoot.exists()) {
                    factory.setDocumentRoot(targetDocRoot);
                    System.out.println("====== [Success] JAR JSP Resource Extracted & Mounted to: " + targetDocRoot.getAbsolutePath() + " ======");
                }
            } catch (Exception e) {
                System.out.println("====== [Warning] Failed to extract physical JSP resource: " + e.getMessage() + " ======");
            }
        };
    }
}
