package com.xiuxiu.app.server.player;

/**
 *  玩家身上的状态
 */
public enum EPlayerDone {
    DONE_GAME(  1),         // 是否完成过一轮游戏
    CONVERSION( 2)          // 是否用房卡兑换过竞技分
    ;
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    EPlayerDone(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EPlayerDone{" +
                "value=" + value +
                '}';
    }
}
