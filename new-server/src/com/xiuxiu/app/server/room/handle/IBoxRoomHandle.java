package com.xiuxiu.app.server.room.handle;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.core.queue.SupportAsynchronous;

public interface IBoxRoomHandle extends IRoomHandle, SupportAsynchronous {

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
    ErrorCode join(IRoomPlayer player);

    //游戏中
    boolean hasPlayed(long playerUid);

    /**
     * 获取包厢类型
     * @return
     */
    EBoxType getBoxType();

    ErrorCode sitDown(IPlayer player, int index);

    /**
     * 牛牛金花获取剩余准备时间
     * @return
     */
    long getReadyTime();
}
