package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfLaiPiInfo {
    public byte fangPai; // 翻的牌
    public byte laiZi; // 赖子牌
    public List<Byte> piList = new ArrayList<>(); // 皮子列表

    @Override
    public String toString() {
        return "PCLIMahjongNtfLaiPiInfo{" +
                "fangPai=" + fangPai +
                ", laiZi=" + laiZi +
                ", piList=" + piList +
                '}';
    }
}
