package com.xiuxiu.app.protocol.client.mahjong;

import java.util.List;

public class PCLIMahjongNtfBumpInfoByKWX extends PCLIMahjongNtfBumpInfo {
    public boolean liangPai;                    // 是否可亮牌
    public List<PCLIMahjongBrightInfo> brightInfo;

    public PCLIMahjongNtfBumpInfoByKWX() {

    }

    public PCLIMahjongNtfBumpInfoByKWX(long bumpPlayerUid, long takePlayerUid, byte card, byte index, boolean bright, List<PCLIMahjongBrightInfo> brightInfo) {
        this.uid = bumpPlayerUid;
        this.cardValue = card;
        this.takeUid = takePlayerUid;
        this.index = index;
        this.brightInfo = brightInfo;
        this.liangPai = bright;
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfBumpInfoByKWX{" +
                "liangPai=" + liangPai +
                ", brightInfo=" + brightInfo +
                ", uid=" + uid +
                ", cardValue=" + cardValue +
                ", takeUid=" + takeUid +
                ", index=" + index +
                '}';
    }
}
