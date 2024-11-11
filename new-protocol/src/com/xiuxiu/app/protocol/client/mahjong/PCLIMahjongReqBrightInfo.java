package com.xiuxiu.app.protocol.client.mahjong;

import java.util.List;

public class PCLIMahjongReqBrightInfo {
    public List<Byte> kou;
    public byte takeCard;
    public byte isLast;//0，1两种值
    public byte takeCardIndex;
    public byte outputCardIndex;
    public int length;

    @Override
    public String toString() {
        return "PCLIMahjongReqBrightInfo{" +
                "kou=" + kou +
                ", takeCard=" + takeCard +
                ", isLast=" + isLast +
                ", takeCardIndex=" + takeCardIndex +
                ", outputCardIndex=" + outputCardIndex +
                ", length=" + length +
                '}';
    }
}
