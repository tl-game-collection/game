package com.xiuxiu.app.server.room.normal.mahjong2.whmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanOperateInfoByWHHH;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCrapAndCardInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByWHMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByWHHH;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithWHMJ;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongFeatures;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHelper;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHu;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IWHMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.WHMJMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.ResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.WHMJMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.WHMJResultRecordAction;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.*;

@GameInfo(gameType = GameType.GAME_TYPE_WHMJ, gameSubType = 3)
public class WHHHMahjongRoom extends MahjongRoom {
    private long features;
    private long barLaiZiPlayerUid = -1;

    private byte fangCard = -1; // 翻出的牌
    protected ArrayList<Byte> piList = new ArrayList<>(); // 皮列表

    // 大胡列表
    private static final List<Long> BIG_HU = Arrays.asList(
            MahjongHu.PENG_PENG_HU,
            MahjongHu.QING_YI_SE,
            MahjongHu.JIANG_YI_SE,
            MahjongHu.QUAN_QIU_REN,
            MahjongHu.QI_DUI
    );

    // 规则-起胡点数
    private int ruleMinPoints; // 0-不限制，1-9分（4人），2-10分（4人）
    private int[][] RULE_MIN_POINTS = {{0, 0, 0}, {3, 6, 9}, {4, 7, 10}}; // 0、1、2所对应的起胡点数，数组内对应2、3、4人的情况

    // 规则-封顶
    private int ruleTop;                  // 封顶
    private static final int RULE_TOP_1 = 1; // 封顶规则1
    private static final int RULE_TOP_2 = 2; // 封顶规则2
    private static final int[] TOP_POINTS = {40, 50}; // 封顶
    private static final int[] GOLD_TOP_POINTS = {60, 80}; // 金顶
    private static final int[] BAO_HU_POINTS = {80, 100}; // 包子、包胡

    // 规则-玩法
    private int rulePlay;
    private static final int RULE_WITH_TIMER = 0x0001; // 计时
    private static final int RULE_WITH_JFYLF = 0x0002; // 见风原赖翻
    private static final int RULE_WITH_LAIZI_ZHONG = 0x0004; // 吃赖子杠红中拦不住
    private static final int RULE_WITH_AN_GANG_MEN_QING = 0x0008; // 暗杠可门前清
    private static final int RULE_WITH_CPG_LAIZI = 0x0010; // 赖子可吃碰杠
    private static final int RULE_WITH_LAIZI_LIMIT = 0x0020; // 2个及以上赖子不可胡牌

    public WHHHMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public WHHHMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.ruleMinPoints = this.getRule().getOrDefault(RoomRule.RR_MJ_WH_BEGIN_HU_POINT, 1);
        this.ruleTop = this.getRule().getOrDefault(RoomRule.RR_MJ_TOP, RULE_TOP_1);
        this.rulePlay = this.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        if ((this.rulePlay & RULE_WITH_TIMER) != 0) {
            this.timeout = 20 * 1000;
        }
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
    protected void doDealAfter() {
        this.fangCard = this.allCard.removeFirst();

        // 确定赖子和皮
        this.laiZiCard = MahjongHelper.getLaiZiCardByFengJian(this.fangCard);
        if (this.laiZiCard == MahjongUtil.MJ_Z_FENG) {
            this.laiZiCard = MahjongUtil.MJ_F_FENG;
        }
        this.piList.add(MahjongUtil.MJ_Z_FENG);
    }

