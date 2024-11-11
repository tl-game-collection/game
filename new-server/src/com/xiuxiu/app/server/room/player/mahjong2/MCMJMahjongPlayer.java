package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.player.IPlayer;

public class MCMJMahjongPlayer extends MahjongPlayer {
    private int topType = 0;
    private int hao = 0; // 豪华数量

    public MCMJMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    public void setTopType(int value) {
        this.topType = value;
    }

    public int getTopType() {
        return this.topType;
    }

    public int getHao() {
        return hao;
    }

    public void setHao(int hao) {
        this.hao = hao;
    }
}
