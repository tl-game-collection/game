package com.xiuxiu.app.protocol.client.mahjong;

import java.util.Collections;
import java.util.List;

public class PCLIMahjongNtfFumbleInfoByKWX extends PCLIMahjongNtfFumbleInfo {
    public boolean liangPai;                        // 是否可亮牌
    public List<PCLIMahjongBrightInfo> brightInfo;

    public PCLIMahjongNtfFumbleInfoByKWX(long playerUid, int playerIndex, byte card, int remainCard, boolean auto) {
        this.uid = playerUid;
        this.index = playerIndex;
        this.value = card;
        this.remainCard = remainCard;
        this.auto = auto;
    }

    public PCLIMahjongNtfFumbleInfoByKWX(long playerUid, int playerIndex, byte card, int remainCard, boolean auto, boolean bright, List<PCLIMahjongBrightInfo> brightInfo) {
        this.uid = playerUid;
        this.index = playerIndex;
        this.value = card;
        this.remainCard = remainCard;
        this.auto = auto;
        this.brightInfo = null == brightInfo ? Collections.EMPTY_LIST : brightInfo;
        this.liangPai = bright;
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfFumbleInfoByKWX{" +
                "uid=" + uid +
                ", index=" + index +
                ", value=" + value +
                ", remainCard=" + remainCard +
                ", auto=" + auto +
                ", liangPai=" + liangPai +
                ", brightInfo=" + brightInfo +
                ", handCard=" + handCard +
                '}';
    }
}
