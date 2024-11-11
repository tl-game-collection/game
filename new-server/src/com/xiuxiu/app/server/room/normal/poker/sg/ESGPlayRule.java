package com.xiuxiu.app.server.room.normal.poker.sg;

public enum ESGPlayRule {
    KUAI_SHU(                       0x00000001),                    // 快速场
    BU_KE_CUO_PAI(                  0x00000002),                    // 不可搓牌
    RR_DETECTION_IP(                0x00000004),                   // 防作弊

    ;

    private int value;

    ESGPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
