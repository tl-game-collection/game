package com.xiuxiu.app.server.room.normal.mahjong.kwx;


import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.normal.RoomInfo;

@GameInfo(gameType = GameType.GAME_TYPE_KWX, gameSubType = 4)
public class ShuiZhouKWXMahjongRoom extends KWXMahjongRoom {
    public ShuiZhouKWXMahjongRoom(RoomInfo info) {
        super(info);
    }

    @Override
    protected boolean isFenSanDui() {
        return true;
    }
}
