package com.example.CafeAutoSystem.review.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class QrCodeService {

    private static final int QR_SIZE = 300;
    private static final String SAVE_DIR = "uploads/qrcodes";

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket:}")
    private String bucket;

    /** app.use-s3=true 이면 S3, false(기본값)이면 로컬 저장 */
    @Value("${app.use-s3:false}")
    private boolean useS3;

    // reviewPageUrl을 QR 이미지로 생성하고, 저장 경로(URL 또는 로컬 경로)를 반환한다.
    public String createQrImage(Long orderId, String reviewPageUrl) {
        try {
            String fileName = "order-" + orderId + ".png";

            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(reviewPageUrl, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

            if (useS3) {
                // S3 업로드
                String s3Key = "qrcodes/" + fileName;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                byte[] imageBytes = baos.toByteArray();

                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(s3Key)
                        .contentType("image/png")
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build();
                s3Client.putObject(putRequest, RequestBody.fromBytes(imageBytes));

                GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                        .bucket(bucket)
                        .key(s3Key)
                        .build();
                return s3Client.utilities().getUrl(getUrlRequest).toString();

            } else {
                // 로컬 저장 (개발용)
                Files.createDirectories(Path.of(SAVE_DIR));
                Path filePath = Path.of(SAVE_DIR, fileName);
                MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
                return "/qrcodes/" + fileName;
            }

        } catch (Exception e) {
            throw new RuntimeException("QR 이미지 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
