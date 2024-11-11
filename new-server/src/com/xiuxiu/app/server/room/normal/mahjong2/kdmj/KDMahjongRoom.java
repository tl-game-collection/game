package com.xiuxiu.app.server.room.normal.mahjong2.kdmj;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanOperateInfoByYYMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanTingInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByKDMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfFumbleInfoYYMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByKDMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfLaiPiInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfTingInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithKDMJ;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong2.BaseMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;
import com.xiuxiu.app.server.room.normal.mahjong2.EBarType;
import com.xiuxiu.app.server.room.normal.mahjong2.EPaiXing;
import com.xiuxiu.app.server.room.normal.mahjong2.EShowFlag;
import com.xiuxiu.app.server.room.normal.mahjong2.HuInfo;
import com.xiuxiu.app.server.room.normal.mahjong2.MahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.TingInfo;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTingAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongFeatures;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHelper;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHu;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.KDMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.MahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.KDMJMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.KDMJResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.RecordMahjongRoomBriefInfo;
import com.xiuxiu.app.server.room.record.mahjong2.ResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.TingRecordAction;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.RandomUtil;

/**
 * 扣点麻将
 */
@GameInfo(gameType = GameType.GAME_TYPE_LYKD)
public class KDMahjongRoom extends MahjongRoom {
    private long features;
    private byte fanPai = -1; // 翻出的牌
    private int playType = 0; // 0,常规玩法、1，风耗子、2，不带风、3，随机耗子
    private int jiaFan = 0;
    private static final int QING_YI_SE = 0x0001; // 清一色
    private static final int YI_TIAO_LONG = 0x0002; // 一条龙
    private static final int QI_DUI = 0x0004; // 七对
    private static final int HAO_HUA_QI_DUI = 0x0008; // 豪华七对

    private boolean isYHBH = false;
    private boolean isJZHZNZK = false;
    private boolean isJZH60F = false;
    private boolean isBHBG = false;
    private boolean isGHZNZK = false;
    
    private boolean specialShunzi = false;

    public KDMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public KDMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.playType = this.getRule().getOrDefault(RoomRule.RR_MJ_KD_PLAY_TYPE, 0);
        this.jiaFan = this.getRule().getOrDefault(RoomRule.RR_MJ_KD_JIA_FAN, 0);
        this.isYHBH = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKDMJPlayRule.YHBH.getValue());
        this.isJZHZNZK =
            0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKDMJPlayRule.JZHZNZK.getValue());
        this.isJZH60F = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKDMJPlayRule.JZH60F.getValue());
        this.isBHBG = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKDMJPlayRule.BHBG.getValue());
        this.isGHZNZK = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKDMJPlayRule.GHZNZK.getValue());
        this.detectionIP =
            0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKDMJPlayRule.RR_DETECTION_IP.getValue());
        boolean isTen = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKDMJPlayRule.TEN.getValue());
        this.endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
        if (isTen) {
            this.timeout = 10 * 1000;
        }
        this.specialShunzi= 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKDMJPlayRule.FZZ.getValue());
        // 默认包含序数牌和字牌
        this.features = MahjongFeatures.WITH_TONG_ZI | MahjongFeatures.WITH_TIAO_ZI | MahjongFeatures.WITH_WAN_ZI
            | MahjongFeatures.WITH_FENG | MahjongFeatures.WITH_JIAN;

        if (2 == this.playType) { // 勾选不带风 去掉风牌
            this.features ^= MahjongFeatures.WITH_FENG;
            this.features ^= MahjongFeatures.WITH_JIAN;

        }
        this.features |=
            MahjongFeatures.ENABLE_PENG | MahjongFeatures.ENABLE_MING_GANG | MahjongFeatures.ENABLE_AN_GANG;
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
        if(this.laiZiCard != -1) {
            // 通知赖子和皮子信息
            PCLIMahjongNtfLaiPiInfo ntfLaiPi = new PCLIMahjongNtfLaiPiInfo();
            ntfLaiPi.laiZi = this.laiZiCard;
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_LAI_PI, ntfLaiPi);
            ((RecordMahjongRoomBriefInfo)this.getRecord().getRoomInfo()).setLaiZiCard(this.laiZiCard);
        }
       
           
        // 开始打牌
        this.doStartTake();
    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithKDMJ info = new PCLIRoomNtfBeginInfoByMJWithKDMJ();
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
            info.d = Config.checkWhiteHas(player.getUid(), 1);
            info.laiZi = this.laiZiCard;
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithKDMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithKDMJ();
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
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        KDMJMahjongPlayer mjPlayer = (KDMJMahjongPlayer)player;
        mjPlayer.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        mjPlayer.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        mjPlayer.clearPaiXing();
        // 碰->杠->胡
