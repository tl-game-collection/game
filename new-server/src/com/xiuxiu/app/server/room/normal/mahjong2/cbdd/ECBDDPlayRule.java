package com.xiuxiu.app.server.room.normal.mahjong2.cbdd;

public enum ECBDDPlayRule {
    TEN(            0x0001),                                    // 10s
    SEVEN(          0x0002),                                    // 胡7对
    BANKERMUL(      0x0004),                                    // 庄家+1
    ONLYZIMO(       0x0008),                                    // 可接跑
    HONGZHOU(       0x0010),                                    // 红中
    ALLOWEAT(       0x0020),                                    // 是否可以吃牌
    FENGCARD(       0x0040),                                    // 有东、南、西、北、发、白

    ;

    private int value;

    ECBDDPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
