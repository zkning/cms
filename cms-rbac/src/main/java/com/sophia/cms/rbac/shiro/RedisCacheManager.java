package com.sophia.cms.rbac.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisCacheManager implements CacheManager {
    RedisTemplate redisTemplate;
    private long defaultExpireTime = 3600;

    public RedisCacheManager(RedisTemplate redisTemplate, long defaultExpireTime) {
        this.redisTemplate = redisTemplate;
        this.defaultExpireTime = defaultExpireTime;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new RedisCache<K, V>(redisTemplate, defaultExpireTime);
    }
}
