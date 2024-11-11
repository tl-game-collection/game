package com.xiuxiu.app.server.room.player.mahjong2;

public class KDMJMahjongPlayer extends MahjongPlayer {
    private int haoHua = 0; // 豪华数量

    public KDMJMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    public int getHao() {
        return haoHua;
    }

    public void setHao(int haoHua) {
        this.haoHua = haoHua;
    }

    @Override
    public void clear() {
        super.clear();
        this.haoHua = 0;
    }
}
