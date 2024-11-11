package com.xiuxiu.core.net.message;

public interface Handler {
    Object handler(Object owner, Object request);
}
