package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.algorithm.poker.PokerHandCard;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.server.room.normal.RoomPlayer;
import com.xiuxiu.app.server.room.player.IPokerPlayer;

import java.util.LinkedList;
import java.util.List;

public abstract class PokerPlayer extends RoomPlayer implements IPokerPlayer {
    protected PokerHandCard handCard;
    protected List<Byte> deskCard = new LinkedList<>();
    /**
     * 选择飘
     */
    protected int piaoScore = 0;
    
    public PokerPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public void initHandCard() {
        this.handCard.init();
    }

    @Override
    public void addHandCard(byte card) {
        this.handCard.addHandCard(card);
    }

    @Override
    public boolean setHandCard(byte card, byte newCard) {
        return this.handCard.setHandCard(card, newCard);
    }

    @Override
    public void takeCard(List<Byte> cards) {
        for (int i = 0, len = cards.size(); i < len; ++i) {
            this.handCard.delHandCard(cards.get(i));
            this.deskCard.add(cards.get(i));
        }
    }

    @Override
    public List<Byte> getHandCard() {
        return this.handCard.getHandCard();
    }

    @Override
    public byte[] getHandCardCnt() {
        return this.handCard.getHandCardCnt();
    }

    @Override
    public List<Byte> getDeskCard() {
        return this.deskCard;
    }

    @Override
    public boolean verifyCard(List<Byte> cards) {
        return this.handCard.verifyCard(cards);
    }

    @Override
    public boolean hasHandCard() {
        return !this.handCard.getHandCard().isEmpty();
    }

    @Override
    public boolean hasBomb() {
        return this.handCard.hasBomb();
    }

    @Override
    public boolean isBomb(byte card) {
        return this.handCard.isBomb(card);
    }

    @Override
    public int getTakeCnt() {
        return this.handCard.getTakeCnt();
    }

    @Override
    public boolean isCurMaxCard(byte card) {
        byte lastCard = PokerUtil.getCardValue(this.handCard.getHandCard().get(this.handCard.getHandCard().size() - 1));
        byte cardValue = PokerUtil.getCardValue(card);
        return cardValue >= lastCard;
    }

    @Override
    public boolean hasCardCnt(byte card, int cnt) {
        card = PokerUtil.getCardValue(card);
        if (card < PokerUtil._3 || card > PokerUtil._2) {
            return false;
        }
        return this.handCard.getHandCardCnt()[card] >= cnt;
    }

    @Override
    public void clear() {
        super.clear();
        this.handCard.clear();
        this.deskCard.clear();
    }
    
    @Override
    public int getPiaoScore() {
        return piaoScore;
    }

    @Override
    public void setPiaoScore(int piaoScore) {
        this.piaoScore = piaoScore;
    }
}
