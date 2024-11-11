package com.xiuxiu.app.server.room.normal.mahjong2.hsmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByHSMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByHSMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfLaiPiInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfYangPaiInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithHSMJ;
import com.xiuxiu.app.server.Config;
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
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeActionByHS;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeActionByYX;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongFeatures;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHelper;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHu;
import com.xiuxiu.app.server.room.player.mahjong2.HSMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.MahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.*;

import java.util.*;

/**
 * 黄石麻将
 */
@GameInfo(gameType = GameType.GAME_TYPE_HSMJ)
public class HSMJMahjongRoom extends MahjongRoom implements IMahjongYangPai {
    private long features;

    private byte fangCard = -1; // 翻出的牌
    protected ArrayList<Byte> piList = new ArrayList<>(); // 皮列表

    private static final int HU_LOWEST_POINTS = 10; // 胡牌所要求的最低分数
    private static final int TOP_POINTS = 40;
    private static final int GOLD_TOP_POINTS = 50;

    private static final int YANG_TIMEOUT = 15 * 1000; // 仰牌计时

    // 大胡列表
    private static final List<Long> BIG_HU = Arrays.asList(
            MahjongHu.PENG_PENG_HU,
            MahjongHu.QING_YI_SE,
            MahjongHu.JIANG_YI_SE,
            MahjongHu.QI_DUI
            
    );

    // 规则-封顶
    private int ruleTop;
    private static final int RULE_TOP_1 = 1; // 40封顶50金顶
    private static final int RULE_TOP_NONE = 2; // 不封顶

    // 规则-种类
    private int ruleType;
    private static final int RULE_TYPE_ZHONG = 1; // 红中杠
    private static final int RULE_TYPE_FA = 2; // 发财杠

    // 规则-玩法
    private int rulePlay;
    private static final int RULE_WITH_TIMER = 0x0001; // 计时
    private static final int RULE_WITH_POINT_LIMIT = 0x0002; // 10分
    private static final int RR_DETECTION_IP = 0x0004; // IP检测开关(0 关闭 1 开启)

