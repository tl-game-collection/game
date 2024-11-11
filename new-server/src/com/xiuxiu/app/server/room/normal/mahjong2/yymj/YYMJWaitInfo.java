package com.xiuxiu.app.server.room.normal.mahjong2.yymj;

import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;

import java.util.Arrays;

public class YYMJWaitInfo extends MahjongWaitAction.WaitInfo {
    public boolean ting; // 可否报听

    public boolean isTing() {
        return ting;
    }

    public void setTing(boolean ting) {
        this.ting = ting;
    }

    @Override
    public String toString() {
        return "YYMJWaitInfo{" +
                "ting=" + ting +
                ", playerUid=" + playerUid +
                ", index=" + index +
                ", bump=" + bump +
                ", bar=" + bar +
                ", eat=" + eat +
                ", hu=" + hu +
                ", timeout=" + timeout +
                ", op=" + op +
                ", param=" + Arrays.toString(param) +
                '}';
    }
}
