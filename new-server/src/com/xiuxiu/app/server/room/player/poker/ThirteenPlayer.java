package com.xiuxiu.app.server.room.player.poker;

import com.xiuxiu.algorithm.poker.CardModel;
import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerHandCard;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfThirteenSortCardInfo;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThirteenPlayer extends PokerPlayer {
    /**
     * 怪物牌型，无为0
     */
    protected EPokerCardType monsterType = EPokerCardType.NONE;

    /**
     * 头墩牌类型
     */
    protected EPokerCardType headType = EPokerCardType.NONE;
    /**
     * 中墩牌类型
     */
    protected EPokerCardType mediumType = EPokerCardType.NONE;
    /**
     * 尾墩牌类型
     */
    protected EPokerCardType tailType = EPokerCardType.NONE;

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

    /**
     * 推荐牌型
     */
    protected List<CardModel> cardModelList = new ArrayList<>();

    /**
     * 推荐牌型
     */
    protected List<PCLIPokerNtfThirteenSortCardInfo.cardType> cardTypeList = new ArrayList<>();

    /**
     * 打枪列表
     */
    protected HashMap<Long, Integer> shootPlayerList = new HashMap<>();


    public ThirteenPlayer(int gameType,long roomUid, int roomId) {
        super(gameType,roomUid, roomId);
        this.handCard = new PokerHandCard();
    }

    public void setMonsterType(EPokerCardType type) { this.monsterType = type; }

    public EPokerCardType getMonsterType() { return this.monsterType; }

    public void setHeadType(EPokerCardType type) {
        this.headType = type;
    }

    public EPokerCardType getHeadType() {
        return this.headType;
    }

    public void setMediumType(EPokerCardType type) {
        this.mediumType = type;
    }

    public EPokerCardType getMediumType() {
        return this.mediumType;
    }

    public void setTailType(EPokerCardType type) {
        this.tailType = type;
    }

    public EPokerCardType getTailType() {
        return this.tailType;
    }

    public void addHeadCard(List<Byte> cards) { this.headCard.addAll(cards); }

    public List<Byte> getHeadCard() {
        return this.headCard;
    }

    public void addMediumCard(List<Byte> cards) {
        this.mediumCard.addAll(cards);
    }

    public List<Byte> getMediumCard(){
        return this.mediumCard;
    }

    public void addTailCard(List<Byte> cards) {
        this.tailCard.addAll(cards);
    }

    public List<Byte> getTailCard() {
        return this.tailCard;
    }

    public HashMap<Long, Integer> getShootPlayerList() {
        return shootPlayerList;
    }

    public void setShootPlayerList(long playerId,int score) {
        this.shootPlayerList.put(playerId,score);
    }

    public List<CardModel> getCardModelList() { return cardModelList; }

    public void addCardModelList(CardModel cardModelList) { this.cardModelList.add(cardModelList); }

    public void addCardType(PCLIPokerNtfThirteenSortCardInfo.cardType cardType) { this.cardTypeList.add(cardType); }

    public List<PCLIPokerNtfThirteenSortCardInfo.cardType> getCardTypeList() {
        return this.cardTypeList;
    }

    @Override
    public void clear() {
        super.clear();
        this.headCard.clear();
        this.mediumCard.clear();
        this.tailCard.clear();
        this.cardTypeList.clear();
        this.cardModelList.clear();
        this.shootPlayerList.clear();

        this.monsterType = EPokerCardType.NONE;                         // 怪物牌型，无为0
        this.headType = EPokerCardType.NONE;                            // 头墩牌类型
        this.mediumType = EPokerCardType.NONE;                          // 中墩牌类型
        this.tailType = EPokerCardType.NONE;                            // 尾墩牌类型
    }
}
