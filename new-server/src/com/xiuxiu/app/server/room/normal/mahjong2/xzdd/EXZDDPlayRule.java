package com.xiuxiu.app.server.room.normal.mahjong2.xzdd;

public enum EXZDDPlayRule {
    TEN(            0x0001),                                    // 10s
    HUANGPAI(       0x0002),                                    // 换牌
    TDH(            0x0004),                                    // 天地胡
    XJJD(           0x0008),                                    // 玄九将对
    MQZZ(           0x0010),                                    // 门清中张
    QYM(            0x0020),                                    // 缺一门
    WANG(           0x0040),                                    // 万
    DGH(            0x0080),                                    // 点杠花
    ;

    private int value;

    EXZDDPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
