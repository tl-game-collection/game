package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongMiddleHu {
    /**
     * 开始中途胡
     */
    void beginMiddleHu(IMahjongPlayer player);

    /**
     * 结束中途胡手胡
     */
    void endMiddleHu(IMahjongPlayer player);

    /**
     * 通知开始起手胡信息
     * @param msg
     */
    void doSendBeginMiddleHu(Object msg);
}
