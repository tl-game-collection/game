package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfGameOverInfoByFZMJ extends PCLIMahjongNtfGameOverInfo {
    public List<Byte> haiDiLaoCard = new ArrayList<>(); // 海底捞摸的牌
    public long haiDiLaoStartPlayerUid = -1;        // 海底捞开始摸牌的玩家uid
    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByFZMJ{" +
                ", haiDiLaoCard=" + haiDiLaoCard +
                ", haiDiLaoStartPlayerUid=" + haiDiLaoStartPlayerUid +
                ", allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
