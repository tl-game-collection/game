package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqDownGold {
    public long clubUid;
    public int gold;
    public String bankCard;
    public String bankCardHolder;

    @Override
    public String toString() {
        return "PCLIPlayerReqDownGold{" +
                "clubUid=" + clubUid +
                ", gold=" + gold +
                ", bankCard='" + bankCard + '\'' +
                ", bankCardHolder='" + bankCardHolder + '\'' +
                '}';
    }
}
