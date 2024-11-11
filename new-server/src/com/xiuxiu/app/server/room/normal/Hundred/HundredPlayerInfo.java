package com.xiuxiu.app.server.room.normal.Hundred;

import com.xiuxiu.algorithm.poker.EPokerCardType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HundredPlayerInfo {
    protected int index;

    protected HashSet<Long> allRebUid = new HashSet<>();
    protected HashMap<Long, Integer>[] allReb = new HashMap[EHundredArenaRebType.values().length];
    protected int[] allRebByType = new int[EHundredArenaRebType.values().length];
    protected HashMap<Long, Integer>[] allWinValue = new HashMap[EHundredArenaRebType.values().length];
    protected HashMap<Long, Integer>[] allLostValue = new HashMap[EHundredArenaRebType.values().length];
    protected boolean[] winByType = new boolean[EHundredArenaRebType.values().length];

    protected List<Byte> realCards = new ArrayList<>();
    protected List<Byte> cards = new ArrayList<>();
    protected List<Byte> resultCards = new ArrayList<>();
    protected EPokerCardType cardType = EPokerCardType.NONE;
    protected double cardValue = 0;
    protected boolean win;

    protected long useColorValue = -1;
    protected long unColorValue = -1;

    public HundredPlayerInfo(int index) {
        this.index = index;
        for (int i = 0, len = EHundredArenaRebType.values().length; i < len; ++i) {
            this.allReb[i] = new HashMap<>();
            this.allWinValue[i] = new HashMap<>();
            this.allLostValue[i] = new HashMap<>();
        }
    }

    public void reb(EHundredArenaRebType type, long playerUid, int value) {
        int oldValue = this.allReb[type.ordinal()].getOrDefault(playerUid, 0) + value;
        this.allReb[type.ordinal()].put(playerUid, oldValue);
        this.allRebUid.add(playerUid);

        this.allRebByType[type.ordinal()] += value;
    }

    public boolean setWinOrLostValue(EHundredArenaRebType type, long playerUid, int value) {
        if (this.winByType[type.ordinal()]) {
            this.allWinValue[type.ordinal()].put(playerUid, value);
            return true;
        } else {
            this.allLostValue[type.ordinal()].put(playerUid, value);
            return false;
        }
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public void setWin(EHundredArenaRebType type, boolean win) {
        this.winByType[type.ordinal()] = win;
    }

    public boolean isWin(EHundredArenaRebType type) {
        return this.winByType[type.ordinal()];
    }

    public boolean isWin() {
        return this.win;
    }

    public int getRebValue(EHundredArenaRebType type, long playerUid) {
        return this.allReb[type.ordinal()].getOrDefault(playerUid, 0);
    }

    public int getAllRebValueByType(EHundredArenaRebType type) {
        return this.allRebByType[type.ordinal()];
    }

    public int getResultValue(EHundredArenaRebType type, long playerUid) {
        if (this.winByType[type.ordinal()]) {
            return this.allWinValue[type.ordinal()].getOrDefault(playerUid, 0);
        } else {
            return this.allLostValue[type.ordinal()].getOrDefault(playerUid, 0);
        }
    }
    public int getAllResultValue(EHundredArenaRebType type) {
        int value = 0;
        if (this.winByType[type.ordinal()]) {
            for (int v : this.allWinValue[type.ordinal()].values()) {
                value += v;
            }

        } else {
            for (int v : this.allLostValue[type.ordinal()].values()) {
                value += v;
            }
        }
        return value;
    }

    public HashSet<Long> getAllRebUid() {
        return allRebUid;
    }

    public void addCard(byte card) {
        this.cards.add(card);
        this.realCards.add(card);
    }

    public List<Byte> getCards() {
        return this.cards;
    }

    public List<Byte> getRealCards() {
        return realCards;
    }

    public List<Byte> getResultCards() {
        return resultCards;
    }

    public EPokerCardType getCardType() {
        return cardType;
    }

    public void setCardType(EPokerCardType cardType) {
        this.cardType = cardType;
    }

    public double getCardValue() {
        return cardValue;
    }

    public void setCardValue(double cardValue) {
        this.cardValue = cardValue;
    }

    public int getIndex() {
        return index;
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

    public void clear() {
        for (int i = 0, len = EHundredArenaRebType.values().length; i < len; ++i) {
            this.allReb[i].clear();
            this.allWinValue[i].clear();
            this.allLostValue[i].clear();
            this.winByType[i] = false;
            this.allRebByType[i] = 0;
        }
        this.allRebUid.clear();
        this.realCards.clear();
        this.cards.clear();
        this.resultCards.clear();
        this.cardType = EPokerCardType.NONE;
        this.cardValue = 0;
        this.win = false;
    }
}
