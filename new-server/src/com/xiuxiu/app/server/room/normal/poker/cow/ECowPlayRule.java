package com.xiuxiu.app.server.room.normal.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/6 17:20
 * @comment:
 */
public enum ECowPlayRule {
    TONG_HUA_SHUN(                  0x00000001),                    // 同花顺
    YI_TIAO_LONG(                   0x00000002),                    // 一条龙
    ZHA_DAN_NIU(                    0x00000004),                    // 炸弹牛
    WU_XIAO_NIU(                    0x00000008),                    // 无小牛
    HU_LU_NIU(                      0x00000010),                    // 葫芦牛
    JIN_NIU(                        0x00000020),                    // 金牛
    TONG_HUA_NIU(                   0x00000040),                    // 同花牛
    YIN_NIU(                        0x00000080),                    // 银牛
    SHUN_ZI_NIU(                    0x00000100),                    // 顺子牛
    ZHONG_TU_KE_JIA_RU(             0x00000200),                    // 中途可加入
    BU_QIANG_ZHUANG_BU_KE_TUI_ZHU(  0x00000400),                    // 不抢庄不可推注
    XIA_ZHU_FAN_BEI(                0x00000800),                    // 下注翻倍
    AN_QIANG(                       0x00001000),                    // 暗抢
    KUAI_SHU(                       0x00002000),                    // 快速场
    BU_KE_CUO_PAI(                  0x00004000),                    // 不可搓牌
    WU_HUA_PAI(                     0x00008000),                    // 无花牌
    HOT_OUT_TIP(                    0x00010000),                    // 是否提示下庄
    //RR_DETECTION_IP(                0x00008000),                    // IP限制
    ;

    private int value;

    ECowPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
