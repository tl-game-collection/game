package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.algorithm.poker.PokerHandCard;

import java.util.ArrayList;
import java.util.List;

public class ArchPlayer extends PokerPlayer {
    private List<Byte> dealtCards = new ArrayList<>(); // 一局中所有拿到的牌
    private int cardScore; // 牌分
    private ArchPlayer partner; // 盟友



    public ArchPlayer(int gameType,long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
        this.handCard = new PokerHandCard();
    }

    public void reset() {
        this.dealtCards.clear();
        this.cardScore = 0;
        this.partner = null;
    }

    public List<Byte> getDealtCards() {
        return dealtCards;
    }

    public void resetDealtCards(List<Byte> cards) {
        this.dealtCards.addAll(cards);
    }

    public int getCardScore() {
        return cardScore;
    }

    public void addCardScore(int score) {
        cardScore += score;
    }

    public void setCardScore(int cardScore) {
        this.cardScore = cardScore;
    }

    public ArchPlayer getPartner() {
        return partner;
    }

    public void setPartner(ArchPlayer partner) {
        this.partner = partner;
    }

    @Override
    public void clear() {
        super.clear();
        this.dealtCards.clear();
    }
}
