package com.xiuxiu.app.server.room.normal.mahjong2.fzmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfBuHuaInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByFZMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByFZMJ;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithFZMJ;
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
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongFeatures;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHelper;
import com.xiuxiu.app.server.room.player.mahjong2.FZMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.FZMJMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.FZMJResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.ResultRecordAction;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.*;

//@GameInfo(gameType = GameType.GAME_TYPE_FZMJ)
public class FZMJMahjongRoom extends MahjongRoom {
    private static final long PI_HU = 0x00000001L; // 屁胡
    private static final long TIAN_HU = 0x00001000L;            // 天胡
    private static final long QIANG_JIN = 0x00002000L;          // 抢金
    private static final long WU_HUA_WU_GANG = 0x00004000L;          // 无花无杠
    private static final long YI_ZHANG_HUA = 0x00008000L;          // 一张花
    private static final long SAN_JIN_DAO = 0x00010000L;        // 三金倒
    private static final long JIN_QUE = 0x00020000L;            // 金雀
    private static final long JIN_LONG = 0x00040000L;            // 金龙
    private static final long HUN_YI_SE = 0x00080000L;            // 混一色
    private static final long QING_YI_SE = 0x00100000L;            // 清一色

    protected long features;
    protected ArrayList<Byte> piList = new ArrayList<>();   // 皮列表
    protected byte fanPai = -1;                             // 翻出的牌
    protected List<Byte> haiDiLaoCard = new ArrayList<>();  // 海底捞摸的牌
    protected long haiDiLaoStarPlayerUid = -1;              // 海底捞开始摸牌的玩家uid

    // 玩法配置
    protected int cardNum = 14;                             // 张数
    protected int fangPao = 1;                              // 放炮
    protected boolean isQHYS = false;                       // 是否有请混一色
    protected boolean isHua = false;                        // 是否有花牌
    protected boolean isJinLong = false;                    // 是否有金龙
    protected boolean isTen = false;                    // 是否十秒
    protected int playType = 1;                             // 1表示新的 2，表示老的
    protected int carelcNum = 1;                             //圈数

    private int lastBankIndex = -1;

    public FZMJMahjongRoom(RoomInfo roomInfo) {
        this(roomInfo, ERoomType.NORMAL);
    }

