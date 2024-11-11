package com.xiuxiu.app.server.room.normal.poker.thirteen;

public enum EThirteenPlayRule {
    //怪物牌型
    ZHI_ZUN_QING_LONG(      0x00000001),                         // 至尊清龙
    BA_XIAN_GUO_HAI(        0x00000002),                         // 八仙过海
    QI_XING_LIAN_ZHU(       0x00000004),                         // 七星连珠
    SHI_SAN_SHUI(           0x00000008),                         // 十三水

    SHI_ER_HUANG_ZU(        0x00000010),                         // 十二皇族
    SAN_TONG_HUA_SHUN(      0x00000020),                         // 三同花顺
    SAN_TAO_ZHA_DAN(        0x00000040),                         // 三套炸弹
    //COU_YI_SE(              0x00000080),                         // 凑一色

    QUAN_DA(                0x00000080),                         // 全大
    QUAN_XIAO(              0x00000100),                         // 全小
    QUAN_HONG(              0x00000200),                         // 全红
    QUAN_HEI(               0x00000400),                         // 全黑

    ZHONG_YUAN_YI_DIAN_HONG(0x00000800),                         // 中原一点红
    ZHONG_YUAN_YI_DIAN_HEI( 0x00001000),                         // 中原一点黑
    SI_TAO_SAN_TIAO(        0x00002000),                         // 四套三条
    WU_DUI_SAN_TIAO(        0x00004000),                         // 五对三条

    LIU_DUI_BAN(            0x00008000),                         // 六对半
    SAN_TONG_HUA(           0x00010000),                         // 三同花
    SAN_SHUN_ZI(            0x00020000),                         // 三顺子

    ZHONG_TU_KE_JIA_RU(     0x00040000),                         // 中途可加入
    USE_COLOR(              0x00080000),                         // 比花色
    TWO_NO_SHOOT(           0x00100000),                         // 两人不打枪
    STRIKE_NO_SHOOT(        0x00200000),                         // 强袭不打枪

    //普通牌型
    WU_TIAO(                0x00400000),                         // 5条
    TONG_HUA_SHUN(          0x00800000),                         // 同花顺
    SI_TIAO(                0x01000000),                         // 四条
    HU_LU(                  0x02000000),                         // 葫芦
    TONG_HUA(               0x04000000),                         // 同花

    SHUN_ZI(                0x08000000),                         // 顺子
    SAN_TIAO(               0x10000000),                         // 三条
    ER_DUI(                 0x20000000),                         // 2对

    ;

    private int value;

    EThirteenPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
