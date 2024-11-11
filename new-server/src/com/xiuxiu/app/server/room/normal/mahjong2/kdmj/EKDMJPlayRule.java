package com.xiuxiu.app.server.room.normal.mahjong2.kdmj;

public enum EKDMJPlayRule {
    TEN(0x000001),              // 十秒
    YHBH(0x000002),             // 有胡必胡
    JZHZNZK(0x000004),          // 见字胡只能自扣
    JZH60F(0x000008),           // 见字胡60分
    BHBG(0x000010),             // 包胡包杠
    GHZNZK(0x000020),           // 过胡只能自扣
    RR_DETECTION_IP(0x000040),  // IP检测开关
    FZZ(0x000080),
    ;

    private int value;

    EKDMJPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
