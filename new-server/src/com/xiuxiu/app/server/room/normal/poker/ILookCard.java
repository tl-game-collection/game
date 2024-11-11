package com.xiuxiu.app.server.room.normal.poker;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;

public interface ILookCard {
    /**
     * 看牌
     * @param player
     * @return
     */
    ErrorCode look(IPlayer player);
}
