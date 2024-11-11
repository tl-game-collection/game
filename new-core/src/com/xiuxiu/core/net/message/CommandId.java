package com.xiuxiu.core.net.message;

public interface CommandId {
    int HEARTBEAT = 0x0000;             // 心跳
    int ERROR = 0x0001;                 // 错误信息
    int HEARTBEAT_CLIENT = 0x0003;      // 客户端心跳
}
