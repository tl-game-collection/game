package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatReqUpdateMsgAckInfo {
    public long messageUid;             // 更新消息uid

    @Override
    public String toString() {
        return "PCLIChatReqUpdateMsgAckInfo{" +
                "messageUid=" + messageUid +
                '}';
    }
}
