package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.algorithm.poker.PokerHandCard;
import com.xiuxiu.app.server.room.Score;

import java.util.LinkedList;
import java.util.List;

public class PaiGowPlayer extends PokerPlayer {
    protected boolean isOpenCard = false;
    protected List<Byte> defaultListCard = new LinkedList<>();
    protected int[] defaultCardType = new int[2];

    protected List<Byte> listCard = new LinkedList<>();
    protected int[] cardType = new int[2];

    /**
     * 构造函数
     * @param gameType 游戏类型
     * @param roomUid
     * @param roomId 房间号
     */
    public PaiGowPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
        this.handCard = new PokerHandCard();
    }

    public List<Byte> getOpenCards() {
        if (this.listCard.size() > 0) {
            return this.listCard;
        }
        return this.defaultListCard;
    }

    public int[] getOpenCardType() {
        if (this.listCard.size() > 0) {
            return this.cardType;
        }
        return this.defaultCardType;
    }

    public List<Byte> getDefaultListCard() {
        return this.defaultListCard;
    }

    public int[] getDefaultCardType() {
        return this.defaultCardType;
    }

    public List<Byte> getListCard() {
        return listCard;
    }

    public int[] getCardType() {
        return cardType;
    }

    public void addDefaultListCard(List<Byte> card1, List<Byte> card2) {
        this.defaultListCard.clear();
        this.defaultListCard.addAll(card1);
        if (null != card2) {
            this.defaultListCard.addAll(card2);
        }
    }

    public void addDefaultCardType(int type1, int type2) {
        this.defaultCardType[0] = type1;
        this.defaultCardType[1] = type2;
    }

    public void addListCard(List<Byte> card1, List<Byte> card2) {
        this.listCard.clear();
        this.listCard.addAll(card1);
        if (null != card2) {
            this.listCard.addAll(card2);
        }
        this.isOpenCard = true;
    }

    public void addCardType(int type1, int type2) {
        this.cardType[0] = type1;
        this.cardType[1] = type2;
    }

    public void setMaxCardType(int cardType) {
        // 越小 牌型就越大；
        this.minScore(Score.ACC_POKER_MAX_CARD_TYPE, cardType, true);
    }

    public void setMaxScore(int score) {
        this.maxScore(Score.ACC_MAX_SCORE, score, true);
    }

    public boolean isEquipHandCard(List<Byte> card1, List<Byte> card2) {
        List<Byte> cards = new LinkedList<>(card1);
        if (null != card2) {
            cards.addAll(card2);
        }
        if (cards.size() != this.getHandCard().size()) {
            return false;
        }
        List<Byte> handCards = new LinkedList<>(this.getHandCard());
        while (!cards.isEmpty()) {
            if (!handCards.remove(cards.remove(0))) {
                return false;
            }
        }
        return true;
    }

    public boolean isOpenCard() {
        return isOpenCard;
    }

    @Override
    public void clear() {
        super.clear();
        this.listCard.clear();
        this.defaultListCard.clear();
        this.isOpenCard = false;
        for (int i = 0; i < cardType.length; ++i) {
            this.cardType[i] = 0;
            this.defaultCardType[i] = 0;
        }
    }
}
