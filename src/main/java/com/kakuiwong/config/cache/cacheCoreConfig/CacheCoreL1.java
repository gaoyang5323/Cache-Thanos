package com.kakuiwong.config.cache.cacheCoreConfig;

import com.kakuiwong.bean.L1CacheWrap;
import com.kakuiwong.config.cache.cacheConfig.ThanosCacheI;
import com.kakuiwong.config.properties.CachePropertiesBean;
import com.kakuiwong.utils.CacheLockL1Util;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author: gaoyang
 * @Description:
 */
@Component
public class CacheCoreL1 implements CacheCoreI {

    @Qualifier("L1Cache")
    @Autowired
    private ThanosCacheI l1Cache;
    @Autowired
    private CachePropertiesBean cachePropertiesBean;

    @Override
    public Object set(String cachename, String key, Long timeout, ProceedingJoinPoint point, boolean sync) throws Throwable {
        try {
            CacheLockL1Util.writeLock(sync);
            if (cachePropertiesBean.getDoubleDelete()) {
                this.cachedelete(key, point);
            }
            return this.cacheSet(key, timeout, point);
        } finally {
            CacheLockL1Util.writeUnlock(sync);
        }
    }

    @Override
    public Object get(String cachename, String key, Long timeout, ProceedingJoinPoint point, boolean sync) throws Throwable {
        boolean lock = false;
        try {
            CacheLockL1Util.readLock(sync);
            Object l1Result = l1Cache.get(key);
            if (l1Result == null) {
                lock = true;
                CacheLockL1Util.writeLock(sync);
                return this.cacheSet(key, timeout, point);
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
                    CacheLockL1Util.writeLock(sync);
                    return this.cacheSet(key, timeout, point);
                }
            }
        } finally {
            CacheLockL1Util.readUnlock(sync);
            if (lock)
                CacheLockL1Util.writeUnlock(sync);
        }
        return null;
    }

    @Override
    public Object delete(String cachename, String key, ProceedingJoinPoint point, boolean sync) throws Throwable {
        Object result = null;
        try {
            CacheLockL1Util.writeLock(sync);
            if (cachePropertiesBean.getDoubleDelete()) {
                this.cachedelete(key, point);
            }
            result = point.proceed();
            this.cachedelete(key, point);
        } finally {
            CacheLockL1Util.writeUnlock(sync);
        }
        return result;
    }

    private boolean cachedelete(String key, ProceedingJoinPoint point) {
        l1Cache.delete(key);
        return true;
    }

    public Object cacheSet(String key, Long timeout, ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        if (result != null) {
            l1Cache.set(key, L1CacheWrap.create(result, timeout), null);
        }
        return result;
    }
}
