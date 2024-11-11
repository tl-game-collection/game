package com.xiuxiu.core.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Cache<K, V> {
    public static interface Load<K, V> {
        V load(K key) throws Exception;
    }

    protected long duration;
    protected TimeUnit unit;
    protected long maximum;
    protected Load<K, V> load;

    protected final LoadingCache<K, V> cache;

    public Cache(long duration, TimeUnit unit, long maximum, Load<K, V> load) {
        this.duration = duration;
        this.unit = unit;
        this.maximum = maximum;
        this.load = load;

        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(duration, unit)
                .maximumSize(maximum)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K key) throws Exception {
                        return load.load(key);
                    }
                });
    }

    public V get(K key) {
        try {
            return this.cache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void put(K key, V value) {
        this.cache.put(key, value);
    }
}
