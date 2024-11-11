package com.xiuxiu.app.server.score;

import com.xiuxiu.core.ds.ConcurrentHashSet;

import java.util.ArrayList;
import java.util.List;

public class ScoreItemInfo {
    protected long playerUid;
    protected int score;
    protected ConcurrentHashSet<Long> like = new ConcurrentHashSet<>();
    //牌型显示，竞技场用
    protected int cardType;//牌型
    protected int cardTypeExtra; //额外牌型
    protected List<Byte> card = new ArrayList();//手牌
    protected boolean isDiscard = false;//扎金花是否弃牌
    protected boolean isWin = false;//扎金花输赢
    protected boolean isBnaker=false;//是否庄家（牛牛）
    protected long bankerMul;//庄家抢庄倍数（牛牛）
    protected long pushMul;//闲家推注倍数（牛牛）
    protected   byte lastCard = -1;  // 最后一张牌(牛牛)
    protected int monsterType;//怪物牌型（十三水）
    /**
     * 头墩牌
     */
    protected List<Byte> headCard = new ArrayList<>(3);
    /**
     * 中墩牌
     */
    protected List<Byte> mediumCard = new ArrayList<>(5);
    /**
     * 尾墩牌
     */
    protected List<Byte> tailCard = new ArrayList<>(5);
    
    protected int[] cardTypes;

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ConcurrentHashSet<Long> getLike() {
        return like;
    }

    public void setLike(ConcurrentHashSet<Long> like) {
        this.like = like;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public List<Byte> getCard() {
        return card;
    }

    public boolean isDiscard() {
        return isDiscard;
    }

    public void setDiscard(boolean discard) {
        isDiscard = discard;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public boolean isBnaker() {
        return isBnaker;
    }

    public void setBnaker(boolean bnaker) {
        isBnaker = bnaker;
    }

    public long getBankerMul() {
        return bankerMul;
    }

    public void setBankerMul(long bankerMul) {
        this.bankerMul = bankerMul;
    }

    public long getPushMul() {
        return pushMul;
    }

    public void setPushMul(long pushMul) {
        this.pushMul = pushMul;
    }

    public byte getLastCard() {
        return lastCard;
    }

    public void setLastCard(byte lastCard) {
        this.lastCard = lastCard;
    }

    public List<Byte> getHeadCard() {
        return headCard;
    }

    public List<Byte> getMediumCard() {
        return mediumCard;
    }

    public List<Byte> getTailCard() {
        return tailCard;
    }

    public int getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(int monsterType) {
        this.monsterType = monsterType;
    }

    public int[] getCardTypes() {
        return cardTypes;
    }

    public void setCardTypes(int[] cardTypes) {
        this.cardTypes = cardTypes;
    }

    public int getCardTypeExtra() {
        return cardTypeExtra;
    }

    public void setCardTypeExtra(int cardTypeExtra) {
        this.cardTypeExtra = cardTypeExtra;
    }
}
