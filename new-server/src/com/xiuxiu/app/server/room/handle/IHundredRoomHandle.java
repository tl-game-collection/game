package com.xiuxiu.app.server.room.handle;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public interface IHundredRoomHandle extends IRoomHandle {
    /**
     * 获取包厢uid
     *
     * @return
     */
    long getBoxUid();

    int getRoomId();

    /**
     * 加入
     *
     * @param player
     */
    ErrorCode onJoin(IRoomPlayer player);

    /**
     * 是否在房间中
     * @param playerUid
     * @return
     */
    boolean hasPlayer(long playerUid);
}
