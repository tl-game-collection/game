package com.xiuxiu.core.net.codec;

public interface Decoder {
    Object decoder(int commandId, byte[] bytes) throws Exception;
}
