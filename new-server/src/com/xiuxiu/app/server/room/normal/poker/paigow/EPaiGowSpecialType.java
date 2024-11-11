package com.xiuxiu.app.server.room.normal.poker.paigow;

public enum EPaiGowSpecialType {
    ZHA_DAN(            0x01), // 炸弹
    GUI_ZI(             0x02), // 鬼子
    TIAN_WANG_JIU(      0x04), // 天王九
    DI_JIU_NIANG_NIANG( 0x08), // 地九娘娘

    BANKER_FIRST_ALLIN( 0x00000001), // 抢庄后头把下满
    TICK_ARENA(         0x00000002), //切锅或者郭底输完后，所有玩家都被踢到竞技场中

    ;

    private int value;

    EPaiGowSpecialType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
