package com.xiuxiu.app.server.room.player.helper;

import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public class HundredRoomPlayerHelper extends AbstractArenaRoomPlayerHelper {

    public HundredRoomPlayerHelper(IRoomPlayer roomPlayer) {
        super(roomPlayer);
    }

    @Override
    public void record(int score, long recordUid, long now) {

    }

    @Override
    public void init() {

    }

    @Override
    public int getCurBureau() {
        return 0;
    }

}
