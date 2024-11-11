package com.xiuxiu.app.server.room.player.helper;

import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public abstract class AbstractRoomPlayerHelper implements IRoomPlayerHelper {

    protected IRoomPlayer roomPlayer;

    public AbstractRoomPlayerHelper(IRoomPlayer roomPlayer) {
        this.roomPlayer = roomPlayer;
    }

    @Override
    public int getScore() {
        return roomPlayer.getScore(Score.ACC_TOTAL_SCORE, true);
    }
}
