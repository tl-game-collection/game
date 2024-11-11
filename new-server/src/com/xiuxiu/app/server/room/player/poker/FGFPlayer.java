package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerHandCard;

public class FGFPlayer extends PokerPlayer {
    protected EPokerCardType curCardType = EPokerCardType.FGF_NONE;
    protected long useColorValue = -1;
    protected long unColorValue = -1;
    protected boolean isDiscard = false;
    protected boolean isLook = false;
    protected boolean isWin = false;
    protected boolean curLoopComp = false;

    public FGFPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
        this.handCard = new PokerHandCard();
    }

    public EPokerCardType getCurCardType() {
        return curCardType;
    }

    public void setCurCardType(EPokerCardType curCardType) {
        this.curCardType = curCardType;
    }

    public boolean isDiscard() {
        return isDiscard;
    }

    public void setDiscard(boolean discard) {
        isDiscard = discard;
        this.over = true;
    }

    public boolean isLook() {
        return isLook;
    }

    public void setLook(boolean look) {
        isLook = look;
    }

    public long getUnColorValue() {
        return unColorValue;
    }

    public void setUnColorValue(long unColorValue) {
        this.unColorValue = unColorValue;
    }

    public long getUseColorValue() {

        return useColorValue;
    }

    public void setUseColorValue(long useColorValue) {

        this.useColorValue = useColorValue;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
        if (!win) {
            this.over = true;
        }
    }

    public boolean isCurLoopComp() {
        return curLoopComp;
    }

    public void setCurLoopComp(boolean curLoopComp) {
        this.curLoopComp = curLoopComp;
    }

    @Override
    public void clear() {
        super.clear();
        this.curCardType = EPokerCardType.FGF_NONE;
        this.useColorValue = -1;
        this.unColorValue = -1;
        this.isDiscard = false;
        this.isLook = false;
        this.isWin = false;
        this.curLoopComp = false;
    }
}
