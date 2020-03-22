package com.kakuiwong.annotation;

import com.kakuiwong.config.ioc.ThanosCacheConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: gaoyang
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({ThanosCacheConfig.class})
public @interface EnableThanosCache {
}
