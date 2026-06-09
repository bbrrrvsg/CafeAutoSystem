package com.example.CafeAutoSystem.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUtil {

    // 자동 설정된 S3Client (AWS SDK v2) 주입
    private final S3Client s3Client;

    // application.properties에서 버킷 이름 주입 (새로운 키 사용)
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /** 파일을 S3에 업로드합니다 (AWS SDK v2 사용). */
    public String fileUpload(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // (*) 동일한 파일명으로 업로드할경우 식별이 불가능하다. 해결방안 : UUID, 식별자 생성
        String uuid = UUID.randomUUID().toString();
        // (*) uuid 와(+) 파일명 (파일명에 _언더바가 존재하면 -하이픈 으로 모두 변경 ) , _언더바는 uuid와파일명 구분하는 용도
        String objectKey = uuid + "_" + multipartFile.getOriginalFilename().replaceAll("_", "-");

        try (InputStream inputStream = multipartFile.getInputStream()) {
            // S3에 업로드할 객체 요청 생성 (SDK v2)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(multipartFile.getContentType())
                    .build();
            // 파일 업로드 실행 (SDK v2)
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
            // 업로드된 객체의 URL 가져오기 (SDK v2)
            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();
            String fileUrl = s3Client.utilities().getUrl(getUrlRequest).toString();
            return fileUrl;
        } catch (Exception e) {
            System.err.println("Unexpected error during upload: " + e.getMessage());
            return null;
        }
    }

    /** S3에서 객체(파일)를 삭제합니다 (AWS SDK v2 사용). */
    public boolean fileDelete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return false;
        }
        try {
            objectKey = objectKey.split("/")[objectKey.split("/").length - 1];
            // 삭제 요청 객체 생성 (SDK v2)
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();
            // 객체 삭제 실행 (SDK v2)
            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (Exception e) {
            System.err.println("Unexpected error during delete: " + e.getMessage());
            return false;
        }
    }
}
