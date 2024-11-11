package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongCSOpenBar {
    /**
     * 开始长沙开杠
     * @param player
     */
    void beginCSOpenBar(IMahjongPlayer player);

    /**
     * 结束长沙开杠
     * @param player
     * @param select
     */
    void endCSOpenCard(IMahjongPlayer player, boolean select);

    /**
     * 选择
     * @param player
     * @param select
     * @return
     */
    ErrorCode selectOpenCard(IPlayer player, boolean select);

    /**
     * 通知开始长沙开杠
     * @param player
     */
    void doSendBeginCSOpenCard(IMahjongPlayer player);
}
