package com.xiuxiu.app.server.room.normal.poker;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public interface IDiscard {
    /**
     * 弃牌
     * @param player
     * @return
     */
    ErrorCode discard(IPlayer player);

    /**
     * 弃牌
     * @param player
     * @return
     */
    ErrorCode discard(IRoomPlayer player);
}
