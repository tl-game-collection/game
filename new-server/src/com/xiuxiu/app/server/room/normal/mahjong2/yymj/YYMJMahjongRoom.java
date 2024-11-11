package com.xiuxiu.app.server.room.normal.mahjong2.yymj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.*;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJ;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithYYMJ;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTingAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongFeatures;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHelper;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHu;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.YYMJMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.*;
import com.xiuxiu.core.ICallback;

import java.util.*;

//@GameInfo(gameType = GameType.GAME_TYPE_YYMJ)
public class YYMJMahjongRoom extends MahjongRoom {
    private static final long TIAN_HU = 0x00000002L;            // 天胡
    private static final long BAO_TING_HU = 0x00000004L;        // 报听胡
    private static final int RULE_WITH_TIMER = 0x01;
    private static final int RULE_YI_ZI_QIAO = 0x02;
    private static final int RULE_GONG_HAI_DI = 0x04;
    private static final int RULE_YI_TIAO_LONG = 0x08;
    private static final int RULE_MEN_QING = 0x10;
    private static final int RULE_MEN_QING_JIANG_JIANG_HU = 0x020;
    private static final int RULE_MEN_QING_QI_DUI = 0x40;
    private long haiDiBeginnerUid = 0;
    private long features;
    private byte fangCard = -1; // 翻的牌
    private int birds; // 抓鸟
    private int fine; // 罚分
    private int playingRules;
    private int maxTop;//封顶
    private List<Byte> niaoList = new ArrayList<>();// 鸟列表

    public YYMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public YYMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public boolean isMoreHu() {
        return true;
    }

    @Override
    public void init() {
        super.init();

        this.birds = this.getRule().getOrDefault(RoomRule.RR_YYMJ_BIRDS, 1);
        this.fine = this.getRule().getOrDefault(RoomRule.RR_YYMJ_FINE, 0);
        this.maxTop = this.getRule().getOrDefault(RoomRule.RR_YYMJ_MAXTOP, 0);

        this.playingRules = this.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        if ((this.playingRules & RULE_WITH_TIMER) != 0) {
            this.timeout = 10 * 1000;
        }
        // 默认包含序数牌
        this.features = MahjongFeatures.WITH_TONG_ZI | MahjongFeatures.WITH_TIAO_ZI | MahjongFeatures.WITH_WAN_ZI;
    }

    @Override
    public void clear() {
        super.clear();
        this.fangCard = -1;
        this.haiDiBeginnerUid = 0;
        this.niaoList.clear();

    }

