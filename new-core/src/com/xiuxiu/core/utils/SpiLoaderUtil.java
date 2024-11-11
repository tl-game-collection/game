package com.xiuxiu.core.utils;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public final class SpiLoaderUtil {
    private static final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    public static <T> T load(Class<T> clazz) {
        return SpiLoaderUtil.load(clazz, null);
    }

    public static <T> T load(Class<T> clazz, boolean cache) {
        if (cache) {
            return load(clazz);
        } else {
            return load0(clazz, null);
        }
    }

    public static <T> T load(Class<T> clazz, String name) {
        String key = clazz.getName();
        Object value = cache.get(key);
        if (null == value) {
            T newValue = load0(clazz, name);
            if (null != newValue) {
                cache.put(key, newValue);
            }
            return newValue;
        } else if (clazz.isInstance(value)) {
            return (T) value;
        }
        return load0(clazz, name);
    }

    private static <T> T load0(Class<T> clazz, String name) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        T value = filterByName(loader, name);
        if (null == value) {
            loader = ServiceLoader.load(clazz, SpiLoaderUtil.class.getClassLoader());
            value = filterByName(loader, name);
        }
        if (null == value) {
            throw new IllegalStateException("can't find META-INF/services/" + clazz.getName() + " on classpath");
        }
        return value;
    }

    private static <T> T filterByName(ServiceLoader<T> loader, String name) {
        Iterator<T> it = loader.iterator();
        do {
            if (StringUtil.isEmptyOrNull(name)) {
                while (it.hasNext()) {
                    return it.next();
                }
                break;
            }
            while (it.hasNext()) {
                T value = it.next();
                if (name.equals(value.getClass().getName()) || name.equals(value.getClass().getSimpleName())) {
                    return value;
                }
            }
        } while (false);
        return null;
    }
}
