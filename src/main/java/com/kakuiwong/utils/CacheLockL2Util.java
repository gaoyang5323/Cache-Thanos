package com.kakuiwong.utils;

import com.kakuiwong.config.properties.RedissonProperties;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author: gaoyang
 * @Description:
 */
@Component
public class CacheLockL2Util {

    @Autowired
    private RedissonClient client;
    @Autowired
    private RedissonProperties redissonProperties;

    private static RedissonClient redissonClient;

    private static long TIMEOUT;

    @PostConstruct
    public void init() {
        CacheLockL2Util.redissonClient = client;
        CacheLockL2Util.TIMEOUT = redissonProperties.getTimeout();
    }

    public static void lock(String lockKey, boolean sync) {
        if (sync) {
            RLock lock = redissonClient.getLock(lockKey);
            lock.lock(TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    public static void unlock(String lockKey, boolean sync) {
        if (sync) {
            RLock lock = redissonClient.getLock(lockKey);
            lock.unlock();
        }
    }

    public static void lock(String lockKey, long timeout, boolean sync) {
        if (sync) {
            RLock lock = redissonClient.getLock(lockKey);
            lock.lock(timeout, TimeUnit.MILLISECONDS);
        }
    }

    public static void l2lock(String lockKey, boolean sync, long timeout) {
        if (sync) {
            if (timeout <= 0) {
                lock(lockKey, sync);
            } else {
                lock(lockKey, timeout, sync);
            }
        }
    }
}
