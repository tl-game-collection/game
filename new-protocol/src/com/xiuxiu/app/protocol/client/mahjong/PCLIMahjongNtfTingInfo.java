package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfTingInfo extends PCLIMahjongNtfTakeInfo {
    public PCLIMahjongNtfTingInfo() {
    }

    public PCLIMahjongNtfTingInfo(long playerUid, byte card, byte last, byte outputCardIndex, int length, byte index, boolean auto) {
        super(playerUid, card, last, outputCardIndex, length, index, auto);
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfTingInfo{" +
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
