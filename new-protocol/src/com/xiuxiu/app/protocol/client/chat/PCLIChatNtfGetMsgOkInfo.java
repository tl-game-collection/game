package com.xiuxiu.app.protocol.client.chat;

import java.util.ArrayList;
import java.util.List;

public class PCLIChatNtfGetMsgOkInfo {
    public List<PCLIChatMsg> list = new ArrayList<>();      // 所有聊天消息
    public long lastMsgUid;                                 // 最后一条消息uid
    public long opaque;                                     // 获取聊天消息流水号

    @Override
    public String toString() {
        return "PCLIChatNtfGetMsgOkInfo{" +
                "list=" + list +
                ", lastMsgUid=" + lastMsgUid +
                ", opaque=" + opaque +
                '}';
    }
}