//        if (!ziMo && EActionOp.BAR == this.curAction && EActionOp.BUMP == this.prevAction && this.curCard == this.prevCard) {
//            return false;
//        }
        // 漏胡不能胡
        if (!ziMo && player.isPassCard(EActionOp.HU, huCard)) {
            return false;
        }

        // 过胡只能自摸,添加限制：听牌后
        if (isGHZNZK && !ziMo && mjPlayer.isHasPassCard()) {
            return false;
        }
        
        if (!ziMo && this.isLaiZi(huCard)) {
            return false;
        }

        int meldCnt = player.getCPGNode().size(); // 碰/杠了多少句话
        int normalCPGCnt = 0; // 常规杠牌的次数（非皮非赖）
        // 检查手里剩余牌，丢到临时容器tempCards
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte)player.getHandCardCnt((byte)i);
        }
        for (CPGNode node : player.getCPGNode()) {
            tempCards.get()[node.getCard1()] += 1;
            if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                normalCPGCnt++;
            }
        }
         

        if (this.isJZHZNZK && this.laiZiCard!=-1 && (player.getHandCardCnt(this.laiZiCard) >= 1 || this.laiZiCard == huCard)) {
            
            if (ziMo) {
                boolean huNoEye = false;
                if (player.getHandCardCnt(huCard) >= 1 ){//&& huCard!=this.laiZiCard) {
                    player.delHandCard(huCard);
                    boolean hu = true;
                    for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                        byte takeCard = (byte) i;
                        player.addHandCard(takeCard);
                        if (!MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE,specialShunzi)) {
                            hu = false;
                        }
                        player.delHandCard(takeCard);
                        player.delHandCard(takeCard);
                        if (!hu) {
                            break;
                        }
                    }
                    huNoEye = hu;
                    player.addHandCard(huCard);
                }
//                } else {
//                    if (this.laiZiCard == huCard) {
////                        huNoEye = MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE);
//                        boolean hu = true;
//                        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
//                            byte takeCard = (byte)i;
//                            player.addHandCard(takeCard);
//                            if (!MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE)) {
//                                hu = false;
//                            }
//                            player.delHandCard(takeCard);
//                            if (!hu) {
//                                break;
//                            }
//                        }
//                        huNoEye = hu;
//                    } else {
//                        player.delHandCard(this.laiZiCard);
////                        huNoEye = MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE);
//                        boolean hu = true;
//                        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
//                            byte takeCard = (byte)i;
//                            player.addHandCard(takeCard);
//                            if (!MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE)) {
//                                hu = false;
//                            }
//                            player.delHandCard(takeCard);
//                            if (!hu) {
//                                break;
//                            }
//                        }
//                        huNoEye = hu;
//                        player.addHandCard(this.laiZiCard);
//                    }
//                }
                
                if (huNoEye && isJZH60F) {
                    player.addPaiXing(EPaiXing.KDMJ_JIAN_ZI_HU);
                }
            } else {
                // 见字胡不能点炮
                if (EActionOp.BAR != this.curAction) {
                    boolean huNoEye = false;
                    if (player.getHandCardCnt(huCard) >= 1 && huCard!=this.laiZiCard) {
                        player.delHandCard(huCard);
                        boolean tempFlag =false;
                        if(player.getHandCardCnt(this.laiZiCard) >= 1) {
                            player.delHandCard(this.laiZiCard);
                            tempFlag = true;
                        }
                        huNoEye = MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE, specialShunzi);
        
                        player.addHandCard(huCard);
                        if(tempFlag) {
                            player.addHandCard(this.laiZiCard);
                        }
                    } else {
                        if (this.laiZiCard == huCard) {
                            huNoEye = MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE, specialShunzi);
                        } else {
                            if(player.getHandCardCnt(this.laiZiCard) >= 1) {
                                player.delHandCard(this.laiZiCard);
                                
                                huNoEye = MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE, specialShunzi);
                
                                player.addHandCard(this.laiZiCard);
                            }
                        }
                    }

                    if (huNoEye) {
                        player.addPaiXing(EPaiXing.KDMJ_JIAN_ZI_HU);
                        return true;
                    }
                }
            }
            
        }
        long hu = 0;
        // 七对
        if (MahjongUtil.isSevenPair(player.getHandCardRaw(), this.laiZiCard)) {
            hu |= MahjongHu.QI_DUI;
            player.addPaiXing(EPaiXing.KDMJ_QI_DUI);

            // 豪华
            ((KDMJMahjongPlayer)player).setHao(this.countOfHaoHua(player));
            if (((KDMJMahjongPlayer)player).getHao() > 0) {
                player.addPaiXing(EPaiXing.KDMJ_HAO_HUA);
            }
        }

        if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE,specialShunzi)) {
            // 清一色
            if (MahjongUtil.isQingYiSe(tempCards.get(), this.laiZiCard)) {
                hu |= MahjongHu.QING_YI_SE;
                player.addPaiXing(EPaiXing.KDMJ_QING_YI_SE);
            }
            // 风一色
            if (MahjongUtil.isFengYiSe(tempCards.get(), this.laiZiCard)) {
                hu |= MahjongHu.FENG_YI_SE;
                player.addPaiXing(EPaiXing.KDMJ_FENG_YI_SE);
            }
        }

        if (player.getHandCardCnt() - normalCPGCnt * 3 >= 11
            && MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE,specialShunzi)) {
            // 一条龙
            byte suitedCard = -1;
            byte[] startCards = new byte[] {MahjongUtil.MJ_1_TIAO, MahjongUtil.MJ_1_WANG, MahjongUtil.MJ_1_TONG};
            for (int i = 0; suitedCard < 0 && i < startCards.length; i++) {
                suitedCard = startCards[i];
                for (byte j = 0; j < 9; j++) {
                    if (!player.hasHandCard((byte)(suitedCard + j), 1)) {
                        suitedCard = -1;
                        break;
                    }
                }
            }
            if (suitedCard >= 0) {
//                if (MahjongUtil.isHu(player.getHandCardRaw(), meldCnt + 3)) {
                if(MahjongUtil.isHu(player.getHandCardRaw(), meldCnt + 3, true, (byte)-1, Integer.MAX_VALUE,specialShunzi)) {
                    hu |= MahjongHu.QING_LONG;
                    player.addPaiXing(EPaiXing.KDMJ_YI_TIAO_LONG);
                }
            }
        }

        if (MahjongUtil.isHuWithSpecialShunzi(player.getHandCardRaw(), normalCPGCnt, this.laiZiCard,specialShunzi)) {
            // 屁胡
            hu |= MahjongHu.PI_HU;
            player.addPaiXing(EPaiXing.KDMJ_NORMAL);
        }

        if (hu != 0) {
            int points = 0;
            if (ziMo) {
                hu |= MahjongHu.ZI_MO;
                points = this.calcPoints(hu, player);
            } else {
                points = this.calcPoints(hu, player);
                if (this.curAction == EActionOp.BAR) {
                    Logs.ROOM.debug("== isHu 抢杠胡");
                    hu = MahjongHu.QIANG_GANG_HU;
                    player.clearPaiXing();
                    player.addPaiXing(EPaiXing.KDMJ_QIANG_GANG_HU);
                }

            }

            player.setScore(Score.MJ_CUR_FANG_SCORE, points, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, 1, false);
        } else {
            player.clearPaiXing();
            player.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        }

        return hu != 0;
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3,
        byte huCard) {
        if (takePlayer.getUid() != player1.getUid()){
            player1.addHandCard(huCard);
        }
        this.isHu(player1,takePlayer.getUid(),takePlayer.getUid() == player1.getUid(),huCard);
        super.onHu(takePlayer, player1, player2, player3, huCard);
        this.calcBarScores(takePlayer,huCard);
        this.calcHuScore(takePlayer, player1, huCard);
        List<EPaiXing> allPaiXing = player1.getAllPaiXing();
        if (allPaiXing.isEmpty()) {
            player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), EPaiXing.KDMJ_NORMAL,
                huCard);
        } else {
            for (EPaiXing px : allPaiXing) {
                player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), px, huCard);
            }
        }

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[i];
            if (player == null || player.isGuest() || player1.getUid() == player.getUid()) {
                continue;
            }
            // 算输赢分-牌型
