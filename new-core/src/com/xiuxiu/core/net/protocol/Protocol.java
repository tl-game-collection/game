package com.xiuxiu.core.net.protocol;

import com.xiuxiu.core.net.codec.ByteBufferWrapper;

import io.netty.buffer.ByteBuf;

public interface Protocol {
    long FLAG_COMPRESS = 0x0000000000000001;        // 压缩标识
    byte getVersion();

    ByteBufferWrapper encode(Object message, ByteBufferWrapper byteBufferWrapper) throws Exception;
    
    ByteBuf encode(Object message) throws Exception;

    Object decode(ByteBufferWrapper byteBufferWrapper) throws Exception;

    enum Version {
        CLIENT(0),
        SERVICE(1),
        CLIENTV2(2),
        ;

        private byte version;

        Version(int version) {
            this.version = (byte) version;
        }

        public static Version parse(byte value) {
            switch (value) {
                case 0:
                    return CLIENT;
                case 1:
                    return SERVICE;
                case 2:
                    return CLIENTV2;
            }
            return null;
        }

        public static byte value(Version version) {
            return version.version;
        }
    }
}
