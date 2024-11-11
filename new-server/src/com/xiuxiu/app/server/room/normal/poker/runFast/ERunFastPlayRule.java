package com.xiuxiu.app.server.room.normal.poker.runFast;

public enum ERunFastPlayRule {
    BOMB_UNDETACHABLE(      0x00001),                    // 炸弹不能拆
    FOUR_THREE(             0x00002),                    // 四代三
    HEART_TEN_FIRED_BIRD(   0x00004),                    // 红桃十炸鸟
    EIGHT_PAIR(             0x00008),                    // 8对
    FIG(                    0x00010),                   // 无花果
    AAA_BOMB(               0x00020),                   // AAA是炸弹
    NOT_CARD_NUM(           0x00040),                   // 不显示剩余牌数数量
    RR_DETECTION_IP(        0x00080),                   // 防作弊
    PRIMULA(                0x00100),                   // 叫春
    ;

    private int value;

    ERunFastPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
