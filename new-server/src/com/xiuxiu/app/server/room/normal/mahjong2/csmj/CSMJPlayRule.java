package com.xiuxiu.app.server.room.normal.mahjong2.csmj;

public enum CSMJPlayRule {
    TEN(                    0x00000001),                                    // 10s
    BANKER_INC(             0x00000002),                                    // 庄家+1
    BAR_WITH_EAT_OR_BUMP(   0x00000004),                                    // 杠后可吃碰
    BAR_FUBLE2(             0x00000008),                                    // 杠后摸2尊
    FAKE_EYE_HU(            0x00000010),                                    // 假将胡
    MEN_QING(               0x00000020),                                    // 门清
    TDH(                    0X00000040),                                    // 天地胡
    DISCARD_HU_ONLY_ZI_MO(  0x00000080),                                    // 弃胡只能自摸
    QYM(                    0x00000100),                                    // 缺一门
    QYS(                    0x00000200),                                    // 清一色
    LLS(                    0x00000400),                                    // 六六顺
    DSX(                    0x00000800),                                    // 大四喜
    BBH(                    0x00001000),                                    // 板板胡
    YZH(                    0x00002000),                                    // 一枝花
    JJG(                    0x00004000),                                    // 节节高
    ZTSX(                   0x00008000),                                    // 中途四喜
    ZTLLS(                  0x00010000),                                    // 中途六六顺
    ST(                     0x00020000),                                    // 三筒
    JTYN(                   0x00040000),                                    // 金童玉女
    ;

    private int value;

    CSMJPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
