package com.xiuxiu.core;

public interface ICallback<T> {
    void call(T... args);
}
