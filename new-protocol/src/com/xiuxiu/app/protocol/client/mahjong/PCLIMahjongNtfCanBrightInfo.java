package com.xiuxiu.app.protocol.client.mahjong;

import java.util.List;

public class PCLIMahjongNtfCanBrightInfo extends PCLIMahjongNtfStartTakeInfo {
    public boolean liangPai;                    // 是否可亮牌
    public List<PCLIMahjongBrightInfo> brightInfo;

    @Override
    public String toString() {
        return "PCLIMahjongNtfCanBrightInfo{" +
                "liangPai=" + liangPai +
                ", brightInfo=" + brightInfo +
                '}';
    }
}
