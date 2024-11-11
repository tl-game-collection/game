package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.player.IPlayer;

public class ZZMJMahjongPlayer extends MahjongPlayer implements IZZMJMahjongPlayer {
    private int piao = 0;

    public ZZMJMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public void setPiao(int value) {
        this.piao = value;
    }

    @Override
    public int getPiao() {
        return this.piao;
    }
}
