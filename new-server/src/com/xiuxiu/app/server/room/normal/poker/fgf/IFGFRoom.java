package com.xiuxiu.app.server.room.normal.poker.fgf;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;

public interface IFGFRoom extends IPokerRoom {
    /**
     * 加注
     * @param player
     * @param value
     * @return
     */
    ErrorCode addNote(IPlayer player, int value, int isFillUp);

    /**
     * 跟注
     * @param player
     * @return
     */
    ErrorCode followNote(IPlayer player);

    /**
     * 比牌
     * @param player
     * @param otherPlayerUid
     * @return
     */
    ErrorCode compare(IPlayer player, long otherPlayerUid);

    /**
     * 下一个
     */
    void next();
}
