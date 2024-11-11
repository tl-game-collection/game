package com.xiuxiu.app.server.room.handle.impl.hundred;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfDeskInfoByLhd;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfOpenCardInfoByLhd;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfTouzhurenRebInfoByLhd;
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
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

/**
 * 百人场-龙虎斗处理器
 * @author Administrator
 *
 */
public class LhdHundredRoomHandle extends AbstractHundredRoomHandle {
  
    /** 特殊下注区域 1.开启 0.不开启 */
    protected int specialType = 1;
    protected boolean awayBureauClearCard = false; // 每局清理牌
    protected byte cardValue1 = 0;//牌1的值(龙)
    protected byte cardValue2 = 0;//牌2的值(虎)
    protected byte cardNum1 = 0;//牌1的编号
    protected byte cardNum2 = 0;//牌2的编号
    protected CopyOnWriteArrayList<Integer> winIndexList = new CopyOnWriteArrayList<>();//赢的下注区域的下标列表
   

    public LhdHundredRoomHandle(IRoom room, Box box) {
        super(room, box);
        this.rebAllRecords = new int[] {0,0,0,0,0,0,0,0};
    }

    @Override
    public void init() {
        super.init();
        this.specialType = this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_SPECIALTYPE, 1);
        this.rebTime = 1000 * this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_REB_TIME, 15);
        this.readyBeginTime = 1000 * this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_READY_TIME, 0);
        this.openCardTime = 1000 * this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_OPEN_CARD_TEIM, 15);
        this.overTime = 1000 * this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_OVER_TIME, 0);
        
    }

    @Override
    protected int needBankerArenaValue(int index, int value, EHundredArenaRebType type) {
        int arenaValue1 = 1 == index ? this.allReb[1].getAllRebValueByType(type) + value : this.allReb[1].getAllRebValueByType(type);//龙
        int arenaValue2 = 2 == index ? this.allReb[2].getAllRebValueByType(type) + value : this.allReb[2].getAllRebValueByType(type);//和
        int arenaValue3 = 3 == index ? this.allReb[3].getAllRebValueByType(type) + value : this.allReb[3].getAllRebValueByType(type);//虎
        int needArenavalue1 = arenaValue1 - arenaValue3 - arenaValue2;
        int needArenavalue2 = arenaValue3 - arenaValue1 - arenaValue2;
        int needArenavalue3 = arenaValue2 * 8 - arenaValue1 / 2 - arenaValue3 / 2;
        int needArenavalue = needArenavalue1 < needArenavalue2 ? needArenavalue2 : needArenavalue1;
        needArenavalue = needArenavalue < needArenavalue3 ? needArenavalue3 : needArenavalue;

        int arenaValue4 = 4 == index ? this.allReb[4].getAllRebValueByType(type) + value : this.allReb[4].getAllRebValueByType(type);//9-K
        int arenaValue5 = 5 == index ? this.allReb[5].getAllRebValueByType(type) + value : this.allReb[5].getAllRebValueByType(type);//4-8
        int arenaValue6 = 6 == index ? this.allReb[6].getAllRebValueByType(type) + value : this.allReb[6].getAllRebValueByType(type);//A-3
        int needArenavalue4 = arenaValue4 * 45 / 100 - arenaValue5 - arenaValue6;
        int needArenavalue5 = arenaValue5 * 2 - arenaValue4 - arenaValue6;
        int needArenavalue6 = arenaValue6 * 9 - arenaValue5 - arenaValue4 ;
//        int m_max = arenaValue4 * 45 / 100 > arenaValue5 * 2 ? arenaValue4 * 45 / 100 : arenaValue5 * 2;
//        m_max = m_max > arenaValue6 * 9 ? m_max : arenaValue6 * 9;//19
//        needArenavalue += m_max;
        int m_max = needArenavalue4 > needArenavalue5 ? needArenavalue4 : needArenavalue5;
        m_max = m_max > needArenavalue6 ? m_max : needArenavalue6;
        needArenavalue += m_max;

        int arenaValue7 = 7 == index ? this.allReb[7].getAllRebValueByType(type) + value : this.allReb[7].getAllRebValueByType(type);
        int arenaValue8 = 8 == index ? this.allReb[8].getAllRebValueByType(type) + value : this.allReb[8].getAllRebValueByType(type);
        needArenavalue += arenaValue7 * 98 / 100 + arenaValue8 * 280 / 100;//210

        return needArenavalue < 0 ? 0 : needArenavalue;
    }

    @Override
    public EHundredGameState getState() {
        return this.gameState.get();
    }

    /**
     * 开牌
     */
    @Override
    protected void doOpenCard() {
//        this.allCard.remove(0);
//        for (int i = 1; i < 4; ++i) {
//            if (2 == i) {
//                continue;
//            }
//            HundredPlayerInfo player = this.allReb[i];
//            byte card = this.allCard.remove(0);
//            player.addCard(card);
//        }
//        this.winIndex = this.setCompareResult(this.allReb[1].getCards().get(0), this.allReb[3].getCards().get(0));
//        this.setWinIndexList();//设置赢的区域

        for (int i = 1; i < this.playerCnt; ++i) {
            HundredPlayerInfo player = this.allReb[i];
            //boolean isWin = i == this.winIndex;
            boolean isWin = this.winIndexList.contains(i);
            player.setWin(isWin);
            player.setWin(EHundredArenaRebType.PLAYER_WIN, isWin);
        }

        //Logs.ARENA.debug("[龙虎斗] [GroupUid:%d] [BoxUid:%d] [Win:%d]", this.room.getGroupUid(), this.getBoxUid(), this.winIndex);

        PCLIHundredNtfOpenCardInfoByLhd info = new PCLIHundredNtfOpenCardInfoByLhd();
        info.boxId = this.getBoxUid();
        info.bankerValueOverFlow = this.leftAreanVlaue <= 0;
        HundredPlayerInfo player = this.allReb[1];
        info.cards.addAll(player.getResultCards().isEmpty() ? player.getCards() : player.getResultCards());
        player = this.allReb[3];
        info.cards.addAll(player.getResultCards().isEmpty() ? player.getCards() : player.getResultCards());
        info.winIndex = this.winIndex;
        this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_OPEN_CARD, info);
    }

    @Override
    protected void doShuffle() {
        if (this.allCard.size() >= 46) {
            return;
        }
        if (Switch.USE_CARD_LIB_POKER) {
            this.allCard.addAll(CardLibraryManager.I.getPokerCard());
            return;
        }
        for (byte i = 0; i < 52; ++i) {
            this.allCard.add(i);
            this.allCard.add(i);
            this.allCard.add(i);
            this.allCard.add(i);
            this.allCard.add(i);
            this.allCard.add(i);
            this.allCard.add(i);
            this.allCard.add(i);
        }
        ShuffleUtil.shuffle(this.allCard);
        int rand = RandomUtil.random(10, 30);
        for (int i = 0; i < rand; ++i) {
            this.allCard.remove(0);
        }
    }

    @Override
    protected void doReadyBegin() {
        this.allCard.remove(0);
        for (int i = 1; i < 4; ++i) {
            if (2 == i) {
                continue;
            }
            HundredPlayerInfo player = this.allReb[i];
            byte card = this.allCard.remove(0);
            player.addCard(card);
        }
        this.winIndex = this.setCompareResult(this.allReb[1].getCards().get(0), this.allReb[3].getCards().get(0));
        this.setWinIndexList();//设置赢的区域
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
            info.remainTime = (int) ((this.expire - System.currentTimeMillis()) / 1000);
        } else {
            info.remainTime = -1;
        }

        this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_READY, info);

