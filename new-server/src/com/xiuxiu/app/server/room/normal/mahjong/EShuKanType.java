package com.xiuxiu.app.server.room.normal.mahjong;

public enum EShuKanType {
    WILL(   0x00000001),
    FIRST_POINT(  0x00000002),
    HU(     0x00000004),
    ZDB(    0x00000008),
    POINT(0x00000010),
    ;

    private int value;

    EShuKanType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
