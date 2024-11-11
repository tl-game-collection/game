package com.xiuxiu.app.server.room.normal.mahjong.kwx;

import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.normal.RoomInfo;

@GameInfo(gameType = GameType.GAME_TYPE_KWX, gameSubType = 5)
public class YiChengKWXMahjongRoom extends KWXMahjongRoom {
    public YiChengKWXMahjongRoom(RoomInfo info) {
        super(info);
    }

    @Override
    protected boolean isBuyOneHorse() {
        return true;
    }

    @Override
    protected boolean isBuySixHorse() {
        return false;
    }

    @Override
    protected boolean isBuyOneGiveOneHorse() {
        return false;
    }
}
