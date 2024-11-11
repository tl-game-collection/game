package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatReqGetMsgInfo {
    public long lastMsgUid;             // 最后一条消息uid

    @Override
    public String toString() {
        return "PCLIChatReqGetMsgInfo{" +
                "lastMsgUid=" + lastMsgUid +
                '}';
    }
}
