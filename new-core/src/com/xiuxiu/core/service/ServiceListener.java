package com.xiuxiu.core.service;

public interface ServiceListener {
    void onSucc(Object... args);

    void onFail(Throwable err);
}