    public FZMJMahjongRoom(RoomInfo roomInfo, ERoomType roomType) {
        super(roomInfo, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.cardNum = this.getRule().getOrDefault(RoomRule.RR_FZMJ_CARD_NUM, 14);
        this.fangPao = this.getRule().getOrDefault(RoomRule.RR_FZMJ_FANG_PAO, 1);
        this.isQHYS = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFZMJPlayRule.QHYS.getValue());
        this.isHua = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFZMJPlayRule.HUA_PAI.getValue());
        this.isJinLong = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFZMJPlayRule.JIN_LONG.getValue());
        this.isTen = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EFZMJPlayRule.TEN.getValue());
        this.playType = this.getRule().getOrDefault(RoomRule.RR_FZMJ_PLAY_TYPE, 1);
        this.carelcNum = this.getRule().getOrDefault(RoomRule.RR_FZMJ_CARELC_NUM, 1);
        if (this.isTen) {
            this.timeout = 10 * 1000;
        }
        this.features = MahjongFeatures.WITH_TONG_ZI | MahjongFeatures.WITH_TIAO_ZI | MahjongFeatures.WITH_WAN_ZI
                | MahjongFeatures.WITH_FENG | MahjongFeatures.WITH_JIAN | MahjongFeatures.WITH_HUA;

        this.features |= MahjongFeatures.ENABLE_CHI | MahjongFeatures.ENABLE_PENG | MahjongFeatures.ENABLE_MING_GANG
                | MahjongFeatures.ENABLE_AN_GANG | MahjongFeatures.ENABLE_HUA_GANG;

        if (!this.isHua) {
            this.features ^= MahjongFeatures.WITH_FENG | MahjongFeatures.WITH_JIAN | MahjongFeatures.WITH_HUA;
        }
    }

    @Override   // 洗牌
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB) {
            MahjongHelper.fixWithLibrariedCards(this.allCard);
        }
        if (this.allCard.isEmpty()) {
            MahjongHelper.fixWithFeaturedCards(this.allCard, this.features);
            MahjongHelper.shuffle(this.allCard);
        }
    }

    @Override   // 发牌
    protected void doDeal() {
        Logs.ROOM.debug("牌库的数量 " + this.allCard.size());
        this.crap1 = RandomUtil.random(1, 6);
        this.crap2 = RandomUtil.random(1, 6);
        this.initBankerIndex();
        for (int i = 0; i < this.cardNum - 1; ++i) {
            for (int j = 0; j < this.playerNum; ++j) {
                IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[(this.bankerIndex + j) % this.playerNum];
                if (null == player || player.isGuest()) {
                    continue;
                }
                byte cardValue = this.allCard.removeFirst();
                player.addHandCard(cardValue);
            }
        }
        IMahjongPlayer bankerPlayer = (IMahjongPlayer) this.allPlayer[this.bankerIndex];
        byte cardValue = this.allCard.removeFirst();
        this.lastFumbleCard = cardValue;
        this.lastFumbleIndex = this.bankerIndex;
        this.setCurOp(bankerPlayer, EActionOp.FUMBLE, cardValue);
        bankerPlayer.fumbleCard(cardValue);
        this.doDealAfter();
        for (IRoomPlayer iRoomPlayer : this.allPlayer) {
            IMahjongPlayer player = (IMahjongPlayer) iRoomPlayer;
            if (null == player || player.isGuest()) {
                continue;
            }
            ((MahjongRecord) this.getRecord()).addPlayer(player, player.getIndex(), player.getBureau());
        }
    }

    @Override   // 发牌后处理
    protected void doDealAfter() {
        // if (!this.isHua) {
        this.fanPai = this.allCard.removeFirst();
        this.laiZiCard = this.fanPai;
        // }
    }

    @Override   // 发送开始
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithFZMJ info = new PCLIRoomNtfBeginInfoByMJWithFZMJ();
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
            info.piList = this.piList;
            info.fangPai = this.fanPai;
            info.laiZi = this.laiZiCard;
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithFZMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithFZMJ();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.d = false;
        roomBeginInfo.piList = this.piList;
        roomBeginInfo.fangPai = this.fanPai;
        roomBeginInfo.laiZi = this.laiZiCard;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override   // 开始
    protected void doStart1() {
        if (!this.isHua) {
            this.doStartTake(); // 开始打牌
        } else {
            this.doSendBeginBuHua();
        }
    }

    protected void doFumbleAfter(IMahjongPlayer player, byte card) {
        if (card >= MahjongUtil.MJ_D_FENG) {
            this.doSendBeginMyBuHua(player, card);
            return;
        }

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

    @Override   // 是否胡
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        player.clearPaiXing();
        if (!ziMo && this.isLaiZi(huCard)) {
            return false;
        }

        // 漏胡不能胡
        if (!ziMo && player.isPass(EActionOp.HU)) {
            return false;
        }
        int meldCnt = player.getCPGNode().size(); // 碰/杠了多少句话
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        for (CPGNode node : player.getCPGNode()) {
            tempCards.get()[node.getCard1()] += 1;
        }
        long hu = 0;
        //混一色
        if (player.hasHandCard(this.laiZiCard, 1) && this.isQHYS) {
            if (MahjongUtil.isHu(player.getHandCardRaw(), meldCnt, this.laiZiCard)) {
                if (MahjongUtil.isQingYiSe(tempCards.get())) {
                    hu |= HUN_YI_SE;
                    player.addPaiXing(EPaiXing.FZMJ_HUN_YI_SE);
                }
            }

        }
        //抢金
        if (MahjongUtil.isHu(player.getHandCardRaw(), meldCnt, this.laiZiCard)) {

            if ((player.getFumbleCnt() == 0 && player.getIndex() != this.bankerIndex && !player.hasHandCard(this.laiZiCard, 2)) || (player.getFumbleCnt() == 1 && player.getIndex() == this.bankerIndex && !player.hasHandCard(this.laiZiCard, 2))) {
                hu |= QIANG_JIN;
                player.addPaiXing(EPaiXing.FZMJ_QIANG_JIN);
            }
           // 屁胡
            if (hu == 0) {
                hu |= PI_HU;
                player.addPaiXing(EPaiXing.FZMJ_NORMAL);
            }

        }
        // 金雀
        if (player.hasHandCard(this.laiZiCard, 2)) {
            if (isJingQue(player.getHandCardRaw(), this.laiZiCard)) {
                hu |= JIN_QUE;
                player.addPaiXing(EPaiXing.FZMJ_JIN_QUE);
            }
        }


        // 三金倒
        if (player.hasHandCard(this.laiZiCard, 3)) {
            hu |= SAN_JIN_DAO;
            player.addPaiXing(EPaiXing.FZMJ_SAN_JIN_DAO);
        }
        if (MahjongUtil.isHu(player.getHandCardRaw(), meldCnt)) {
            //  清一色
            if (MahjongUtil.isQingYiSe(tempCards.get()) && this.isQHYS) {
                hu |= QING_YI_SE;
                player.addPaiXing(EPaiXing.YYMJ_QING_YI_SE);
            }
            if (ziMo && this.isTianHu(player)) { // 天胡
                hu |= TIAN_HU;
                player.addPaiXing(EPaiXing.YYMJ_TIAN_HU);
            }

            //金龙
            if (player.hasHandCard(this.laiZiCard, 3) && this.isJinLong) {
                hu |= JIN_LONG;
                player.addPaiXing(EPaiXing.FZMJ_JIN_LONG);
            }
            //一张花
            if (((FZMJMahjongPlayer) player).getHuaNum() == 1) {
                hu |= YI_ZHANG_HUA;
                player.addPaiXing(EPaiXing.FZMJ_YI_ZHANG_HUA);
            }
            //无花无杠
            if (((FZMJMahjongPlayer) player).getHuaNum() == 0 && meldCnt == 0) {
                hu |= WU_HUA_WU_GANG;
                player.addPaiXing(EPaiXing.FZMJ_WU_HUA_WU_GANG);
            }


        }
        if (hu == 0) {
            player.clearPaiXing();
        }

        return hu != 0;
    }

    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * value;
    }

    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1) {
        // 基础分
        int huScore = 1;
        List<Integer> list = new ArrayList<Integer>();
        for (EPaiXing px : player1.getAllPaiXing()) {
            switch (px) {
                case FZMJ_QING_YI_SE:
                    list.add(240);
                case FZMJ_JIN_LONG:
                    list.add(120);
                case FZMJ_HUN_YI_SE:
                    list.add(120);
                case FZMJ_JIN_QUE:
                    list.add(60);
                case FZMJ_SAN_JIN_DAO:
                    list.add(40);
                case FZMJ_TIAN_HU:
                    list.add(30);
                case FZMJ_QIANG_JIN:
                    list.add(30);
                case FZMJ_WU_HUA_WU_GANG:
                    list.add(30);
                case FZMJ_YI_ZHANG_HUA:
                    list.add(15);
                    Logs.ROOM.debug("== calcHuScore, px:%s, huScore:%d", px, huScore);
                    break;
            }

        }
        if(list.size()>0) {
            huScore = Collections.max(list);
        }
        //花牌
        List<Byte> huaCard = ((FZMJMahjongPlayer) player1).getHuaCard();
        int huaCount = huaCard.size();
        for (int j = MahjongUtil.MJ_D_FENG; j < 34; ++j) {
            if (Collections.frequency(huaCard, j) == 4) {
                huaCount+= 2;
            }
        }
        int count1 = 0, count2 = 0;

        for (int i = 0; i < huaCard.size(); i++) {
            if (huaCard.get(i) == 35 || huaCard.get(i) == 36 || huaCard.get(i) == 37 || huaCard.get(i) == 38) {
                count1++;
            }
            if (huaCard.get(i) == 39 || huaCard.get(i) == 40 || huaCard.get(i) == 41 || huaCard.get(i) == 42) {
                count2++;
            }
        }
        if (count1 == 4) huaCount += 2;
        if (count2 == 4) huaCount += 2;
        //杠牌
        int barFan = 0;
        for (CPGNode node : player1.getCPGNode()) {
            switch (node.getType()) {
                case BAR_MING:
                    barFan += 1;
                case BAR_AN:
                    barFan += 2;
                    break;

            }
        }

        boolean ziMo = takePlayer.getUid() == player1.getUid();
        int bankCount = ((FZMJMahjongPlayer) player1).getBankCount() - 1;
        int jing = player1.getHandCardCnt(this.laiZiCard);
        int score = (huaCount + barFan + bankCount + jing + 5) * 2 + this.getScore(huScore);
        int score1 = (huaCount + barFan + bankCount + jing + 5) + this.getScore(huScore);
        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest() || player.getUid() == player1.getUid()) {
                continue;
            }

            if (ziMo) {
                player1.addScore(Score.MJ_CUR_HU_SCORE, score, false);
                player.addScore(Score.MJ_CUR_HU_SCORE, -score, false);
            } else if (player.getUid() == takePlayer.getUid()) {
                if (fangPao == 1) {
                    player1.addScore(Score.MJ_CUR_HU_SCORE, score1, false);
                    player.addScore(Score.MJ_CUR_HU_SCORE, -score1, false);
                } else if (fangPao == 2) {
                    player1.addScore(Score.MJ_CUR_HU_SCORE, score * (this.playerNum - 1), false);
                    player.addScore(Score.MJ_CUR_HU_SCORE, -score * (this.playerNum - 1), false);
                } else {
                    player1.addScore(Score.MJ_CUR_HU_SCORE, score * 2, false);
                    player.addScore(Score.MJ_CUR_HU_SCORE, -score * 2, false);
                }
            }
        }

