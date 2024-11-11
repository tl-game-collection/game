package com.xiuxiu.app.server.room.normal.mahjong2.zzmj;

public enum EZZMJPlayRule {
    TEN(                    0x000001),                                   // 10s
    SEVEN(                  0x000002),                                   // 胡7对
    QGH(                    0x000004),                                   // 抢杠胡
    HU_MUST(                0x000008),                                   // 必胡
    BANKER_INC(             0x000010),                                   // 庄家+1
    ONLY_ZIMO(              0x000020),                                   // 只能自摸
    TONG_LESS(              0x000040),                                   // 缺少筒子
    EAT(                    0x000080),                                   // 吃
    HZ_LAI_ZI(              0x000100),                                   // 红中癞子
    _258_EYE(               0x000200),                                   // 258将
    PIAO_SCORE(             0x000400),                                   // 飘分
    LOU_PENG(               0x000800),                                   // 楼碰
    WYPN(                   0x001000),                                   // 围一飘鸟
    FANGBAR(                0x002000),                                   // 放杠3番
    GEN(                    0x004000),                                   // 根
    ;

    private int value;

    EZZMJPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
