package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatReqDelMsgInfo {
    public long delMsgUid;             // 删除消息uid

    @Override
    public String toString() {
        return "PCLIChatReqDelMsgInfo{" +
                "delMsgUid=" + delMsgUid +
                '}';
    }
}
