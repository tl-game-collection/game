package com.xiuxiu.app.protocol.client.mahjong;

import java.util.List;

public class PCLIMahjongNtfSelectPiaoFixedValueInfo {
    public List<Long> playerUids;
    public int value;

    @Override
    public String toString() {
        return "PCLIMahjongNtfSelectPiaoFixedValueInfo{" + "playerUids=" + playerUids + ", value=" + value + '}';
    }
}
