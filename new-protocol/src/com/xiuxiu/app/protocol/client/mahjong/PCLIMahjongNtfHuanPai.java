package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfHuanPai {
    public List<Byte> card = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfHuanPai{" +
                "card=" + card +
                '}';
    }
}
