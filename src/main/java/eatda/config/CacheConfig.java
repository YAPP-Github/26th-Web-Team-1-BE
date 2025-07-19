package eatda.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import eatda.repository.CacheSetting;
import java.util.Arrays;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        List<CaffeineCache> caches = Arrays.stream(CacheSetting.values())
                .map(this::createCaffeineCache)
                .toList();

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        cacheManager.afterPropertiesSet();
        return cacheManager;
    }

    private CaffeineCache createCaffeineCache(CacheSetting cacheSetting) {
        return new CaffeineCache(
                cacheSetting.getName(),
                Caffeine.newBuilder()
                        .expireAfterWrite(cacheSetting.getTimeToLive())
                        .maximumSize(cacheSetting.getMaximumSize())
                        .build());
    }
}
