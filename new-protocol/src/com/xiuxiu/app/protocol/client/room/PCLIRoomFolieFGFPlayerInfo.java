package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomFolieFGFPlayerInfo extends PCLIRoomPlayerInfo {
    public int bankRoll;                    // 剩余筹码数量
    public int pot;                         // 桌上筹码
    public boolean autoFillUpStakes;        // 是否自动补充筹码
    public int stakes;

    @Override
    public String toString() {
        return "PCLIRoomFolieFGFPlayerInfo{" +
                "playerInfo=" + playerInfo +
                ", index=" + index +
                ", state=" + state +
                ", guess=" + guess +
                ", pot=" + pot +
                ", bankRoll=" + bankRoll +
                ", stakes=" + stakes +
                ", autoFillUpStakes=" + autoFillUpStakes +
                ", deskCard='" + deskCard + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
