package eatda.repository;

import eatda.config.CacheConfig;
import org.springframework.cache.CacheManager;

public abstract class BaseCacheRepositoryTest {

    private final CacheManager cacheManager = new CacheConfig().cacheManager();

    protected CacheManager getCacheManager() {
        return cacheManager;
    }
}
