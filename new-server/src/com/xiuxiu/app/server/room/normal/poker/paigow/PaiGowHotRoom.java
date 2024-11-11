package com.xiuxiu.app.server.room.normal.poker.paigow;

import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowDeskInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowGameOverInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowHotCardInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowHotLoopInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByPaiGowHot;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowHotAgainAction;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowHotOutAction;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowReadyAction;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowRebetAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.PaiGowPlayer;
import com.xiuxiu.app.server.room.record.poker.PaiGowSendCardRecordAction;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.ResultRecordAction;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.List;

@GameInfo(gameType = GameType.GAME_TYPE_PAIGOW, gameSubType = 1)
public class PaiGowHotRoom extends AbstractPaiGowRoom {
    enum EPhase {
        INIT, READY, REB, OPEN_CARD
    }
    protected static final long HOT_AGAIN_TS = 10 * 1000;
    protected static final long HOT_STOP_TS = 10 * 1000;
    protected static final long HOT_READY_TS = 2 * 1000;

    protected int minRebScore; // 最低下注
    protected int hotCnt; // 续锅次数
    protected int opTime; // 操作时间
    protected int opType; // 操作流程 1: 先发牌后下注, 2: 先下注后发牌
    protected int maxLoop = -1; // 最大局数

    protected EPhase curPhase = EPhase.INIT; // 当前阶段

    /**
     * 构造函数
     * @param info
     */
    public PaiGowHotRoom(RoomInfo info) {
        super(info, ERoomType.NORMAL);
    }