    public HSMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public HSMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.ruleTop = this.getRule().getOrDefault(RoomRule.RR_HSMJ_SCORE_TOP, RULE_TOP_1);
        this.ruleType = this.getRule().getOrDefault(RoomRule.RR_HSMJ_BAR_TYPE, RULE_TYPE_ZHONG);
        this.rulePlay = this.getRule().getOrDefault(RoomRule.RR_PLAY, RULE_WITH_TIMER);
        this.detectionIP = 0 != (this.rulePlay & RR_DETECTION_IP);
    }

    @Override
    protected void doShuffle() {
        // 默认包含序数牌和箭牌
        this.features = MahjongFeatures.WITH_TONG_ZI | MahjongFeatures.WITH_TIAO_ZI | MahjongFeatures.WITH_WAN_ZI
                | MahjongFeatures.WITH_JIAN;
        if (this.getCurPlayerCnt() == 2) { // 2人时去掉万子
            this.features ^= MahjongFeatures.WITH_WAN_ZI;
        }
        this.features |= MahjongFeatures.ENABLE_CHI | MahjongFeatures.ENABLE_PENG | MahjongFeatures.ENABLE_MING_GANG
                | MahjongFeatures.ENABLE_AN_GANG;
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
        this.startWithFanLaiZi();
    }
    private void startWithFanLaiZi() {
        // 发完牌后，牌墙上第一张翻出
        this.fangCard = this.allCard.removeFirst();

        // 确定赖子和皮
        this.laiZiCard = MahjongHelper.getLaiZiCardByFengJian(this.fangCard);
        if (MahjongHelper.isJianPai(this.laiZiCard)) {
            if (this.ruleType == RULE_TYPE_ZHONG && this.laiZiCard == MahjongUtil.MJ_Z_FENG) {
                this.laiZiCard = MahjongUtil.MJ_F_FENG;
            } else if (this.ruleType == RULE_TYPE_FA && this.laiZiCard == MahjongUtil.MJ_F_FENG) {
                this.laiZiCard = MahjongUtil.MJ_BAI_FENG;
            }
        }
        this.piList.add(this.ruleType == RULE_TYPE_ZHONG ? MahjongUtil.MJ_Z_FENG : MahjongUtil.MJ_F_FENG);

        // 通知赖子和皮子信息
        PCLIMahjongNtfLaiPiInfo ntfLaiPi = new PCLIMahjongNtfLaiPiInfo();
        ntfLaiPi.fangPai = this.fangCard;
        ntfLaiPi.laiZi = this.laiZiCard;
        ntfLaiPi.piList.addAll(this.piList);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_LAI_PI, ntfLaiPi);

        ((RecordMahjongRoomBriefInfo) this.getRecord().getRoomInfo()).setLaiZiCard(this.laiZiCard);
        ((RecordMahjongRoomBriefInfo) this.getRecord().getRoomInfo()).setPiList(this.piList);

        this.doStartTake();
    }
    @Override
    public void beginYangPai() {
    }

    @Override
    public void endYangPai() {
    }
    @Override
    public void doSendBeginYangPai(IMahjongPlayer player) {
    }

    @Override
    public void doSendYangPaiInfo(IMahjongPlayer player, List<Byte> cards) {
    }
    @Override
    public ErrorCode yangPai(IPlayer player, List<Byte> cards) {
        HSMJMahjongPlayer mjPlayer = (HSMJMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mjPlayer || mjPlayer.isGuest()) {
            Logs.ROOM.warn("%s %s 观察者, 无法仰牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法仰牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (mjPlayer.getCanOperate()) {
            Logs.ROOM.warn("%s %s 已经操作过, 无法仰牌", this, player);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (cards.size() % 3 != 0 || cards.size() > 12) {
            Logs.ROOM.warn("%s %s %s 仰牌数量不对, 无法仰牌", this, player, cards);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (!cards.isEmpty()) {
            Map<Byte, Integer> cardCount = new HashMap<>();
            for (byte card : cards) {
                if (card != MahjongUtil.MJ_Z_FENG && card != MahjongUtil.MJ_F_FENG && card != MahjongUtil.MJ_BAI_FENG) {
                    Logs.ROOM.warn("%s %s %s 只有中发白能仰, 无法仰牌", this, player, cards);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
                int count = cardCount.getOrDefault(card, 0) + 1;
                cardCount.put(card, count);
            }
            for (int count : cardCount.values()) {
                if (count != cards.size() / 3) {
                    Logs.ROOM.warn("%s %s %s 仰牌不成句, 无法仰牌", this, player, cards);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
            }
        }
        ((MahjongRecord) this.record).addYangPaiRecordAction(player.getUid(), cards);
        for (byte card : cards) {
            mjPlayer.delHandCard(card);
        }
        for (int i = 0; i < cards.size() / 3; i++) {
            mjPlayer.addCPGWithAnyThree(-1, MahjongUtil.MJ_Z_FENG, MahjongUtil.MJ_F_FENG, MahjongUtil.MJ_BAI_FENG);
        }
        PCLIMahjongNtfYangPaiInfo info = new PCLIMahjongNtfYangPaiInfo();
        info.playerUid = player.getUid();
        info.cards.addAll(cards);
        this.generateTingInfo(mjPlayer);
        info.tingInfo.putAll(mjPlayer.getTingInfo().getTing());
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_YANG_PAI, info);
        return ErrorCode.OK;
    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithHSMJ info = new PCLIRoomNtfBeginInfoByMJWithHSMJ();
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
            info.laiZi = this.laiZiCard;
            info.piList = this.piList;
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithHSMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithHSMJ();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.d = false;
        roomBeginInfo.fangPai = this.fangCard;
        roomBeginInfo.laiZi = this.laiZiCard;
        roomBeginInfo.piList = this.piList;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByHSMJ info = new PCLIMahjongNtfGameOverInfoByHSMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByHSMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByHSMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByHSMJ.HuInfo temp = new PCLIMahjongNtfGameOverInfoByHSMJ.HuInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByHSMJ.ScoreInfo();
            playerInfo.score.fangScore = 0;
            playerInfo.score.gangScore = 0;
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE, false));
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByHSMJ.FinalResult();
                playerInfo.finalResult.anGangCnt = player.getScore(Score.ACC_MJ_AN_GANG_CNT, true);
                playerInfo.finalResult.fangPaoCnt = player.getScore(Score.ACC_MJ_DIAN_PAO_CNT, true);
                playerInfo.finalResult.huCnt = player.getScore(Score.ACC_MJ_HU_CNT, true);
                playerInfo.finalResult.mingGangCnt = player.getScore(Score.ACC_MJ_MING_GANG_CNT, true);
                playerInfo.finalResult.ziMoCnt = player.getScore(Score.ACC_MJ_ZIMO_CNT, true);
                playerInfo.finalResult.score = player.getScore(Score.ACC_TOTAL_SCORE, true);
            }

            info.allPlayer.put(player.getUid(), playerInfo);
        }
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, info);
        Logs.ROOM.debug("CLI_NTF_ROOM_GAMEOVER %s", info);
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer&& !this.watchList.contains(player.getUid())) {
            return;
        }

        PCLIMahjongNtfDeskInfoByHSMJ deskInfo= new PCLIMahjongNtfDeskInfoByHSMJ();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau =	null == mahjongPlayer ? 0 :  mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        IRoomPlayer banker = this.bankerIndex >= 0 ? this.getRoomPlayer(this.bankerIndex) : null;
        deskInfo.bankerPlayerUid = banker == null || banker.isGuest() ? -1L : banker.getUid();
        deskInfo.fangPai = this.fangCard;
        deskInfo.laiZi = this.laiZiCard;
        deskInfo.piList = this.piList;
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();
        IAction action = null;
        if (!this.action.isEmpty()){
            action = this.action.peek();
            if (!(action instanceof MahjongTakeAction)) {
                action = null;
            }
        }
        try {
            this.rwLock.readLock().lock();
            int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer other = (IMahjongPlayer) this.allPlayer[i];
                if (null == other || other.isGuest()) {
                    continue;
                }
                int yangCnt = 0;        // 仰牌计数
                for (CPGNode node : other.getCPGNode()) {
                    if (node.getType() == CPGNode.EType.ANY_THREE) {
                        yangCnt++;
                        continue;
                    }
                }
                deskInfo.allOnlineState.put(other.getUid(), !other.isOffline());
                PCLIMahjongNtfDeskInfoByHSMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByHSMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                deskPlayerInfo.yangPai= (null != other) && (((HSMJMahjongPlayer) other).getCanOperate());
                deskPlayerInfo.fumbleCnt= other.getFumbleCnt();
                deskPlayerInfo.tingInfo = ((MahjongPlayer)other).getTingInfo().getTing();
                deskPlayerInfo.yangPaiCnt=yangCnt;
                deskPlayerInfo.needCheckHu = true;
                if (action != null) {
                    if (((MahjongTakeAction) action).getPlayer().getUid() == other.getUid()) {
                        deskPlayerInfo.needCheckHu = ((MahjongTakeAction) action).getOp() != EActionOp.HU;
                    }
                }
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByHSMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByHSMJ.CardNode();
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
    protected void doFumbleAfter(IMahjongPlayer player, byte card) {
        MahjongWaitAction waitAction = null;
        if (this.isMustHu()) {
            waitAction = this.getWaitActionWithOnlyHu(player, card);
        }
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            if(this.isHu(player,player.getUid(),true,card)){
                MahjongTakeActionByHS action = this.getTakeAction1(player, card);
                action.setOp(EActionOp.HU);
                this.addAction(action);
            }else{
                MahjongTakeAction  action = this.getTakeAction(player, card);
                this.addAction(action);
            }

        }
    }
    /**
     * 打牌
     * @param player
     * @param defaultTakeCard
     * @return
     */
    protected MahjongTakeActionByHS getTakeAction1(IMahjongPlayer player, byte defaultTakeCard) {
        long timeout = player.getTimeout(this.timeout);
        if (this.isLastCard) {
            timeout = BaseMahjongRoom.LAST_CARD_TAKE_TIMEOUT;
        } else if (player.isAutoTake()) {
            timeout = BaseMahjongRoom.AUTO_TAKE_TIMEOUT;
        }
        MahjongTakeActionByHS action = new MahjongTakeActionByHS(this, player, timeout);
        action.setParam(defaultTakeCard);
        return action;
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        this.calcHuScore(takePlayer, player1);

        List<EPaiXing> allPaiXing = player1.getAllPaiXing();
        if (allPaiXing.isEmpty()) {
            player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), EPaiXing.HSMJ_NORMAL, huCard);
        } else {
            for (EPaiXing px : allPaiXing) {
                player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), px, huCard);
            }
        }

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest() || player1.getUid() == player.getUid()) {
                continue;
            }
            int score = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            //int score = this.getScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            Logs.ROOM.debug("=== onHu player:：%d，MJ_CUR_HU_SCORE：%d", player.getUid(), score);
            player.addScore(Score.SCORE, -score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, -score, true);
            player1.addScore(Score.SCORE, score, false);
            player1.addScore(Score.ACC_TOTAL_SCORE, score, true);
        }

        // 实际竞技值计算，并抽水
        this.getRoomHandle().calculateGold();

        HSMJResultRecordAction resultRecordAction = (HSMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            HSMJResultRecordAction.PlayerInfo playerInfo = new HSMJResultRecordAction.PlayerInfo();

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
            HSMJResultRecordAction.ScoreInfo scoreInfo = new HSMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(0);
            scoreInfo.setGangScore(0);
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        this.bankerIndex = player1.getIndex();

        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        HSMJResultRecordAction resultRecordAction = (HSMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            int gangScore = this.getScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, gangScore, false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

//            HSMJResultRecordAction.PlayerInfo playerInfo = new HSMJResultRecordAction.PlayerInfo();
//            HSMJResultRecordAction.ScoreInfo scoreInfo = new HSMJResultRecordAction.ScoreInfo();
//            scoreInfo.setFangScore(0);
//            scoreInfo.setGangScore(0);
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
            HSMJResultRecordAction.PlayerInfo playerInfo = new HSMJResultRecordAction.PlayerInfo();
            HSMJResultRecordAction.ScoreInfo scoreInfo = new HSMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(0);
            scoreInfo.setGangScore(0);
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new HSMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        if (!ziMo && this.isPiOrLaiZi(huCard)) {
            return false;
        }

        // 漏胡不能胡
        if (!ziMo && player.isPass(EActionOp.HU)) {
            Logs.ROOM.debug("== isHu 漏胡不能胡");
            return false;
        }

        // 有皮不能胡
        if (this.hasPiCard(player)) {
            return false;
        }

        HSMJMahjongPlayer mjPlayer = (HSMJMahjongPlayer) player;
        mjPlayer.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        mjPlayer.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        mjPlayer.clearPaiXing();

        int eatCnt = 0; // 吃牌计数
        int yangCnt = 0; // 仰牌计数
        int normalCPGCnt = 0; // 常规杠牌的次数（非皮非赖）
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        for (CPGNode node : player.getCPGNode()) {
            if (node.getType() == CPGNode.EType.ANY_THREE) {
                yangCnt++;
                continue;
            }

            tempCards.get()[node.getCard1()] += 1;
            if (node.isEat()) {
                eatCnt++;
            }
            if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                normalCPGCnt++;
            }
        }

        long hu = 0;
        int points = 0;
        do {
            int count = 0;
            if (yangCnt == 0) {
                // 七对
                if (MahjongUtil.isSevenPair(player.getHandCardRaw(), this.laiZiCard)) {
                    hu |= MahjongHu.QI_DUI;
                    player.addPaiXing(EPaiXing.HSMJ_QI_DUI);
                    // 豪华
                    ((HSMJMahjongPlayer) player).setHao(this.countOfHaoHua(player));
                    int hao =((HSMJMahjongPlayer) player).getHao();
                    if (hao >= 2) {
                       player.addPaiXing(EPaiXing.HSMJ_HAO_HUA_2); 
                        if (!player.hasPaiXing(EPaiXing.HSMJ_YING) && MahjongUtil.isBigBigSevenPair(player.getHandCardRaw()) && count==0) {
                            player.addPaiXing(EPaiXing.HSMJ_YING);
                        }else {
                            ++count;
                        }
                    }else if (hao >=1) {
                        player.addPaiXing(EPaiXing.HSMJ_HAO_HUA);
                        if (!player.hasPaiXing(EPaiXing.HSMJ_YING) && MahjongUtil.isBigSevenPair(player.getHandCardRaw()) && count==0) {
                            player.addPaiXing(EPaiXing.HSMJ_YING);
                        }else {
                            ++count;
                        }
                    }else{
                        if (!player.hasPaiXing(EPaiXing.HSMJ_YING) && MahjongUtil.isSevenPair(player.getHandCardRaw()) && count==0) {
                            player.addPaiXing(EPaiXing.HSMJ_YING);
                        }else {
                            ++count;
                        }
                    }
                }
                // 将一色，不需要成“话”
                if (hu == 0 && MahjongUtil.isEyeYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                    hu |= MahjongHu.JIANG_YI_SE;
                    player.addPaiXing(EPaiXing.HSMJ_JIANG_YI_SE);
                    if (!player.hasPaiXing(EPaiXing.HSMJ_YING) && MahjongUtil.isEyeYiSe(tempCards.get()) && count==0) {
                        player.addPaiXing(EPaiXing.HSMJ_YING);
                    }else {
                        ++count;
                    }
                }
            }
            if (hu == 0 && !MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
                break;
            }
            if (yangCnt == 0) {
                //  清一色
                if (MahjongUtil.isQingYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                    hu |= MahjongHu.QING_YI_SE;
                    player.addPaiXing(EPaiXing.HSMJ_QING_YI_SE);
                    if (!player.hasPaiXing(EPaiXing.HSMJ_YING) && MahjongUtil.isQingYiSe(tempCards.get(), (byte) -1, this.piList) && count==0) {
                        player.addPaiXing(EPaiXing.HSMJ_YING);
                    }else {
                        ++count;
                    }
                }
                //  碰碰胡
                if (eatCnt == 0 && MahjongUtil.isPengPengHu(player.getHandCardRaw(), this.laiZiCard)) {
                    hu |= MahjongHu.PENG_PENG_HU;
                    player.addPaiXing(EPaiXing.HSMJ_PENG_PENG_HU);
                    if (!player.hasPaiXing(EPaiXing.HSMJ_YING) && MahjongUtil.isPengPengHu(player.getHandCardRaw()) && count==0) {
                        player.addPaiXing(EPaiXing.HSMJ_YING);
                    }else {
                        ++count;
                    }
                }
            }
            
            
            if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt, true, this.laiZiCard, Integer.MAX_VALUE)) {
                // 门前
                if (ziMo && player.getCPGNodeCntWithOutType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI,CPGNode.EType.ANY_THREE) == 0) {
                    hu |= MahjongHu.MEN_QIAN_QING;
                    if(!this.isBig(hu)) {
                        player.addPaiXing(EPaiXing.HSMJ_MEN_QING);
                    }
                } else {
                    //见字胡不能点炮
                    if (!ziMo && EActionOp.BAR != this.curAction && player.getHandCardCnt(this.laiZiCard) >= 1) {
                      player.delHandCard(huCard);
                      player.delHandCard(this.laiZiCard);
                      player.addHandCard(this.piList.get(0));
                      player.addHandCard(this.piList.get(0));
                      boolean huNoEye = MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt, false, this.laiZiCard, Integer.MAX_VALUE);
                      player.delHandCard(this.piList.get(0));
                      player.delHandCard(this.piList.get(0));
                      player.addHandCard(huCard);
                      player.addHandCard(this.laiZiCard);
                      if (huNoEye && !this.isBig(hu)) {
                          hu = 0;
                          break;
                      }
                  }
                    
                    if(!this.isBig(hu)) {
                        if (!ziMo && player.getHandCardCnt(this.laiZiCard) >= 2) {
                            break;
                        }
                        hu |= MahjongHu.PI_HU;
                        player.addPaiXing(EPaiXing.HSMJ_NORMAL);
                        if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt)) {
                            player.addPaiXing(EPaiXing.HSMJ_YING);
                        }
                    }
   
                }
                
            }
            
            
            // 门前清或屁胡
