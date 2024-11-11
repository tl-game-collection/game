package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.player.IPlayer;

public class HZMJMahjongPlayer extends MahjongPlayer implements IHZMJMahjongPlayer {
    private int piao = 0;
    private boolean startHu = false;

    public HZMJMahjongPlayer(int gameType, long roomUid, int roomId) {
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

    @Override
    public void setHu(boolean hu) {
        this.startHu = hu;
    }

    @Override
    public boolean getHu() {
        return this.startHu;
    }
    @Override
    public void clear() {
        super.clear();
       this.piao=0;
       this.startHu=false;
    }
}
