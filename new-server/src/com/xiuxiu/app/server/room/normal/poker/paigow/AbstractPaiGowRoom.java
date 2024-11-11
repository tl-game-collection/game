package com.xiuxiu.app.server.room.normal.poker.paigow;

import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowDeskInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowGameOverInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowOpenInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowPreDealCardInfo;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowRobBankerResultInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.handle.impl.BoxArenaPawiGowHotRoomHandle;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowOpenAction;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowRebetAction;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowRobBankerAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.PaiGowPlayer;
import com.xiuxiu.app.server.room.record.poker.RecordPokerPlayerBriefInfo;
import com.xiuxiu.app.server.table.TbPai9;
import com.xiuxiu.app.server.table.TbPai9Manager;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.*;

public abstract class AbstractPaiGowRoom extends PokerRoom implements IPaiGowRoom{

    protected int dao; // 道的选择 2-两道杠 3-三道杠
    protected int baseBottomScore; // 锅底分
    protected int paiGowType; // 1-大牌九 2-小牌九 3-加锅牌九
    protected int bankerCard; // 庄家亮牌 0-不亮牌 2-亮2张 3-亮三张
    protected int openCardTime; // 开牌超时时间

    protected int keepCount = 0;
    public int curHotDeskNote = 0; // 当前锅底,当前锅中的筹码

    protected int curLoop = 0; // 当前轮数；加锅牌九，连庄次数，换庄或重新抢庄清零

    protected List<Byte> curDealCards = new ArrayList<>();//当前轮发的牌
    protected List<Byte> preDealCards = new ArrayList<>();//上一轮发的牌
    protected int crap1;//骰子1
    protected int crap2;//骰子2
    protected int handCardNum = 0;//玩家手牌数量  大牌九4 小牌九2

    protected static final int REBET_OUTTIME = 15000; //下注超时时间(毫秒)
    protected static final int ROBBANK_OUTTIME = 15000; //抢庄超时时间(毫秒)

    /**
     * 构造函数
     * @param info
     */
    public AbstractPaiGowRoom(RoomInfo info) {
        super(info, ERoomType.NORMAL);
    }

