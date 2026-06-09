package com.example.CafeAutoSystem.global.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.UUID;

/**
 * AWS S3 전용 서비스
 * - 메뉴 이미지 업로드 / 삭제
 * - 추후 다른 S3 연동 기능도 여기에 추가
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // =====================================================================
    // 메뉴 이미지 업로드
    // =====================================================================

    /**
     * MultipartFile을 S3 menu-images/ 경로에 업로드하고 퍼블릭 URL을 반환한다.
     */
    public String uploadMenuImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String orig = file.getOriginalFilename();
        String ext = (orig != null && orig.contains("."))
                ? orig.substring(orig.lastIndexOf('.')).toLowerCase()
                : "";
        String objectKey = "menu-images/" + UUID.randomUUID().toString().replace("-", "") + ext;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 퍼블릭 URL 반환
            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();
            return s3Client.utilities().getUrl(getUrlRequest).toString();

        } catch (Exception e) {
            log.error("메뉴 이미지 S3 업로드 실패: {}", e.getMessage());
            return null;
        }
    }

    // =====================================================================
    // S3 객체 삭제
    // =====================================================================

    /**
     * S3 URL 또는 objectKey를 받아 해당 객체를 삭제한다.
     * URL 형태면 버킷 이름 이후 경로를 objectKey로 파싱한다.
     */
    public boolean deleteObject(String urlOrKey) {
        if (urlOrKey == null || urlOrKey.isBlank()) {
            return false;
        }

        // URL에서 objectKey 추출 (예: https://bucket.s3.../menu-images/xxx.jpg → menu-images/xxx.jpg)
        String objectKey = urlOrKey.contains(".amazonaws.com/")
                ? urlOrKey.substring(urlOrKey.indexOf(".amazonaws.com/") + ".amazonaws.com/".length())
                : urlOrKey;

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("S3 객체 삭제 실패: {}", e.getMessage());
            return false;
        }
    }
}
