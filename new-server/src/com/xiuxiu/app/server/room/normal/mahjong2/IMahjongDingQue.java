package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongDingQue {
    /**
     * 开始定缺
     */
    void beginDingQue();

    /**
     * 结束定缺
     */
    void endDingQue();

    /**
     * 定缺
     * @param player
     * @param color
     * @return
     */
    ErrorCode dingQue(IPlayer player, int color);

    /**
     * 通知开始定缺信息
     */
    void doSendBeginDingQue();

    /**
     * 通知定缺信息
     * @param player
     */
    void doSendBeginDingQue(IMahjongPlayer player);

    /**
     * 通知结束定缺
     */
    void doSendEndDingQue();

    /**
     * 通知定缺信息
     * @param player
     * @param color
     */
    void doSendDingQueInfo(IMahjongPlayer player, int color);
}
