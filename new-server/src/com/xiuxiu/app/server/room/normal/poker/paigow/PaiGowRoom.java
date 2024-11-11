package com.xiuxiu.app.server.room.normal.poker.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfHotPaiGowKeepHotInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfHotPaiGowLeaveBankerInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowDeskInfo;
import com.xiuxiu.app.protocol.client.room.PCLIPaiGowRoomNtfBeginInfoByPoker;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.AutoStartAction;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowRebetAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.PaiGowPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.ResultRecordAction;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;

@GameInfo(gameType = GameType.GAME_TYPE_PAIGOW, gameSubType = 0)
public class PaiGowRoom extends AbstractPaiGowRoom {
    protected int baseRebet;

    protected int robBankerType; // 庄家的选择 1 抢庄， 2 轮流庄  3 霸王庄

    protected int selectScore; // 牌九选分
    protected int minScore; // 最低下注
    protected int fixedScore; // 1 (1 1 1) 2()
    protected int bets;
    protected boolean isOutPaiGow = false;//？？？不知道干嘛用的

    private int ruleKeepHot; // 续锅选择，0-不续，1-续1次

    protected boolean isDealCard = false;

    /**
     * 构造函数
     * @param info
     */
    public PaiGowRoom(RoomInfo info) {
        super(info, ERoomType.NORMAL);
    }

