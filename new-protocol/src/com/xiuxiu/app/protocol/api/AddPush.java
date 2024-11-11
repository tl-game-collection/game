package com.xiuxiu.app.protocol.api;

public class AddPush {
    public int type;
    public String message;

    @Override
    public String toString() {
        return "AddPush{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
