package com.xiuxiu.app.server.room.normal.mahjong2.csmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.*;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.action.*;
import com.xiuxiu.app.server.room.normal.mahjong2.bird.IMahjongBird;
import com.xiuxiu.app.server.room.player.mahjong2.CSMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.ICSMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IXuanZeng;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.*;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.*;

//@GameInfo(gameType = GameType.GAME_TYPE_CSMJ)
public class CSMJMahjongRoom extends MahjongRoom implements IMahjongCSOpenBar, IMahjongMiddleHu {
    protected int niaoScore = 1;                                // 1鸟分
    protected int niaoType = 1;                                 // 转鸟类型
    protected int huType = 2;                                   // 胡牌类型
    protected int startHuType = 2;                              // 起手胡牌类型
    protected int zengType = 0;                                 // 选增

    protected boolean bankerInc = false;                        // 庄家加1
    protected boolean barWithEatOrBump = false;                 // 杠后可吃碰
    protected boolean barFumble2 = false;                       // 杠后抓2尊
    protected boolean fakeEyeHu = false;                        // 假将胡
    protected boolean menQing = false;                          // 门清
    protected boolean tianHuDiHu = false;                       // 天湖地胡
    protected boolean discardHuOnlyZiMo = false;                // 弃胡只能自摸
    protected boolean qym = false;                              // 缺一门
    protected boolean qys = false;                              // 缺一色
    protected boolean lls = false;                              // 六六顺
    protected boolean dsx = false;                              // 大四喜
    protected boolean bbh = false;                              // 板板胡
    protected boolean yzh = false;                              // 一枝花
    protected boolean jjg = false;                              // 节节高
    protected boolean ztsx = false;                             // 中途四喜0
    protected boolean ztlls = false;                            // 中途六六顺
    protected boolean st = false;                               // 三筒
    protected boolean jtyn = false;                             // 金童玉女

    protected List<Byte> niaoList = new ArrayList<>();          // 鸟列表

    protected int[] cnt = new int[5];
    protected int[] type = new int[5];
    protected int[][] useCardCnt = new int[5][MahjongUtil.MJ_CARD_KINDS];
    protected long[] min = new long[1];
    protected int[] bright = new int[MahjongUtil.MJ_CARD_KINDS];

    public CSMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public CSMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.fumbleWithBarOnFrontend = false;

        this.niaoScore = this.getRule().getOrDefault(RoomRule.RR_MJ_CS_NIAO_SCORE, 1);
        this.niaoType = this.getRule().getOrDefault(RoomRule.RR_MJ_CS_NIAO_TYPE, 1);
        this.huType = this.getRule().getOrDefault(RoomRule.RR_MJ_CS_HU_TYPE, 2);
        this.startHuType = this.getRule().getOrDefault(RoomRule.RR_MJ_CS_START_HU_TYPE, 2);
        this.zengType = this.getRule().getOrDefault(RoomRule.RR_MJ_CS_ZENG, 0);

