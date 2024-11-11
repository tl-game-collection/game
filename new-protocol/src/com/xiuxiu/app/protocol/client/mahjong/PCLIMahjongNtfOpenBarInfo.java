package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfOpenBarInfo {
    public List<Byte> card = new ArrayList<>();
    public int cap1;
    public int cap2;

    @Override
    public String toString() {
        return "PCLIMahjongNtfOpenBarInfo{" +
                "card=" + card +
                ", cap1=" + cap1 +
                ", cap2=" + cap2 +
                '}';
    }
}
