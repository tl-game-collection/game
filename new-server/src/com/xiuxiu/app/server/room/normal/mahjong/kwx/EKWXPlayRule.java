package com.xiuxiu.app.server.room.normal.mahjong.kwx;

public enum EKWXPlayRule {
    KWX(                    0x0001),                // 卡五星
    PPH(                    0x0002),                // 碰碰胡
    GSH(                    0x0004),                // 杠上花
    SK(                     0x0008),                // 数坎
    TEN(                    0x0010),                // 10s
    FULL_CHANNEL(           0x00020),               // 全频道
    CDJ(                    0x00040),               // 查大叫
    SL(                     0x0080),                // 上楼
    BFL(                    0x0100),                // 部分亮
    PQMB(                   0x0200),                // 跑恰模八
    RR_DETECTION_IP(        0x00400),               // 防作弊
    DLDF(                   0x00800),               // 对亮对番
    
    ;

    private int value;

    EKWXPlayRule(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
