package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfTakeInfo {
    public long uid;
    public byte cardValue;
    public byte isLast;//0，1两种值
    public byte index;
    public byte outputCardIndex;
    public int length;
    public boolean auto;
    public List<Byte> myDeskCard = new ArrayList<>();
    public List<Byte> myHandCard = new ArrayList<>();

    public PCLIMahjongNtfTakeInfo() {
    }

    public PCLIMahjongNtfTakeInfo(long playerUid, byte card, byte last, byte outputCardIndex, int length, byte index, boolean auto) {
        this.uid = playerUid;
        this.cardValue = card;
        this.isLast = last;
        this.outputCardIndex = outputCardIndex;
        this.length = length;
        this.index = index;
        this.auto = auto;
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfTakeInfo{" +
                "uid=" + uid +
                ", cardValue=" + cardValue +
                ", isLast=" + isLast +
                ", index=" + index +
                ", outputCardIndex=" + outputCardIndex +
                ", length=" + length +
                ", auto=" + auto +
                ", myDeskCard=" + myDeskCard +
                ", myHandCard=" + myHandCard +
                '}';
    }
}
