package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.player.IPlayer;

public class CBDDMahjongPlayer extends MahjongPlayer implements ICBDDMahjongPlayer, IXuanZeng {
    protected int zengValue = -1;

    public CBDDMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public void setZeng(int value) {
        this.zengValue = value;
    }

    @Override
    public int getZeng() {
        return this.zengValue;
    }

    @Override
    public void clear() {
        super.clear();
        this.zengValue = -1;
    }
}
