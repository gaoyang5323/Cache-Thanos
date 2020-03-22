package com.kakuiwong.config.cache.cacheConfig;

import org.springframework.cache.caffeine.CaffeineCache;

/**
 * @author: gaoyang
 * @Description:
 */
public class CaffCacheImpl implements ThanosCacheI {
    private CaffeineCache caffeineCache;

    public CaffCacheImpl(CaffeineCache caffeineCache) {
        this.caffeineCache = caffeineCache;
    }

    @Override
    public void set(String key, Object value, Long timeout) {
        caffeineCache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return caffeineCache.get(key);
    }

    @Override
    public boolean delete(String key) {
        caffeineCache.evict(key);
        return true;
    }

    @Override
    public Long getExpire(String key) {
        throw new UnsupportedOperationException();
    }
}
