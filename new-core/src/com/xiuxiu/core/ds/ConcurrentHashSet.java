package com.xiuxiu.core.ds;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> {
    private ConcurrentHashMap<E, Boolean> map;

    public ConcurrentHashSet() {
        super();
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
        return null == this.map.putIfAbsent(e, Boolean.TRUE);
    }

    @Override
    public boolean remove(Object o) {
        return null != this.map.remove(o);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public int size() {
        return this.map.size();
    }
}
