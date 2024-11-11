package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatReqGetMsgAckInfo {
    public long lastMsgUid;             // 最后一条消息uid
    public long opaque;                 // 聊天消息流水号, 有PCLIChatNtfGetMsgOkInfo协议中提供

    @Override
    public String toString() {
        return "PCLIChatReqGetMsgAckInfo{" +
                "lastMsgUid=" + lastMsgUid +
                ", opaque=" + opaque +
                '}';
    }
}
