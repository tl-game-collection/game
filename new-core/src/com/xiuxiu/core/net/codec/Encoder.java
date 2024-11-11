package com.xiuxiu.core.net.codec;

public interface Encoder {
    byte[] encode(Object msg) throws Exception;
}
