package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongLastCard {
    /**
     * 开始最后一张牌操作
     * @param player
     */
    void beginLastCard(IMahjongPlayer player);

    /**
     * 结束最后一张牌操作
     * @param player
     * @param card
     */
    void endLastCard(IMahjongPlayer player, byte card);

    /**
     * 选择
     * @param player
     * @param select
     * @return
     */
    ErrorCode selectLastCard(IPlayer player, boolean select);

    /**
     * 通知开始最后一张票
     * @param player
     */
    void doSendBeginLastCard(IMahjongPlayer player);
}
