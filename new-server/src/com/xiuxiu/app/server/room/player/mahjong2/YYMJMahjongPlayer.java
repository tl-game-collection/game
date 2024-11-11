package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.player.IPlayer;

public class YYMJMahjongPlayer extends MahjongPlayer {
    private int hao; // 豪华数量
    private boolean ting; // 是否报听
    private boolean manualTake;
    private boolean firstHu;//庄家第一张牌是否可以胡
    private boolean flag;
    private boolean isHaiDi;

    public YYMJMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    public int getHao() {
        return hao;
    }

    public void setHao(int hao) {
        this.hao = hao;
    }

    public boolean isTing() {
        return ting;
    }

    public void setTing(boolean ting) {
        this.ting = ting;
    }

    public boolean isFirstHu() {
        return firstHu;
    }

    public void setFirstHu(boolean firstHu) {
        this.firstHu = firstHu;
    }
    public boolean isHaiDi() {
        return isHaiDi;
    }

    public void setIsHaiDi(boolean isHaiDi) {
        this.isHaiDi = isHaiDi;
    }
    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public void setManualTake(boolean manualTake) {
        this.manualTake = manualTake;
    }

    @Override
    public boolean canManualTake() {
        return manualTake;
    }

    @Override
    public void clear() {
        super.clear();
        this.hao = 0;
        this.ting = false;
        this.manualTake = true;
        this.firstHu = false;
        this.flag = false;
        this.isHaiDi=false;
    }
}