//            if (hu == 0) {
//                if (!ziMo && player.getHandCardCnt(this.laiZiCard) >= 2) {
//                    break;
//                }
//
//                if (ziMo && player.getCPGNodeCntWithOutType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI) == 0) {
//                    hu |= MahjongHu.MEN_QIAN_QING;
//                    player.addPaiXing(EPaiXing.HSMJ_MEN_QING);
//                } else {
//                    // 见字胡不能点炮
//                    if (!ziMo && EActionOp.BAR != this.curAction && player.getHandCardCnt(this.laiZiCard) >= 1) {
//                        player.delHandCard(huCard);
//                        player.delHandCard(this.laiZiCard);
//                        player.addHandCard(this.piList.get(0));
//                        player.addHandCard(this.piList.get(0));
//                        boolean huNoEye = MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt, false, this.laiZiCard, Integer.MAX_VALUE);
//                        player.delHandCard(this.piList.get(0));
//                        player.delHandCard(this.piList.get(0));
//                        player.addHandCard(huCard);
//                        player.addHandCard(this.laiZiCard);
//                        if (huNoEye) {
//                            break;
//                        }
//                    }
//
//                    hu |= MahjongHu.PI_HU;
//                    player.addPaiXing(EPaiXing.HSMJ_NORMAL);
//                }
//                if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt)) {
//                    player.addPaiXing(EPaiXing.HSMJ_YING);
//                }
//            }

            // 自摸
            if (ziMo) {
                hu |= MahjongHu.ZI_MO;
            }

            points = this.calcPoints(hu);
            if ((this.rulePlay & RULE_WITH_POINT_LIMIT) != 0) {
                int fan = this.getHuFang(player);
                if (player.hasPaiXing(EPaiXing.HSMJ_YING)) {
                    fan += 1;
                }
                if (player.hasPaiXing(EPaiXing.HSMJ_HAO_HUA)) {
                    fan += ((HSMJMahjongPlayer) player).getHao();
                }
                int totalPoints = 0;
                for (IRoomPlayer p : this.allPlayer) {
                    if (p == null || p.isGuest() || p.getUid() == player.getUid()) {
                        continue;
                    }

                    int otherFan = this.getHuFang((IMahjongPlayer) p);
                    if (!ziMo && p.getUid() == takePlayerUid) {
                        otherFan += 1;
                    }
                    totalPoints += points * Math.pow(2, fan + otherFan);
                }
                if (totalPoints < HU_LOWEST_POINTS) {
                    Logs.ROOM.debug("=== isHu 赢分太低:%d，不能胡", totalPoints);
                    hu = 0;
                }
            }
        } while (false);

        if (hu != 0) {
            Logs.ROOM.debug("=== isHu Score.MJ_CUR_FANG_SCORE: %d, huCard: %d", points, huCard);
            player.setScore(Score.MJ_CUR_FANG_SCORE, points, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, this.isBig(hu) ? 1 : 2, false);
        } else {
            player.clearPaiXing();
            player.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        }

        return hu != 0;
    }

    @Override
    public void clear() {
        super.clear();
        this.laiZiCard = -1;
        this.piList.clear();
        this.fangCard = -1;
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
    public void onTake(IMahjongPlayer takePlayer, boolean auto, Object... param) {
        super.onTake(takePlayer, auto, param);

        byte card = (byte) param[0];
        if (this.isLaiZi(card)) {
            takePlayer.addScore(Score.MJ_CUR_TAKE_LAIZI_CNT, 1, false);
        } else if (this.isPi(card)) {
            takePlayer.addScore(Score.MJ_CUR_TAKE_PI_CNT, 1, false);
        }
        ((HSMJMahjongPlayer) takePlayer).addTakeCardCnt();
        boolean isBanker= takePlayer.getIndex() == this.bankerIndex;
        if(!((HSMJMahjongPlayer) takePlayer).getCanOperate()){
            if((isBanker&&((HSMJMahjongPlayer) takePlayer).getTakeCardCnt()==2)||(!isBanker&&((HSMJMahjongPlayer) takePlayer).getTakeCardCnt()==1)){
                ((HSMJMahjongPlayer) takePlayer).setCanOperate(true);
            }
        }
    }

    @Override
    public boolean isMoreHu() {
        return false;
    }

    @Override
    public boolean isCanTakeCard(IMahjongPlayer player, byte card) {
        Logs.ROOM.debug("isCanTakeCard:%d", card);
        return !this.isPiOrLaiZi(card) && super.isCanTakeCard(player, card);
    }

    @Override
    public boolean isPi(byte card) {
        return this.piList.contains(card);
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (this.isPiOrLaiZi(takeCard)) {
            return !fangGang && player.hasHandCard(takeCard, 1);
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if ((this.features & MahjongFeatures.ENABLE_PENG) == 0) {
            return false;
        }
        return !this.isPiOrLaiZi(takeCard) && super.isBump(player, takeCard);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return !this.isPiOrLaiZi(takeCard) && super.isEat(player, takeCard);
    }

    @Override
    protected boolean isFrontendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPi((byte) (takeCard - 1)) || this.isPi((byte) (takeCard - 2))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isFrontendEat(player, takeCard);
    }

    @Override
    protected boolean isMiddleEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPi((byte) (takeCard - 1)) || this.isPi((byte) (takeCard + 1))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isMiddleEat(player, takeCard);
    }

    @Override
    protected boolean isBackendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPi((byte) (takeCard + 1)) || this.isPi((byte) (takeCard + 2))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isBackendEat(player, takeCard);
    }

    @Override
    public void onEat(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        super.onEat(takePlayer, player, param);

        boolean laiZiEat = false;
        switch ((byte) param[1]) {
            case 1:
                laiZiEat = (this.curCard - 2) == this.laiZiCard || (this.curCard - 1) == this.laiZiCard;
                break;
            case 2:
                laiZiEat = (this.curCard + 1) == this.laiZiCard || (this.curCard - 1) == this.laiZiCard;
                break;
            case 3:
                laiZiEat = (this.curCard + 1) == this.laiZiCard || (this.curCard + 2) == this.laiZiCard;
                break;
        }

        if (laiZiEat) {
            player.addScore(Score.MJ_CUR_TAKE_LAIZI_CNT, 1, false);
        }
    }

    @Override
    public boolean canPiBar() {
        return true;
    }

    @Override
    public boolean canLaiZiBar() {
        return true;
    }

    @Override
    public Record getRecord() {
        if (this.record == null) {
            this.record = new HSMJMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return false;
    }

    @Override
    protected MahjongTakeAction getTakeAction(IMahjongPlayer player, byte defaultTakeCard) {
        long timeout = player.getTimeout(this.timeout);
        if (this.isLastCard) {
            timeout = BaseMahjongRoom.LAST_CARD_TAKE_TIMEOUT;
        } else if (player.isAutoTake()) {
            timeout = BaseMahjongRoom.AUTO_TAKE_TIMEOUT;
        }
        MahjongTakeOrBarAction action = new MahjongTakeOrBarAction(this, player, timeout);
        action.setParam(defaultTakeCard);
        return action;
    }

    private int getHuFang(IMahjongPlayer player) {
        Logs.ROOM.debug("\t\t=== getHuFang: %d===", player.getUid());

        // 打皮算1番，打赖算2番
        int pi = player.getScore(Score.MJ_CUR_TAKE_PI_CNT, false);
        int lai = player.getScore(Score.MJ_CUR_TAKE_LAIZI_CNT, false);
        int points = pi + (lai * 2);

        int countOfKaiKou = 0;
        for (CPGNode node : player.getCPGNode()) {
            switch (node.getType()) {
                // 皮子杠、明杠和放杠算1番
                case BAR_MING:
                case BAR_FANG:
                case BAR_PI:
                    Logs.ROOM.debug("\t\t\t+1, %s", node.getType());
                    points += 1;
                    if (node.getType() == CPGNode.EType.BAR_MING || node.getType() == CPGNode.EType.BAR_FANG) {
                        countOfKaiKou++;
                    }
                    break;

                // 癞子杠和暗杠算2番
                case BAR_LAIZI:
                case BAR_AN:
                    Logs.ROOM.debug("\t\t\t+2, %s", node.getType());
                    points += 2;
                    break;

                // 仰牌算2番，含赖子算4番
                case ANY_THREE:
                    Logs.ROOM.debug("\t\t\t+%d, YANG_PAI", MahjongHelper.isJianPai(this.laiZiCard) ? 4 : 2);
                    points += MahjongHelper.isJianPai(this.laiZiCard) ? 4 : 2;
                    break;

                default:
                    countOfKaiKou++;
                    break;
            }
        }

        if (countOfKaiKou >= 3) {
            points += 1;
            Logs.ROOM.debug("\t\t\t+1, 三铺");
        }

        Logs.ROOM.debug("\t\t\tresult: %d, pi:%d, lai:%d", points, pi, lai);
        return points;
    }

    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1) {
        Logs.ROOM.debug("=== calcHuScore  taker: %d, player1: %d ===", takePlayer.getUid(), player1.getUid());
        // 基础分
        int points = player1.getScore(Score.MJ_CUR_FANG_SCORE, false);
        boolean menQing = player1.hasPaiXing(EPaiXing.HSMJ_MEN_QING);
        int fan = this.getHuFang(player1);
        int count = 0;
        for (EPaiXing px : player1.getAllPaiXing()) {
            
            switch (px) {
                case HSMJ_PENG_PENG_HU:
                case HSMJ_QING_YI_SE:
                case HSMJ_JIANG_YI_SE:
                case HSMJ_QI_DUI:
                    fan += 1;
                    count++;
                    break;
            }
        }
        if (count > 0) fan -= 1;
        if (player1.hasPaiXing(EPaiXing.HSMJ_YING)) {
            Logs.ROOM.debug("\tYING: +1");
            fan += 1;
        }
        if (player1.hasPaiXing(EPaiXing.HSMJ_HAO_HUA)) {
            fan += 1;
        }
        if (player1.hasPaiXing(EPaiXing.HSMJ_HAO_HUA_2)) {
            fan += 2;
        }
        
        // 计算输家分数
        int countOfTop = 0; // 封顶个数
        for (IRoomPlayer p : this.allPlayer) {
            if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                continue;
            }

            HSMJMahjongPlayer otherPlayer = (HSMJMahjongPlayer) p;
            Logs.ROOM.debug("\t玩家：%d", otherPlayer.getUid());
            int otherPoints = points;
//            if (otherPlayer.getUid() == takePlayer.getUid() && !menQing) {
//                if (player1.hasPaiXing(EPaiXing.HSMJ_NORMAL)) {
//                    Logs.ROOM.debug("\t\totherPoints:%d + 1", otherPoints);
//                    otherPoints += 1;
//                } else {
//                    Logs.ROOM.debug("\t\totherPoints:%d + 5", otherPoints);
//                    otherPoints += 5;
//                }
//            }
            int otherFan = this.getHuFang(otherPlayer);
            if (otherPlayer.getUid() == takePlayer.getUid()) {
                otherFan += 1;
            }
            otherPoints *= (int) Math.pow(2, fan + otherFan);

            if (otherPoints >= TOP_POINTS) {
                if ((this.ruleTop & RULE_TOP_1) != 0) {
                    otherPoints = TOP_POINTS;
                    countOfTop++;
                }
            }

            Logs.ROOM.debug("\t\totherPoints:%d, fan:%d, otherFan:%d", otherPoints, fan, this.getHuFang(otherPlayer));

            otherPoints = this.getScore(otherPoints);
            otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, otherPoints, false);
            Logs.ROOM.debug("\t\t最终分数：%d", otherPoints);
        }

        // 是否算金顶
        if ((this.ruleTop & RULE_TOP_1) != 0 && countOfTop == this.getCurPlayerCnt() - 1) {
            for (IRoomPlayer p : this.allPlayer) {
                if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                    continue;
                }
                int otherPoints = GOLD_TOP_POINTS * this.getScore(1);
                p.setScore(Score.MJ_CUR_HU_SCORE, otherPoints, false);
                ((IMahjongPlayer) p).addShowFlag(EShowFlag.HSMJ_JJIN_DING);

                Logs.ROOM.debug("\t玩家：%d，金顶：%d", p.getUid(), otherPoints);
            }
        }
    }

    // 豪华的个数，仅限七对牌型
    private int countOfHaoHua(IMahjongPlayer player) {
        int count = 0;
        int laiZiCnt = 0;
        for (byte i = MahjongUtil.MJ_1_WANG; i <= MahjongUtil.MJ_BAI_FENG; i++) {
            int cardCnt = player.getHandCardCnt(i);
            if (this.isLaiZi(i)) {
                laiZiCnt += cardCnt;
            } else if (cardCnt == 4) {
                count++;
            } else if (cardCnt == 3) {
                count++;
                laiZiCnt--; // 用掉一个赖子
            } else if (cardCnt == 1) {
                laiZiCnt--; // 用掉一个赖子
            }
        }

        assert laiZiCnt >= 0: "laiZiCnt value error";

        count += laiZiCnt / 2;
        return Math.min(count, 3);
    }
    
    private boolean hasPiCard(IMahjongPlayer player) {
        for (Byte pi : this.piList) {
            if (player.hasHandCard(pi, 1)) {
                return true;
            }
        }
        return false;
    }

    // 是否大胡
    private boolean isBig(long hu) {
        for (long big : BIG_HU) {
            if ((hu & big) != 0) {
                return true;
            }
        }
        return false;
    }

    // 胡的点数
    private int calcPoints(long hu) {
        int points = 0;

        if (this.isBig(hu)) {
            points += (hu & MahjongHu.ZI_MO) != 0 ? 10 : 5;
        } else if ((hu & MahjongHu.MEN_QIAN_QING) != 0) {
            points += 5;
        } else if ((hu & MahjongHu.PI_HU) != 0) {
            points += (hu & MahjongHu.ZI_MO) != 0 ? 3 : 1;
        }

        Logs.ROOM.debug("=== points: %d, calcPoints(0x%x)", points, hu);
        return points;
    }

    /**
     * 生成听信息
     * @param player
     */
    @Override
    protected void generateTingInfo(IMahjongPlayer player) {
        TingInfo tingInfo = player.getTingInfo();
        tingInfo.clear();

        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (!player.hasHandCard((byte) i, 1)) {
                continue;
            }
            byte takeCard = (byte) i;
            player.delHandCard(takeCard);
            ++this.deskCard[takeCard];

            for (int j = 1; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
                //麻将不含梅兰竹菊
                if (j >= 35) {
                    continue;
                }
                //麻将不含东南西北
                if (j >= 28 && j <= 31) {
                    continue;
                }
                //两人不含万
                if (this.curPlayerCnt == 2 && j <=9) {
                    continue;
                }
                byte huCard = (byte) j;
                int fang = this.getFang(player, huCard);
                if (fang > 0) {
                    tingInfo.add(takeCard, huCard, fang, this.getRemainCardCntByPlayer(player, huCard));
                }
            }

            player.addHandCard(takeCard);
            --this.deskCard[takeCard];
        }
        tingInfo.setBuild(true);
    }
    
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * value;
    }

}