//            int score = this.getScore(
//                player.getScore(Score.MJ_CUR_HU_SCORE, false) + player.getScore(Score.MJ_CUR_GANG_SCORE, false));
//         
            int score =   (player.getScore(Score.MJ_CUR_HU_SCORE, false) + player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            System.out.println(player.getScore(Score.MJ_CUR_HU_SCORE, false) + "---"
                + player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            Logs.ROOM.debug("=== onHu player:：%d，MJ_CUR_HU_SCORE：%d", player.getUid(), score);
            player.addScore(Score.SCORE, -score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, -score, true);
            player1.addScore(Score.SCORE, score, false);
            player1.addScore(Score.ACC_TOTAL_SCORE, score, true);
        }

        // 实际竞技值计算，并抽水
        this.getRoomHandle().calculateGold();

        KDMJResultRecordAction resultRecordAction =
            (KDMJResultRecordAction)((MahjongRecord)this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            KDMJResultRecordAction.PlayerInfo playerInfo = new KDMJResultRecordAction.PlayerInfo();

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
            KDMJResultRecordAction.ScoreInfo scoreInfo = new KDMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setHuScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
           
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

    private void calcBarScore(IMahjongPlayer player, int points) {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer tempPlayer = (IMahjongPlayer)this.allPlayer[i];
            if (tempPlayer == null || tempPlayer.isGuest()) {
                continue;
            }
            if (tempPlayer.getUid() == player.getUid()) {
                // 赢分
                tempPlayer.addScore(Score.MJ_CUR_GANG_SCORE, -(points * (getPlayerCnt() - 1)), false);
            } else {
                // 输分
                tempPlayer.addScore(Score.MJ_CUR_GANG_SCORE, points, false);
            }
        }
    }

    /**
     * 计算玩家杠相关的输赢分
     * 
     * @param takePlayer
     * @param huCard
     */
    private void calcBarScores(IMahjongPlayer takePlayer,byte huCard) {
        long ignoreTakePlayerId = -1;
        int endScoce =this.endPoint;
      
        if (isBHBG && !takePlayer.isHasTing()) {
            ignoreTakePlayerId = takePlayer.getUid();
        }
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer tempPlayer = (IMahjongPlayer)this.allPlayer[i];
            if (tempPlayer == null || tempPlayer.isGuest()) {
                continue;
            }
            if (tempPlayer.getUid() == ignoreTakePlayerId) {
                continue;
            }
            List<CPGNode> cpgNodes = tempPlayer.getCPGNode();
            for (int m = 0,n = cpgNodes.size(); m < n; m++) {
                CPGNode node = cpgNodes.get(m);
                if (m == n - 1 && node.getCard1() == huCard) {
                    continue;
                }
                if (CPGNode.EType.BAR_AN == node.getType()) {
                    // 暗杠
                    int points = 2 * getCardPoint(node.getCard1()) * endScoce;
                    calcBarScore(tempPlayer, points);
                } else if (CPGNode.EType.BAR_MING == node.getType()) {
                    // 明杠
                    int points = getCardPoint(node.getCard1()) * endScoce;
                    calcBarScore(tempPlayer, points);
                } else if (CPGNode.EType.BAR_FANG == node.getType()) {
                    boolean isTing = node.isTing();
                    if (!isTing) {
                        // 放杠
                        int points = getCardPoint(node.getCard1()) * (getPlayerCnt() - 1) * endScoce;
                        // 输分
                        IMahjongPlayer tempTakePlayer = (IMahjongPlayer)this.allPlayer[node.getTakePlayerIndex()];
                        if (tempTakePlayer != null) {
                            tempTakePlayer.addScore(Score.MJ_CUR_GANG_SCORE, points, false);
                        }
                        // 赢分
                        tempPlayer.addScore(Score.MJ_CUR_GANG_SCORE, -points, false);
                    } else {
                        int points = getCardPoint(node.getCard1()) * endScoce;
                        calcBarScore(tempPlayer, points);
                    }
                }
            }
        }
        if (ignoreTakePlayerId != -1) {
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer)this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest()) {
                    continue;
                }
                if (otherPlayer.getUid() == ignoreTakePlayerId) {
                    continue;
                }
                int score = otherPlayer.getScore(Score.MJ_CUR_GANG_SCORE, false);
                if (score > 0) {
                    takePlayer.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
                    otherPlayer.setScore(Score.MJ_CUR_GANG_SCORE, 0, false);
                }
            }
        }
    }

    @Override
    public void onHuangZhuang(boolean next) {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            player.setScore(Score.SCORE, 0, false);
            player.setScore(Score.MJ_CUR_GANG_SCORE, 0, false);

            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);
        }

        // 实际竞技值计算，并抽水
        this.getRoomHandle().calculateGold();

        KDMJResultRecordAction resultRecordAction =
            (KDMJResultRecordAction)((MahjongRecord)this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }

            KDMJResultRecordAction.PlayerInfo playerInfo = new KDMJResultRecordAction.PlayerInfo();
            KDMJResultRecordAction.ScoreInfo scoreInfo = new KDMJResultRecordAction.ScoreInfo();
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
        PCLIMahjongNtfGameOverInfoByKDMJ info = new PCLIMahjongNtfGameOverInfoByKDMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer)this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByKDMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByKDMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByKDMJ.HuInfo temp = new PCLIMahjongNtfGameOverInfoByKDMJ.HuInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByKDMJ.ScoreInfo();
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            playerInfo.score.huScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE, false));//
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByKDMJ.FinalResult();
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
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer)this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }

        PCLIMahjongNtfDeskInfoByKDMJ deskInfo = new PCLIMahjongNtfDeskInfoByKDMJ();
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
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction)this.action.peek()).getRemain();
        if (!action.isEmpty()) {
            BaseAction baseAction = (BaseAction)action.peek();
        }

        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
                for (int i = 0; i < this.playerNum; ++i) {
                    IMahjongPlayer other = (IMahjongPlayer)this.allPlayer[i];
                    if (null == other || other.isGuest()) {
                        continue;
                    }
                    deskInfo.allOnlineState.put(other.getUid(), !other.isOffline());
                    PCLIMahjongNtfDeskInfoByKDMJ.DeskPlayerInfo deskPlayerInfo =
                        new PCLIMahjongNtfDeskInfoByKDMJ.DeskPlayerInfo();
                    deskPlayerInfo.totalScore = this
                        .getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false)));//
                    deskPlayerInfo.remainCard = other.getHandCardCnt();
                    deskPlayerInfo.fumbleCnt = other.getFumbleCnt();
                    deskPlayerInfo.ting = other.isHasTing();
                    deskPlayerInfo.tingCardValue = ((MahjongPlayer)other).getTingCardValue();
                    deskPlayerInfo.tingCardIndex = ((MahjongPlayer)other).getTingCardIndex();
                    deskPlayerInfo.desktingIndex = ((MahjongPlayer)other).getDesktingIndex();
                    deskPlayerInfo.tingInfo = ((MahjongPlayer)other).getTingInfo().getTing();
                    deskPlayerInfo.lastFumbleCard=((MahjongPlayer)other).getLastFumbleCard();
                  

                    for (CPGNode node : other.getCPGNode()) {
                        PCLIMahjongNtfDeskInfoByKDMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByKDMJ.CardNode();
                        cardNode.type = node.getType().ordinal();
                        if (node.getTakePlayerIndex() >= 0) {
                            cardNode.playerId = this.allPlayer[node.getTakePlayerIndex()].getUid();
                        }
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
                    other.addDeskCardTo(deskPlayerInfo.deskCard);
                    if (player.getUid() == other.getUid()) {
                        other.addHandCardTo(deskPlayerInfo.card, deskPlayerInfo.fumble);
                    }
                    other.addHuCardTo(deskPlayerInfo.huCard);
                    deskInfo.other.put(other.getUid(), deskPlayerInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (this.isPiOrLaiZi(takeCard)) {
            if (player.hasHandCard(takeCard, 4)) {// 癞子暗杠
                return true;
            }
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
        return false;
    }

    @Override
    protected EPaiXing getPaiXing(IMahjongPlayer player) {
        return null;
    }

    @Override
    protected int getFang(IMahjongPlayer player) {
        return this.isHu(player) ? 1 : 0;
    }

    @Override
    protected int getFang(IMahjongPlayer player, byte addCard) {
        if (!MahjongHelper.isCardEnabledByFeatures(addCard, this.features)) {
            return 0;
        }
        return super.getFang(player, addCard);
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    public IRoomPlayer createPlayer() {
        return new KDMJMahjongPlayer(this.getGameType(), this.getRoomUid(), this.getRoomId());
    }

    @Override
    public boolean isMoreHu() {
        return false;
    }

    @Override
    public Record getRecord() {
        if (this.record == null) {
            this.record = new KDMJMahjongRecord(this);
        }
        return this.record;

    }

    /**
     * 杠分不飘
     */
    @Override
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return false;
    }

    @Override
    public void clear() {
        super.clear();

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

        assert laiZiCnt >= 0 : "laiZiCnt value error";

        count += laiZiCnt / 2;
        return Math.min(count, 3);
    }

        // 胡的番数
    private int calcPoints(long hu, IMahjongPlayer player) {
        int points = 0;
        // 加翻清一色
        if (isQingYiSe()) {
            if ((hu & MahjongHu.QING_YI_SE) != 0) {
                points += (hu & MahjongHu.QING_YI_SE) != 0 ? 1 : 0;
            }
            if ((hu & MahjongHu.FENG_YI_SE) != 0) {
                points += (hu & MahjongHu.FENG_YI_SE) != 0 ? 1 : 0;
            }
        }

        // 加翻一条龙
        if (isYiTiaoLong()) {
            if ((hu & MahjongHu.QING_LONG) != 0) {
                points += (hu & MahjongHu.QING_LONG) != 0 ? 1 : 0;
            }
        }
        // 加翻七对
        if (isQiDui()) {
            if ((hu & MahjongHu.QI_DUI) != 0) {
                if (player.getAllPaiXing().contains(EPaiXing.KDMJ_QI_DUI)) {
                    points += (hu & MahjongHu.QI_DUI) != 0 ? 1 : 0;
                } ;
            }
        }
        // 加翻豪华七对
        if (isHaoHuaQiDui()) {
            if ((hu & MahjongHu.QI_DUI) != 0) {
                if (player.getAllPaiXing().contains(EPaiXing.KDMJ_HAO_HUA)) {
                    points += (hu & MahjongHu.QI_DUI) != 0 ? 1 : 0;
                }
            }

        }
        // 自摸
        if (!player.hasPaiXing(EPaiXing.KDMJ_JIAN_ZI_HU)){
            points += (hu & MahjongHu.ZI_MO) != 0 ? 1 : 0;
        }
        Logs.ROOM.debug("=== points: %d, calcPoints(%x)", points, hu);
        return points;
    }

    // 计算胡牌分数
    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1, byte hucard) {
        int cardPoint = 0;
        int endsorce = this.endPoint;
        if (hucard == laiZiCard) {
            Set<Byte> tingCards = player1.getTingInfo().getHuCard(player1.getLastTakeCard());
            tingCards.remove(this.laiZiCard);
            for (byte temp : tingCards) {
                int tempPoint = getCardPoint(temp);
                if (tempPoint > cardPoint) {
                    cardPoint = tempPoint;
                }
            }
            int normalCPGCnt = 0;
            // 检查手里剩余牌，丢到临时容器tempCards
            for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                tempCards.get()[i] = (byte)player1.getHandCardCnt((byte)i);
            }
            for (CPGNode node : player1.getCPGNode()) {
                tempCards.get()[node.getCard1()] += 1;
                if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                    normalCPGCnt++;
                }
            }
            int laiCardSize =  player1.getHandCardRaw()[this.laiZiCard];
            player1.addHandCard(this.laiZiCard);
            if (MahjongUtil.isHu(player1.getHandCardRaw(),normalCPGCnt,true,this.laiZiCard,laiCardSize,specialShunzi)){
                if (getCardPoint(this.laiZiCard) > cardPoint){
                    cardPoint = getCardPoint(this.laiZiCard);
                }
            }
            player1.delHandCard(this.laiZiCard);
        } else {
            cardPoint = getCardPoint(hucard);
        }
        // 计算输家分数
        int fang =player1.getScore(Score.MJ_CUR_FANG_SCORE, false);
        for (IRoomPlayer p : this.allPlayer) {
            if (p == null || p.isGuest() || p.getUid() == player1.getUid()) {
                continue;
            }
            int otherPoints;
            KDMJMahjongPlayer otherPlayer = (KDMJMahjongPlayer)p;
            if (player1.hasPaiXing(EPaiXing.KDMJ_JIAN_ZI_HU)) {
                otherPoints = (int)(20 * Math.pow(2, fang))* endsorce  ;//
            } else {
                otherPoints = (int)(cardPoint * Math.pow(2, fang))*endsorce ;
            }

            Logs.ROOM.debug("\t玩家：%d", otherPlayer.getUid());

            Logs.ROOM.debug("\t\totherPoints:%d, points:%d", otherPoints, fang);

            otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, otherPoints, false);
            player1.setScore(Score.MJ_CUR_HU_SCORE, -otherPoints*(getPlayerCnt() - 1), false);
            Logs.ROOM.debug("\t\t最终分数：%d", otherPoints);
        }

        if (isBHBG && !takePlayer.isHasTing()) {
            for (IRoomPlayer p : this.allPlayer) {
                if (p == null || p.isGuest() || p.getUid() == takePlayer.getUid()) {
                    continue;
                }

                int score = p.getScore(Score.MJ_CUR_HU_SCORE, false);
                if (score > 0) {

                    takePlayer.setScore(Score.MJ_CUR_HU_SCORE, score * (getPlayerCnt() - 1), false);
                    p.setScore(Score.MJ_CUR_HU_SCORE, 0, false);
                }
            }
        }
    }

    // 获取胡点数 风算10，其他算本身点数
    public int getCardPoint(byte huCard) {
        if (huCard <= 27) {
            int cardValue = huCard % 9;
            return cardValue == 0 ? 9 : cardValue;
        }
        return 10;
    }

    // 获取牌值
    public byte getCardValue(byte huCard) {

        if ((huCard % 9) == 0) {
            return 9;
        }
        return (byte)(huCard % 9);
    }

    // 是否勾选清一色翻倍
    private boolean isQingYiSe() {
        return (this.jiaFan & QING_YI_SE) != 0;
    }

    // 是否勾选一条龙翻倍
    private boolean isYiTiaoLong() {
        return (this.jiaFan & YI_TIAO_LONG) != 0;
    }

    // 是否勾选七对翻倍
    private boolean isQiDui() {
        return (this.jiaFan & QI_DUI) != 0;
    }

    // 是否勾选豪华七对翻倍
    private boolean isHaoHuaQiDui() {
        return (this.jiaFan & HAO_HUA_QI_DUI) != 0;
    }

    // 是否勾选豪华七对翻倍
    private boolean isTureHu() {
        return (this.jiaFan & HAO_HUA_QI_DUI) != 0;
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
        IMahjongPlayer tingPlayer = (IMahjongPlayer)this.getRoomPlayer(player.getUid());

        boolean ting = (boolean)param[5];
        IAction iaction = this.action.peek();
        if (ting) {
            this.action.remove(iaction);
            MahjongTingAction tingAction = new MahjongTingAction(this, tingPlayer, timeout);
            this.addAction(tingAction);

        }
        IAction action = this.action.peek();

        if (action instanceof MahjongTingAction) {
            MahjongTingAction tingAction = (MahjongTingAction)action;
            if (player.getUid() != tingAction.getPlayer().getUid()) {
                Logs.ROOM.warn("%s 听牌的人:%s 当前轮打牌人是:%s 不是你, 无效听牌", this, player, tingAction.getPlayer().getUid());
                return ErrorCode.ROOM_NOT_CUR_PLAYER;
            }
            byte card = (byte)param[0];
            if (card <= 0 || card >= MahjongUtil.MJ_CARD_KINDS) {
                Logs.ROOM.warn("%s 听牌的人:%s 非法牌:%d, 无效听牌", this, player, card);
                return ErrorCode.ROOM_MJ_INVALID_CARD;
            }
            MahjongPlayer mahjongPlayer = (MahjongPlayer)tingAction.getPlayer();
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
        byte card = (byte)param[0];
        takePlayer.clearPassCard();
        takePlayer.takeCard(card);
        ++this.deskCard[card];

        if (!this.isOverBanker && this.bankerIndex != takePlayer.getIndex()) {
            this.isOverBanker = true;
        }

        Logs.ROOM.debug("%s 听牌的人:%s 打牌 card:%s auto:%s param:%s", this, takePlayer, MahjongUtil.getCardStr(card), auto,
            Arrays.toString(param));

        this.setCurOp(takePlayer, EActionOp.TING, card);
        this.doSendTing(takePlayer, auto, param);

        takePlayer.setHasTing(true);
        takePlayer.setManualTake(false);
        MahjongPlayer mahjongPlayer = (MahjongPlayer)takePlayer;

        // 记录打出去的牌、索引
        byte outputCardIndex = (byte)param[3];
        byte desktingIndex = (byte)param[6];

        mahjongPlayer.setTingCardValue(card);
        mahjongPlayer.setTingCardIndex(outputCardIndex);
        mahjongPlayer.setDesktingIndex(desktingIndex);

        MahjongWaitAction waitAction = this.getWaitAction(takePlayer, card, false, true, 2);
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

    private void doSendTing(IMahjongPlayer player, boolean auto, Object... param) {
        byte card = (byte)param[0];
        byte last = (byte)param[1];
        byte index = (byte)param[2];
        byte outputCardIndex = (byte)param[3];
        int length = (int)param[4];
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
            boolean isHu = this.isHu(mahjongPlayer);
            if (!mahjongPlayer.canManualTake() && !isHu) {
                Logs.ROOM.warn("%s 打牌的人:%s 不能手动打牌, 无法打牌", this, player);
                return ErrorCode.ROOM_MJ_ALREADY_LIANG_PAI;
            }
            if (isHu && mahjongPlayer.isHasTing() && this.isHu(mahjongPlayer,mahjongPlayer.getUid(),true,card)) {
                selectPass(player);
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
    public void doSendFumble(IMahjongPlayer player, byte card) {
        PCLIMahjongNtfFumbleInfoYYMJ info = new PCLIMahjongNtfFumbleInfoYYMJ();
        info.uid = player.getUid();
        info.index = player.getIndex();
        info.auto = true;
        info.value = card;
        info.remainCard = this.allCard.size();
        player.addHandCardTo(info.handCard, card);
        info.tingInfo.putAll(this.getTing(player));
        info.ting = ((MahjongPlayer)player).isHasTing();
        player.send(CommandId.CLI_NTF_MAHJONG_FUMBLE, info);

        info = new PCLIMahjongNtfFumbleInfoYYMJ();
        info.uid = player.getUid();
        info.index = player.getIndex();
        info.auto = true;
        info.remainCard = this.allCard.size();
        info.value = -1;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_FUMBLE, info, player.getUid());

        ((MahjongRecord)this.getRecord()).addFumbleRecordAction(player.getUid(), card);
    }

    @Override
    protected void doFumbleAfter(IMahjongPlayer player, byte card) {
        if (player.isHasTing()) {
            boolean hu = false, bar = false;
            if ((hu = !(card != this.laiZiCard && MahjongUtil.is12(card)) && this.isHu(player, player.getUid(), true, card)) || (bar = this.isBar1(player, card, false,3) == true)) {
                this.doSendCanOperate(player, hu, bar, false, false, card);
                super.doFumbleAfter(player, card);
            } else {
                MahjongRoom self = this;
                DelayAction delayAction = new DelayAction(this, 200);
                delayAction.setCallback(new ICallback<Object>() {
                    @Override
                    public void call(Object... o) {
                        self.onTake(player, true, card, (byte)0, (byte)0, player.getHandCardIndex(card), 0);
                    }
                });
                this.addAction(delayAction);
            }
        } else {
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
            } else {
                MahjongTakeAction action = this.getTakeAction(player, card);
                this.addAction(action);
            }
        }
    }

    @Override
    public boolean isMustHu() {
        return isYHBH;
    }

    @Override
    public void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat,
        byte takeCard) {
        MahjongPlayer mahjongPlayer = (MahjongPlayer)player;
        this.doSendCanOperate(player, hu, bar, bump, eat, mahjongPlayer.isHasTing(), takeCard);
    }

    private void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat,
        boolean ting, byte takeCard) {
        PCLIMahjongNtfCanOperateInfoByYYMJ info =
            new PCLIMahjongNtfCanOperateInfoByYYMJ(bump, bar, ting ? hu : false, eat, ting, takeCard);
        player.send(CommandId.CLI_NTF_MAHJONG_CAN_OPERATE, info);
    }

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
            if (!mahjongPlayer.isHasTing()) {
                Logs.ROOM.warn("%s 胡牌的人:%s 没听牌不能胡操作 ", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
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
            if (!takePlayer.isHasTing()) {
                Logs.ROOM.warn("%s 胡牌的人:%s 没听牌不能胡操作 ", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ((MahjongTakeAction)action).setOp(EActionOp.HU);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 胡牌的人:%s 本来不是胡牌动作, 无法胡牌", this, player);
        return ErrorCode.ROOM_ACTION_ERR;
    }

    protected MahjongWaitAction getWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu, int type) {
        return getWaitAction(player, takeCard, onlyHu, false, type);
    }

    /**
     * 检查等待
     * 
     * @param player
     * @param takeCard
     * @param onlyHu
     * @param flag
     * @param type
     * @return
     */
    protected MahjongWaitAction getWaitAction(IMahjongPlayer player, byte takeCard, boolean onlyHu, boolean flag, int type) {
        MahjongPlayer tempMahjongPlayer = (MahjongPlayer)player;
        boolean isTingCard = tempMahjongPlayer.isHasTing() && tempMahjongPlayer.getTingCardValue() == takeCard && flag;
        MahjongWaitAction waitAction = null;
        int canEatIndex = getNextRoomPlayer(player.getIndex()).getIndex();
        for (int i = 0; i < allPlayer.length; ++i) {
            IMahjongPlayer otherPlayer = (IMahjongPlayer)this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid()
                || otherPlayer.isOver()) {
                continue;
            }

            boolean hu = this.isHu(otherPlayer, player.getUid(), takeCard);
            boolean bar = onlyHu ? false : this.isBar1(otherPlayer, takeCard, true, type);
            boolean bump = onlyHu ? false : bar || this.isBump(otherPlayer, takeCard);
            boolean eat = onlyHu ? false : (i != canEatIndex ? false : this.isEat(otherPlayer, takeCard));
            MahjongPlayer otherMahjongPlayer = (MahjongPlayer)otherPlayer;
            if (!otherPlayer.isHasTing()) {
                if (hu) {
                    hu = false;
                }
            } else {
                if (hu) {
                    // 限制3、4、5不能点炮
                    if (MahjongUtil.is345(takeCard)) {
                        hu = false;
                    }
                    // 限制1、2不能点炮
                    if (MahjongUtil.is12(takeCard)) {
                        hu = false;
                    }
                    if (isGHZNZK && type == 1 && otherMahjongPlayer.isHasPassCard()) {
                        hu = false;
                    }
                }
            }
            // 听牌的那张牌值不能胡
            if (hu && isTingCard || (type == 1 && takeCard == this.laiZiCard)) {
                hu = false;
            }
            if (bar && isTingCard || (type == 1 && takeCard == this.laiZiCard)) {
                bar = false;
            }
            if (bump && isTingCard) {
                bump = false;
            }
            
            if (bump && otherMahjongPlayer.isHasTing() || (type == 1 && takeCard == this.laiZiCard)) {
                bump = false;
            }
            
            if (hu && otherMahjongPlayer.isHasTing() && (type == 1 && isJZHZNZK)) {
                int normalCPGCnt = 0;
                // 检查手里剩余牌，丢到临时容器tempCards
                for (int j = 0; j < MahjongUtil.MJ_CARD_KINDS; ++j) {
                    tempCards.get()[j] = (byte)otherMahjongPlayer.getHandCardCnt((byte)i);
                }
                for (CPGNode node : otherMahjongPlayer.getCPGNode()) {
                    tempCards.get()[node.getCard1()] += 1;
                    if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                        normalCPGCnt++;
                    }
                }
                boolean tempHu = true;
                for (int m = 1; m < MahjongUtil.MJ_CARD_KINDS; ++m) {
                    byte tempCard = (byte)m;
                    otherMahjongPlayer.addHandCard(tempCard);
                    if (!MahjongUtil.isHu(otherMahjongPlayer.getHandCardRaw(), normalCPGCnt, true, this.laiZiCard, Integer.MAX_VALUE,specialShunzi)) {
                        tempHu = false;
                    }
                    otherMahjongPlayer.delHandCard(tempCard);
                    if (!tempHu) {
                        break;
                    }
                }
                hu = !tempHu;
            }

            if (!hu && !bar && !bump) {
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
            waitInfo.setCloseYHBH(!isYHBH);
            if (null == waitAction) {
                waitAction = new MahjongWaitAction(this, player);
                waitAction.setTakeCard(takeCard);
            }
            waitAction.addWait(waitInfo);

            this.doSendCanOperate(otherPlayer, hu, bar, bump, eat, takeCard);
        }
        return waitAction;
    }

    /**
     * 能否产生听
     */
    @Override
    protected boolean canGenerateTingInfo(IMahjongPlayer player, byte card) {
        if (!player.isHasTing()) {
            if (MahjongUtil.is12(card) || MahjongUtil.is345(card)) {
                return false;
            }
        } else {
            if (MahjongUtil.is12(card)) {
                if (card == this.laiZiCard) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * 庄家摸第一张牌后处理, 开始打牌
     */
    @Override
    protected void doDealAfter() {
        if (3 == this.playType) {
            this.laiZiCard = (byte)RandomUtil.random(MahjongUtil.MJ_1_WANG, MahjongUtil.MJ_BAI_FENG); // 确定随机耗子
        } else if (1 == this.playType) {
            this.laiZiCard = (byte)RandomUtil.random(MahjongUtil.MJ_D_FENG, MahjongUtil.MJ_BAI_FENG); // 确定风耗子
        }
        this.doStartTake();
    }

    @Override
    protected void addTingInfoLaizhi(IMahjongPlayer player, TingInfo tingInfo, byte takeCard) {
        if ((playType == 1 || playType == 3) && laiZiCard != -1) {
            tingInfo.add(takeCard, laiZiCard, 1, this.getRemainCardCntByPlayer(player, laiZiCard));
        }
    }

    @Override
    public HashMap<Byte, HashMap<Byte, Integer>> getTing(IMahjongPlayer player) {
        HashMap<Byte, HashMap<Byte, Integer>> data = player.getTingInfo().getTing();
        Iterator iter = data.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            HashMap<Byte,Integer> huCardMap = (HashMap<Byte, Integer>) entry.getValue();
            if (huCardMap.size() == 1 && huCardMap.containsKey(this.laiZiCard)){
                int normalCPGCnt = 0;
                // 检查手里剩余牌，丢到临时容器tempCards
                for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                    tempCards.get()[i] = (byte)player.getHandCardCnt((byte)i);
                }
                for (CPGNode node : player.getCPGNode()) {
                    tempCards.get()[node.getCard1()] += 1;
                    if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                        normalCPGCnt++;
                    }
                }
                boolean flag= false;
                if (player.hasHandCard((Byte) entry.getKey(),1)){
                    player.delHandCard((Byte) entry.getKey());
                    flag= true;
                }
                int laiCardSize =  player.getHandCardRaw()[this.laiZiCard] ;
                player.addHandCard(this.laiZiCard);
                if (!MahjongUtil.isHu(player.getHandCardRaw(),normalCPGCnt,true,this.laiZiCard,laiCardSize,specialShunzi)){
                    huCardMap.clear();
                    iter.remove();
                }
                if (flag) {
                    player.addHandCard((Byte) entry.getKey());
                }
                player.delHandCard(this.laiZiCard);
            }
        }
        return data;
    }
}
