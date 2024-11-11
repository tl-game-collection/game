package com.xiuxiu.app.protocol.client.room;

import com.xiuxiu.app.protocol.client.PCLIPlayerBriefInfo;

public class PCLIRoomPlayerInfo {
    public PCLIPlayerBriefInfo playerInfo;
    public int index;
    public int state;                       // 0: 未准备, 1:准备
    public boolean guess;                   // 是否是游客
    public boolean isBan;                   // 是否禁言
    public String deskCard;                 // 桌上的牌
    public long score;                    // 分数

    @Override
    public String toString() {
        return "PCLIRoomPlayerInfo{" +
                "playerInfo=" + playerInfo +
                ", index=" + index +
                ", state=" + state +
                ", guess=" + guess +
                ", isBan=" + isBan +
                ", deskCard='" + deskCard + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
