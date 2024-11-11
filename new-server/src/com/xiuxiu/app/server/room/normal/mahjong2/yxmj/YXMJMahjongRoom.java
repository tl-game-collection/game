package com.xiuxiu.app.server.room.normal.mahjong2.yxmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByYXMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByYXMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfLaiPiInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfYangPaiInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithYXMJ;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.kwx.EKWXPlayRule;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeActionByYX;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongFeatures;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHelper;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHu;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IYXMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.MahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.YXMJMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.*;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.*;

/**
 * 阳新麻将
 */
@GameInfo(gameType = GameType.GAME_TYPE_YXMJ)
public class YXMJMahjongRoom extends MahjongRoom implements IMahjongYangPai {
    private long features;

    private byte fangPai = -1; // 翻出的牌
    protected ArrayList<Byte> piList = new ArrayList<>(); // 皮列表
    private static final int TOP_POINTS = 64;   // 64封顶
    private static final int GOLD_TOP_POINTS = 80;  // 80金顶
    private List<Byte> haiDiLaoCard = new ArrayList<>();  // 海底捞摸的牌
    private long haiDiLaoStarPlayerUid = -1;              // 海底捞开始摸牌的玩家uid
    // 大胡列表
    private static final List<Long> BIG_HU = Arrays.asList(
            MahjongHu.PENG_PENG_HU,         // 碰碰胡
            MahjongHu.QING_YI_SE,           // 清一色
            MahjongHu.JIANG_YI_SE,          // 将一色
            MahjongHu.QI_DUI,               // 七  对
            MahjongHu.MEN_QIAN_QING,        // 门前清
            MahjongHu.GANG_SHANG_KAI_HUA,   // 杠上开花
            MahjongHu.GANG_SHANG_PAO,
            MahjongHu.HAI_DI_LAO
    );
    // 配置：玩法
    private int is258Jiang = 1; // 做将          1:（默认258做将) 2 :（任意将）
    private int bridgeCard = 1; // 杠牌是否翻倍    1:(默认 翻倍)  2 :(不翻倍)
    private int playingRules;
    private static final int RULE_WITH_TIMER = 0x0001; // 倒计时
    private static final int RULE_PENG_PENG_HU = 0x0002; // 碰碰胡
    private static final int RULE_YANG_FAN_BEI = 0x0004; // 仰翻倍
    private static final int RR_DETECTION_IP = 0x0008; // IP检测开关(0 关闭 1 开启)
    


