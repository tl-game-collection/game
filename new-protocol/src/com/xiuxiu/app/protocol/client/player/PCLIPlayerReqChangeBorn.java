package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqChangeBorn {
    public long born;       // 出生年月 时间戳ms

    @Override
    public String toString() {
        return "PCLIPlayerReqChangeBorn{" +
                "born=" + born +
                '}';
    }
}
