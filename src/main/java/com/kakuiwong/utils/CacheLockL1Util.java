package com.kakuiwong.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: gaoyang
 * @Description:
 */
public class CacheLockL1Util {
    private static ReadWriteLock readWriteLock;
    private static Lock readLock;
    private static Lock writeLock;

    static {
        readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
    }

    public static void readLock(boolean sync) {
        if (sync)
            readLock.lock();
    }

    public static void readUnlock(boolean sync) {
        if (sync)
            readLock.unlock();
    }

    public static void writeLock(boolean sync) {
        if (sync)
            writeLock.lock();
    }

    public static void writeUnlock(boolean sync) {
        if (sync)
            writeLock.unlock();
    }
}
