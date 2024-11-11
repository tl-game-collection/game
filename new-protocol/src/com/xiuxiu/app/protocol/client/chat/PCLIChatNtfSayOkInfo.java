package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatNtfSayOkInfo {
    public long opaque;         // 该次请求标识
    public long msgUid;         // 消息uid

    @Override
    public String toString() {
        return "PCLIChatNtfSayOkInfo{" +
                "opaque=" + opaque +
                ", msgUid=" + msgUid +
                '}';
    }
}
