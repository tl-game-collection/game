package com.xiuxiu.app.server.room.normal.mahjong2.dymj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByDYMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskShowInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByDYMJ;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithDYMJ;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.player.mahjong2.IDYMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.DYMJMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.*;
import com.xiuxiu.app.server.table.TbWHMJStartHuManager;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class DYMJRoom extends MahjongRoom {
    protected static final int[][] TOP_VALUE = new int[][]{{4, 8}, {5, 10}, {6, 12}, {7, 14}};
    protected static final int TOP_TYPE_NORMAL = 0;
    protected static final int TOP_TYPE_JD = 1;
    protected static final int TOP_TYPE_YGD = 2;
    protected static final int TOP_TYPE_SYKT = 3;

    
    protected ArrayList<Byte> piList = new ArrayList<>();   // 皮列表
    protected byte fangCard = -1;           // 翻牌
    protected boolean isGK = false;         // 是否杠上开花
    protected int beginHuFang = 4;          // 起胡番数, 4
    protected int top = 5;                  // 封顶 5,6
    protected int bagType = 2;              // 包类型, 0: 反包, 1: 陪包, 2: 不包

    protected boolean isGold = false;       // 连金, 反金
    protected boolean isJFYLF = false;      // 见风原癞翻
    protected boolean isJ258F = false;      // 见258翻
    protected boolean isBaoZiF = false;     // 豹子翻
    protected boolean isTuiGold = false;    // 推金
    protected boolean isTwo = false;        // 2人
    protected boolean isSmallGold = false;  // 小金顶
    protected boolean isPiOrLaiBump = false;// 皮子和癞子必须摸牌

    protected int prevGoldIndex = -1;       // 上一把金顶玩家索引
    protected List<Byte> haiDiLaoCard = new ArrayList<>();  // 海底捞摸的牌
    protected long haiDiLaoStarPlayerUid = -1;              // 海底捞开始摸牌的玩家uid
    private HashMap<Long, Long> allDeskShowInfo = new HashMap<>();   // 牌桌显示

    private List<EPaiXing> tempPaiXing = new ArrayList<>();     // 临时牌型记录

    public DYMJRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public DYMJRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.fumbleWithBarOnFrontend = false;
        this.beginHuFang = this.getRule().getOrDefault(RoomRule.RR_MJ_DY_BEGIN_HU_FANG,4);
        this.top = this.getRule().getOrDefault(RoomRule.RR_MJ_TOP,5);
        if (this.top < 5 || this.top > 6) {
            this.top = 5;
        }
        this.bagType = this.getRule().getOrDefault(RoomRule.RR_MJ_DY_BAG_TYPE, 0);
        this.isGold = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & DYMJPlayRule.GOLD.getValue());
        this.isJFYLF = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & DYMJPlayRule.JFYLF.getValue());
        this.isJ258F = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & DYMJPlayRule.J258F.getValue());
        this.isBaoZiF = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & DYMJPlayRule.BAOZIF.getValue());
        this.isTuiGold = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & DYMJPlayRule.TUI_GOLD.getValue());
        this.isSmallGold = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & DYMJPlayRule.SMALL_GOLD.getValue());
        this.isPiOrLaiBump = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & DYMJPlayRule.PI_LAI_BUMP.getValue());
        this.detectionIP = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & DYMJPlayRule.RR_DETECTION_IP.getValue());
        this.timeout = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & 0x01) ? 20000 : -1;
    }

    @Override
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB) {
            this.allCard.addAll(CardLibraryManager.I.getMahjongCard());
            return;
        }
        this.isTwo = 2 == this.curPlayerCnt;
        for (int i = 0; i < 4; ++i) {
            if (!this.isTwo) {
                for (int j = MahjongUtil.MJ_1_WANG; j <= MahjongUtil.MJ_9_WANG; ++j) {
                    this.allCard.add((byte) j);
                }
            }
            for (int j = MahjongUtil.MJ_1_TIAO; j <= MahjongUtil.MJ_9_TIAO; ++j) {
                this.allCard.add((byte) j);
            }
            for (int j = MahjongUtil.MJ_1_TONG; j <= MahjongUtil.MJ_9_TONG; ++j) {
                this.allCard.add((byte) j);
            }
            // 风
            for (int j = MahjongUtil.MJ_D_FENG; j <= MahjongUtil.MJ_BAI_FENG; ++j) {
                this.allCard.add((byte) j);
            }
        }
        ShuffleUtil.shuffle(this.allCard);
