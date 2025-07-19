package eatda.repository.image;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ImageRepository {

    private final S3ImageRepository s3Repository;
    private final CachePreSignedUrlRepository cachePreSignedUrlRepository;


    public String upload(MultipartFile file, ImageDomain domain) {
        String imageKey = s3Repository.upload(file, domain);

        String preSignedUrl = s3Repository.getPresignedUrl(imageKey);
        cachePreSignedUrlRepository.put(imageKey, preSignedUrl);
        return imageKey;
    }

    @Nullable
    public String getPresignedUrl(@Nullable String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return null;
        }

        Optional<String> cachedUrl = cachePreSignedUrlRepository.get(imageKey);
        if (cachedUrl.isPresent()) {
            return cachedUrl.get();
        }
        String preSignedUrl = s3Repository.getPresignedUrl(imageKey);
        cachePreSignedUrlRepository.put(imageKey, preSignedUrl);
        return preSignedUrl;
    }
}
