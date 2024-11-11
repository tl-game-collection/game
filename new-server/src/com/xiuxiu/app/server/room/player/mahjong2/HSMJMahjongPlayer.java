package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.player.IPlayer;

public class HSMJMahjongPlayer extends MahjongPlayer {
    private int hao = 0; // 豪华数量
    private boolean isCanOperate = false;         // 是否操作过(打牌、吃、碰、杠、仰)    改为(打完或者仰过牌之后不能仰)
    private int takeCardCnt=0;
    public HSMJMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    public int getHao() {
        return hao;
    }

    public void setHao(int hao) {
        this.hao = hao;
    }

    public void setCanOperate(boolean isCanOperate) {
        this.isCanOperate = isCanOperate;
    }

    public boolean getCanOperate() {
        return isCanOperate;
    }
    public int getTakeCardCnt() {
        return takeCardCnt;
    }

    public void addTakeCardCnt() {
        this.takeCardCnt++;
    }
    @Override
    public void clear() {
        super.clear();
        this.hao = 0;
        this.isCanOperate = false;
        takeCardCnt=0;
    }
}
