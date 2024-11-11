package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;

public class PCLIMahjongNtfBumpInfo {
    public long uid;
    public byte cardValue;
    public long takeUid;
    public byte index;
    public boolean ting;//能否听牌
    public HashMap<Byte, HashMap<Byte, Integer>> tingInfo = new HashMap<>();

    public PCLIMahjongNtfBumpInfo() {

    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfBumpInfo{" +
                "uid=" + uid +
                ", cardValue=" + cardValue +
                ", takeUid=" + takeUid +
                ", index=" + index +
                ", tingInfo=" + tingInfo +
                  "ting"+
                '}';
    }
}
