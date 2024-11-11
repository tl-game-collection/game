package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.player.IPlayer;

import java.util.ArrayList;
import java.util.List;

public class FZMJMahjongPlayer extends MahjongPlayer {
    public List<Byte> huaCard = new ArrayList<>();  // 花牌
    public int huaNum = 0;
    public int bankCount=0;

    public FZMJMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    public void addHuaCard(byte huaCard){
        this.huaCard.add(huaCard);
    }

    public List<Byte> getHuaCard() {
        return huaCard;
    }

    public void setHuaCard(List<Byte> huaCard) {
        this.huaCard = huaCard;
    }

    public int getHuaNum() {
        return huaNum;
    }

    public void setHuaNum(int huaNum) {
        this.huaNum = huaNum;
    }
    public int getBankCount() {
        return bankCount;
    }

    public void setBankCount(int bankCount) {
        this.bankCount = bankCount;
    }

    public boolean hasLaiZiCard(byte cards) {
        for(byte c:deskCard){
            if(c==cards)return true;
        }

        return false;
    }
    @Override
    public void clear() {
        super.clear();
        this.huaCard.clear();
        this.huaNum = 0;
    }
}
