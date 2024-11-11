package com.xiuxiu.app.server.room.normal.mahjong2.mcmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByMCMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByMCMJ;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithMCMJ;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongFeatures;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHelper;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHu;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.MCMJMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.MCMJMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.MCMJResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.ResultRecordAction;

import java.util.*;

/**
 * 麻城麻将
 */
//@GameInfo(gameType = GameType.GAME_TYPE_MCMJ, gameSubType = 1)
public class MCMJMahjongRoom extends MahjongRoom {
    private long features;

    private byte fangCard = -1; // 翻出的牌
    private ArrayList<Byte> piList = new ArrayList<>(); // 皮列表

    private LinkedList<Integer> cunList = new LinkedList<>(); // "存"的翻倍，数值含义参考"配置：玩法"
    private List<Integer> fanList = new ArrayList<>(); // 当局使用的翻倍

    private static final int HU_LOWEST_POINTS = 3; // 胡牌所要求的最低番数

    private static final int TOP_TYPE_NORMAL = 0; // 常规输赢
    private static final int TOP_TYPE_SILVER = 1; // 银顶
    private static final int TOP_TYPE_GOLD = 2; // 金顶

    private static final int NORMAL_POINTS = 1; // 常规输赢起始倍数
    private static final int[] SILVER_POINTS = {8, 4}; // 银顶分数，2人、3/4人
    private static final int[] GOLD_POINTS = {10, 5}; // 金顶分数，2人、3/4人

    // 大胡列表
    private static final List<Long> BIG_HU = Arrays.asList(
            MahjongHu.PENG_PENG_HU,
            MahjongHu.QING_YI_SE,
            MahjongHu.FENG_YI_SE,
            MahjongHu.JIANG_YI_SE,
            MahjongHu.QI_DUI
    );

    // 配置：玩法
    private int playingRules;
    private static final int RULE_WITH_TIMER    = 0x0001; // 倒计时
    private static final int RULE_BI            = 0x0002; // 闭
    private static final int RULE_FENG_FAN      = 0x0004; // 风翻
    private static final int RULE_MAN_PAO       = 0x0008; // 满跑
    private static final int RULE_JIANG_FAN     = 0x0010; // 将翻
    private static final int RULE_CUN           = 0x0020; // 存
    private static final int RULE_LIAN_BAO_FAN  = 0x0040; // 连宝翻
    private static final int RULE_BAO_ZI_FAN    = 0x0080; // 豹子翻

