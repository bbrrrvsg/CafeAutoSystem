package com.example.CafeAutoSystem.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

/**
 * 이미지 업로드 (메뉴 사진 등).
 * uploads/images/ 폴더에 저장하고, /uploads/images/{파일} URL을 반환한다.
 * (WebConfig 의 /uploads/** 리소스 핸들러로 서빙)
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 비어있습니다."));
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미지 파일만 업로드할 수 있습니다."));
        }
        try {
            String orig = file.getOriginalFilename();
            String ext = (orig != null && orig.contains(".")) ? orig.substring(orig.lastIndexOf('.')) : "";
            String name = UUID.randomUUID().toString().replace("-", "") + ext.toLowerCase();

            Path dir = Paths.get("uploads", "images");
            Files.createDirectories(dir);
            Path dest = dir.resolve(name).toAbsolutePath();
            file.transferTo(dest);

            String url = "/uploads/images/" + name;
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            log.error("이미지 업로드 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
