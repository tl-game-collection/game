package com.xiuxiu.app.server.room.normal.mahjong2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfBarInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfBumpInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanOperateInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanTakeInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfEatInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfFumbleInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGangScoreInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfTakeInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJ;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMyHandCardInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.MahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.BarScoreRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.DefaultMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.core.utils.RandomUtil;

public abstract class MahjongRoom extends BaseMahjongRoom implements IMahjongRoom {
    protected static ThreadLocal<byte[]> tempCards = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[MahjongUtil.MJ_CARD_KINDS];
        }
    };

    // 骰子1
    protected int crap1 = -1;
    // 骰子2
    protected int crap2 = -1;
    // 最后摸牌的人
    protected int lastFumbleIndex = -1;
    // 最后摸到的牌
    protected byte lastFumbleCard = -1;
    // 牌桌上打出去的牌
    protected byte[] deskCard = new byte[MahjongUtil.MJ_CARD_KINDS];
    // 杠次数
    protected int barCnt = 0;
    // 当前操作
    protected EActionOp curAction = EActionOp.NORMAL;
    protected int curOpIndex = -1;
    protected byte curCard = -1;
    protected EBarType curBarType = EBarType.NONE;
    // 前一步操作
    protected EActionOp prevAction = EActionOp.NORMAL;
    protected int prevOpIndex = -1;
    protected byte prevCard = -1;
    protected EBarType prevBarType = EBarType.NONE;
    // 前二步操作
    protected EActionOp prevPrevAction = EActionOp.NORMAL;
    protected int prevPrevOpIndex = -1;
    protected byte prevPrevCard = -1;
    protected EBarType prevPrevBarType = EBarType.NONE;

    // 杠后摸牌是否是前摸, 默认是前摸
    protected boolean fumbleWithBarOnFrontend = true;
    // 杠后摸牌次数
    protected int fumbleCntWithBar = 0;
    // 过庄
    protected boolean isOverBanker = false;
    // 开始打牌
    protected boolean isStartTake = false;
    // 癞子
    protected byte laiZiCard = -1;
    // 上一局癞子
    protected byte prevLaiZiCard = -1;

    protected List<BarScoreRecord> barScoreRecord = new ArrayList<>();

    // 通用玩法
    protected int endPoint = 0; // 底分
    protected int fangGangScore = 3; // 放杠分数

    // 最后一张牌
    protected boolean isLastCard = false;

    public MahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public MahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
        this.timeout = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & 0x01) ? 11000 : -1;
    }

    @Override
    protected void doDeal() {
        this.crap1 = RandomUtil.random(1, 6);
        this.crap2 = RandomUtil.random(1, 6);
        this.initBankerIndex();
        for (int i = 0; i < 13; ++i) {
            for (int j = 0; j < this.playerNum; ++j) {
                IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[(this.bankerIndex + j) % this.playerNum];
                if (null == player || player.isGuest()) {
                    continue;
                }
                byte cardValue = this.allCard.removeFirst();
                player.addHandCard(cardValue);
            }
        }
        IMahjongPlayer bankerPlayer = (IMahjongPlayer)this.allPlayer[this.bankerIndex];
        byte cardValue = this.allCard.removeFirst();
        this.lastFumbleCard = cardValue;
        this.lastFumbleIndex = this.bankerIndex;
        this.setCurOp(bankerPlayer, EActionOp.FUMBLE, cardValue);
        bankerPlayer.fumbleCard(cardValue);
        //this.generateTingInfo(bankerPlayer);
        //this.doSendFumble(bankerPlayer, cardValue);
        this.doDealAfter();

        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            ((MahjongRecord)this.getRecord()).addPlayer(player, player.getIndex(), player.getBureau());
        }

        this.sendMyCard();
    }

    protected void sendMyCard() {
        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[j];
            if (null == player || player.isGuest() || player.isOffline()) {
                continue;
            }
            if (Config.checkWhiteHas(player.getUid(), 1)) {
                PCLIRoomNtfMyHandCardInfo info = new PCLIRoomNtfMyHandCardInfo();
                info.rc.addAll(this.allCard);
                for (int i = 0; i < this.allPlayer.length; i++) {
                    IMahjongPlayer m_player = (IMahjongPlayer)this.allPlayer[i];
                    if (null == m_player || m_player.isGuest() || m_player.isOffline()) {
                        continue;
                    }
                    if (player.getUid() == m_player.getUid()) {
                        continue;
                    }
                    List<Byte> m_handCards = new ArrayList<>();
                    m_player.addHandCardTo(m_handCards);
                    info.ohc.put(m_player.getUid(), m_handCards);
                }
                player.send(CommandId.CLI_NTF_ROOM_MY_CARD, info);
            }
        }
    }

    @Override
    public void replaceHandCard(IRoomPlayer player, int card) {
        if (!Config.checkWhiteHas(player.getUid(), 1)) {
            return;
        }
        if (this.isOver) {
            return;
        }
        player.setScore(Score.MJ_NEXT_CARD, card, false);
    }

    /**
     * 初始化庄家
     */
    protected void initBankerIndex() {
//        while (this.bankerIndex < 0 || this.bankerIndex >= this.playerNum || null == this.allPlayer[this.bankerIndex]
//            || this.allPlayer[this.bankerIndex].isGuest()) {
//            this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
//        }
        if (-1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex]) {
            do {
                this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            } while (null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest());
        }
    }

    /**
     * 发牌后处理
     */
    protected void doDealAfter() {

    }

    @Override
    protected void doBeginRecord() {
        // TODO record
    }

    /**
     * 开始打牌
     */
    protected void doStartTake() {
        this.isStartTake = true;
        MahjongPlayer bankerPlayer = (MahjongPlayer)this.allPlayer[this.bankerIndex];
        this.generateTingInfo(bankerPlayer);

        this.doSendStarTake(bankerPlayer);

        MahjongWaitAction waitAction = null;
        if (this.isMustHu()) {
            waitAction = this.getWaitActionWithOnlyHu(bankerPlayer, this.lastFumbleCard);
        }
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            MahjongTakeAction action = this.getTakeAction(bankerPlayer, lastFumbleCard);
            this.addAction(action);
        }
    }

    // private void brightProcess(IMahjongPlayer bankerPlayer,MahjongTakeAction action, byte takeCard) {
    // boolean hu = bankerPlayer.isBright() ? bankerPlayer.getTingInfo().isHuCard(takeCard, takeCard)
    // : this.isHu(bankerPlayer, bankerPlayer.getUid(), lastFumbleCard);
    //
    // if (bankerPlayer.isBright()) {
    // if (hu && isMustHu()) {
    // action.setOp(EActionOp.HU);
    // action.setParam(takeCard);
    // } else {
    // List<Byte> bar = this.barOnFumble(bankerPlayer, takeCard);
    // if (null != bar) {
    // takeCard = bar.get(0);
    // byte index = bankerPlayer.getHandCardIndex(bar.get(0));
    // boolean mustBar = false;
    // for (Byte c : bar) {
    // if (4 == bankerPlayer.getHandCardRaw()[c]) {
    // continue;
    // }
    // mustBar = true;
    // takeCard = c;
    // break;
    // }
    // action.setOp(mustBar ? EActionOp.MUST_BAR : EActionOp.BAR);
    // if (mustBar) {
    // action.setTimeout(2000);
    // }
    // action.setParam(takeCard, index, (byte)(index + (bar.get(0) == takeCard ? 2 : 3)), (byte)0);
    // }
    // }
    // } else {
    // if (bankerPlayer.isHosting(this.timeout)) {
    // Logs.ROOM.warn("%s %s 托管, 不能主动碰,杠吃", this, bankerPlayer);
    // if (hu) {
    // action.setOp(EActionOp.HU);
    // action.setParam(takeCard);
    // }
    // }
    // }
    // }

    // protected List<Byte> barOnFumble(IMahjongPlayer player, byte fumbleCard) {
    // List<Byte> bar = null;
    // if (player.hasBump(fumbleCard)) {
    // // 明杠
    // if (null == bar) {
    // bar = new LinkedList<>();
    // }
    // bar.add(fumbleCard);
    // }
    // for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
    // if (4 == player.getHandCardCnt((byte) i)) {
    //
    // if (null == bar) {
    // bar = new LinkedList<>();
    // }
    // bar.add((byte) i);
    // }
    // }
    // return bar;
    // }

    @Override
    protected void doFinish(boolean isNormal, boolean isNewBureau) {
        Logs.ROOM.debug("%s finish", this);
        if (!isNormal && !isNewBureau) {
            this.onHuangZhuang(false);
        }
        this.info.setEndTime(System.currentTimeMillis());
        this.saveRoomScore();
    }

    @Override
    public void onFumble(IMahjongPlayer player) {
        if (EActionOp.BAR != this.curAction && this.isLastCardSelect() && !this.isLastCard
            && 1 == this.allCard.size()) {
            this.isLastCard = true;
            this.beginLastCard(player);
        } else {
            byte card = -1;
            if (EActionOp.BAR == this.curAction) {
                if (this.fumbleWithBarOnFrontend) {
                    card = this.allCard.removeFirst();
                } else {
                    card = this.allCard.removeLast();
                }
                ++this.fumbleCntWithBar;
            } else {
                card = this.allCard.removeFirst();
            }

            player.fumbleCard(card);
            this.lastFumbleIndex = player.getIndex();
            this.lastFumbleCard = card;
            this.setCurOp(player, EActionOp.FUMBLE, card);

            Logs.ROOM.debug("%s 摸牌的人:%s 摸牌:%s", this, player, MahjongUtil.getCardStr(card));

            this.generateTingInfo(player);

            this.doSendFumble(player, card);

            this.doFumbleAfter(player, card);
        }
    }

    protected void doFumbleAfter(IMahjongPlayer player, byte card) {
        MahjongWaitAction waitAction = null;
        if (this.isMustHu()) {
            waitAction = this.getWaitActionWithOnlyHu(player, card);
        }
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            MahjongTakeAction action = this.getTakeAction(player, card);
            this.addAction(action);
        }
    }

    @Override
    public ErrorCode take(IPlayer player, Object... param) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s 打牌的人:%s 房间还没开始, 无法打牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s 打牌的人:%s 没有动作, 无法打牌", this, player);
            return ErrorCode.ROOM_NOT_ACTION;
        }
        if (param.length < 1) {
            Logs.ROOM.warn("%s 打牌的人:%s 无效参数, 无效打牌", this, player);
            return ErrorCode.ROOM_MJ_INVALID_CARD;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongTakeAction) {
            if (player.getUid() != ((MahjongTakeAction)action).getPlayer().getUid()) {
                Logs.ROOM.warn("%s 打牌的人:%s 当前轮打牌人是:%s 不是你, 无效打牌", this, player,
                    ((MahjongTakeAction)action).getPlayer().getUid());
                return ErrorCode.ROOM_NOT_CUR_PLAYER;
            }
            byte card = (byte)param[0];
            if (card <= 0 || card >= MahjongUtil.MJ_CARD_KINDS) {
                Logs.ROOM.warn("%s 打牌的人:%s 非法牌:%d, 无效打牌", this, player, card);
                return ErrorCode.ROOM_MJ_INVALID_CARD;
            }
            IMahjongPlayer mahjongPlayer = ((MahjongTakeAction)action).getPlayer();
            if (mahjongPlayer.isAutoTake()) {
                Logs.ROOM.warn("%s 打牌的人:%s 主动打 无法手动操作 card:%d, 无效打牌", this, player, card);
                return ErrorCode.ROOM_MJ_AUTO_TAKE;
            }
            if (mahjongPlayer.isOver()) {
                Logs.ROOM.warn("%s 打牌的人:%s 已经结束 无法打牌操作 card:%d, 无效打牌", this, player, card);
                return ErrorCode.ROOM_MJ_OVER;
            }
            if (!this.isCanTakeCard(mahjongPlayer, card)) {
                Logs.ROOM.warn("%s 打牌的人:%s 手牌不足 card:%d, 无效打牌", this, player, card);
                return ErrorCode.ROOM_MJ_HAND_CARD_NOT_ENOUGH;
            }
            if (!mahjongPlayer.canManualTake()) {
                Logs.ROOM.warn("%s 打牌的人:%s 不能手动打牌, 无法打牌", this, player);
                return ErrorCode.ROOM_MJ_ALREADY_LIANG_PAI;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongTakeAction)action).getPlayer().clearOperationTimeoutCnt();
            ((MahjongTakeAction)action).setOp(EActionOp.TAKE);
            ((MahjongTakeAction)action).setParam(param);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 打牌的人:%s 本来不是打牌动作, 无法打牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }

    @Override
    public void onTake(IMahjongPlayer takePlayer, boolean auto, Object... param) {
        byte card = (byte)param[0];

        takePlayer.clearPassCard();
        takePlayer.takeCard(card);
        ++this.deskCard[card];

        if (!this.isOverBanker && this.bankerIndex != takePlayer.getIndex()) {
            this.isOverBanker = true;
        }

        Logs.ROOM.debug("%s 打牌的人:%s 打牌 card:%s auto:%s param:%s", this, takePlayer, MahjongUtil.getCardStr(card), auto,
            Arrays.toString(param));

        this.setCurOp(takePlayer, EActionOp.TAKE, card);
        this.doSendTake(takePlayer, auto, param);

        MahjongWaitAction waitAction = this.getWaitAction(takePlayer, card, false, 1);
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            if (this.onCheckOver()) {
                this.onHuangZhuang(this.curBureau < this.bureau);
            } else {
                this.onFumble((IMahjongPlayer)this.getNextRoomPlayer(takePlayer.getIndex()));
            }
        }
    }

    @Override
    public ErrorCode bump(IPlayer player, Object... param) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s 碰牌的人:%s 房间还没开始, 无法碰牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s 碰牌的人:%s 没有动作, 无法碰牌", this, player);
            return ErrorCode.ROOM_NOT_ACTION;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction)action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s 碰牌的人:%s 没有等待操作, 无效碰牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.getOp()) {
                Logs.ROOM.warn("%s 碰牌的人:%s 已经操作过了 op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            if (waitInfo.isHu() && this.isMustHu()) {
                Logs.ROOM.warn("%s 跳过的人:%s 胡 必须胡op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            if (!waitInfo.isBump()) {
                Logs.ROOM.warn("%s %s 不能碰操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(),
                    waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            byte card = (byte)param[0];
            if (EActionOp.OPEN_BAR != this.curAction && card != this.curCard) {
                Logs.ROOM.warn("%s %s 不能碰操作, 碰的不是当前打出去的牌 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer)this.getRoomPlayer(player.getUid());
            if (!this.isBump(mahjongPlayer, card)) {
                Logs.ROOM.warn("%s 碰牌的人:%s 不能碰操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongWaitAction)action).opWait(waitInfo, EActionOp.BUMP, param);
            if (((MahjongWaitAction)action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 碰牌的人:%s 本来不是碰牌动作, 无法碰牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }

    @Override
    public void onBump(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        player.clearPassCard();
        byte card = (byte)param[0];
        byte index = (byte)param[1];
        takePlayer.delDeskLastCard(card);
        player.addCPG(takePlayer.getIndex(), CPGNode.EType.BUMP, card);
        player.delHandCard(card, 2);
        this.deskCard[card] = 3;

        if (!this.isOverBanker && this.bankerIndex != takePlayer.getIndex()) {
            this.isOverBanker = true;
        }

        Logs.ROOM.debug("%s 碰的人:%s 打牌的人:%s 碰牌 cardValue:%s index:%d", this, player, takePlayer,
            MahjongUtil.getCardStr(card), index);

        this.setCurOp(player, EActionOp.BUMP, card);

        this.generateTingInfo(player);
        this.doSendBump(takePlayer, player, param);

        MahjongTakeAction action = this.getTakeAction(player, this.getCanTakeHandCard(player, (byte)-1));
        this.addAction(action);
    }

    @Override
    public ErrorCode bar(IPlayer player, Object... param) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s 杠牌的人:%s 房间还没开始, 无法杠牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s 杠牌的人:%s 没有动作, 无法杠牌", this, player);
            return ErrorCode.ROOM_NOT_ACTION;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction)action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s 杠牌的人:%s 没有等待操作, 无效杠牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.getOp()) {
                Logs.ROOM.warn("%s 杠牌的人:%s 已经操作过了 op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            if (waitInfo.isHu() && this.isMustHu()) {
                Logs.ROOM.warn("%s 跳过的人:%s 胡 必须胡op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            if (!waitInfo.isBar()) {
                Logs.ROOM.warn("%s 杠牌的人:%s 不能杠操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer)this.getRoomPlayer(player.getUid());
            byte barCard = (byte)param[0];
            if (EActionOp.OPEN_BAR != this.curAction && this.curCard != barCard) {
                Logs.ROOM.warn("%s 杠牌的人:%s 杠的不是当前打出去的牌 不能杠操作2 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            if (!this.isBar1(mahjongPlayer, barCard, true, -1)) {
                Logs.ROOM.warn("%s 杠牌的人:%s 不能杠操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongWaitAction)action).opWait(waitInfo, EActionOp.BAR, param);
            if (((MahjongWaitAction)action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        if (action instanceof MahjongTakeAction) {
            IMahjongPlayer takePlayer = ((MahjongTakeAction)action).getPlayer();
            if (player.getUid() != takePlayer.getUid()) {
                Logs.ROOM.warn("%s 杠牌的人:%s 当前轮杠牌人是:%s 不是你, 无效杠牌", this, player,
                    ((MahjongTakeAction)action).getPlayer().getUid());
                return ErrorCode.ROOM_NOT_CUR_PLAYER;
            }
            byte barCard = (byte)param[0];
            if (takePlayer.isOver()) {
                Logs.ROOM.warn("%s 打牌的人:%s 已经结束 无法打牌操作 card:%d, 无效杠牌", this, player, barCard);
                return ErrorCode.ROOM_MJ_OVER;
            }
            if (!this.isBar1(takePlayer, barCard, false,-1)) {
                Logs.ROOM.warn("%s 杠牌的人:%s 不能杠操作4", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            
            ((MahjongTakeAction)action).setOp(EActionOp.BAR);
            ((MahjongTakeAction)action).setParam(param);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 杠牌的人:%s 本来不是杠牌动作, 无法杠牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }
    
    protected boolean isBar1(IMahjongPlayer player, byte takeCard, boolean fangGang, int type) {
        boolean bar = isBar(player, takeCard, fangGang);
        if (player.isHasTing()) {
            return isCanBar(player, bar, takeCard, fangGang, type);
        }
        return bar;
    }
    private boolean isCanBar(IMahjongPlayer player, boolean bar, byte takeCard, boolean fangGang, int type) {
        MahjongPlayer mahjongPlayer = (MahjongPlayer)player;
        if (takeCard == mahjongPlayer.getTingCardValue()) {
            return false;
        }
        if (type == 1) {
            if (!fangGang) {
                return false;
            }
        } else if (type == 3) {
            // 摸牌可能有两种杠
            if (player.hasBump(takeCard)) {
                return true;
            }
        }
        if (bar) {
            boolean canBar = false;
            CPGNode tempCpg = null;
            try {
                if (fangGang) {
                    player.delHandCard(takeCard, 3);
                    tempCpg = new CPGNode(player.getIndex(), CPGNode.EType.BAR_FANG, takeCard);
                    tempCpg.setTing(player.isHasTing());
                    player.getCPGNode().add(tempCpg);
                } else {
                    player.delHandCard(takeCard, 4);
                    tempCpg = new CPGNode(player.getIndex(), CPGNode.EType.BAR_AN, takeCard);
                    player.getCPGNode().add(tempCpg);
                }
                for (int j = 1; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
                    byte huCard = (byte)j;
                    int fang = getFang(player, huCard);
                    if (fang > 0) {
                        canBar = true;
                        break;
                    }
                }
            } finally {
                if (fangGang) {
                    player.addHandCard(takeCard, 3);
                } else {
                    player.addHandCard(takeCard, 4);
                }
                
                if (tempCpg != null) {
                    player.getCPGNode().remove(tempCpg);
                }
            }

            if (canBar) {
//                HashMap<Byte, HashMap<Byte, Integer>> tingInfo = player.getTingInfo().getTing();
//                List<Byte> listTingInfo = new ArrayList<Byte>();
//                for (Map.Entry<Byte, HashMap<Byte, Integer>> entry : tingInfo.entrySet()) {
//                    if (fangGang) {
//                        if (entry.getKey() == player.getLastTakeCard()) {
//                            HashMap<Byte, Integer> ting1 = entry.getValue();
//                            for (Map.Entry<Byte, Integer> entry1 : ting1.entrySet()) {
//                                listTingInfo.add(entry1.getKey());
//
//                            }
//                        }
//                    } else {
//                        if (entry.getKey() == takeCard) {
//                            HashMap<Byte, Integer> ting1 = entry.getValue();
//                            for (Map.Entry<Byte, Integer> entry1 : ting1.entrySet()) {
//                                listTingInfo.add(entry1.getKey());
//
//                            }
//                        }
//                    }
//
//                }
//                List<Byte> listTing = new ArrayList<Byte>();
//                CPGNode temp;
//                if (fangGang) {
//                    temp = new CPGNode(player.getIndex(), CPGNode.EType.BAR_FANG, takeCard);
//                    player.getCPGNode().add(temp);
//                } else {
//                    temp = new CPGNode(player.getIndex(), CPGNode.EType.BAR_AN, takeCard);
//                    player.getCPGNode().add(temp);
//                }
//                player.delAllHandCard(takeCard);
                this.deskCard[takeCard] = 4;
                
//                HashMap<Byte, HashMap<Byte, Integer>> ting = player.getTingInfo().getTing();
//
//                for (Map.Entry<Byte, HashMap<Byte, Integer>> entry : ting.entrySet()) {
//                    HashMap<Byte, Integer> ting1 = entry.getValue();
//                    for (Map.Entry<Byte, Integer> entry1 : ting1.entrySet()) {
//                        if (!listTing.contains(entry1.getKey())) {
//                            listTing.add(entry1.getKey());
//                        }
//                    }
//                }
                if (fangGang) {
//                    player.addHandCard(takeCard, 3);
                    this.deskCard[takeCard] = 1;
                } else {
//                    player.addHandCard(takeCard, 4);
                    this.deskCard[takeCard] = 0;
                }

//                player.getCPGNode().remove(temp);
//                int count = 0;
//                if (listTing.size() == listTingInfo.size()) {
//                    for (int i = 0; i < listTing.size(); i++) {
//                        for (int j = 0; j < listTingInfo.size(); j++) {
//                            if (listTingInfo.get(j) == listTing.get(i)) {
//                                count++;
//                            }
//                        }
//                    }
//
//                    if (count == listTing.size())
//                        return true;
//                }
            }
            return canBar;
        }
        ///fdas 
        return false;
    }
    
    private void generateTingInfo1(IMahjongPlayer player) {
        TingInfo tingInfo = player.getTingInfo();
        tingInfo.clear();
        for (int j = 1; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
            byte huCard = (byte) j;
            int fang = getFang(player, huCard);
            if (fang > 0) {
                tingInfo.add((byte) 0, huCard, fang, this.getRemainCardCntByPlayer(player, huCard));
            }
        }
        tingInfo.setBuild(true);
    }
    @Override
    public void onBar(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        player.clearPassCard();
        EBarType type = EBarType.NONE;
        byte barCard = (byte)param[0];
        if (takePlayer.getUid() == player.getUid()) {
            // 自己杠
            if (this.canPiBar() && this.isPi(barCard)) {
                // 皮子杠
                type = EBarType.BAR_PI;
                player.addCPG(takePlayer.getIndex(), CPGNode.EType.BAR_PI, barCard);
                player.delHandCard(barCard);
                ++this.deskCard[barCard];
            } else if (this.canLaiZiBar() && this.isLaiZi(barCard)) {
                // 癞子杠
                type = EBarType.BAR_LAIZI;
                player.addCPG(takePlayer.getIndex(), CPGNode.EType.BAR_LAIZI, barCard);
                player.delHandCard(barCard);
                ++this.deskCard[barCard];
            } else {
                if (player.hasBump(barCard)) {
                    // 明杠
                    type = EBarType.BAR_MING;
                    player.setBumpToBar(barCard, CPGNode.EType.BAR_MING);
                } else {
                    type = EBarType.BAR_AN;
                    player.addCPG(takePlayer.getIndex(), CPGNode.EType.BAR_AN, barCard);
                }

                player.delAllHandCard(barCard);
                this.deskCard[barCard] = 4;
            }
        } else {
            // 放杠
            type = EBarType.BAR_FANG;
            takePlayer.delDeskLastCard(barCard);
            player.delHandCard(barCard, 3);
            CPGNode node = player.addCPG(takePlayer.getIndex(), CPGNode.EType.BAR_FANG, barCard);
            node.setTing(takePlayer.isHasTing());
            player.delAllHandCard(barCard);
            this.deskCard[barCard] = 4;
        }

        if (!this.isOverBanker && this.bankerIndex != takePlayer.getIndex()) {
            this.isOverBanker = true;
        }

        Logs.ROOM.debug("%s 杠的人:%s 打牌的人:%s 碰牌 cardValue:%s param:%s", this, player, takePlayer,
            MahjongUtil.getCardStr(barCard), Arrays.toString(param));
        this.setCurOp(player, EActionOp.BAR, barCard, type);

        this.doSendBar(takePlayer, player, type, param);

        this.doBarAfter(takePlayer, player, type, barCard);
    }

    /**
     * 杠后处理
     * 
     * @param takePlayer
     * @param player
     * @param type
     * @param barCard
     */
    protected void doBarAfter(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        MahjongWaitAction waitAction = null;
        if (EBarType.BAR_MING == type) {
            waitAction = this.getWaitAction(player, barCard, true, -1);
        }
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            this.checkBarScore(takePlayer, player, type, barCard);
            if (this.onCheckOver()) {
                this.onHuangZhuang(this.curBureau < this.bureau);
            } else {
                this.onFumble(player);
            }
        }
    }

    @Override
    public ErrorCode eat(IPlayer player, Object... param) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s 吃牌的人:%s 房间还没开始, 无法吃牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s 吃牌的人:%s 没有动作, 无法吃牌", this, player);
            return ErrorCode.ROOM_NOT_ACTION;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction)action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s 吃牌的人:%s 没有等待操作, 无效吃牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (waitInfo.isHu() && this.isMustHu()) {
                Logs.ROOM.warn("%s 跳过的人:%s 胡 必须胡op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.getOp()) {
                Logs.ROOM.warn("%s 吃牌的人:%s 已经操作过了 op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            if (!waitInfo.isEat()) {
                Logs.ROOM.warn("%s 吃牌的人:%s 不能吃操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer)this.getRoomPlayer(player.getUid());
            byte card = (byte)param[0];
            if (EActionOp.OPEN_BAR != this.curAction && this.curCard != card) {
                Logs.ROOM.warn("%s 吃牌的人:%s 吃的不是当前打出去的牌 不能吃操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            byte type = (byte)param[1];
            boolean isEat = false;
            if (1 == type) {
                // 前吃
                isEat = this.isFrontendEat(mahjongPlayer, card);
            } else if (2 == type) {
                // 中吃
                isEat = this.isMiddleEat(mahjongPlayer, card);
            } else if (3 == type) {
                // 后吃
                isEat = this.isBackendEat(mahjongPlayer, card);
            }
            if (!isEat) {
                Logs.ROOM.warn("%s 吃牌的人:%s 不能吃操作2 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongWaitAction)action).opWait(waitInfo, EActionOp.EAT, param);
            if (((MahjongWaitAction)action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 吃牌的人:%s 本来不是吃牌动作, 无法吃牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }

    @Override
    public void onEat(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        player.clearPassCard();
        byte card = (byte)param[0];
        byte type = (byte)param[1];
        takePlayer.delDeskLastCard(card);
        CPGNode.EType eatType = null;
        if (1 == type) {
            eatType = CPGNode.EType.EAT_LEFT;
            player.delHandCard((byte)(card - 2));
            player.delHandCard((byte)(card - 1));
            ++this.deskCard[card - 2];
            ++this.deskCard[card - 1];
        } else if (2 == type) {
            eatType = CPGNode.EType.EAT_MIDDLE;
            player.delHandCard((byte)(card - 1));
            player.delHandCard((byte)(card + 1));
            ++this.deskCard[card - 1];
            ++this.deskCard[card + 1];
        } else if (3 == type) {
            eatType = CPGNode.EType.EAT_RIGHT;
            player.delHandCard((byte)(card + 1));
            player.delHandCard((byte)(card + 2));
            ++this.deskCard[card + 2];
            ++this.deskCard[card + 1];
        }
        player.addCPG(takePlayer.getIndex(), eatType, card);

        if (!this.isOverBanker && this.bankerIndex != takePlayer.getIndex()) {
            this.isOverBanker = true;
        }

        Logs.ROOM.debug("%s 吃的人:%s 打牌的人:%s 吃牌 cardValue:%s type:%s", this, player, takePlayer,
            MahjongUtil.getCardStr(card), eatType);

        this.setCurOp(player, EActionOp.EAT, card);

        this.generateTingInfo(player);
        this.doSendEat(takePlayer, player, param);

        MahjongTakeAction action = this.getTakeAction(player, this.getCanTakeHandCard(player));
        this.addAction(action);
    }

    @Override
    public ErrorCode pass(IPlayer player) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s 跳过的人:%s 房间还没开始, 无法跳过", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s 跳过的人:%s 没有动作, 无法跳过", this, player);
            return ErrorCode.ROOM_NOT_ACTION;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction)action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s 跳过的人:%s 没有等待操作, 无效跳过", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.getOp()) {
                Logs.ROOM.warn("%s 跳过的人:%s 已经操作过了 op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            if (waitInfo.isHu() && this.isMustHu()) {
                Logs.ROOM.warn("%s 跳过的人:%s 胡 必须胡op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer)this.getRoomPlayer(player.getUid());
            mahjongPlayer.clearOperationTimeoutCnt();
            if (waitInfo.isHu()) {
                selectPass(player);
                mahjongPlayer.addPassCard(EActionOp.HU, ((MahjongWaitAction)action).getTakeCard());
            }
            if (waitInfo.isBar()) {
                mahjongPlayer.addPassCard(EActionOp.BAR, ((MahjongWaitAction)action).getTakeCard());
            }
            if (waitInfo.isBump()) {
                mahjongPlayer.addPassCard(EActionOp.BUMP, ((MahjongWaitAction)action).getTakeCard());
            }
            if (waitInfo.isEat()) {
                mahjongPlayer.addPassCard(EActionOp.EAT, ((MahjongWaitAction)action).getTakeCard());
            }
            ((MahjongWaitAction)action).opWait(waitInfo, EActionOp.PASS);
            if (((MahjongWaitAction)action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 跳过的人:%s 本来不是跳过动作, 无法跳过", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }
    
    protected void selectPass(IPlayer player) {
        IRoomPlayer roomPlayer = getRoomPlayer(player.getUid());
        if (roomPlayer != null) {
            MahjongPlayer mahjongPlayer = (MahjongPlayer)roomPlayer;
            mahjongPlayer.setHasPassCard(Boolean.TRUE);
        }
    }

    @Override
    public void onPass() {
        Logs.ROOM.debug("%s 所有人都跳过", this);
        if (EActionOp.BAR == this.curAction) {
            this.checkBarScore((IMahjongPlayer)this.getRoomPlayer(this.prevOpIndex),
                (IMahjongPlayer)this.getRoomPlayer(this.curOpIndex), this.prevBarType, this.curCard);
        }
        if (this.onCheckOver()) {
            this.onHuangZhuang(this.curBureau < this.bureau);
        } else {
            if (EActionOp.BAR == this.curAction) {
                this.onFumble((IMahjongPlayer)this.getRoomPlayer(this.curOpIndex));
            } else {
                this.onFumble((IMahjongPlayer)this.getNextRoomPlayer(this.curOpIndex));
            }
        }
    }

    @Override
    public ErrorCode ting(IPlayer player, Object... param) {
        Logs.ROOM.warn("不支持报听");
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void onTing(IMahjongPlayer takePlayer, boolean auto, Object... param) {}

    @Override
    public void doSendCanTing(IMahjongPlayer player, boolean broadcast) {}

    @Override
    public ErrorCode hu(IPlayer player) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s 胡牌的人:%s 房间还没开始, 无法胡牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s 胡牌的人:%s 没有动作, 无法胡牌", this, player);
            return ErrorCode.ROOM_NOT_ACTION;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction)action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s 胡牌的人:%s 没有等待操作, 无效胡牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.getOp()) {
                Logs.ROOM.warn("%s 胡牌的人:%s 已经操作过了 op:%s", this, player, waitInfo.getOp());
                return ErrorCode.REQUEST_INVALID;
            }
            if (!waitInfo.isHu()) {
                Logs.ROOM.warn("%s 胡牌的人:%s 不能胡操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(),
                    waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer)this.getRoomPlayer(player.getUid());
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongWaitAction)action).opWait(waitInfo, EActionOp.HU);
            if (((MahjongWaitAction)action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        if (action instanceof MahjongTakeAction) {
            IMahjongPlayer takePlayer = ((MahjongTakeAction)action).getPlayer();
            if (player.getUid() != takePlayer.getUid()) {
                Logs.ROOM.warn("%s 胡牌的人:%s 当前轮胡牌人是:%s 不是你, 无效胡牌", this, player,
                    ((MahjongTakeAction)action).getPlayer().getUid());
                return ErrorCode.ROOM_NOT_CUR_PLAYER;
            }
            if (takePlayer.isOver()) {
                Logs.ROOM.warn("%s 打牌的人:%s 已经结束 无法打牌操作 无效胡牌", this, player);
                return ErrorCode.ROOM_MJ_OVER;
            }
            if (!this.isHu(takePlayer)) {
                Logs.ROOM.warn("%s 胡牌的人:%s 不能胡操作4", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ((MahjongTakeAction)action).setOp(EActionOp.HU);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 胡牌的人:%s 本来不是胡牌动作, 无法胡牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, byte huCard) {
        this.onHu(takePlayer, player1, null, huCard);
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, byte huCard) {
        this.onHu(takePlayer, player1, player2, null, huCard);
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3,
        byte huCard) {
        Logs.ROOM.debug("%s 胡 打牌的人:%s 胡牌的人1: %s 胡牌的人2: %s 胡牌的人3: %s 胡牌: %s", this, takePlayer, player1, player2,
            player3, -1 == huCard ? -1 : MahjongUtil.getCardStr(huCard));
        if (EActionOp.TAKE == this.curAction) {
            // 当前是打牌动作, 点炮
            takePlayer.delDeskLastCard(huCard);
        } else if (EActionOp.FUMBLE == this.curAction) {
            // 当前是自己打牌, 自摸
            if (-1 != huCard) {
                player1.takeCardByZiMo(huCard);
                player1.clearPassCard();
                ++this.deskCard[huCard];
            }
        } else if (EActionOp.BAR == this.curAction) {
            // 杠
            // takePlayer.delDeskLastCard();
        }

        if (null != player1) {
            if (-1 != huCard) {
                player1.addHuCard(huCard);
            }
            if (takePlayer.getUid() == player1.getUid()) {
                player1.addScore(Score.ACC_MJ_ZIMO_CNT, 1, true);
            } else {
                takePlayer.addScore(Score.ACC_MJ_DIAN_PAO_CNT, 1, true);
                player1.addScore(Score.ACC_MJ_HU_CNT, 1, true);
            }
            player1.addScore(Score.MJ_CUR_ZIMO_CNT, 1, false);
            player1.addScore(Score.MJ_CUR_HU_CNT, 1, false);
        }
        if (null != player2) {
            if (-1 != huCard) {
                player2.addHuCard(huCard);
            }
            player2.addScore(Score.MJ_CUR_ZIMO_CNT, 1, false);
            player2.addScore(Score.MJ_CUR_HU_CNT, 1, false);
            player2.addScore(Score.ACC_MJ_HU_CNT, 1, true);
        }
        if (null != player3) {
            if (-1 != huCard) {
                player3.addHuCard(huCard);
            }
            player3.addScore(Score.MJ_CUR_ZIMO_CNT, 1, false);
            player3.addScore(Score.MJ_CUR_HU_CNT, 1, false);
            player3.addScore(Score.ACC_MJ_HU_CNT, 1, true);
        }
        if (null != player1) {
            this.setCurOp(player1, EActionOp.HU, huCard);
        }
    }

    /**
     * 打牌
     * 
     * @param player
     * @param defaultTakeCard
     * @return
     */
    protected MahjongTakeAction getTakeAction(IMahjongPlayer player, byte defaultTakeCard) {
        long timeout = player.getTimeout(this.timeout);
        if (this.isLastCard) {
            timeout = BaseMahjongRoom.LAST_CARD_TAKE_TIMEOUT;
        } else if (player.isAutoTake()) {
            timeout = BaseMahjongRoom.AUTO_TAKE_TIMEOUT;
        }
        MahjongTakeAction action = new MahjongTakeAction(this, player, timeout);
        action.setParam(defaultTakeCard);
        return action;
    }

    /**
     * 检查等待
     * 
     * @param player
     * @param takeCard
     * @param onlyHu
     * @param flag
     * @return
     */
    protected MahjongWaitAction getWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu, int flag) {
        MahjongWaitAction waitAction = null;
        int canEatIndex = getNextRoomPlayer(player.getIndex()).getIndex();
        for (int i = 0; i < allPlayer.length; ++i) {
            IMahjongPlayer otherPlayer = (IMahjongPlayer)this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid()
                || otherPlayer.isOver()) {
                continue;
            }
            boolean hu = this.isHu(otherPlayer, player.getUid(), takeCard);
            boolean bar = onlyHu ? false : this.isBar(otherPlayer, takeCard, true);
            boolean bump = onlyHu ? false : bar || this.isBump(otherPlayer, takeCard);
            boolean eat = onlyHu ? false : (i != canEatIndex ? false : this.isEat(otherPlayer, takeCard));
            if (!hu && !bar && !bump && !eat) {
                continue;
            }
            if (this.onCheckOver()) {
                bar = false;
            }
            long timeout = otherPlayer.getTimeout(this.timeout);
            if (hu) {
                if (this.isMustHu()) {
                    timeout = HU_TIMEOUT;
                    bar = false;
                    bump = false;
                    eat = false;
                } else if (otherPlayer.isAutoTake()) {
                    timeout = AUTO_TAKE_TIMEOUT;
                }
            }

            MahjongWaitAction.WaitInfo waitInfo = new MahjongWaitAction.WaitInfo();
            waitInfo.setPlayerUid(otherPlayer.getUid());
            waitInfo.setIndex(otherPlayer.getIndex());
            waitInfo.setTimeout(timeout);
            waitInfo.setHu(hu);
            waitInfo.setBar(bar);
            waitInfo.setBump(bump);
            waitInfo.setEat(eat);
            if (null == waitAction) {
                waitAction = new MahjongWaitAction(this, player);
                waitAction.setTakeCard(takeCard);
            }
            waitAction.addWait(waitInfo);

            this.doSendCanOperate(otherPlayer, hu, bar, bump, eat, takeCard);
        }
        return waitAction;
    }

    protected MahjongWaitAction getWaitActionWithOnlyHu(IMahjongPlayer player, byte takeCard) {
        MahjongWaitAction waitAction = null;
        boolean hu = this.isHu(player, player.getUid(), true, takeCard);
        long timeout = player.getTimeout(this.timeout);
        if (hu && this.isMustHu()) {
            MahjongPlayer mahjongPlayer = (MahjongPlayer)player;
            if (mahjongPlayer.isHasTing()) {
                timeout = 3000;
            } else {
                hu = false;
            }
        }
        if (hu) {
            MahjongWaitAction.WaitInfo waitInfo = new MahjongWaitAction.WaitInfo();
            waitInfo.setPlayerUid(player.getUid());
            waitInfo.setIndex(player.getIndex());
            waitInfo.setTimeout(timeout);
            waitInfo.setHu(hu);
            waitInfo.setBar(false);
            waitInfo.setBump(false);
            waitInfo.setEat(false);
            waitAction = new MahjongWaitAction(this, player);
            waitAction.setTakeCard(takeCard);
            waitAction.addWait(waitInfo);

            // this.doSendCanOperate(player, hu, false, false, false, takeCard);
        }
        return waitAction;
    }

    @Override
    public boolean isMustHu() {
        if (this.isLastCard) {
            return true;
        }
        return false;
    }

    /**
     * 天湖
     * 
     * @param player
     * @return
     */
    protected boolean isTianHu(IMahjongPlayer player) {
        return this.bankerIndex == player.getIndex() && 1 == player.getFumbleCnt() && 14 == player.getHandCardCnt();
    }

    /**
     * 地胡
     * 
     * @param player
     * @return
     */
    protected boolean isDiHu(IMahjongPlayer player) {
        return this.bankerIndex != player.getIndex() && 0 == player.getCPGNodeCnt() && -1 == player.getLastTakeCard();
    }

    /**
     * 判断是否胡牌
     * 
     * @param player
     * @return
     */
    protected boolean isHu(IMahjongPlayer player) {
        return this.isHu(player, player.getUid(), true, this.lastFumbleCard);
    }

    /**
     * 判断是否胡牌
     * 
     * @param player
     * @param takePlayerUid
     * @param addCard
     * @return
     */
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, byte addCard) {
        player.addHandCard(addCard);
        boolean hu = this.isHu(player, takePlayerUid, false, addCard);
        player.delHandCard(addCard);
        return hu;
    }

    /**
     * 判断是否胡牌
     * 
     * @param player
     * @param takePlayerUid
     * @param ziMo
     * @param huCard
     * @return
     */
    protected abstract boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard);

    /**
     *
     * @param player
     * @param takePlayerUid
     * @param ziMo
     * @param addCard
     * @return
     */
    protected boolean isHu0(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte addCard) {
        player.addHandCard(addCard);
        boolean hu = this.isHu(player, takePlayerUid, ziMo, addCard);
        player.delHandCard(addCard);
        return hu;
    }

    /**
     * 判断是否杠
     * 
     * @param player
     * @param takeCard
     * @return
     */
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (fangGang) {
            return player.hasHandCard(takeCard, 3);
        }
        if (player.hasBump(takeCard) && player.hasHandCard(takeCard, 1)) {
            return true;
        }
        return player.hasHandCard(takeCard, 4);
    }

    /**
     * 判断是否碰
     * 
     * @param player
     * @param takeCard
     * @return
     */
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        return player.hasHandCard(takeCard, 2);
    }

    /**
     * 判断是否吃
     * 
     * @param player
     * @param takeCard
     * @return
     */
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        return this.isFrontendEat(player, takeCard) || this.isMiddleEat(player, takeCard)
            || this.isBackendEat(player, takeCard);
    }

    /**
     * 判断是否前吃
     * 
     * @param player
     * @param takeCard
     * @return
     */
    protected boolean isFrontendEat(IMahjongPlayer player, byte takeCard) {
        if (takeCard < MahjongUtil.MJ_3_WANG || takeCard > MahjongUtil.MJ_9_TONG) {
            return false;
        }
        int color = MahjongUtil.getColor(takeCard);
        int tempColor = MahjongUtil.getColor((byte)(takeCard - 2));
        if (tempColor != color) {
            return false;
        }
        return player.hasHandCard((byte)(takeCard - 2), 1) && player.hasHandCard((byte)(takeCard - 1), 1);
    }

    /**
     * 判断是否中吃
     * 
     * @param player
     * @param takeCard
     * @return
     */
    protected boolean isMiddleEat(IMahjongPlayer player, byte takeCard) {
        if (takeCard < MahjongUtil.MJ_2_WANG || takeCard > MahjongUtil.MJ_8_TONG) {
            return false;
        }
        int color = MahjongUtil.getColor(takeCard);
        int tempColor = MahjongUtil.getColor((byte)(takeCard - 1));
        if (tempColor != color) {
            return false;
        }
        tempColor = MahjongUtil.getColor((byte)(takeCard + 1));
        if (color != tempColor) {
            return false;
        }
        return player.hasHandCard((byte)(takeCard - 1), 1) && player.hasHandCard((byte)(takeCard + 1), 1);
    }

    @Override
    public boolean isLaiZi(byte card) {
        return this.laiZiCard == card;
    }

    /**
     * 判断是否后吃
     * 
     * @param player
     * @param takeCard
     * @return
     */
    protected boolean isBackendEat(IMahjongPlayer player, byte takeCard) {
        if (takeCard > MahjongUtil.MJ_7_TONG) {
            return false;
        }
        int color = MahjongUtil.getColor(takeCard);
        int tempColor = MahjongUtil.getColor((byte)(takeCard + 2));
        if (tempColor != color) {
            return false;
        }
        return player.hasHandCard((byte)(takeCard + 2), 1) && player.hasHandCard((byte)(takeCard + 1), 1);
    }

    /**
     * 获取牌型, 一定要在胡之后调用
     * 
     * @param player
     * @return
     */
    protected abstract EPaiXing getPaiXing(IMahjongPlayer player);

    /**
     * 获取番数
     * 
     * @param player
     * @return
     */
    protected abstract int getFang(IMahjongPlayer player);

    /**
     * 获取番数
     * 
     * @param player
     * @param addCard
     * @return
     */
    protected int getFang(IMahjongPlayer player, byte addCard) {
        player.addHandCard(addCard);
        int fang = this.getFang(player);
        player.delHandCard(addCard);
        return fang;
    }

    /**
     * 生成听信息
     * 
     * @param player
     */
    protected void generateTingInfo(IMahjongPlayer player) {
        TingInfo tingInfo = player.getTingInfo();
        tingInfo.clear();

        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (!player.hasHandCard((byte)i, 1)) {
                continue;
            }
            byte takeCard = (byte)i;
            player.delHandCard(takeCard);
            ++this.deskCard[takeCard];

            for (int j = 1; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
                byte huCard = (byte)j;
                if (!canGenerateTingInfo(player, huCard)) {
                    continue;
                }
                int fang = this.getFang(player, huCard);
                if (fang > 0) {
                    tingInfo.add(takeCard, huCard, fang, this.getRemainCardCntByPlayer(player, huCard));
                    addTingInfoLaizhi(player, tingInfo, takeCard);
                }
            }

            player.addHandCard(takeCard);
            --this.deskCard[takeCard];
        }
        tingInfo.setBuild(true);
    }

    protected void addTingInfoLaizhi(IMahjongPlayer player, TingInfo tingInfo, byte takeCard) {

    }

    protected HashMap<Byte, HashMap<Byte, Integer>> getTing(IMahjongPlayer player) {
        return player.getTingInfo().getTing();
    }

    /**
     * 能否生成听信息
     * 
     * @param player
     * @param card
     * @return
     */
    protected boolean canGenerateTingInfo(IMahjongPlayer player, byte card) {
        return true;
    }

    /**
     * 获取剩余牌的数量
     * 
     * @param player
     * @param card
     * @return
     */
    protected int getRemainCardCntByPlayer(IMahjongPlayer player, byte card) {
        return 4 - this.deskCard[card] - player.getHandCardCnt(card);
    }

    /**
     * 检查杠分
     * 
     * @param takePlayer
     * @param player
     * @param type
     */
    protected void checkBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        if (this.isCalcBarScore(takePlayer, player, type, barCard)) {
            this.calcBarScore(takePlayer, player, type);
        }
        if (EBarType.BAR_AN == type) {
            player.addScore(Score.MJ_CUR_AN_GANG_CNT, 1, false);
            player.addScore(Score.ACC_MJ_AN_GANG_CNT, 1, true);
        } else {
            player.addScore(Score.MJ_CUR_MING_GANG_CNT, 1, false);
            if (EBarType.BAR_LAIZI != type && EBarType.BAR_PI != type) {
                player.addScore(Score.ACC_MJ_MING_GANG_CNT, 1, true);
            }
        }
    }

    /**
     * 计算杠分
     * 
     * @param takePlayer
     * @param player
     * @param type
     */
    protected void calcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type) {
        BarScoreRecordAction barScoreRecordAction = ((MahjongRecord)this.getRecord()).addBarScoreRecordAction();

        PCLIMahjongNtfGangScoreInfo info = new PCLIMahjongNtfGangScoreInfo();
        if (EBarType.BAR_FANG == type) {
            // 放杠
            int score = this.fangGangScore;
            this.barScoreRecord.add(new BarScoreRecord(takePlayer.getUid(), player.getUid(), type, score));
            takePlayer.addScore(Score.MJ_CUR_GANG_SCORE, -score, false);
            player.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
            player.addScore(Score.ACC_MJ_MING_GANG_CNT, 1, true);
            info.totalScore.put(takePlayer.getUid(), this.getCurScore(takePlayer));
            info.totalScore.put(player.getUid(), this.getCurScore(player));
            score *= this.endPoint;
            info.gangScore.put(takePlayer.getUid(), -score);
            info.gangScore.put(player.getUid(), score);
            barScoreRecordAction.addBarScore(takePlayer.getUid(), -score);
            barScoreRecordAction.addBarScore(player.getUid(), score);
        } else {
            this.barScoreRecord.add(
                new BarScoreRecord(takePlayer.getUid(), takePlayer.getUid(), type, EBarType.BAR_AN == type ? 2 : 1));
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer temp = (IMahjongPlayer)this.allPlayer[i];
                if (null == temp || temp.isGuest() || temp.isOver()) {
                    continue;
                }
                if (player.getUid() == temp.getUid()) {
                    continue;
                }
                int score = EBarType.BAR_AN == type ? 2 : 1;
                player.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
                info.totalScore.put(player.getUid(), this.getCurScore(player));
                score *= this.endPoint;
                info.gangScore.put(player.getUid(), info.gangScore.getOrDefault(player.getUid(), 0) + score);
                barScoreRecordAction.addBarScore(player.getUid(), score);

                score = EBarType.BAR_AN == type ? -2 : -1;
                temp.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
                info.totalScore.put(temp.getUid(), this.getCurScore(temp));
                score *= this.endPoint;
                info.gangScore.put(temp.getUid(), info.gangScore.getOrDefault(temp.getUid(), 0) + score);
                barScoreRecordAction.addBarScore(temp.getUid(), score);
            }
        }
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_GANG_SCORE_INFO, info);
    }

    /**
     * 设置当前操作状态
     * 
     * @param player
     * @param op
     * @param card
     */
    protected void setCurOp(IMahjongPlayer player, EActionOp op, byte card) {
        this.setCurOp(player, op, card, EBarType.NONE);
    }

    /**
     * 设置当前操作状态
     * 
     * @param player
     * @param op
     * @param card
     * @param barType
     */
    protected void setCurOp(IMahjongPlayer player, EActionOp op, byte card, EBarType barType) {
        this.prevPrevAction = this.prevAction;
        this.prevPrevCard = this.prevCard;
        this.prevPrevOpIndex = this.prevOpIndex;
        this.prevPrevBarType = this.prevBarType;
        this.prevAction = this.curAction;
        this.prevCard = this.curCard;
        this.prevOpIndex = this.curOpIndex;
        this.prevBarType = this.curBarType;
        this.curOpIndex = player.getIndex();
        this.curAction = op;
        this.curCard = card;
        this.curBarType = barType;
    }

    @Override
    public boolean isCurAction(IMahjongPlayer player, EActionOp op) {
        if (op == this.curAction) {
            return this.curOpIndex == player.getIndex();
        }
        return false;
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new DefaultMahjongRecord(this);
        }
        return this.record;
    }

    /**
     * 检查是否结束
     * 
     * @return
     */
    protected boolean onCheckOver() {
        return this.allCard.isEmpty();
    }

    /**
     * 获取当前玩家分数
     * 
     * @param player
     * @return
     */
    protected String getCurScore(IMahjongPlayer player) {
        return this
            .getFormatScore(player.getScore() + player.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100);
    }

    @Override
    public int getCrap1() {
        return this.crap1;
    }

    @Override
    public int getCrap2() {
        return this.crap2;
    }

    @Override
    public byte getLaiZi() {
        return this.laiZiCard;
    }

    /*******************************************************************************************************************
     * 发送客户端信息区域-----
     ******************************************************************************************************************/
    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJ();
            roomBeginInfo.bankerIndex = this.bankerIndex;
            roomBeginInfo.crap1 = this.crap1;
            roomBeginInfo.crap2 = this.crap2;
            roomBeginInfo.myIndex = player.getIndex();
            if (i == this.bankerIndex) {
                player.addHandCardTo(roomBeginInfo.myCards, this.lastFumbleCard);
                roomBeginInfo.myCards.add(this.lastFumbleCard);
            } else {
                player.addHandCardTo(roomBeginInfo.myCards);
            }
            roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
            roomBeginInfo.bureau = player.getBureau();
            roomBeginInfo.d = Config.checkWhiteHas(player.getUid(), 1);
            roomBeginInfo.laiZi = this.laiZiCard;
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
        }
        PCLIRoomNtfBeginInfoByMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJ();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.d = false;
        roomBeginInfo.laiZi = this.laiZiCard;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer)this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIMahjongNtfDeskInfo deskInfo = new PCLIMahjongNtfDeskInfo();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 : mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        deskInfo.bankerPlayerUid = -1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex]
            || this.allPlayer[this.bankerIndex].isGuest() ? -1L : this.allPlayer[this.bankerIndex].getUid();
        deskInfo.laiZi = this.laiZiCard;
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction)this.action.peek()).getRemain();

        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
                for (int i = 0; i < this.playerNum; ++i) {
                    IMahjongPlayer other = (IMahjongPlayer)this.allPlayer[i];
                    if (null == other || other.isGuest()) {
                        continue;
                    }
                    deskInfo.allOnlineState.put(other.getUid(), other.isOffline() ? false : true);
                    PCLIMahjongNtfDeskInfo.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfo.DeskPlayerInfo();
                    deskPlayerInfo.totalScore = this.getFormatScore(
                        other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                    deskPlayerInfo.remainCard = other.getHandCardCnt();
                    for (CPGNode node : other.getCPGNode()) {
                        PCLIMahjongNtfDeskInfo.CardNode cardNode = new PCLIMahjongNtfDeskInfo.CardNode();
                        cardNode.type = node.getType().ordinal();
                        cardNode.playerId = this.allPlayer[node.getTakePlayerIndex()].getUid();
                        if (CPGNode.EType.BUMP == node.getType()) {
                            cardNode.a = cardNode.b = cardNode.c = node.getCard1();
                        } else if (node.isBar()) {
                            cardNode.a = cardNode.b = cardNode.c = cardNode.d = node.getCard1();
                        } else if (CPGNode.EType.EAT_LEFT == node.getType()) {
                            cardNode.a = (byte)(node.getCard1() - 2);
                            cardNode.b = (byte)(node.getCard1() - 1);
                            cardNode.c = node.getCard1();
                        } else if (CPGNode.EType.EAT_RIGHT == node.getType()) {
                            cardNode.a = node.getCard1();
                            cardNode.b = (byte)(node.getCard1() + 1);
                            cardNode.c = (byte)(node.getCard1() + 2);
                        } else if (CPGNode.EType.EAT_MIDDLE == node.getType()) {
                            cardNode.a = (byte)(node.getCard1() - 1);
                            cardNode.b = node.getCard1();
                            cardNode.c = (byte)(node.getCard1() + 1);
                        } else if (CPGNode.EType.ANY_THREE == node.getType()) {
                            cardNode.a = node.getCard1();
                            cardNode.b = node.getCard2();
                            cardNode.c = node.getCard3();
                        }
                        deskPlayerInfo.cpgCard.add(cardNode);
                    }
                    deskPlayerInfo.over = other.isOver();
                    other.addDeskCardTo(deskPlayerInfo.deskCard);
                    if (other.isOver() || player.getUid() == other.getUid()) {
                        other.addHandCardTo(deskPlayerInfo.card, deskPlayerInfo.fumble);
                    }
                    other.addHuCardTo(deskPlayerInfo.huCard);
                    deskInfo.other.put(other.getUid(), deskPlayerInfo);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    /**
     * 通知开始打牌
     * 
     * @param bankerPlayer
     */
    protected void doSendStarTake(IMahjongPlayer bankerPlayer) {
        PCLIMahjongNtfFumbleInfo info = new PCLIMahjongNtfFumbleInfo();
        info.ting = bankerPlayer.isHasTing();
        info.tingInfo.putAll(getTing(bankerPlayer));
        bankerPlayer.send(CommandId.CLI_NTF_MAHJONG_START_TAKE, info);
    }

    @Override
    public void doSendFumble(IMahjongPlayer player, byte card) {
        PCLIMahjongNtfFumbleInfo info = new PCLIMahjongNtfFumbleInfo();
        info.uid = player.getUid();
        info.index = player.getIndex();
        info.auto = true;
        info.value = card;
        info.remainCard = this.allCard.size();
        player.addHandCardTo(info.handCard, card);
        info.tingInfo.putAll(getTing(player));
        player.send(CommandId.CLI_NTF_MAHJONG_FUMBLE, info);

        info = new PCLIMahjongNtfFumbleInfo();
        info.uid = player.getUid();
        info.index = player.getIndex();
        info.auto = true;
        info.remainCard = this.allCard.size();
        info.value = -1;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_FUMBLE, info, player.getUid());

        ((MahjongRecord)this.getRecord()).addFumbleRecordAction(player.getUid(), card);
    }

    /**
     * 通知打牌信息
     * 
     * @param player
     * @param param
     */
    public void doSendTake(IMahjongPlayer player, boolean auto, Object... param) {
        byte card = (byte)param[0];
        byte last = (byte)param[1];
        byte index = (byte)param[2];
        byte outputCardIndex = (byte)param[3];
        int length = (int)param[4];
        PCLIMahjongNtfTakeInfo info;
        if (auto) {
            info = new PCLIMahjongNtfTakeInfo();
            info.uid = player.getUid();
            info.cardValue = card;
            info.isLast = last;
            info.outputCardIndex = outputCardIndex;
            info.length = length;
            info.index = index;
            info.auto = auto;
            player.addDeskCardTo(info.myDeskCard);
            player.addHandCardTo(info.myHandCard);
            player.send(CommandId.CLI_NTF_MAHJONG_TAKE, info);
        }
        info = new PCLIMahjongNtfTakeInfo();
        info.uid = player.getUid();
        info.cardValue = card;
        info.isLast = last;
        info.outputCardIndex = outputCardIndex;
        info.length = length;
        info.index = index;
        info.auto = auto;
        if (auto) {
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_TAKE, info, player.getUid());
        } else {
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_TAKE, info);
        }

        ((MahjongRecord)this.getRecord()).addTakeRecordAction(player.getUid(), card);
    }

    @Override
    public void doSendCanTake(IMahjongPlayer player, boolean broadcast) {
        PCLIMahjongNtfCanTakeInfo info = new PCLIMahjongNtfCanTakeInfo();
        info.uid = player.getUid();
        if (broadcast) {
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_CAN_TAKE, info);
        } else {
            player.send(CommandId.CLI_NTF_MAHJONG_CAN_TAKE, info);
        }
    }

    /**
     * 通知碰牌信息
     * 
     * @param takePlayer
     * @param player
     * @param param
     */
    public void doSendBump(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        byte card = (byte)param[0];
        byte index = (byte)param[1];
        PCLIMahjongNtfBumpInfo info = new PCLIMahjongNtfBumpInfo();
        info.uid = player.getUid();
        info.takeUid = takePlayer.getUid();
        info.cardValue = this.curCard;
        info.index = index;
        info.tingInfo.putAll(getTing(player));
        info.ting = player.isHasTing();
        player.send(CommandId.CLI_NTF_MAHJONG_BUMP, info);
        info = new PCLIMahjongNtfBumpInfo();
        info.uid = player.getUid();
        info.takeUid = takePlayer.getUid();
        info.cardValue = this.curCard;
        info.index = index;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BUMP, info, player.getUid());
        ((MahjongRecord)this.getRecord()).addBumpRecordAction(player.getUid(), takePlayer.getUid(), (Byte)param[0],false,null);
        // boolean bright = false;
        // boolean ting = false;
        // MahjongPlayer bumpPlayer = (MahjongPlayer)player;
        // if (this.switchBright && !bumpPlayer.isBright()) {
        // BrightInfo brightInfo = bumpPlayer.getBrightInfo();
        // bright = bumpPlayer.isBright() ? false : this.isBright(brightInfo.child.size() > 0);
        // ting = true;
        // }
        //
        //((MahjongRecord)this.getRecord()).addBumpRecordAction(player.getUid(), takePlayer.getUid(),param[0],false,null);
        // bright, ting ? bumpPlayer.getBrightInfo().copy() : null);
    }

    /**
     * 是否可以亮牌
     * 
     * @param bright
     * @return
     */
    protected boolean isBright(boolean bright) {
        return false;
    }

    /**
     * 通知吃信息
     * 
     * @param takePlayer
     * @param player
     * @param param
     */
    public void doSendEat(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        byte card = (byte)param[0];
        byte type = (byte)param[1];
        PCLIMahjongNtfEatInfo info = new PCLIMahjongNtfEatInfo();
        info.uid = player.getUid();
        info.takeUid = takePlayer.getUid();
        info.cardValue = this.curCard;
        info.type = type;
        info.tingInfo.putAll(getTing(player));
        player.send(CommandId.CLI_NTF_MAHJONG_EAT, info);

        info = new PCLIMahjongNtfEatInfo();
        info.uid = player.getUid();
        info.takeUid = takePlayer.getUid();
        info.cardValue = this.curCard;
        info.type = type;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_EAT, info, player.getUid());

        ((MahjongRecord)this.getRecord()).addEatRecordAction(player.getUid(), takePlayer.getUid(), this.curCard, type);
    }

    /**
     * 通知杠信息
     * 
     * @param takePlayer
     * @param player
     * @param type
     * @param param
     */
    public void doSendBar(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, Object... param) {
        PCLIMahjongNtfBarInfo info = new PCLIMahjongNtfBarInfo();
        info.uid = player.getUid();
        info.takeUid = takePlayer.getUid();
        info.type = type.ordinal();
        info.cardValue = (byte)param[0];
        info.startIndex = (byte)param[1];
        info.endIndex = (byte)param[2];
        info.insertIndex = (byte)param[3];
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BAR, info);

        ((MahjongRecord)this.getRecord()).addBarRecordAction(player.getUid(), takePlayer.getUid(), (Byte)param[0]);
    }

    @Override
    public void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat,
        byte takeCard) {
        PCLIMahjongNtfCanOperateInfo info = new PCLIMahjongNtfCanOperateInfo(bump, bar, hu, eat, takeCard);
        player.send(CommandId.CLI_NTF_MAHJONG_CAN_OPERATE, info);
    }

    @Override
    public void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat,
        List<Byte> takeCard) {
        PCLIMahjongNtfCanOperateInfo info = new PCLIMahjongNtfCanOperateInfo(bump, bar, hu, eat, takeCard);
        player.send(CommandId.CLI_NTF_MAHJONG_CAN_OPERATE, info);
    }

    /*******************************************************************************************************************
     * -----发送客户端信息区域
     ******************************************************************************************************************/

    @Override
    public void clear() {
        super.clear();
        this.crap1 = -1;
        this.crap2 = -1;
        this.lastFumbleIndex = -1;
        this.lastFumbleCard = -1;
        this.curAction = EActionOp.NORMAL;
        this.curOpIndex = -1;
        this.curCard = -1;
        this.curBarType = EBarType.NONE;
        this.prevAction = EActionOp.NORMAL;
        this.prevOpIndex = -1;
        this.prevCard = -1;
        this.prevBarType = EBarType.NONE;
        this.prevPrevAction = EActionOp.NORMAL;
        this.prevPrevOpIndex = -1;
        this.prevPrevCard = -1;
        this.prevPrevBarType = EBarType.NONE;
        this.fumbleCntWithBar = 0;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            this.deskCard[i] = 0;
        }
        this.barScoreRecord.clear();
        this.isStartTake = false;
        this.laiZiCard = -1;
        this.isLastCard = false;
    }

    @Override
    public void clearByArenaOver() {
        super.clearByArenaOver();
        this.prevLaiZiCard = -1;
    }

    @Override
    protected void savePrevInfo() {
        super.savePrevInfo();
        this.prevLaiZiCard = this.laiZiCard;
    }

    public static class BarScoreRecord {
        private long takePlayerUid;
        private long tagPlayerUid;
        private EBarType type;
        private int value;

        public BarScoreRecord() {

        }

        public BarScoreRecord(long takePlayerUid, long tagPlayerUid, EBarType type, int value) {
            this.takePlayerUid = takePlayerUid;
            this.tagPlayerUid = tagPlayerUid;
            this.type = type;
            this.value = value;
        }

        public void setTakePlayerUid(long takePlayerUid) {
            this.takePlayerUid = takePlayerUid;
        }

        public void setTagPlayerUid(long tagPlayerUid) {
            this.tagPlayerUid = tagPlayerUid;
        }

        public void setType(EBarType type) {
            this.type = type;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public long getTakePlayerUid() {
            return takePlayerUid;
        }

        public long getTagPlayerUid() {
            return tagPlayerUid;
        }

        public EBarType getType() {
            return type;
        }

        public int getValue() {
            return value;
        }
    }

}