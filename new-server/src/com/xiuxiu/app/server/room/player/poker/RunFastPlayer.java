package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.algorithm.poker.RunFastPokerHandCard;
import com.xiuxiu.app.server.room.normal.poker.runFast.ERunFastCardType;

public class RunFastPlayer extends PokerPlayer {
    protected ERunFastCardType cardType = ERunFastCardType.NORMAL;

    public RunFastPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
        this.handCard = new RunFastPokerHandCard();
    }

    public boolean isFig() {
        return ((RunFastPokerHandCard) this.handCard).isFig();
    }

    public boolean isEightPair() {
        return ((RunFastPokerHandCard) this.handCard).isEightPair();
    }

    public boolean hasRedPeachTen() {
        return ((RunFastPokerHandCard) this.handCard).hasRedPeachTen();
    }

    public ERunFastCardType getCardType() {
        return cardType;
    }

    public void setCardType(ERunFastCardType cardType) {
        this.cardType = cardType;
    }

    @Override
    public void clear() {
        super.clear();
        this.cardType = ERunFastCardType.NORMAL;
    }
}
