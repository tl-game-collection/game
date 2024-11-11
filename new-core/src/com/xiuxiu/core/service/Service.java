package com.xiuxiu.core.service;

public interface Service {
    void init();

    void start();

    void start(ServiceListener listener);

    void stop();

    void stop(ServiceListener listener);

    boolean running();
}
