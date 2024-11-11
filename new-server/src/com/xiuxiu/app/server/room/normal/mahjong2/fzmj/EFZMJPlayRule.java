package com.xiuxiu.app.server.room.normal.mahjong2.fzmj;

public enum EFZMJPlayRule {
    TEN(        0x000001),                           // 10s
    QHYS(       0x000002),                           // 胡7对
    HUA_PAI(    0x000004),                           // 无中翻倍
    JIN_LONG(   0x000008),                           // 庄家+1
    ;

    private int value;

    EFZMJPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
