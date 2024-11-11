package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfCrapAndCardInfo {
    public long playerUid;
    public List<Integer> craps = new ArrayList<>(); // 骰子点数
    public byte card; // 牌

    @Override
    public String toString() {
        return "PCLIMahjongNtfCrapAndCardInfo{" +
                "playerUid=" + playerUid +
                ", craps=" + craps +
                ", card=" + card +
                '}';
    }
}
