package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfCSHuInfo {
    public List<Byte> handCard = new ArrayList<>();         // 手牌
    public List<Byte> capList = new ArrayList<>();          // 筛子
    public List<Integer> allPx = new ArrayList<>();         // 所有牌型
    public HashMap<Long, Integer> allScore = new HashMap<>();// 分数
    public long huPlayerUid;                                // 胡牌玩家uid

    @Override
    public String toString() {
        return "PCLIMahjongNtfCSHuInfo{" +
                "handCard=" + handCard +
                ", capList=" + capList +
                ", allPx=" + allPx +
                ", allScore=" + allScore +
                ", huPlayerUid=" + huPlayerUid +
                '}';
    }
}