    /**
     * 构造函数
     * @param info
     * @param roomType
     */
    public PaiGowHotRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    /**
     * 初始化
     */
    @Override
    public void init() {
        super.init();
        this.minRebScore = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_MINBETS_RULE, 5);
        this.hotCnt = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_HOT_CNT, 0);

        this.opTime = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_OP_TIME, 15000);
        this.opType = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_OP_TYPE, 1);
        this.bankerCard = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_BANKERCARD_RULE, 0);
        this.maxLoop  = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_MAX_LOOP, -1);
        this.handCardNum = this.info.getRule().get(RoomRule.RR_PAIGOW_GAMEPLAY_RULE) == 1 ? 4 : 2;
        this.bankerCard = Math.min(this.bankerCard, this.handCardNum);

        //是否自动准备
        this.autoReady = false;
    }

    /**
     * 开始选庄
     */
    @Override
    protected void doStart1() {
        this.beginRobBank(this.opTime);
    }

    @Override
    protected void doDealPrepare() {
        // to do nothing...
    }

    @Override
    protected void doDealInit() {
        // to do nothing...
    }

    @Override
    protected void doSendGameStart() {
        PCLIRoomNtfBeginInfoByPaiGowHot info = new PCLIRoomNtfBeginInfoByPaiGowHot();
        info.roomBriefInfo = this.getRoomBriefInfo();
        info.loop = this.curLoop;
        info.hotNote = this.curHotDeskNote;
        for (int i = 0, len = this.allPlayer.length; i < len; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            info.bureau = player.getBureau();
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }

        info = new PCLIRoomNtfBeginInfoByPaiGowHot();
        info.bureau = 0;
        info.roomBriefInfo = this.getRoomBriefInfo();
        info.loop = this.curLoop;
        info.hotNote = this.curHotDeskNote;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, info);
    }

    private void doDeal0() {
        this.doShuffle();

        this.curDealCards.clear();
        this.curPlayerCnt = this.playerCnt.get();

        doDealHandCard();

        this.initCardType();
        PaiGowSendCardRecordAction sendCardRecordAction = ((PokerRecord) this.getRecord()).addPaiGowSendCardRecordAction();
        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            PokerUtil.sort(player.getHandCard());
            // this.getRecord().addPlayer(new
            // RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(),
            // player.getBureau(), player.getHandCard()));
            sendCardRecordAction.addCard(player.getUid(), player.getHandCard());
        }
    }

    /**
     * 发牌
     */
    private void doSendCard() {
        IPokerPlayer bankerPlayer = (IPokerPlayer) this.allPlayer[this.bankerIndex];
        for (int i = 0, len = this.allPlayer.length; i < len; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIPokerNtfPaiGowHotCardInfo info = new PCLIPokerNtfPaiGowHotCardInfo();
            info.crap1 = this.crap1;
            info.crap2 = this.crap2;
            info.myCard.addAll(player.getHandCard());
            if (this.bankerCard > 0) {
                info.bankerCard.addAll(bankerPlayer.getHandCard().subList(0, this.bankerCard));
            }
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_CARD_INFO, info);//通知牌信息
        }
        PCLIPokerNtfPaiGowHotCardInfo info = new PCLIPokerNtfPaiGowHotCardInfo();
        if (this.bankerCard > 0) {
            info.bankerCard.addAll(bankerPlayer.getHandCard().subList(0, this.bankerCard));
        }
        info.crap1 = this.crap1;
        info.crap2 = this.crap2;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_PAI_GOW_CARD_INFO, info);
    }

    @Override
    protected void doSetMaxRobBankerAfter() {
        PaiGowPlayer bankerPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
        this.setBanker(bankerPlayer);
        this.newLoop();
    }

    private void setBanker(PaiGowPlayer bankerPlayer) {
        this.curHotDeskNote = this.baseBottomScore;
        bankerPlayer.addScore(Score.SCORE, this.getScore(this.curHotDeskNote), false);
    }

    private void newLoop() {
        if (this.curLoop > 0) {
            getRoomHandle().again();
        }
        ++this.curLoop;
        this.curPhase = EPhase.INIT;
        this.crap1 = RandomUtil.random(1, 6);
        this.crap2 = RandomUtil.random(1, 6);
        this.doSendLoopInfo();
        this.doSendPreDealCard();
        // 准备阶段
        this.beginReady();
    }

    private void doSendLoopInfo() {
        PCLIPokerNtfPaiGowHotLoopInfo info = new PCLIPokerNtfPaiGowHotLoopInfo();
        info.curLoop = this.curLoop;
        info.curNote = this.curHotDeskNote;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_LOOP, info);
    }

    /**
     * 开始准备
     */
    private void beginReady() {
        this.curPhase = EPhase.READY;
        if (1 == this.curLoop) {
            this.onReadyOver();
        } else {
            //long time = 1000;
            long time = HOT_READY_TS*2+500;
//            if (this.curLoop <= 3)
//                time = HOT_READY_TS*2+500;
            PaiGowReadyAction action = new PaiGowReadyAction(this,time);
            for (int i = 0; i < this.playerNum; ++i) {
                PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                action.addPlayer(player.getUid());
                player.send(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_READY, null);
            }
            this.addAction(action);
            this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_READY, null);
        }
    }

    /**
     * 准备结束
     */
    public void onReadyOver() {
        this.doDeal0();
        if (1 == this.opType) {
            // 先发牌后下注
            this.doSendCard();
            final PaiGowHotRoom self = this;
            DelayAction action = new DelayAction(this, 3800);
            action.setCallback(new ICallback<Object>() {
                @Override
                public void call(Object... args) {
                    self.beginRebet(self.opTime);
                }
            });
            this.addAction(action);
        } else {
            this.beginRebet(this.opTime);
        }
    }

    @Override
    protected void beginRebet(long timeout) {
        this.curPhase = EPhase.REB;
        PaiGowRebetAction action = new PaiGowRebetAction(this, timeout/*REBET_OUTTIME*/);
        action.setBase(this.getRule().getOrDefault(RoomRule.RR_PAIGOU_ROB_BASE, 1));//设置底分
        for (int i = 0; i < this.playerNum; ++i) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_REBET_BEGIN, null);//通知开始下注
            if (i == this.bankerIndex) {
                continue;
            }
            action.addCanPushNotePlayer(player.getUid());//不是庄家，就可以下注
        }
        this.addAction(action);
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_PAI_GOW_REBET_BEGIN, null);
    }

    @Override
    protected void beginOpenCardBefore() {
        if (2 == this.opType) {
            this.doSendCard();
        }
        this.curPhase = EPhase.OPEN_CARD;
    }

    @Override
    protected void onOver() {
        this.preDealCards.addAll(this.curDealCards);
        this.onResult();
        // 记录战绩
        this.record();
        this.getRecord().save();
        this.record = null;
        
        this.doSendGameOverLoop();
        this.clearLoop();
        

        for (int i = 0; i < this.playerNum; i++) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (i == this.bankerIndex) {
                continue;
            }
            
            IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) getRoomHandle();
            long boxUid = boxRoomHandle.getBoxUid();
            Box box = BoxManager.I.getBox(boxUid);
            if (box != null) {
                Player tempPlayer = PlayerManager.I.getPlayer(player.getUid());
                // 判断身上竞技值是否小于出场分数
                if (tempPlayer != null && getPlayerGold(player.getUid()) <= getRule().getOrDefault(RoomRule.RR_LEAVEGOLD, 0)) {
                    box.sitUp(tempPlayer);
                    continue;
                }
            }
        }

        if (this.curHotDeskNote <= 0) {
            // 计算实际庄家竞技分
            doBankerGold(this.getScore(this.curHotDeskNote - this.baseBottomScore * (1 + this.keepCount)));
            // 续锅
            if (this.keepCount < this.hotCnt) {
                // 可以续锅
                this.beginHotAgain();
            } else {
                // 结束
                this.onGameOver();
            }
        } else {
            if (getPlayPlayerCount() < playerMinNum) {
                // 计算实际庄家竞技分
                doBankerGold(this.getScore(this.curHotDeskNote - this.baseBottomScore * (1 + this.keepCount)));
                // 快速直接揭锅
                fastHotOut();
            } else {
                // 揭锅
                this.beginHotOut();
            }
        }
    }
    
    private void doBankerGold(int value) {
        IBoxOwner boxOwner = getBoxOwner();
        if (null == boxOwner) {
            return;
        }
        if (-1 != this.bankerIndex) {
            IRoomPlayer bankerPlayer = this.allPlayer[this.bankerIndex];
            if (null != bankerPlayer) {
                IClub mainClub = (IClub) boxOwner;
                boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(bankerPlayer.getUid()),
                        bankerPlayer.getUid(), value, 0);
            }
        }
    }
    
    /**
     * 快速直接揭锅
     */
    private void fastHotOut() {
        PaiGowHotOutAction action = new PaiGowHotOutAction(this, HOT_STOP_TS);
        action.setFive(this.curHotDeskNote >= 5 * this.baseBottomScore);
        this.action.add(action);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_OUT, null);
        this.onHotOut(this.allPlayer[this.bankerIndex].getPlayer(), true);
        
        
        //PCLIPokerNtfPaiGowHotOutInfo info = new PCLIPokerNtfPaiGowHotOutInfo();
        //info.out = true;
        //this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_OUT_INFO, info);
        //this.onHotOutOver(true);
    }

    private void onResult() {
        PaiGowPlayer bankerPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
        List<PaiGowPlayer> playerList = this.getPlayerSortList();
        int preHotDeskNote = this.curHotDeskNote;
        for (int i = 0, len = playerList.size(); i < len; ++i) {
            PaiGowPlayer player = playerList.get(i);
            if (null == player || player.isGuest()) {
                continue;
            }
            if (player.getUid() == bankerPlayer.getUid()) {
                continue;
            }

            int bankerWin = this.compare(bankerPlayer, player);
            int oneRebet = player.getScore(Score.POKER_PAIGOW_ONE_REB, false);
            int twoRebet = player.getScore(Score.POKER_PAIGOW_TWO_REB, false);
            int threeRebet = player.getScore(Score.POKER_PAIGOW_THREE_REB, false);

            if (bankerWin > 0) { // 庄赢
                player.addScore(Score.SCORE, this.getScore(-oneRebet), false);
                player.addScore(Score.POKER_PAIGOW_LOOP_SCORE, this.getScore(-oneRebet), false);
                this.curHotDeskNote += oneRebet;

                if (twoRebet > 0) {
                    boolean twoWin = (0 != bankerPlayer.getOpenCardType()[0] && bankerPlayer.getOpenCardType()[0] <= 68);
                    if (1 == this.paiGowType) {
                        twoWin = twoWin
                                ? (0 != bankerPlayer.getOpenCardType()[1] && bankerPlayer.getOpenCardType()[1] <= 68 ? twoWin : false)
                                : twoWin;
                    }
                    // 第二道是赢家所有组合牌型（小牌九1个，大牌九2个都必须）大于等于8点(杂八)
                    this.curHotDeskNote += twoWin ? twoRebet : 0;
                    player.addScore(Score.SCORE, this.getScore(twoWin ? twoRebet * -1 : 0), false);
                    player.addScore(Score.POKER_PAIGOW_LOOP_SCORE, this.getScore(twoWin ? twoRebet * -1 : 0), false);
                }

                if (threeRebet > 0) {
                    boolean threeWin = (0 != bankerPlayer.getOpenCardType()[0] && bankerPlayer.getOpenCardType()[0] <= 50);
                    if (1 == this.paiGowType) {
                        threeWin = threeWin
                                ? (0 != bankerPlayer.getOpenCardType()[1] && bankerPlayer.getOpenCardType()[1] <= 50 ? threeWin : false)
                                : threeWin;
                    }
                    // 第三道是赢家所有组合牌大于等于9点(杂九)，得分情况看赢家牌型
                    this.curHotDeskNote += threeWin ? threeRebet : 0;
                    player.addScore(Score.SCORE, this.getScore(threeWin ? threeRebet * -1 : 0), false);
                    player.addScore(Score.POKER_PAIGOW_LOOP_SCORE, this.getScore(threeWin ? threeRebet * -1 : 0), false);
                }
            } else if (bankerWin < 0) { // 庄输
                int lostNote = this.curHotDeskNote >= oneRebet ? oneRebet : this.curHotDeskNote;
                player.addScore(Score.SCORE, this.getScore(lostNote), false);
                player.addScore(Score.POKER_PAIGOW_LOOP_SCORE, this.getScore(lostNote), false);
                this.curHotDeskNote += -lostNote;

                if (twoRebet > 0) {
                    boolean twoWin = 0 != player.getOpenCardType()[0] && player.getOpenCardType()[0] <= 68;
                    if (1 == this.paiGowType) {
                        twoWin = twoWin ? (0 != player.getOpenCardType()[1] && player.getOpenCardType()[1] <= 68 ? twoWin : false) : twoWin;
                    }

                    if (this.curHotDeskNote > 0) {
                        lostNote = this.curHotDeskNote >= twoRebet ? twoRebet : this.curHotDeskNote;
                        player.addScore(Score.SCORE, this.getScore(twoWin ? lostNote : 0), false);
                        player.addScore(Score.POKER_PAIGOW_LOOP_SCORE, this.getScore(twoWin ? lostNote : 0), false);
                        this.curHotDeskNote += twoWin ? lostNote * -1 : 0;
                    }
                }

                if (threeRebet > 0) {
                    boolean threeWin = 0 != player.getOpenCardType()[0] && player.getOpenCardType()[0] <= 50;
                    if (1 == this.paiGowType) {
                        threeWin = threeWin
                                ? (0 != player.getOpenCardType()[1] && player.getOpenCardType()[1] <= 50 ? threeWin : false)
                                : threeWin;
                    }

                    if (this.curHotDeskNote > 0) {
                        lostNote = this.curHotDeskNote >= threeRebet ? threeRebet : this.curHotDeskNote;
                        player.addScore(Score.SCORE, this.getScore(threeWin ? lostNote : 0), false);
                        player.addScore(Score.POKER_PAIGOW_LOOP_SCORE, this.getScore(threeWin ? lostNote : 0), false);
                        this.curHotDeskNote += threeWin ? lostNote * -1 : 0;
                    }
                }
            }

            if (player.getScore(Score.POKER_PAIGOW_LOOP_SCORE, false) > 0) {
                player.addScore(Score.ACC_LOST_CNT, 1, true);
            } else if (player.getScore(Score.POKER_PAIGOW_LOOP_SCORE, false) < 0) {
                player.addScore(Score.ACC_WIN_CNT, 1, true);
            }
        }

        int winHotDeskNote = this.curHotDeskNote - preHotDeskNote;
        bankerPlayer.addScore(Score.SCORE, this.getScore(winHotDeskNote), false);
        bankerPlayer.addScore(Score.POKER_PAIGOW_LOOP_SCORE, this.getScore(winHotDeskNote), false);
        if (winHotDeskNote > 0) {
            bankerPlayer.addScore(Score.ACC_WIN_CNT, 1, true);
        } else if (winHotDeskNote < 0) {
            bankerPlayer.addScore(Score.ACC_LOST_CNT, 1, true);
        }
        
        // 竞技值计算处理
        this.getRoomHandle().calculateGold();

        ResultRecordAction resultRecordAction = ((PokerRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }

            int curScore = player.getScore(Score.POKER_PAIGOW_LOOP_SCORE, false);
            ((PaiGowPlayer) player).setMaxScore(curScore);

            ResultRecordAction.GameOverInfo actionGameOverInfo = new ResultRecordAction.GameOverInfo();
            actionGameOverInfo.setBankerIndex(this.bankerIndex);
            actionGameOverInfo.setCurHotDeskNote(this.curHotDeskNote);
            actionGameOverInfo.setCurLoop(this.curLoop);
            actionGameOverInfo.getCard().addAll(player.getHandCard());
            actionGameOverInfo.setScore(this.getClientScore(player.getScore(Score.POKER_PAIGOW_LOOP_SCORE, false)));
            actionGameOverInfo.setTotalScore(this.getClientScore((int) getPlayerGold(player.getUid())));
            resultRecordAction.getAllGameOverInfo().put(player.getUid(), actionGameOverInfo);
        }
    }

    private void doSendGameOverLoop() {
        PCLIPokerNtfPaiGowGameOverInfo gameOverInfo = new PCLIPokerNtfPaiGowGameOverInfo();
        gameOverInfo.next = true;
        gameOverInfo.curHotDeskNote = this.curHotDeskNote;
        gameOverInfo.bankerBureau = this.curLoop;
        gameOverInfo.keepHotCount = this.keepCount;

        for (int i = 0; i < this.playerNum; ++i) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIPokerNtfPaiGowGameOverInfo.GameOverInfo info = new PCLIPokerNtfPaiGowGameOverInfo.GameOverInfo();
            info.score = this.getClientScore(player.getScore(Score.POKER_PAIGOW_LOOP_SCORE, false));
            //info.totalScore = this.getClientScore(player.getScore() + player.getScore(Score.SCORE, false) + m_total);
            info.totalScore = this.getClientScore((int) getPlayerGold(player.getUid()));
            info.bureau = player.getBureau();
            info.card.addAll(player.getOpenCards());

            gameOverInfo.allGameOverInfo.put(player.getUid(), info);
        }

        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_GAME_OVER_LOOP, gameOverInfo);
    }

    /**
     * 开始续锅
     */
    private void beginHotAgain() {
        PaiGowHotAgainAction action = new PaiGowHotAgainAction(this, HOT_AGAIN_TS);
        this.action.add(action);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_AGAIN, null);
    }

    /**
     * 执行续锅
     * @param player
     * @param again
     * @param score
     * @return
     */
    @Override
    public ErrorCode onHotAgain(IPlayer player, boolean again, int score) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法续锅", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法续锅", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (this.bankerIndex != roomPlayer.getIndex()) {
            Logs.ROOM.warn("%s %s 不是庄家, 无法续锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        //身上竞技值小于续锅的锅底分
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
                Logs.ROOM.warn("%s %s 竞技分不足, 无法续锅", this, player);
                return ErrorCode.CLUB_OWNER_NOT_ARENA_VALUE;
            }
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法续锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof PaiGowHotAgainAction) {
            ErrorCode err = ((PaiGowHotAgainAction) action).again(again);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是抢庄动作, 无法续锅", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 续锅结束
     * @param again
     */
    public void onHotAgainOver(boolean again) {
        if (again) {
            ++this.keepCount;
            this.setBanker((PaiGowPlayer) this.allPlayer[this.bankerIndex]);
            this.newLoop();
        } else {
            this.onGameOver();
        }
    }

    /**
     * 开始揭锅
     */
    private void beginHotOut() {
        boolean auto = false;
        //if ((-1 != this.minRebScore && this.curLoop >= this.minRebScore) || this.curHotDeskNote >= 5 * this.baseBottomScore) {
        if ((-1 != this.maxLoop && this.curLoop >= this.maxLoop) || this.curHotDeskNote >= 5 * this.baseBottomScore) {
            auto = true;
        }
        if (auto || 0 == (this.curLoop % 3)) {
            PaiGowHotOutAction action = new PaiGowHotOutAction(this, HOT_STOP_TS);
            action.setFive(this.curHotDeskNote >= 5 * this.baseBottomScore);
            this.action.add(action);
            this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_BEGIN_HOT_OUT, null);
            if (auto) {
                this.onHotOut(this.allPlayer[this.bankerIndex].getPlayer(), true);
            }
        } else {
            this.newLoop();
        }
    }
    
    private int getPlayPlayerCount() {
        int count = 0;
        for (int i = 0; i < this.playerNum; ++i) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            ++count;
        }
        return count;
    }

    @Override
    public ErrorCode onHotOut(IPlayer player, boolean out) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法揭锅", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法揭锅", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (this.bankerIndex != roomPlayer.getIndex()) {
            Logs.ROOM.warn("%s %s 不是庄家, 无法揭锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法揭锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof PaiGowHotOutAction) {
            ErrorCode err = ((PaiGowHotOutAction) action).out(out);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是抢庄动作, 无法揭锅", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 揭锅结束
     * @param out
     */
    public void onHotOutOver(boolean out) {
        if (out) {
            // 计算实际庄家竞技分
            doBankerGold(getScore(this.curHotDeskNote - this.baseBottomScore * (1 + this.keepCount)));
            this.onGameOver();
        } else {
            this.newLoop();
        }
    }

    protected void onGameOver() {
        boolean isNext = this.checkAgain();
        this.gameOver(isNext);
        this.stop();
    }

    @Override
    protected int getShowCurHotDeskNote() {
        return this.curHotDeskNote;
    }

    @Override
    protected String getShowScore(PaiGowPlayer player){
        int score = this.getRecordScore(player);
        if (this.bankerIndex == player.getIndex()) {
            score -= this.getScore(this.baseBottomScore * (1 + this.keepCount));
        }
        return this.getClientScore(score);
    }

    @Override
    protected String getShowAllScore(IPokerPlayer pokerPlayer) {
        return this.getClientScore((int) getPlayerGold(pokerPlayer.getUid()));
    }

    @Override
    protected void fillBankerShowCards(PCLIPokerNtfPaiGowDeskInfo deskInfo) {
        if (this.bankerCard > 0 && this.bankerIndex > 0) {
            PaiGowPlayer bPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
            if (bPlayer.getHandCard().size() > 0) {
                deskInfo.bankerShowCards.addAll(bPlayer.getHandCard().subList(0, this.bankerCard));
            }
        }
    }

    @Override
    public int getDefaultBet(IRoomPlayer player) {
        return Math.min(this.minRebScore, this.curHotDeskNote);
    }

    private void clearLoop() {
        for (int i = 0; i < this.playerNum; ++i) {
            if (null != this.allPlayer[i]) {
                int oldScore = this.allPlayer[i].getScore(Score.SCORE, false);
                this.allPlayer[i].clear();
                this.allPlayer[i].setScore(Score.SCORE, oldScore, false);
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.curLoop = 0;
        this.curPhase = EPhase.INIT;
        this.preDealCards.clear();
        this.curDealCards.clear();
        this.keepCount = 0;
        this.bankerIndex = -1;
    }

    @Override
    protected int getCurPhase() {
        return curPhase.ordinal();
    }

}
