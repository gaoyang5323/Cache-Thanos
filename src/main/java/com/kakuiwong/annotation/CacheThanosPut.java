package com.kakuiwong.annotation;

import java.lang.annotation.*;

/**
 * @author: gaoyang
 * @Description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheThanosPut {

    String cachename() default "";

    String key() default "";

    long timeout() default 0L;

    boolean sync() default false;
}
