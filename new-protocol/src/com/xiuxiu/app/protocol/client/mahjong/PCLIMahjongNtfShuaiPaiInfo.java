package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfShuaiPaiInfo {
    public long playerUid;
    public List<Byte> card = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfShuaiPaiInfo{" +
                "playerUid=" + playerUid +
                ", card=" + card +
                '}';
    }
}
