package com.sophia.cms.rbac.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.codec.Base64;
import org.assertj.core.util.Lists;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 自定义redis key
 *
 * @param <K>
 * @param <V>
 */
public class RedisCache<K, V> implements Cache<K, V> {
    private final String sessionCachePrefix = "session_cache:";
    RedisTemplate redisTemplate;
    private long defaultExpireTime;

    public RedisCache(RedisTemplate redisTemplate, long defaultExpireTime) {
        this.redisTemplate = redisTemplate;
        this.defaultExpireTime = defaultExpireTime;
    }

    @Override
    public V get(K k) throws CacheException {
        Object value = redisTemplate.opsForValue().get(getSessionCacheKey(k));
        if (null != value) {
            return (V) deserialize(value.toString());
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        redisTemplate.opsForValue().set(getSessionCacheKey(k), serialize(v), defaultExpireTime, TimeUnit.SECONDS);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        V v = (V) redisTemplate.opsForValue().get(getSessionCacheKey(k));
        if (null != v) {
            redisTemplate.delete(getSessionCacheKey(k));
        }
        return v;
    }

    @Override
    public void clear() throws CacheException {
        Set<K> keys = keys();
        if (null != keys) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public int size() {
        Set<K> keys = keys();
        if (null != keys) {
            return keys.size();
        }
        return 0;
    }

    @Override
    public Set<K> keys() {
        return redisTemplate.keys(this.sessionCachePrefix + "*");
    }

    @Override
    public Collection<V> values() {
        Set<K> keys = keys();
        if (null == keys) {
            return null;
        }
        List<V> list = Lists.newArrayList();
        for (K k : keys) {
            Object value = redisTemplate.opsForValue().get(k);
            if (null != value) {
                list.add((V) value);
            }
        }
        return list;
    }

    private String getSessionCacheKey(K k) {
        return this.sessionCachePrefix + k;
    }

    private Object deserialize(String str) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(Base64.decode(str));
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("deserialize session error", e);
        } finally {
            try {
                ois.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String serialize(Object obj) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return Base64.encodeToString(bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("serialize session error", e);
        } finally {
            try {
                oos.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
