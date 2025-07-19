package eatda.repository.image;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import eatda.repository.CacheSetting;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
public class S3ImageRepository {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpg", "image/jpeg", "image/png");
    private static final String DEFAULT_CONTENT_TYPE = "bin";
    private static final String PATH_DELIMITER = "/";
    private static final String EXTENSION_DELIMITER = ".";
    private static final Duration PRESIGNED_URL_DURATION = CacheSetting.IMAGE.getTimeToLive()
            .plus(Duration.ofMinutes(5));

    private final S3Client s3Client;
    private final String bucket;
    private final S3Presigner s3Presigner;

    public S3ImageRepository(
            S3Client s3Client,
            @Value("${spring.cloud.aws.s3.bucket}") String bucket,
            S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.s3Presigner = s3Presigner;
    }

    public String upload(MultipartFile file, ImageDomain domain) {
        validateContentType(file);
        String extension = getExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String key = domain.getName() + PATH_DELIMITER + uuid + EXTENSION_DELIMITER + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key;
        } catch (IOException exception) {
            throw new BusinessException(BusinessErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException(BusinessErrorCode.INVALID_IMAGE_TYPE);
        }
    }

    private String getExtension(String filename) {
        if (filename == null
                || filename.lastIndexOf(EXTENSION_DELIMITER) == -1
                || filename.startsWith(EXTENSION_DELIMITER)) {
            return DEFAULT_CONTENT_TYPE;
        }
        return filename.substring(filename.lastIndexOf(EXTENSION_DELIMITER) + 1);
    }


    public String getPresignedUrl(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(PRESIGNED_URL_DURATION)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception exception) {
            throw new BusinessException(BusinessErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }
}
