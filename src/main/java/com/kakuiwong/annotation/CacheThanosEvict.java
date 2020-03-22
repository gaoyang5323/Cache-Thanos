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
public @interface CacheThanosEvict {

    String cachename() default "";

    String key() default "";

    boolean sync() default false;
}
