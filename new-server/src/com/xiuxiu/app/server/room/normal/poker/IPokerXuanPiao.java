package com.xiuxiu.app.server.room.normal.poker;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;

/**
 * 定义扑克选飘接口
 * 
 * @author Administrator
 *
 */
public interface IPokerXuanPiao {

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
     * 
     * @param player
     * @param value
     * @return
     */
    ErrorCode xuanPiao(IPlayer player, int value);

}