    @Override
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB) {
            MahjongHelper.fixWithLibrariedCards(this.allCard);
        }
        if (this.allCard.isEmpty()) {
            MahjongHelper.fixWithFeaturedCards(this.allCard, this.features);
            MahjongHelper.shuffle(this.allCard);
        }
    }

    @Override
    protected void doStart1() {
        this.doStartTake();
    }

    /**
     * 通知开始打牌
     *
     * @param bankerPlayer
     */
    @Override
    protected void doSendStarTake(IMahjongPlayer bankerPlayer) {
        PCLIMahjongNtfFumbleInfoYYMJ info = new PCLIMahjongNtfFumbleInfoYYMJ();
        info.tingInfo.putAll(bankerPlayer.getTingInfo().getTing());
        if (bankerPlayer.getFumbleCnt() == 1 && !bankerPlayer.getTingInfo().getTing().isEmpty() && bankerPlayer.getCPGNodeCnt() == 0) {
            info.ting = true;
        }
        bankerPlayer.send(CommandId.CLI_NTF_MAHJONG_START_TAKE, info);
    }

    /**
     * 开始打牌
     */
    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithYYMJ info = new PCLIRoomNtfBeginInfoByMJWithYYMJ();
            info.bankerIndex = this.bankerIndex;
            info.crap1 = this.crap1;
            info.crap2 = this.crap2;
            info.myIndex = player.getIndex();
            if (i == this.bankerIndex) {
                player.addHandCardTo(info.myCards, this.lastFumbleCard);
                info.myCards.add(this.lastFumbleCard);
            } else {
                player.addHandCardTo(info.myCards);
            }
            info.roomBriefInfo = this.getRoomBriefInfo();
            info.bureau = player.getBureau();
            info.d = Config.checkWhiteHas(player.getUid(),1);
            info.fangPai = this.fangCard;
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithYYMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithYYMJ();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.d = false;
        roomBeginInfo.fangPai = this.fangCard;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByYYMJ info = new PCLIMahjongNtfGameOverInfoByYYMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

        info.niaoList.addAll(this.niaoList);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByYYMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByYYMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByYYMJ.HuInfo temp = new PCLIMahjongNtfGameOverInfoByYYMJ.HuInfo();
                temp.fang = huInfo.getFang();
                temp.huCard = huInfo.getHuCard();
                temp.paiXing = huInfo.getPaiXing().getClientValue();
                temp.ziMo = huInfo.isZiMo();
                temp.takePlayerUid = huInfo.getTakePlayerUid();
                playerInfo.allHuInfo.add(temp);
            }
            List<EShowFlag> showFlagList = player.getAllShowFlag();
            for (EShowFlag flag : showFlagList) {
                playerInfo.allShow.put(flag.getDesc(), 0);
            }

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByYYMJ.ScoreInfo();
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE, false));
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByYYMJ.FinalResult();
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
        Logs.ROOM.debug("CLI_NTF_ROOM_GAMEOVER %s", info);
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new YYMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIMahjongNtfDeskInfoByYYMJ deskInfo = new PCLIMahjongNtfDeskInfoByYYMJ();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 :  mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        IRoomPlayer banker = this.bankerIndex >= 0 ? this.getRoomPlayer(this.bankerIndex) : null;
        deskInfo.bankerPlayerUid = banker == null || banker.isGuest() ? -1L : banker.getUid();
        deskInfo.fangPai = this.fangCard;
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();

        try {
            this.rwLock.readLock().lock();
            int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer other = (IMahjongPlayer) this.allPlayer[i];
                YYMJMahjongPlayer mjPlayer = (YYMJMahjongPlayer) other;

                if (null == other || other.isGuest()) {
                    continue;
                }
                deskInfo.allOnlineState.put(other.getUid(), !other.isOffline());

                PCLIMahjongNtfDeskInfoByYYMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByYYMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                deskPlayerInfo.ting = mjPlayer.isTing();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByYYMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByYYMJ.CardNode();
                    cardNode.type = node.getType().ordinal();
                    if(node.getTakePlayerIndex()>=0) {
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
                other.addDeskCardTo(deskPlayerInfo.deskCard);
                if (player.getUid() == other.getUid()) {
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

    @Override
    public void stop() {
//        if (this.roomState.compareAndSet(ERoomState.START, ERoomState.STOP)) {
//            Logs.ROOM.debug("%s 房间结束一局", this);
//            if (this.checkAgain()) {
//                this.doSendGameOver(true);
//                this.sendGameCurBureauFinish(true);
//                if (ERoomType.ARENA == this.roomType) {
//                    ((IArenaRoom) this).serviceCharge(false);
//                }
//                this.savePrevInfo();
//                this.clear();
//                this.again();
//                this.autoStart();
//            } else {
//                this.finish();
//                this.doSendGameOver(false);
//                this.sendGameCurBureauFinish(false);
//                if (ERoomType.ARENA == this.roomType) {
//                    ((IArenaRoom) this).serviceCharge(false);
//                    if (this.roomState.compareAndSet(ERoomState.FINISH, ERoomState.AGAIN)) {
//                        ((IArenaRoom) this).doFinish();
//                        this.curBureau = 0;
//                        this.info.setCurBureau(0);
//                        this.info.setDirty(true);
//                        this.doAgain();
//                    }
//                }
//                this.savePrevInfo();
//                this.clear();
//                if (this.autoDestroy) {
//                    this.destroy();
//                }
//            }
//            this.curPlayerCnt = 0;
//        }
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        // 漏胡不能胡
        if (!ziMo && player.isPassCard(EActionOp.HU, huCard)) {
            Logs.ROOM.debug("--- isHu 漏胡不能胡");
            return false;
        }

        YYMJMahjongPlayer mjPlayer = (YYMJMahjongPlayer) player;
        mjPlayer.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        mjPlayer.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        mjPlayer.clearPaiXing();

        int meldCnt = player.getCPGNode().size(); // 碰/杠了多少句话
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        for (CPGNode node : player.getCPGNode()) {
            tempCards.get()[node.getCard1()] += 1;
        }

        long hu = 0;

        // 将一色，不需要成“话”
        if (meldCnt == 0) {
            if (MahjongUtil.isEyeYiSe(tempCards.get())) {
                hu |= MahjongHu.JIANG_YI_SE;
                player.addPaiXing(EPaiXing.YYMJ_JIANG_YI_SE);
            }
        } else {
            if (ziMo) {
                if (MahjongUtil.isEyeYiSe(tempCards.get())) {
                    hu |= MahjongHu.JIANG_YI_SE;
                    player.addPaiXing(EPaiXing.YYMJ_JIANG_YI_SE);
                }
            }
        }

        // 七对
        if (MahjongUtil.isSevenPair(player.getHandCardRaw())) {
            hu |= MahjongHu.QI_DUI;

            // 豪华
            ((YYMJMahjongPlayer) player).setHao(this.countOfHaoHua(player));
            int hao = ((YYMJMahjongPlayer) player).getHao();
            if (hao >= 2) {
                player.addPaiXing(EPaiXing.YYMJ_HAO_HUA_2);
            } else if (hao >= 1) {
                player.addPaiXing(EPaiXing.YYMJ_HAO_HUA);
            } else {
                player.addPaiXing(EPaiXing.YYMJ_QI_DUI);
            }
        }

        // 碰碰胡
        if (MahjongUtil.isPengPengHu(player.getHandCardRaw())) {
            hu |= MahjongHu.PENG_PENG_HU;
            player.addPaiXing(EPaiXing.YYMJ_PENG_PENG_HU);
        }

        if (MahjongUtil.isHu(player.getHandCardRaw(), meldCnt)) {
            //  清一色
            if (MahjongUtil.isQingYiSe(tempCards.get())) {
                hu |= MahjongHu.QING_YI_SE;
                player.addPaiXing(EPaiXing.YYMJ_QING_YI_SE);
            }

            if (player.getHandCardCnt() == 1 || player.getHandCardCnt() == 2) {
                hu |= MahjongHu.QUAN_QIU_REN;
                player.addPaiXing(EPaiXing.YYMJ_QUAN_QIU_REN);
            }

            // 屁胡
            if (hu == 0) {
                hu |= MahjongHu.PI_HU;
                player.addPaiXing(EPaiXing.YYMJ_NORMAL);
            }
        }

        if (hu != 0) {
            if (ziMo && this.prevAction == EActionOp.BAR) { // 杠上开花
                hu |= MahjongHu.GANG_SHANG_KAI_HUA;
                player.addPaiXing(EPaiXing.YYMJ_GANG_KAI);
            } else if (this.curAction == EActionOp.BAR) { // 抢杠胡
                hu |= MahjongHu.QIANG_GANG_HU;
                player.addPaiXing(EPaiXing.YYMJ_QIANG_GANG_HU);
            } else if (ziMo && this.isTianHu(player)) { // 天胡
                hu |= TIAN_HU;
                player.addPaiXing(EPaiXing.YYMJ_TIAN_HU);
            }

            // 门前清
            if (ziMo) {
                if ((this.playingRules & RULE_MEN_QING) != 0
                        && player.getCPGNodeCntWithOutType(CPGNode.EType.BAR_AN) == 0) {
                    hu |= MahjongHu.MEN_QIAN_QING;
                    player.addPaiXing(EPaiXing.YYMJ_MEN_QING);
                }
            } else if (meldCnt == 0) {
                // 门清将将胡可接炮，门清小七对
                if (((this.playingRules & RULE_MEN_QING_JIANG_JIANG_HU) != 0 && (hu & MahjongHu.JIANG_YI_SE) != 0)
                        || (this.playingRules & RULE_MEN_QING_QI_DUI) != 0 && (hu & MahjongHu.QI_DUI) != 0) {
                    hu |= MahjongHu.MEN_QIAN_QING;
                    player.addPaiXing(EPaiXing.YYMJ_MEN_QING);
                }
            }

            // 一条龙
            if ((this.playingRules & RULE_YI_TIAO_LONG) != 0 && player.getHandCardCnt() >= 11) {
                byte suitedCard = -1;
                byte[] startCards = new byte[]{MahjongUtil.MJ_1_TIAO, MahjongUtil.MJ_1_WANG, MahjongUtil.MJ_1_TONG};
                for (int i = 0; suitedCard < 0 && i < startCards.length; i++) {
                    suitedCard = startCards[i];
                    for (byte j = 0; j < 9; j++) {
                        if (!player.hasHandCard((byte) (suitedCard + j), 1)) {
                            suitedCard = -1;
                            break;
                        }
                    }
                }
                if (suitedCard >= 0) {
//                    for (byte i = 0; i < 9; i++) {
                    //player.delHandCard((byte) (suitedCard + i), 1);
//                    }
                    if (MahjongUtil.isHu(player.getHandCardRaw(), meldCnt + 3)) {
                        hu |= MahjongHu.QING_LONG;
                        player.addPaiXing(EPaiXing.YYMJ_YI_TIAO_LONG);
                    }
                }
            }

            // 海底捞
            if (this.haiDiBeginnerUid > 0) {
                hu |= MahjongHu.HAI_DI_LAO;
                player.addPaiXing(EPaiXing.YYMJ_HAI_DI_LAO);
            }

            // 报听胡
            if (((YYMJMahjongPlayer) player).isTing() || (!this.isTianHu(player) && ((YYMJMahjongPlayer) player).getFumbleCnt() == 1) || ((YYMJMahjongPlayer) player).getFumbleCnt() == 0) {
                hu |= BAO_TING_HU;
                player.addPaiXing(EPaiXing.YYMJ_BAO_TING_HU);
            }

            // 门子分
            int huScore = 1; // 第一个门子 x 4，其他门子 x 2
            for (EPaiXing px : player.getAllPaiXing()) {
                if (px == EPaiXing.YYMJ_HAO_HUA) {
                    huScore *= 4;
                } else if (px == EPaiXing.YYMJ_HAO_HUA_2) {
                    huScore *= 8;
                } else {
                    switch (px) {
                        case YYMJ_PENG_PENG_HU:
                        case YYMJ_TIAN_HU:
                        case YYMJ_BAO_TING_HU:
                        case YYMJ_GANG_KAI:
                        case YYMJ_HAI_DI_LAO:
                        case YYMJ_QI_DUI:
                        case YYMJ_QUAN_QIU_REN:
                        case YYMJ_QING_YI_SE:
                        case YYMJ_QIANG_GANG_HU:
                        case YYMJ_JIANG_YI_SE:
                        case YYMJ_MEN_QING:
                        case YYMJ_YI_TIAO_LONG:
                            huScore *= 2;
                            break;
                    }
                }
            }
            player.setScore(Score.MJ_CUR_FANG_SCORE, huScore, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, this.isBig(hu) ? 1 : 2, false);
        } else {
            player.clearPaiXing();
        }
        return hu != 0;
    }

    @Override
    protected EPaiXing getPaiXing(IMahjongPlayer player) {
        return null;
    }

    @Override
    protected int getFang(IMahjongPlayer player) {
        return this.isHu(player) ? player.getScore(Score.MJ_CUR_FANG_SCORE, false) : 0;
    }


    @Override
    protected int getFang(IMahjongPlayer player, byte addCard) {
        if (!MahjongHelper.isCardEnabledByFeatures(addCard, this.features)) {
            return 0;
        }
        return super.getFang(player, addCard);
    }


    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        List<IMahjongPlayer> winners = Arrays.asList(player1, player2, player3);
        // 胡分
        for (IRoomPlayer player : this.allPlayer) {
            if (player != null && !player.isGuest()) {
                player.setScore(Score.MJ_CUR_HU_SCORE, 0, false);
            }
        }
        boolean ziMo = false;
        boolean fg=false;
        int winnerSize = 0;
        for (IMahjongPlayer winner : winners) {
            if (winner != null) {
                winnerSize++;
                winner.addHandCard(huCard);
                this.calcHuScore(takePlayer, winner);
                if (winner == takePlayer) {
                    ziMo = true;
                    this.calcNiaoScore(takePlayer, winner, null, null, ziMo);
                }

                winner.delHandCard(huCard);

                for (EPaiXing px : winner.getAllPaiXing()) {
                    winner.addHu(takePlayer.getUid() == winner.getUid() ? -1 : takePlayer.getUid(), px, huCard);
                    if(px==EPaiXing.YYMJ_HAI_DI_LAO){
                        ((YYMJMahjongPlayer) winner).setIsHaiDi(true);
                        fg=true;
                    }
                }
            }
        }
        if (!ziMo) {
            this.calcNiaoScore(takePlayer, player1, player2, player3, ziMo);
        }

        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest()) {
                continue;
            }
            int score = player.getScore(Score.SCORE, false);
            Logs.ROOM.debug("=== onHu player:：%d，SCORE：%d", player.getUid(), score);
            player.setScore(Score.SCORE, score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, score, true);

            if(((YYMJMahjongPlayer) player).isHaiDi()==false&&fg==true){
                ((YYMJMahjongPlayer) player).delHandCard(this.lastFumbleCard);
            }

        }

        // 实际竞技值计算，并抽水
        this.getRoomHandle().calculateGold();

        YYMJResultRecordAction resultRecordAction = (YYMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            YYMJResultRecordAction.PlayerInfo playerInfo = new YYMJResultRecordAction.PlayerInfo();

            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                ResultRecordAction.HuInfo temp = new ResultRecordAction.HuInfo();
                temp.setFang(huInfo.getFang());
                temp.setHuCard(huInfo.getHuCard());
                temp.setPaiXing(huInfo.getPaiXing().getClientValue());
                temp.setZiMo(huInfo.isZiMo());
                temp.setTakePlayerUid(huInfo.getTakePlayerUid());
                playerInfo.addHuInfo(temp);
            }
            playerInfo.addAllClientShow(player.getAllShowFlag());
            YYMJResultRecordAction.ScoreInfo scoreInfo = new YYMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setNiaoScore(player.getScore(Score.MJ_CUR_NIAO_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        this.bankerIndex = winnerSize > 1 ? takePlayer.getIndex() : player1.getIndex();
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        YYMJResultRecordAction resultRecordAction = (YYMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        // 处理罚分
        if (this.fine > 0) {
            List<IRoomPlayer> tingPlayers = new ArrayList<>();
            for (IRoomPlayer player : this.allPlayer) {
                if (player != null && !player.isGuest() && ((YYMJMahjongPlayer) player).getTingInfo().isTing()) {
                    tingPlayers.add(player);
                }
            }
            for (IRoomPlayer player : this.allPlayer) {
                if (player != null && !player.isGuest() && !tingPlayers.contains(player)) {
                    for (IRoomPlayer winner : tingPlayers) {
                        int score = this.fine;
                        winner.addScore(Score.SCORE, score, false);
                        player.addScore(Score.SCORE, -score, false);
                    }
                }
            }
        }

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }

            int score = this.getScore(player.getScore(Score.SCORE, false) + player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.setScore(Score.SCORE, score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, score, true);
            if (this.onCheckOver()) {
                player.delHandCard(player.getLastFumbleCard());
            }
//            YYMJResultRecordAction.PlayerInfo playerInfo = new YYMJResultRecordAction.PlayerInfo();
//            YYMJResultRecordAction.ScoreInfo scoreInfo = new YYMJResultRecordAction.ScoreInfo();
//            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
//            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
//            playerInfo.setScoreInfo(scoreInfo);
//
//            resultRecordAction.addResult(player.getUid(), playerInfo);
        }
        // 实际竞技值计算，并抽水
        this.getRoomHandle().calculateGold();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            YYMJResultRecordAction.PlayerInfo playerInfo = new YYMJResultRecordAction.PlayerInfo();
            YYMJResultRecordAction.ScoreInfo scoreInfo = new YYMJResultRecordAction.ScoreInfo();
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }
        this.bankerIndex = this.lastFumbleIndex;
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public Record getRecord() {
        if (this.record == null) {
            this.record = new YYMJMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        return false;
    }

    @Override
    protected boolean isBar1(IMahjongPlayer player, byte takeCard, boolean fangGang, int flag) {
        int numHaiDiCard = (this.playingRules & RULE_GONG_HAI_DI) == 0 ? this.getCurPlayerCnt() : 1;
        boolean bar = super.isBar(player, takeCard, fangGang);
        if (((YYMJMahjongPlayer) player).isTing() && !isCanBar(player, bar, takeCard, fangGang)) {
            return false;
        }
        return this.allCard.size() > numHaiDiCard && bar;
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        int numHaiDiCard = (this.playingRules & RULE_GONG_HAI_DI) == 0 ? this.getCurPlayerCnt() : 1;
        return this.allCard.size() > numHaiDiCard && super.isBar(player, takeCard, fangGang);
    }

    @Override
    public ErrorCode ting(IPlayer player, Object... param) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s 听牌的人:%s 房间还没开始, 无法听牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s 听牌的人:%s 没有动作, 无法听牌", this, player);
            return ErrorCode.ROOM_NOT_ACTION;
        }
        if (param.length < 1) {
            Logs.ROOM.warn("%s 听牌的人:%s 无效参数, 无效听牌", this, player);
            return ErrorCode.ROOM_MJ_INVALID_CARD;
        }
        IMahjongPlayer tingPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());

        boolean ting = (boolean) param[5];
        IAction iaction = this.action.peek();
        if (ting) {
            this.action.remove(iaction);
            MahjongTingAction tingAction = new MahjongTingAction(this, tingPlayer, timeout);
            this.addAction(tingAction);


        }
        IAction action = this.action.peek();

        if (action instanceof MahjongTingAction) {
            MahjongTingAction tingAction = (MahjongTingAction) action;
            if (player.getUid() != tingAction.getPlayer().getUid()) {
                Logs.ROOM.warn("%s 听牌的人:%s 当前轮打牌人是:%s 不是你, 无效听牌", this, player, tingAction.getPlayer().getUid());
                return ErrorCode.ROOM_NOT_CUR_PLAYER;
            }
            byte card = (byte) param[0];
            if (card <= 0 || card >= MahjongUtil.MJ_CARD_KINDS) {
                Logs.ROOM.warn("%s 听牌的人:%s 非法牌:%d, 无效听牌", this, player, card);
                return ErrorCode.ROOM_MJ_INVALID_CARD;
            }
            YYMJMahjongPlayer mahjongPlayer = (YYMJMahjongPlayer) tingAction.getPlayer();
            if (mahjongPlayer.isAutoTake()) {
                Logs.ROOM.warn("%s 听牌的人:%s 主动听 无法手动操作 card:%d, 无效听牌", this, player, card);
                return ErrorCode.ROOM_MJ_AUTO_TAKE;
            }
            if (mahjongPlayer.isOver()) {
                Logs.ROOM.warn("%s 听牌的人:%s 已经结束 无法听牌操作 card:%d, 无效听牌", this, player, card);
                return ErrorCode.ROOM_MJ_OVER;
            }
            if (!this.isCanTakeCard(mahjongPlayer, card)) {
                Logs.ROOM.warn("%s 听牌的人:%s 手牌不足 card:%d, 无效听牌", this, player, card);
                return ErrorCode.ROOM_MJ_HAND_CARD_NOT_ENOUGH;
            }
            if (!mahjongPlayer.canManualTake()) {
                Logs.ROOM.warn("%s 听牌的人:%s 不能手动打牌, 无法听牌", this, player);
                return ErrorCode.ROOM_MJ_ALREADY_LIANG_PAI;
            }
            // 检测打该张牌是否能听
            Set<Byte> huCards = mahjongPlayer.getTingInfo().getHuCard(card);
            if (huCards == null) {
                Logs.ROOM.warn("%s 听牌的人:%s 打出的牌不符合听牌条件 card:%d, 无法听牌", this, player, card);
                return ErrorCode.REQUEST_INVALID;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            tingAction.getPlayer().clearOperationTimeoutCnt();
            tingAction.setOp(EActionOp.TING);
            tingAction.setParam(param);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 听牌的人:%s 本来不是听牌动作, 无法听牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }

    @Override
    public void onTing(IMahjongPlayer takePlayer, boolean auto, Object... param) {
        byte card = (byte) param[0];
        takePlayer.clearPassCard();
        takePlayer.takeCard(card);
        ++this.deskCard[card];

        if (!this.isOverBanker && this.bankerIndex != takePlayer.getIndex()) {
            this.isOverBanker = true;
        }

        Logs.ROOM.debug("%s 听牌的人:%s 打牌 card:%s auto:%s param:%s", this, takePlayer, MahjongUtil.getCardStr(card), auto, Arrays.toString(param));

        this.setCurOp(takePlayer, EActionOp.TING, card);
        this.doSendTing(takePlayer, auto, param);

        MahjongWaitAction waitAction = this.getWaitAction(takePlayer, card, false,2);
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            if (this.onCheckOver()) {
                this.onHuangZhuang(this.curBureau < this.bureau);
            } else {
                this.onFumble((IMahjongPlayer) this.getNextRoomPlayer(takePlayer.getIndex()));
            }
        }

        ((YYMJMahjongPlayer) takePlayer).setTing(true);
        ((YYMJMahjongPlayer) takePlayer).setManualTake(false);
        //takePlayer.getTingInfo().clear();
    }

    private void doSendTing(IMahjongPlayer player, boolean auto, Object... param) {
        byte card = (byte) param[0];
        byte last = (byte) param[1];
        byte index = (byte) param[2];
        byte outputCardIndex = (byte) param[3];
        int length = (int) param[4];
        PCLIMahjongNtfTingInfo info;
        if (auto) {
            info = new PCLIMahjongNtfTingInfo();
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
        info = new PCLIMahjongNtfTingInfo();
        info.uid = player.getUid();
        info.cardValue = card;
        info.isLast = last;
        info.outputCardIndex = outputCardIndex;
        info.length = length;
        info.index = index;
        info.auto = auto;
        if (auto) {
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_TING, info, player.getUid());
        } else {
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_TING, info);
        }

        TingRecordAction recordAction = new TingRecordAction(player.getUid(), card);
        this.getRecord().addAction(recordAction);
    }

    @Override
    public void doSendCanTing(IMahjongPlayer player, boolean broadcast) {
        PCLIMahjongNtfCanTingInfo info = new PCLIMahjongNtfCanTingInfo();
        info.uid = player.getUid();
        if (broadcast) {
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_CAN_TING, info);
        } else {
            player.send(CommandId.CLI_NTF_MAHJONG_CAN_TING, info);
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
            if (player.getUid() != ((MahjongTakeAction) action).getPlayer().getUid()) {
                Logs.ROOM.warn("%s 打牌的人:%s 当前轮打牌人是:%s 不是你, 无效打牌", this, player, ((MahjongTakeAction) action).getPlayer().getUid());
                return ErrorCode.ROOM_NOT_CUR_PLAYER;
            }
            byte card = (byte) param[0];
            if (card <= 0 || card >= MahjongUtil.MJ_CARD_KINDS) {
                Logs.ROOM.warn("%s 打牌的人:%s 非法牌:%d, 无效打牌", this, player, card);
                return ErrorCode.ROOM_MJ_INVALID_CARD;
            }
            IMahjongPlayer mahjongPlayer = ((MahjongTakeAction) action).getPlayer();
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
            if (!mahjongPlayer.canManualTake()&&!this.isHu(mahjongPlayer)) {
                Logs.ROOM.warn("%s 打牌的人:%s 不能手动打牌, 无法打牌", this, player);
                return ErrorCode.ROOM_MJ_ALREADY_LIANG_PAI;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongTakeAction) action).getPlayer().clearOperationTimeoutCnt();
            ((MahjongTakeAction) action).setOp(EActionOp.TAKE);
            ((MahjongTakeAction) action).setParam(param);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 打牌的人:%s 本来不是打牌动作, 无法打牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }
    @Override
    public void doSendFumble(IMahjongPlayer player, byte card) {
        PCLIMahjongNtfFumbleInfoYYMJ info = new PCLIMahjongNtfFumbleInfoYYMJ();
        info.uid = player.getUid();
        info.index = player.getIndex();
        info.auto = true;
        info.value = card;
        info.remainCard = this.allCard.size();
        player.addHandCardTo(info.handCard, card);
        info.tingInfo.putAll(player.getTingInfo().getTing());
        if (player.getFumbleCnt() == 1 && !player.getTingInfo().getTing().isEmpty() && player.getCPGNodeCnt() == 0) {
            info.ting = true;
        }
        player.send(CommandId.CLI_NTF_MAHJONG_FUMBLE, info);

        info = new PCLIMahjongNtfFumbleInfoYYMJ();
        info.uid = player.getUid();
        info.index = player.getIndex();
        info.auto = true;
        info.remainCard = this.allCard.size();
        info.value = -1;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_FUMBLE, info, player.getUid());

        ((MahjongRecord) this.getRecord()).addFumbleRecordAction(player.getUid(), card);
    }

    @Override
    protected void doFumbleAfter(IMahjongPlayer player, byte card) {
        int numHaiDiCard = (this.playingRules & RULE_GONG_HAI_DI) == 0 ? this.getCurPlayerCnt() : 1;
        if (this.allCard.size() < numHaiDiCard) {
            if (this.haiDiBeginnerUid <= 0) {
                this.haiDiBeginnerUid = player.getUid();
            }
            IMahjongPlayer nextPlayer = (IMahjongPlayer) this.getNextRoomPlayer(player.getIndex());
            if ((this.playingRules & RULE_GONG_HAI_DI) != 0 && nextPlayer.getUid() != haiDiBeginnerUid) {
                // 公海底，给下一位玩家同样的牌
                this.allCard.add(card);
            }

            YYMJMahjongRoom self = this;
            DelayAction delayAction = new DelayAction(this, 200);
            // 能胡则自动胡
            if (this.isHu(player, player.getUid(), true, card)) {
                delayAction.setCallback(new ICallback<Object>() {
                    @Override
                    public void call(Object... o) {
                        self.onHu(player, player, card);
                    }
                });
            } else {
                // 下一个玩家摸牌
                delayAction.setCallback(new ICallback<Object>() {
                    @Override
                    public void call(Object... o) {
                        if (self.allCard.isEmpty()) {
                            self.onHuangZhuang(self.curBureau < self.bureau);//self.checkAgain());
                        } else {
                            self.onFumble(nextPlayer);
                        }
                    }
                });
            }
            this.addAction(delayAction);
        } else if (((YYMJMahjongPlayer) player).isTing()) {
            if (this.isHu(player, player.getUid(), true, card) || this.isBar1(player, card, false, -1)) {
                super.doFumbleAfter(player, card);
            } else {
                YYMJMahjongRoom self = this;
                DelayAction delayAction = new DelayAction(this, 200);
                delayAction.setCallback(new ICallback<Object>() {
                    @Override
                    public void call(Object... o) {
                        self.onTake(player, true, card, (byte) 0, (byte) 0, player.getHandCardIndex(card), 0);
                    }
                });
                this.addAction(delayAction);
            }
        } else {
//            super.doFumbleAfter(player, card);
            MahjongWaitAction waitAction = null;
            if (this.isMustHu()) {
                waitAction = this.getWaitActionWithOnlyHu(player, card);
            }
            if (null != waitAction) {
                this.addAction(waitAction);
            } else if (this.isHu(player, player.getUid(), true, card)) {
                long timeout = player.getTimeout(this.timeout);
                if (this.isLastCard) {
                    timeout = BaseMahjongRoom.LAST_CARD_TAKE_TIMEOUT;
                } else if (player.isAutoTake()) {
                    timeout = BaseMahjongRoom.AUTO_TAKE_TIMEOUT;
                }
                MahjongTakeAction action = new MahjongTakeAction(this, player, timeout);
                action.setParam(card);
                this.addAction(action);
                //doFumbleAfter2(player, card);
            } else {
                MahjongTakeAction action = this.getTakeAction(player, card);
                this.addAction(action);
            }
        }
    }

    private void calcNiaoScore(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, boolean ziMo) {
        // 抓鸟

        byte birdCard = -1;
        for (int i = 0; i < this.birds; i++) {
            if (this.allCard.isEmpty()) {
                if (birdCard < 0) {
                    birdCard = this.curCard;
                }
            } else {
                birdCard = this.allCard.removeFirst();
            }
            this.niaoList.add(birdCard);
            IRoomPlayer birdPlayer = this.getRoomPlayer(this.bankerIndex);
            int pos = this.bankerIndex;
            int value=birdCard % 9;
            int index = (value==0?9:value) % this.playerNum;
            if (birdCard == 6) {
                int bankRight = this.bankerIndex + 1;
                if (bankRight >= this.playerNum) {
                    bankRight = 0;
                }
                birdPlayer = this.getRoomPlayer(bankRight);
                if(birdPlayer==null)
                	 birdPlayer = this.getNextRoomPlayer(bankRight);
            } else {
                switch (index) {
                    case 0:
                        if (this.playerNum == 2) {
                            pos += 1;
                        } else if (this.playerNum == 3) {
                            pos += 2;
                        } else {
                            pos += 3;
                        }
                        birdPlayer = this.getRoomPlayer(pos % this.playerNum);
                        if(birdPlayer==null)
                        	birdPlayer = this.getNextRoomPlayer(pos % this.playerNum);
                        break;
                    case 1:
                        birdPlayer = this.getRoomPlayer(pos);
                        if(birdPlayer==null)
                        	birdPlayer = this.getNextRoomPlayer(pos);
                        break;
                    case 2:
                        birdPlayer = this.getRoomPlayer((pos + 1) % this.playerNum);
                        if(birdPlayer==null)
                        	birdPlayer = this.getNextRoomPlayer((pos + 1) % this.playerNum);
                        break;
                    case 3:
                        birdPlayer = this.getRoomPlayer((pos + 2) % this.playerNum);
                        if(birdPlayer==null)
                        	birdPlayer = this.getNextRoomPlayer((pos + 2) % this.playerNum);
                        break;
                }

            }


            Logs.ROOM.debug("===  birdPlayer:：%d", birdPlayer.getUid());
            int player1Score = 0;
            for (IRoomPlayer player : this.allPlayer) {
                if (player == null || player.isGuest()) {
                    continue;
                }
                if (ziMo) {
                    int score = player.getScore(Score.MJ_CUR_HU_SCORE, false) * 2;
                    if (maxTop != -1 && Math.abs(score) > maxTop) score = -maxTop;
                    if (birdPlayer == player1 || birdPlayer == player) {
                        player.setScore(Score.MJ_CUR_HU_SCORE, score, false);
                    }
                    if (player1 != player)
                        player1Score += player.getScore(Score.MJ_CUR_HU_SCORE, false);
                }
            }
            if (ziMo) player1.setScore(Score.MJ_CUR_HU_SCORE, -player1Score, false);
            int takePlayerScore = 0;
            if (player1 != null && !ziMo) {
                if (birdPlayer == player1 || birdPlayer == takePlayer) {
                    int score = player1.getScore(Score.MJ_CUR_HU_SCORE, false) * 2;
                    if (maxTop != -1 && Math.abs(score) > maxTop) score = maxTop;
                    player1.setScore(Score.MJ_CUR_HU_SCORE, score, false);

                }
                takePlayerScore += player1.getScore(Score.MJ_CUR_HU_SCORE, false);

            }
            if (player2 != null && !ziMo) {
                if (birdPlayer == player2 || birdPlayer == takePlayer) {
                    int score = player2.getScore(Score.MJ_CUR_HU_SCORE, false) * 2;
                    if (maxTop != -1 && Math.abs(score) > maxTop) score = maxTop;
                    player2.setScore(Score.MJ_CUR_HU_SCORE, score, false);

                }
                takePlayerScore += player2.getScore(Score.MJ_CUR_HU_SCORE, false);

            }

            if (player3 != null && !ziMo) {
                if (birdPlayer == player3 || birdPlayer == takePlayer) {
                    int score = player3.getScore(Score.MJ_CUR_HU_SCORE, false) * 2;
                    if (maxTop != -1 && Math.abs(score) > maxTop) score = maxTop;

                    player3.setScore(Score.MJ_CUR_HU_SCORE, score, false);

                }
                takePlayerScore += player3.getScore(Score.MJ_CUR_HU_SCORE, false);

            }
            if (!ziMo && takePlayerScore != 0) takePlayer.setScore(Score.MJ_CUR_HU_SCORE, -takePlayerScore, false);


        }
        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest()) {
                continue;
            }
            if (ziMo) {
                if ((this.playingRules & RULE_YI_ZI_QIAO) != 0 && player1.hasPaiXing(EPaiXing.YYMJ_QUAN_QIU_REN)) {
                    player1.addScore(Score.MJ_CUR_HU_SCORE, 2, false);
                    player.addScore(Score.MJ_CUR_HU_SCORE, -2, false);
                }
            } else {
                if ((this.playingRules & RULE_YI_ZI_QIAO) != 0 && ((IMahjongPlayer) player).hasPaiXing(EPaiXing.YYMJ_QUAN_QIU_REN)) {
                    player.addScore(Score.MJ_CUR_HU_SCORE, 2, false);
                    takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -2, false);
                }
            }
        }
        // 小结
        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest()) {
                continue;
            }

            int score = this.getScore(player.getScore(Score.MJ_CUR_HU_SCORE, false) + player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, score, false);
        }
    }

    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1) {
        // 基础分
        int huScore = 1;
        // 门子分
        // 第一个门子 x 4，其他门子 x 2
        if (player1.hasPaiXing(EPaiXing.YYMJ_NORMAL) && player1.getAllPaiXing().size() == 1) {
            huScore *= 1;
            if (player1 != takePlayer && ((YYMJMahjongPlayer) takePlayer).isTing()) {
                huScore *= 4;
            }
        } else {
            huScore *= 2;
            if (player1 != takePlayer && ((YYMJMahjongPlayer) takePlayer).isTing()) {
                huScore *= 2;
            }
        }

        for (EPaiXing px : player1.getAllPaiXing()) {
            if (px == EPaiXing.YYMJ_HAO_HUA) {
                huScore *= 4;
                Logs.ROOM.debug("== calcHuScore, px:%s, huScore:%d", px, huScore);
            } else if (px == EPaiXing.YYMJ_HAO_HUA_2) {
                huScore *= 8;
                Logs.ROOM.debug("== calcHuScore, px:%s, huScore:%d", px, huScore);
            } else {
                switch (px) {
                    case YYMJ_PENG_PENG_HU:
                    case YYMJ_TIAN_HU:
                    case YYMJ_BAO_TING_HU:
                    case YYMJ_GANG_KAI:
                    case YYMJ_HAI_DI_LAO:
                    case YYMJ_QI_DUI:
                    case YYMJ_QUAN_QIU_REN:
                    case YYMJ_QING_YI_SE:
                    case YYMJ_QIANG_GANG_HU:
                    case YYMJ_JIANG_YI_SE:
                    case YYMJ_MEN_QING:
                    case YYMJ_YI_TIAO_LONG:
                        huScore *= 2;
                        Logs.ROOM.debug("== calcHuScore, px:%s, huScore:%d", px, huScore);
                        break;
                }
            }
        }


        boolean ziMo = takePlayer.getUid() == player1.getUid();
        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest() || player.getUid() == player1.getUid()) {
                continue;
            }
            if (ziMo || player.getUid() == takePlayer.getUid()) {
                if (maxTop != -1 && huScore > maxTop) huScore = maxTop;
                player1.addScore(Score.MJ_CUR_HU_SCORE, huScore, false);
                player.addScore(Score.MJ_CUR_HU_SCORE, -huScore, false);
            }
        }


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

    private boolean isCanBar(IMahjongPlayer player, boolean bar, byte takeCard, boolean fangGang) {
        if (bar) {
        HashMap<Byte, HashMap<Byte, Integer>> tingInfo = player.getTingInfo().getTing();
        List<Byte> listTingInfo = new ArrayList<Byte>();
        for (Map.Entry<Byte, HashMap<Byte, Integer>> entry : tingInfo.entrySet()) {
            if(fangGang){
                if (entry.getKey() == player.getLastTakeCard()) {
                    HashMap<Byte, Integer> ting1 = entry.getValue();
                    for (Map.Entry<Byte, Integer> entry1 : ting1.entrySet()) {
                        listTingInfo.add(entry1.getKey());

                    }
                }
            }else{
                if (entry.getKey()==takeCard) {
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
                temp = new CPGNode(player.getIndex(), CPGNode.EType.BAR_MING, takeCard);
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

        }
        return false;
    }

    @Override
    protected MahjongWaitAction getWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu, int flag) {
        MahjongWaitAction waitAction = null;
        int canEatIndex = (player.getIndex() + 1) % this.playerNum;
        for (int i = 0; i < this.playerNum; ++i) {
            YYMJMahjongPlayer otherPlayer = (YYMJMahjongPlayer) this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid() || otherPlayer.isOver()) {
                continue;
            }
            boolean bar = !onlyHu && this.isBar1(otherPlayer, takeCard, true, flag);
            boolean hu = this.isHu(otherPlayer, player.getUid(), takeCard);
            if ((hu && otherPlayer.isTing() && !otherPlayer.isFirstHu()) || (otherPlayer.getFumbleCnt() == 0 && hu)) {
                otherPlayer.setFirstHu(true);
            }

            if (otherPlayer.isTing() && otherPlayer.isFirstHu()) {
                if (otherPlayer.isFlag()) {
                    hu = false;
                }
                otherPlayer.setFlag(true);
            }

//            if(otherPlayer.isTing()&& !isCanBar(otherPlayer,bar,takeCard)){bar=false;
//            }
            boolean bump = !onlyHu && !otherPlayer.isTing() && (bar || this.isBump(otherPlayer, takeCard));
            boolean eat = !onlyHu && !otherPlayer.isTing() && (i == canEatIndex && this.isEat(otherPlayer, takeCard));
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

            YYMJWaitInfo waitInfo = new YYMJWaitInfo();
            waitInfo.setPlayerUid(otherPlayer.getUid());
            waitInfo.setIndex(otherPlayer.getIndex());
            waitInfo.setTimeout(timeout);
            waitInfo.setHu(hu);
            waitInfo.setBar(bar);
            waitInfo.setBump(bump);
            waitInfo.setEat(eat);
            // waitInfo.setTing(player.getTingInfo().isTing());

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
    public void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat, byte takeCard) {
        this.doSendCanOperate(player, hu, bar, bump, eat, false, takeCard);
    }

    private void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat, boolean ting, byte takeCard) {
        PCLIMahjongNtfCanOperateInfoByYYMJ info = new PCLIMahjongNtfCanOperateInfoByYYMJ(bump, bar, hu, eat, ting, takeCard);
        player.send(CommandId.CLI_NTF_MAHJONG_CAN_OPERATE, info);
    }

    // 是否大胡
    private boolean isBig(long hu) {
        return (hu & MahjongHu.PI_HU) != 0;
    }

    // 豪华的个数，仅限七对牌型
    private int countOfHaoHua(IMahjongPlayer player) {
        int count = 0;
        for (byte i = MahjongUtil.MJ_1_WANG; i < MahjongUtil.MJ_CARD_KINDS; i++) {
            if (MahjongHelper.isCardEnabledByFeatures(i, this.features)) {
                if (player.getHandCardCnt(i) == 4) {
                    count++;
                }
            }
        }
        return Math.min(count, 3);
    }

}
