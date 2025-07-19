package eatda.repository.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.net.URL;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

class S3ImageRepositoryTest {

    private static final String TEST_BUCKET = "test-bucket";

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private S3ImageRepository s3ImageRepository;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3Presigner = mock(S3Presigner.class);
        s3ImageRepository = new S3ImageRepository(s3Client, TEST_BUCKET, s3Presigner);
    }

    @Nested
    class FileUpload {

        @ParameterizedTest
        @EnumSource(ImageDomain.class)
        void 허용된_이미지_타입이면_정상적으로_업로드되고_생성된_Key를_반환한다(ImageDomain imageDomain) {
            String originalFilename = "test-image.jpg";
            String contentType = "image/jpeg";

            MockMultipartFile file = new MockMultipartFile(
                    "image", originalFilename, contentType, "image-content".getBytes()
            );

            String key = s3ImageRepository.upload(file, imageDomain);

            ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
            verify(s3Client).putObject(putObjectRequestCaptor.capture(), any(RequestBody.class));
            PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();

            String expectedPattern = imageDomain.getName() + "/[a-f0-9\\-]{36}\\.jpg";

            assertAll(
                    () -> assertThat(key).matches(expectedPattern),
                    () -> assertThat(capturedRequest.key()).isEqualTo(key),
                    () -> assertThat(capturedRequest.bucket()).isEqualTo(TEST_BUCKET),
                    () -> assertThat(capturedRequest.contentType()).isEqualTo(contentType)
            );
        }

        @Test
        void 허용되지_않은_파일_타입이면_BusinessException을_던진다() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.txt", "text/plain", "file-content".getBytes()
            );

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> s3ImageRepository.upload(file, ImageDomain.STORY));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_IMAGE_TYPE);
        }
    }

    @Nested
    class GeneratePresignedUrl {

        @Test
        void 유효한_key로_요청_시_Presigned_URL을_성공적으로_반환한다() throws Exception {
            String key = "stores/image.jpg";
            String expectedUrlString = "https://example.com/presigned-url-for-image.jpg";
            URL expectedUrl = new URL(expectedUrlString);

            PresignedGetObjectRequest presignedRequestResult = mock(PresignedGetObjectRequest.class);

            when(presignedRequestResult.url()).thenReturn(expectedUrl);
            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenReturn(presignedRequestResult);

            String presignedUrl = s3ImageRepository.getPresignedUrl(key);

            ArgumentCaptor<GetObjectPresignRequest> presignRequestCaptor =
                    ArgumentCaptor.forClass(GetObjectPresignRequest.class);
            verify(s3Presigner).presignGetObject(presignRequestCaptor.capture());
            GetObjectPresignRequest capturedPresignRequest = presignRequestCaptor.getValue();

            assertAll(
                    () -> assertThat(presignedUrl).isEqualTo(expectedUrlString),
                    () -> assertThat(capturedPresignRequest.getObjectRequest().key()).isEqualTo(key),
                    () -> assertThat(capturedPresignRequest.getObjectRequest().bucket()).isEqualTo(TEST_BUCKET),
                    () -> assertThat(capturedPresignRequest.signatureDuration()).isEqualTo(Duration.ofMinutes(30))
            );
        }

        @Test
        void Presigner가_예외를_던지면_BusinessException으로_전환하여_던진다() {
            String key = "stores/image.jpg";

            when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .thenThrow(SdkClientException.create("AWS SDK 통신 실패"));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> s3ImageRepository.getPresignedUrl(key));

            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }
}