    /**
     * 构造函数
     * @param info
     * @param roomType
     */
    public AbstractPaiGowRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    /**
     * 房间初始化
     */
    @Override
    public void init() {
        super.init();
        this.dao = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_DAO_RULE, 2);
        this.baseBottomScore = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_BOTTOMSCORE_RULE, 60);
        this.paiGowType = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_GAMEPLAY_PAGE, 2);
        this.openCardTime = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_OPNE_CARD_TIME,30000);
        this.detectionIP = false;
    }

    @Override
    protected void doDeal() {
        doDealPrepare();
        doDealRecord();
        doDealInit();
    }

    protected abstract void doDealInit();

    private void doDealRecord(){
        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            PokerUtil.sort(player.getHandCard());
            this.getRecord().addPlayer(new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getBureau(), player.getHandCard()));
        }
    }

    protected  abstract void doDealPrepare();

    /**
     * 设置庄家
     * @param maxRobBanker
     * @param max
     */
    @Override
    public void setMaxRobBanker(Long[] maxRobBanker, int max) {
        doSetMaxRobBanker(maxRobBanker, max);
        noticeRobBankerResult();
        doSetMaxRobBankerAfter();
    }

    protected abstract void doSetMaxRobBankerAfter();

    protected void doSetMaxRobBanker(Long[] maxRobBanker, int max) {
        this.bankerIndex = this.getRoomPlayer(maxRobBanker[RandomUtil.random(0, max - 1)]).getIndex();
    }

    private void noticeRobBankerResult(){
        PCLIPokerNtfCowRobBankerResultInfo info = new PCLIPokerNtfCowRobBankerResultInfo();
        info.bankerPlayerUid = this.allPlayer[this.bankerIndex].getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_ROB_BANKER_RESULT, info);
    }

    /**
     * 好牌率
     * @param playerGoodCards
     */
    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    /**
     * 通知结束
     * @param next 是否有下一轮
     */
    @Override
    protected void doSendGameOver(boolean next) {
        PCLIPokerNtfPaiGowGameOverInfo gameOverInfo = new PCLIPokerNtfPaiGowGameOverInfo();
        gameOverInfo.next = next;
        gameOverInfo.curHotDeskNote = getShowCurHotDeskNote();
        gameOverInfo.bankerBureau = this.curLoop;
        gameOverInfo.keepHotCount = this.keepCount;

        for (int i = 0; i < this.playerNum; ++i) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIPokerNtfPaiGowGameOverInfo.GameOverInfo info = new PCLIPokerNtfPaiGowGameOverInfo.GameOverInfo();
            info.score = getShowScore(player);
            //玩家自身的竞技值
            int m_total = 0;
            long formClubUid = this.getFromClubUid(player.getUid());
            IClub iClub = ClubManager.I.getClubByUid(formClubUid);
            if (iClub != null) {
                m_total = (int)iClub.getMemberExt(player.getUid(),true).getGold();
            }
            info.totalScore = this.getClientScore(m_total);
            //info.totalScore = getShowTotalScore(player);
            //info.totalScore = this.getClientScore(player.getScore());
            info.bureau = player.getBureau();
            info.card.addAll(player.getOpenCards());

            if (!next) {
                PCLIPokerNtfPaiGowGameOverInfo.TotalCnt totalCnt = new PCLIPokerNtfPaiGowGameOverInfo.TotalCnt();
                totalCnt.lostCnt = player.getScore(Score.ACC_LOST_CNT, true);
                totalCnt.winCnt = player.getScore(Score.ACC_WIN_CNT, true);
                totalCnt.maxCardType = player.getScore(Score.ACC_MAX_SCORE, true) / 100;
                totalCnt.maxScore = player.getScore(Score.ACC_POKER_MAX_CARD_TYPE, true);
                info.totalCnt = totalCnt;
            }
            gameOverInfo.allGameOverInfo.put(player.getUid(), info);
        }

        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, gameOverInfo);
    }

    protected abstract int getShowCurHotDeskNote();

    protected abstract String getShowScore(PaiGowPlayer player);

    /**
     * 同步牌桌信息
     * @param player
     */
    @Override
    public void syncDeskInfo(IPlayer player) {
        IPokerPlayer paiGowPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        PCLIPokerNtfPaiGowDeskInfo deskInfo = new PCLIPokerNtfPaiGowDeskInfo();
        deskInfo.roomInfo = this.getRoomInfo();
        // deskInfo.curBureau =  null == paiGowPlayer ? 0 : paiGowPlayer.getRoomPlayerHelper().getCurBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        if (!deskInfo.gameing && this.paiGowType == 3 && !this.checkAgain()) {
            deskInfo.bankerIndex = -1;
        }
        deskInfo.lastTakePlayerUid = null == this.lastTakeCardPlayer ? -1 : this.lastTakeCardPlayer.getUid();
        deskInfo.preDealCard = this.preDealCards;
        deskInfo.curHotDeskNote = this.curHotDeskNote;
        deskInfo.keepHotCount = this.keepCount;
        deskInfo.bankerBureau = this.curLoop;

        if (paiGowPlayer != null) {
            deskInfo.card.addAll(paiGowPlayer.getHandCard());
            deskInfo.defaultCards = ((PaiGowPlayer) paiGowPlayer).getDefaultListCard();
            deskInfo.defaultType = ((PaiGowPlayer) paiGowPlayer).getDefaultCardType();
            deskInfo.openCard = ((PaiGowPlayer) paiGowPlayer).getListCard();
            deskInfo.openCardType = ((PaiGowPlayer) paiGowPlayer).getCardType();
        }

        fillBankerShowCards(deskInfo);
        int curBureau = 0;
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer temp = (IPokerPlayer) this.allPlayer[i];
            if (null == temp) {
                continue;
            }
            if (curBureau == 0) {
                curBureau = temp.getRoomPlayerHelper().getCurBureau();
            }
            List<Integer> rebets = new ArrayList<>();
            rebets.add(temp.getScore(Score.POKER_PAIGOW_ONE_REB, false));
            rebets.add(temp.getScore(Score.POKER_PAIGOW_TWO_REB, false));
            rebets.add(temp.getScore(Score.POKER_PAIGOW_THREE_REB, false));
            deskInfo.allRebet.put(temp.getUid(), rebets);
            deskInfo.allRobBank.put(temp.getUid(), temp.getScore(Score.POKER_PAIGOW_ROB_BANKER_MUL, false));
            deskInfo.allScore.put(temp.getUid(), getShowAllScore(temp));
            deskInfo.allOnlineState.put(temp.getUid(), temp.isOffline() ? false : true);
            boolean isOpenCard = ((PaiGowPlayer) temp).isOpenCard();
            deskInfo.isOpenCards.put(temp.getUid(), isOpenCard);
            if (isOpenCard) {
                deskInfo.openCards.put(temp.getUid(), ((PaiGowPlayer) temp).getOpenCards());
            }
        }
        deskInfo.curBureau =  null == paiGowPlayer ? curBureau : paiGowPlayer.getRoomPlayerHelper().getCurBureau();
        deskInfo.curPhase = getCurPhase();
        deskInfo.robBank = isRobBank();
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }
    
    protected boolean isRobBank() {
        if (this.action.isEmpty()) {
            return Boolean.FALSE;
        }
        IAction action = this.action.peek();
        if (action instanceof PaiGowRobBankerAction) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    protected abstract int getCurPhase();

    protected abstract void fillBankerShowCards(PCLIPokerNtfPaiGowDeskInfo deskInfo);

    protected abstract String getShowAllScore(IPokerPlayer pokerPlayer);

    public int getCurLoop() {
        return curLoop;
    }


    /**
     * 洗牌
     */
    @Override
    protected void doShuffle() {
        //是否使用牌库--配牌
        if (Switch.USE_CARD_LIB_POKER) {
            this.allCard.clear();
            this.allCard.addAll(CardLibraryManager.I.getPokerCard());
            return;
        }
        int minNum = this.handCardNum * 4;//最少需要多少牌（每个人手牌数量 * 玩家人数）
        //如果牌够
        if (this.allCard.size() >= minNum) {
            return;
        }
        //牌不够，重新添加牌
        this.allCard.clear();
        this.preDealCards.clear();
        this.curDealCards.clear();
        for (byte i = 1; i < 22; ++i) {
            this.allCard.add(i);
            if (i < 12) {
                this.allCard.add(i);
            }
        }
        //乱序
        Collections.shuffle(this.allCard);
    }

    protected void initCardType() {
        for (int i = 0, len = this.allPlayer.length; i < len; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            List<Byte> handCard = player.getHandCard();
            if (2 == handCard.size()) {
                int type = this.getPaiGowID(handCard.get(0), handCard.get(1));
                ((PaiGowPlayer) player).addDefaultListCard(handCard, null);
                ((PaiGowPlayer) player).addDefaultCardType(type, 0);
            } else if (4 == handCard.size()) {
                int maxType = Integer.MAX_VALUE;
                int[] cardIndex = new int[2];
                for (int j = 0, size = handCard.size(); j < size; ++j) {
                    byte curCard = handCard.get(j);
                    for (int k = j + 1; k < size; ++k) {
                        byte nextCard = handCard.get(k);
                        int temp = this.getPaiGowID(curCard, nextCard);
                        if (maxType > temp) {
                            maxType = temp;
                            cardIndex[0] = j;
                            cardIndex[1] = k;
                        }
                    }
                }
                List<Byte> minCard = new ArrayList<>();
                List<Byte> maxCard = new ArrayList<>();
                for (int j = 0, size = handCard.size(); j < size; ++j) {
                    if ((j == cardIndex[0] || j == cardIndex[1]) && maxCard.size() < 2) {
                        maxCard.add(handCard.get(j));
                    } else {
                        minCard.add(handCard.get(j));
                    }
                }
                int minCardType = this.getPaiGowID(minCard.get(0), minCard.get(1));
                ((PaiGowPlayer) player).addDefaultListCard(minCard, maxCard);
                ((PaiGowPlayer) player).addDefaultCardType(minCardType, maxType);
            }
        }
    }

    /**
     * 通知上一次牌
     */
    protected void doSendPreDealCard() {
        PCLIPokerNtfPaiGowPreDealCardInfo info = new PCLIPokerNtfPaiGowPreDealCardInfo();
        info.preDealCard.addAll(this.preDealCards);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_PREDEALCARD_INFO, info);
    }

    /**
     * 通知开始抢庄
     * @param timeout
     */
    protected void beginRobBank(long timeout) {
        PaiGowRobBankerAction action = new PaiGowRobBankerAction(this, timeout/*ROBBANK_OUTTIME*/);
        for (int i = 0; i < this.playerNum; ++i) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addRobBanker(player.getUid());
        }
        this.action.add(action);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_ROB_BANKER_BEGIN, null);
    }

    /**
     * 请求抢庄
     * @param player
     * @param mul
     * @return
     */
    @Override
    public ErrorCode onRobBank(Player player, int mul) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法抢庄", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法抢庄", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法抢庄", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        //身上竞技值小于锅底分
        IClub club = ClubManager.I.getClubByUid(this.getGroupUid());
        if (club != null) {
            long clubUid = club.getEnterFromClubUid(player.getUid());
            if (clubUid != club.getClubUid()) {
                IClub tempClub = ClubManager.I.getClubByUid(clubUid);
                if (tempClub != null) {
                    club = tempClub;
                }
            }
            if (club.getGold(player.getUid()) < this.baseBottomScore * 100) {
                Logs.ROOM.warn("%s %s 竞技分不足, 无法抢庄", this, player);
                return ErrorCode.CLUB_OWNER_NOT_ARENA_VALUE;
            }
        }
        IAction action = this.action.peek();
        if (action instanceof PaiGowRobBankerAction) {
            ErrorCode err = ((PaiGowRobBankerAction) action).selectRobBaker(player.getUid(), mul);
            if (ErrorCode.OK == err) {
                roomPlayer.setScore(Score.POKER_PAIGOW_ROB_BANKER_MUL, mul, false);
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是抢庄动作, 无法抢庄", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 开始下注
     * @param timeout
     */
    protected abstract void beginRebet(long timeout);

    /**
     * 请求下注
     * @param player
     * @param rebets
     * @return
     */
    @Override
    public ErrorCode onRebet(Player player, List<Integer> rebets) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法下注", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法下注", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法下注", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (this.bankerIndex == roomPlayer.getIndex()) {
            Logs.ROOM.warn("%s %s 庄家不可以下注", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        //抢庄牌九竞技场中下注竞技值不足
        if (GameType.GAME_TYPE_PAIGOW_ROB == this.info.getGameType() && roomPlayer.getScore() < rebets.get(0)*10) {
            Logs.ROOM.warn("%s %s 竞技值不足", this, player);
            return ErrorCode.ARENA_NOTE_FAIL;
        }

        Logs.ROOM.debug("%s %s  onRebet下注值 rebet:%s ", this, player, rebets);

        IAction action = this.action.peek();
        if (action instanceof PaiGowRebetAction) {
            ErrorCode err = ((PaiGowRebetAction) action).rebet(player.getUid(), rebets);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是下注动作, 无法下注", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 下注结束
     */
    @Override
    public void onRebOver() {
        this.beginOpenCardBefore();
        this.beginOpenCard(this.openCardTime);
    }

    /**
     * 开始开牌前处理
     */
    protected abstract void beginOpenCardBefore();

    /**
     * 开始开牌
     * @param timeout
     */
    protected void beginOpenCard(long timeout) {
        PaiGowOpenAction action = new PaiGowOpenAction(this, this.openCardTime + 4000);
        for (int i = 0; i < this.playerNum; ++i) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addOpenCard(player.getUid());
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_OPNE_CARD_BEGIN, null);
        }
        this.addAction(action);
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_PAI_GOW_OPNE_CARD_BEGIN, null);
    }

    /**
     * 请求开牌
     * @param player
     * @param card1
     * @param card2
     * @return
     */
    @Override
    public ErrorCode onOpenCard(Player player, List<Byte> card1, List<Byte> card2) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法开牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法开牌", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法开牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (card1 == null || card1.size() != 2) {
            Logs.ROOM.warn("%s %s 数据格式不对", this, player);
            return ErrorCode.REQUEST_INVALID;
        }

        if (!((PaiGowPlayer) roomPlayer).isEquipHandCard(card1, card2)) {
            Logs.ROOM.warn("%s %s 手中的牌跟发送的牌不一致", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        int type1 = null != card1 && 2 == card1.size() ? this.getPaiGowID(card1.get(0), card1.get(1)) : 0;
        int type2 = null != card2 && 2 == card2.size() ? this.getPaiGowID(card2.get(0), card2.get(1)) : 0;

        IAction action = this.action.peek();
        if (action instanceof PaiGowOpenAction) {
            ErrorCode err = ((PaiGowOpenAction) action).open(player.getUid(), card1, card2);
            if (ErrorCode.OK == err) {
                if (type1 >= type2) {
                    ((PaiGowPlayer) roomPlayer).addListCard(card1, card2);
                    ((PaiGowPlayer) roomPlayer).addCardType(type1, type2);
                } else {
                    ((PaiGowPlayer) roomPlayer).addListCard(card2, card1);
                    ((PaiGowPlayer) roomPlayer).addCardType(type2, type1);
                }
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是开牌动作, 无法开牌", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 开牌结束
     */
    @Override
    public void onOpenOver() {
        for (int i = 0; i < this.playerNum; ++i) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIPokerNtfPaiGowOpenInfo openInfo = new PCLIPokerNtfPaiGowOpenInfo();
            openInfo.playerUid = player.getUid();
            openInfo.card = player.getOpenCards();
            openInfo.cardType[0] = player.getOpenCardType()[0];
            openInfo.cardType[1] = player.getOpenCardType()[1];
            this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_OPNE_CARD, openInfo);
        }

        DelayAction action = new DelayAction(this, 2000 * this.getCurPlayerCnt());
        final AbstractPaiGowRoom self = this;
        action.setCallback(new ICallback<Object>() {
            @Override
            public void call(Object... o) {
                self.onOver();
            }
        });
        this.addAction(action);
    }

    protected abstract void onOver();

    private int getPaiGowID(int card1, int card2) {
        TbPai9.TbPai9Info info = TbPai9Manager.I.getType(card1, card2);
        if (info != null) {
            int playMethod = this.getRule().getOrDefault(RoomRule.RR_PAIGOW_PLAYMETHOD, 0);
            if (1 == info.getType()) {
                return 0 != (playMethod & EPaiGowSpecialType.ZHA_DAN.getValue()) ? info.getNum1() : info.getNum2();
            }
            if (2 == info.getType()) {
                return 0 != (playMethod & EPaiGowSpecialType.GUI_ZI.getValue()) ? info.getNum1() : info.getNum2();
            }
            if (3 == info.getType()) {
                return 0 != (playMethod & EPaiGowSpecialType.TIAN_WANG_JIU.getValue()) ? info.getNum1() : info.getNum2();
            }
            if (4 == info.getType()) {
                return 0 != (playMethod & EPaiGowSpecialType.DI_JIU_NIANG_NIANG.getValue()) ? info.getNum1() : info.getNum2();
            }
            return info.getNum2();
        }
        return 0;
    }

    /**
     * 比牌逻辑
     * @param banker
     * @param player
     * @return 0和  -1庄输  1庄赢
     */
    protected int compare(PaiGowPlayer banker, PaiGowPlayer player) {
        if (2 == this.handCardNum) {
            int bankerCardType = banker.getOpenCardType()[0];
            int playerCardType = player.getOpenCardType()[0];
            banker.setMaxCardType(bankerCardType);
            player.setMaxCardType(playerCardType);
            int result = -Integer.compare(bankerCardType, playerCardType);
            return result == 0 ? 1 : result; // 一样大的时候，算作庄家大
        }

        if (4 == this.handCardNum) {
            int bankerMaxCardType = banker.getOpenCardType()[0] > banker.getOpenCardType()[1] ? banker.getOpenCardType()[1] : banker.getOpenCardType()[0];
            int playerMaxCardType = player.getOpenCardType()[0] > player.getOpenCardType()[1] ? player.getOpenCardType()[1] : player.getOpenCardType()[0];
            banker.setMaxCardType(bankerMaxCardType);
            player.setMaxCardType(playerMaxCardType);
            int temp1 = -Integer.compare(banker.getOpenCardType()[0], player.getOpenCardType()[0]);
            if (0 == temp1) {
                temp1 = 1;
            }
            int temp2 = -Integer.compare(banker.getOpenCardType()[1], player.getOpenCardType()[1]);
            if (0 == temp2) {
                temp2 = 1;
            }
            int rs = temp1 + temp2;
            if (0 == rs) {
                return 0;
            } else if (rs < 0) {
                return -1;
            } else {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 获取所有闲家list
     * @return
     */
    protected List<PaiGowPlayer> getPlayerSortList() {
        PaiGowPlayer bankerPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
        //-----看不懂为什么要排序输了的闲家
        List<PaiGowPlayer> lostPlayerList = new ArrayList<>();
        List<PaiGowPlayer> winPlayerList = new ArrayList<>();
        for (int j = 0; j < this.playerNum; ++j) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (player.getUid() == bankerPlayer.getUid()) {
                continue;
            }
            int bankerWin = this.compare(bankerPlayer, player);
            if (bankerWin > 0) {
                winPlayerList.add(player);
            } else if (bankerWin < 0) {
                lostPlayerList.add(player);
            }
        }
        lostPlayerList.sort(new Comparator<PaiGowPlayer>() {
            @Override
            public int compare(PaiGowPlayer o1, PaiGowPlayer o2) {
                return (int) o1.getOpenCardType()[0] - (int) o2.getOpenCardType()[0];
            }
        });
        winPlayerList.addAll(lostPlayerList);
        return winPlayerList;
    }

    /**
     * 发手牌
     */
    protected  void doDealHandCard() {
        int dealNum = this.handCardNum;
        for (int i = 0; i < dealNum; ++i) {
            int temp = 0;
            for (int j = this.bankerIndex, k = 0; k < this.playerNum; ++k) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[(j + k) % this.playerNum];
                if (null == player || player.isGuest()) {
                    continue;
                }
                byte card = this.allCard.removeFirst();
                player.addHandCard(card);
                this.curDealCards.add(card);
                ++temp;
            }

            while (4 - temp > 0) {
                byte card = this.allCard.removeFirst();
                this.curDealCards.add(card);
                ++temp;
            }
        }
    }
    

    @Override
    protected void gameOver(boolean next) {
        if (getRoomHandle() instanceof BoxArenaPawiGowHotRoomHandle) {
            BoxArenaPawiGowHotRoomHandle handle = (BoxArenaPawiGowHotRoomHandle)getRoomHandle();
            handle.serviceCharge(false);
        }
        
    }

    public int getBaseBottomScore() {
        return getScore(baseBottomScore);
    }

    public int getKeepCount() {
        return keepCount;
    }

    @Override
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 * value / 10;
    }
}
