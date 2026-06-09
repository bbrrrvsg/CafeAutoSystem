package com.example.CafeAutoSystem.global.config;

import com.example.CafeAutoSystem.global.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
 * app.use-s3=true  → S3 업로드 후 퍼블릭 URL 반환
 * app.use-s3=false → 로컬 uploads/images/ 저장 후 /uploads/images/{파일} URL 반환
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class FileUploadController {

    private final AwsS3Service awsS3Service;

    @Value("${app.use-s3:false}")
    private boolean useS3;

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 비어있습니다."));
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미지 파일만 업로드할 수 있습니다."));
        }

        if (useS3) {
            // S3 업로드 (배포 환경)
            String url = awsS3Service.uploadMenuImage(file);
            if (url == null) {
                return ResponseEntity.internalServerError().body(Map.of("error", "S3 업로드에 실패했습니다."));
            }
            return ResponseEntity.ok(Map.of("url", url));
        } else {
            // 로컬 저장 (개발 환경)
            try {
                String orig = file.getOriginalFilename();
                String ext = (orig != null && orig.contains(".")) ? orig.substring(orig.lastIndexOf('.')).toLowerCase() : "";
                String name = UUID.randomUUID().toString().replace("-", "") + ext;

                Path dir = Paths.get("uploads", "images");
                Files.createDirectories(dir);
                file.transferTo(dir.resolve(name).toAbsolutePath());

                return ResponseEntity.ok(Map.of("url", "/uploads/images/" + name));
            } catch (Exception e) {
                log.error("로컬 이미지 업로드 실패: {}", e.getMessage());
                return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
            }
        }
    }
}
