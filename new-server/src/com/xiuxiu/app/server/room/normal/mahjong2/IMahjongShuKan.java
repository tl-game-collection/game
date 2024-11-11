package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;

public interface IMahjongShuKan {
    /**
     * 开始数坎
     */
    void beginShuKan();

    /**
     * 结束数坎
     */
    void endShuKan();

    /**
     * 数坎
     * @param player
     * @param value
     * @return
     */
    ErrorCode shuKan(IPlayer player, int value);
}
