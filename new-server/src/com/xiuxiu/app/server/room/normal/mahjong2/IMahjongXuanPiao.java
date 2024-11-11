package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongXuanPiao {
    /**
     * 开始选飘
     */
    void beginXuanPiao();

    /**
     * 结束选飘
     */
    void endXuanPiao();

    /**
     * 选飘
     * @param player
     * @param value
     * @return
     */
    ErrorCode xuanPiao(IPlayer player, int value);

    /**
     * 通知开始选票信息
     * @param player
     */
    void doSendBeginXuanPiao(IMahjongPlayer player);

    /**
     * 通知结束选票
     */
    void doSendEndXuanPiao();

    /**
     * 通知选票信息
     * @param player
     * @param value
     */
    void doSendXuanPiaoInfo(IMahjongPlayer player, int value);
}
