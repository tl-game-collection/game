package com.xiuxiu.app.server.room.player.poker.cow;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerHandCard;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/6 17:27
 * @comment:
 */
public class CowPlayer extends PokerPlayer {
    private byte lastCard = -1;                                         // 最后一张牌
    private boolean isLook = false;
    private boolean isPrevWin = false;
    private int prevWinValue = 0;
    private boolean prePushNote;                                        //上局是否是推注；
    private EPokerCardType prevCardType = EPokerCardType.COW_NONE;      //上一把的牌型；
    private List<Byte> resultCard = new ArrayList<>();                  // 结果牌型
    private EPokerCardType curCardType = EPokerCardType.COW_NONE;       // 当前牌型
    private double cardValue;                                           // 牌型值
    private boolean curPushNote;                                        //当前局是否是推注；
    /** 先发牌的信息 */
    private List<Byte> firstShowCard = new ArrayList<>();

    public CowPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
        this.handCard = new PokerHandCard();
        this.setScore(Score.ACC_POKER_MAX_CARD_TYPE, EPokerCardType.COW_NONE.getValue(), true);
    }

    public byte getLastCard() {
        return lastCard;
    }

    public void setLastCard(byte lastCard) {
        this.lastCard = lastCard;
    }

    public boolean isLook() {
        return isLook;
    }

    public void setLook(boolean look) {
        isLook = look;
    }

    public boolean isPrevWin() {
        return isPrevWin;
    }

    public void setPrevWin(boolean prevWin) {
        isPrevWin = prevWin;
    }

    public int getPrevWinValue() {
        return prevWinValue;
    }

    public void setPrevWinValue(int prevWinValue) {
        this.prevWinValue = prevWinValue;
    }

    public boolean isPrePushNote() {
        return prePushNote;
    }

    public void setPrePushNote(boolean prePushNote) {
        this.prePushNote = prePushNote;
    }

    public EPokerCardType getPrevCardType() {
        return prevCardType;
    }

    public void setPrevCardType(EPokerCardType prevCardType) {
        this.prevCardType = prevCardType;
    }

    public List<Byte> getResultCard() {
        if (resultCard == null) {
            resultCard = new ArrayList<>();
        }
        return resultCard;
    }

    public void setResultCard(List<Byte> resultCard) {
        this.resultCard = resultCard;
    }

    public List<Byte> getFirstShowCard() {
        if (firstShowCard == null) {
            firstShowCard = new ArrayList<>();
        }
        return firstShowCard;
    }

    public void setFirstShowCard(List<Byte> firstShowCard) {
        this.firstShowCard = firstShowCard;
    }

    public EPokerCardType getCurCardType() {
        return curCardType;
    }

    public void setCurCardType(EPokerCardType curCardType) {
        this.curCardType = curCardType;
    }

    public double getCardValue() {
        return cardValue;
    }

    public void setCardValue(double cardValue) {
        this.cardValue = cardValue;
    }

    public boolean isCurPushNote() {
        return curPushNote;
    }

    public void setCurPushNote(boolean curPushNote) {
        this.curPushNote = curPushNote;
    }

    public void setMaxCardType() {
        this.maxScore(Score.ACC_POKER_MAX_CARD_TYPE, this.curCardType.getValue(), true);
    }

    public void addRebet(int rebet, boolean isBanker) {
        this.addScore(Score.SCORE, rebet, false);
        this.addScore(Score.ACC_TOTAL_SCORE, rebet, true);
        if (!isBanker) {
            if (rebet < 0) {
                this.addScore(Score.ACC_LOST_CNT, 1, true);
            } else if (rebet > 0) {
                this.addScore(Score.ACC_WIN_CNT, 1, true);
            }
            this.maxScore(Score.ACC_MAX_SCORE, rebet, true);
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.isLook = false;
        this.curCardType = EPokerCardType.COW_NONE;
        this.resultCard.clear();
        this.cardValue = -1;
        this.lastCard = -1;
        this.curPushNote = false;
    }
}