// 小结
        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest()) {
                continue;
            }

            int curScore = this.getScore(player.getScore(Score.MJ_CUR_HU_SCORE, false) + player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, curScore, false);
        }
    }

    @Override   // 执行胡
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        List<IMahjongPlayer> winners = Arrays.asList(player1, player2, player3);
        // 胡分
        for (IRoomPlayer player : this.allPlayer) {
            if (player != null && !player.isGuest()) {
                player.setScore(Score.MJ_CUR_HU_SCORE, 0, false);
            }
        }
        if (this.bankerIndex == this.lastBankIndex) {
            FZMJMahjongPlayer p = (FZMJMahjongPlayer) this.getRoomPlayer(bankerIndex);
            p.setBankCount(p.getBankCount() + 1);
        } else if (this.lastBankIndex != -1 && this.bankerIndex != this.lastBankIndex) {
            FZMJMahjongPlayer p = (FZMJMahjongPlayer) this.getRoomPlayer(lastBankIndex);
            p.setBankCount(0);
        }
        this.lastBankIndex = this.bankerIndex;
        this.calcHuScore(takePlayer, player1);
        for (EPaiXing px : player1.getAllPaiXing()) {
            player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), px, huCard);
        }

        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest()) {
                continue;
            }
            int score = player.getScore(Score.SCORE, false);
            Logs.ROOM.debug("=== onHu player:：%d，SCORE：%d", player.getUid(), score);
            player.setScore(Score.SCORE, score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, score, true);

        }
        FZMJResultRecordAction resultRecordAction = (FZMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            FZMJResultRecordAction.PlayerInfo playerInfo = new FZMJResultRecordAction.PlayerInfo();

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
            FZMJResultRecordAction.ScoreInfo scoreInfo = new FZMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }
        this.bankerIndex = player1.getIndex() == this.bankerIndex ? player1.getIndex() : this.getNextRoomPlayer(this.bankerIndex).getIndex();
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override   // 发送结束
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByFZMJ info = new PCLIMahjongNtfGameOverInfoByFZMJ();
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
            PCLIMahjongNtfGameOverInfoByFZMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByFZMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByFZMJ.HuInfo temp = new PCLIMahjongNtfGameOverInfoByFZMJ.HuInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByFZMJ.ScoreInfo();
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE, false));
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByFZMJ.FinalResult();
                playerInfo.finalResult.anGangCnt = player.getScore(Score.ACC_MJ_AN_GANG_CNT, true);
                playerInfo.finalResult.fangPaoCnt = player.getScore(Score.ACC_MJ_DIAN_PAO_CNT, true);
                playerInfo.finalResult.huCnt = player.getScore(Score.ACC_MJ_HU_CNT, true);
                playerInfo.finalResult.mingGangCnt = player.getScore(Score.ACC_MJ_MING_GANG_CNT, true);
                playerInfo.finalResult.ziMoCnt = player.getScore(Score.ACC_MJ_ZIMO_CNT, true);
                playerInfo.finalResult.score = player.getScore(Score.ACC_TOTAL_SCORE, true) / 100;
            }

            info.allPlayer.put(player.getUid(), playerInfo);
        }
        Logs.ROOM.debug(" 通知游戏结束信息 %s", info);
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, info);
    }

    @Override   // 荒庄
    public void onHuangZhuang(boolean next) {
//        for (int i = 0; i < this.playerNum; ++i) {
//            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[(this.curOpIndex + 1) % this.playerNum];
//            if (null == player || player.isGuest()) {
//                continue;
//            }
//            byte lastCard = this.allCard.removeFirst();
//            this.haiDiLaoCard.add(lastCard);
//            if (0 == i) {
//                this.haiDiLaoStarPlayerUid = player.getUid();
//            }
//            this.setCurOp(player, EActionOp.FUMBLE, lastCard);
//            if (this.isHu0(player, -1, true, lastCard)) {
//                this.onHu(player, player, lastCard);
//                return;
//            }
//        }
        FZMJResultRecordAction resultRecordAction = (FZMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
//        resultRecordAction.addHaiDiLaoCard(this.haiDiLaoCard);
//        resultRecordAction.setHaiDiLaoStartPlayerUid(this.haiDiLaoStarPlayerUid);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            int gangScore = this.getScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, gangScore, false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);
            FZMJResultRecordAction.PlayerInfo playerInfo = new FZMJResultRecordAction.PlayerInfo();
            FZMJResultRecordAction.ScoreInfo scoreInfo = new FZMJResultRecordAction.ScoreInfo();
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

    @Override   // 同步牌桌信息
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }

        PCLIMahjongNtfDeskInfoByFZMJ deskInfo = new PCLIMahjongNtfDeskInfoByFZMJ();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 : mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        IRoomPlayer banker = this.bankerIndex >= 0 ? this.getRoomPlayer(this.bankerIndex) : null;
        deskInfo.bankerPlayerUid = banker == null || banker.isGuest() ? -1L : banker.getUid();
        deskInfo.fangPai = this.fanPai;
        deskInfo.laiZi = this.laiZiCard;
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();

        try {
            this.rwLock.readLock().lock();
            int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer other = (IMahjongPlayer) this.allPlayer[i];
                PCLIMahjongNtfDeskInfoByFZMJ.BuHuaInfo buHuaInfo = new PCLIMahjongNtfDeskInfoByFZMJ.BuHuaInfo();
                if (null == other || other.isGuest()) {
                    continue;
                }
                deskInfo.allOnlineState.put(other.getUid(), !other.isOffline());
                PCLIMahjongNtfDeskInfoByFZMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByFZMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                 buHuaInfo.huaNUm = ((FZMJMahjongPlayer) other).getHuaNum();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByFZMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByFZMJ.CardNode();
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
                deskInfo.buHuaInfo.put(other.getUid(), buHuaInfo);
            }
        } finally {
            this.rwLock.readLock().unlock();
        }
        Logs.ROOM.debug(" 通知更新牌桌信息 %s", deskInfo);
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override   // 是否杠
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (this.isPiOrLaiZi(takeCard)) {
            return !fangGang && player.hasHandCard(takeCard, 1);
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override   // 是否碰
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if ((this.features & MahjongFeatures.ENABLE_PENG) == 0) {
            return false;
        }
        return !this.isPiOrLaiZi(takeCard) && super.isBump(player, takeCard);
    }

    @Override   // 是否吃
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return !this.isPiOrLaiZi(takeCard) && super.isEat(player, takeCard);
    }

    @Override   // 是否前吃
    protected boolean isFrontendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi((byte) (takeCard - 1)) || this.isPiOrLaiZi((byte) (takeCard - 2))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isFrontendEat(player, takeCard);
    }

    @Override   // 是否中吃
    protected boolean isMiddleEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi((byte) (takeCard - 1)) || this.isPiOrLaiZi((byte) (takeCard + 1))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isMiddleEat(player, takeCard);
    }

    @Override   // 是否后吃
    protected boolean isBackendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi((byte) (takeCard + 1)) || this.isPiOrLaiZi((byte) (takeCard + 2))) {
            return false;
        }
        if ((this.features & MahjongFeatures.ENABLE_CHI) == 0) {
            return false;
        }
        return super.isBackendEat(player, takeCard);
    }

    @Override   // 是否计数杠分
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return false;
    }

    @Override   // 玩家数据
    public IRoomPlayer createPlayer() {
        return new FZMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override   // 获取牌型
    protected EPaiXing getPaiXing(IMahjongPlayer player) {
        return null;
    }

    @Override   // 获取番数
    protected int getFang(IMahjongPlayer player) {
        return this.isHu(player) ? player.getScore(Score.MJ_CUR_FANG_SCORE, false) : 0;
    }

    @Override   // 获取回放记录
    public Record getRecord() {
        if (this.record == null) {
            this.record = new FZMJMahjongRecord(this);
        }
        return this.record;
    }

    @Override   // 清理
    public void clear() {
        super.clear();

    }

    // 发送开始补花
    protected void doSendBeginBuHua() {
        Logs.ROOM.debug(" 通知开始补花 ");
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_BU_HUA, null);
        this.buHuaInfo();
    }

    // 补花信息
    protected void buHuaInfo() {

        int time = 0;
        PCLIMahjongNtfBuHuaInfo info = new PCLIMahjongNtfBuHuaInfo();
        int sendCnt;
        int num=0;
        do {
            sendCnt = 0;
            for (int i = 0; i < this.playerNum; ++i) {
                List<Byte> cardList = new ArrayList<Byte>();
                IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                PCLIMahjongNtfBuHuaInfo.BuHuaInfo buHuaInfo = new PCLIMahjongNtfBuHuaInfo.BuHuaInfo();
                if (null == player || player.isGuest()) {
                    continue;
                }
                for (int j = MahjongUtil.MJ_D_FENG; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
                    int cnt = player.getHandCardCnt((byte) j);  // 这里是获取当前牌在手里有多少张
                    if (cnt > 0) {
                        for (int k = 0; k < cnt; ++k) {
                            ((FZMJMahjongPlayer) player).addHuaCard((byte) j);
                            ((FZMJMahjongPlayer) player).setHuaNum(++num);
                            buHuaInfo.huaCard.add((byte) j);
                            buHuaInfo.huaNum++;
                            player.takeCard((byte) j);
                            player.delAllHandCard((byte) j);
                            byte card = this.allCard.removeFirst(); // 拿到的牌
                            cardList.add(card);
                            // player.addHandCard(card);
                            if (card >= MahjongUtil.MJ_D_FENG)
                                ++sendCnt;
                        }
                    }
                }
                if (cardList.size() > 0) {
                    for (int m = 0; m < cardList.size(); m++) {
                        player.addHandCard(cardList.get(m));
                        buHuaInfo.newCard.add(cardList.get(m));
                    }
                }
                player.addHandCardTo(buHuaInfo.handCard);
                buHuaInfo.playerUid = player.getUid();
                info.buHuaInfo.add(buHuaInfo);
                time += 0.4 * buHuaInfo.huaNum + 1.5;
            }
        } while (sendCnt > 0);
        Logs.ROOM.debug("%s  通知花牌信息 ", info);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BU_HUA_INFO, info);

        FZMJMahjongRoom self = this;
        DelayAction delayAction = new DelayAction(this, time * 1000);
        delayAction.setCallback(new ICallback<Object>() {
            @Override
            public void call(Object... o) {
                self.doStartTake();
            }
        });
        this.addAction(delayAction);
    }

    // 发送摸牌补花
    protected void doSendBeginMyBuHua(IMahjongPlayer player, byte card) {
        Logs.ROOM.debug(" 通知开始补花 ");
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_BU_HUA, null);
        this.myBuHuaInfo(player, card);
    }

    // 摸牌补花信息
    protected void myBuHuaInfo(IMahjongPlayer player, byte card) {
        PCLIMahjongNtfBuHuaInfo info = new PCLIMahjongNtfBuHuaInfo();

        int sendCnt;
        byte card1 = 0;
        do {
            sendCnt = 0;
            List<Byte> cardList = new ArrayList<Byte>();
            PCLIMahjongNtfBuHuaInfo.BuHuaInfo buHuaInfo = new PCLIMahjongNtfBuHuaInfo.BuHuaInfo();
            ((FZMJMahjongPlayer) player).addHuaCard(card);
            buHuaInfo.huaCard.add(card);
            buHuaInfo.huaNum++;
            player.takeCard(card);
            player.delAllHandCard(card);
            card1 = this.allCard.removeFirst(); // 拿到的牌
            if (card1 >= MahjongUtil.MJ_D_FENG) {
                ++sendCnt;
                card = card1;
            }
            player.addHandCard(card1);
            buHuaInfo.newCard.add(card1);
            player.addHandCardTo(buHuaInfo.handCard);
            buHuaInfo.playerUid = player.getUid();
            info.buHuaInfo.add(buHuaInfo);
        } while (sendCnt > 0);
        Logs.ROOM.debug("%s  通知花牌信息 ", info);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BU_HUA_INFO, info);

        MahjongWaitAction waitAction = null;
        if (this.isMustHu()) {
            waitAction = this.getWaitActionWithOnlyHu(player, card1);
        }
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            MahjongTakeAction action = this.getTakeAction(player, card1);
            this.addAction(action);
        }

    }

    protected MahjongWaitAction getWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu) {
        MahjongWaitAction waitAction = null;
        int canEatIndex = (player.getIndex() + 1) % this.playerNum;
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid() || otherPlayer.isOver()) {
                continue;
            }

            boolean hu = this.isHu(otherPlayer, player.getUid(), takeCard) && this.laiZiCard != takeCard && ((FZMJMahjongPlayer) player).hasLaiZiCard(this.laiZiCard);
            boolean bar = onlyHu ? false : this.isBar(otherPlayer, takeCard, true) && this.laiZiCard != takeCard;
            boolean bump = onlyHu ? false : bar || this.isBump(otherPlayer, takeCard) && this.laiZiCard != takeCard;
            boolean eat = onlyHu ? false : (i != canEatIndex ? false : this.isEat(otherPlayer, takeCard)) && this.laiZiCard != takeCard;
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

    private static boolean isJingQue(byte[] card, byte laiziCard) {
        // 刻字
        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (card[i] >= 3) {

                card[i] -= 3;
                if (isJingQue(card, laiziCard)) {
                    return true;
                }
                card[i] += 3;
            }
        }
        // 顺子
        for (int i = 0; i < 25; ++i) {
            if (i % 9 < 7 && card[i + 1] >= 1 && card[i + 2] >= 1 && card[i + 3] >= 1) {

                card[i + 1]--;
                card[i + 2]--;
                card[i + 3]--;
                if (isJingQue(card, laiziCard)) {
                    return true;
                }
                card[i + 1]++;
                card[i + 2]++;
                card[i + 3]++;
            }
        }
        //如果剩一对
        if (card.length == 2 && card[0] == card[1] && card[0] == laiziCard) {
            return true;
        }
        return false;
    }

}