    @Override
    protected void doStart1() {
        this.doStartTake();
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new WHMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithWHMJ info = new PCLIRoomNtfBeginInfoByMJWithWHMJ();
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
            info.laiZi = this.laiZiCard;
            info.fangPai = this.fangCard;
            info.piList.addAll(this.piList);
            if ((this.rulePlay & RULE_WITH_JFYLF) != 0) {
                // 见风原癞翻倍
                if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                    info.isJFYLF = true;
                }
            }
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithWHMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithWHMJ();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.d = false;
        roomBeginInfo.laiZi = this.laiZiCard;
        roomBeginInfo.fangPai = this.fangCard;
        roomBeginInfo.piList.addAll(this.piList);
        if ((this.rulePlay & RULE_WITH_JFYLF) != 0) {
            // 见风原癞翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                roomBeginInfo.isJFYLF = true;
            }
        }
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByWHHH info = new PCLIMahjongNtfGameOverInfoByWHHH();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;
        if ((this.rulePlay & RULE_WITH_JFYLF) != 0) { // 见风原癞翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                info.isJFYLF = true;
            }
        }
        for (int i = 0; i < this.playerNum; ++i) {
            IWHMJMahjongPlayer player = (IWHMJMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByWHHH.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByWHHH.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByWHHH.HuInfo temp = new PCLIMahjongNtfGameOverInfoByWHHH.HuInfo();
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

            PCLIMahjongNtfGameOverInfoByWHHH.ScoreInfo scoreInfo = new PCLIMahjongNtfGameOverInfoByWHHH.ScoreInfo();
            playerInfo.score = scoreInfo;
            scoreInfo.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false)
                    + player.getScore(Score.MJ_CUR_EXTRA_GANG_SCORE, false)
                    + player.getScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, false)
                    + player.getScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, false);
            scoreInfo.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            scoreInfo.score = this.getFormatScore(player.getScore(Score.SCORE, false));
            scoreInfo.totalScore = this.getFormatScore(player.getScore());
            scoreInfo.extraScore = player.getScore(Score.MJ_CUR_EXTRA_GANG_SCORE, false);

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByWHHH.FinalResult();
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

        PCLIMahjongNtfDeskInfoByWHMJ deskInfo = new PCLIMahjongNtfDeskInfoByWHMJ();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 : mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        deskInfo.bankerPlayerUid = -1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest() ? -1L : this.allPlayer[this.bankerIndex].getUid();
        deskInfo.laiZi = this.laiZiCard;
        deskInfo.fangPai = this.fangCard;
        deskInfo.piList.addAll(this.piList);
        if ((this.rulePlay & RULE_WITH_JFYLF) != 0) { // 见风原癞翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                deskInfo.isJFYLF = true;
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
                deskInfo.allOnlineState.put(other.getUid(), other.isOffline() ? false : true);
                PCLIMahjongNtfDeskInfoByWHMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByWHMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByWHMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByWHMJ.CardNode();
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
    protected String getFormatScore(int value) {
        return NumberUtils.get2Decimals(value);
    }

    @Override
    public void onHuangZhuang(boolean next) {
        WHMJResultRecordAction resultRecordAction = (WHMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }

            WHMJResultRecordAction.PlayerInfo playerInfo = new WHMJResultRecordAction.PlayerInfo();

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
            WHMJResultRecordAction.ScoreInfo scoreInfo = new WHMJResultRecordAction.ScoreInfo();
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
    public Record getRecord() {
        if (null == this.record) {
            this.record = new WHMJMahjongRecord(this);
            if ((this.rulePlay & RULE_WITH_JFYLF) != 0) { // 见风原癞翻倍
                if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                    ((WHMJMahjongRecord) this.record).setJFYLF(true);
                }
            }
        }
        return this.record;
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        if (!ziMo && this.isPiOrLaiZi(huCard)) {
            return false;
        }

        // 漏胡不能胡
        if (!ziMo && player.isPassCard(EActionOp.HU, huCard)) {
            Logs.ROOM.debug("== isHu 漏胡不能胡");
            return false;
        }

        // 有皮不能胡
        if (this.hasPiCard(player)) {
            return false;
        }

        if ((this.rulePlay & RULE_WITH_LAIZI_LIMIT) != 0 && player.getHandCardCnt(this.laiZiCard) >= 2) {
            return false;
        }

        WHMJMahjongPlayer mjPlayer = (WHMJMahjongPlayer) player;
        mjPlayer.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        mjPlayer.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        mjPlayer.setScore(Score.MJ_CUR_SEVEN_SCORE, 0, false);
        mjPlayer.clearPaiXing();

        int eatCnt = 0; // 吃牌计数
        int anBarCnt = 0;
        int normalCPGCnt = 0; // 常规吃碰杠牌的次数
        int normalCPGLaiZiCnt = 0; // 赖子当自身杠次数
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        for (CPGNode node : player.getCPGNode()) {
            if (node.getType() == CPGNode.EType.ANY_THREE) {
                continue;
            }

            tempCards.get()[node.getCard1()] += 1;
            if (node.isEat()) {
                eatCnt++;
            }
            if (node.getType() == CPGNode.EType.BAR_AN) {
                anBarCnt++;
            } else if (node.getCard1() == this.laiZiCard
                    && (node.getType() == CPGNode.EType.BAR_FANG || node.getType() == CPGNode.EType.BAR_MING)) {
                normalCPGLaiZiCnt++;
            }
            if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                normalCPGCnt++;
            }
        }

        long hu = 0;
        do {
            // 七对
            if (MahjongUtil.isSevenPair(player.getHandCardRaw(), this.laiZiCard)) {
                hu |= MahjongHu.QI_DUI;
                player.addPaiXing(EPaiXing.WHMJ_QI_DUI);
                if (!player.hasPaiXing(EPaiXing.WHMJ_YING) && MahjongUtil.isSevenPair(player.getHandCardRaw())) {
                    player.addPaiXing(EPaiXing.WHMJ_YING);
                }
                // 豪华
                player.setScore(Score.MJ_CUR_SEVEN_SCORE, this.countOfHaoHua(player), false);
                if (player.getScore(Score.MJ_CUR_SEVEN_SCORE, false) >= 3) {
                    player.addPaiXing(EPaiXing.WHMJ_CHAO_HAO_HUA);
                }
               if (player.getScore(Score.MJ_CUR_SEVEN_SCORE, false) >= 2) {
                    player.addPaiXing(EPaiXing.WHMJ_SHUANG_HAO_HUA);
                }
                if (player.getScore(Score.MJ_CUR_SEVEN_SCORE, false) >= 1){
                    player.addPaiXing(EPaiXing.WHMJ_HAO_HUA);
                }
            }
            // 将一色
            if (MahjongUtil.isEyeYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                hu |= MahjongHu.JIANG_YI_SE;
                player.addPaiXing(EPaiXing.WHMJ_JIANG_YI_SE);
                if (!player.hasPaiXing(EPaiXing.WHMJ_YING) && MahjongUtil.isEyeYiSe(tempCards.get())) {
                    player.addPaiXing(EPaiXing.WHMJ_YING);
                }
            }
            if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE)) {
                //  清一色
                if (MahjongUtil.isQingYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                    hu |= MahjongHu.QING_YI_SE;
                    player.addPaiXing(EPaiXing.WHMJ_QING_YI_SE);
                    if (!player.hasPaiXing(EPaiXing.WHMJ_YING)
                            && MahjongUtil.isQingYiSe(tempCards.get())
                            && MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, false)) {
                        player.addPaiXing(EPaiXing.WHMJ_YING);
                    }
                }
                //  碰碰胡
                if (eatCnt == 0 && MahjongUtil.isPengPengHu(player.getHandCardRaw(), this.laiZiCard)) {
                    hu |= MahjongHu.PENG_PENG_HU;
                    player.addPaiXing(EPaiXing.WHMJ_PENG_PENG_HU);
                    if (!player.hasPaiXing(EPaiXing.WHMJ_YING) && MahjongUtil.isPengPengHu(player.getHandCardRaw())) {
                        player.addPaiXing(EPaiXing.WHMJ_YING);
                    }
                }
                // 全求人
                if (!ziMo && anBarCnt == 0 && player.getHandCardCnt() == 2) {
                    hu |= MahjongHu.QUAN_QIU_REN;
                    player.addPaiXing(EPaiXing.WHMJ_QUAN_QIU_REN);
                    if (!player.hasPaiXing(EPaiXing.WHMJ_YING)
                            && MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, false)) {
                        player.addPaiXing(EPaiXing.WHMJ_YING);
                    }
                }
                // 屁胡
                if (hu == 0) {
                    if (player.getHandCardCnt(this.laiZiCard) < 2) {
                        hu |= MahjongHu.PI_HU;
                        player.addPaiXing(EPaiXing.WHMJ_NORMAL);
                    }
                    if (hu != 0 && MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt)) {
                        player.addPaiXing(EPaiXing.WHMJ_YING);
                    }
                }
            }
            if (hu == 0) {
                break;
            } else if (ziMo) {
                // 门前清
                if ((this.rulePlay & RULE_WITH_AN_GANG_MEN_QING) != 0) {
                    if (player.getCPGNodeCntWithOutType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI) == 0) {
                        hu |= MahjongHu.MEN_QIAN_QING;
                        player.addPaiXing(EPaiXing.WHMJ_MEN_QING);
                    }
                } else if (player.getCPGNodeCntWithOutType(CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI) == 0) {
                    hu |= MahjongHu.MEN_QIAN_QING;
                    player.addPaiXing(EPaiXing.WHMJ_MEN_QING);
                }
            }
        } while (false);

        int points = 0;
        if (hu != 0) {
            if (ziMo) {
                hu |= MahjongHu.ZI_MO;
            } else {
                if (this.curAction == EActionOp.BAR) {
                    Logs.ROOM.debug("== isHu 抢杠胡");
                    hu = MahjongHu.QIANG_GANG_HU;
                    player.clearPaiXing();
                    player.addPaiXing(EPaiXing.WHMJ_QIANG_GANG_HU);
                } else if (this.prevPrevAction == EActionOp.BAR && !this.isPiOrLaiZi(this.prevPrevCard)) {
                    Logs.ROOM.debug("== isHu 杠上炮");
                    hu = MahjongHu.GANG_SHANG_PAO;
                    player.clearPaiXing();
                    player.addPaiXing(EPaiXing.WHMJ_GANG_SHANG_PAO);
                }
            }

            points = this.calcPoints(hu);

            if (this.ruleMinPoints != 0) {
                int fan = this.getHuFang(player);
                fan += normalCPGLaiZiCnt * 4 * 2;
                if (player.hasPaiXing(EPaiXing.WHMJ_YING)) {
                    fan += 1;
                }
                if (player.hasPaiXing(EPaiXing.WHMJ_HAO_HUA)) {
                    fan += player.getScore(Score.MJ_CUR_SEVEN_SCORE, false);
                }
                int totalPoints = 0;
                for (IRoomPlayer p : this.allPlayer) {
                    if (p == null || p.isGuest() || p.getUid() == player.getUid()) {
                        continue;
                    }

                    int otherFan = this.getHuFang((IMahjongPlayer) p);
                    int otherPoints = 0;
                    if (!ziMo && p.getUid() == takePlayerUid) { // 点炮大胡加3分，屁胡加1分
                        otherPoints += player.hasPaiXing(EPaiXing.WHMJ_NORMAL) ? 1 : 3;
                    }
                    totalPoints += (points + otherPoints) * Math.pow(2, fan + otherFan);
                }
                int minPointsRequired = RULE_MIN_POINTS[this.ruleMinPoints][this.getCurPlayerCnt() - 2];
                if (totalPoints < minPointsRequired) {
                    Logs.ROOM.debug("=== isHu 赢分太低:%d，不能胡", totalPoints);
                    hu = 0;
                }
            }
        }

        if (hu != 0) {
            Logs.ROOM.debug("=== isHu Score.MJ_CUR_FANG_SCORE: %d, huCard: %d", points, huCard);
            player.setScore(Score.MJ_CUR_FANG_SCORE, points, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, this.isBig(hu) ? 1 : 2, false);
        } else {
            player.clearPaiXing();
        }

        return hu != 0;
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        boolean isQGH = player1.hasPaiXing(EPaiXing.WHMJ_QIANG_GANG_HU);
        boolean isGSP = player1.hasPaiXing(EPaiXing.WHMJ_GANG_SHANG_PAO);
        if (isQGH || isGSP) {
            takePlayer.addShowFlag(EShowFlag.WHMJ_CHENG_BAO);
            this.calcHuScore(takePlayer, player1, true);
        } else {
            this.calcHuScore(takePlayer, player1, false);
        }

        // 牌型
        for (EPaiXing px : player1.getAllPaiXing()) {
            player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), px, huCard);
        }

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest() || player1.getUid() == player.getUid()) {
                continue;
            }
            int score = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            player.addScore(Score.SCORE, -score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, -score, true);
            player1.addScore(Score.SCORE, score, false);
            player1.addScore(Score.ACC_TOTAL_SCORE, score, true);
        }

        WHMJResultRecordAction resultRecordAction = (WHMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            WHMJResultRecordAction.PlayerInfo playerInfo = new WHMJResultRecordAction.PlayerInfo();

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
            WHMJResultRecordAction.ScoreInfo scoreInfo = new WHMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        if (isQGH || isGSP) {
            this.bankerIndex = takePlayer.getIndex();
        } else {
            this.bankerIndex = player1.getIndex();
        }
        this.getRecord().save();
        this.record();
        this.stop();
    }

    private int getHuFang(IMahjongPlayer player) {
        // 打皮算1番，打赖算2番
        int pi = player.getScore(Score.MJ_CUR_TAKE_PI_CNT, false);
        int lai = player.getScore(Score.MJ_CUR_TAKE_LAIZI_CNT, false);
        int fang = pi + (lai * 2);

        // 杠，掷骰子额外番
        fang += player.getScore(Score.MJ_CUR_EXTRA_GANG_SCORE, false);

        for (CPGNode node : player.getCPGNode()) {
            if (node.isEat()) {
                if (node.getType() == CPGNode.EType.EAT_LEFT) {
                    byte card = node.getCard1();
                    if (card == this.laiZiCard || card - 1 == this.laiZiCard || card - 2 == this.laiZiCard) {
                        fang += 2;
                    }
                } else if (node.getType() == CPGNode.EType.EAT_MIDDLE) {
                    byte card = node.getCard1();
                    if (card == this.laiZiCard || card - 1 == this.laiZiCard || card + 1 == this.laiZiCard) {
                        fang += 2;
                    }
                } else {
                    byte card = node.getCard1();
                    if (card == this.laiZiCard || card + 1 == this.laiZiCard || card + 2 == this.laiZiCard) {
                        fang += 2;
                    }
                }
                continue;
            }

            switch (node.getType()) {
                // 皮子杠算1番
                case BAR_PI:
                    fang += 1;
                    break;

                // 癞子杠和暗杠算2番
                case BAR_LAIZI:
                case BAR_AN:
                    fang += 2;
                    break;

                // 明杠、放杠
                case BAR_MING:
                case BAR_FANG:
                    // 杠赖子算10番，否则算2番
                    fang += node.getCard1() == this.laiZiCard ? 10 : 2;
                    break;

                case BUMP:
                    if (node.getCard1() == this.laiZiCard) {
                        fang += 6;
                    }
                    break;
            }
        }
        return fang;
    }

    @Override
    protected EPaiXing getPaiXing(IMahjongPlayer player) {
        return null;
    }

    @Override
    protected int getFang(IMahjongPlayer player) {
        if (this.isHu(player)) {
            return player.getScore(Score.MJ_CUR_FANG_SCORE, false);
        }
        return 0;
    }

    @Override
    protected int getFang(IMahjongPlayer player, byte addCard) {
        if (!MahjongHelper.isCardEnabledByFeatures(addCard, this.features)) {
            return 0;
        }
        return super.getFang(player, addCard);
    }

    @Override
    public void clear() {
        super.clear();
        this.barLaiZiPlayerUid = -1;
        this.fangCard = -1;
        this.piList.clear();
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
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction) action).getWaitInfo(player.getUid());
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
                Logs.ROOM.warn("%s 吃牌的人:%s 不能吃操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            // 相对特殊的地方，可能有两张牌可以操作：杠的牌和最后打出的那张牌
            byte card = (byte) param[0];
            if (EActionOp.OPEN_BAR != this.curAction && this.curCard != card) {
                if (this.barLaiZiPlayerUid == -1 || this.laiZiCard != card) {
                    Logs.ROOM.warn("%s 吃牌的人:%s 吃的不是当前打出去的牌 不能吃操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                    return ErrorCode.REQUEST_INVALID;
                }
            }
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            byte type = (byte) param[1];
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
                Logs.ROOM.warn("%s 吃牌的人:%s 不能吃操作2 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongWaitAction) action).opWait(waitInfo, EActionOp.EAT, param);
            if (((MahjongWaitAction) action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 吃牌的人:%s 本来不是吃牌动作, 无法吃牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
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
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction) action).getWaitInfo(player.getUid());
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
                Logs.ROOM.warn("%s %s 不能碰操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            // 相对特殊的地方，可能有两张牌可以操作：杠的牌和最后打出的那张牌
            byte card = (byte) param[0];
            if (EActionOp.OPEN_BAR != this.curAction && card != this.curCard) {
                if (this.barLaiZiPlayerUid == -1 || this.laiZiCard != card) {
                    Logs.ROOM.warn("%s %s 不能碰操作, 碰的不是当前打出去的牌 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                    return ErrorCode.REQUEST_INVALID;
                }
            }
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (!this.isBump(mahjongPlayer, card)) {
                Logs.ROOM.warn("%s 碰牌的人:%s 不能碰操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                return ErrorCode.REQUEST_INVALID;
            }
            mahjongPlayer.clearOperationTimeoutCnt();
            ((MahjongWaitAction) action).opWait(waitInfo, EActionOp.BUMP, param);
            if (((MahjongWaitAction) action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 碰牌的人:%s 本来不是碰牌动作, 无法碰牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
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
            // 相对特殊的地方，可能有两张牌可以操作：杠的牌和最后打出的那张牌
            if (EActionOp.OPEN_BAR != this.curAction && this.curCard != barCard) {
                if (this.barLaiZiPlayerUid == -1 || this.laiZiCard != barCard) {
                    Logs.ROOM.warn("%s 杠牌的人:%s 杠的不是当前打出去的牌 不能杠操作2 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.isEat(), waitInfo.isBump(), waitInfo.isBar(), waitInfo.isHu());
                    return ErrorCode.REQUEST_INVALID;
                }
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
            ((MahjongTakeAction) action).setOp(EActionOp.BAR);
            ((MahjongTakeAction) action).setParam(param);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 杠牌的人:%s 本来不是杠牌动作, 无法杠牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (this.allCard.isEmpty()) {
            return false;
        }
        if (this.isPi(takeCard)) {
            if (fangGang) {
                return false;
            }
            return player.hasHandCard(takeCard, 1);
        }
        if (this.isLaiZi(takeCard)) {
            if (fangGang) {
                return (this.rulePlay & RULE_WITH_CPG_LAIZI) != 0 && player.hasHandCard(takeCard, 3);
            }
            return player.hasHandCard(takeCard, 1);
        }
        if (player.hasBump(takeCard)) { // 补杠，仅摸的牌可当即补杠
            return this.curAction == EActionOp.FUMBLE && takeCard == player.getLastFumbleCard();
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if ((this.rulePlay & RULE_WITH_CPG_LAIZI) == 0) {
            if (this.isPiOrLaiZi(takeCard)) {
                return false;
            }
        } else if (this.isPi(takeCard)) {
            return false;
        }
        return super.isBump(player, takeCard);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        if ((this.rulePlay & RULE_WITH_CPG_LAIZI) == 0) {
            if (this.isPiOrLaiZi(takeCard)) {
                return false;
            }
        } else if (this.isPi(takeCard)) {
            return false;
        }
        return super.isEat(player, takeCard);
    }

    @Override
    protected boolean isFrontendEat(IMahjongPlayer player, byte takeCard) {
        if ((this.rulePlay & RULE_WITH_CPG_LAIZI) == 0) {
            if (this.isPiOrLaiZi((byte) (takeCard - 1)) || this.isPiOrLaiZi((byte) (takeCard - 2))) {
                return false;
            }
        } else if (this.isPi((byte) (takeCard - 1)) || this.isPi((byte) (takeCard - 2))) {
            return false;
        }
        return super.isFrontendEat(player, takeCard);
    }

    @Override
    protected boolean isMiddleEat(IMahjongPlayer player, byte takeCard) {
        if ((this.rulePlay & RULE_WITH_CPG_LAIZI) == 0) {
            if (this.isPiOrLaiZi((byte) (takeCard - 1)) || this.isPiOrLaiZi((byte) (takeCard + 1))) {
                return false;
            }
        } else if (this.isPi((byte) (takeCard - 1)) || this.isPi((byte) (takeCard + 1))) {
            return false;
        }
        return super.isMiddleEat(player, takeCard);
    }

    @Override
    protected boolean isBackendEat(IMahjongPlayer player, byte takeCard) {
        if ((this.rulePlay & RULE_WITH_CPG_LAIZI) == 0) {
            if (this.isPiOrLaiZi((byte) (takeCard + 1)) || this.isPiOrLaiZi((byte) (takeCard + 2))) {
                return false;
            }
        } else if (this.isPi((byte) (takeCard + 1)) || this.isPi((byte) (takeCard + 2))) {
            return false;
        }
        return super.isBackendEat(player, takeCard);
    }

    @Override
    public boolean isMoreHu() {
        return false;
    }

    @Override
    public boolean isPi(byte card) {
        return this.piList.contains(card);
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
    public List<Byte> getPiList() {
        return this.piList;
    }

    @Override
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return false;
    }

    @Override
    public void onFumble(IMahjongPlayer player) {
        this.barLaiZiPlayerUid = -1;
        super.onFumble(player);
    }

    @Override
    public void onBar(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        if (takePlayer.getUid() != this.barLaiZiPlayerUid) {
            this.barLaiZiPlayerUid = -1;
        }
        super.onBar(takePlayer, player, param);
        if (takePlayer.getUid() == player.getUid()) { // 自己杠
            byte barCard = (byte) param[0];
            if (this.isLaiZi(barCard)) {
                this.barLaiZiPlayerUid = player.getUid();
            } else if ((this.rulePlay & RULE_WITH_LAIZI_ZHONG) == 0 || barCard != MahjongUtil.MJ_Z_FENG) {
                this.barLaiZiPlayerUid = -1;
            }
        }
    }

    @Override
    protected void doBarAfter(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        MahjongWaitAction waitAction = null;
        if (type == EBarType.BAR_MING) {
            waitAction = this.getWaitAction(player, barCard, true,-1);
        }
        if (waitAction != null) {
            this.addAction(waitAction);
        } else {
            this.checkBarScore(takePlayer, player, type, barCard);

            byte card;
            PCLIMahjongNtfCrapAndCardInfo ntfCrapAndCard = new PCLIMahjongNtfCrapAndCardInfo();
            while (true) {
                if (this.allCard.size() <= 6) {
                    card = this.allCard.removeLast();
                    break;
                }
                int points = RandomUtil.random(1, 6);
                ntfCrapAndCard.craps.add(points);
                if (this.fangCard != MahjongUtil.MJ_Z_FENG || points != Math.max(this.crap1, this.crap2)) {
                    card = this.allCard.remove(this.allCard.size() - points);
                    break;
                }
                player.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, this.isPi(barCard) ? 1 : 2, false);
            }
            ntfCrapAndCard.playerUid = player.getUid();
            ntfCrapAndCard.card = barCard;
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_CRAP_AND_CARD, ntfCrapAndCard);

            if (ntfCrapAndCard.craps.isEmpty()) {
                this.fumbleForBar(player, card);
            } else {
                DelayAction delayAction = new DelayAction(this, 1500 * ntfCrapAndCard.craps.size());
                final WHHHMahjongRoom self = this;
                delayAction.setCallback(new ICallback<Object>() {
                    @Override
                    public void call(Object... o) {
                        self.fumbleForBar(player, card);
                    }
                });
                this.addAction(delayAction);
            }
        }
    }

    private void fumbleForBar(IMahjongPlayer player, byte card) {
        player.fumbleCard(card);
        this.lastFumbleIndex = player.getIndex();
        this.lastFumbleCard = card;
        this.setCurOp(player, EActionOp.FUMBLE, card);

        Logs.ROOM.debug("%s 摸牌的人:%s 摸牌:%s", this, player, MahjongUtil.getCardStr(card));

        this.generateTingInfo(player);
        this.doSendFumble(player, card);

        this.doFumbleAfter(player, card);
    }

    @Override
    public void onEat(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        this.barLaiZiPlayerUid = -1;
        super.onEat(takePlayer, player, param);
    }

    @Override
    public void onTake(IMahjongPlayer takePlayer, boolean auto, Object... param) {
        if (takePlayer.getUid() != this.barLaiZiPlayerUid) {
            this.barLaiZiPlayerUid = -1;
        }
        super.onTake(takePlayer, auto, param);

        byte card = (byte) param[0];
        if (this.isLaiZi(card)) {
            takePlayer.addScore(Score.MJ_CUR_TAKE_LAIZI_CNT, 1, false);
        } else if (this.isPi(card)) {
            takePlayer.addScore(Score.MJ_CUR_TAKE_PI_CNT, 1, false);
        }
    }

    @Override
    protected MahjongWaitAction getWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu, int flag) {
        return generateWaitAction(player, takeCard, onlyHu);
    }

    @Override
    public void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat, byte takeCard) {
        WHHHWaitInfo wait = new WHHHWaitInfo(takeCard);
        wait.setBar(bar);
        wait.setBump(bump);
        wait.setHu(hu);
        wait.setEat(eat);
        this.doSendCanOperate(player, wait);
    }

    private void doSendCanOperate(IMahjongPlayer player, WHHHWaitInfo waitInfo) {
        PCLIMahjongNtfCanOperateInfoByWHHH operates = new PCLIMahjongNtfCanOperateInfoByWHHH();
        List<WHHHWaitInfo> waits = new ArrayList<>();
        waits.add(waitInfo);
        if (waitInfo.getExtraWait() != null) {
            waits.add(waitInfo.getExtraWait());
        }
        for (WHHHWaitInfo wait : waits) {
            PCLIMahjongNtfCanOperateInfoByWHHH.Operate op = new PCLIMahjongNtfCanOperateInfoByWHHH.Operate();
            op.bar = wait.isBar();
            op.bump = wait.isBump();
            op.eat = wait.isEat();
            op.hu = wait.isHu();
            op.card = wait.getCard();
            operates.ops.add(op);
        }
        player.send(CommandId.CLI_NTF_MAHJONG_CAN_OPERATE, operates);
        Logs.ROOM.debug("CLI_NTF_MAHJONG_CAN_OPERATE %s", operates);
    }

    private WHHHWaitInfo generateWaitInfo(IMahjongPlayer player, IMahjongPlayer takePlayer, byte card, boolean onlyHu) {
        int canEatIndex = (takePlayer.getIndex() + 1) % this.playerNum;
        boolean hu = this.isHu(player, takePlayer.getUid(), card);
        boolean bar = !onlyHu && this.isBar(player, card, true);
        boolean bump = !onlyHu && (bar || this.isBump(player, card));
        boolean eat = !onlyHu && (canEatIndex == player.getIndex() && this.isEat(player, card));
        if (!hu && !bar && !bump && !eat) {
            return null;
        }
        if (this.onCheckOver()) {
            bar = false;
        }
        long timeout = player.getTimeout(this.timeout);
        if (hu) {
            if (this.isMustHu()) {
                timeout = HU_TIMEOUT;
                bar = false;
                bump = false;
                eat = false;
            } else if (player.isAutoTake()) {
                timeout = AUTO_TAKE_TIMEOUT;
            }
        }

        WHHHWaitInfo waitInfo = new WHHHWaitInfo(card);
        waitInfo.setPlayerUid(player.getUid());
        waitInfo.setIndex(player.getIndex());
        waitInfo.setTimeout(timeout);
        waitInfo.setHu(hu);
        waitInfo.setBar(bar);
        waitInfo.setBump(bump);
        waitInfo.setEat(eat);
        return waitInfo;
    }

    private MahjongWaitAction generateWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu) {
        MahjongWaitAction waitAction = null;
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
            if (otherPlayer == null || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid() || otherPlayer.isOver()) {
                continue;
            }

            WHHHWaitInfo waitInfo = this.generateWaitInfo(otherPlayer, player, takeCard, onlyHu);
            if (takeCard != this.laiZiCard && this.barLaiZiPlayerUid == player.getUid()) {
                WHHHWaitInfo waitInfo2 = this.generateWaitInfo(otherPlayer, player, this.laiZiCard, onlyHu);
                if (waitInfo2 != null) {
                    if (waitInfo == null) {
                        waitInfo = waitInfo2;
                    } else {
                        waitInfo.setExtraWait(waitInfo2);
                    }
                }
            }

            if (waitInfo == null) {
                continue;
            }

            if (waitAction == null) {
                waitAction = new MahjongWaitAction(this, player);
                // pass card会用到takeCard
                waitAction.setTakeCard(takeCard);
            }
            waitAction.addWait(waitInfo);

            this.doSendCanOperate(otherPlayer, waitInfo);
        }
        return waitAction;
    }

    // 豪华的个数，仅限七对牌型
    private int countOfHaoHua(IMahjongPlayer player) {
        int count = 0;
        for (byte i = MahjongUtil.MJ_1_WANG; i <= MahjongUtil.MJ_BAI_FENG; i++) {
            if (player.getHandCardCnt(i) == 4) {
                count++;
            }
        }
        return count;
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

        if ((hu & MahjongHu.QIANG_GANG_HU) != 0 || (hu & MahjongHu.GANG_SHANG_PAO) != 0) {
            points += BAO_HU_POINTS[this.ruleTop - 1];
        } else if (this.isBig(hu)) {
            if ((hu & MahjongHu.MEN_QIAN_QING) != 0) {
                points += 10;
            } else {
                points += (hu & MahjongHu.ZI_MO) != 0 ? 8 : 5;
            }
        } else {
            if ((hu & MahjongHu.MEN_QIAN_QING) != 0) {
                points += 5;
            } else {
                points += (hu & MahjongHu.ZI_MO) != 0 ? 3 : 1;
            }
        }


        Logs.ROOM.debug("=== points: %d, calcPoints(0x%x)", points, hu);
        return points;
    }

    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1, boolean baoHu) {
        Logs.ROOM.debug("=== calcHuScore  taker: %d, player1: %d ===", takePlayer.getUid(), player1.getUid());

        // 基础分
        int basePoints = player1.getScore(Score.MJ_CUR_FANG_SCORE, false);
        Logs.ROOM.debug("basePoints: %d", basePoints);

        if (baoHu) {
            int points = this.getScore(BAO_HU_POINTS[this.ruleTop - 1]);
            takePlayer.setScore(Score.MJ_CUR_HU_SCORE, points, false);
        } else {
            boolean menQing = player1.hasPaiXing(EPaiXing.WHMJ_MEN_QING);
            int fan = this.getHuFang(player1);
            if (player1.hasPaiXing(EPaiXing.WHMJ_YING)) {
                Logs.ROOM.debug("\tYING: +1");
                fan += 1;
            }
            if (player1.hasPaiXing(EPaiXing.WHMJ_HAO_HUA)||player1.hasPaiXing(EPaiXing.WHMJ_SHUANG_HAO_HUA)||player1.hasPaiXing(EPaiXing.WHMJ_CHAO_HAO_HUA)) {
                int hao = player1.getScore(Score.MJ_CUR_SEVEN_SCORE, false);
                Logs.ROOM.debug("\t豪华七对: +%d", hao);
                fan += hao;
            }
            // 多个大胡，每多一个加一番
            int countOfBig = 0;
            for (EPaiXing px : player1.getAllPaiXing()) {
                if (px == EPaiXing.WHMJ_QING_YI_SE || px == EPaiXing.WHMJ_PENG_PENG_HU
                        || px == EPaiXing.WHMJ_JIANG_YI_SE || px == EPaiXing.WHMJ_QUAN_QIU_REN
                        || px == EPaiXing.WHMJ_QI_DUI) {
                    countOfBig++;
                }
            }
            if (countOfBig > 1) {
                fan += countOfBig - 1;
            }

            // 计算输家分数
            int countOfTop = 0; // 封顶个数
            for (IRoomPlayer p : this.allPlayer) {
                if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                    continue;
                }

                WHMJMahjongPlayer otherPlayer = (WHMJMahjongPlayer) p;
                Logs.ROOM.debug("\t玩家：%d", otherPlayer.getUid());

                int points = basePoints;
                if (otherPlayer.getUid() == takePlayer.getUid() && !menQing) {
                    int dianPaoPoints = player1.hasPaiXing(EPaiXing.WHMJ_NORMAL) ? 1 : 3;
                    Logs.ROOM.debug("\t\tdianPaoPoints:%d + 1", dianPaoPoints);
                    points += dianPaoPoints;
                }

                int otherFan = this.getHuFang(otherPlayer);
                points *= (int) Math.pow(2, fan + otherFan);

                if (points >= TOP_POINTS[this.ruleTop - 1]) {
                    otherPlayer.addShowFlag(EShowFlag.WHMJ_FENG_DING);
                    points = TOP_POINTS[this.ruleTop - 1];
                    countOfTop++;
                }

                Logs.ROOM.debug("\t\tpoints:%d, fan:%d, otherFan:%d", points, fan, otherFan);
                otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, this.getScore(points), false);
            }

            // 是否算金顶
            if (countOfTop == this.getCurPlayerCnt() - 1) {
                for (IRoomPlayer p : this.allPlayer) {
                    if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                        continue;
                    }
                    int points = this.getScore(GOLD_TOP_POINTS[this.ruleTop - 1]);
                    p.setScore(Score.MJ_CUR_HU_SCORE, points, false);
                    ((IMahjongPlayer) p).delShowFlag(EShowFlag.WHMJ_FENG_DING);
                    ((IMahjongPlayer) p).addShowFlag(EShowFlag.WHMJ_JJIN_DING);
                    Logs.ROOM.debug("\t玩家：%d，金顶：%d", p.getUid(), points);
                }
            }

            // 见风原赖翻，不受封顶限制
            if ((this.rulePlay & RULE_WITH_JFYLF) != 0
                    && (this.laiZiCard == this.prevLaiZiCard || MahjongHelper.isJianPai(this.laiZiCard))) {
                for (IRoomPlayer p : this.allPlayer) {
                    if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                        continue;
                    }
                    int points = p.getScore(Score.MJ_CUR_HU_SCORE, false) * 2;
                    if (points != 0) {
                        p.setScore(Score.MJ_CUR_HU_SCORE, points, false);
                        Logs.ROOM.debug("\t玩家：%d，见风原赖翻：%d", p.getUid(), points);
                    }
                }
            }
        }

        // 底分除以10
        for (IRoomPlayer p : this.allPlayer) {
            if (p != null && !p.isGuest()) {
                int score = p.getScore(Score.MJ_CUR_HU_SCORE, false) / 10;
                p.setScore(Score.MJ_CUR_HU_SCORE, score, false);
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
}
