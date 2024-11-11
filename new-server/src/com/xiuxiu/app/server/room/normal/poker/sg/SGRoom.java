package com.xiuxiu.app.server.room.normal.poker.sg;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.*;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoBySG;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.ILookCard;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.SGLordBankerAction;
import com.xiuxiu.app.server.room.normal.poker.action.SGRebetAction;
import com.xiuxiu.app.server.room.normal.poker.action.SGRobBankerAction;
import com.xiuxiu.app.server.room.normal.poker.action.SGTakeAction;
import com.xiuxiu.app.server.room.normal.poker.fgf.EFGFPlayRule;
import com.xiuxiu.app.server.room.normal.poker.runFast.ERunFastPlayRule;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.FGFPlayer;
import com.xiuxiu.app.server.room.player.poker.SGPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.RecordPokerPlayerBriefInfo;
import com.xiuxiu.app.server.room.record.poker.SGResultRecordAction;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.*;


@GameInfo(gameType = GameType.GAME_TYPE_SG)
public class SGRoom extends PokerRoom implements ILookCard {
    /**
     * 等待客户端显示先发牌的表现时间
     */
    private static final long HOT_SHOW_CARD_TS = 2 * 1000;
    /**
     * 等待抢庄庄时间
     */
    private static final long HOT_ROB_BANK_TS = 7 * 1000;
    /**
     * 等待下注时间
     */
    private static final long HOT_REB_TS = 8 * 1000;
    /**
     * 等待开牌时间
     * 2020-08-22修改15->7
     */
    private static final int HOT_OPEN_CARD_TS = 7 * 1000;
    /**
     * 
     * 快速时间
     */
    private static final int HOT_QUIKE_TS = 2 * 1000;

    /**
     * 金花牌型翻倍分
     */
    private static final int[] FGF_CARD_TYPE_MULS = {1,2,3,4,5,6};

    private Long[] maxRobBanker = null;
    private int maxRobBankerIndex = -1;

    private int prevBankerIndex = -1;          // 上一把庄索引
    private int prevMaxSGPlayerIndex = -1;     // 上一把三公最大牌型的玩家index
    private int prevMaxCardPlayerIndex = -1;   // 上一把最大牌型的玩家index
    private boolean preBankerHasSG = false;    // 上一把庄家是否有三公
    private int cardNum = 0;                   // 扣牌张数，0-全扣，1-扣两张，2-扣一张
    private int bankerType = 0;                // 庄类型  1: 明牌抢庄 2: 自由抢庄  3: 通比玩法 4: 三公当庄 , 5:三公加金花
    private boolean doubling;                  // TODO
    private int sendCardCount = -1;

    private boolean isQuick = false;           // 快速场
    private int idx = 0;                       // 第一个玩家位置

    private byte dealCardOkCnt = 0;            // 明牌抢庄添加是否发头张牌完成
    private boolean isSendRobBanker = false;   // 明牌抢庄 开始抢庄是否已经发给玩家
    private int robLessAreanValue = 100;       // 限制竞技值
    private List<Integer> baseRebet = new ArrayList<>();           // 底分
    private boolean notRobNotPush;             // TODO
    private int maxPushNote;                   // 推注倍数 0: 无, 3: 3倍 5: 5倍 10: 10倍 20: 20倍
    private int pushNoteType;                  // 推注类型；3.无，2闲家推注，1 抢庄推注 4 闲家推注 经典
    private int robBankerMul;                  // 抢庄倍数 1: 1倍, 2: 2倍, 3: 3倍 4: 4倍
    private int cardTypeMulType;               // 三公牌型翻倍類型 0 不翻倍 1 翻倍類型1 2 翻倍類型2

    public SGRoom(RoomInfo info) {
        super(info, ERoomType.NORMAL);
    }

