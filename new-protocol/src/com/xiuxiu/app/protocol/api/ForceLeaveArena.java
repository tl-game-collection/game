package com.xiuxiu.app.protocol.api;

public class ForceLeaveArena {
    public long playerUid;                          // 玩家Uid
    public String sign;                             // md5(playerUid + key)

    @Override
    public String toString() {
        return "ForceLeaveArena{" +
                "playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
