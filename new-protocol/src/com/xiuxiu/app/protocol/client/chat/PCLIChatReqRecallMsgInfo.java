package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatReqRecallMsgInfo {
    public long recallMsgUid;             // 撤销消息uid

    @Override
    public String toString() {
        return "PCLIChatReqRecallMsgInfo{" +
                "recallMsgUid=" + recallMsgUid +
                '}';
    }
}
