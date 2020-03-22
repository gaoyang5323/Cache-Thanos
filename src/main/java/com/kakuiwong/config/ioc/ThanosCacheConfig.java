package com.kakuiwong.config.ioc;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kakuiwong.config.aop.ThanosCacheAop;
import com.kakuiwong.config.cache.cacheConfig.CaffCacheImpl;
import com.kakuiwong.config.cache.cacheConfig.RedisCacheImpl;
import com.kakuiwong.config.cache.cacheConfig.ThanosCacheI;
import com.kakuiwong.config.cache.cacheCoreConfig.CacheCoreI;
import com.kakuiwong.config.cache.cacheCoreConfig.CacheCoreL1;
import com.kakuiwong.config.cache.cacheCoreConfig.CacheCoreL1L2;
import com.kakuiwong.config.cache.cacheCoreConfig.CacheCoreL2;
import com.kakuiwong.config.properties.CachePropertiesBean;
import com.kakuiwong.config.properties.RedissonProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.util.StringUtils;

/**
 * @author: gaoyang
 * @Description:
 */
@Configuration
@EnableConfigurationProperties({RedissonProperties.class, CachePropertiesBean.class})
public class ThanosCacheConfig {

    @Autowired(required = false)
    private RedisTemplate redisTemplate;
    @Autowired(required = false)
    private RedisConnectionFactory factory;
    @Autowired
    private CachePropertiesBean cachePropertiesBean;
    @Autowired
    private RedissonProperties redssionProperties;


    @ConditionalOnMissingBean(name = "L1Cache", value = ThanosCacheI.class)
    @Bean("L1Cache")
    public ThanosCacheI l1Cache() {
        Cache<Object, Object> cache = Caffeine.newBuilder().maximumSize(cachePropertiesBean.getLocalMaxSize()).build();
        return new CaffCacheImpl((CaffeineCache) cache);
    }

    @ConditionalOnMissingBean(name = "L2Cache", value = ThanosCacheI.class)
    @Bean("L2Cache")
    public ThanosCacheI l2Cache() {
        if (redisTemplate == null) {
            if (factory == null) {
                throw new RuntimeException();
            }
            redisTemplate = new StringRedisTemplate(factory);
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            jackson2JsonRedisSerializer.setObjectMapper(om);
            redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.afterPropertiesSet();
        }
        RedisCacheImpl redisCache = new RedisCacheImpl(redisTemplate);
        return redisCache;
    }


    @Bean
    @ConditionalOnProperty(name = "redisson.master-name")
    RedissonClient redissonSentinel() {
        Config config = new Config();
        SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(redssionProperties.getSentinelAddresses())
                .setMasterName(redssionProperties.getMasterName())
                .setTimeout(redssionProperties.getTimeout())
                .setMasterConnectionPoolSize(redssionProperties.getMasterConnectionPoolSize())
                .setSlaveConnectionPoolSize(redssionProperties.getSlaveConnectionPoolSize());

        if (!StringUtils.isEmpty(redssionProperties.getPassword())) {
            serverConfig.setPassword(redssionProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnProperty(name = "redisson.address")
    RedissonClient redissonSingle() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redssionProperties.getAddress())
                .setTimeout(redssionProperties.getTimeout())
                .setConnectionPoolSize(redssionProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redssionProperties.getConnectionMinimumIdleSize());

        if (!StringUtils.isEmpty(redssionProperties.getPassword())) {
            serverConfig.setPassword(redssionProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    public CacheCoreI CacheCoreL1() {
        return new CacheCoreL1();
    }

    @Bean
    public CacheCoreI CacheCoreL1L2() {
        return new CacheCoreL1L2();
    }

    @Bean
    public CacheCoreI CacheCoreL2() {
        return new CacheCoreL2();
    }

    @Bean
    public ThanosCacheAop thanosCacheAop() {
        return new ThanosCacheAop();
    }
}
