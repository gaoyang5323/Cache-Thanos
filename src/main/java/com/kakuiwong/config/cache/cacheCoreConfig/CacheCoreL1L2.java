package com.kakuiwong.config.cache.cacheCoreConfig;

import com.kakuiwong.bean.L1CacheWrap;
import com.kakuiwong.config.cache.cacheConfig.ThanosCacheI;
import com.kakuiwong.config.properties.CachePropertiesBean;
import com.kakuiwong.utils.CacheLockL2Util;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author: gaoyang
 * @Description:
 */
@Component
public class CacheCoreL1L2 implements CacheCoreI {

    @Qualifier("L1Cache")
    @Autowired
    private ThanosCacheI l1Cache;
    @Qualifier("L2Cache")
    @Autowired
    private ThanosCacheI l2Cache;

    @Autowired
    private CachePropertiesBean cachePropertiesBean;

    @Override
    public Object set(String cachename, String key, Long timeout, ProceedingJoinPoint point, boolean sync) throws Throwable {
        try {
            CacheLockL2Util.l2lock(cachename, sync, timeout);
            if (cachePropertiesBean.getDoubleDelete()) {
                this.cachedelete(key, point);
            }
            return this.cacheSet(key, timeout, point);
        } finally {
            CacheLockL2Util.unlock(cachename, sync);
        }
    }

    @Override
    public Object get(String cachename, String key, Long timeout, ProceedingJoinPoint point, boolean sync) throws Throwable {
        Object l1Result = l1Cache.get(key);
        Object l2Result = null;
        boolean lock = false;
        try {
            if (l1Result == null) {
                l2Result = l2Cache.get(key);
                if (l2Result == null) {
                    CacheLockL2Util.l2lock(cachename, sync, timeout);
                    return this.cacheSet(key, timeout, point);
                }
                if (l2Result != null) {
                    lock = true;
                    l1Cache.set(key, L1CacheWrap.create(l2Result, l2Cache.getExpire(key)), null);
                    return l2Result;
                }
            }

            if (l1Result != null) {
                L1CacheWrap l1CacheWrap = (L1CacheWrap) l1Result;
                Long l1Timeout = l1CacheWrap.getTimeout();
                boolean isTimeout = L1CacheWrap.isTimeout(l1Timeout);
                if (isTimeout) {
                    return l1CacheWrap.getValue();
                }
                if (!isTimeout) {
                    lock = true;
                    CacheLockL2Util.l2lock(cachename, sync, timeout);
                    return this.cacheSet(key, timeout, point);
                }
            }
        } finally {
            if (lock)
                CacheLockL2Util.unlock(cachename, sync);
        }
        return null;
    }

    @Override
    public Object delete(String cachename, String key, ProceedingJoinPoint point, boolean sync) throws Throwable {
        Object result = null;
        try {
            CacheLockL2Util.lock(cachename, sync);
            if (cachePropertiesBean.getDoubleDelete()) {
                this.cachedelete(key, point);
            }
            result = point.proceed();
            this.cachedelete(key, point);
        } finally {
            CacheLockL2Util.unlock(cachename, sync);
        }
        return result;
    }

    private boolean cachedelete(String key, ProceedingJoinPoint point) {
        l1Cache.delete(key);
        l2Cache.delete(key);
        return true;
    }

    public Object cacheSet(String key, Long timeout, ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        if (result != null) {
            l1Cache.set(key, L1CacheWrap.create(result, timeout), null);
            l2Cache.set(key, result, timeout);
        }
        return result;
    }
}