//        byte[] cards ={10,10,10,11,12,13,14,34,34,19,20,19,20,11,11,11,12,13,14,15,34,34,20,21,20,21,1,2,3,4,5,6,7,33,33,10,11,12,13,21,3,34,32,33,34,32,33,34};
//        for (Byte i : cards){
//            this.allCard.add(i);
//        }

    }

    @Override
    protected void doStart1() {
        this.doStartTake();
    }

    @Override
    public void onBump(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {

        super.onBump(takePlayer, player, param);
        this.updateCPFBao();
    }

    @Override
    public void onBar(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        super.onBar(takePlayer, player, param);
        this.updateCPFBao();
    }

    @Override
    public void onEat(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        super.onEat(takePlayer, player, param);
        this.updateCPFBao();
    }

    private void updateCPFBao() {
        boolean diff = false;
        for (int i = 0; i < this.playerNum; ++i) {
            IDYMJMahjongPlayer tempI = (IDYMJMahjongPlayer) this.allPlayer[i];
            if (null == tempI || tempI.isGuest()) {
                continue;
            }
            long oldValue = this.allDeskShowInfo.getOrDefault(tempI.getUid(), 0L);
            long newValue = 0;
            int fang = this.getHuFang(tempI);
            boolean isTop = true;
            for (int j = 0; j < this.playerNum; ++j) {
                IDYMJMahjongPlayer tempJ = (IDYMJMahjongPlayer) this.allPlayer[j];
                if (null == tempJ || tempJ.isGuest() || i == j) {
                    continue;
                }
                int totalFang = fang + this.getHuFang(tempJ);
                if (!this.isTwo && (this.bankerIndex == tempI.getIndex() || this.bankerIndex == tempJ.getIndex())) {
                    ++totalFang;
                }
                if (totalFang < this.top) {
                    isTop = false;
                }
                if (tempJ.is258()) {
                    boolean is258 = false;
                    if (tempJ.getChengBagPlayerIndex() == tempI.getIndex()) {
                        newValue |= 1 << EDeskShowType.CHENGBAO_258.ordinal();
                        is258 = true;
                    }
                    List<Integer> _258Index = tempJ.getAllFangBagPlayerIndexWithJYS();
                    if (0 == this.bagType) {
                        // 反包
                        if (is258 && _258Index.size() > 0) {
                            // 只显示反包
                            newValue &= ~(1 << EDeskShowType.CHENGBAO_258.ordinal());
                        }
                        if (_258Index.size() > 0 && tempI.getIndex() == _258Index.get(_258Index.size() - 1)) {
                            newValue |= 1 << EDeskShowType.FAN_BAO_258.ordinal();
                        }
                    } else if (1 == this.bagType) {
                        // 陪包
                        for (Integer idx : _258Index) {
                            if (idx == tempI.getIndex()) {
                                // 只显示承包
                                if (!is258) {
                                    newValue |= 1 << EDeskShowType.PEI_BAO_258.ordinal();
                                }
                                break;
                            }
                        }
                    }
                }
                if (tempJ.isQYS()) {
                    boolean isQys = false;
                    if (tempJ.getChengBagPlayerIndex() == tempI.getIndex()) {
                        if (MahjongUtil.COLOR_WANG == tempJ.getChengColor()) {
                            newValue |= 1 << EDeskShowType.CHENGBAO_WANG.ordinal();
                            isQys = true;
                        } else if (MahjongUtil.COLOR_TIAO == tempJ.getChengColor()) {
                            newValue |= 1 << EDeskShowType.CHENGBAO_TIAO.ordinal();
                            isQys = true;
                        } else if (MahjongUtil.COLOR_TONG == tempJ.getChengColor()) {
                            newValue |= 1 << EDeskShowType.CHENGBAO_TONG.ordinal();
                            isQys = true;
                        }
                    }
                    List<Integer> qysIndex = tempJ.getAllFangBagPlayerIndexWithQYS();
                    if (0 == this.bagType) {
                        // 反包
                        if (isQys && qysIndex.size() > 0) {
                            if (MahjongUtil.COLOR_WANG == tempJ.getChengColor()) {
                                newValue &= ~(1 << EDeskShowType.CHENGBAO_258.ordinal());
                            } else if (MahjongUtil.COLOR_TIAO == tempJ.getChengColor()) {
                                newValue &= ~(1 << EDeskShowType.CHENGBAO_258.ordinal());
                            } else if (MahjongUtil.COLOR_TONG == tempJ.getChengColor()) {
                                newValue &= ~(1 << EDeskShowType.CHENGBAO_258.ordinal());
                            }
                        }
                        if (qysIndex.size() > 0 && tempI.getIndex() == qysIndex.get(qysIndex.size() - 1)) {
                            if (MahjongUtil.COLOR_WANG == tempJ.getChengColor()) {
                                newValue |= 1 << EDeskShowType.FAN_BAO_WANG.ordinal();
                            } else if (MahjongUtil.COLOR_TIAO == tempJ.getChengColor()) {
                                newValue |= 1 << EDeskShowType.FAN_BAO_TIAO.ordinal();
                            } else if (MahjongUtil.COLOR_TONG == tempJ.getChengColor()) {
                                newValue |= 1 << EDeskShowType.FAN_BAO_TONG.ordinal();
                            }
                        }
                    } else if (1 == this.bagType) {
                        // 陪包
                        for (Integer idx : qysIndex) {
                            if (idx == tempI.getIndex()) {
                                if (MahjongUtil.COLOR_WANG == tempJ.getChengColor()) {
                                    if (!isQys) {
                                        newValue |= 1 << EDeskShowType.PEI_BAO_WANG.ordinal();
                                    }
                                } else if (MahjongUtil.COLOR_TIAO == tempJ.getChengColor()) {
                                    if (!isQys) {
                                        newValue |= 1 << EDeskShowType.PEI_BAO_TIAO.ordinal();
                                    }
                                } else if (MahjongUtil.COLOR_TONG == tempJ.getChengColor()) {
                                    if (!isQys) {
                                        newValue |= 1 << EDeskShowType.PEI_BAO_TONG.ordinal();
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (this.isTuiGold && isTop) {
                if (0 == ((oldValue >> EDeskShowType.TUI_JIN.ordinal()) & 0x01)) {
                    for (int j = 0; j < this.playerNum; ++j) {
                        IDYMJMahjongPlayer tempJ = (IDYMJMahjongPlayer) this.allPlayer[j];
                        if (null == tempJ || tempJ.isGuest() || i == j) {
                            continue;
                        }
                        tempJ.addPassCard(EActionOp.TUI_GOLD, (byte) -1);
                    }
                }
                newValue |= 1 << EDeskShowType.TUI_JIN.ordinal();
            }
            if (oldValue != newValue) {
                diff = true;
            }
            this.allDeskShowInfo.put(tempI.getUid(), newValue);
        }
        if (diff) {
            this.syncChengInfo();
        }
    }

    private void syncChengInfo() {
        DeskShowRecordAction action = ((MahjongRecord) this.getRecord()).addDeskShowRecordAction();
        action.addAllShow(this.allDeskShowInfo);

        PCLIMahjongNtfDeskShowInfo info = new PCLIMahjongNtfDeskShowInfo();
        info.allShow.putAll(this.allDeskShowInfo);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_DESK_SHOW_INFO, info);
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        if (!ziMo && this.isLaiZi(huCard)) {
            return false;
        }
        // 碰->杠->胡
         if (!ziMo && EActionOp.BAR == this.curAction && EActionOp.BUMP == this.prevAction && this.curCard == this.prevCard) {
            return false;
        }
        player.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        player.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        player.clearPaiXing();
        ((IDYMJMahjongPlayer) player).setFangTop(false);
        ((IDYMJMahjongPlayer) player).setSmallGold(false);
        if (this.hasPiCard(player)) {
            // 有皮不能胡
            return false;
        }
        if (player.getScore(Score.MJ_CUR_KAI_KOU_CNT, false) < 1) {
            // 没有开口不能胡
            return false;
        }
        int eatCnt = 0;
        int anBarCnt = 0;
        int noPiAndLaiZiBarCnt = 0;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        List<CPGNode> cpgNodes = player.getCPGNode();
        for (CPGNode node : cpgNodes) {
            if (CPGNode.EType.ANY_THREE == node.getType()) {
                ++noPiAndLaiZiBarCnt;
                continue;
            }
            tempCards.get()[node.getCard1()] += 1;
            if (node.isEat()) {
                ++eatCnt;
            }
            if (CPGNode.EType.BAR_AN == node.getType()) {
                ++anBarCnt;
            }
            if (CPGNode.EType.BAR_PI != node.getType() && CPGNode.EType.BAR_LAIZI != node.getType()) {
                ++noPiAndLaiZiBarCnt;
            }
        }
        boolean hu = false;
        boolean isBigHu = false;
        boolean smallGold = this.isSmallGold;
        boolean isNeed258Eye = true;
        boolean isFangTop = true;
        int fang = 0;
        int huCnt = 0;
        do {
            boolean isQYS = false;
            boolean is258 = false;
            boolean isFYS = false;
            boolean isQQR = false;
            boolean isPPH = false;
            // 2. 将一色
            if (MahjongUtil.isEyeYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                isBigHu = true;
                isNeed258Eye = false;
                is258 = true;
                ++huCnt;
                player.addPaiXing(EPaiXing.DYMJ_JIANG_YI_SE);
            }
            // 3. 风一色
            if (MahjongUtil.isFengYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                isBigHu = true;
                isNeed258Eye = false;
                isFYS = true;
                ++huCnt;
                player.addPaiXing(EPaiXing.DYMJ_FENG_YIS_SE);
            }
            // 0. 无七对
            if (!isBigHu && !MahjongUtil.isHu(player.getHandCardRaw(), noPiAndLaiZiBarCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
                break;
            }
            // 1. 清一色
            if (MahjongUtil.isQingYiSe(tempCards.get(), this.laiZiCard, this.piList)) {
                isBigHu = true;
                ++huCnt;
                isNeed258Eye = false;
                isQYS = true;
                player.addPaiXing(EPaiXing.DYMJ_QING_YI_SE);
            }
            // 4. 全球人 需要2,5,8将, 不能暗杠, 不能自摸
            if (!ziMo && 0 == anBarCnt && 2 == player.getHandCardCnt() && (MahjongUtil.is258(huCard, this.laiZiCard) && MahjongUtil.is258(player.getLastHandCard(), this.laiZiCard))) {
                isBigHu = true;
                isNeed258Eye = false;
                isQQR = true;
                ++huCnt;
                player.addPaiXing(EPaiXing.DYMJ_QUAN_QIU_REN);
            }

            // 5. 碰碰胡
            if (0 == eatCnt && MahjongUtil.isPengPengHu(player.getHandCardRaw(), this.laiZiCard)) {
                // 见字胡不能点炮
//                if (!ziMo && EActionOp.BAR != this.curAction && player.getHandCardCnt(this.laiZiCard) >= 1) {
//                    // 0. 点炮
//                    // 1. 有癞子才能胡见字胡
//                    byte delCard = -1;
//                    if (huCard == this.laiZiCard) {
//                        delCard = player.delHandCardNoCard(this.laiZiCard);
//                        if (-1 == delCard) {
//                            player.delHandCard(huCard);
//                            delCard = huCard;
//                        }
//                    } else {
//                        player.delHandCard(huCard);
//                        delCard = huCard;
//                    }
//                    player.delHandCard(this.laiZiCard);
//                    boolean huNoEye = MahjongUtil.isPengPengHuNoEye(player.getHandCardRaw(), this.laiZiCard);
//                    player.addHandCard(delCard);
//                    player.addHandCard(this.laiZiCard);
//                    if (huNoEye && !isQYS && !is258 && !isFYS && !isQQR) {
//                        // 1. 清一色可以点炮
//                        // 2. 将一色可以点炮
//                        // 3. 风一色可以点炮
//                        // 4. 全球人可以点炮
//                        break;
//                    }
//                }
                isBigHu = true;
                ++huCnt;
                isNeed258Eye = false;
                isPPH = true;
                player.addPaiXing(EPaiXing.DYMJ_PENG_PENG_HU);
            }
            //杠上开花
            if (this.isGK) {
                isBigHu = true;
                player.addPaiXing(EPaiXing.DYMJ_GANG_KAI);
                
                ++huCnt;
            } else {
                if (EActionOp.BAR == this.curAction || (EActionOp.BAR == this.prevAction && ziMo)) {
                    if (!isBigHu && !MahjongUtil.isHu258Eye(player.getHandCardRaw(), noPiAndLaiZiBarCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
                        break;
                    }
                    isBigHu = true;
                    if (ziMo) {
                        player.addPaiXing(EPaiXing.DYMJ_GANG_KAI);
                    } else {
                        player.addPaiXing(EPaiXing.DYMJ_QIANG_GANG_HU);
                    }
                    ++huCnt;
                }
            }

            // 6. 2个癞子以上需要大胡
            if (!isBigHu && player.getHandCardCnt(this.laiZiCard) >= 2) {
                break;
            }
            // 7. 小胡需要2,5,8将
            if (isNeed258Eye && !MahjongUtil.isHu258Eye(player.getHandCardRaw(), noPiAndLaiZiBarCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
                break;
            }else{
                if( !isBigHu) {
                    player.addPaiXing(EPaiXing.DYMJ_NORMAL);
                }
            }
            if (this.isTwo) {
                if (this.allCard.size() <= (1 == this.fumbleCntWithBar % 2 ? 11 : 12)) {
                    player.addPaiXing(EPaiXing.DYMJ_HAI_DI_LAO);
                    ++huCnt;
                }
            } else {
                if (this.allCard.size() <= (1 == this.fumbleCntWithBar % 2 ? 13 : 14)) {
                    player.addPaiXing(EPaiXing.DYMJ_HAI_DI_LAO);
                    ++huCnt;
                }
            }
            fang = this.getHuFang(player);
            if (MahjongUtil.isHu(player.getHandCardRaw(), noPiAndLaiZiBarCnt, false)) {
                // 硬胡
                ++fang;
                player.addPaiXing(EPaiXing.DYMJ_YING);
            }
            if (ziMo) {
                ++fang;
            }
            
         
            hu = true;
            int cutNum=0;
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                
                int temp = fang + huCnt+ this.getHuFang(otherPlayer);
                if (!isBigHu && !ziMo && otherPlayer.getUid() != takePlayerUid) {
                    temp = 0;
                    continue;
                }
                if (!ziMo && otherPlayer.getUid() == takePlayerUid) {
                    ++temp;
                }
//                int startHuNum =TbWHMJStartHuManager.I.getStartHu(this.beginHuFang, huCnt, ziMo, ziMo ? true : otherPlayer.getUid() == takePlayerUid);
                int startHuNum=beginHuFang;
                if (!isBigHu) {
                    if (temp < startHuNum) {
                        hu = false;
                        break;
                    }
                }else {
                    if (temp < startHuNum) {
                        cutNum++;
                        
                        if(cutNum == (this.playerNum -1) ){
                            hu = false;
                            break;
                        }
                        continue;
                    }
                }
                
                if (temp > startHuNum) {
                    smallGold = false;
                }
                if (temp < this.top) {
                    isFangTop = false;
                }
                if (!ziMo && player.isPassCard(EActionOp.HU, huCard) && temp < this.top) {
                    hu = false;
                    break;
                }
            }
        } while (false);

        if (hu) {
            player.setScore(Score.MJ_CUR_FANG_SCORE, fang, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, isBigHu ? 1 : 2, false);
            ((IDYMJMahjongPlayer) player).setSmallGold(smallGold);
            ((IDYMJMahjongPlayer) player).setFangTop(isFangTop);
        } else {
            player.clearPaiXing();
            ((IDYMJMahjongPlayer) player).setFangTop(false);
            ((IDYMJMahjongPlayer) player).setSmallGold(false);
        }

        return hu;
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
    public void onTake(IMahjongPlayer takePlayer, boolean auto, Object... param) {
        byte card = (byte) param[0];
        if(isPiOrLaiBump&&(this.isLaiZi(card)||this.isPi(card))){
            super.onBar(takePlayer,takePlayer,param);
        }else{
            super.onTake(takePlayer, auto, param);
            if (this.isLaiZi(card)) {
                takePlayer.addScore(Score.MJ_CUR_TAKE_LAIZI_CNT, 1, false);
            } else if (this.isPi(card)) {
                takePlayer.addScore(Score.MJ_CUR_TAKE_PI_CNT, 1, false);
            }
        }
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest() || takePlayer.getUid() == otherPlayer.getUid()) {
                continue;
            }
            ((IDYMJMahjongPlayer) otherPlayer).addBag(takePlayer, card, this.laiZiCard);
        }
        this.updateCPFBao();
    }

    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player1,boolean isGK) {
        // 小胡= 基础分(底分) * 2^番数, 其中番数=
        // 大胡= 基础分(底分) * 2^番数 * (自摸/点炮) * 15 / 10, 其中番数=
        int huType = player1.getScore(Score.MJ_CUR_HU_TYPE, false);
        int fang = player1.getScore(Score.MJ_CUR_FANG_SCORE, false);
        int curTop = TOP_VALUE[0][this.top -5];
        boolean isAllTop = true;
        int fangTop = this.top + 1;

        if (isGK || 1 == huType) {
            // 大胡
            int cnt = player1.getAllPaiXing().size();
            if (player1.hasPaiXing(EPaiXing.DYMJ_NORMAL)){
                --cnt;
            }
            if (player1.hasPaiXing(EPaiXing.DYMJ_YING)) {
                --cnt;
            }
//            if (player1.hasPaiXing(EPaiXing.DYMJ_HAI_DI_LAO)) {
//                --cnt;
//            }
            //int bigHuScore = (int) (Math.pow(2, cnt));
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                int takeFang = this.getHuFang(otherPlayer);
                if (takePlayer.getUid() != player1.getUid()) {
                    // 点炮
                    if (takePlayer.getUid() == otherPlayer.getUid()) {
                        ++takeFang;
                    }
                }
                int tempFang = fang + takeFang + cnt;
                if (tempFang >= fangTop) {
                    tempFang = fangTop;
                }
                int temp;
                if(tempFang-3>0 || tempFang-3==0) {
                	temp=(int) Math.pow(2, (tempFang-3));	
				}else {
					temp=0;
				}
                if (!isGK && (takePlayer.getUid() == player1.getUid() || takePlayer.getUid() == otherPlayer.getUid())) {
                    temp = (int) (temp);
                }
                if (temp >= curTop) {
                    temp = curTop;
                    ((IDYMJMahjongPlayer) otherPlayer).setTopType(TOP_TYPE_NORMAL);
                } else {
                    isAllTop = false;
                }
                otherPlayer.addScore(Score.MJ_CUR_HU_SCORE, temp, false);
            }
        } else if (2 == huType) {
            // 小胡
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                int otherFang = this.getHuFang(otherPlayer);
                if (!this.isTwo) {
                    if ((this.bankerIndex == player1.getIndex() || this.bankerIndex == otherPlayer.getIndex())) {
                        // 庄家
                        //++otherFang;
                    }
                }
                
                if (takePlayer.getUid() != player1.getUid()) {
                    // 点炮
                    if (takePlayer.getUid() == otherPlayer.getUid()) {
                        ++otherFang;
                    } 
                }
                
                int tempFang = fang + otherFang;
                if (tempFang >= fangTop) {
                    tempFang = fangTop;
                }
                int temp;
                if(tempFang-3>0 || tempFang-3==0) {
                	temp=(int) Math.pow(2, (tempFang-3));	
				}else {
					temp=0;
				}
                
                if (temp >= curTop) {
                    temp = curTop;
                    if (takePlayer.getUid() != player1.getUid()) {
                        ((IDYMJMahjongPlayer) takePlayer).setTopType(TOP_TYPE_NORMAL);
                    }else {
                        ((IDYMJMahjongPlayer) otherPlayer).setTopType(TOP_TYPE_NORMAL);
                    }
                } else {
                    isAllTop = false;
                }

                if (takePlayer.getUid() != player1.getUid()) {
                    // 点炮
                    //otherPlayer.addScore(Score.MJ_CUR_HU_SCORE, 0, false);
                    if (takePlayer.getUid() != otherPlayer.getUid()) {
                        otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, 0, false);
                        continue;
                    }else{
                        otherPlayer.addScore(Score.MJ_CUR_HU_SCORE, temp, false);
                        continue;
                    }
                }
                otherPlayer.addScore(Score.MJ_CUR_HU_SCORE, temp, false);
            }
        }
        do {
            if (player1.hasPaiXing(EPaiXing.DYMJ_FENG_YIS_SE)) {
                isAllTop = true;
            }
            if (!isAllTop) {
                ((IDYMJMahjongPlayer) player1).setGold(false);
                break;
            }
            if (takePlayer.getUid() != player1.getUid() && 2 == huType) {
                ((IDYMJMahjongPlayer) player1).setGold(false);
                break;
            }
            ((IDYMJMahjongPlayer) player1).setGold(true);
            // 金顶
            boolean isAllUnKaiKou = true;
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                if (0 == otherPlayer.getScore(Score.MJ_CUR_KAI_KOU_CNT, false)) {
               
                    ((IDYMJMahjongPlayer) otherPlayer).setTopType(TOP_TYPE_YGD);
                } 
                else 
                {
                    isAllUnKaiKou = false;
                    ((IDYMJMahjongPlayer) otherPlayer).setTopType(TOP_TYPE_JD);
                }
            }

            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                if (!this.isTwo && isAllUnKaiKou) {
                    ((IDYMJMahjongPlayer) otherPlayer).setTopType(TOP_TYPE_SYKT);
                }
                int topType = ((IDYMJMahjongPlayer) otherPlayer).getTopType();
                if (topType > TOP_TYPE_NORMAL) {
                	otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, TOP_VALUE[topType][this.top - 5], false);
                }
            }
                  
        } while (false);

        int endValue = 1;//this.getScore(1);
        int eEndValue = -1;
        boolean lianGold = false;
        if (this.isBaoZiF) {
            // 豹子翻倍
            if (this.crap1 == this.crap2) {
                endValue = 2;
            }
        }
        if (this.isJFYLF) {
            // 见风原癞翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                endValue = 2;
            }
        }
        if (this.isJ258F) {
            // 见258将翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.is258(this.laiZiCard)) {
                endValue = 2;
            }
        }
        if (this.isGold) {
            // 连金
            if (((IDYMJMahjongPlayer) player1).isGold()) {
                if (((IDYMJMahjongPlayer) player1).isPrevGold()) {
                    lianGold = true;
                } else if (-1 != this.prevGoldIndex) {
                    eEndValue = this.prevGoldIndex;
                }
            }
        }
        int smallGoldMul = 1;
        // 2人没有小金顶
        if (!this.isTwo && ((IDYMJMahjongPlayer) player1).isSmallGold()) {
            int p1 = RandomUtil.random(1, 6);
            int p2 = RandomUtil.random(1, 6);
            ((IDYMJMahjongPlayer) player1).setSmallGoldPoint(p1, p2);
            smallGoldMul = p1 + p2;
            player1.addShowFlag(EShowFlag.DYMJ_XIAO_JIN_DING);
        }

        int ep = this.getScore(1);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
            if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                continue;
            }
            int topValue = ((IDYMJMahjongPlayer) otherPlayer).getTopType();
            if (TOP_TYPE_NORMAL == topValue) {
                otherPlayer.addShowFlag(EShowFlag.DYMJ_FENG_DING);
            } else if (TOP_TYPE_JD == topValue) {
                otherPlayer.addShowFlag(EShowFlag. DYMJ_JJIN_DING);
            } else if (TOP_TYPE_YGD == topValue) {
                otherPlayer.addShowFlag(EShowFlag.DYMJ_YANG_GUANG_DING);
            } else if (TOP_TYPE_SYKT == topValue) {           
                otherPlayer.addShowFlag(EShowFlag. DYMJ_SAN_YANG_KAI_TAI);
            }
            int temp = otherPlayer.getScore(Score.MJ_CUR_HU_SCORE, false);
            if (lianGold && !this.isTwo) {
                otherPlayer.addShowFlag(EShowFlag. DYMJ_LIAN_JIN);
                temp += TOP_VALUE[TOP_TYPE_JD][this.top - 5];
            } else if (otherPlayer.getIndex() == eEndValue) {
                otherPlayer.addShowFlag(EShowFlag.DYMJ_FAN_JIN);
                int topType = ((IDYMJMahjongPlayer) otherPlayer).getTopType();
                temp += TOP_VALUE[topType][this.top - 5];
            }
            temp = temp * endValue * smallGoldMul * ep;

            temp /= 100;
            otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, temp, false);
        }
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);
        this.calcHuScore(takePlayer, player1, player1.hasPaiXing(EPaiXing.WHMJ_GANG_KAI));
        boolean hasBag = false;
        do {
            if (this.isTwo) {
                break;
            }
            if (player1.hasPaiXing(EPaiXing.DYMJ_QING_YI_SE)) {
                // 清一色
                if (player1.getScore(Score.MJ_CUR_KAI_KOU_CNT, false) >= 3) {
                    hasBag = true;
                    int chengBaoPlayerIndex = ((IDYMJMahjongPlayer) player1).getChengBagPlayerIndex();
                    IMahjongPlayer chengBagPlayer = (IMahjongPlayer) this.getRoomPlayer(chengBaoPlayerIndex);
                    chengBagPlayer.addShowFlag(EShowFlag.DYMJ_CHENG_BAO);
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
            if (player1.hasPaiXing(EPaiXing.DYMJ_JIANG_YI_SE)) {
                // 将一色
                if (player1.getScore(Score.MJ_CUR_KAI_KOU_CNT, false) >= 3) {
                    hasBag = true;
                    int chengBaoPlayerIndex = ((IDYMJMahjongPlayer) player1).getChengBagPlayerIndex();
                    IMahjongPlayer chengBagPlayer = (IMahjongPlayer) this.getRoomPlayer(chengBaoPlayerIndex);
                    chengBagPlayer.addShowFlag(EShowFlag.DYMJ_CHENG_BAO);
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
            //全球人
            if (player1.hasPaiXing(EPaiXing.DYMJ_QUAN_QIU_REN)) {
            	if(takePlayer.getScore(Score.MJ_CUR_KAI_KOU_CNT,false)==0) {
            	    hasBag = true;
            	    takePlayer.addShowFlag(EShowFlag.DYMJ_CHENG_BAO);
            		for (int i = 0; i < this.playerNum; ++i) {
                        IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                        if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid() || takePlayer.getUid() == otherPlayer.getUid()) {
                            continue;
                        }
                        takePlayer.addScore(Score.MJ_CUR_HU_SCORE, otherPlayer.getScore(Score.MJ_CUR_HU_SCORE, false), false);
                        otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, 0, false);
                    }
            	}
            	break;
            }
        } while (false);

        // 推金
        if (!this.isTwo && !hasBag && this.isTuiGold) {
            if (((IDYMJMahjongPlayer) player1).isFangTop()) {
                int temp = 0;
                int piCnt = 0;
                for (int i = 0; i < this.playerNum; ++i) {
                    IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                    if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                        continue;
                    }
                    if (otherPlayer.isPass(EActionOp.TUI_GOLD)) {
                        continue;
                    }
                    for (Byte pi : this.piList) {
                        if (otherPlayer.hasHandCard(pi, 1)) {
                            temp |= (1 << otherPlayer.getIndex());
                            ++piCnt;
                            break;
                        }
                    }
                }
                if (piCnt > 0) {
                    for (int i = 0; i < this.playerNum; ++i) {
                        IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                        if (null == otherPlayer || otherPlayer.isGuest() || player1.getUid() == otherPlayer.getUid()) {
                            continue;
                        }
                        otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, 0, false);
                    }
                
                }
            }
        }

        // TODO 牌型
        List<EPaiXing> allPaiXing = player1.getAllPaiXing();
        if (allPaiXing.isEmpty()) {
            player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), EPaiXing.DYMJ_NORMAL, huCard);
        } else {
            for (EPaiXing px : allPaiXing) {
                player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), px, huCard);
            }
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

        DYMJResultRecordAction resultRecordAction = (DYMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        resultRecordAction.addHaiDiLaoCard(this.haiDiLaoCard);
        resultRecordAction.setHaiDiLaoStartPlayerUid(this.haiDiLaoStarPlayerUid);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            DYMJResultRecordAction.PlayerInfo playerInfo = new DYMJResultRecordAction.PlayerInfo();

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
            DYMJResultRecordAction.ScoreInfo scoreInfo = new DYMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        if (((IDYMJMahjongPlayer) player1).isGold()) {
            this.prevGoldIndex = player1.getIndex();
        } else {
            this.prevGoldIndex = -1;
        }
        if (!hasBag && this.bankerIndex != player1.getIndex()) {
            this.bankerIndex = (this.bankerIndex + 1) % this.playerNum;
        }
        this.getRoomHandle().calculateGold();
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        this.isGK = true;
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[(this.curOpIndex + 1) % this.playerNum];
            if (null == player || player.isGuest()) {
                continue;
            }
           
            if (this.isTwo) {
                if (this.allCard.size() <= (1 == this.fumbleCntWithBar % 2 ? 11 : 12)) {
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
            } else {
                if (this.allCard.size() <= (1 == this.fumbleCntWithBar % 2 ? 13 : 14)) {
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
        }

        DYMJResultRecordAction resultRecordAction = (DYMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        resultRecordAction.addHaiDiLaoCard(this.haiDiLaoCard);
        resultRecordAction.setHaiDiLaoStartPlayerUid(this.haiDiLaoStarPlayerUid);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }

            DYMJResultRecordAction.PlayerInfo playerInfo = new DYMJResultRecordAction.PlayerInfo();

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
            DYMJResultRecordAction.ScoreInfo scoreInfo = new DYMJResultRecordAction.ScoreInfo();
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }
        this.getRoomHandle().calculateGold();
        this.prevGoldIndex = -1;
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new DYMJMahjongRecord(this);
            if (this.isBaoZiF) {
                // 豹子翻倍
                if (this.crap1 == this.crap2) {
                    ((DYMJMahjongRecord) this.record).setBaoZiF(true);
                }
            }
            if (this.isJFYLF) {
                // 见风原癞翻倍
                if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                    ((DYMJMahjongRecord) this.record).setJFYLF(true);
                }
            }
            if (this.isJ258F) {
                // 见258将翻倍
                if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.is258(this.laiZiCard)) {
                    ((DYMJMahjongRecord) this.record).setJ258F(true);
                }
            }
        }
        return this.record;
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (this.isPiOrLaiZi(takeCard)) {
            if (fangGang) {
                return false;
            }
            return player.hasHandCard(takeCard, 1);
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if (player.isPassCard(EActionOp.BUMP, takeCard)) {
            return false;
        }
        if (this.isPiOrLaiZi(takeCard)) {
            return false;
        }
        return super.isBump(player, takeCard);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi(takeCard)) {
            return false;
        }
        return super.isEat(player, takeCard);
    }

    @Override
    protected boolean isFrontendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi((byte) (takeCard - 1)) || this.isPiOrLaiZi((byte) (takeCard - 2))) {
            return false;
        }
        return super.isFrontendEat(player, takeCard);
    }

    @Override
    protected boolean isMiddleEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi((byte) (takeCard - 1)) || this.isPiOrLaiZi((byte) (takeCard + 1))) {
            return false;
        }
        return super.isMiddleEat(player, takeCard);
    }

    @Override
    protected boolean isBackendEat(IMahjongPlayer player, byte takeCard) {
        if (this.isPiOrLaiZi((byte) (takeCard + 1)) || this.isPiOrLaiZi((byte) (takeCard + 2))) {
            return false;
        }
        return super.isBackendEat(player, takeCard);
    }

    protected boolean hasPiCard(IMahjongPlayer player) {
        for (Byte pi : this.piList) {
            if (-1 != pi && player.hasHandCard(pi, 1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMoreHu() {
        return false;
    }

    @Override
    public boolean isPi(byte card) {
        return -1 != this.piList.indexOf(card);
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
    protected boolean onCheckOver() {
        if (this.isTwo) {
            return this.allCard.size() <= (1 == this.fumbleCntWithBar % 2 ? 11 : 12);
        }
        return this.allCard.size() <= (1 == this.fumbleCntWithBar % 2 ? 13 : 14);
    }

    @Override
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return false;
    }

    /**
     * 获取胡番数
     *
     * @param player
     * @return
     */
    protected abstract int getHuFang(IMahjongPlayer player);

    @Override
    public IRoomPlayer createPlayer() {
        return new DYMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithDYMJ info = new PCLIRoomNtfBeginInfoByMJWithDYMJ();
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
            if (this.isBaoZiF) {
                // 豹子翻倍
                if (this.crap1 == this.crap2) {
                    info.isBaoZiF = true;
                }
            }
            if (this.isJFYLF) {
                // 见风原癞翻倍
                if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                    info.isJFYLF = true;
                }
            }
            if (this.isJ258F) {
                // 见258将翻倍
                if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.is258(this.laiZiCard)) {
                    info.isJ258F = true;
                }
            }
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithDYMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithDYMJ();
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
        if (this.isBaoZiF) {
            // 豹子翻倍
            if (this.crap1 == this.crap2) {
                roomBeginInfo.isBaoZiF = true;
            }
        }
        if (this.isJFYLF) {
            // 见风原癞翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                roomBeginInfo.isJFYLF = true;
            }
        }
        if (this.isJ258F) {
            // 见258将翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.is258(this.laiZiCard)) {
                roomBeginInfo.isJ258F = true;
            }
        }
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByDYMJ info = new PCLIMahjongNtfGameOverInfoByDYMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;
        if (this.isBaoZiF) {
            // 豹子翻倍
            if (this.crap1 == this.crap2) {
                info.isBaoZiF = true;
            }
        }
        if (this.isJFYLF) {
            // 见风原癞翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                info.isJFYLF = true;
            }
        }
        if (this.isJ258F) {
            // 见258将翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.is258(this.laiZiCard)) {
                info.isJ258F = true;
            }
        }
        info.haiDiLaoCard.addAll(this.haiDiLaoCard);
        info.haiDiLaoStartPlayerUid = this.haiDiLaoStarPlayerUid;
        for (int i = 0; i < this.playerNum; ++i) {
            IDYMJMahjongPlayer player = (IDYMJMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (player.isSmallGold()) {
                info.isSmallGold = true;
                info.smallGoldPoint1 = player.getSmallGoldPoint1();
                info.smallGoldPoint2 = player.getSmallGoldPoint2();
            }
            PCLIMahjongNtfGameOverInfoByDYMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByDYMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByDYMJ.HuInfo temp = new PCLIMahjongNtfGameOverInfoByDYMJ.HuInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByDYMJ.ScoreInfo();
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false)
                    + player.getScore(Score.MJ_CUR_EXTRA_GANG_SCORE, false)
                    + player.getScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, false)
                    + player.getScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE, false));
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByDYMJ.FinalResult();
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
    /**
     * 生成听信息
     * @param player
     */
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
                //两人不含万
                if (this.isTwo&& j <=9) {
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
    @Override
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIMahjongNtfDeskInfoByDYMJ deskInfo = new PCLIMahjongNtfDeskInfoByDYMJ();
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
        deskInfo.allShow.putAll(this.allDeskShowInfo);
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();
        if (this.isBaoZiF) {
            // 豹子翻倍
            if (this.crap1 == this.crap2) {
                deskInfo.isBaoZiF = true;
            }
        }
        if (this.isJFYLF) {
            // 见风原癞翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.COLOR_FENG == MahjongUtil.getColor(this.laiZiCard)) {
                deskInfo.isJFYLF = true;
            }
        }
        if (this.isJ258F) {
            // 见258将翻倍
            if (this.laiZiCard == this.prevLaiZiCard || MahjongUtil.is258(this.laiZiCard)) {
                deskInfo.isJ258F = true;
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
                PCLIMahjongNtfDeskInfoByDYMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByDYMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByDYMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByDYMJ.CardNode();
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
    public void clear() {
        super.clear();
        this.piList.clear();
        this.fangCard = -1;
        this.haiDiLaoStarPlayerUid = -1;
        this.haiDiLaoCard.clear();
        this.tempPaiXing.clear();
        this.allDeskShowInfo.clear();
        this.isGK = false;
    }

    @Override
    public void clearByArenaOver() {
        super.clearByArenaOver();
        this.prevGoldIndex = -1;
    }
}