package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfEndHuanPai {
    public List<Byte> card = new ArrayList<>();
    public List<Byte> myCard = new ArrayList<>();
    public int type;

    @Override
    public String toString() {
        return "PCLIMahjongNtfEndHuanPai{" +
                "card=" + card +
                ", myCard=" + myCard +
                ", type=" + type +
                '}';
    }
}
