package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfBrightInfo {
    public long uid;
    public List<Byte> bright = new ArrayList<>();                   // 亮牌
    public HashMap<Byte, Integer> ting = new HashMap();             // 听牌
    public byte takeCard;                                           // 打牌
    public byte takeCardIndex;                                      // 打牌索引

    @Override
    public String toString() {
        return "PCLIMahjongNtfBrightInfo{" +
                "uid=" + uid +
                ", bright=" + bright +
                ", ting=" + ting +
                ", takeCard=" + takeCard +
                ", takeCardIndex=" + takeCardIndex +
                '}';
    }
}
