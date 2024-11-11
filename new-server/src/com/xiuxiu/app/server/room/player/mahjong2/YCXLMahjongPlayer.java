package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.player.IPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YCXLMahjongPlayer extends MahjongPlayer implements IYCXLMahjongPlayer, IDingQue {
    protected int queColor = -1;
    protected boolean chaHuaZhu = false;
    protected boolean chaDaJiao = false;
    protected int chaValue = 0;
    protected boolean huanPai = false;
    protected  boolean ting=false;
    protected Set<Byte> allHuCard=new HashSet<Byte>();
    protected List<Long> huanPaiRecord = new ArrayList<>();

    public YCXLMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public int setQue(int color) {
        if (-1 == color) {
            color = MahjongUtil.getColor((byte) this.getMinSameColorCard(1));
        }
        this.queColor = color;
        return this.queColor;
    }

    @Override
    public int getQue() {
        return this.queColor;
    }

    @Override
    public boolean isQueCard(byte card) {
        return this.queColor == MahjongUtil.getColor(card);
    }

    @Override
    public boolean hasQueCard() {
        if (-1 == this.queColor) {
            return false;
        }
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (this.handCard[i] < 1) {
                continue;
            }
            if (this.queColor == MahjongUtil.getColor((byte) i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setChaDaJiao(boolean chaDaJiao) {
        this.chaDaJiao = chaDaJiao;
    }

    @Override
    public boolean isChaDaJiao() {
        return this.chaDaJiao;
    }

    public void setHuanPai(boolean huanPai) {
        this.huanPai = huanPai;
    }

    public boolean isHuanPai() {
        return this.huanPai;
    }
    public void setTing(boolean ting) {
        this.ting = ting;
    }

    public boolean isTing() {
        return this.ting;
    }

    public void setAllHuCard(Set<Byte>allHuCard) {
       this.allHuCard.addAll(allHuCard);
    }

    public Set<Byte> getAllHuCard() {
        return this.allHuCard;
    }

    public List<Long> getHuahuanPaiRecordnpai() {
        return this.huanPaiRecord;
    }

    @Override
    public void setChaHuaZhu(boolean chaHuaZhu) {
        this.chaHuaZhu = chaHuaZhu;
    }

    @Override
    public boolean isChaHuZhu() {
        return this.chaHuaZhu;
    }

    @Override
    public void addChaValue(int value) {
        this.chaValue += value;
    }

    @Override
    public int getChaValue() {
        return this.chaValue;
    }
    public void addHuanPai(long p1, long p2 ,int card1, int card2,int type){
        huanPaiRecord.add(p1);
        huanPaiRecord.add(p2);
        huanPaiRecord.add((long)card1);
        huanPaiRecord.add((long)card2);
        huanPaiRecord.add((long)type);
    }
    @Override
    public void clear() {
        super.clear();
        this.queColor = -1;
        this.chaDaJiao = false;
        this.chaHuaZhu = false;
        this.chaValue = 0;
        this.huanPai = false;
        this.ting=false;
        this.allHuCard.clear();
        huanPaiRecord.clear();
    }
}
