package com.kakuiwong.config.cache.cacheConfig;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author: gaoyang
 * @Description:
 */
public class RedisCacheImpl implements ThanosCacheI {
    private RedisTemplate<Object, Object> redisTemplate;

    public RedisCacheImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value, Long timeout) {
        redisTemplate.boundValueOps(key).set(value, timeout);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    @Override
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.boundValueOps(key).getExpire();
    }
}
