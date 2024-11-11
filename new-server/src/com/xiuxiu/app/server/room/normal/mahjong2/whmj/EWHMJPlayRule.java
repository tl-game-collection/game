package com.xiuxiu.app.server.room.normal.mahjong2.whmj;

public enum EWHMJPlayRule {
    TEN(        0x0001),                                    // 10s
    GOLD(       0x0002),                                    // 连金, 反金
    JFYLF(      0x0004),                                    // 见风原癞翻
    J258F(      0x0008),                                    // 见258翻
    BAOZIF(     0x0010),                                    // 豹子翻
    TUI_GOLD(   0x0020),                                    // 推金
    SMALL_GOLD( 0x0040),                                    // 小金顶
    PI_LAI_BUMP(0x0080),                                    // 皮子和癞子必须摸牌
    RR_DETECTION_IP(        0x0100),                         // 防作弊

    ;

    private int value;

    EWHMJPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
