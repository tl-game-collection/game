package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongXuanZeng {
    /**
     * 开始选增
     */
    void beginXuanZeng(int type);

    /**
     * 结束选增
     */
    void endXuanZeng();

    /**
     * 选增
     * @param player
     * @param value
     * @return
     */
    ErrorCode xuanZeng(IPlayer player, int value);

    /**
     * 通知开始选增信息
     * @param player
     */
    void doSendBeginXuanZeng(IMahjongPlayer player);

    /**
     * 通知结束选增
     */
    void doSendEndXuanZeng();

    /**
     * 通知选增信息
     * @param player
     * @param value
     */
    void doSendXuanZengInfo(IMahjongPlayer player, int value);
}
