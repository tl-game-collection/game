package com.xiuxiu.core.net.message;

public enum MessageType {
    REQUEST,
    ONEWAY,
    RESPONSE;

    public static MessageType parse(byte value) {
        switch (value) {
            case 0:
                return REQUEST;
            case 1:
                return ONEWAY;
            case 2:
                return RESPONSE;
        }
        return null;
    }

    public static byte value(MessageType type) {
        return (byte) type.ordinal();
    }
}
