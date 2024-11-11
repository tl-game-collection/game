package com.xiuxiu.app.protocol.client.forbid;

import java.util.List;

public class PCLIForbidReqAdd {
    public long clubUid;        //亲友圈uid
    public List<Long> playerUids; //玩家id列表目前长度是2-3个

    @Override
    public String toString() {
        return "PCLIForbidReqAdd{" +
                ", clubUid=" + clubUid +
                ", playerUids=" + playerUids +
                '}';
    }
}
