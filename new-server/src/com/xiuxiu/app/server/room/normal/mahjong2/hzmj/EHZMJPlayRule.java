package com.xiuxiu.app.server.room.normal.mahjong2.hzmj;

public enum EHZMJPlayRule {
    TEN(                    0x000001),                                   // 10s
    SEVEN(                  0x000002),                                   // 胡7对
    NONE_ZHONG_MUL(         0x000004),                                   // 无中翻倍
    BANKER_INC(             0x000008),                                   // 庄家+1
    HU_MUST(                0x000010),                                   // 必胡
    START_4_HZ_HU(          0x000020),                                   // 起手4红中胡
    PIAO_SCORE(             0x000040),                                   // 飘分
    LOU_PENG(               0x000080),                                   // 楼碰
    MORE_HU(                0x000100),                                   // 一炮多响
    WYPN(                   0x000200),                                   // 围一飘鸟
    TONG_LESS(              0x000400),                                   // 缺少筒子
    QD_PPH_TH_QYS_INC(      0x000800),                                   // 胡七对,碰碰胡,天胡,清一色+1
    DIAN_PAO(               0x001000),                                   // 可接跑
    HZ_UN_DIAN_PAO(         0x002000),                                   // 红中不可接跑
    DIAN_PAO_2(             0x004000),                                   // 点炮2分
    QGH(                    0x008000),                                   // 抢杠胡
    HZ_UN_QGH(              0x010000),                                   // 有红中不可抢杠胡
    QGH_UN_BP(              0x020000),                                   // 抢杠胡不包赔
    ALL_MISS_IS_ALL_HIT(    0x040000),                                   // 全不中算全中
    ALL_HIT_MUL(            0x080000),                                   // 全中翻倍
    NONE_HZ_ADD_2_NIAO(     0x100000),                                   // 无红中叫2鸟
    ;

    private int value;

    EHZMJPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