        int play = this.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        this.bankerInc = 0 != (play & CSMJPlayRule.BANKER_INC.getValue());
        this.barWithEatOrBump = 0 != (play & CSMJPlayRule.BAR_WITH_EAT_OR_BUMP.getValue());
        this.barFumble2 = 0 != (play & CSMJPlayRule.BAR_FUBLE2.getValue());
        this.fakeEyeHu = 0 != (play & CSMJPlayRule.FAKE_EYE_HU.getValue());
        this.menQing = 0 != (play & CSMJPlayRule.MEN_QING.getValue());
        this.tianHuDiHu = 0 != (play & CSMJPlayRule.TDH.getValue());
        this.discardHuOnlyZiMo = 0 != (play & CSMJPlayRule.DISCARD_HU_ONLY_ZI_MO.getValue());
        this.qym = 0 != (play & CSMJPlayRule.QYM.getValue());
        this.qys = 0 != (play & CSMJPlayRule.QYS.getValue());
        this.lls = 0 != (play & CSMJPlayRule.LLS.getValue());
        this.dsx = 0 != (play & CSMJPlayRule.DSX.getValue());
        this.bbh = 0 != (play & CSMJPlayRule.BBH.getValue());
        this.yzh = 0 != (play & CSMJPlayRule.YZH.getValue());
        this.jjg = 0 != (play & CSMJPlayRule.JJG.getValue());
        this.ztsx = 0 != (play & CSMJPlayRule.ZTSX.getValue());
        this.ztlls = 0 != (play & CSMJPlayRule.ZTLLS.getValue());
        this.st = 0 != (play & CSMJPlayRule.ST.getValue());
        this.jtyn = 0 != (play & CSMJPlayRule.JTYN.getValue());
    }

    @Override
    public void doShuffle() {
        if (Switch.USE_CARD_LIB) {
            this.allCard.addAll(CardLibraryManager.I.getMahjongCard());
            return;
        }
        for (int i = 0; i < 4; ++i) {
            for (int j = MahjongUtil.MJ_1_WANG; j <= MahjongUtil.MJ_9_WANG; ++j) {
                this.allCard.add((byte) j);
            }
            for (int j = MahjongUtil.MJ_1_TIAO; j <= MahjongUtil.MJ_9_TIAO; ++j) {
                this.allCard.add((byte) j);
            }
            if (!this.qym) {
                for (int j = MahjongUtil.MJ_1_TONG; j <= MahjongUtil.MJ_9_TONG; ++j) {
                    this.allCard.add((byte) j);
                }
            }
        }

        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doStart1() {
        this.beginXuanZeng(this.zengType);
    }

    @Override
    public void endXuanZeng() {
        super.endXuanZeng();
        this.beginStartHu();
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new CSMJMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    public void beginStartHu() {
        boolean has = false;
        MahjongCSStartHuAction action = new MahjongCSStartHuAction(this);
        action.setNiaoInfo(this.bankerInc, this.niaoType, this.startHuType, this.niaoScore);
        for (int i = 0; i < this.playerNum; ++i) {
            ICSMJMahjongPlayer player = (ICSMJMahjongPlayer) this.allPlayer[(this.bankerIndex + i) % this.playerNum];
            if (null == player || player.isGuest()) {
                continue;
            }
            List<Byte> bright = this.getBright(player, false);
            if (!bright.isEmpty()) {
                action.addPlayer(player, bright);
                has = true;
            }
        }
        if (has) {
            action.start();
            this.addAction(action);
        } else {
            this.endStartHu(false);
        }
    }

    @Override
    public void endStartHu(boolean has) {
        this.doStartTake();
    }

    @Override
    public void beginMiddleHu(IMahjongPlayer player) {
        MahjongCSMiddleHuAction action = new MahjongCSMiddleHuAction(this, player);
        this.addAction(action);
        action.setNiaoInfo(this.bankerInc, this.niaoType, this.startHuType, this.niaoScore);
        action.setBright(this.getBright(player, true));
        action.start();
    }

    @Override
    public void endMiddleHu(IMahjongPlayer player) {
        ((ICSMJMahjongPlayer) player).clearMiddleHuPaiXing();
        super.doFumbleAfter(player, this.lastFumbleCard);
    }

    @Override
    public void doSendBeginMiddleHu(Object msg) {
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_START_HU_INFO, msg);
    }

    @Override
    public boolean isLastCardSelect() {
        return true;
    }

    @Override
    public void beginCSOpenBar(IMahjongPlayer player) {
        MahjongCSOpenBarAction action = new MahjongCSOpenBarAction(this, player, CS_OPEN_BAR_TIMEOUT);
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_CS_OPEN_BAR, null);
        this.addAction(action);
    }

    @Override
    public void endCSOpenCard(IMahjongPlayer player, boolean select) {
        if (!select) {
            this.onFumble(player);
        } else {
            int openBarCap1 = RandomUtil.random(1, 6);
            int openBarCap2 = RandomUtil.random(1, 6);

            int lastDunCnt = 0 == (this.fumbleCntWithBar % 2) ? 2 : 1;
            int total = 1 + (this.allCard.size() - lastDunCnt) / 2 + (this.allCard.size() - lastDunCnt) % 2;
            int dun = openBarCap1 + openBarCap2;
            if (dun > total) {
                dun = Math.min(openBarCap1, openBarCap2);
            }
            if (dun > total) {
                this.onFumble(player);
                return;
            }
            ((ICSMJMahjongPlayer) player).setOpenBar(true);
            ArrayList<Byte> card = new ArrayList<>();
            if (1 == dun) {
                if (1 == lastDunCnt) {
                    //card.add(this.allCard.removeLast());
                    card.add(this.allCard.removeFirst());
                } else {
                    card.add(this.allCard.removeFirst());
                    card.add(this.allCard.removeFirst());
                    //card.add(this.allCard.removeLast());
                    //card.add(this.allCard.removeLast());
                }
                if (this.barFumble2) {
                    int temp = 2;
                    while (this.allCard.size() > 0 && temp > 0) {
                        card.add(this.allCard.removeFirst());
                        //card.add(this.allCard.removeLast());
                        --temp;
                    }
                }
            } else if (total == dun) {
                if (0 == (this.allCard.size() - lastDunCnt) % 2) {
                    card.add(this.allCard.removeFirst());
                    card.add(this.allCard.removeFirst());
                } else {
                    card.add(this.allCard.removeFirst());
                }
                if (this.barFumble2) {
                    int temp = 2;
                    while (this.allCard.size() > 0 && temp > 0) {
                        card.add(this.allCard.removeFirst());
                        //card.add(this.allCard.removeLast());
                        --temp;
                    }
                }
            } else {
                int cnt = this.allCard.size() - 2 * (dun - 1) - (1 == lastDunCnt ? 1 : 2);
                card.add(this.allCard.removeFirst());
                card.add(this.allCard.removeFirst());
                //card.add(this.allCard.remove(cnt));
                //card.add(this.allCard.remove(cnt));
                if (this.barFumble2) {
                    int temp = 2;
                    while (this.allCard.size() > cnt && temp > 0) {
                        card.add(this.allCard.removeFirst());
                        //card.add(this.allCard.remove(cnt));
                        --temp;
                    }
                }
            }

            PCLIMahjongNtfOpenBarInfo openBarInfo = new PCLIMahjongNtfOpenBarInfo();
            openBarInfo.card.addAll(card);
            openBarInfo.cap1 = openBarCap1;
            openBarInfo.cap2 = openBarCap2;
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_CS_OPEN_BAR_INFO, openBarInfo);

            this.setCurOp(player, EActionOp.OPEN_BAR, this.curCard);
            OpenBarRecordAction csOpenBarRecordAction = ((MahjongRecord) this.getRecord()).addCSOpenBarAction(player.getUid());
            csOpenBarRecordAction.setCap1(openBarCap1);
            csOpenBarRecordAction.setCap1(openBarCap2);
            csOpenBarRecordAction.addCard(card);

            if (this.openBarHu(player, player, card, true)) {
                // 胡
                this.onHu(player, player, (byte) -1);
            } else {
                for (Byte c : card) {
                    player.takeCardWithoutHand(c);
                }
                IMahjongPlayer hu1 = null;
                IMahjongPlayer hu2 = null;
                IMahjongPlayer hu3 = null;
                for (int i = 0; i < this.playerNum; ++i) {
                    ICSMJMahjongPlayer otherPlayer = (ICSMJMahjongPlayer) this.allPlayer[i];
                    if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid()) {
                        continue;
                    }
                    if (this.openBarHu(player, otherPlayer, card, false)) {
                        if (null == hu1) {
                            hu1 = otherPlayer;
                        } else if (null == hu2) {
                            hu2 = otherPlayer;
                        } else if (null == hu3) {
                            hu3 = otherPlayer;
                        }
                    }
                }
                if (null != hu1) {
                    this.onHu(player, hu1, hu2, hu3, (byte) -1);
                } else {
                    // 没有胡
                    MahjongWaitAction waitAction = null;
                    for (int i = 0; i < this.playerNum; ++i) {
                        ICSMJMahjongPlayer otherPlayer = (ICSMJMahjongPlayer) this.allPlayer[i];
                        if (null == otherPlayer || otherPlayer.isGuest()) {
                            continue;
                        }
                        boolean bar = false;
                        boolean bump = false;
                        boolean eat = false;
                        for (Byte c : card) {
                            if (!bar) {
                                if (otherPlayer.getUid() == player.getUid()) {
                                    // 可以明杠
                                    bar = this.isBar(otherPlayer, c, false);
                                }
                                if (!bar) {
                                    // 放杠
                                    if (this.isBar(otherPlayer, c, true)) {
                                        if (otherPlayer.isOpenBar()) {
                                            otherPlayer.delHandCard(c, 3);
                                            otherPlayer.setTempCpgNodeCnt(1);
                                            bar = this.isOpenBarTingInfo(player);
                                            otherPlayer.setTempCpgNodeCnt(0);
                                            otherPlayer.addHandCard(c, 3);
                                        } else {
                                            bar = true;
                                        }
                                    }
                                }
                            }

                            if (this.barWithEatOrBump && otherPlayer.getUid() != player.getUid()) {
                                if (!bump && this.isBump(otherPlayer, c)) {
                                    bump = true;
                                }
                                if (!eat && (otherPlayer.getIndex() == (player.getIndex() + 1) % this.playerNum) && this.isEat(otherPlayer, c)) {
                                    eat = true;
                                }
                            }
                        }
                        if (otherPlayer.isOpenBar()) {
                            bump = false;
                            eat = false;
                        }
                        if (this.onCheckOver()) {
                            bar = false;
                        }
                        if (bar || bump || eat) {
                            long timeout = otherPlayer.getTimeout(this.timeout);
                            MahjongWaitAction.WaitInfo waitInfo = new MahjongWaitAction.WaitInfo();
                            waitInfo.setPlayerUid(otherPlayer.getUid());
                            waitInfo.setIndex(otherPlayer.getIndex());
                            waitInfo.setTimeout(timeout);
                            waitInfo.setHu(false);
                            waitInfo.setBar(bar);
                            waitInfo.setBump(bump);
                            waitInfo.setEat(eat);
                            if (null == waitAction) {
                                waitAction = new MahjongWaitAction(this, player);
                                waitAction.setTakeCard(card.get(0));
                            }
                            waitAction.addWait(waitInfo);

                            this.doSendCanOperate(otherPlayer, false, bar, bump, eat, card);
                        }
                    }
                    for (Byte c : card) {
                        ++this.deskCard[c];
                    }
                    if (null != waitAction) {
                        this.addAction(waitAction);
                    } else {
                        this.onFumble((IMahjongPlayer) this.getNextRoomPlayer(player.getIndex()));
                    }
                }
            }
        }
    }

    private boolean openBarHu(IMahjongPlayer takePlayer, IMahjongPlayer player, List<Byte> card, boolean ziMo) {
        int huCnt = 0;
        int maxBigHuCnt = 0;
        List<EPaiXing> tempPaiXing = new ArrayList<>();
        for (Byte c : card) {
            player.addHandCard(c);
            if (this.isHu(player, player.getUid(), ziMo, c)) {
                ++huCnt;
                int temp = player.getScore(Score.MJ_CUR_BIG_HU_CNT, false);
                if (temp > maxBigHuCnt) {
                    maxBigHuCnt = temp;
                    tempPaiXing.clear();
                    tempPaiXing.addAll(player.getAllPaiXing());
                }
                for (EPaiXing px : player.getAllPaiXing()) {
                    player.addHu(takePlayer.getUid(), px, c);
                }
            }
            player.delHandCard(c);
        }

        if (huCnt > 0) {
            player.setScore(Score.MJ_CUR_BIG_HU_CNT, maxBigHuCnt, false);
            player.setScore(Score.MJ_CUR_OPEN_BAR_CNT, huCnt, false);
            player.clearPaiXing();
            player.addAllPaiXing(tempPaiXing);
            return true;
        }
        return false;
    }

    @Override
    public ErrorCode selectOpenCard(IPlayer player, boolean select) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法选择开杠操作", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法选择开杠操作", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongCSOpenBarAction) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (mahjongPlayer.getUid() != ((MahjongCSOpenBarAction) action).getPlayer().getUid()) {
                Logs.ROOM.warn("%s 不是你开杠操作, 无法开杠操作", this);
                return ErrorCode.REQUEST_OPERATE_ERROR;
            }
            ErrorCode err = ((MahjongCSOpenBarAction) action).select(select);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是开杠操作动作, 无法开杠操作", this);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public void doSendBeginCSOpenCard(IMahjongPlayer player) {
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_CS_OPEN_BAR, null);
    }

    protected void openBar(IMahjongPlayer player) {
        boolean isTing = false;
        for (int j = 1; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
            byte huCard = (byte) j;
            int fang = this.getFang(player, huCard);
            if (fang > 0) {
                isTing = true;
                break;
            }
        }

        if (isTing) {
            this.beginCSOpenBar(player);
        } else {
            this.onFumble(player);
        }
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
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction) action).getWaitInfo(player.getUid());
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
                Logs.ROOM.warn("%s 杠牌的人:%s 不能杠操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            byte barCard = (byte) param[0];
            if (EActionOp.OPEN_BAR != this.curAction && this.curCard != barCard) {
                Logs.ROOM.warn("%s 杠牌的人:%s 杠的不是当前打出去的牌 不能杠操作2 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            if (!this.isBar(mahjongPlayer, barCard, true)) {
                Logs.ROOM.warn("%s 杠牌的人:%s 不能杠操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongWaitAction) action).opWait(waitInfo, EActionOp.BAR, param);
            if (((MahjongWaitAction) action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        if (action instanceof MahjongTakeAction) {
            IMahjongPlayer takePlayer = ((MahjongTakeAction) action).getPlayer();
            if (player.getUid() != takePlayer.getUid()) {
                Logs.ROOM.warn("%s 杠牌的人:%s 当前轮杠牌人是:%s 不是你, 无效杠牌", this, player, ((MahjongTakeAction) action).getPlayer().getUid());
                return ErrorCode.ROOM_NOT_CUR_PLAYER;
            }
            byte barCard = (byte) param[0];
            if (takePlayer.isOver()) {
                Logs.ROOM.warn("%s 打牌的人:%s 已经结束 无法打牌操作 card:%d, 无效杠牌", this, player, barCard);
                return ErrorCode.ROOM_MJ_OVER;
            }
            if (!this.isBar(takePlayer, barCard, false)) {
                Logs.ROOM.warn("%s 杠牌的人:%s 不能杠操作4", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            boolean canBar = true;
            if (((ICSMJMahjongPlayer) takePlayer).isOpenBar()) {
                if (takePlayer.hasBump(barCard)) {
                    takePlayer.delHandCard(barCard, 1);
                } else {
                    takePlayer.delHandCard(barCard, 4);
                }
                takePlayer.setTempCpgNodeCnt(1);
                canBar = this.isOpenBarTingInfo(takePlayer);
                takePlayer.setTempCpgNodeCnt(0);
                if (takePlayer.hasBump(barCard)) {
                    takePlayer.addHandCard(barCard, 1);
                } else {
                    takePlayer.addHandCard(barCard, 4);
                }
            }
            if (!canBar) {
                Logs.ROOM.warn("%s 杠牌的人:%s 不能杠操作5", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ((MahjongTakeAction) action).setOp(EActionOp.BAR);
            ((MahjongTakeAction) action).setParam(param);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 杠牌的人:%s 本来不是杠牌动作, 无法杠牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }

    @Override
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return false;
    }

    @Override
    protected void doFumbleAfter(IMahjongPlayer player, byte card) {
        boolean hu = false;
        if (this.ztsx && !((ICSMJMahjongPlayer) player).hasStartHuPaiXing(EPaiXing.CSMJ_DSX)) {
            for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                if (player.hasHandCard((byte) i, 4)) {
                    ((ICSMJMahjongPlayer) player).addMiddleHuPaiXing(EPaiXing.CSMJ_DSX);
                    hu = true;
                    break;
                }
            }
        }
        if (this.ztlls && !((ICSMJMahjongPlayer) player).hasStartHuPaiXing(EPaiXing.CSMJ_LLS)) {
            int AAACnt = 0;
            for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                if (player.hasHandCard((byte) i, 3)) {
                    ++AAACnt;
                    if (AAACnt >= 2) {
                        ((ICSMJMahjongPlayer) player).addMiddleHuPaiXing(EPaiXing.CSMJ_LLS);
                        hu = true;
                        break;
                    }
                }
            }
        }
        if (hu) {
            this.beginMiddleHu(player);
        } else {
            super.doFumbleAfter(player, card);
        }
    }

    @Override
    protected void doBarAfter(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        MahjongWaitAction waitAction = null;
        if (EBarType.BAR_MING == type) {
            waitAction = this.getWaitAction(player, barCard, true,-1);
        }
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            this.checkBarScore(takePlayer, player, type, barCard);
            if (this.onCheckOver()) {
                this.onHuangZhuang(this.curBureau < this.bureau);
            } else {
                this.openBar(player);
            }
        }
    }

    @Override
    public void onPass() {
        Logs.ROOM.debug("%s 所有人都跳过", this);
        if (EActionOp.BAR == this.curAction) {
            this.checkBarScore((IMahjongPlayer) this.getRoomPlayer(this.prevOpIndex), (IMahjongPlayer) this.getRoomPlayer(this.curOpIndex), this.prevBarType, this.curCard);
        }
        if (this.onCheckOver()) {
            this.onHuangZhuang(this.curBureau < this.bureau);
        } else {
            if (EActionOp.BAR == this.curAction) {
                this.openBar((IMahjongPlayer) this.getRoomPlayer(this.curOpIndex));
            } else {
                this.onFumble((IMahjongPlayer) this.getNextRoomPlayer(this.curOpIndex));
            }
        }
    }

    @Override
    public boolean isStartHu(IMahjongPlayer player) {
        return false;
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        player.clearPaiXing();
        int oldHuType = player.getScore(Score.MJ_CUR_BIG_HU_CNT, false);
        player.setScore(Score.MJ_CUR_BIG_HU_CNT, 0, false);
        if (discardHuOnlyZiMo) {
            if (((ICSMJMahjongPlayer) player).isPassHu() && !ziMo) {
                return false;
            }
        }
        int meldCnt = player.getCPGNode().size(); // 碰/杠了多少句话
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        for (CPGNode node : player.getCPGNode()) {
            tempCards.get()[node.getCard1()] += 1;
        }
        if (!MahjongUtil.isHu(player.getHandCardRaw(), meldCnt, true)) {
            return false;
        }
        int bigHuCnt = 0;
        int huCnt = 0;
        if (MahjongUtil.isSevenPair(player.getHandCardRaw())) {
            player.addPaiXing(EPaiXing.CSMJ_QXD);
            ++bigHuCnt;
            ++huCnt;
        }
        if (MahjongUtil.isBigSevenPair(player.getHandCardRaw())) {
            player.addPaiXing(EPaiXing.CSMJ_HHQXD);
            ++bigHuCnt;
            ++huCnt;
        }
        boolean twoSame = false;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
            if (2 == tempCards.get()[i]) {
                twoSame = true;
            }
        }
        List<CPGNode> cpgNodes = player.getCPGNode();
        int eatCnt = 0;
        for (CPGNode node : cpgNodes) {
            if (CPGNode.EType.ANY_THREE == node.getType()) {
                continue;
            }
            if (node.isEat()) {
                ++eatCnt;
            }
            tempCards.get()[node.getCard1()] += 1;
        }
        if (2 == player.getHandCardCnt() && twoSame) {
            player.addPaiXing(EPaiXing.CSMJ_QQR);
            ++bigHuCnt;
            ++huCnt;
        }
        if (0 == eatCnt && MahjongUtil.isPengPengHu(player.getHandCardRaw())) {
            player.addPaiXing(EPaiXing.CSMJ_PPH);
            ++bigHuCnt;
            ++huCnt;
        }
        if (MahjongUtil.isQingYiSe(tempCards.get())) {
            player.addPaiXing(EPaiXing.CSMJ_QYS2);
            ++bigHuCnt;
            ++huCnt;
        }
        if (MahjongUtil.is258(tempCards.get())) {
            player.addPaiXing(EPaiXing.CSMJ_JJH);
            ++bigHuCnt;
            ++huCnt;
        }
        boolean isHu258Eye = false;
        if (this.isLastCard) {
            if (this.fakeEyeHu || MahjongUtil.is258(huCard)) {
                if (ziMo) {
                    player.addPaiXing(EPaiXing.CSMJ_HDH);
                } else {
                    player.addPaiXing(EPaiXing.CSMJ_HDP);
                }
                ++bigHuCnt;
                ++huCnt;
                if (!this.fakeEyeHu) {
                    isHu258Eye = true;
                }
            }
        }
        if (ziMo) {
            if (this.tianHuDiHu && this.isTianHu(player)) {
                ++huCnt;
                player.addPaiXing(EPaiXing.CSMJ_TH);
                ++bigHuCnt;
            }
        } else if (this.tianHuDiHu && this.isDiHu(player)) {
            ++huCnt;
            player.addPaiXing(EPaiXing.CSMJ_DH);
            ++bigHuCnt;
        }
        if (0 == player.getCPGNodeCnt()) {
            if (this.menQing) {
                ++huCnt;
                ++bigHuCnt;
                player.addPaiXing(EPaiXing.CSMJ_MQ);
            }
        }
        if (0 == huCnt) {
            if (isHu258Eye || MahjongUtil.isHu258Eye(player.getHandCardRaw(), player.getCPGNodeCnt(), true)) {
                ++huCnt;
                player.addPaiXing(EPaiXing.CSMJ_NORMAL);
            }
        }
        if (huCnt > 0) {
            if (EActionOp.BAR == this.curAction) {
                // 抢杠胡
                ++bigHuCnt;
                if (!ziMo) {
                    player.addPaiXing(EPaiXing.CSMJ_QGH);
                }
            } else if (EActionOp.OPEN_BAR == this.curAction) {
                // 杠开/杠上跑
                if (ziMo) {
                    player.addPaiXing(EPaiXing.CSMJ_GK);
                } else {
                    player.addPaiXing(EPaiXing.CSMJ_GSP);
                }
                ++bigHuCnt;
            }
        }
        if (huCnt > 0 && !ziMo && player.isPass(EActionOp.HU) && (oldHuType > 0 || bigHuCnt < 1)) {
            huCnt = 0;
        }
        if (huCnt > 0) {
            player.setScore(Score.MJ_CUR_BIG_HU_CNT, bigHuCnt, false);
            return true;
        }
        player.clearPassCard();
        player.setScore(Score.MJ_CUR_BIG_HU_CNT, 0, false);
        return false;
    }

    @Override
    protected EPaiXing getPaiXing(IMahjongPlayer player) {
        return null;
    }

    @Override
    protected int getFang(IMahjongPlayer player) {
        if (this.isHu(player)) {
            return 1;
        }
        return 0;
    }

    private void calcFangScore(IMahjongPlayer takePlayer, IMahjongPlayer player, boolean ziMo, byte card) {
        int bigCnt = player.getScore(Score.MJ_CUR_BIG_HU_CNT, false);
        //int openCardCnt = player.getScore(Score.MJ_CUR_OPEN_BAR_CNT, false);
        int score = 0;
        if (0 == bigCnt) {
            // 小胡
            score = ziMo ? 2 : 1;
        } else {
            score = 6 * bigCnt;
        }
        if (this.bankerInc && (this.bankerIndex == takePlayer.getIndex() || this.bankerIndex == player.getIndex())) {
            ++score;
        }
//       anyi
        int zengScore = 0;
        if (zengType != 0) {
            zengScore = ((IXuanZeng) takePlayer).getZeng() + ((IXuanZeng) player).getZeng();
        }
        takePlayer.addScore(Score.MJ_CUR_FANG_SCORE, -score, false);
        takePlayer.addScore(Score.MJ_CUR_ZENG_SCORE, -zengScore, false);
        player.addScore(Score.MJ_CUR_FANG_SCORE, score, false);
        player.addScore(Score.MJ_CUR_ZENG_SCORE, zengScore, false);

        if (-1 != card) {
            if (EActionOp.OPEN_BAR != this.prevAction) {
                for (EPaiXing px : player.getAllPaiXing()) {
                    player.addHu(ziMo ? -1L : takePlayer.getUid(), px, card);
                }
            }
        }
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        int lostCnt = 0;
        if (takePlayer.getUid() == player1.getUid()) {
            // 自摸
            for (int i = 0; i < this.playerNum; ++i) {
                ICSMJMahjongPlayer otherPlayer = (ICSMJMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                ++lostCnt;
                this.calcFangScore(otherPlayer, player1, true, huCard);
            }
        } else {
            if (null != player1) {
                ++lostCnt;
                this.calcFangScore(takePlayer, player1, false, huCard);
            }
            if (null != player2) {
                ++lostCnt;
                this.calcFangScore(takePlayer, player2, false, huCard);
            }
            if (null != player3) {
                ++lostCnt;
                this.calcFangScore(takePlayer, player3, false, huCard);
            }
        }
        // 抓鸟
        if (this.huType > 0) {
            IMahjongBird bird = IMahjongBird.get(this.niaoType);
            do {
                if (null == bird) {
                    break;
                }
                int num = this.huType;//中鸟的数量
                num = Math.max(0, num);
                num = Math.min(num, this.allCard.size());
                bird.setCnt(num);
                for (int i = 0; i < num; ++i) {
                    byte niaoCard = this.allCard.removeFirst();
                    if (null != player2) {
                        bird.isHit(this, takePlayer, niaoCard);
                    } else {
                        bird.isHit(this, player1, niaoCard);
                    }
                    this.niaoList.add(niaoCard);
                }
                if (takePlayer.getUid() == player1.getUid()) {
                    // 自摸
                    int niaoScore = 0;
                    for (int i = 0; i < this.playerNum; ++i) {
                        IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                        if (null == player || player.isGuest() || player.getUid() == takePlayer.getUid()) {
                            continue;
                        }
                        int value = bird.calcNiaoScore(takePlayer, player, this.niaoScore);
                        if (0 == this.niaoScore) {
                            int fangScore = takePlayer.getScore(Score.MJ_CUR_FANG_SCORE, false) / lostCnt;
                            int temp = (int) (fangScore * Math.pow(2, Math.abs(value)));
                            if (value == 0) {
                                player.setScore(Score.MJ_CUR_NIAO_SCORE, 0, false);
                            } else {
                                niaoScore += temp;
                                player.setScore(Score.MJ_CUR_NIAO_SCORE, -temp, false);
                            }
                        } else {
                            niaoScore += value;
                            player.setScore(Score.MJ_CUR_NIAO_SCORE, -value, false);
                        }
                    }
                    takePlayer.setScore(Score.MJ_CUR_NIAO_SCORE, niaoScore, false);
                } else {
                    // 点炮
                    int niaoScore = 0;
                    if (null != player1) {
                        int value = bird.calcNiaoScore(takePlayer, player1, this.niaoScore);
                        if (0 == this.niaoScore) {
                            int fangScore = player1.getScore(Score.MJ_CUR_FANG_SCORE, false);
                            int temp = (int) (fangScore * Math.pow(2, Math.abs(value)));
                            if (value == 0) {
                                player1.setScore(Score.MJ_CUR_NIAO_SCORE, 0, false);
                            } else {
                                niaoScore += temp;
                                player1.setScore(Score.MJ_CUR_NIAO_SCORE, temp, false);
                            }
                        } else {
                            niaoScore += value;
                            player1.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        }
                    }
                    if (null != player2) {
                        int value = bird.calcNiaoScore(takePlayer, player2, this.niaoScore);
                        if (0 == this.niaoScore) {
                            int fangScore = player2.getScore(Score.MJ_CUR_FANG_SCORE, false);
                            int temp = (int) (fangScore * Math.pow(2, Math.abs(value)));
                            if (value == 0) {
                                player2.setScore(Score.MJ_CUR_NIAO_SCORE, 0, false);
                            } else {
                                niaoScore += temp;
                                player2.setScore(Score.MJ_CUR_NIAO_SCORE, temp, false);
                            }
                        } else {
                            niaoScore += value;
                            player2.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        }
                    }
                    if (null != player3) {
                        int value = bird.calcNiaoScore(takePlayer, player3, this.niaoScore);
                        if (0 == this.niaoScore) {
                            int fangScore = player3.getScore(Score.MJ_CUR_FANG_SCORE, false);
                            int temp = (int) (fangScore * Math.pow(2, Math.abs(value)));
                            if (value == 0) {
                                player3.setScore(Score.MJ_CUR_NIAO_SCORE, 0, false);
                            } else {
                                niaoScore += temp;
                                player3.setScore(Score.MJ_CUR_NIAO_SCORE, temp, false);
                            }
                        } else {
                            niaoScore += value;
                            player3.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        }
                    }
                    takePlayer.setScore(Score.MJ_CUR_NIAO_SCORE, -niaoScore, false);
                }
            } while (false);
        }

        CSMJResultRecordAction resultRecordAction = (CSMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            ICSMJMahjongPlayer otherPlayer = (ICSMJMahjongPlayer) this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest()) {
                continue;
            }
            int fangScore = otherPlayer.getScore(Score.MJ_CUR_FANG_SCORE, false);
            int niaoScore = otherPlayer.getScore(Score.MJ_CUR_NIAO_SCORE, false);
            if (this.niaoScore > 0) {
                fangScore += niaoScore;
            } else {
                if (niaoScore != 0)
                    fangScore = niaoScore;
            }
            fangScore += otherPlayer.getScore(Score.MJ_CUR_START_HU_SCORE, false);
            fangScore += otherPlayer.getScore(Score.MJ_CUR_ZENG_SCORE, false);
            int score = this.getScore(fangScore);
            otherPlayer.addScore(Score.SCORE, score, false);
            otherPlayer.addScore(Score.ACC_TOTAL_SCORE, score, true);

            int openBatCnt = otherPlayer.getScore(Score.MJ_CUR_OPEN_BAR_CNT, false) + 1;
            CSMJResultRecordAction.PlayerInfo playerInfo = new CSMJResultRecordAction.PlayerInfo();
            List<HuInfo> huList = otherPlayer.getHuList();
            for (HuInfo huInfo : huList) {
                EPaiXing px = huInfo.getPaiXing();
                ResultRecordAction.HuInfo temp = new ResultRecordAction.HuInfo();
                if (EPaiXing.CSMJ_NORMAL == px) {
                    temp.setFang(px.getDefaultValue());
                } else {
                    temp.setFang(px.getDefaultValue() * openBatCnt);
                }
                temp.setHuCard(huInfo.getHuCard());
                temp.setPaiXing(px.getClientValue());
                temp.setZiMo(huInfo.isZiMo());
                temp.setTakePlayerUid(huInfo.getTakePlayerUid());
                playerInfo.addHuInfo(temp);
            }

            CSMJResultRecordAction.ScoreInfo scoreInfo = new CSMJResultRecordAction.ScoreInfo();
            scoreInfo.setStartHuScore(otherPlayer.getScore(Score.MJ_CUR_START_HU_SCORE, false));
            scoreInfo.setNiaoScore(otherPlayer.getScore(Score.MJ_CUR_NIAO_SCORE, false));
            scoreInfo.setFangScore(otherPlayer.getScore(Score.MJ_CUR_FANG_SCORE, false));
            scoreInfo.setZengScore(otherPlayer.getScore(Score.MJ_CUR_ZENG_SCORE, false));
            scoreInfo.setGangScore(otherPlayer.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(otherPlayer.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(otherPlayer.getScore()));
            playerInfo.setScoreInfo(scoreInfo);
            resultRecordAction.addResult(otherPlayer.getUid(), playerInfo);
        }

        if (null == player2) {
            this.bankerIndex = player1.getIndex();
        } else {
            this.bankerIndex = takePlayer.getIndex();
        }
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        CSMJResultRecordAction resultRecordAction = (CSMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.addScore(Score.SCORE, this.getScore(
                    player.getScore(Score.MJ_CUR_START_HU_SCORE, false)
            ), false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            CSMJResultRecordAction.PlayerInfo playerInfo = new CSMJResultRecordAction.PlayerInfo();
            CSMJResultRecordAction.ScoreInfo scoreInfo = new CSMJResultRecordAction.ScoreInfo();
            scoreInfo.setStartHuScore(player.getScore(Score.MJ_CUR_START_HU_SCORE, false));
            scoreInfo.setNiaoScore(player.getScore(Score.MJ_CUR_NIAO_SCORE, false));
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_FANG_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        if (this.allCard.isEmpty()) {
            this.bankerIndex = this.lastFumbleIndex;
        }
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByCSMJ info = new PCLIMahjongNtfGameOverInfoByCSMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

        info.niaoList.addAll(this.niaoList);

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByCSMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByCSMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            int openBatCnt = player.getScore(Score.MJ_CUR_OPEN_BAR_CNT, false) + 1;
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                EPaiXing px = huInfo.getPaiXing();
                PCLIMahjongNtfGameOverInfo.HuInfo temp = new PCLIMahjongNtfGameOverInfo.HuInfo();
                if (EPaiXing.CSMJ_NORMAL == px) {
                    temp.fang = px.getDefaultValue();
                } else {
                    temp.fang = px.getDefaultValue() * openBatCnt;
                }
                temp.huCard = huInfo.getHuCard();
                temp.paiXing = px.getClientValue();
                temp.ziMo = huInfo.isZiMo();
                temp.takePlayerUid = huInfo.getTakePlayerUid();
                playerInfo.allHuInfo.add(temp);
            }

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByCSMJ.ScoreInfo();
            ((PCLIMahjongNtfGameOverInfoByCSMJ.ScoreInfo) playerInfo.score).niaoScore = player.getScore(Score.MJ_CUR_NIAO_SCORE, false);
            ((PCLIMahjongNtfGameOverInfoByCSMJ.ScoreInfo) playerInfo.score).startHuScore = player.getScore(Score.MJ_CUR_START_HU_SCORE, false);
            ((PCLIMahjongNtfGameOverInfoByCSMJ.ScoreInfo) playerInfo.score).zengScore = player.getScore(Score.MJ_CUR_ZENG_SCORE, false);
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_FANG_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE, false));
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfo.FinalResult();
                playerInfo.finalResult.anGangCnt = player.getScore(Score.ACC_MJ_AN_GANG_CNT, true);
                playerInfo.finalResult.fangPaoCnt = player.getScore(Score.ACC_MJ_DIAN_PAO_CNT, true);
                playerInfo.finalResult.huCnt = player.getScore(Score.ACC_MJ_HU_CNT, true);
                playerInfo.finalResult.mingGangCnt = player.getScore(Score.ACC_MJ_MING_GANG_CNT, true);
                playerInfo.finalResult.ziMoCnt = player.getScore(Score.ACC_MJ_ZIMO_CNT, true);
                playerInfo.finalResult.score = player.getScore(Score.ACC_TOTAL_SCORE, true) / 100;
            }

            info.allPlayer.put(player.getUid(), playerInfo);
        }
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, info);
    }

    private boolean isCanBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        HashMap<Byte, HashMap<Byte, Integer>> tingInfo = player.getTingInfo().getTing();
        List<Byte> listTingInfo = new ArrayList<Byte>();
        for (Map.Entry<Byte, HashMap<Byte, Integer>> entry : tingInfo.entrySet()) {
            if (fangGang) {
                if (entry.getKey() == player.getLastTakeCard()) {
                    HashMap<Byte, Integer> ting1 = entry.getValue();
                    for (Map.Entry<Byte, Integer> entry1 : ting1.entrySet()) {
                        listTingInfo.add(entry1.getKey());

                    }
                }
            } else {
                if (entry.getKey() == takeCard) {
                    HashMap<Byte, Integer> ting1 = entry.getValue();
                    for (Map.Entry<Byte, Integer> entry1 : ting1.entrySet()) {
                        listTingInfo.add(entry1.getKey());

                    }
                }
            }

        }
        List<Byte> listTing = new ArrayList<Byte>();
        CPGNode temp;
        if (fangGang) {
            temp = new CPGNode(player.getIndex(), CPGNode.EType.BAR_FANG, takeCard);
            player.getCPGNode().add(temp);
        } else {
            temp = new CPGNode(player.getIndex(), CPGNode.EType.BAR_AN, takeCard);
            player.getCPGNode().add(temp);
        }
        player.delAllHandCard(takeCard);
        this.deskCard[takeCard] = 4;
        this.generateTingInfo1(player);
        HashMap<Byte, HashMap<Byte, Integer>> ting = player.getTingInfo().getTing();

        for (Map.Entry<Byte, HashMap<Byte, Integer>> entry : ting.entrySet()) {
            HashMap<Byte, Integer> ting1 = entry.getValue();
            for (Map.Entry<Byte, Integer> entry1 : ting1.entrySet()) {
                listTing.add(entry1.getKey());

            }
        }
        if (fangGang) {
            player.addHandCard(takeCard, 3);
            this.deskCard[takeCard] = 1;
        } else {
            player.addHandCard(takeCard, 4);
            this.deskCard[takeCard] = 0;
        }

        player.getCPGNode().remove(temp);
        int count = 0;
        if (listTing.size() == listTingInfo.size()) {
            for (int i = 0; i < listTing.size(); i++) {
                for (int j = 0; j < listTingInfo.size(); j++) {
                    if (listTingInfo.get(j) == listTing.get(i)) {
                        count++;
                    }
                }
            }

            if (count == listTing.size()) return true;
        }

        return false;
    }

    private void generateTingInfo1(IMahjongPlayer player) {
        TingInfo tingInfo = player.getTingInfo();
        tingInfo.clear();
        for (int j = 1; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
            byte huCard = (byte) j;
            int fang = super.getFang(player, huCard);
            if (fang > 0) {
                tingInfo.add((byte) 0, huCard, fang, this.getRemainCardCntByPlayer(player, huCard));
            }
        }
        tingInfo.setBuild(true);
    }

    @Override
    protected MahjongWaitAction getWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu, int flag) {
        MahjongWaitAction waitAction = null;
        int canEatIndex = (player.getIndex() + 1) % this.playerNum;
        if (allPlayer[canEatIndex] == null)
            canEatIndex = getNextRoomPlayer(canEatIndex).getIndex();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid() || otherPlayer.isOver()) {
                continue;
            }
            boolean hu = this.isHu(otherPlayer, player.getUid(), takeCard);
            boolean bar = onlyHu ? false : this.isBar(otherPlayer, takeCard, true);
            boolean bump = onlyHu ? false : bar || this.isBump(otherPlayer, takeCard);
            boolean eat = onlyHu ? false : (i != canEatIndex ? false : this.isEat(otherPlayer, takeCard));
            if (((ICSMJMahjongPlayer) otherPlayer).isOpenBar()) {
                bump = false;
                eat = false;
                if (bar) {
                    if (!isCanBar(otherPlayer, takeCard, true)) {
                        bar = false;
                    }

                }
            }
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

    @Override
    public IRoomPlayer createPlayer() {
        return new CSMJMahjongPlayer(this.getGameType(), this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    public void clear() {
        super.clear();
        this.niaoList.clear();
    }

    protected void clearCnt() {
        for (int i = 0; i < 5; ++i) {
            this.cnt[i] = 0;
        }
    }

    protected void clearType() {
        for (int i = 0; i < 5; ++i) {
            this.type[i] = 0;
        }
    }

    protected void clearUseCardCnt() {
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
                this.useCardCnt[i][j] = 0;
            }
        }
    }

    protected void clearMin() {
        this.min[0] = 0;
    }

    protected void clearBright() {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            this.bright[i] = 0;
        }
    }

    protected List<Byte> getBright(IMahjongPlayer player, boolean middle) {
        List<Integer> temp = new ArrayList<>();
        boolean isQYS = this.qys && !this.qym && !middle ? CSMJUtil.isQYS(player.getHandCardRaw()) : false;
        boolean isBBH = this.bbh && !middle ? CSMJUtil.isBBH(player.getHandCardRaw()) : false;
        boolean isYZH = this.yzh && !middle ? CSMJUtil.isYZH(player.getHandCardRaw()) : false;
        int isJTYN = this.jtyn && !middle ? CSMJUtil.isJTYN(player.getHandCardRaw(), temp) : 0;
        int isDSX = this.dsx ? CSMJUtil.isDSX(player.getHandCardRaw(), temp) : 0;
        int isJJG = this.jjg && !middle ? CSMJUtil.isJJG(player.getHandCardRaw(), temp) : 0;
        int isST = this.st && !middle ? CSMJUtil.isST(player.getHandCardRaw(), temp) : 0;
        int isLLS = this.lls ? CSMJUtil.isLLS(player.getHandCardRaw(), temp) : 0;

        if (!middle) {
            if (isQYS) {
                ((ICSMJMahjongPlayer) player).addStartHuPaiXing(EPaiXing.CSMJ_QYS);
            }
            if (isBBH) {
                ((ICSMJMahjongPlayer) player).addStartHuPaiXing(EPaiXing.CSMJ_BBH);
            }
            if (isYZH) {
                ((ICSMJMahjongPlayer) player).addStartHuPaiXing(EPaiXing.CSMJ_YZH);
            }
            if (0 != isJTYN) {
                ((ICSMJMahjongPlayer) player).addStartHuPaiXing(EPaiXing.CSMJ_JTYN);
            }
            if (0 != isDSX) {
                ((ICSMJMahjongPlayer) player).addStartHuPaiXing(EPaiXing.CSMJ_DSX);
            }
            if (0 != isJJG) {
                ((ICSMJMahjongPlayer) player).addStartHuPaiXing(EPaiXing.CSMJ_JJG);
            }
            if (0 != isST) {
                ((ICSMJMahjongPlayer) player).addStartHuPaiXing(EPaiXing.CSMJ_ST);
            }
            if (0 != isLLS) {
                ((ICSMJMahjongPlayer) player).addStartHuPaiXing(EPaiXing.CSMJ_LLS);
            }
        }

        List<Byte> bright = new ArrayList<>();
        if (isQYS || isBBH || isYZH || 0 != isJTYN || 0 != isDSX || 0 != isJJG || 0 != isST || 0 != isLLS) {
            if (isQYS || isBBH || isYZH) {
                player.addHandCardTo(bright);
            } else {
                this.clearCnt();
                this.clearType();
                this.clearUseCardCnt();
                this.clearMin();
                this.clearBright();

                int index = 0;
                if (0 != isJTYN) {
                    this.cnt[index] = 1;
                    this.type[index] = 1;
                    ++index;
                }
                if (0 != isDSX) {
                    this.cnt[index] = isDSX;
                    this.type[index] = 2;
                    ++index;
                }
                if (0 != isJJG) {
                    this.cnt[index] = isJJG / 3;
                    this.type[index] = 3;
                    ++index;
                }
                if (0 != isST) {
                    this.cnt[index] = isST / 3;
                    this.type[index] = 4;
                    ++index;
                }
                if (0 != isLLS) {
                    this.cnt[index] = 1;
                    this.type[index] = 5;
                    ++index;
                }
                CSMJUtil.calc(index, 0, this.cnt, this.type, this.useCardCnt, temp, 0, isJTYN, isDSX, isJJG, isST, isLLS, this.min);
                long tempIndex = min[0] & 0xFFFFFFFFFFFFL;
                int step = 0;
                while (step < index) {
                    int tempIndex1 = (int) ((tempIndex >> (8 * type[step])) & 0xFF);
                    if (1 == type[step]) {
                        // 金童玉女
                        this.bright[temp.get(0) >> 8] = 2;
                        this.bright[temp.get(1) >> 8] = 2;
                    } else if (2 == type[step]) {
                        // 大四喜
                        this.bright[temp.get(isJTYN + tempIndex1) >> 8] = 4;
                    } else if (3 == type[step]) {
                        // 节节高
                        int c = temp.get(isJTYN + isDSX + tempIndex1 * 3) >> 8;
                        if (this.bright[c] < 2) {
                            this.bright[c] = 2;
                        }
                        if (this.bright[c + 1] < 2) {
                            this.bright[c + 1] = 2;
                        }
                        if (this.bright[c + 2] < 2) {
                            this.bright[c + 2] = 2;
                        }
                    } else if (4 == type[step]) {
                        // 三同
                        int c = temp.get(isJTYN + isDSX + isJJG + tempIndex1 * 3) >> 8;
                        if (this.bright[c] < 2) {
                            this.bright[c] = 2;
                        }
                        if (this.bright[c + MahjongUtil.MJ_9_WANG] < 2) {
                            this.bright[c + MahjongUtil.MJ_9_WANG] = 2;
                        }
                        if (this.bright[c + MahjongUtil.MJ_9_TIAO] < 2) {
                            this.bright[c + MahjongUtil.MJ_9_TIAO] = 2;
                        }
                    } else if (5 == type[step]) {
                        // TODO: 先取头2个 六六顺
                        int c1 = tempIndex1 >> 4;
                        int c2 = tempIndex1 & 0xF;
                        int c11 = temp.get(isJTYN + isDSX + isJJG + isST + c1) >> 8;
                        int c22 = temp.get(isJTYN + isDSX + isJJG + isST + c2) >> 8;
                        if (this.bright[c11] < 3) {
                            this.bright[c11] = 3;
                        }
                        if (this.bright[c22] < 3) {
                            this.bright[c22] = 3;
                        }
                    }
                    ++step;
                }
                for (int k = 0; k < MahjongUtil.MJ_CARD_KINDS; ++k) {
                    for (int j = 0; j < this.bright[k]; ++j) {
                        bright.add((byte) k);
                    }
                }
            }
        }
        return bright;
    }

    protected boolean isOpenBarTingInfo(IMahjongPlayer player) {
        for (int j = 1; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
            byte huCard = (byte) j;
            int fang = this.getFang(player, huCard);
            if (fang > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void beginLastCard(IMahjongPlayer player) {
        MahjongCSLastCardAction action = new MahjongCSLastCardAction(this);
        action.setStartIndex(player.getIndex(), this.allCard.peek());
        action.start();
        this.addAction(action);
    }

    @Override
    public void onFumble(IMahjongPlayer player) {
        if (EActionOp.BAR != this.curAction && this.isLastCardSelect() && !this.isLastCard && 1 == this.allCard.size()) {
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

    @Override
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIMahjongNtfDeskInfoByCSMJ deskInfo = new PCLIMahjongNtfDeskInfoByCSMJ();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 : mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        deskInfo.bankerPlayerUid = -1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest() ? -1L : this.allPlayer[this.bankerIndex].getUid();
        deskInfo.laiZi = this.laiZiCard;
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();

        try {

            this.rwLock.readLock().lock();
            int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer other = (IMahjongPlayer) this.allPlayer[i];
                if (null == other || other.isGuest()) {
                    continue;
                }
                deskInfo.allOnlineState.put(other.getUid(), other.isOffline() ? false : true);
                PCLIMahjongNtfDeskInfoByCSMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByCSMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                deskPlayerInfo.zeng = ((IXuanZeng) other).getZeng();
                deskPlayerInfo.isOutTake = ((ICSMJMahjongPlayer) other).isOpenBar();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfo.CardNode cardNode = new PCLIMahjongNtfDeskInfo.CardNode();
                    cardNode.type = node.getType().ordinal();
                    if (node.getTakePlayerIndex() >= 0) {
                        cardNode.playerId = this.allPlayer[node.getTakePlayerIndex()].getUid();
                    }
                    if (CPGNode.EType.BUMP == node.getType()) {
                        cardNode.a = cardNode.b = cardNode.c = node.getCard1();
                    } else if (node.isBar()) {
                        cardNode.a = cardNode.b = cardNode.c = cardNode.d = node.getCard1();
                    } else if (CPGNode.EType.EAT_LEFT == node.getType()) {
                        cardNode.a = (byte) (node.getCard1() - 2);
                        cardNode.b = (byte) (node.getCard1() - 1);
                        cardNode.c = node.getCard1();
                    } else if (CPGNode.EType.EAT_RIGHT == node.getType()) {
                        cardNode.a = node.getCard1();
                        cardNode.b = (byte) (node.getCard1() + 1);
                        cardNode.c = (byte) (node.getCard1() + 2);
                    } else if (CPGNode.EType.EAT_MIDDLE == node.getType()) {
                        cardNode.a = (byte) (node.getCard1() - 1);
                        cardNode.b = node.getCard1();
                        cardNode.c = (byte) (node.getCard1() + 1);
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
        } finally {
            this.rwLock.readLock().unlock();
        }
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }
}
