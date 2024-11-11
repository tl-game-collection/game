package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;

public class PCLIMahjongNtfEatInfo {
    public long uid;
    public long takeUid;
    public byte type;            // 1: 前(left), 2: 中(middle), 3: 后(right)
    public byte cardValue;
    public HashMap<Byte, HashMap<Byte, Integer>> tingInfo = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfEatInfo{" +
                "uid=" + uid +
                ", takeUid=" + takeUid +
                ", type=" + type +
                ", cardValue=" + cardValue +
                ", tingInfo=" + tingInfo +
                '}';
    }
}