    public YXMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public YXMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.playingRules = this.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        if ((this.playingRules & RULE_WITH_TIMER) != 0) {
            this.timeout = 10 * 1000;
        }
        this.is258Jiang = this.getRule().getOrDefault(RoomRule.RR_YXMJ_GANG_GENERAL_CARD, 1);
        this.bridgeCard = this.getRule().getOrDefault(RoomRule.RR_YXMJ_GANG_BRIDGE_CARD, 1);
        this.detectionIP = 0 != (this.playingRules & RR_DETECTION_IP);
    }

    @Override
    protected void doShuffle() {
        // 默认包含序数牌和字牌
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
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithYXMJ info = new PCLIRoomNtfBeginInfoByMJWithYXMJ();
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
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithYXMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithYXMJ();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.d = false;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    protected void doStart1() {
        this.startWithFanLaiZi();
    }

    private void startWithFanLaiZi() {
        // 发完牌后，牌墙上第一张翻出，作为皮子牌
        this.fangPai = this.allCard.removeFirst();
        this.laiZiCard = MahjongHelper.getLaiZiCardByFengJian(this.fangPai);   // 确定赖子
        // 通知赖子和皮子信息
        PCLIMahjongNtfLaiPiInfo ntfLaiPi = new PCLIMahjongNtfLaiPiInfo();
        ntfLaiPi.piList = this.piList;
        ntfLaiPi.laiZi = this.laiZiCard;
        ntfLaiPi.fangPai = this.fangPai;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_LAI_PI, ntfLaiPi);
        ((RecordMahjongRoomBriefInfo) this.getRecord().getRoomInfo()).setLaiZiCard(this.laiZiCard);
        ((RecordMahjongRoomBriefInfo) this.getRecord().getRoomInfo()).setPiList(this.piList);
        //开始打牌
        this.doStartTake();
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
                MahjongTakeActionByYX action = this.getTakeAction(player, card);
                action.setOp(EActionOp.HU);
                this.addAction(action);
            }else{
                MahjongTakeAction  action = super.getTakeAction(player, card);
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
    protected MahjongTakeActionByYX getTakeAction(IMahjongPlayer player, byte defaultTakeCard) {
        long timeout = player.getTimeout(this.timeout);
        if (this.isLastCard) {
            timeout = BaseMahjongRoom.LAST_CARD_TAKE_TIMEOUT;
        } else if (player.isAutoTake()) {
            timeout = BaseMahjongRoom.AUTO_TAKE_TIMEOUT;
        }
        MahjongTakeActionByYX action = new MahjongTakeActionByYX(this, player, timeout);
        action.setParam(defaultTakeCard);
        return action;
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        YXMJMahjongPlayer mjPlayer = (YXMJMahjongPlayer) player;
        mjPlayer.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        mjPlayer.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        mjPlayer.clearPaiXing();

        if (!ziMo && this.isLaiZi(huCard)) {
            return false;
        }

        // 漏胡不能胡
        if (!ziMo && player.isPass(EActionOp.HU)) {
            return false;
        }

        int eatCnt = 0;         // 吃牌计数
        int yangCnt = 0;        // 仰牌计数
        int normalCPGCnt = 0;   // 常规杠牌的次数（非皮非赖）
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
        int points;

        // 七对
        if (yangCnt == 0 && MahjongUtil.isSevenPair(player.getHandCardRaw(), this.laiZiCard)) {
            hu |= MahjongHu.QI_DUI;
            player.addPaiXing(EPaiXing.YXMJ_QI_DUI);
            if (!player.hasPaiXing(EPaiXing.YXMJ_YING) && MahjongUtil.isSevenPair(player.getHandCardRaw())) {
                player.addPaiXing(EPaiXing.YXMJ_YING);
            }
            // 豪华
            ((YXMJMahjongPlayer) player).setHao(this.countOfHaoHua(player));
            if (((YXMJMahjongPlayer) player).getHao() > 0) {
                player.addPaiXing(EPaiXing.YXMJ_HAO_HUA);
            }
        }
        // 将一色”
        if (yangCnt == 0 && MahjongUtil.isEyeYiSe(tempCards.get(), this.laiZiCard)) {
            hu |= MahjongHu.JIANG_YI_SE;
            player.addPaiXing(EPaiXing.YXMJ_JIANG_YI_SE);
            if (!player.hasPaiXing(EPaiXing.YXMJ_YING) && MahjongUtil.isEyeYiSe(tempCards.get())) {
                player.addPaiXing(EPaiXing.YXMJ_YING);
            }
        }
        //  碰碰胡
        if (eatCnt == 0 && yangCnt == 0 && this.enabledPengPengHu() && MahjongUtil.isPengPengHu(player.getHandCardRaw(), this.laiZiCard)) {
            hu |= MahjongHu.PENG_PENG_HU;
            player.addPaiXing(EPaiXing.YXMJ_PENG_PENG_HU);
            if (!player.hasPaiXing(EPaiXing.YXMJ_YING) && MahjongUtil.isPengPengHu(player.getHandCardRaw())) {
                player.addPaiXing(EPaiXing.YXMJ_YING);
            }
        }
        if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt, true, this.laiZiCard, Integer.MAX_VALUE)) {
            //  清一色
            if (yangCnt == 0 && MahjongUtil.isQingYiSe(tempCards.get(), this.laiZiCard)) {
                hu |= MahjongHu.QING_YI_SE;
                player.addPaiXing(EPaiXing.YXMJ_QING_YI_SE);
                if (!player.hasPaiXing(EPaiXing.YXMJ_YING) && MahjongUtil.isQingYiSe(tempCards.get()) && MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, false)) {
                    player.addPaiXing(EPaiXing.YXMJ_YING);
                }
            }
        }

        if (this.enabled258Jiang()) {
            if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt, true, this.laiZiCard, Integer.MAX_VALUE)) {
                // 门前清
                if (ziMo && player.getCPGNodeCntWithOutType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI,CPGNode.EType.ANY_THREE) == 0) {
                    hu |= MahjongHu.MEN_QIAN_QING;
                    player.addPaiXing(EPaiXing.YXMJ_MEN_QING);
                } else {
                    if(!this.isBig(hu)) {
                        hu |= MahjongHu.PI_HU;
                        player.addPaiXing(EPaiXing.YXMJ_NORMAL);
                        if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt + yangCnt)) {
                            player.addPaiXing(EPaiXing.YXMJ_YING);
                        }
                    }
   
                }
                
            }

        } else {
            if (MahjongUtil.isHu258Eye(player.getHandCardRaw(), normalCPGCnt + yangCnt, this.laiZiCard)) {
                if(!this.isBig(hu)) {
                    hu |= MahjongHu.PI_HU;
                    player.addPaiXing(EPaiXing.YXMJ_NORMAL);
                    if (MahjongUtil.isHu258Eye(player.getHandCardRaw(), normalCPGCnt + yangCnt)) {
                        player.addPaiXing(EPaiXing.YXMJ_YING);
                    }
                }
               
            }
            
        }
        if (hu != 0) {
            if (EActionOp.BAR == this.prevAction && ziMo) {
                // 杠上开花
                hu |= MahjongHu.GANG_SHANG_KAI_HUA;
                player.addPaiXing(EPaiXing.YXMJ_GANG_KAI);
            } else if (EActionOp.BAR == this.prevPrevAction) {
                hu |= MahjongHu.GANG_SHANG_PAO;
                player.addPaiXing(EPaiXing.YXMJ_GANG_SHANG_PAO);
            } else if (EActionOp.BAR == this.curAction) {
                hu |= MahjongHu.QIANG_GANG_HU;// 抢杠胡
                player.addPaiXing(EPaiXing.YXMJ_QIANG_GANG_HU);
            }
            if (this.allCard.size() < 14) {
                hu |= MahjongHu.HAI_DI_LAO;
                player.addPaiXing(EPaiXing.YXMJ_HAI_DI_LAO);
            }
            if (ziMo) {
                hu |= MahjongHu.ZI_MO;
            }
        }

        points = this.calcPoints(hu);

        if (hu != 0) {
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
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        this.calcHuScore(takePlayer, player1);
        do {
            if (player1.hasPaiXing(EPaiXing.YXMJ_QING_YI_SE)) {
                if (player1.getScore(Score.MJ_CUR_KAI_KOU_CNT, false) >= 3) {
                    int chengBaoPlayerIndex = ((IYXMJMahjongPlayer) player1).getChengBagPlayerIndex();
                    IMahjongPlayer chengBagPlayer = (IMahjongPlayer) this.getRoomPlayer(chengBaoPlayerIndex);
                    chengBagPlayer.addShowFlag(EShowFlag.YXMJ_CHENG_BAO);
                    // 承包
                    for (int i = 0; i < this.playerNum; ++i) {
                        IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                        if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid() || chengBagPlayer.getUid() == otherPlayer.getUid()) {
                            continue;
                        }
                        chengBagPlayer.addScore(Score.MJ_CUR_HU_SCORE, otherPlayer.getScore(Score.MJ_CUR_HU_SCORE, false), false);
                        otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, 0, false);
                    }
                    break;
                }
            }
            if (player1.hasPaiXing(EPaiXing.YXMJ_JIANG_YI_SE)) {
                if (player1.getScore(Score.MJ_CUR_KAI_KOU_CNT, false) >= 3) {
                    int chengBaoPlayerIndex = ((IYXMJMahjongPlayer) player1).getChengBagPlayerIndex();
                    IMahjongPlayer chengBagPlayer = (IMahjongPlayer) this.getRoomPlayer(chengBaoPlayerIndex);
                    chengBagPlayer.addShowFlag(EShowFlag.YXMJ_CHENG_BAO);
                    // 承包
                    for (int i = 0; i < this.playerNum; ++i) {
                        IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                        if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid() || chengBagPlayer.getUid() == otherPlayer.getUid()) {
                            continue;
                        }
                        chengBagPlayer.addScore(Score.MJ_CUR_HU_SCORE, otherPlayer.getScore(Score.MJ_CUR_HU_SCORE, false), false);
                        otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, 0, false);
                    }
                    break;
                }
            }
            if (player1.hasPaiXing(EPaiXing.YXMJ_QIANG_GANG_HU)) {
                // 抢杠胡
                for (int i = 0; i < this.playerNum; ++i) {
                    IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                    if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                        continue;
                    }
                    if (otherPlayer.getUid() == takePlayer.getUid()) {
                        otherPlayer.addScore(Score.MJ_CUR_HU_SCORE, otherPlayer.getScore(Score.MJ_CUR_HU_SCORE, false), false);
                    }
                }
                break;
            }

        } while (false);

        List<EPaiXing> allPaiXing = player1.getAllPaiXing();
        if (allPaiXing.isEmpty()) {
            player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), EPaiXing.YXMJ_NORMAL, huCard);
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
            int score = this.getScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));

            Logs.ROOM.debug("=== onHu player:：%d，MJ_CUR_HU_SCORE：%d", player.getUid(), score);
            player.addScore(Score.SCORE, -score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, -score, true);
            player1.addScore(Score.SCORE, score, false);
            player1.addScore(Score.ACC_TOTAL_SCORE, score, true);
        }

        // 实际竞技值计算，并抽水
        this.getRoomHandle().calculateGold();

        YXMJResultRecordAction resultRecordAction = (YXMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        resultRecordAction.addHaiDiLaoCard(this.haiDiLaoCard);
        resultRecordAction.setHaiDiLaoStartPlayerUid(this.haiDiLaoStarPlayerUid);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            YXMJResultRecordAction.PlayerInfo playerInfo = new YXMJResultRecordAction.PlayerInfo();

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
            YXMJResultRecordAction.ScoreInfo scoreInfo = new YXMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        if (this.bankerIndex != player1.getIndex()) {
            this.bankerIndex = player1.getIndex();
        }
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[(this.curOpIndex + 1) % this.playerNum];
            if (null == player || player.isGuest()) {
                continue;
            }
          
            if(this.allCard.size() < 14) {
                byte lastCard = this.allCard.removeFirst();
                this.haiDiLaoCard.add(lastCard);
                if (0 == i) {
                    this.haiDiLaoStarPlayerUid = player.getUid();
                }
                this.setCurOp(player, EActionOp.FUMBLE, lastCard);
                if (this.isHu0(player, -1, true, lastCard)) {
                    this.onHu(player, player, lastCard);
                    return;
                } 
            }

        }
        YXMJResultRecordAction resultRecordAction = (YXMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        resultRecordAction.addHaiDiLaoCard(this.haiDiLaoCard);
        resultRecordAction.setHaiDiLaoStartPlayerUid(this.haiDiLaoStarPlayerUid);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            int gangScore = this.getScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, gangScore, false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

//            YXMJResultRecordAction.PlayerInfo playerInfo = new YXMJResultRecordAction.PlayerInfo();
//            YXMJResultRecordAction.ScoreInfo scoreInfo = new YXMJResultRecordAction.ScoreInfo();
//            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
//            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
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
            YXMJResultRecordAction.PlayerInfo playerInfo = new YXMJResultRecordAction.PlayerInfo();
            YXMJResultRecordAction.ScoreInfo scoreInfo = new YXMJResultRecordAction.ScoreInfo();
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
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByYXMJ info = new PCLIMahjongNtfGameOverInfoByYXMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;
        info.haiDiLaoCard.addAll(this.haiDiLaoCard);
        info.haiDiLaoStartPlayerUid = this.haiDiLaoStarPlayerUid;
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByYXMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByYXMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByYXMJ.HuInfo temp = new PCLIMahjongNtfGameOverInfoByYXMJ.HuInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByYXMJ.ScoreInfo();
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE , false));
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByYXMJ.FinalResult();
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
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }

        

        PCLIMahjongNtfDeskInfoByYXMJ deskInfo = new PCLIMahjongNtfDeskInfoByYXMJ();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 : mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        IRoomPlayer banker = this.bankerIndex >= 0 ? this.getRoomPlayer(this.bankerIndex) : null;
        deskInfo.bankerPlayerUid = banker == null || banker.isGuest() ? -1L : banker.getUid();
        deskInfo.fangPai = this.fangPai;
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
                
                
                int yangCnt = 0;        // 仰牌计数
                for (CPGNode node : other.getCPGNode()) {
                    if (node.getType() == CPGNode.EType.ANY_THREE) {
                        yangCnt++;
                        continue;
                    }
               
                }

                deskInfo.allOnlineState.put(other.getUid(), !other.isOffline());
                PCLIMahjongNtfDeskInfoByYXMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByYXMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                deskPlayerInfo.yangPai = (null != other) && (((YXMJMahjongPlayer) other).getCanOperate());
                
                deskPlayerInfo.yangPaiCnt=yangCnt;
                
                deskPlayerInfo.tingInfo = ((MahjongPlayer)other).getTingInfo().getTing();
                deskPlayerInfo.fumbleCnt = other.getFumbleCnt();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByYXMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByYXMJ.CardNode();
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
    public IRoomPlayer createPlayer() {
        return new YXMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    public void clear() {
        super.clear();
        this.piList.clear();
        this.fangPai = -1;
        this.laiZiCard = -1;
        this.haiDiLaoStarPlayerUid = -1;
        this.haiDiLaoCard.clear();
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
    public void onTake(IMahjongPlayer takePlayer, boolean auto, Object... param) {
        super.onTake(takePlayer, auto, param);

        byte card = (byte) param[0];
        if (this.isLaiZi(card)) {
            takePlayer.addScore(Score.MJ_CUR_TAKE_LAIZI_CNT, 1, false);
        }
        ((YXMJMahjongPlayer) takePlayer).addTakeCardCnt();
       boolean isBanker= takePlayer.getIndex() == this.bankerIndex;
        if(!((YXMJMahjongPlayer) takePlayer).getCanOperate()){
            if((isBanker&&((YXMJMahjongPlayer) takePlayer).getTakeCardCnt()==2)||(!isBanker&&((YXMJMahjongPlayer) takePlayer).getTakeCardCnt()==1)){
                ((YXMJMahjongPlayer) takePlayer).setCanOperate(true);
            }
        }
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
    public boolean canLaiZiBar() {
        return true;
    }

    @Override
    public boolean isMoreHu() {
        return false;
    }

    @Override
    public Record getRecord() {
        // 回放数据
        if (this.record == null) {
            this.record = new YXMJMahjongRecord(this);
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

    // 是否需要258做将
    private boolean enabled258Jiang() {
        return (this.is258Jiang) != 1;
    }

    // 杠牌是否翻倍
    private boolean enabledBridgeCard() {
        return (this.bridgeCard) == 1;
    }

    // 仰是否翻倍
    private boolean enabledYangFanBei() {
        return (this.playingRules & RULE_YANG_FAN_BEI) != 0;
    }

    //是否可以碰碰胡
    private boolean enabledPengPengHu() {
        return (this.playingRules & RULE_PENG_PENG_HU) != 0;
    }

    // 豪华的个数，仅限七对牌型
    private int countOfHaoHua(IMahjongPlayer player) {
        int count = 0;
        for (byte i = MahjongUtil.MJ_1_WANG; i <= MahjongUtil.MJ_BAI_FENG; i++) {
            int cardCnt = player.getHandCardCnt(i);
            if (cardCnt == 4) {
                count++;
            }
        }
        return Math.min(count, 4);
    }

    private int getHuFang(IMahjongPlayer player) {
        // 打赖算1番
        int points = player.getScore(Score.MJ_CUR_TAKE_LAIZI_CNT, false);
        int countKaiKou = 0; // 三扑
        // 杠番、明杠和放杠算1番、癞子杠,暗杠算2番，仰牌一番（含赖子加一番）
        for (CPGNode node : player.getCPGNode()) {
            switch (node.getType()) {
                case BAR_MING:
                    if (this.enabledBridgeCard()) {
                        points += 1;
                    }
                    countKaiKou += 1;
                    break;
                case BAR_FANG:
                    if (this.enabledBridgeCard()) {
                        points += 1;
                    }
                    countKaiKou += 1;
                    break;
                case BAR_AN:
                    if (this.enabledBridgeCard()) {
                        points += 2;
                        break;
                    }
                    break;
                case BAR_LAIZI:
                    points += 1;
                    break;
                case ANY_THREE:
                    if (this.enabledYangFanBei()) {
                        points += MahjongHelper.isJianPai(this.laiZiCard) ? 2 : 1;
                        break;

                    }
                    break;
                default:
                    countKaiKou += 1;
                    break;
            }
        }
        if (countKaiKou >= 3) {
            points += 1;
            Logs.ROOM.debug("countKaiKou  == " + countKaiKou);
        }
        return points;
    }

    // 计算胡牌分数
    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1) {
        // 基础分
        int fan = this.getHuFang(player1);
        int count = 0;
        for (EPaiXing px : player1.getAllPaiXing()) {
            switch (px) {
                case YXMJ_MEN_QING:
                case YXMJ_PENG_PENG_HU:
                case YXMJ_QING_YI_SE:
                case YXMJ_JIANG_YI_SE:
                case YXMJ_QI_DUI:
                case YXMJ_HAO_HUA:
                case YXMJ_GANG_KAI:
                case YXMJ_HAI_DI_LAO:
                case YXMJ_GANG_SHANG_PAO:
                    fan += 1;
                    count++;
                    break;
            }
        }
        if (count > 0) fan -= 1;
        if (player1.hasPaiXing(EPaiXing.YXMJ_QIANG_GANG_HU)) {
            player1.setScore(Score.MJ_CUR_FANG_SCORE, count != 0 ? 7 : 2, false);

        }
        int points = player1.getScore(Score.MJ_CUR_FANG_SCORE, false);
        if (player1.hasPaiXing(EPaiXing.YXMJ_YING)) {
            fan += 1;
        }

        // 计算输家分数
        int countOfTop = 0; // 封顶个数
        boolean ziMo = takePlayer.getUid() == player1.getUid();
        for (IRoomPlayer p : this.allPlayer) {
            if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                continue;
            }
            YXMJMahjongPlayer otherPlayer = (YXMJMahjongPlayer) p;
            int otherPoints = points;
            Logs.ROOM.debug("\t玩家：%d", otherPlayer.getUid());
            if (!player1.hasPaiXing(EPaiXing.YXMJ_QIANG_GANG_HU)) {
                if (otherPlayer.getUid() == takePlayer.getUid()) {
                    if (count == 0 && !ziMo) {
                        Logs.ROOM.debug("\t\totherPoints:%d + 1", otherPoints);
                        otherPoints += 1;
                    } else {
                        Logs.ROOM.debug("\t\totherPoints:%d + 2", otherPoints);
                        otherPoints += 2;
                    }
                }
            }
            int otherFan = this.getHuFang(otherPlayer);
            otherPoints *= (int) Math.pow(2, fan + otherFan);
            if (otherPoints >= TOP_POINTS) {
                otherPoints = TOP_POINTS;
                countOfTop++;
            }
            Logs.ROOM.debug("\t\totherPoints:%d, fan:%d, otherFan:%d", otherPoints, fan, otherFan);
            otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, otherPoints, false);
            Logs.ROOM.debug("\t\t最终分数：%d", otherPoints);
        }

        // 是否算金顶
        if (countOfTop == this.getCurPlayerCnt() - 1) {
            for (IRoomPlayer p : this.allPlayer) {
                if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                    continue;
                }
                p.setScore(Score.MJ_CUR_HU_SCORE, GOLD_TOP_POINTS, false);
                ((IMahjongPlayer) p).addShowFlag(EShowFlag.YXMJ_JJIN_DING);
                Logs.ROOM.debug("=== 玩家：%d, 金顶：%d", p.getUid(), GOLD_TOP_POINTS);
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

    // 胡的点数
    private int calcPoints(long hu) {
        int points = 0;

        if (this.isBig(hu)) {
            points += (hu & MahjongHu.ZI_MO) != 0 ? 7 : 5;
        } else if ((hu & MahjongHu.PI_HU) != 0) {
            points += (hu & MahjongHu.ZI_MO) != 0 ? 2 : 1;
        }

        Logs.ROOM.debug("=== points: %d, calcPoints(%x)", points, hu);
        return points;
    }

    @Override
    public ErrorCode yangPai(IPlayer player, List<Byte> cards) {
        YXMJMahjongPlayer mjPlayer = (YXMJMahjongPlayer) this.getRoomPlayer(player.getUid());
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
    protected MahjongWaitAction getWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu, int flag) {
        MahjongWaitAction waitAction = null;
        int canEatIndex = getNextRoomPlayer(player.getIndex()).getIndex();
        for (int i = 0; i < allPlayer.length; ++i) {
            IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid() || otherPlayer.isOver()) {
                continue;
            }
            boolean hu = false;
            boolean bar = false;
            if (this.allCard.size() > 13) {
                hu = this.isHu(otherPlayer, player.getUid(), takeCard);
                bar = onlyHu ? false : this.isBar(otherPlayer, takeCard, true);
            }
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

    /**
     * 生成听信息
     *
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
                if (this.curPlayerCnt == 2 && j <= 9) {
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
