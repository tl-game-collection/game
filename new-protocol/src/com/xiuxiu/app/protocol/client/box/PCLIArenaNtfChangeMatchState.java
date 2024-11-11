package com.xiuxiu.app.protocol.client.box;

public class PCLIArenaNtfChangeMatchState {
    public long groupUid;               // 群uid
    public long arenaUid;               // 竞技场uid
    public long matchUid;               // 匹配uid
    public long playerUid;              // 玩家uid
    public boolean ready;               // true: 准备, false: 没准备

    @Override
    public String toString() {
        return "PCLIArenaNtfChangeMatchState{" +
                "groupUid=" + groupUid +
                ", arenaUid=" + arenaUid +
                ", matchUid=" + matchUid +
                ", playerUid=" + playerUid +
                ", ready=" + ready +
                '}';
    }
}
