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
import com.xiuxiu.app.server.room.record.mahjong2.MCHHMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.MCHHResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.ResultRecordAction;

import java.util.*;

/**
 * 麻城晃晃
 */
//@GameInfo(gameType = GameType.GAME_TYPE_MCMJ, gameSubType = 2)
public class MCHHMahjongRoom extends MahjongRoom {
    private long features;

    private byte fangCard = -1; // 翻出的牌

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
    private static final int RULE_WITH_TIMER = 0x0001; // 倒计时

    public MCHHMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public MCHHMahjongRoom(RoomInfo info, ERoomType roomType) {
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
        // 默认包含序数牌和箭牌
        this.features = MahjongFeatures.WITH_TONG_ZI | MahjongFeatures.WITH_TIAO_ZI | MahjongFeatures.WITH_WAN_ZI
                | MahjongFeatures.WITH_JIAN;
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

        // 确定赖子
        this.laiZiCard = MahjongHelper.getLaiZiCardByFengJian(this.fangCard);
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
            info.piList = Collections.emptyList();
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
        roomBeginInfo.piList = Collections.emptyList();
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByMCMJ info = new PCLIMahjongNtfGameOverInfoByMCMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

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
        deskInfo.curBureau = null == mahjongPlayer ? 0 :  mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        IRoomPlayer banker = this.bankerIndex >= 0 ? this.getRoomPlayer(this.bankerIndex) : null;
        deskInfo.bankerPlayerUid = banker == null || banker.isGuest() ? -1L : banker.getUid();
        deskInfo.fangPai = this.fangCard;
        deskInfo.laiZi = this.laiZiCard;
        deskInfo.piList = Collections.emptyList();
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
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        List<IMahjongPlayer> winners = Arrays.asList(player1, player2, player3);
        for (IMahjongPlayer winner : winners) {
            if (winner != null) {
                winner.addHandCard(huCard);
                this.calcHuScore(takePlayer, winner);
                winner.delHandCard(huCard);

                for (EPaiXing px : winner.getAllPaiXing()) {
                    winner.addHu(takePlayer.getUid() == winner.getUid() ? -1 : takePlayer.getUid(), px, huCard);
                }
            }
        }

        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest()) {
                continue;
            }
            int score = this.getScore(player.getScore(Score.SCORE, false));
            Logs.ROOM.debug("=== onHu player:：%d，SCORE：%d", player.getUid(), score);
            player.setScore(Score.SCORE, score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, score, true);
        }

        MCHHResultRecordAction resultRecordAction = (MCHHResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            MCHHResultRecordAction.PlayerInfo playerInfo = new MCHHResultRecordAction.PlayerInfo();

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
            MCHHResultRecordAction.ScoreInfo scoreInfo = new MCHHResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        if (this.bankerIndex != player1.getIndex()) {
            this.bankerIndex = (this.bankerIndex + 1) % this.playerNum;
        }
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        MCHHResultRecordAction resultRecordAction = (MCHHResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            int gangScore = this.getScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, gangScore, false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            MCHHResultRecordAction.PlayerInfo playerInfo = new MCHHResultRecordAction.PlayerInfo();
            MCHHResultRecordAction.ScoreInfo scoreInfo = new MCHHResultRecordAction.ScoreInfo();
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
        if (!ziMo && this.isLaiZi(huCard)) { // 不能胡打的赖子
            Logs.ROOM.debug("--- isHu 不能胡打的赖子");
            return false;
        }

        // 碰->杠->胡
        if (!ziMo && this.curAction == EActionOp.BAR && this.prevAction == EActionOp.BUMP && this.curCard == this.prevCard) {
            Logs.ROOM.debug("--- isHu 碰->杠->胡");
            return false;
        }

        // 漏胡不能胡
        if (!ziMo && player.isPassCard(EActionOp.HU, huCard)) {
            Logs.ROOM.debug("--- isHu 漏胡不能胡");
            return false;
        }

        MCMJMahjongPlayer mjPlayer = (MCMJMahjongPlayer) player;
        mjPlayer.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        mjPlayer.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        mjPlayer.clearPaiXing();

        int eatCnt = 0; // 吃牌的次数
        int normalBarCnt = 0; // 常规杠牌的次数（非皮非赖）
        int normalCPGCnt = 0; // 常规吃碰杠牌的次数（非皮非赖）
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        for (CPGNode node : player.getCPGNode()) {
            if (node.getType() == CPGNode.EType.ANY_THREE) {
                ++normalBarCnt;
                continue;
            }
            tempCards.get()[node.getCard1()] += 1;
            if (node.isEat()) {
                ++eatCnt;
            }
            if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                ++normalBarCnt;
                ++normalCPGCnt;
            }
        }

        long hu = 0;

        // 七对
        if (MahjongUtil.isSevenPair(player.getHandCardRaw(), this.laiZiCard)) {
            hu |= MahjongHu.QI_DUI;
            player.addPaiXing(EPaiXing.MCMJ_QI_DUI);
            if (!player.hasPaiXing(EPaiXing.MCMJ_YING) && MahjongUtil.isSevenPair(player.getHandCardRaw())) {
                player.addPaiXing(EPaiXing.MCMJ_YING);
            }
        }

        //  清一色
        if (hu !=0 || MahjongUtil.isHu(player.getHandCardRaw(), normalBarCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
            if (MahjongUtil.isQingYiSe(tempCards.get(), this.laiZiCard)) {
                hu |= MahjongHu.QING_YI_SE;
                player.addPaiXing(EPaiXing.MCMJ_QING_YI_SE);
                if (!player.hasPaiXing(EPaiXing.MCMJ_YING) && MahjongUtil.isQingYiSe(tempCards.get())) {
                    player.addPaiXing(EPaiXing.MCMJ_YING);
                }
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
        // 屁胡
        if (hu == 0 && MahjongUtil.isHu(player.getHandCardRaw(), normalBarCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
            hu |= MahjongHu.PI_HU;
            player.addPaiXing(EPaiXing.MCMJ_NORMAL);
            if (!player.hasPaiXing(EPaiXing.MCMJ_YING)
                    && MahjongUtil.isHu(player.getHandCardRaw(), normalBarCnt, false)) {
                player.addPaiXing(EPaiXing.MCMJ_YING);
            }
        }

        if (hu != 0) {
            player.setScore(Score.MJ_CUR_FANG_SCORE, this.isBig(hu) ? 5 : 3, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, this.isBig(hu) ? 1 : 2, false);
        } else {
            player.clearPaiXing();
        }
        return hu != 0;
    }

    @Override
    public void clear() {
        super.clear();
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
    public boolean isPi(byte card) {
        return false;
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (this.isLaiZi(takeCard)) {
            return !fangGang && player.hasHandCard(takeCard, 1);
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if ((this.features & MahjongFeatures.ENABLE_PENG) == 0) {
            return false;
        }
        return !this.isLaiZi(takeCard) && super.isBump(player, takeCard);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return !this.isLaiZi(takeCard) && super.isEat(player, takeCard);
    }

    @Override
    protected boolean isFrontendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isLaiZi((byte) (takeCard - 1)) || this.isLaiZi((byte) (takeCard - 2))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isFrontendEat(player, takeCard);
    }

    @Override
    protected boolean isMiddleEat(IMahjongPlayer player, byte takeCard) {
        if (this.isLaiZi((byte) (takeCard - 1)) || this.isLaiZi((byte) (takeCard + 1))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isMiddleEat(player, takeCard);
    }

    @Override
    protected boolean isBackendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isLaiZi((byte) (takeCard + 1)) || this.isLaiZi((byte) (takeCard + 2))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isBackendEat(player, takeCard);
    }

    @Override
    public boolean isMoreHu() {
        return true;
    }

    @Override
    public boolean canPiBar() {
        return false;
    }

    @Override
    public boolean canLaiZiBar() {
        return true;
    }

    @Override
    public Record getRecord() {
        if (this.record == null) {
            this.record = new MCHHMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return false;
    }

    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1) {
        int score = player1.hasPaiXing(EPaiXing.MCMJ_YING) ? 4 : 2; // 硬胡4分，软胡2分
        boolean ziMo = takePlayer.getUid() == player1.getUid();

        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest() || player.getUid() == player1.getUid()) {
                continue;
            }
            if (ziMo || player.getUid() == takePlayer.getUid()) { // 自摸或者点炮翻倍
                player.addScore(Score.SCORE, -score * 2, false);
                player1.addScore(Score.SCORE, score * 2, false);
            } else {
                player.addScore(Score.SCORE, -score, false);
                player1.addScore(Score.SCORE, score, false);
            }
        }
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
}