    /**
     * 构造函数
     * @param info
     * @param roomType
     */
    public PaiGowRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    /**
     * 房间初始化
     */
    @Override
    public void init() {
        super.init();
        this.bankerCard = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_BANKERCARD_RULE, 0);
        this.robBankerType = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_BANKER_RULE, 1);
        this.selectScore = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_SELECTSCORE, 1);
        this.minScore = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_MINBETS_RULE, 5);
        this.bets = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_BETS, 1);
        this.fixedScore = this.info.getRule().getOrDefault(RoomRule.RR_PAIGOW_FIXEDSCORE, 1);
        this.baseRebet = selectScore;
        this.timeout = this.getRule().getOrDefault(RoomRule.RR_PAIGOU_OP_TIMER, 30) * 1000;

        //1大牌九 2小牌九 3加锅牌九
        if (1 == this.paiGowType) {
            this.handCardNum = 4;
        } else if (2 == this.paiGowType) {
            this.handCardNum = 2;
        } else if (3 == this.paiGowType) {
            this.handCardNum = this.info.getRule().get(RoomRule.RR_PAIGOW_GAMEPLAY_RULE) == 1 ? 4 : 2;
            this.ruleKeepHot = this.getRule().getOrDefault(RoomRule.RR_PAIGOU_KEEP_HOT, 0);
        }
        //是否自动准备
        this.autoReady = false;
    }

    /**
     * 开局前处理 选庄...等等
     */
    @Override
    protected void doDealPrepare() {
        //加锅牌九
        if (3 == this.paiGowType) {
            if (-1 == this.bankerIndex && this.roomType == ERoomType.NORMAL && -1 != this.info.getOwnerPlayerUid()) {
                this.bankerIndex = this.getRoomPlayer(this.info.getOwnerPlayerUid()).getIndex();
            }
        } else {
            if (3 == this.robBankerType) { // 霸王庄
                if (-1 == this.bankerIndex && -1 != this.info.getOwnerPlayerUid()) {
                    this.bankerIndex = this.getRoomPlayer(this.info.getOwnerPlayerUid()).getIndex();
                }
            } else if (2 == this.robBankerType) { // 轮流庄
                if (-1 != this.bankerIndex && 32 == this.allCard.size()) { // 新牌库，换庄
                    this.bankerIndex = (this.bankerIndex + 1) % this.playerNum;
                }
            }
        }

        if (-1 == this.bankerIndex) {
            this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
        }
        IRoomPlayer banker = this.getRoomPlayer(this.bankerIndex);
        if (banker == null || banker.isGuest()) {
            this.curHotDeskNote = 0;
            for (int i = 0; i < this.playerNum; i++) {
                IRoomPlayer m_roomplayer = this.getRoomPlayer(i);
                if (null == m_roomplayer || m_roomplayer.isGuest()) {
                    continue;
                }
                this.bankerIndex = i;
                banker = this.getRoomPlayer(this.bankerIndex);
            }
        }
        this.preDealCards.clear();
        this.preDealCards.addAll(this.curDealCards);
        this.curDealCards.clear();

        doDealHandCard();
    }

    @Override
    protected void doDealInit() {
        this.crap1 = RandomUtil.random(1, 6);
        this.crap2 = RandomUtil.random(1, 6);
        this.initCardType();
    }

    @Override
    protected void doSendGameStart() {
        // to do nothing...
    }

    private void sendGameStart() {
        PCLIPaiGowRoomNtfBeginInfoByPoker roomBeginInfo = new PCLIPaiGowRoomNtfBeginInfoByPoker();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.hotNote = this.curHotDeskNote;
        for (int i = 0, len = this.allPlayer.length; i < len; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            roomBeginInfo.myIndex = player.getIndex();
            roomBeginInfo.myCards = player.getHandCard();
            roomBeginInfo.bureau = player.getBureau();
            roomBeginInfo.defaultCards = ((PaiGowPlayer) player).getDefaultListCard();
            roomBeginInfo.defaultType = ((PaiGowPlayer) player).getDefaultCardType();
            if (i != this.bankerIndex && 2 != this.paiGowType && 0 != this.bankerCard) {
                int showCardNum = this.bankerCard;
                while (showCardNum > 0) {
                    PaiGowPlayer bPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
                    roomBeginInfo.bankerCards.add(bPlayer.getHandCard().get(showCardNum));
                    --showCardNum;
                }
            }
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
        }

        roomBeginInfo = new PCLIPaiGowRoomNtfBeginInfoByPoker();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.bureau = 0;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.hotNote = this.curHotDeskNote;
        if (2 != this.paiGowType && 0 != this.bankerCard) {
            int showCardNum = this.bankerCard;
            while (showCardNum > 0) {
                PaiGowPlayer bPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
                roomBeginInfo.bankerCards.add(bPlayer.getHandCard().get(showCardNum));
                --showCardNum;
            }
        }
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
        this.isDealCard = true;
    }

    /**
     * 游戏开始
     */
    @Override
    protected void doStart1() {
        this.isOutPaiGow = false;
        this.doSendPreDealCard();
        if (3 == this.paiGowType) { // 加锅牌九
            if (0 == this.curHotDeskNote) {
                this.beginRobBank(16000);
                this.curLoop = 1;
                this.keepCount = 0;
            } else {
                this.sendGameStart();
                this.beginRebet(this.timeout);
                this.curLoop++;
            }
        } else { //其他
            if (1 == this.robBankerType) { //抢庄
                this.bankerIndex = -1;
                this.beginRobBank(16000);
            }
            if (2 == this.robBankerType || 3 == this.robBankerType) { //2轮流庄 3霸王庄
                this.sendGameStart();
                this.beginRebet(this.timeout);
            }
        }
    }

    @Override
    protected void doSetMaxRobBankerAfter() {
        //加锅牌九
        if (3 == this.paiGowType) {
            this.curHotDeskNote = this.baseBottomScore;
            PaiGowPlayer bankerPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
            bankerPlayer.addScore(Score.ACC_TOTAL_SCORE, this.getScore(-this.baseBottomScore), true);
        }
        this.sendGameStart();
        this.beginRebet(this.timeout);
    }

    @Override
    public int getDefaultBet(IRoomPlayer player) {
        return this.selectScore;
    }

    /**
     * 开始下注
     * @param timeout
     */
    @Override
    protected void beginRebet(long timeout) {
        if ((1 == this.paiGowType || 2 == this.paiGowType) && 2 == this.bets) {
            for (int i = 0; i < this.playerNum; ++i) {
                PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (i == this.bankerIndex) {
                    continue;
                }
                player.setScore(Score.POKER_PAIGOW_ONE_REB, this.fixedScore, false);
                player.setScore(Score.POKER_PAIGOW_TWO_REB, this.fixedScore, false);
                player.setScore(Score.POKER_PAIGOW_THREE_REB, this.fixedScore, false);
            }
            this.onRebOver();
        } else {
            PaiGowRebetAction action = new PaiGowRebetAction(this,REBET_OUTTIME + 5000 );
            action.setBase(this.baseRebet);
            for (int i = 0; i < this.playerNum; ++i) {
                PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (i == this.bankerIndex) {
                    continue;
                }
                action.addCanPushNotePlayer(player.getUid());
                player.send(CommandId.CLI_NTF_POKER_PAI_GOW_REBET_BEGIN, null);
            }
            this.addAction(action);
            this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_PAI_GOW_REBET_BEGIN, null);
        }
    }

    /**
     * 请求切锅
     * @param player
     * @return
     */
    @Override
    public ErrorCode onHotOut(IPlayer player, boolean out) {
        if (ERoomState.START == this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间还没结束, 无法切锅", this, player);
            return ErrorCode.ARENA_ALREADY_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法切锅", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (!this.checkAgain()) {
            Logs.ROOM.warn("%s %s 已经结束,", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (3 != this.paiGowType) {
            Logs.ROOM.warn("%s %s 当前玩法不可以切锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        PaiGowPlayer bankerPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
        if (bankerPlayer == null || bankerPlayer.getUid() != player.getUid()) {
            Logs.ROOM.warn("%s %s 请求玩家不是庄家,", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (this.curLoop < 3) {
            Logs.ROOM.warn("%s %s 次数不足三次, 无法切锅", this, player);
            return ErrorCode.OUT_HOT_ROOM_FAIL;
        }
        IAction lastAction = this.action.peek();
        if (!(lastAction instanceof AutoStartAction)
                || !this.roomState.compareAndSet(ERoomState.AUTO_START, ERoomState.AGAIN)) {
            Logs.ROOM.warn("%s %s 当前状态无法切锅,", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        this.action.pop();
        bankerPlayer.addScore(Score.ACC_TOTAL_SCORE, this.getScore(this.curHotDeskNote), true);
        PCLIPokerNtfHotPaiGowLeaveBankerInfo info = new PCLIPokerNtfHotPaiGowLeaveBankerInfo();
        info.playerUid = bankerPlayer.getUid();
        info.score = this.getClientScore(bankerPlayer.getScore(Score.ACC_TOTAL_SCORE, true));
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_OUTHOT_INFO, info);//广播切锅信息
        this.curHotDeskNote = 0;
        this.curLoop = 0;
        this.scheduleGameOver();
        return ErrorCode.OK;
    }

    /**
     * 请求续锅
     * @param player
     * @param again
     * @param score
     * @return
     */
    @Override
    public ErrorCode onHotAgain(IPlayer player, boolean again, int score) {
        long uid = player.getUid();
        if (ERoomState.START == this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间还没结束, 无法续锅", this, uid);
            return ErrorCode.ARENA_ALREADY_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(uid);
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法续锅", this, uid);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        PaiGowPlayer bankerPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
        if (bankerPlayer.getUid() != uid) {
            Logs.ROOM.warn("%s %s 请求玩家不是庄家", this, uid);
            return ErrorCode.REQUEST_INVALID;
        }
        if (!this.checkAgain()) {
            Logs.ROOM.warn("%s %s 已经结束", this, uid);
            return ErrorCode.REQUEST_INVALID;
        }
        if (this.keepCount > 0) {
            Logs.ROOM.warn("%s %s 当前没有续锅次数了", this, uid);
            return ErrorCode.REQUEST_INVALID;
        }
        if (3 != this.paiGowType || 1 > this.ruleKeepHot) {
            Logs.ROOM.warn("%s %s 当前玩法不可以续锅", this, uid);
            return ErrorCode.REQUEST_INVALID;
        }
        if (score == 0) {
            Logs.ROOM.warn("%s %s 庄家选择不续锅，结束游戏 %s", this, uid, score);
            PCLIPokerNtfHotPaiGowKeepHotInfo info = new PCLIPokerNtfHotPaiGowKeepHotInfo();
            info.playerUid = uid;
            info.onScore = score;
            this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_KEEP_INFO, info);
            this.scheduleGameOver();
            return ErrorCode.OK;
        }
        bankerPlayer.addScore(Score.ACC_TOTAL_SCORE, this.getScore(score) * -1, true);
        PCLIPokerNtfHotPaiGowKeepHotInfo info = new PCLIPokerNtfHotPaiGowKeepHotInfo();
        info.playerUid = uid;
        info.onScore = score;
        info.leftScore = this.getClientScore(bankerPlayer.getScore(Score.ACC_TOTAL_SCORE, true));
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_KEEP_INFO, info);//广播续锅信息
        this.curHotDeskNote += score;
        ++this.keepCount;
        return ErrorCode.OK;
    }

    private void doHotDeskNote() {
        if (3 == this.paiGowType) { // 游戏大结束 清理桌面上的积分；
            PaiGowPlayer bankerPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
            if (null != bankerPlayer) {
                bankerPlayer.addScore(Score.ACC_TOTAL_SCORE, this.getScore(this.curHotDeskNote), true);
            }
            this.curHotDeskNote = 0;
        }
    }

    /**
     * 一局游戏结束
     */
    @Override
    protected void onOver() {
        if (3 == this.paiGowType) { //加锅
            this.onHotResult();
        } else {
            this.onResult();
        }
        this.gameOver(this.checkAgain());
        this.stop();
    }

    /**
     * 加锅牌九结算
     */
    private void onHotResult() {
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
            if (bankerWin > 0) {  //庄赢
                player.addScore(Score.SCORE, this.getScore(-oneRebet), false);
                this.curHotDeskNote += oneRebet;
                if (twoRebet > 0) {
                    boolean twoWin = (0 != bankerPlayer.getOpenCardType()[0] && bankerPlayer.getOpenCardType()[0] <= 68);
                    if (1 == this.paiGowType) {
                        twoWin = twoWin ? (0 != bankerPlayer.getOpenCardType()[1] && bankerPlayer.getOpenCardType()[1] <= 68 ? twoWin : false) : twoWin;
                    }
                    //第二道是赢家所有组合牌型（小牌九1个，大牌九2个都必须）大于等于8点(杂八)
                    this.curHotDeskNote += twoWin ? twoRebet : 0;
                    player.addScore(Score.SCORE, this.getScore(twoWin ? twoRebet * -1 : 0), false);
                }
                if (threeRebet > 0) {
                    boolean threeWin = (0 != bankerPlayer.getOpenCardType()[0] && bankerPlayer.getOpenCardType()[0] <= 50);
                    if (1 == this.paiGowType) {
                        threeWin = threeWin ? (0 != bankerPlayer.getOpenCardType()[1] && bankerPlayer.getOpenCardType()[1] <= 50 ? threeWin : false) : threeWin;
                    }
                    //第三道是赢家所有组合牌大于等于9点(杂九)，得分情况看赢家牌型
                    this.curHotDeskNote += threeWin ? threeRebet : 0;
                    player.addScore(Score.SCORE, this.getScore(threeWin ? threeRebet * -1 : 0), false);
                }
            } else if (bankerWin < 0) {  //庄输
                int lostNote = this.curHotDeskNote >= oneRebet ? oneRebet : this.curHotDeskNote;
                player.addScore(Score.SCORE, this.getScore(lostNote), false);
                this.curHotDeskNote += -lostNote;
                if (twoRebet > 0) {
                    boolean twoWin = 0 != player.getOpenCardType()[0] && player.getOpenCardType()[0] <= 68;
                    if (1 == this.paiGowType) {
                        twoWin = twoWin ? (0 != player.getOpenCardType()[1] && player.getOpenCardType()[1] <= 68 ? twoWin : false) : twoWin;
                    }
                    if (this.curHotDeskNote > 0) {
                        lostNote = this.curHotDeskNote >= twoRebet ? twoRebet : this.curHotDeskNote;
                        player.addScore(Score.SCORE, this.getScore(twoWin ? lostNote : 0), false);
                        this.curHotDeskNote += twoWin ? lostNote * -1 : 0;
                    }
                }
                if (threeRebet > 0) {
                    boolean threeWin = 0 != player.getOpenCardType()[0] && player.getOpenCardType()[0] <= 50;
                    if (1 == this.paiGowType) {
                        threeWin = threeWin ? (0 != player.getOpenCardType()[1] && player.getOpenCardType()[1] <= 50 ? threeWin : false) : threeWin;
                    }
                    if (this.curHotDeskNote > 0) {
                        lostNote = this.curHotDeskNote >= threeRebet ? threeRebet : this.curHotDeskNote;
                        player.addScore(Score.SCORE, this.getScore(threeWin ? lostNote : 0), false);
                        this.curHotDeskNote += threeWin ? lostNote * -1 : 0;
                    }
                }
            }
            if (player.getScore(Score.SCORE, false) > 0) {
                player.addScore(Score.ACC_LOST_CNT, 1, true);
            }
            if (player.getScore(Score.SCORE, false) < 0) {
                player.addScore(Score.ACC_WIN_CNT, 1, true);
            }
        }
        int winHotDeskNote = this.curHotDeskNote - preHotDeskNote;
        bankerPlayer.addScore(Score.SCORE, this.getScore(winHotDeskNote), false);
        if (winHotDeskNote > 0) {
            bankerPlayer.addScore(Score.ACC_WIN_CNT, 1, true);
        }
        if (winHotDeskNote < 0) {
            bankerPlayer.addScore(Score.ACC_LOST_CNT, 1, true);
        }
        ResultRecordAction resultRecordAction = ((PokerRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            int curScore = player.getScore(Score.SCORE, false);
            ((PaiGowPlayer) player).setMaxScore(curScore);
            if (bankerPlayer.getUid() != player.getUid()) {
                player.addScore(Score.ACC_TOTAL_SCORE, curScore, true);
            }
            ResultRecordAction.GameOverInfo actionGameOverInfo = new ResultRecordAction.GameOverInfo();
            actionGameOverInfo.getCard().addAll(player.getHandCard());
            actionGameOverInfo.setScore(this.getClientScore(player.getScore(Score.SCORE, false)));
            actionGameOverInfo.setTotalScore(this.getClientScore(player.getScore(Score.ACC_TOTAL_SCORE, true)));
            resultRecordAction.getAllGameOverInfo().put(player.getUid(), actionGameOverInfo);
        }
    }

    /**
     * 非加锅牌九
     */
    private void onResult() {
        PaiGowPlayer bankerPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
        int bankerWinScore = 0;
        for (int i = 0, len = this.allPlayer.length; i < len; ++i) {
            PaiGowPlayer player = (PaiGowPlayer) this.allPlayer[i];
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
            if (bankerWin > 0) {
                player.addScore(Score.SCORE, this.getScore(-oneRebet), false);
                bankerWinScore += oneRebet;
                if (twoRebet > 0) {
                    boolean twoWin = (0 != bankerPlayer.getOpenCardType()[0] && bankerPlayer.getOpenCardType()[0] <= 68);
                    if (1 == this.paiGowType) {
                        twoWin = twoWin ? (0 != bankerPlayer.getOpenCardType()[1] && bankerPlayer.getOpenCardType()[1] <= 68 ? twoWin : false) : twoWin;
                    }
                    //第二道是赢家所有组合牌型（小牌九1个，大牌九2个都必须）大于等于8点(杂八)
                    bankerWinScore += twoWin ? twoRebet : 0;
                    player.addScore(Score.SCORE, this.getScore(twoWin ? twoRebet * -1 : 0), false);
                }
                if (threeRebet > 0) {
                    boolean threeWin = (0 != bankerPlayer.getOpenCardType()[0] && bankerPlayer.getOpenCardType()[0] <= 50);
                    if (1 == this.paiGowType) {
                        threeWin = threeWin ? (0 != bankerPlayer.getOpenCardType()[1] && bankerPlayer.getOpenCardType()[1] <= 50 ? threeWin : false) : threeWin;
                    }
                    //第三道是赢家所有组合牌大于等于9点(杂九)，得分情况看赢家牌型
                    bankerWinScore += threeWin ? threeRebet : 0;
                    player.addScore(Score.SCORE, this.getScore(threeWin ? threeRebet * -1 : 0), false);
                }
            } else if (bankerWin < 0) {
                player.addScore(Score.SCORE, this.getScore(oneRebet), false);
                bankerWinScore += -oneRebet;
                if (twoRebet > 0) {
                    boolean twoWin = 0 != player.getOpenCardType()[0] && player.getOpenCardType()[0] <= 68;
                    if (1 == this.paiGowType) {
                        twoWin = twoWin ? (0 != player.getOpenCardType()[1] && player.getOpenCardType()[1] <= 68 ? twoWin : false) : twoWin;
                    }
                    //第二道是赢家所有组合牌型（小牌九1个，大牌九2个都必须）大于等于8点(杂八)
                    player.addScore(Score.SCORE, this.getScore(twoWin ? twoRebet : 0), false);
                    bankerWinScore += twoWin ? twoRebet * -1 : 0;
                }
                if (threeRebet > 0) {
                    boolean threeWin = 0 != player.getOpenCardType()[0] && player.getOpenCardType()[0] <= 50;
                    if (1 == this.paiGowType) {
                        threeWin = threeWin ? (0 != player.getOpenCardType()[1] && player.getOpenCardType()[1] <= 50 ? threeWin : false) : threeWin;
                    }
                    //第三道是赢家所有组合牌大于等于9点(杂九)，得分情况看赢家牌型
                    player.addScore(Score.SCORE, this.getScore(threeWin ? threeRebet : 0), false);
                    bankerWinScore += threeWin ? threeRebet * -1 : 0;
                }
            }
            if (player.getScore(Score.SCORE, false) > 0) {
                player.addScore(Score.ACC_WIN_CNT, 1, true);
            }
            if (player.getScore(Score.SCORE, false) < 0) {
                player.addScore(Score.ACC_LOST_CNT, 1, true);
            }
        }
        bankerPlayer.addScore(Score.SCORE, this.getScore(bankerWinScore), false);
        if (bankerWinScore > 0) {
            bankerPlayer.addScore(Score.ACC_WIN_CNT, 1, true);
        }
        if (bankerWinScore < 0) {
            bankerPlayer.addScore(Score.ACC_LOST_CNT, 1, true);
        }
        ResultRecordAction resultRecordAction = ((PokerRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            int curScore = player.getScore(Score.SCORE, false);
            ((PaiGowPlayer) player).setMaxScore(curScore);
            player.addScore(Score.ACC_TOTAL_SCORE, curScore, true);
            ResultRecordAction.GameOverInfo actionGameOverInfo = new ResultRecordAction.GameOverInfo();
            actionGameOverInfo.getCard().addAll(player.getHandCard());
            actionGameOverInfo.setScore(this.getClientScore(player.getScore(Score.SCORE, false)));
            actionGameOverInfo.setTotalScore(this.getClientScore(player.getScore(Score.ACC_TOTAL_SCORE, true)));
            resultRecordAction.getAllGameOverInfo().put(player.getUid(), actionGameOverInfo);
        }
    }

    /**
     * 一小局游戏结束
     * @param next
     */
    @Override
    protected void gameOver(boolean next) {
        if (!next) {
            this.doHotDeskNote();
        }
        this.getRoomHandle().calculateGold();//小局结束后抽水

        this.record();
        this.getRecord().save();
    }

    private void scheduleGameOver() {
        this.isOutPaiGow = true;

        this.doSendGameOver(false);
        if (this.autoDestroy) {
            DelayAction action = new DelayAction(this, 30);
            final PaiGowRoom self = this;
            action.setCallback(new ICallback<Object>() {
                @Override
                public void call(Object... o) {
                    self.destroy();
                }
            });
            this.addAction(action);
        }
    }

    /**
     * 检查是否有下已轮
     * @return
     */
    @Override
    protected boolean checkAgain() {
        boolean next = super.checkAgain() && !this.isOutPaiGow;
        if (next && 3 == this.paiGowType) {
            if (this.curHotDeskNote <= 0) { // 锅底输完，直接结束游戏
                next = this.keepCount < this.ruleKeepHot;
            }
        }
        return next;
    }

    @Override
    protected int getShowCurHotDeskNote() {
        return 3 == this.paiGowType ? this.curHotDeskNote : -1;
    }

    @Override
    protected String getShowScore(PaiGowPlayer player){
        return this.getClientScore(player.getScore(Score.SCORE, false));
    }

    /**
     * 清理牌局信息
     */
    @Override
    public void clear() {
        List<Byte> remainedCards = new ArrayList<>(this.allCard);
        super.clear();
        this.allCard.addAll(remainedCards);
        this.isDealCard = false;
    }

    @Override
    protected void doFinish(boolean isNormal, boolean isNewBureau) {
        doHotDeskNote();
        super.doFinish(isNormal, isNewBureau);
    }

    @Override
    protected void fillBankerShowCards(PCLIPokerNtfPaiGowDeskInfo deskInfo) {
        if (this.isDealCard && 2 != this.paiGowType && 0 != this.bankerCard) {
            int showCardNum = this.bankerCard;
            while (showCardNum > 0) {
                PaiGowPlayer bPlayer = (PaiGowPlayer) this.allPlayer[this.bankerIndex];
                deskInfo.bankerShowCards.add(bPlayer.getHandCard().get(showCardNum));
                --showCardNum;
            }
        }
    }

    @Override
    protected String getShowAllScore(IPokerPlayer pokerPlayer) {
        return this.getClientScore(pokerPlayer.getScore(Score.ACC_TOTAL_SCORE, true));
    }

    @Override
    protected void beginOpenCardBefore() {

    }

    @Override
    protected int getCurPhase() {
        return 0;
    }

}
