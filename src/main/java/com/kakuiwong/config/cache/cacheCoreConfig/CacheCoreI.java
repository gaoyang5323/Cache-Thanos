package com.kakuiwong.config.cache.cacheCoreConfig;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author: gaoyang
 * @Description:
 */
public interface CacheCoreI {

    public Object set(String cachename, String key, Long timeout, ProceedingJoinPoint point, boolean sync) throws Throwable;

    public Object get(String cachename, String key, Long timeout, ProceedingJoinPoint point, boolean sync) throws Throwable;

    public Object delete(String cachename, String key, ProceedingJoinPoint point, boolean sync) throws Throwable;
}
