package com.xiuxiu.app.protocol.api;

public class BindBizChannelInfo {
    public int bizChannel;
    public long groupUid;

    @Override
    public String toString() {
        return "BindBizChannelInfo{" +
                "groupUid=" + groupUid +
                ", bizChannel=" + bizChannel +
                '}';
    }
}
