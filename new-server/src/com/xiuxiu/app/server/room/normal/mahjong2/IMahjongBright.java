package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongBright {
    /**
     * 亮牌
     * @param player
     * @param param
     * @return
     */
    ErrorCode bright(IPlayer player, Object... param);

    /**
     * 执行亮牌
     * @param player
     * @param param
     */
    void onBright(IMahjongPlayer player, Object... param);
}
