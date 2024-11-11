package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerHandCard;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.poker.sg.ESGPokerType;

public class SGPlayer  extends PokerPlayer {
    protected ESGPokerType curCardType = ESGPokerType.NONE;             // 当前牌型
    protected int point;                                                // 牌型点数；

    protected EPokerCardType curCardTypeExtra = EPokerCardType.FGF_NONE; // 額外牌型值 （三公加金花玩法）
    protected long useColorValue = -1;                                   // 金花比花色點數
    protected long unColorValue = -1;                                    // 金花不比花色点数

    protected boolean prePushNote;                                       //上局是否是推注；
    protected boolean curPushNote;                                       //当前局是否是推注；

    protected boolean isPrevWin = false;
    protected int prevWinValue = 0;
    protected ESGPokerType prevCardType = ESGPokerType.NONE;     //上一把的牌型；

    /** 是否看过牌 */
    private boolean isLook = false;



    public SGPlayer(int gameType ,long roomUid, int roomId) {
        super(gameType,roomUid, roomId);
        this.handCard = new PokerHandCard();
        this.setScore(Score.ACC_POKER_MAX_CARD_TYPE, ESGPokerType.NONE.getValue(), true);
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

    public boolean isPrevWin() {
        return isPrevWin;
    }

    public void setIsPrevWin(boolean value){
        this.isPrevWin = value;
    }

    public int getPrevWinValue() {
        return prevWinValue;
    }

    public void setPrevWinValue(int value){
        this.prevWinValue = value;
    }

    public void setMaxCardType(ESGPokerType cardType, int point) {
        this.curCardType = cardType;
        this.point = point;
        this.maxScore(Score.ACC_POKER_MAX_CARD_TYPE, this.curCardType.getValue(), true);
    }

    public ESGPokerType getCurCardType() {
        return this.curCardType;
    }

    public boolean getPrePutNote() {
        return prePushNote;
    }

    public void setPrePutNote(boolean value) {
        this.prePushNote = value;
    }

    public boolean getCurPutNote() {
        return curPushNote;
    }

    public void setCurPutNote(boolean value) {
        this.curPushNote = value;
    }

    public void setCurCardType(ESGPokerType curCardType) {
        this.curCardType = curCardType;
    }

    public ESGPokerType getPrevCardType() {
        return prevCardType;
    }

    public int getCardValue(){
        return PokerUtil.getCardValue((byte) (this.getHandCard().get(0) + 2));
    }

    public void setPrevCardType(ESGPokerType prevCardType) {
        this.prevCardType = prevCardType;
    }

    public boolean isLook() {
        return isLook;
    }

    public void setLook(boolean look) {
        isLook = look;
    }

    public EPokerCardType getCurCardTypeExtra() {
        return curCardTypeExtra;
    }

    public void setCurCardTypeExtra(EPokerCardType curCardTypeExtra) {
        this.curCardTypeExtra = curCardTypeExtra;
    }

    public long getUseColorValue() {
        return useColorValue;
    }

    public void setUseColorValue(long useColorValue) {
        this.useColorValue = useColorValue;
    }

    public long getUnColorValue() {
        return unColorValue;
    }

    public void setUnColorValue(long unColorValue) {
        this.unColorValue = unColorValue;
    }

    @Override
    public void clear() {
        super.clear();
        this.curCardType = ESGPokerType.NONE;
        this.point = -1;
    }
}
