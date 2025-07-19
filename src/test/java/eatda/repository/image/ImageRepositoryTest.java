package eatda.repository.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import eatda.repository.BaseCacheRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.mock.web.MockMultipartFile;

class ImageRepositoryTest extends BaseCacheRepositoryTest {

    private S3ImageRepository s3ImageRepository;
    private CachePreSignedUrlRepository cachePreSignedUrlRepository;
    private ImageRepository imageRepository;

    @BeforeEach
    void setUp() {
        s3ImageRepository = mock(S3ImageRepository.class);
        cachePreSignedUrlRepository = new CachePreSignedUrlRepository(getCacheManager());
        imageRepository = new ImageRepository(s3ImageRepository, cachePreSignedUrlRepository);
    }

    @Nested
    class Upload {

        @Test
        void 이미지가_S3에_업로드된다() {
            MockMultipartFile file = new MockMultipartFile(
                    "image", "test-image.jpg", "image/jpeg", "image-content".getBytes()
            );
            doReturn("test-image-key").when(s3ImageRepository).upload(file, ImageDomain.MEMBER);

            String imageKey = imageRepository.upload(file, ImageDomain.MEMBER);

            assertThat(imageKey).isEqualTo("test-image-key");
        }

        @Test
        void 이미지_업로드_시_PreSignedUrl이_캐시에_저장된다() {
            MockMultipartFile file = new MockMultipartFile(
                    "image", "test-image.jpg", "image/jpeg", "image-content".getBytes()
            );
            doReturn("test-image-key").when(s3ImageRepository).upload(file, ImageDomain.MEMBER);
            doReturn("https://example.url.com").when(s3ImageRepository).getPresignedUrl("test-image-key");

            imageRepository.upload(file, ImageDomain.MEMBER);

            assertThat(cachePreSignedUrlRepository.get("test-image-key")).contains("https://example.url.com");
        }
    }

    @Nested
    class GetPresignedUrl {

        @ParameterizedTest
        @NullAndEmptySource
        void 이미지_키가_null이면__null을_반환한다(String imageKey) {
            String actual = imageRepository.getPresignedUrl(imageKey);

            assertThat(actual).isNull();
        }

        @Test
        void 이미지_키가_캐시에_존재하면_s3에_요청하지_않고_PreSignedUrl을_반환한다() {
            String imageKey = "test-image-key";
            cachePreSignedUrlRepository.put(imageKey, "https://example.url.com");

            String preSignedUrl = imageRepository.getPresignedUrl(imageKey);

            assertAll(
                    () -> assertThat(preSignedUrl).isEqualTo("https://example.url.com"),
                    () -> verify(s3ImageRepository, never()).getPresignedUrl(anyString())
            );
        }

        @Test
        void 이미지_키가_캐시에_존재하지_않으면_S3에서_PreSignedUrl을_조회하고_캐시에_저장한다() {
            String imageKey = "test-image-key";
            doReturn("https://example.url.com").when(s3ImageRepository).getPresignedUrl(imageKey);

            String preSignedUrl = imageRepository.getPresignedUrl(imageKey);

            assertAll(
                    () -> assertThat(preSignedUrl).isEqualTo("https://example.url.com"),
                    () -> assertThat(cachePreSignedUrlRepository.get(imageKey)).contains("https://example.url.com")
            );
        }
    }
}
