package com.xiuxiu.app.server.room.normal.poker.fgf;

public enum EFGFPlayRule {
    SINGLE_235(                     0x00000001),                    // 高牌235吃豹子
    BI_PAI_DOUBLE(                  0x00000002),                    // 比牌双倍 || 无限加注(疯狂炸金花)
    WEI_BI_PAI_BU_KE_JIAN(          0x00000004),                    // 未比牌不可见
    FANG_GOU_SHOU(                  0x00000008),                    // 防勾手
    YA_MAN(                         0x00000010),                    // 压满　
    FIRST_FOLLOW_MAX_REB(           0x00000020),                    // 首轮跟注最大注
    RR_DETECTION_IP(                0x00000040),                    // IP限制

    OUT_BALANCE(                    0x00000080),                    // 解散局算分 || 彩金(疯狂炸金花)
    BU_KE_CUO_PAI(                  0x00000100),                    // 搓牌


    ;

    private int value;

    EFGFPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
