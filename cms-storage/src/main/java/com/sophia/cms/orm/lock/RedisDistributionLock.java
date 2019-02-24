package com.sophia.cms.orm.lock;

import com.google.common.collect.Maps;
import com.sophia.cms.framework.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

import java.util.Map;
import java.util.UUID;


@Component
public class RedisDistributionLock {

    /**
     * 默认锁失效时间:MILLISECONDS
     */
    private static final long DEFAULT_EXPIRE = 5 * 60 * 1000;
    private ThreadLocal<Map<String, String>> lockThreadLocal = new ThreadLocal<>();

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public boolean acquire(String key) {
        return acquire(key, DEFAULT_EXPIRE);
    }

    public boolean acquire(String key, long expire) {
        String lockValue = UUID.randomUUID().toString();

        // setNX
        Boolean flag = (Boolean) redisTemplate.execute((RedisCallback) connection -> {
            RedisSerializer keySerializer = redisTemplate.getKeySerializer();
            RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
            Object result = connection.execute("set",
                    keySerializer.serialize(key),
                    valueSerializer.serialize(lockValue),
                    SafeEncoder.encode("NX"),
                    SafeEncoder.encode("PX"),
                    Protocol.toByteArray(expire)
            );
            return result != null;
        });
        lockThreadLocal.set(Maps.newConcurrentMap());
        lockThreadLocal.get().put(key, lockValue);
        return flag;
    }

    public boolean acquire(String key, long expire, int retryTimes, long sleepMillis) {
        Boolean lockResult = acquire(key, expire);
        while (retryTimes-- > 0 && !lockResult) {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                throw new ServiceException("retry set redis lock error", e);
            }
            lockResult = acquire(key, expire);
        }
        return lockResult;
    }

    public void release(String key) {
        Map<String, String> cacheLockMap = lockThreadLocal.get();

        // 线程未申请锁
        if (null == cacheLockMap) {
            return;
        }
        try {
            String lockValue = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(lockValue) && lockValue.equals(cacheLockMap.get(key))) {
                redisTemplate.delete(key);
            }
        } finally {
            if (null != cacheLockMap) {
                cacheLockMap.remove(key);
            }
            if (null != lockThreadLocal) {
                lockThreadLocal.remove();
            }
        }
    }
}
