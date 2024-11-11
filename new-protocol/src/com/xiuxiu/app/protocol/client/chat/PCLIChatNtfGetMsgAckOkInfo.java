package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatNtfGetMsgAckOkInfo {
    public long lastMsgUid;             // 最后一条消息uid

    @Override
    public String toString() {
        return "PCLIChatNtfGetMsgAckOkInfo{" +
                "lastMsgUid=" + lastMsgUid +
                '}';
    }
}
