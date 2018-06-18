package com.imooc.cache;

import com.imooc.util.JedisUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

@Component
public class RedisCache<K,V> implements Cache<K,V> {

    @Resource
    private JedisUtil jedisUtil;

    private final String CACHE_PREFIX = "imooc-cache";

    private byte[] getKey(K k){
        if(k instanceof String){
            return (CACHE_PREFIX+k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }
    @Override
    public V get(K key) throws CacheException {
        System.out.println("从redis获取权限数据");
        byte[] value = jedisUtil.get(getKey(key));
        if(value != null){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        byte[] keybyte = getKey(key);
        byte[] valuebyte = SerializationUtils.serialize(value);
        jedisUtil.set(keybyte,valuebyte);
        jedisUtil.expire(keybyte,600);
        return value;
    }

    @Override
    public V remove(K key) throws CacheException {
        byte[] keybyte = getKey(key);
        byte[] value = jedisUtil.get(keybyte);
        jedisUtil.del(keybyte);
        if (value != null){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
