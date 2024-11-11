package com.xiuxiu.app.protocol.client.mahjong;

import java.util.List;

public class PCLIMahjongNtfCanTakeInfoByKWX extends PCLIMahjongNtfCanTakeInfo {
    public boolean liangPai;                    // 是否可亮牌
    public List<PCLIMahjongBrightInfo> brightInfo;

    public PCLIMahjongNtfCanTakeInfoByKWX() {
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfCanTakeInfo{" +
                "uid=" + uid +
                ", liangPai=" + liangPai +
                ", brightInfo=" + brightInfo +
                '}';
    }
}