    public MCMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public MCMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.playingRules = this.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        if ((this.playingRules & RULE_WITH_TIMER) != 0) {
            this.timeout = 20 * 1000;
        }
    }

    @Override
    protected void doShuffle() {

        // 默认包含序数牌和字牌
        this.features = MahjongFeatures.WITH_TONG_ZI | MahjongFeatures.WITH_TIAO_ZI | MahjongFeatures.WITH_WAN_ZI
                | MahjongFeatures.WITH_FENG | MahjongFeatures.WITH_JIAN;
        if (this.getCurPlayerCnt() <= 3) { // 2人和3人时去掉万子
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
    protected void doDealAfter() {
        // 发完牌后，牌墙上第一张翻出，作为皮子牌
        this.fangCard = this.allCard.removeFirst();

        // 确定赖子和皮
        this.laiZiCard = MahjongHelper.getLaiZiCardByFengJian(this.fangCard);
        if (this.laiZiCard == MahjongUtil.MJ_F_FENG) { // 翻红中，白板做皮
            this.piList.add(MahjongUtil.MJ_BAI_FENG);
        } else {
            if (this.laiZiCard == MahjongUtil.MJ_Z_FENG) { // 如果赖子定红中，则换成发财
                this.laiZiCard = MahjongUtil.MJ_F_FENG;
            }
            this.piList.add(this.fangCard);
        }
        this.piList.add(MahjongUtil.MJ_Z_FENG);
    }

    @Override
    protected void doStart1() {
        this.doStartTake();
    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithMCMJ info = new PCLIRoomNtfBeginInfoByMJWithMCMJ();
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
            info.cunList.addAll(this.cunList);
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithMCMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithMCMJ();
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
        roomBeginInfo.cunList.addAll(this.cunList);
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByMCMJ info = new PCLIMahjongNtfGameOverInfoByMCMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;
        info.cunList.addAll(this.cunList);
        info.fanList.addAll(this.fanList);

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByMCMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByMCMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByMCMJ.HuInfo temp = new PCLIMahjongNtfGameOverInfoByMCMJ.HuInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByMCMJ.ScoreInfo();
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE, false));
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByMCMJ.FinalResult();
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
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }

        PCLIMahjongNtfDeskInfoByMCMJ deskInfo= new PCLIMahjongNtfDeskInfoByMCMJ();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 : mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        IRoomPlayer banker = this.bankerIndex >= 0 ? this.getRoomPlayer(this.bankerIndex) : null;
        deskInfo.bankerPlayerUid = banker == null || banker.isGuest() ? -1L : banker.getUid();
        deskInfo.fangPai = this.fangCard;
        deskInfo.laiZi = this.laiZiCard;
        deskInfo.piList = this.piList;
        deskInfo.cunList.addAll(this.cunList);
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();

        try {
            this.rwLock.readLock().lock();
            int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer other = (IMahjongPlayer) this.allPlayer[i];
                if (null == other || other.isGuest()) {
                    continue;
                }
                deskInfo.allOnlineState.put(other.getUid(), !other.isOffline());
                PCLIMahjongNtfDeskInfoByMCMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByMCMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByMCMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByMCMJ.CardNode();
                    cardNode.type = node.getType().ordinal();
                    cardNode.playerId=this.allPlayer[node.getTakePlayerIndex()].getUid();
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
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        this.calcHuScore(takePlayer, player1);

        List<EPaiXing> allPaiXing = player1.getAllPaiXing();
        if (allPaiXing.isEmpty()) {
            player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), EPaiXing.MCMJ_NORMAL, huCard);
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
            Logs.ROOM.debug("=== onHu player:：%d，MJ_CUR_HU_SCORE：%d", player.getUid(), score);
            player.addScore(Score.SCORE, -score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, -score, true);
            player1.addScore(Score.SCORE, score, false);
            player1.addScore(Score.ACC_TOTAL_SCORE, score, true);
        }

        MCMJResultRecordAction resultRecordAction = (MCMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            MCMJResultRecordAction.PlayerInfo playerInfo = new MCMJResultRecordAction.PlayerInfo();

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
            MCMJResultRecordAction.ScoreInfo scoreInfo = new MCMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        this.bankerIndex = player1.getIndex();

        ((MCMJMahjongRecord) this.getRecord()).setCunList(this.cunList);
        ((MCMJMahjongRecord) this.getRecord()).setFanList(this.fanList);
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        MCMJResultRecordAction resultRecordAction = (MCMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            int gangScore = this.getScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, gangScore, false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            MCMJResultRecordAction.PlayerInfo playerInfo = new MCMJResultRecordAction.PlayerInfo();
            MCMJResultRecordAction.ScoreInfo scoreInfo = new MCMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
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
        return new MCMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        // 碰->杠->胡
        if (!ziMo && this.curAction == EActionOp.BAR && this.prevAction == EActionOp.BUMP && this.curCard == this.prevCard) {
            return false;
        }

        MCMJMahjongPlayer mjPlayer = (MCMJMahjongPlayer) player;
        mjPlayer.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        mjPlayer.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        mjPlayer.clearPaiXing();

        // 漏胡不能胡
        if (!ziMo && player.isPassCard(EActionOp.HU, huCard)) {
            Logs.ROOM.debug("isHu 漏胡不能胡");
            return false;
        }

        // 有皮不能胡
        if (this.hasPiCard(mjPlayer)) {
            return false;
        }

        // 未勾选“满跑”时，没有开口不能胡
        if ((this.playingRules & RULE_MAN_PAO) == 0 && !this.hasPlayerKaiKou(mjPlayer)) {
            return false;
        }

        int eatCnt = 0; // 吃牌的次数
        int normalCPGCnt = 0; // 常规吃碰杠牌的次数（非皮非赖）
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        for (CPGNode node : player.getCPGNode()) {
            if (node.getType() == CPGNode.EType.ANY_THREE) {
                continue;
            }

            tempCards.get()[node.getCard1()] += 1;
            if (node.isEat()) {
                ++eatCnt;
            }
            if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                ++normalCPGCnt;
            }
        }

        long hu = 0;
        int points = 0;
        do {
            // 将一色，不需要成“话”
            if (MahjongUtil.isEyeYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                hu |= MahjongHu.JIANG_YI_SE;
                player.addPaiXing(EPaiXing.MCMJ_JIANG_YI_SE);
                if (!player.hasPaiXing(EPaiXing.MCMJ_YING) && MahjongUtil.isEyeYiSe(tempCards.get(), (byte) -1, this.piList)) {
                    player.addPaiXing(EPaiXing.MCMJ_YING);
                }
            }
            // 风一色，不需要成“话”
            if (MahjongUtil.isFengYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                hu |= MahjongHu.FENG_YI_SE;
                player.addPaiXing(EPaiXing.MCMJ_FENG_YI_SE);
                if (!player.hasPaiXing(EPaiXing.MCMJ_YING) && MahjongUtil.isFengYiSe(tempCards.get(), (byte) -1, this.piList)) {
                    player.addPaiXing(EPaiXing.MCMJ_YING);
                }
            }
            // 七对
            if (this.enabledQiDui() && MahjongUtil.isSevenPair(player.getHandCardRaw(), this.laiZiCard)) {
                hu |= MahjongHu.QI_DUI;
                player.addPaiXing(EPaiXing.MCMJ_QI_DUI);
                if (!player.hasPaiXing(EPaiXing.MCMJ_YING) && MahjongUtil.isSevenPair(player.getHandCardRaw())) {
                    player.addPaiXing(EPaiXing.MCMJ_YING);
                }
                // 豪华
                ((MCMJMahjongPlayer) player).setHao(this.countOfHaoHua(player));
                if (((MCMJMahjongPlayer) player).getHao() > 0) {
                    player.addPaiXing(EPaiXing.MCMJ_HAO_HUA);
                    points += 1;
                }
            }
            // 是否满足常规胡牌要求（成话成句）
            if (hu == 0
                    && !MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
                Logs.ROOM.debug("isHu 乱将没有胡 %d", huCard);
                break;
            }
            //  清一色
            if (MahjongUtil.isQingYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                hu |= MahjongHu.QING_YI_SE;
                player.addPaiXing(EPaiXing.MCMJ_QING_YI_SE);
                if (!player.hasPaiXing(EPaiXing.MCMJ_YING) && MahjongUtil.isQingYiSe(tempCards.get(), (byte) -1, this.piList)) {
                    player.addPaiXing(EPaiXing.MCMJ_YING);
                }
            }
            //  碰碰胡
            if (eatCnt == 0 && MahjongUtil.isPengPengHu(player.getHandCardRaw(), this.laiZiCard)) {
                hu |= MahjongHu.PENG_PENG_HU;
                player.addPaiXing(EPaiXing.MCMJ_PENG_PENG_HU);
                if (!player.hasPaiXing(EPaiXing.MCMJ_YING) && MahjongUtil.isPengPengHu(player.getHandCardRaw())) {
                    player.addPaiXing(EPaiXing.MCMJ_YING);
                }
            }

            // 是否满足常规胡牌要求，258将
            if (hu == 0
                    && !MahjongUtil.isHu258Eye(player.getHandCardRaw(), normalCPGCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
                Logs.ROOM.debug("isHu 258将没有胡 %d", huCard);
                break;
            }
            if (ziMo) {
                // 门前清
                if (this.enabledMenQianQing()
                        && player.getCPGNodeCntWithOutType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI) == 0) {
                    if (hu == 0 && MahjongUtil.isHu258Eye(player.getHandCardRaw(), normalCPGCnt, false)) {
                        player.addPaiXing(EPaiXing.MCMJ_YING);
                    }
                    hu |= MahjongHu.MEN_QIAN_QING;
                    player.addPaiXing(EPaiXing.MCMJ_MEN_QING);
                }

                if (this.isBig(hu)) { // 大胡自摸
                    hu |= MahjongHu.ZI_MO;
                }
            }

            // 上面的可以点炮胡，下面的（屁胡）只能自摸或者有开口
            if (hu == 0 && !ziMo && !this.hasPlayerKaiKou(mjPlayer)) {
                Logs.ROOM.debug("isHu 未开口，不能胡屁胡点炮");
                break;
            }

            // 屁胡
            if (hu == 0) {
                // 检测赖子数量是否满足胡牌要求，杠上开无此限制
                int laiZiLimit = this.numberLimitOfLaiZi(hu);
                if (player.getHandCardCnt(this.laiZiCard) > laiZiLimit && (!ziMo || this.prevAction != EActionOp.BAR)) {
                    Logs.ROOM.debug("isHu 赖子太多不能胡");
                    break;
                }

                Logs.ROOM.debug("isHu 屁胡");
                hu |= MahjongHu.PI_HU;
                player.addPaiXing(EPaiXing.MCMJ_NORMAL);
                if (MahjongUtil.isHu258Eye(player.getHandCardRaw(), normalCPGCnt, false)) {
                    player.addPaiXing(EPaiXing.MCMJ_YING);
                }
            }

            // 杠上开花
            if (ziMo && this.prevAction == EActionOp.BAR) {
                hu |= MahjongHu.GANG_SHANG_KAI_HUA;
                player.addPaiXing(EPaiXing.MCMJ_GANG_KAI);
            }


            //如果有7对
            if (player.hasPaiXing(EPaiXing.MCMJ_QI_DUI)) {
                //不是硬胡7对,并且有硬胡,就去掉硬胡
                if (!MahjongUtil.isSevenPair(player.getHandCardRaw()) && player.hasPaiXing(EPaiXing.MCMJ_YING)) {
                    for (int i = 0; i < player.getAllPaiXing().size(); i++) {
                        if (player.getAllPaiXing().get(i) == EPaiXing.MCMJ_YING) {
                            player.getAllPaiXing().remove(i);
                            break;
                        }
                    }
                }
            }

            points += this.calcPoints(hu) + this.getHuFang(player);

            // 硬胡计番
            if (player.hasPaiXing(EPaiXing.MCMJ_YING)) {
                Logs.ROOM.debug("isHu points: 1 YING");
                ++points;
            }

            if (points < HU_LOWEST_POINTS) {
                List<IMahjongPlayer> others = new ArrayList<>();
                if (!this.isBig(hu) && !ziMo) {
                    others.add((IMahjongPlayer) this.getRoomPlayer(takePlayerUid));
                } else {
                    for (IRoomPlayer p : this.allPlayer) {
                        if (p != null && !p.isGuest() && p.getUid() != player.getUid()) {
                            others.add((IMahjongPlayer) p);
                        }
                    }
                }

                boolean canHu = false;
                for (IMahjongPlayer p : others) {
                    // 大胡或者自摸时所有人输分，屁胡点炮只有点炮方输分
                    if (this.isBig(hu) || ziMo || p.getUid() == takePlayerUid) {
                        int thisPoints = points + this.getHuFang(p);

                        // 大胡，点炮方算一番
                        if (this.isBig(hu) && !ziMo && p.getUid() == takePlayerUid) {
                            ++thisPoints;
                        }

                        // 任一人满足胡番即可
                        if (thisPoints >= HU_LOWEST_POINTS) {
                            canHu = true;
                            break;
                        }
                    }
                }

                if (!canHu) {
                    Logs.ROOM.debug("isHu 番数太低不能胡");
                    hu = 0;
                    break;
                }
            }
        } while(false);

        if (hu != 0) {
            Logs.ROOM.debug("=== isHu player.setScore(Score.MJ_CUR_FANG_SCORE, %d, false)", points);
            player.setScore(Score.MJ_CUR_FANG_SCORE, points, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, this.isBig(hu) ? 1 : 2, false);
        } else {
            player.clearPaiXing();
        }

        return hu != 0;
    }

    @Override
    public void clear() {
        super.clear();
        this.piList.clear();
        this.fanList.clear();
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
    public boolean isMoreHu() {
        return false;
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
        if (this.isPiOrLaiZi((byte) (takeCard - 1)) || this.isPiOrLaiZi((byte) (takeCard - 2))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isFrontendEat(player, takeCard);
    }

    @Override
    protected boolean isMiddleEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi((byte) (takeCard - 1)) || this.isPiOrLaiZi((byte) (takeCard + 1))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isMiddleEat(player, takeCard);
    }

    @Override
    protected boolean isBackendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi((byte) (takeCard + 1)) || this.isPiOrLaiZi((byte) (takeCard + 2))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isBackendEat(player, takeCard);
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
            this.record = new MCMJMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return false;
    }

    @Override
    protected boolean onCheckOver() {
        return this.allCard.size() <= 10;
    }

    @Override
    public List<Byte> getPiList() {
        return this.piList;
    }

    private boolean enabledQiDui() {
        return (this.playingRules & RULE_MAN_PAO) != 0;
    }

    private boolean enabledMenQianQing() {
        return (this.playingRules & RULE_MAN_PAO) != 0;
    }

    private int getHuFang(IMahjongPlayer player) {
        // 有开口算1番，打皮算1番，打赖算2番
        boolean kaiKou = this.hasPlayerKaiKou(player);
        int pi = player.getScore(Score.MJ_CUR_TAKE_PI_CNT, false);
        int lai = player.getScore(Score.MJ_CUR_TAKE_LAIZI_CNT, false);
        int points = (kaiKou ? 1 : 0) + pi + (lai * 2);

        // 杠番，皮子杠、明杠和放杠算1番，癞子杠和暗杠算2番
        for (CPGNode node : player.getCPGNode()) {
            switch (node.getType()) {
                case BAR_PI:
                case BAR_MING:
                case BAR_FANG:
                    Logs.ROOM.debug("=== getHuFang: +1, playerUid: %d, %s", player.getUid(), node.getType());
                    points += 1;
                    break;

                case BAR_LAIZI:
                case BAR_AN:
                    Logs.ROOM.debug("=== getHuFang: +2, playerUid: %d, %s", player.getUid(), node.getType());
                    points += 2;
                    break;
            }
        }
        Logs.ROOM.debug("=== getHuFang: %d, playerUid: %d, kaiKou:%s, pi:%d, lai:%d", points, player.getUid(), kaiKou, pi, lai);
        return points;
    }

    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1) {
        // 基础分
        int fang = player1.getScore(Score.MJ_CUR_FANG_SCORE, false);
        boolean bigHu = player1.getScore(Score.MJ_CUR_HU_TYPE, false) == 1;
        boolean ziMo = takePlayer.getUid() == player1.getUid();

        // 计算输家分数
        int countOfYinDing = 0;
        int countOfJinDing = 0;
        int topOfYinDing = this.getCurPlayerCnt() == 2 ? 6 : 5;
        List<MCMJMahjongPlayer> biPlayers = new ArrayList<>();
        for (IRoomPlayer p : this.allPlayer) {
            if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                continue;
            }
            // 大胡或者自摸时所有人输分，屁胡点炮只有点炮方输分
            if (!bigHu && !ziMo && p.getUid() != takePlayer.getUid()) {
                continue;
            }

            MCMJMahjongPlayer otherPlayer = (MCMJMahjongPlayer) p;
            int points = fang + this.getHuFang(otherPlayer);
            if (bigHu && otherPlayer.getUid() == takePlayer.getUid()) { // 大胡点炮玩家，加一番
                ++points;
            }

            // 低于最低番数，不输分
            if (points < HU_LOWEST_POINTS) {
                continue;
            }

            if (points > topOfYinDing) {
                otherPlayer.setTopType(TOP_TYPE_GOLD);
                otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, this.getCurPlayerCnt() == 2 ? GOLD_POINTS[0] : GOLD_POINTS[1], false);
                otherPlayer.addShowFlag(EShowFlag.MCMJ_JIN_DING);
                countOfJinDing++;
                Logs.ROOM.debug("=== 金顶：%d", this.getCurPlayerCnt() == 2 ? GOLD_POINTS[0] : GOLD_POINTS[1]);
            } else if (points == topOfYinDing) {
                otherPlayer.setTopType(TOP_TYPE_SILVER);
                otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, this.getCurPlayerCnt() == 2 ? SILVER_POINTS[0] : SILVER_POINTS[1], false);
                otherPlayer.addShowFlag(EShowFlag.MCMJ_YIN_DING);
                countOfYinDing++;
                Logs.ROOM.debug("=== 银顶：%d", this.getCurPlayerCnt() == 2 ? SILVER_POINTS[0] : SILVER_POINTS[1]);
            } else {
                otherPlayer.setTopType(TOP_TYPE_NORMAL);
                otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, NORMAL_POINTS << (points - HU_LOWEST_POINTS), false);
                Logs.ROOM.debug("=== 番数：%d，分数：%d", points, NORMAL_POINTS << (points - HU_LOWEST_POINTS));
            }
        }

        // 大于2人时，如果所有人都是银顶及以上，也算金顶
        if (countOfJinDing == this.playerNum - 1
                || (this.getCurPlayerCnt() > 2 && countOfYinDing + countOfJinDing == this.playerNum - 1)) {
            for (IRoomPlayer p : this.allPlayer) {
                if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                    continue;
                }
                MCMJMahjongPlayer otherPlayer = (MCMJMahjongPlayer) p;
                otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, this.getCurPlayerCnt() == 2 ? GOLD_POINTS[0] : GOLD_POINTS[1], false);
                otherPlayer.setTopType(TOP_TYPE_GOLD);
                otherPlayer.delShowFlag(EShowFlag.MCMJ_YIN_DING);
                otherPlayer.addShowFlag(EShowFlag.MCMJ_JIN_DING);

                // 未开口的玩家
                if ((this.playingRules & RULE_BI) != 0 && !this.hasPlayerKaiKou(otherPlayer)) {
                    biPlayers.add(otherPlayer);
                }
            }
        }

        // 闭，不受封顶限制
        for (MCMJMahjongPlayer otherPlayer : biPlayers) {
            Logs.ROOM.debug("=== 闭 player：%d", otherPlayer.getUid());
            int points = otherPlayer.getScore(Score.MJ_CUR_HU_SCORE, false) * (biPlayers.size() + 1);
            otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, points, false);
            otherPlayer.addShowFlag(EShowFlag.MCMJ_BI);
        }

        // 玩法：翻倍与存，不受封顶限制
        if ((this.playingRules & RULE_BAO_ZI_FAN) != 0 && this.crap1 == this.crap2) { // 豹子翻倍
            this.cunList.push(RULE_BAO_ZI_FAN);
        }
        if ((this.playingRules & RULE_FENG_FAN) != 0 && MahjongHelper.isZiPai(this.laiZiCard)) { // 风翻
            this.cunList.push(RULE_FENG_FAN);
        }
        if ((this.playingRules & RULE_JIANG_FAN) != 0 && MahjongUtil.is258(this.laiZiCard)) { // 将翻
            this.cunList.push(RULE_JIANG_FAN);
        }
        if ((this.playingRules & RULE_LIAN_BAO_FAN) != 0 && this.laiZiCard == this.prevLaiZiCard) { // 连宝翻
            this.cunList.push(RULE_LIAN_BAO_FAN);
        }

        int doubleTimes = 0;
        if (!this.cunList.isEmpty()) {
            doubleTimes = 1;
            int fan = this.cunList.removeLast();
            this.fanList.add(fan);
            if ((this.playingRules & RULE_CUN) == 0) {
                this.cunList.clear();
            }
        }
        Logs.ROOM.debug("=== doubleTimes：%d", doubleTimes);

        if (doubleTimes > 0) {
            int scale = (int) Math.pow(2, doubleTimes);
            for (IRoomPlayer p : this.allPlayer) {
                if (p != null && !p.isGuest() && p.getScore(Score.MJ_CUR_HU_SCORE, false) != 0) {
                    int points = p.getScore(Score.MJ_CUR_HU_SCORE, false);
                    p.setScore(Score.MJ_CUR_HU_SCORE, points * scale, false);
                    Logs.ROOM.debug("=== points：%d", points * scale);
                }
            }
        }

        for (IRoomPlayer p : this.allPlayer) {
            if (p != null && !p.isGuest() && p.getUid() != player1.getUid()) {
                int score = p.getScore(Score.MJ_CUR_HU_SCORE, false) * this.getScore(1);
                p.setScore(Score.MJ_CUR_HU_SCORE, score, false);
                Logs.ROOM.debug("=== calcHuScore player:：%d，MJ_CUR_HU_SCORE：%d", p.getUid(), score);
            }
        }
    }

    private boolean hasPiCard(IMahjongPlayer player) {
        for (Byte pi : this.piList) {
            if (player.hasHandCard(pi, 1)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPlayerKaiKou(IMahjongPlayer player) {
        return player.getCPGNodeCntWithOutType(CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI, CPGNode.EType.BAR_AN) > 0;
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

    // 胡牌时所持赖子限制的数量
    private int numberLimitOfLaiZi(long hu) {
        long noLimit = MahjongHu.PENG_PENG_HU
                | MahjongHu.QING_YI_SE
                | MahjongHu.FENG_YI_SE
                | MahjongHu.JIANG_YI_SE
                | MahjongHu.QI_DUI
                | MahjongHu.GANG_SHANG_KAI_HUA
                | MahjongHu.MEN_QIAN_QING;
        return (hu & noLimit) == 0 ? 1 : Integer.MAX_VALUE;
    }

    // 胡的点数
    private int calcPoints(long hu) {
        int points = 0;

        // 每个大胡算1点，最多累计2点
        for (int i = 0; points < 2 && i < BIG_HU.size(); i++) {
            if ((hu & BIG_HU.get(i)) != 0) {
                points++;
            }
        }
        Logs.ROOM.debug("=== points: BIG_HU %d", points);

        // 大胡自摸算1点
        if (isBig(hu) && (hu & MahjongHu.ZI_MO) != 0) {
            Logs.ROOM.debug("=== points: ZI_MO 1");
            points++;
        }

        // 杠上开算1点
        if ((hu & MahjongHu.GANG_SHANG_KAI_HUA) != 0) {
            Logs.ROOM.debug("=== points: GANG_SHANG_KAI_HUA 1");
            points++;
        }

        // 门前清算1点
        if ((hu & MahjongHu.MEN_QIAN_QING) != 0) {
            Logs.ROOM.debug("=== points: MEN_QIAN_QING 1");
            points++;
        }

        Logs.ROOM.debug("=== points: %d, calcPoints(%x)", points, hu);
        return points;
    }

    // 豪华的个数，仅限七对牌型
    private int countOfHaoHua(IMahjongPlayer player) {
        int count = 0;
        for (byte i = MahjongUtil.MJ_1_WANG; i <= MahjongUtil.MJ_BAI_FENG; i++) {
            //如果有4张一样的牌
            if (player.getHandCardCnt(i) == 4) {
                //如果是赖子
                if (this.laiZiCard == i) {
                    boolean m_bhasDan = false;//是否有单张牌存在
                    for (byte j = MahjongUtil.MJ_1_WANG; j <= MahjongUtil.MJ_BAI_FENG ; j++) {
                        if (player.getHandCardCnt(j) % 2 == 1) {
                            m_bhasDan = true;
                            break;
                        }
                    }
                    //如果没有单张，说明除这4张赖子以外的牌都是对子
                    if (!m_bhasDan) {
                        count++;
                    }
                } else {
                    count++;
                }
            }
        }
        return count;
    }
}
