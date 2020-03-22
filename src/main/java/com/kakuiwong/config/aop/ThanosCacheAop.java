package com.kakuiwong.config.aop;

import com.kakuiwong.annotation.CacheThanos;
import com.kakuiwong.annotation.CacheThanosEvict;
import com.kakuiwong.annotation.CacheThanosPut;
import com.kakuiwong.bean.ThanosCacheTypeE;
import com.kakuiwong.config.cache.cacheCoreConfig.CacheCoreI;
import com.kakuiwong.config.properties.CachePropertiesBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * @author: gaoyang
 * @Description:
 */
@Aspect
public class ThanosCacheAop {

    @Autowired
    private CacheCoreI CacheCoreL1;
    @Autowired
    private CacheCoreI CacheCoreL2;
    @Autowired
    private CacheCoreI CacheCoreL1L2;
    @Autowired
    private CachePropertiesBean cachePropertiesBean;
    private SpelExpressionParser spelParser = new SpelExpressionParser();

    @Around(value = "@annotation(com.kakuiwong.annotation.CacheThanos)")
    public Object cacheThanos(ProceedingJoinPoint point) throws Throwable {
        CacheThanos annotation = getAnnotation(point, CacheThanos.class);
        return cacheCore().get(annotation.cachename(), key(point, annotation.key()), annotation.timeout(), point, annotation.sync());
    }

    @Around(value = "@annotation(com.kakuiwong.annotation.CacheThanosEvict)")
    public Object cacheThanosEvict(ProceedingJoinPoint point) throws Throwable {
        CacheThanosEvict annotation = getAnnotation(point, CacheThanosEvict.class);
        return cacheCore().delete(annotation.cachename(), key(point, annotation.key()), point, annotation.sync());
    }

    @Around(value = "@annotation(com.kakuiwong.annotation.CacheThanosPut)")
    public Object cacheThanosPut(ProceedingJoinPoint point) throws Throwable {
        CacheThanosPut annotation = getAnnotation(point, CacheThanosPut.class);
        return cacheCore().set(annotation.cachename(), key(point, annotation.key()), annotation.timeout(), point, annotation.sync());
    }

    public <T extends Annotation> T getAnnotation(ProceedingJoinPoint point, Class<T> aclass) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        return (T) signature.getMethod().getAnnotation(aclass);
    }

    public String key(ProceedingJoinPoint joinPoint, String key) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        List<String> paramNameList = Arrays.asList(methodSignature.getParameterNames());
        List<Object> paramList = Arrays.asList(joinPoint.getArgs());
        EvaluationContext ctx = new StandardEvaluationContext();
        for (int i = 0; i < paramNameList.size(); i++) {
            ctx.setVariable(paramNameList.get(i), paramList.get(i));
        }
        return spelParser.parseExpression(key).getValue(ctx).toString();
    }

    public CacheCoreI cacheCore() {
        switch (ThanosCacheTypeE.valueOf(cachePropertiesBean.getType())) {
            case L1:
                return CacheCoreL1;
            case L2:
                return CacheCoreL2;
            case L1L2:
                return CacheCoreL1L2;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
