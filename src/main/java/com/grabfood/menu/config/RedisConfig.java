package com.grabfood.menu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabfood.menu.entity.Menu;
import java.time.Duration;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.*;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig implements CachingConfigurer {
    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);
    private final RedisConnectionFactory factory;
    private final ObjectMapper mapper;
    private final Duration ttl;
    public RedisConfig(RedisConnectionFactory factory, ObjectMapper mapper,
            @Value("${menu.cache-ttl:10m}") Duration ttl) {
        this.factory = factory; this.mapper = mapper; this.ttl = ttl;
    }
    @Bean @Override public CacheManager cacheManager() {
        Jackson2JsonRedisSerializer<Menu> serializer = new Jackson2JsonRedisSerializer<>(mapper, Menu.class);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl).disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
        return RedisCacheManager.builder(factory).cacheDefaults(config).build();
    }
    @Bean @Override public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            public void handleCacheGetError(RuntimeException e, Cache c, Object key) {
                log.warn("Redis GET lỗi key={}; đọc DB: {}", key, e.getMessage());
            }
            public void handleCachePutError(RuntimeException e, Cache c, Object key, Object value) {
                log.warn("Redis PUT lỗi key={}: {}", key, e.getMessage());
            }
            public void handleCacheEvictError(RuntimeException e, Cache c, Object key) {
                log.error("Redis EVICT lỗi key={}: {}", key, e.getMessage());
            }
            public void handleCacheClearError(RuntimeException e, Cache c) {
                log.error("Redis CLEAR lỗi: {}", e.getMessage());
            }
        };
    }
}