//        for (int i = 1; i < this.playerCnt; ++i) {
//            HundredPlayerInfo player = this.allReb[i];
//            //boolean isWin = i == this.winIndex;
//            boolean isWin = this.winIndexList.contains(i);
//            player.setWin(isWin);
//            player.setWin(EHundredArenaRebType.PLAYER_WIN, isWin);
//        }
    }

    @Override
    protected void doClear() {
        super.doClear();
        for (int i = 0; i < rebAllRecords.length; i++) {
            rebAllRecords[i] = 0;
        }
    }

    @Override
    public void setWinIndex(int index) {
        if (this.isStart() && this.gameState.get().ordinal() < EHundredGameState.OPEN_CARD.ordinal()) {
            if (index != this.winIndex) {
                if (2 == index) {
                    // 和
                    HundredPlayerInfo player1 = this.allReb[1];
                    byte card1 = player1.getCards().get(0);
                    HundredPlayerInfo player2 = this.allReb[3];
                    player2.getCards().clear();
                    player2.getRealCards().clear();
                    player2.addCard(card1);
                    this.winIndex = 2;
                    this.setCompareResult(card1, card1);
                    this.setWinIndexList();//设置赢的区域
                } else {
                    if (2 != this.winIndex) {
                        HundredPlayerInfo player1 = this.allReb[1];
                        byte card1 = player1.getCards().get(0);
                        HundredPlayerInfo player2 = this.allReb[3];
                        byte card2 = player2.getCards().get(0);
                        player1.getCards().clear();
                        player1.getRealCards().clear();
                        player1.addCard(card2);
                        player2.getCards().clear();
                        player2.getRealCards().clear();
                        player2.addCard(card1);
                        this.winIndex = this.setCompareResult(card2, card1);
                        this.setWinIndexList();//设置赢的区域
                    } else {
                        HundredPlayerInfo player2 = this.allReb[3];
                        byte card1 = this.allReb[1].getCards().get(0);
                        while (index != this.winIndex && !this.allCard.isEmpty()) {
                            player2.getCards().clear();
                            player2.getRealCards().clear();
                            byte card2 = this.allCard.remove(0);
                            player2.addCard(card2);
                            this.winIndex = this.setCompareResult(card1, card2);
                            this.setWinIndexList();//设置赢的区域
                        }
                    }
                }
                for (int i = 1; i < this.playerCnt; ++i) {
                    HundredPlayerInfo player = this.allReb[i];
//                    boolean isWin = i == this.winIndex;
                    boolean isWin = this.winIndexList.contains(i);
                    player.setWin(isWin);
                    player.setWin(EHundredArenaRebType.PLAYER_WIN, isWin);
                }
            }
        }
    }

    @Override
    public int getWinIndex() {
        return this.winIndex;
    }

    @Override
    public List<Integer> getWinIndexList() {
        return this.winIndexList;
    }

    /**
     * 比牌
     * @param card1 牌1编号
     * @param card2 牌2编号
     * @return 赢的位置 1龙 2和 3虎
     */
    private int setCompareResult(byte card1, byte card2) {
        this.cardNum1 = card1;
        this.cardNum2 = card2;
        this.cardValue1 = this.getLhdCardValue(card1);
        this.cardValue2 = this.getLhdCardValue(card2);
        return (this.cardValue1 == this.cardValue2) ? 2 : (this.cardValue1 > this.cardValue2 ? 1 : 3);
    }

    /**
     * 根据编号获取牌值
     * @param card 编号
     * @return
     */
    private byte getLhdCardValue(byte card) {
        byte temp = (byte) ((card % 13) + 3);
        if (14 == temp) {
            return 1;
        }
        if (15 == temp) {
            return 2;
        }
        return temp;
    }

    /**
     * 赋值赢的区域列表
     */
    private void setWinIndexList(){
        this.winIndexList.clear();
        //较大的牌值
        byte m_maxCard = this.cardValue1 > this.cardValue2 ? this.cardValue1 : this.cardValue2;
        //龙、和、虎
        this.winIndexList.add(this.winIndex);
        //9-K
        if (m_maxCard >= 9 && m_maxCard <= 13) {
            this.winIndexList.add(4);
        }
        //4-8
        if (m_maxCard >= 4 && m_maxCard <= 8) {
            this.winIndexList.add(5);
        }
        //A-3
        if (m_maxCard >= 1 && m_maxCard <= 3) {
            this.winIndexList.add(6);
        }
        //同色
        if (Double.valueOf(Math.ceil((float)(this.cardNum1 + 1) / 13)).intValue() % 2 == Double.valueOf(Math.ceil((float)(this.cardNum2 + 1) / 13)).intValue() % 2) {
            this.winIndexList.add(7);
        }
        //同花
        if (Double.valueOf(Math.ceil((float)(this.cardNum1 + 1) / 13)).intValue() == Double.valueOf(Math.ceil((float)(this.cardNum2 + 1) / 13)).intValue()) {
            this.winIndexList.add(8);
        }
    }

    @Override
    protected List<Byte> getCards(HundredPlayerInfo playerInfo) {
        return playerInfo.getCards();
    }

    @Override
    protected boolean checkBanker(IHundredBanker bankerPlayer) {
        return Boolean.TRUE;
    }

    @Override
    protected int getWinOrLostValue(EHundredArenaRebType type, HundredPlayerInfo banker, HundredPlayerInfo player,
            boolean isWin, int value) {
        if (EHundredArenaRebType.PLAYER_WIN == type) {
            if (isWin) {
                return value * this.getMul(player.getIndex()) / 100;
            } else {
                int index = player.getIndex();//玩家下注位置
                if (this.winIndex == 2 && (index == 1 || index == 3)) {
                    return -value / 2;
                }
                return -value;
            }
        }
        return 0;
    }
    
    @Override
    protected int getMul(int index) {
        int mul = 0;
        //和
        if (index == 2) {
            mul = 800;
        }
        //龙和虎
        if (index == 1 || index == 3) {
            mul = 100;
        }
        //9~K
        if (index == 4) {
            mul = 45;
        }
        //4~8
        if (index == 5) {
            mul = 200;
        }
        //1~3
        if (index == 6) {
            mul = 900;
        }
        //同色
        if (index == 7) {
            mul = 98;
        }
        //同花
        if (index == 8) {
            mul = 280;
        }
        return mul;
    }

    /**
     * 房卡消耗
     * @return
     */
    @Override
    public Float roomCard() {
        if (this.allRebValue == 0) {
            return 0f;
        }
        float value = this.allRebValue / 50000f;
        DecimalFormat df = new DecimalFormat("#.0");
        df.format(value);
        return Float.valueOf(df.format(value)) > 0.2f ? Float.valueOf(df.format(value)) : 0.2f;
    }

}
