package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongStartHu {
    /**
     * 开始起手胡
     */
    void beginStartHu();

    /**
     * 结束起手胡
     * @param has
     */
    void endStartHu(boolean has);

    /**
     * 选胡
     * @param player
     * @param select
     * @return
     */
    ErrorCode startHu(IPlayer player, boolean select);

    /**
     * 通知开始起手胡信息
     * @param player
     */
    void doSendBeginStartHu(IMahjongPlayer player, Object msg);

    /**
     * 通知结束起手胡
     */
    void doSendEndStartHu();

    /**
     * 通知起手胡信息
     * @param player
     * @param select
     */
    void doSendStartHuInfo(IMahjongPlayer player, boolean select);
}
