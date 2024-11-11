package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatReqDelRecallMsgInfo {
    public long delRecallMsgUid;             // 删除测回消息uid

    @Override
    public String toString() {
        return "PCLIChatReqDelRecallMsgInfo{" +
                "delRecallMsgUid=" + delRecallMsgUid +
                '}';
    }
}
