package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfBarInfo {
    public long uid;
    public byte cardValue;
    public long takeUid;
    public int type;                // 1: 放杠, 2: 明杠, 3: 暗杠, 4: 朝天杠
    public byte startIndex;
    public byte endIndex;
    public byte insertIndex;

    @Override
    public String toString() {
        return "PCLIMahjongNtfBarInfo{" +
                "uid=" + uid +
                ", cardValue=" + cardValue +
                ", takeUid=" + takeUid +
                ", type=" + type +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", insertIndex=" + insertIndex +
                '}';
    }
}
