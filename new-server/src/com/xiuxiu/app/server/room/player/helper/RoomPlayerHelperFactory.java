package com.xiuxiu.app.server.room.player.helper;

import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public final class RoomPlayerHelperFactory {

    public static IRoomPlayerHelper createRoomPlayerHelper(IRoomPlayer roomPlayer, int gameType) {
        IRoomPlayerHelper roomPlayerHelper;
        if (GameType.isArenaGame(gameType)) {
            if (GameType.GAME_TYPE_PAIGOW == gameType) {
                roomPlayerHelper = new ArenaPaiGowRoomPlayerHelper(roomPlayer);
            } else if (GameType.GAME_TYPE_HUNDRED_LHD == gameType || GameType.GAME_TYPE_HUNDRED_BACCARAT == gameType) {
                roomPlayerHelper = new HundredRoomPlayerHelper(roomPlayer);
            } else {
                roomPlayerHelper = new ArenaRoomPlayerHelper(roomPlayer);
            }
        } else {
            roomPlayerHelper = new RoomPlayerHelper(roomPlayer);
        }
        roomPlayerHelper.init();
        return roomPlayerHelper;
    }
}
