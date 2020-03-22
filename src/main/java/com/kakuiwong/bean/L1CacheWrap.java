package com.kakuiwong.bean;

/**
 * @author: gaoyang
 * @Description:
 */
public class L1CacheWrap {

    private Object value;

    private Long timeout;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public static L1CacheWrap create(Object value, Long timeout) {
        L1CacheWrap l1CacheValue = new L1CacheWrap();
        l1CacheValue.setTimeout(timeout <= 0L ? 0L : System.currentTimeMillis() + timeout);
        l1CacheValue.setValue(value);
        return l1CacheValue;
    }

    public static boolean isTimeout(Long timeout) {
        if (timeout <= 0) {
            return true;
        }
        return timeout > System.currentTimeMillis();
    }
}