    public SGRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.playerNum = this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_NUM, 8);
        this.playerMinNum = this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_MIN_NUM, 2);
        this.allPlayer = new IRoomPlayer[this.playerNum];
        this.isQuick = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ESGPlayRule.KUAI_SHU.getValue());
        this.cardNum = this.info.getRule().getOrDefault(RoomRule.RR_SG_PORKER_CARD_NUMBER, 0);
        this.bankerType = this.info.getRule().getOrDefault(RoomRule.RR_SG_BANKER_TYPE, 1);
        this.robLessAreanValue = this.info.getRule().getOrDefault(RoomRule.RR_DEPUTY_DIVIDE, 0);
        int endPoint= this.info.getRule().getOrDefault(RoomRule.RR_SG_END_POINT, 1);
        if (bankerType != 3) {
            for (int i = 0; i < 3; i++) {
                int value = endPoint >> (i * 10) & 0X3ff;
                if (value > 0) {
                    this.baseRebet.add(value);
                } else {
                    break;
                }
            }
        }else {
            for (int i = 0; i < 2; i++) {
                int value = endPoint >> (i * 14) & 0X3fff;
                if (value > 0) {
                    this.baseRebet.add(value);
                } else {
                    break;
                }
            }
        }
        this.maxPushNote = this.info.getRule().getOrDefault(RoomRule.RR_SG_PUSH_NOTE_LIMIT, 0);
        if (this.maxPushNote > 0) {
            this.maxPushNote *= this.getBaseRetValue();
        }
        this.pushNoteType = this.info.getRule().getOrDefault(RoomRule.RR_SG_PUSH_NOTE_TYPE, 0);
        this.robBankerMul = this.info.getRule().getOrDefault(RoomRule.RR_SG_ROB_BANKER_MULTIPLE, 1);
        //明牌抢庄固定 发2张；
        if (1 == bankerType || bankerType == 5) {
            cardNum = 2;
        }
        this.cardTypeMulType = this.info.getRule().getOrDefault(RoomRule.RR_SG_CARD_MUL_TYPE, 0);
        this.detectionIP = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ESGPlayRule.RR_DETECTION_IP.getValue());
        this.autoReady = false;
    }

    @Override
    protected void doShuffle() {
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
    protected void doDeal() {
        this.idx = Integer.MAX_VALUE;
//        for (int i = 0; i < 3; ++i) {
//            int start = RandomUtil.random(this.playerNum);
//            for (int j = 0; j < this.playerNum; ++j) {
//                int index = (start + j) % this.playerNum;
//                IPokerPlayer player = (IPokerPlayer) this.allPlayer[index];
//                if (null == player || player.isGuest()) {
//                    continue;
//                }
//                byte card = this.allCard.removeFirst();
//                player.addHandCard(card);
//            }
//        }

        for (int j = 0; j < this.playerNum; ++j) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            for (int i = 0;i<3;i++){
                byte card = this.allCard.removeFirst();
                player.addHandCard(card);
            }
        }



        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PokerUtil.sort(player.getHandCard());
            this.getRecord().addPlayer(new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getBureau()));

            // 玩家加入顺序
            if (this.idx > i) {
                this.idx = i;
            }
        }
    }

    @Override
    protected void doStart1() {
        if (1 == this.bankerType || bankerType == 5) {
            // 1 明牌抢庄
            this.dealCardOkCnt = 0;
            this.isSendRobBanker = false;
            this.sendFirstCard();
        } else if (2 == this.bankerType) {
            // 2 自由抢庄
            this.freedomBanker();
        } else if (3 == this.bankerType) {
            // 3 通比玩法
            this.onRebet();//不用抢庄 自动开始下注；
        } else if (4 == this.bankerType) {
            // 4 三公当庄
            this.sendFirstCard();
            this.fancyBanker();
        }
        this.prevBankerIndex = this.bankerIndex;
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        SGPlayer sgPlayer = (SGPlayer) this.getRoomPlayer(player.getUid());
        if (null == sgPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIPokerNtfSGDeskInfo deskInfo = new PCLIPokerNtfSGDeskInfo();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.curBureau = null == sgPlayer ? 0 : sgPlayer.getBureau();
        deskInfo.sendCardCount = this.sendCardCount;
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        if (null != sgPlayer) {
            if (1 == deskInfo.sendCardCount) {
                deskInfo.card.addAll(sgPlayer.getHandCard().subList(0, this.cardNum));
            } else {
                deskInfo.card.addAll(sgPlayer.getHandCard());
            }
        }
        if (!this.action.isEmpty() && (this.action.peek() instanceof SGRobBankerAction)) {
            deskInfo.bankerIndex = -1;
        }

        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer temp = (IPokerPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            deskInfo.allScore.put(temp.getUid(), this.getClientScore(temp.getScore()));
            deskInfo.allOnlineState.put(temp.getUid(), temp.isOffline() ? false : true);
            deskInfo.allRebet.put(temp.getUid(), temp.getScore(Score.POKER_SG_REBET, false));
            deskInfo.allRobBank.put(temp.getUid(), temp.getScore(Score.POKER_SG_ROB_BANKER_MUL, false));
            deskInfo.isLookCard.put(temp.getUid(), isLookCard(temp.getUid()));
            deskInfo.pushNoteScore.put(temp.getUid(), this.getPushNoteScore((SGPlayer) temp, false));
        }
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }

            PCLIRoomNtfBeginInfoBySG beginInfoBySG = new PCLIRoomNtfBeginInfoBySG();
            beginInfoBySG.bureau = player.getBureau();
            beginInfoBySG.roomBriefInfo = this.getRoomBriefInfo();
            for (int j = 0; j < this.playerNum; ++j) {
                IPokerPlayer tempPlayer = (IPokerPlayer) this.allPlayer[j];
                if (null == tempPlayer || tempPlayer.isGuest()) {
                    continue;
                }
                beginInfoBySG.pushNoteScore.put(tempPlayer.getUid(), this.getPushNoteScore((SGPlayer) tempPlayer, true));
            }
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, beginInfoBySG);
        }
        PCLIRoomNtfBeginInfoBySG beginInfoBySG = new PCLIRoomNtfBeginInfoBySG();
        beginInfoBySG.bureau = 0;
        beginInfoBySG.roomBriefInfo = this.getRoomBriefInfo();
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, beginInfoBySG);
    }

    private boolean isLookCard(long uid) {
        if (!this.action.isEmpty() && this.action.peek() instanceof SGTakeAction) {
            return ((SGTakeAction) this.action.peek()).getLookPlayer(uid);
        }
        return false;
    }

    //发第一手牌；
    private void sendFirstCard() {
        if (-1 == this.bankerIndex || this.allPlayer[this.bankerIndex] == null) {
            this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            while (null == (this.allPlayer[this.bankerIndex]) || this.allPlayer[this.bankerIndex].isGuest()) {
                this.bankerIndex = (++this.bankerIndex) % this.playerNum;
            }
        }
        int sendCardNum = this.cardNum;

        if (sendCardNum != 0) {
            this.sendCardCount = 1;
            for (int j = 0; j < this.playerNum; ++j) {
                SGPlayer player = (SGPlayer) this.allPlayer[(j + this.bankerIndex) % this.playerNum];
                if (null == player || player.isGuest()) {
                    continue;
                }

                PCLIPokerNtfSGHandCardInfo info = new PCLIPokerNtfSGHandCardInfo();
                info.handCard.addAll(player.getHandCard().subList(0, this.cardNum));
                info.sendCardCount = 1;
                player.send(CommandId.CLI_NTF_POKER_SG_DEAL_CARD, info);
            }

            PCLIPokerNtfSGHandCardInfo info = new PCLIPokerNtfSGHandCardInfo();
            info.sendCardCount = 1;
            this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_SG_DEAL_CARD, info);
        }
        if (1 == this.bankerType || bankerType == 5) {//明牌开始抢庄；
            DelayAction action = new DelayAction(this, HOT_SHOW_CARD_TS/*60 * this.playerCnt.get() * sendCardNum*/);
            final SGRoom self = this;
            action.setCallback(new ICallback<Object>() {
                @Override
                public void call(Object... o) {
                    self.showCardRobBanker();
                }
            });
            this.addAction(action);
        }
    }

    //发最后一手牌
    private void sendLastCards() {
        int sendCardNum = 3 - this.cardNum;

        if (sendCardNum != 0) {
            this.sendCardCount = 2;
            for (int j = 0; j < this.playerNum; ++j) {
                SGPlayer player = (SGPlayer) this.allPlayer[(j + this.bankerIndex) % this.playerNum];
                if (null == player || player.isGuest()) {
                    continue;
                }

                PCLIPokerNtfSGHandCardInfo info = new PCLIPokerNtfSGHandCardInfo();
                info.handCard.addAll(player.getHandCard().subList(this.cardNum, 3));
                info.sendCardCount = 2;
                player.send(CommandId.CLI_NTF_POKER_SG_DEAL_CARD, info);
            }

            PCLIPokerNtfSGHandCardInfo info = new PCLIPokerNtfSGHandCardInfo();
            info.sendCardCount = 2;
            this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_SG_DEAL_CARD, info);
        }
        int timeout = HOT_OPEN_CARD_TS;//6000 + (150 * 5 * this.getCurPlayerCnt());
        if (this.isQuick) {
            // 快速
            timeout -= HOT_QUIKE_TS;
        }
        this.setPlayerPokerCardInfo();
        SGTakeAction action = new SGTakeAction(this, timeout);
        for (int i = 0; i < this.playerNum; ++i) {
            SGPlayer player = (SGPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addLookPlayer(player.getUid(), false);
        }
        this.addAction(action);
    }

    private void freedomBanker() {
        // 自由
        SGRobBankerAction action = new SGRobBankerAction(this, this.isQuick ? HOT_ROB_BANK_TS - HOT_QUIKE_TS : HOT_ROB_BANK_TS);
        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer player = this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addRobBanker(player.getUid());
        }
        this.addAction(action);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_SG_ROB_BANKER_BEGIN, null);
    }

    private void fancyBanker() {
        if (-1 != this.prevMaxSGPlayerIndex) {
            this.bankerIndex = this.prevMaxSGPlayerIndex;
        } else {
            this.bankerIndex = (++this.prevBankerIndex) % this.playerNum;
        }

        if (-1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex]) {
            this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            while (null == (this.allPlayer[this.bankerIndex]) || this.allPlayer[this.bankerIndex].isGuest()) {
                this.bankerIndex = (++this.bankerIndex) % this.playerNum;
            }
        }

        PCLIPokerNtfSGRobBankerResultInfo info = new PCLIPokerNtfSGRobBankerResultInfo();
        info.bankerPlayerUid = this.allPlayer[this.bankerIndex].getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_SG_ROB_BANKER_RESULT, info);
        this.onRebet();
    }

    private void showCardRobBanker() {
        if ((1 == this.bankerType  || bankerType == 5) && !isSendRobBanker) {
            SGRobBankerAction action = new SGRobBankerAction(this, HOT_ROB_BANK_TS);
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player = this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                action.addRobBanker(player.getUid());
            }
            this.addAction(action);
            this.broadcast2Client(CommandId.CLI_NTF_POKER_SG_ROB_BANKER_BEGIN, null);
            isSendRobBanker = true;
        }
    }

    public ErrorCode onAddDealCardPlayerOkCnt() {
        if (isSendRobBanker) {
            return ErrorCode.REQUEST_INVALID;
        }
        ++dealCardOkCnt;
        if (dealCardOkCnt == this.getCurPlayerCnt()) {
            showCardRobBanker();
        }
        return ErrorCode.OK;
    }

    public ErrorCode onRobBankerMul(Player player, int mul) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法抢庄", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法抢庄", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        int robBankerMul = this.info.getRule().getOrDefault(RoomRule.RR_SG_ROB_BANKER_MULTIPLE, 1);
        if (mul < 0 || (mul != 0 && mul > robBankerMul)) {
            Logs.ROOM.warn("%s %s 抢庄倍数不对, rule:robBankerMul:%d player mul:%d", this, player, robBankerMul, mul);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法抢庄", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
//        if (roomPlayer instanceof IArenaPlayer) {
//            if (!((IArenaPlayer) roomPlayer).hasArenaValue(this.robLessAreanValue * 100)) {
//                Logs.ROOM.warn("%s %s 竞技值不够, 无法抢庄", this, player);
//                this.setRobBankerFail(player);
//                return ErrorCode.ARENA_LESS_THAN_ROB_BANKER;
//            }
//        }
        
        

        IAction action = this.action.peek();
        if (action instanceof SGRobBankerAction) {
            ErrorCode err = ((SGRobBankerAction) action).selectRobBaker(player.getUid(), mul);
            if (ErrorCode.OK == err) {
                roomPlayer.setScore(Score.POKER_SG_ROB_BANKER_MUL, mul, false);
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是抢庄动作, 无法抢庄", this);
        return ErrorCode.REQUEST_INVALID;
    }

    private void setRobBankerFail(Player player) {
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        IAction action = this.action.peek();
        if (action instanceof SGRobBankerAction) {
            ErrorCode err = ((SGRobBankerAction) action).selectRobBaker(player.getUid(), 0);
            if (ErrorCode.OK == err) {
                roomPlayer.setScore(Score.POKER_SG_ROB_BANKER_MUL, 0, false);
                this.tick();
            }
        }
    }

    public ErrorCode setLordBanker(Player player, int selectState) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法操作", this, player);
            return ErrorCode.ROOM_NOT_START;
        }

        IAction action = this.action.peek();
        if (action instanceof SGLordBankerAction) {
            ErrorCode err = ((SGLordBankerAction) action).setSelectBanker(player.getUid(), selectState);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 当前不是霸王庄选择庄时间", this);
        return ErrorCode.REQUEST_INVALID;
    }

    public void setMaxRobBanker(Long[] maxRobBanker, int max, boolean darkRob) {
        this.maxRobBanker = maxRobBanker;
        this.maxRobBankerIndex = max;
        this.bankerIndex = this.getRoomPlayer(maxRobBanker[RandomUtil.random(0, max - 1)]).getIndex();

        PCLIPokerNtfSGRobBankerResultInfo info = new PCLIPokerNtfSGRobBankerResultInfo();
        if (darkRob) {
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player = this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
            }
        }
        info.bankerPlayerUid = this.allPlayer[this.bankerIndex].getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_SG_ROB_BANKER_RESULT, info);
    }

    private int getPushNoteScore(SGPlayer player, boolean force) {
        if (this.maxPushNote < 1) {
            return 0;
        }
        int score = 0;
        if (this.pushNoteType == 1) { //抢庄推注
            if (player.getScore(Score.POKER_SG_ROB_BANKER_MUL, false) < this.robBankerMul) {
                score = 0;
            } else {
                score = this.maxPushNote;
            }
        } else if (this.pushNoteType == 2) { //闲家推注
            if (this.notRobNotPush && player.getScore(Score.POKER_SG_ROB_BANKER_MUL, false) < 1) {
                score = 0;
            } else {
                if ((player.getIndex() != (force ? this.bankerIndex : this.prevBankerIndex)) && player.isPrevWin()) {
                    score = player.getPrevWinValue() / 100 + this.getBaseRetValue();
                    if (score > this.maxPushNote) {
                        score = this.maxPushNote;
                    }
                    if (score > 0 && !player.getPrePutNote()) {
                    } else {
                        score = 0;
                    }
                }
            }
        }


        return score;
    }

    public void onRebet() {
        if (-1 == this.bankerIndex || this.allPlayer[this.bankerIndex] == null) {
            Logs.ROOM.debug(" this.bankerIndex is -1 ");
            this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            while (null == (this.allPlayer[this.bankerIndex]) || this.allPlayer[this.bankerIndex].isGuest()) {
                this.bankerIndex = (++this.bankerIndex) % this.playerNum;
            }
        }

        if (this.bankerIndex != -1) {
            ((PokerRecord) this.getRecord()).addBankerRecordAction(this.allPlayer[this.bankerIndex].getUid());
        }

        // 下注
        SGRebetAction action = new SGRebetAction(this, HOT_REB_TS);
        this.maxPushNote = (this.pushNoteType == 3) ? 0 : this.maxPushNote;
        int actionBase =  this.getBaseRetValue();
        action.setBase(actionBase);
        for (int i = 0; i < this.playerNum; ++i) {
            SGPlayer player = (SGPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (i == this.bankerIndex && 3 != this.bankerType) {
                continue;
            }
            int score = this.getPushNoteScore(player, false);
            boolean isPushNote = score > 0;

            boolean candoubling = false; //是否可以下注翻倍；
            int bankMul = player.getScore(Score.POKER_SG_ROB_BANKER_MUL, false);
            if (this.doubling && bankMul >= robBankerMul) {
                candoubling = true;
            }

            action.addPushNote(player.getUid(), score);
            action.setDoubling(player.getUid(), candoubling);
            player.setCurPutNote(isPushNote);

            PCLIPokerNtfSGRebetBeginInfo info = new PCLIPokerNtfSGRebetBeginInfo();
            info.doubling = candoubling;
            info.baseRebet = this.getBaseRetValue();
            info.pushNote = score;
            info.isPushNote = isPushNote;
            player.send(CommandId.CLI_NTF_POKER_SG_REBET_BEGIN, info);
        }
        this.addAction(action);
    }

    public ErrorCode onRebet(Player player, int rebet) {
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
        if (roomPlayer.getIndex() == this.bankerIndex && 3 != this.bankerType) {//通比玩法，没有庄家，都可以下注
            Logs.ROOM.warn("%s %s 庄家无法下注, 无法下注", this, player);
            return ErrorCode.ROOM_POKER_COW_BANKER_NOT_REBET;
        }
        if (rebet < 1 || this.getPlayerGold(roomPlayer.getUid()) < rebet) {
            Logs.ROOM.warn("%s %s 下注金额不对, 无法下注", this, player);
            return ErrorCode.REQUEST_INVALID;
        }

        IAction action = this.action.peek();
        if (action instanceof SGRebetAction) {
            ErrorCode err = ((SGRebetAction) action).rebet(player.getUid(), rebet);
            if (ErrorCode.OK == err) {
                roomPlayer.setScore(Score.POKER_SG_REBET, rebet, false);
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是下注动作, 无法下注", this);
        return ErrorCode.REQUEST_INVALID;
    }

    public void onDealCard() {
        Logs.ROOM.debug(" SG onDealCard bankerType " + bankerType);
        if (1 == bankerType || 4 == bankerType || bankerType == 5) {
            // 明牌抢庄、三公当庄 玩法 ；
            this.sendLastCards();
            return;
        }

        this.sendCardCount = 2;

        int timeout = HOT_OPEN_CARD_TS;//6000 + (150 * 5 * this.getCurPlayerCnt());
        if (this.isQuick) {
            // 快速
            timeout -= 2000;
        }

        for (int j = 0; j < this.playerNum; ++j) {
            SGPlayer player = (SGPlayer) this.allPlayer[(j + this.bankerIndex) % this.playerNum];
            if (null == player || player.isGuest()) {
                continue;
            }

            PCLIPokerNtfSGHandCardInfo info = new PCLIPokerNtfSGHandCardInfo();
            info.handCard.addAll(player.getHandCard());
            player.send(CommandId.CLI_NTF_POKER_SG_DEAL_CARD, info);
        }

        PCLIPokerNtfSGHandCardInfo info = new PCLIPokerNtfSGHandCardInfo();
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_SG_DEAL_CARD, info);

        this.setPlayerPokerCardInfo();
        SGTakeAction action = new SGTakeAction(this, timeout);
        for (int i = 0; i < this.playerNum; ++i) {
            SGPlayer player = (SGPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addLookPlayer(player.getUid(), false);
        }
        this.addAction(action);
    }

    private void setPlayerPokerCardInfo() {
        for (int j = 0; j < this.playerNum; ++j) {
            SGPlayer player = (SGPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            this.setPlayerPokerCardInfo(player);
        }
    }

    private void setPlayerPokerCardInfo(SGPlayer player) {
        player.setPrePutNote(player.getCurPutNote());
        player.setCurPutNote(false);
        byte[] cardTypes = this.getCardTypeBySG(player.getHandCard());
        ESGPokerType SgType = ESGPokerType.parse(cardTypes[0]);
        int point = this.getCardPoint(player.getHandCard());
        player.setMaxCardType(SgType, point);

        if(this.bankerType == 5){
            EPokerCardType playerCardType = this.getFGFCardType(player.getHandCard().get(0), player.getHandCard().get(1), player.getHandCard().get(2));
            player.setCurCardTypeExtra(playerCardType);
            long playerUseColorValue = PokerUtil.getFriedGoldenFlowerValue(player.getHandCard().get(0), player.getHandCard().get(1), player.getHandCard().get(2), playerCardType,true);
            player.setUseColorValue(playerUseColorValue);
            long playerUnColorValue = PokerUtil.getFriedGoldenFlowerValue(player.getHandCard().get(0), player.getHandCard().get(1), player.getHandCard().get(2), playerCardType, false);
            player.setUnColorValue(playerUnColorValue);
        }
    }

    public void onOver() {
        this.prevMaxSGPlayerIndex = -1;
        this.prevMaxCardPlayerIndex = -1;
        this.preBankerHasSG = false;
        this.isOver = true;

        if (3 == bankerType) {
            onAllCompare();
        } else {
            onBankCompare();
        }

        this.gameOver(this.checkAgain());
        this.stop();
    }

    @Override
    public ErrorCode look(IPlayer player) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法看牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }

        IPokerPlayer pokerPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == pokerPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法看牌", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }

        PCLIPokerNtfSGCardInfo ntfSGCardInfo = new PCLIPokerNtfSGCardInfo();
        ntfSGCardInfo.card.addAll(((SGPlayer) pokerPlayer).getHandCard());
        ntfSGCardInfo.cardType = ((SGPlayer) pokerPlayer).getCurCardType().getValue();
        player.send(CommandId.CLI_NTF_POKER_LOOK_OK, ntfSGCardInfo);
        return ErrorCode.OK;
    }

    public void addLookPlayer(Player player) {
        IAction action = this.action.peek();
        if (action instanceof SGTakeAction) {
            ((SGTakeAction) action).addLookPlayer(player.getUid(), true);
            this.tick();
        }
    }

    private void onBankCompare() {
        List<SGPlayer> playerList = this.getPlayerSortList();
        Logs.ROOM.warn("%s  当前玩家顺序", playerList);
        SGPlayer bankerPlayer = (SGPlayer) this.allPlayer[this.bankerIndex];
        int bankerRobMultiple = bankerPlayer.getScore(Score.POKER_SG_ROB_BANKER_MUL, false) > 1 ? bankerPlayer.getScore(Score.POKER_SG_ROB_BANKER_MUL, false) : 1;
        SGResultRecordAction action = ((PokerRecord) this.getRecord()).addSGResultRecordAction();
        this.prevMaxCardPlayerIndex = this.bankerIndex;
//        long curArenaValue = 0;
//        if (1 == this.bankerType) {
//            curArenaValue = this.getPlayerGold(bankerPlayer);;
//        }
        for (SGPlayer player : playerList) {
            if (null == player || player.isGuest()) {
                continue;
            }
            if (player.getUid() == bankerPlayer.getUid()) {
                continue;
            }

            if (this.compare(player, (SGPlayer) this.allPlayer[this.prevMaxCardPlayerIndex])) {
                this.prevMaxCardPlayerIndex = player.getIndex();
            }
            if (player.getCurCardType().getValue() >= ESGPokerType.SG_SG.getValue()) {
                if (-1 == this.prevMaxSGPlayerIndex || this.compare(player, (SGPlayer) this.allPlayer[this.prevMaxSGPlayerIndex])) {
                    this.prevMaxSGPlayerIndex = player.getIndex();
                }
            } else {
                this.prevMaxSGPlayerIndex = this.prevBankerIndex;
            }
            int rebet = this.getScore(player.getScore(Score.POKER_SG_REBET, false));
            boolean bankerWin = this.compare(bankerPlayer, player);
            ESGPokerType winCardType = bankerWin ? bankerPlayer.getCurCardType() : player.getCurCardType();
            int multiple = this.getMultiple(winCardType);
            int bankerWinScore = bankerRobMultiple * rebet * multiple * (bankerWin ? 1 : -1);
            int playerWinScore = bankerWinScore * -1;

            if(this.bankerType == 5){
                EPokerCardType cardType = EPokerCardType.FGF_235;
                bankerWin = this.compareFgf(bankerPlayer,player);
                if (bankerWin){
                    cardType = bankerPlayer.getCurCardTypeExtra();
                }else {
                    cardType = player.getCurCardTypeExtra();
                }
                if (cardType == EPokerCardType.FGF_NONE){
                    cardType = EPokerCardType.FGF_235;
                }
                multiple = this.FGF_CARD_TYPE_MULS[cardType.getValue() - EPokerCardType.FGF_235.getValue()];
                int bankerWinScoreTemp = bankerRobMultiple * rebet * multiple * (bankerWin ? 1 : -1);
                playerWinScore += -bankerWinScoreTemp;
                bankerWinScore += bankerWinScoreTemp;
            }

            bankerPlayer.addRebet(bankerWinScore, true);
            player.addRebet(playerWinScore, false);
            player.setPrevWinValue(playerWinScore);//设置分数
            player.setIsPrevWin(playerWinScore > 0);
            action.addResult(player.getUid(), player.getHandCard(),
                    this.getClientScore(player.getScore(Score.SCORE, false)),
                    this.getClientScore(player.getScore(Score.ACC_TOTAL_SCORE, true)), player.getCurCardType().getValue());
//            if (1 == this.bankerType) {
//                if (bankerWinScore < 0) { //庄家输了
//                    if (curArenaValue > bankerWinScore * -1) { //竞技值足够；
//                        curArenaValue -= bankerWinScore * -1;
//                    } else {
//                        bankerWinScore = curArenaValue > 0 ? (int) (curArenaValue * -1) : 0;
//                        curArenaValue = 0;
//                    }
//                } else { //庄家赢了；
//                    curArenaValue += bankerWinScore;
//                }
//            }

//            if (player instanceof ArenaSGPlayer) {
//                if (!((ArenaSGPlayer) player).hasArenaValue(Math.abs(playerWinScore))) {
//                    if (playerWinScore < 0) {
//                        playerWinScore = ((ArenaSGPlayer) player).getArenaValue() * -1;
//                    } else {
//                        playerWinScore = ((ArenaSGPlayer) player).getArenaValue();
//                    }
//                }
//            }
        }

        int bankerScore = bankerPlayer.getScore(Score.SCORE, false);
        if (bankerScore > 0) {
            bankerPlayer.addScore(Score.ACC_WIN_CNT, 1, true);
        } else if (bankerScore < 0) {
            bankerPlayer.addScore(Score.ACC_LOST_CNT, 1, true);
        }
        bankerPlayer.maxScore(Score.ACC_MAX_SCORE, bankerScore, true);

        if (bankerPlayer.getCurCardType().getValue() >= ESGPokerType.SG_SG.getValue()) {
            this.preBankerHasSG = true;
            if (this.prevMaxSGPlayerIndex == -1) {
                this.prevMaxSGPlayerIndex = this.bankerIndex;
            }
        }

        if (preBankerHasSG && this.bankerIndex != this.prevMaxSGPlayerIndex) {
            if (this.compare(bankerPlayer, (SGPlayer) this.allPlayer[this.prevMaxSGPlayerIndex])) {
                this.prevMaxSGPlayerIndex = this.bankerIndex;
            }
        }
        action.addResult(bankerPlayer.getUid(), bankerPlayer.getHandCard(),
                this.getClientScore(bankerPlayer.getScore(Score.SCORE, false)),
                this.getClientScore(bankerPlayer.getScore(Score.ACC_TOTAL_SCORE, true)), bankerPlayer.getCurCardType().getValue());
    }

    public int getMultiple(ESGPokerType type) {
        return type.getMul(this.cardTypeMulType);
    }

    private void onAllCompare() {
        SGResultRecordAction action = ((PokerRecord) this.getRecord()).addSGResultRecordAction();
        int totalRebetValue = 0;
        List<Long> playerUids = new ArrayList<>();
        Map<Long,Integer> winCntMap = new HashMap<>();
        for (int i = 0; i < this.playerNum; ++i) {
            SGPlayer player = (SGPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            playerUids.add(0L);
            winCntMap.put(player.getUid(), winCntMap.getOrDefault(player.getUid(), 0) + 0);
            totalRebetValue += this.getScore(player.getScore(Score.POKER_SG_REBET, false));

            for (int j = i; j < this.playerNum; ++j) {
                SGPlayer tempPlayer = (SGPlayer) this.allPlayer[j];
                if (null == tempPlayer || tempPlayer.isGuest() || player.getUid() == tempPlayer.getUid()) {
                    continue;
                }
                boolean bankerWin = this.compare(player, tempPlayer);
                if (bankerWin){
                    winCntMap.put(player.getUid(), winCntMap.getOrDefault(player.getUid(), 0) + 1);
                }else{
                    winCntMap.put(tempPlayer.getUid(), winCntMap.getOrDefault(tempPlayer.getUid(), 0) + 1);
                }
            }
        }

        for (Map.Entry<Long,Integer> entry : winCntMap.entrySet()) {
            playerUids.set(entry.getValue(),entry.getKey());
        }

        for (int i = playerUids.size() -1;i >= 0; i--) {
            SGPlayer player = (SGPlayer) this.getRoomPlayer(playerUids.get(i));
            int rebetValue = this.getScore(player.getScore(Score.POKER_SG_REBET, false));
            totalRebetValue -= rebetValue;
            int multiple = 1;//this.getMultiple(player.getCurCardType());
            int realValue = Math.min(rebetValue * multiple,totalRebetValue);
            if (realValue > 0){
                totalRebetValue -= realValue;
            }else {
                totalRebetValue = 0;
            }
            player.addRebet(realValue,false);
        }

        for (int j = 0; j < this.playerNum; ++j) {
            SGPlayer sPlayer = (SGPlayer) this.allPlayer[j];
            if (null == sPlayer || sPlayer.isGuest()) {
                continue;
            }
            action.addResult(sPlayer.getUid(), sPlayer.getHandCard(),
                    this.getClientScore(sPlayer.getScore(Score.SCORE, false)),
                    this.getClientScore(sPlayer.getScore(Score.ACC_TOTAL_SCORE, true)), sPlayer.getCurCardType().getValue());
        }
    }

    private List<SGPlayer> getPlayerSortList() {
        SGPlayer bankerPlayer = (SGPlayer) this.allPlayer[this.bankerIndex];
        List<SGPlayer> lostPlayerList = new ArrayList<>();
        List<SGPlayer> winPlayerList = new ArrayList<>();
        for (int j = 0; j < this.playerNum; ++j) {
            SGPlayer player = (SGPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }

            if (bankerPlayer.getCurCardType().getValue() > player.getCurCardType().getValue()) {
                lostPlayerList.add(player);
            } else {
                winPlayerList.add(player);
            }
        }
        winPlayerList.sort(new Comparator<SGPlayer>() {
            @Override
            public int compare(SGPlayer player1, SGPlayer player2) {
                return (int) player2.getCurCardType().getValue() - (int) player1.getCurCardType().getValue();
            }
        });
        lostPlayerList.addAll(winPlayerList);
        return lostPlayerList;
    }

    protected EPokerCardType getFGFCardType(byte a, byte b, byte c) {
        if (PokerUtil.isFriedGoldenFlowerThree(a, b, c)) {
            return EPokerCardType.FGF_THREE;
        } else if (PokerUtil.isFriedGoldenFlowerSameColorLine(a, b, c)) {
            return EPokerCardType.FGF_SAME_COLOR_AND_LINE;
        } else if (PokerUtil.isFriedGoldenFlowerSameColor(a, b, c)) {
            return EPokerCardType.FGF_SAME_COLOR;
        } else if (PokerUtil.isFriedGoldenFlowerLine(a, b, c)) {
            return EPokerCardType.FGF_LINE;
        } else if (PokerUtil.isFriedGoldenFlowerDouble(a, b, c)) {
            return EPokerCardType.FGF_DOUBLE;
        } else {
            return EPokerCardType.FGF_NONE;
        }
    }

    /**
     * 檢查是否不是123牌
     * @param cardOne
     * @param cardTwo
     * @param cardThree
     * @return
     */
    protected boolean checkIs123(byte cardOne,byte cardTwo,byte cardThree){
        for (int i = PokerUtil._A; i <= (PokerUtil._2+1); i++){
            byte value = PokerUtil.getCardValue((byte) i);
            if (PokerUtil.getCardValue(cardOne) != value
                    && PokerUtil.getCardValue(cardTwo) != value
                    && PokerUtil.getCardValue(cardThree) != value){
                return false;
            }
        }
        return true;
    }

    /**
     * 檢查是否不是JQK牌
     * @param cardOne
     * @param cardTwo
     * @param cardThree
     * @return
     */
    protected boolean checkIsJQK(byte cardOne,byte cardTwo,byte cardThree){
        for (int i = PokerUtil._J; i <= PokerUtil._K; i++){
            if (PokerUtil.getCardValue(cardOne) != i
                    && PokerUtil.getCardValue(cardTwo) != i
                    && PokerUtil.getCardValue(cardThree) != i){
                return false;
            }
        }
        return true;
    }


    /**
     * 比較金花牌型
     * @param player
     * @param otherPlayer
     * @return
     */
    protected boolean compareFgf(SGPlayer player, SGPlayer otherPlayer) {
         EPokerCardType playerCardType = player.getCurCardTypeExtra();
         EPokerCardType ohterPlayerCardType = otherPlayer.getCurCardTypeExtra();
//         if(playerCardType == ohterPlayerCardType){
//             if (checkIs123(player.getHandCard().get(0), player.getHandCard().get(1), player.getHandCard().get(2))
//              && checkIsJQK(otherPlayer.getHandCard().get(0), otherPlayer.getHandCard().get(1), otherPlayer.getHandCard().get(2))){
//                 return true;
//             }
//             if (checkIs123(otherPlayer.getHandCard().get(0), otherPlayer.getHandCard().get(1), otherPlayer.getHandCard().get(2))
//                     && checkIsJQK(player.getHandCard().get(0), player.getHandCard().get(1), player.getHandCard().get(2))){
//                 return false;
//             }
//         }
        long playerUseColorValue = player.getUseColorValue();
        long playerUnColorValue = player.getUnColorValue();

        long otherPlayerUseColorValue = otherPlayer.getUseColorValue();
        long otherPlayerUnColorValue = otherPlayer.getUnColorValue();
        if(playerUnColorValue != otherPlayerUnColorValue){
            return playerUnColorValue > otherPlayerUnColorValue;
        }
        return playerUseColorValue > otherPlayerUseColorValue;
    }

    protected boolean compare(SGPlayer player1, SGPlayer player2) {
        ESGPokerType player1CardType = player1.getCurCardType();
        ESGPokerType player2CardType = player2.getCurCardType();

        if (player1CardType.getValue() != player2CardType.getValue()) {
            return player1CardType.getValue() > player2CardType.getValue();
        }

        if (player1CardType.getValue() < ESGPokerType.SG_SG.getValue()) {
            List<Byte> player1CardList = this.getCardPublicCard(player1.getHandCard());
            List<Byte> player2CardList = this.getCardPublicCard(player2.getHandCard());
            if (player1CardList.size() != player2CardList.size()) {
                return player1CardList.size() > player2CardList.size();
            }
        }
        byte player1MaxCard = this.getHandleSingleMaxCard(player1.getHandCard());
        byte player2MaxCard = this.getHandleSingleMaxCard(player2.getHandCard());
        if (((player1MaxCard + 2) % 13) != ((player2MaxCard + 2) % 13)){
            return ((player1MaxCard + 2) % 13) > ((player2MaxCard + 2) % 13);
        }

        return PokerUtil.getCardColor(player1MaxCard) > PokerUtil.getCardColor(player2MaxCard);
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new SGPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void gameOver(boolean next) {
        this.getRoomHandle().calculateGold();
        this.record();
        this.getRecord().save();
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIPokerNtfSGGameOverInfo info = new PCLIPokerNtfSGGameOverInfo();
        info.next = next;
        List<PCLIPokerNtfSGGameOverInfo.GameOverInfo> overInfos = new ArrayList<>();
        for (int j = 0; j < this.playerNum; ++j) {
            SGPlayer player = (SGPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.setLook(false);
            player.setPrevCardType(player.getCurCardType());
            PCLIPokerNtfSGGameOverInfo.GameOverInfo gameOverInfo = new PCLIPokerNtfSGGameOverInfo.GameOverInfo();
            IPlayer iPlayer = player.getPlayer();
            if (null != iPlayer) {
                gameOverInfo.name = iPlayer.getName();
                gameOverInfo.icon = iPlayer.getIcon();
            }
            gameOverInfo.card.addAll(player.getHandCard());
            gameOverInfo.score = this.getClientScore(player.getScore(Score.SCORE, false));
            gameOverInfo.scoreValue = player.getScore(Score.SCORE, false);
            gameOverInfo.totalScore = this.getClientScore(player.getScore());
            gameOverInfo.cardType = player.getCurCardType().getValue();
            gameOverInfo.cardTypeExtra = player.getCurCardTypeExtra().ordinal();
            gameOverInfo.robBankerMul = player.getScore(Score.POKER_SG_ROB_BANKER_MUL, false);
            gameOverInfo.cardDouble = this.getMultiple(player.getCurCardType());
            gameOverInfo.playerUid = player.getUid();
            if (!next) {
                PCLIPokerNtfSGGameOverInfo.TotalCnt totalCnt = new PCLIPokerNtfSGGameOverInfo.TotalCnt();
                totalCnt.maxScore = player.getScore(Score.ACC_MAX_SCORE, true) / 100;
                totalCnt.maxCardType = player.getScore(Score.ACC_POKER_MAX_CARD_TYPE, true);
                totalCnt.winCnt = player.getScore(Score.ACC_WIN_CNT, true);
                totalCnt.lostCnt = player.getScore(Score.ACC_LOST_CNT, true);
                gameOverInfo.totalCnt = totalCnt;
            }
            info.allGameOverInfo.put(player.getUid(), gameOverInfo);
            overInfos.add(gameOverInfo);
        }

        //积分排序 第一 第二。。。
        overInfos.sort(new Comparator<PCLIPokerNtfSGGameOverInfo.GameOverInfo>() {
            @Override
            public int compare(PCLIPokerNtfSGGameOverInfo.GameOverInfo o1, PCLIPokerNtfSGGameOverInfo.GameOverInfo o2) {
                return o2.scoreValue - o1.scoreValue;
            }
        });

        Iterator<PCLIPokerNtfSGGameOverInfo.GameOverInfo> it = overInfos.iterator();
        while (it.hasNext()) {
            PCLIPokerNtfSGGameOverInfo.GameOverInfo gInfo = it.next();
            info.sortScorePlayerUidList.add(gInfo.playerUid);
        }

        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, info);
    }

    @Override
    public boolean canWatch() {
        return true;
    }

    @Override
    protected void firstStart() {
        super.firstStart();
        this.bankerIndex = -1;
    }

    @Override
    public void clear() {
        super.clear();
        this.maxRobBanker = null;
        this.maxRobBankerIndex = -1;
        this.dealCardOkCnt = 0;
        this.isSendRobBanker = false;
        this.sendCardCount = -1;
    }

    private List<Byte> getCardPublicCard(List<Byte> cardList) {
        List<Byte> pCardList = new ArrayList<>();
        if(null == cardList || cardList.size() != 3){
            return pCardList;
        }

        for (int i = 0;i < 3;i++){
            byte cardValue = PokerUtil.getCardValue(cardList.get(i));
            if (cardValue > PokerUtil._10 && cardValue < PokerUtil._A){
                pCardList.add(cardList.get(i));
            }
        }
        return pCardList;
    }

    private byte getHandleSingleMaxCard(List<Byte> cardList) {
        byte maxCard = -1;
        byte maxCardValue = -1;
        for (int i = 0;i < 3;i++){
            byte cardValue = (byte) ((cardList.get(i) + 2) % 13);
            if (cardValue > maxCardValue){
                maxCard = cardList.get(i);
                maxCardValue = cardValue;
                continue;
            }

            if (cardValue == maxCardValue && PokerUtil.getCardColor(cardList.get(i)) > PokerUtil.getCardColor(maxCard)){
                maxCard = cardList.get(i);
                continue;
            }
        }
        return maxCard;
    }

    private byte[] getCardTypeBySG(List<Byte> cardList) {
        if (null != cardList && cardList.size() == 3) {
            PokerUtil.sort(cardList);
            byte c1 = PokerUtil.getCardValue(cardList.get(0));
            byte c2 = PokerUtil.getCardValue(cardList.get(1));
            byte c3 = PokerUtil.getCardValue(cardList.get(2));
            byte maxCard = cardList.get(2);
            if (c1 == c2 && c1 == c3) {
                if (0 == c1) {
                    return new byte[]{ESGPokerType.SG_BAOJIU.getValue(), maxCard};
                }
                if (8 == c1 || 9 == c1 || 10 == c1) {
                    return new byte[]{ESGPokerType.SG_MAXSG.getValue(), maxCard};
                }
                return new byte[]{ESGPokerType.SG_MINSG.getValue(), maxCard};
            }

            if (c1 > 7 && c1 < 11 && c3 < 11) {
                return new byte[]{ESGPokerType.SG_SG.getValue(), maxCard};
            }

            int total = getCardValueBySG(cardList.get(0)) + getCardValueBySG(cardList.get(1)) + getCardValueBySG(cardList.get(2));
            int point = total % 10;
            switch (point) {
                case 0:
                    return new byte[]{ESGPokerType.SG_0DIAN.getValue(), 0};
                case 1:
                    return new byte[]{ESGPokerType.SG_1DIAN.getValue(), 1};
                case 2:
                    return new byte[]{ESGPokerType.SG_2DIAN.getValue(), 2};
                case 3:
                    return new byte[]{ESGPokerType.SG_3DIAN.getValue(), 3};
                case 4:
                    return new byte[]{ESGPokerType.SG_4DIAN.getValue(), 4};
                case 5:
                    return new byte[]{ESGPokerType.SG_5DIAN.getValue(), 5};
                case 6:
                    return new byte[]{ESGPokerType.SG_6DIAN.getValue(), 6};
                case 7:
                    return new byte[]{ESGPokerType.SG_7DIAN.getValue(), 7};
                case 8:
                    return new byte[]{ESGPokerType.SG_8DIAN.getValue(), 8};
                case 9:
                    return new byte[]{ESGPokerType.SG_9DIAN.getValue(), 9};
            }
        }
        return new byte[]{ESGPokerType.NONE.getValue(), -1};
    }

    private byte getCardValueBySG(byte card) {
        byte temp = (byte) ((byte) (card % 13) + 3);
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

    private int getCardPoint(List<Byte> cardList) {
        if (null != cardList && cardList.size() == 3) {
            int total = getCardValueBySG(cardList.get(0)) + getCardValueBySG(cardList.get(1)) + getCardValueBySG(cardList.get(2));
            return total % 10;
        }
        return 0;
    }

	@Override
	protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {
		// TODO Auto-generated method stub
		
	}
    /**
     *检查下注值是否合法
     * @return
     */
    public boolean checkRebValue(IPokerPlayer pokerPlayer, int rebValue,int pushNote){
        if (rebValue <= 0 || rebValue < this.getBaseRetValue()){
            return false;
        }
        if(this.bankerType == 3) {
            if (this.baseRebet.size() != 2) {
                return false;
            }
            if (rebValue >= this.baseRebet.get(0) && rebValue <= this.baseRebet.get(1)) {
                return true;
            }
            return false;
        }else if (this.bankerType == 5){
            return this.baseRebet.contains(rebValue);
        }

        if (this.baseRebet.contains(rebValue)){
            return true;
        }
        if (pushNote == rebValue){
            return true;
        }
        return false;
    }
    /**
     * 获取下注最小值
     * @param pokerPlayer
     * @return
     */
    public int getMinReb(IPokerPlayer pokerPlayer){
        return Math.min(this.getBaseRetValue(),getExchangeGoldForScore(this.getPlayerGold(pokerPlayer.getUid())));
    }
    /**
     * 获取基础下注值
     * @return
     */
    public int getBaseRetValue(){
        return this.baseRebet.size() > 0 ? this.baseRebet.get(0) : 0;
    }
    /**
     * 竞技分兑换游戏分
     * @return
     */
    public int getExchangeGoldForScore(long gold){
        return (int) (gold / (this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 /10));
    }

    @Override
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 * value / 10;
    }
}
