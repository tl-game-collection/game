package com.xiuxiu.app.server.room.handle.impl.hundred;

import java.util.ArrayList;
import java.util.List;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredArenaNtfOpenCardInfoByBaccarat;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfDeskInfoByLhd;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.CardLibraryManager;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredArenaRebType;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredGameState;
import com.xiuxiu.app.server.room.normal.Hundred.HundredPlayerInfo;
import com.xiuxiu.app.server.room.normal.Hundred.IHundredBanker;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.ShuffleUtil;

/**
 * 百家乐处理器
 */
public class BaccaratHundredRoomHandle extends AbstractHundredRoomHandle {

    public BaccaratHundredRoomHandle(IRoom room, Box box) {
        super(room, box);
        this.rebAllRecords = new int[] {0, 0, 0, 0, 0};
    }

    @Override
    public void init() {
        super.init();
        this.rebTime = 1000 * this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_REB_TIME, 15);
        this.readyBeginTime = 1000 * this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_READY_TIME, 0);
        this.openCardTime = 1000 * this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_OPEN_CARD_TEIM, 15);
        this.overTime = 0;
        this.maxBureau = 10;
        this.maxMultiple = 20;
    }

    @Override
    protected int needBankerArenaValue(int index, int value, EHundredArenaRebType type) {
        // （（庄-闲-和，闲-庄-和，和*8）三个里面取最大的，加上庄对*11，闲对*11））
        int bankerValue =
            1 == index ? this.allReb[1].getAllRebValueByType(type) + value : this.allReb[1].getAllRebValueByType(type);
        int playerValue =
            5 == index ? this.allReb[5].getAllRebValueByType(type) + value : this.allReb[5].getAllRebValueByType(type);
        int tieValue =
            3 == index ? this.allReb[3].getAllRebValueByType(type) + value : this.allReb[3].getAllRebValueByType(type);
        int playerDouble =
            2 == index ? this.allReb[2].getAllRebValueByType(type) + value : this.allReb[2].getAllRebValueByType(type);
        int bankerDouble =
            4 == index ? this.allReb[4].getAllRebValueByType(type) + value : this.allReb[4].getAllRebValueByType(type);
        return Math.max(Math.max(bankerValue - playerValue - tieValue, playerValue - bankerValue - tieValue),
            tieValue * 8) + playerDouble * 11 + bankerDouble * 11;
    }

    @Override
    public EHundredGameState getState() {
        return this.gameState.get();
    }

    @Override
    public void setWinIndex(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getWinIndex() {
        return this.winIndex;
    }

    @Override
    public List<Integer> getWinIndexList() {
        List<Integer> list = new ArrayList<>();
        list.add(this.winIndex); //1.庄 5.闲
        return list;
    }

    @Override
    protected boolean checkBanker(IHundredBanker bankerPlayer) {
        return Boolean.TRUE;
    }

    @Override
    protected List<Byte> getCards(HundredPlayerInfo playerInfo) {
        return playerInfo.getCards();
    }

    @Override
    protected int getWinOrLostValue(EHundredArenaRebType type, HundredPlayerInfo banker, HundredPlayerInfo player,
        boolean isWin, int value) {
        if (EHundredArenaRebType.PLAYER_WIN == type) {
            if (isWin) {
                return value * this.getMul(player.getIndex());
            } else {
                if (3 == this.winIndex && (1 == player.getIndex() || 5 == player.getIndex())) {
                    return 0;
                }
                return -value;
            }
        }
        return 0;
    }

    @Override
    protected void doOpenCard() {
        PCLIHundredArenaNtfOpenCardInfoByBaccarat info = new PCLIHundredArenaNtfOpenCardInfoByBaccarat();
        info.boxId = this.getBoxUid();
        info.bankerValueOverFlow = this.leftAreanVlaue <= 0;

        for (int i = 1; i < this.playerCnt; ++i) {
            HundredPlayerInfo player = this.allReb[i];
            PCLIHundredArenaNtfOpenCardInfoByBaccarat.BaccaratCardInfo cardInfo =
                new PCLIHundredArenaNtfOpenCardInfoByBaccarat.BaccaratCardInfo();
            cardInfo.cards.addAll(player.getResultCards().isEmpty() ? player.getCards() : player.getResultCards());
            cardInfo.type = player.getCardType().getValue();
            cardInfo.win = player.isWin(EHundredArenaRebType.PLAYER_WIN);
            cardInfo.point = (int)player.getCardValue();
            info.data.add(cardInfo);
        }
        this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_OPEN_CARD, info);
    }

    @Override
    protected void doClear() {
        super.doClear();
        for (int i = 0; i < rebAllRecords.length; i++) {
            rebAllRecords[i] = 0;
        }
        this.winIndex = -1;
    }

    @Override
    protected void doShuffle() {
        if (this.allCard.size() > 6) {
            return;
        }
        if (Switch.USE_CARD_LIB_POKER) {
            this.allCard.addAll(CardLibraryManager.I.getPokerCard());
            return;
        }
        for (byte i = 0; i < 52; ++i) {
            this.allCard.add(i);
        }
        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doReadyBegin() {
        HundredPlayerInfo banker = this.allReb[1];
        banker.addCard(this.allCard.remove(0));
        banker.addCard(this.allCard.remove(0));

        HundredPlayerInfo player = this.allReb[5];
        player.addCard(this.allCard.remove(0));
        player.addCard(this.allCard.remove(0));

        this.setWinResult(player, banker);

        this.remainRebValue = this.curBanker.getValue();
        this.leftAreanVlaue = this.remainRebValue;
        Player bankerPlayer = PlayerManager.I.getPlayer(this.curBanker.getUid());
        PCLIHundredNtfDeskInfoByLhd info = new PCLIHundredNtfDeskInfoByLhd();
        info.boxId = this.getBoxUid();
        info.groupUid = this.room.getGroupUid();
        info.gameType = this.room.getGameType();
        info.bankerPlayerUid = null == bankerPlayer ? -1 : bankerPlayer.getUid();
        info.bankerPlayerName = null == bankerPlayer ? "" : bankerPlayer.getName();
        info.bankerPlayerIcon = null == bankerPlayer ? "" : bankerPlayer.getIcon();
        info.bankerUid = null == bankerPlayer ? -1 : this.curBanker.getBankerUid();
        info.bankerValue = NumberUtils.get2Decimals(null == bankerPlayer ? 0 : this.curBanker.getValue());
        info.curBureau = null == bankerPlayer ? 0 : this.curBanker.getBureau();
        info.remainRebValue = null == bankerPlayer ? 0 : this.remainRebValue / 100;
        info.rule.putAll(this.room.getRule());
        info.state = this.gameState.get().ordinal();
        if (-1 != this.expire) {
            info.remainTime = (int)((this.expire - System.currentTimeMillis()) / 1000);
        } else {
            info.remainTime = -1;
        }

        this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_READY, info);

        banker.setWin(1 == this.winIndex);
        banker.setWin(EHundredArenaRebType.PLAYER_WIN, 1 == this.winIndex);

        player.setWin(5 == this.winIndex);
        player.setWin(EHundredArenaRebType.PLAYER_WIN, 5 == this.winIndex);

        HundredPlayerInfo bankerDoubleInfo = this.allReb[2];
        bankerDoubleInfo.setWin(banker.getCardType() == EPokerCardType.DOUBLE);
        bankerDoubleInfo.setWin(EHundredArenaRebType.PLAYER_WIN, banker.getCardType() == EPokerCardType.DOUBLE);

        HundredPlayerInfo equipInfo = this.allReb[3];
        equipInfo.setWin(3 == this.winIndex);
        equipInfo.setWin(EHundredArenaRebType.PLAYER_WIN, 3 == this.winIndex);

        HundredPlayerInfo playerDoubleInfo = this.allReb[4];
        playerDoubleInfo.setWin(player.getCardType() == EPokerCardType.DOUBLE);
        playerDoubleInfo.setWin(EHundredArenaRebType.PLAYER_WIN, player.getCardType() == EPokerCardType.DOUBLE);
    }

    protected void setWinResult(HundredPlayerInfo player, HundredPlayerInfo banker) {
        int playerPoint = this.getCardPoint(player.getCards());
        int bankerPoint = this.getCardPoint(banker.getCards());

        if (8 != playerPoint && 9 != playerPoint && 8 != bankerPoint && 9 != bankerPoint) {
            if (playerPoint >= 0 && playerPoint < 6) {
                player.addCard(this.allCard.remove(0));
                playerPoint = this.getCardPoint(player.getCards());
            }

            if (bankerPoint >= 0 && bankerPoint < 3) {
                banker.addCard(this.allCard.remove(0));
                bankerPoint = this.getCardPoint(banker.getCards());
            } else if (bankerPoint > 2 && bankerPoint < 7) {
                if (3 == player.getCards().size()) {
                    int playerCard = this.getBaccaratCardValue(player.getCards().get(2));
                    if (3 == bankerPoint && playerCard != 8) {
                        banker.addCard(this.allCard.remove(0));
                    }
                    if (4 == bankerPoint && (playerCard > 1 && playerCard != 8 && playerCard != 9)) {
                        banker.addCard(this.allCard.remove(0));
                    }
                    if (5 == bankerPoint && (playerCard > 3 && playerCard != 8 && playerCard != 9)) {
                        banker.addCard(this.allCard.remove(0));
                    }
                    if (6 == bankerPoint && (playerCard > 5 && playerCard != 8 && playerCard != 9)) {
                        banker.addCard(this.allCard.remove(0));
                    }
                } else {
                    banker.addCard(this.allCard.remove(0));
                }
                bankerPoint = this.getCardPoint(banker.getCards());
            }
        }

        player.setCardValue(playerPoint);
        banker.setCardValue(bankerPoint);
        player.setCardType(
            PokerUtil.getCardValue(player.getCards().get(0)) == PokerUtil.getCardValue(player.getCards().get(1))
                ? EPokerCardType.DOUBLE : EPokerCardType.NONE);
        banker.setCardType(
            PokerUtil.getCardValue(banker.getCards().get(0)) == PokerUtil.getCardValue(banker.getCards().get(1))
                ? EPokerCardType.DOUBLE : EPokerCardType.NONE);

        if (bankerPoint == playerPoint) {
            this.winIndex = 3;
        } else {
            this.winIndex = bankerPoint > playerPoint ? 1 : 5;
        }
    }

    protected byte getCardPoint(List<Byte> card) {
        if (null == card || 0 == card.size()) {
            return 0;
        }
        byte point = 0;
        for (Byte c : card) {
            byte val = this.getBaccaratCardValue(c);
            point += val;
        }
        return (byte)(point % 10);
    }

    protected byte getBaccaratCardValue(byte card) {
        byte temp = (byte)((byte)(card % 13) + 3);
        if (temp >= 10 && temp <= 13) {
            return 0;
        }
        if (14 == temp) {
            return 1;
        }
        if (15 == temp) {
            return 2;
        }
        return temp;
    }

    @Override
    protected int getMul(int index) {
        int mul = 0;

        if (1 == index || 5 == index) {
            mul = 1;
        }

        if (3 == index) {
            mul = 8;
        }

        if (2 == index || 4 == index) {
            mul = 11;
        }

        return mul;
    }
}
