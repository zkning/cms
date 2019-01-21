package com.sophia.cms.orm.lock;

import com.sophia.cms.framework.exception.ServiceException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


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
        Boolean lockResult = redisTemplate.opsForValue().setIfAbsent(key, lockValue);
        if (!lockResult) {
            return false;
        }
        Boolean expireResult = redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
        if (!expireResult) {
            return false;
        }
        lockThreadLocal.set(Maps.newConcurrentMap());
        lockThreadLocal.get().put(key, lockValue);
        return true;
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
