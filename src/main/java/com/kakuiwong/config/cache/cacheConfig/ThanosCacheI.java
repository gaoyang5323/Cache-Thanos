package com.kakuiwong.config.cache.cacheConfig;

/**
 * @author: gaoyang
 * @Description:
 */
public interface ThanosCacheI {

    public void set(String key, Object value, Long timeout);

    public Object get(String key);

    public boolean delete(String key);

    public Long getExpire(String key);
}
