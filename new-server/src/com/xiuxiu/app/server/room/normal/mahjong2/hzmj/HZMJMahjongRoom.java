package com.xiuxiu.app.server.room.normal.mahjong2.hzmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByHZMJ;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.bird.IMahjongBird;
import com.xiuxiu.app.server.room.player.mahjong2.HZMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IHZMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.HZMJMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.HZMJResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.ResultRecordAction;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@GameInfo(gameType = GameType.GAME_TYPE_HZMJ)
public class HZMJMahjongRoom extends MahjongRoom {
    protected int bankerType = 0;                               // 庄类型
    protected int hongZhongNum = 4;                             // 红中数量
    protected int niaoScore = 1;                                // 1鸟分
    protected int niaoType = 0;                                 // 转鸟类型
    protected int huType = 2;                                   // 胡牌类型

    protected boolean seven = false;                            // 胡七对
    protected boolean noneZhongMul = false;                     // 无红中翻倍
    protected boolean bankerInc = false;                        // 庄家加1
    protected boolean mustHu = false;                           // 必胡
    protected boolean start4HZHu = false;                       // 起手4红中可胡
    protected boolean piaoScore = false;                        // 飘分
    protected boolean louPeng = false;                          // 漏碰
    protected boolean moreHu = false;                           // 一炮多响
    protected boolean wypn = false;                             // 围一票鸟
    protected boolean lessTong = false;                         // 缺筒
    protected boolean qdPphThQysInc = false;                    // 七对,碰碰胡,天湖,清一色+1
    protected boolean dianPao = false;                          // 点炮
    protected boolean hzUnDianPao = false;                      // 有红中不能点炮
    protected boolean dianPao2 = false;                         // 点炮2分
    protected boolean qgh = false;                              // 抢杠胡
    protected boolean hzUnQgh = false;                          // 有红中不能抢杠胡
    protected boolean qgUnBp = false;                           // 抢杠胡不包赔
    protected boolean allMissIsAllHit = false;                  // 全不中算全中
    protected boolean allHitMul = false;                        // 全中翻倍
    protected boolean noneHZAdd2 = false;                       // 无红中加2鸟

    protected List<Byte> niaoList = new ArrayList<>();// 鸟列表

    public HZMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public HZMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.bankerType = this.getRule().getOrDefault(RoomRule.RR_MJ_HZ_BANKER_TYPE, 0);
        this.hongZhongNum = this.getRule().getOrDefault(RoomRule.RR_MJ_HZ_ZHONG_NUM, 4);
        if (4 != this.hongZhongNum && 8 != this.hongZhongNum) {
            this.hongZhongNum = 4;
        }
        this.niaoScore = this.getRule().getOrDefault(RoomRule.RR_MJ_HZ_NIAO_SCORE, 0);
        this.niaoType = this.getRule().getOrDefault(RoomRule.RR_MJ_HZ_NIAO_TYPE, 0);
        this.huType = this.getRule().getOrDefault(RoomRule.RR_MJ_HZ_HU_TYPE, 2);

        this.seven = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.SEVEN.getValue());
        this.noneZhongMul = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.NONE_ZHONG_MUL.getValue());
        this.bankerInc = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.BANKER_INC.getValue());
        this.mustHu = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.HU_MUST.getValue());
        this.start4HZHu = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.START_4_HZ_HU.getValue());
        this.piaoScore = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.PIAO_SCORE.getValue());
        this.louPeng = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.LOU_PENG.getValue());
        this.moreHu = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.MORE_HU.getValue());
        this.wypn = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.WYPN.getValue());
        this.lessTong = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.TONG_LESS.getValue());
        this.qdPphThQysInc = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.QD_PPH_TH_QYS_INC.getValue());
        this.dianPao = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.DIAN_PAO.getValue());
        this.hzUnDianPao = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.HZ_UN_DIAN_PAO.getValue());
        this.dianPao2 = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.DIAN_PAO_2.getValue());
        this.qgh = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.QGH.getValue());
        this.hzUnQgh = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.HZ_UN_QGH.getValue());
        this.qgUnBp = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.QGH_UN_BP.getValue());
        this.allMissIsAllHit = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.ALL_MISS_IS_ALL_HIT.getValue());
        this.allHitMul = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.ALL_HIT_MUL.getValue());
        this.noneHZAdd2 = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EHZMJPlayRule.NONE_HZ_ADD_2_NIAO.getValue());
        if (this.niaoType == 5){
            this.huType = 1;
            this.allMissIsAllHit = false;
            this.allHitMul = false;
            this.noneHZAdd2 = false;
        }
    }

    @Override
    protected void doShuffle() {
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
            if (!this.lessTong) {
                for (int j = MahjongUtil.MJ_1_TONG; j <= MahjongUtil.MJ_9_TONG; ++j) {
                    this.allCard.add((byte) j);
                }
            }
            // 中
            this.allCard.add(MahjongUtil.MJ_Z_FENG);
            if (8 == this.hongZhongNum) {
                this.allCard.add(MahjongUtil.MJ_Z_FENG);
            }
        }

        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {
    }

    @Override
    protected void initBankerIndex() {
        if (1 == this.curBureau) {
            if (1 == this.bankerType) {
                // TODO 第一个不一定是第一个近来
                this.bankerIndex = 0;
            } else {
                // 随机
                this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            }
        }
    }

    @Override
    protected void doDealAfter() {
        super.doDealAfter();
        this.laiZiCard = MahjongUtil.MJ_Z_FENG;
    }

    @Override
    protected void doStart1() {
        if (this.piaoScore) {
            this.beginXuanPiao();
        } else {
            this.beginStartHu();
        }
    }

    @Override
    public void endXuanPiao() {
        super.endXuanPiao();
        this.beginStartHu();
    }

    @Override
    public void endStartHu(boolean has) {
        super.endStartHu(has);
        if (has) {
            for (int i = 0; i < this.playerNum; ++i) {
                IHZMJMahjongPlayer player = (IHZMJMahjongPlayer) this.getRoomPlayer((this.bankerIndex + i) % this.playerNum);
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (player.getHu()) {
                    this.onHu(player, player, null, null, player.getLastFumbleCard());
                    break;
                }
            }
        } else {
            this.doStartTake();
        }
    }
    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new HZMJMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (MahjongUtil.MJ_Z_FENG == takeCard) {
            return false;
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if (MahjongUtil.MJ_Z_FENG == takeCard) {
            return false;
        }
        if (this.louPeng && player.isPassCard(EActionOp.BUMP, takeCard)) {
            return false;
        }
        return super.isBump(player, takeCard);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        return false;
    }

    @Override
    public boolean isMoreHu() {
        return this.moreHu;
    }

    @Override
    public boolean isMustHu() {
        return this.mustHu;
    }

    @Override
    public boolean isStartHu(IMahjongPlayer player) {
        if (this.isStartTake) {
            return false;
        }
        if (!this.start4HZHu) {
            return false;
        }
        return player.hasHandCard(MahjongUtil.MJ_Z_FENG, 4);
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        if (!ziMo) {
            // 不是自摸
            if (EActionOp.BAR == this.curAction) {
                // 抢杠胡
                if (!this.qgh) {
                    // 不可抢杠胡
                    return false;
                }
                if (this.hzUnQgh) {
                    // 有红中不可抢杠胡
                    if (player.getHandCardCnt(MahjongUtil.MJ_Z_FENG) > (MahjongUtil.MJ_Z_FENG == huCard ? 1 : 0)) {
                        return false;
                    }
                }
            } else {
                // 点炮
                if (!this.dianPao) {
                    // 不能接跑
                    return false;
                }
                if (this.hzUnDianPao) {
                    // 有红中不可接跑
                    if (player.getHandCardCnt(MahjongUtil.MJ_Z_FENG) > (MahjongUtil.MJ_Z_FENG == huCard ? 1 : 0)) {
                        return false;
                    }
                }
            }
            if (player.isPass(EActionOp.HU)) {
                // 漏胡
                return false;
            }
            if(huCard==MahjongUtil.MJ_Z_FENG){
                return false;
            }
        }

        return MahjongUtil.isHu(player.getHandCardRaw(), player.getCPGNodeCnt(), this.seven, MahjongUtil.MJ_Z_FENG);
    }

    @Override
    protected EPaiXing getPaiXing(IMahjongPlayer player) {
        return null;
    }

    @Override
    protected int getFang(IMahjongPlayer player) {
        if (!this.isHu(player)) {
            return 0;
        }
        if (MahjongUtil.isPengPengHu(player.getHandCardRaw(), this.laiZiCard)) {
            return 2;
        }
        if (this.seven && MahjongUtil.isSevenPair(player.getHandCardRaw(), this.laiZiCard)) {
            return 2;
        }
        return 1;
    }

    protected void calcHuScore(IHZMJMahjongPlayer takePlayer, IHZMJMahjongPlayer player, boolean isZiMo, byte huCard, IMahjongBird bird) {
        if (!isZiMo) {
            if (EActionOp.BAR == this.prevAction) {
                // 抢杠胡
                do {
                    if (!this.qgh) {
                        // 不可抢杠胡
                        break;
                    }
                    if (this.hzUnQgh) {
                        // 有红中不可抢杠胡
                        if (player.getHandCardCnt(MahjongUtil.MJ_Z_FENG) > (MahjongUtil.MJ_Z_FENG == huCard ? 1 : 0)) {
                            break;
                        }
                    }
                    if(dianPao){
                        isZiMo=false;
                    }else{
                        isZiMo = true;
                    }
                } while (false);
            }
        }
        boolean isBigHu = false;
        boolean hasHZ = player.getHandCardCnt(MahjongUtil.MJ_Z_FENG) > 0;
        if (MahjongUtil.MJ_Z_FENG == huCard) {
            hasHZ = true;
        }
        if (!this.isStartTake) {
            player.addHu(EPaiXing.HZMJ_TH, huCard);
            isBigHu = true;
        } else {
            if (isZiMo && this.isTianHu(player)) {
                player.addHu(isZiMo ? -1 : takePlayer.getUid(), EPaiXing.HZMJ_TH, huCard);
                isBigHu = true;
            } else if (MahjongUtil.isPengPengHu(player.getHandCardRaw(), this.laiZiCard)) {
                player.addHu(isZiMo ? -1 : takePlayer.getUid(), EPaiXing.HZMJ_PPH, huCard);
                isBigHu = true;
            } else if (this.seven && MahjongUtil.isSevenPair(player.getHandCardRaw(), this.laiZiCard)) {
                player.addHu(isZiMo ? -1 : takePlayer.getUid(), EPaiXing.HZMJ_QD, huCard);
                isBigHu = true;
            } else {
                for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                    tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
                }
                List<CPGNode> cpgNodes = player.getCPGNode();
                for (CPGNode node : cpgNodes) {
                    if (CPGNode.EType.ANY_THREE == node.getType()) {
                        continue;
                    }
                    tempCards.get()[node.getCard1()] += 1;
                }
            }
            if (MahjongUtil.isQingYiSe(tempCards.get(), this.laiZiCard)) {
                player.addHu(isZiMo ? -1 : takePlayer.getUid(), EPaiXing.HZMJ_QYS, huCard);
                isBigHu = true;
            }
            if (!isBigHu) {
                player.addHu(isZiMo ? -1 : takePlayer.getUid(), EPaiXing.HZMJ_NORMAL, huCard);
            }
        }
        if (isZiMo) {
            int niaoScore = 0;
            for (int i = 0; i < this.playerNum; ++i) {
                IHZMJMahjongPlayer otherPlayer = (IHZMJMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || player.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                int huScore = 2;
                if (this.qdPphThQysInc && isBigHu) {
                    huScore += 1;
                }
                if (this.noneZhongMul && !hasHZ) {
                    huScore *= 2;
                }

                if (this.bankerInc && (this.bankerIndex == player.getIndex() || this.bankerIndex == otherPlayer.getIndex())) {
                    huScore += 1;
                }

                if (this.wypn) {
                    huScore += 2;
                }

                int piaoScore = player.getPiao() + otherPlayer.getPiao();

                otherPlayer.addScore(Score.MJ_CUR_HU_SCORE, -huScore, false);
                otherPlayer.addScore(Score.MJ_CUR_PIAO_SCORE, -piaoScore, false);
                player.addScore(Score.MJ_CUR_HU_SCORE, huScore, false);
                player.addScore(Score.MJ_CUR_PIAO_SCORE, piaoScore, false);

                if (this.wypn) {
                    otherPlayer.addScore(Score.MJ_CUR_WYPN_SCORE, -2, false);
                    player.addScore(Score.MJ_CUR_WYPN_SCORE, 2, false);
                }
                if (null != bird) {
                    int value = bird.calcNiaoScore(player, otherPlayer, this.allMissIsAllHit, this.allHitMul, this.niaoScore);
                    otherPlayer.addScore(Score.MJ_CUR_NIAO_SCORE, -value, false);
                    niaoScore += value;
                }
            }
            player.addScore(Score.MJ_CUR_NIAO_SCORE, niaoScore, false);
        } else {
            int huScore = 1;
            if (this.dianPao2) {
                huScore = 2;
            }
            if (this.qdPphThQysInc && isBigHu) {
                huScore += 1;
            }

            if (this.noneZhongMul && !hasHZ) {
                huScore *= 2;
            }
            if (this.bankerInc && (this.bankerIndex == player.getIndex() || this.bankerIndex == takePlayer.getIndex())) {
                huScore += 1;
            }

            if (this.wypn) {
                huScore += 2;
            }
            int piaoScore = player.getPiao() + takePlayer.getPiao();

            takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -huScore, false);
            takePlayer.addScore(Score.MJ_CUR_PIAO_SCORE, -piaoScore, false);
            player.addScore(Score.MJ_CUR_HU_SCORE, huScore, false);
            player.addScore(Score.MJ_CUR_PIAO_SCORE, piaoScore, false);

            if (this.wypn) {
                takePlayer.addScore(Score.MJ_CUR_WYPN_SCORE, -2, false);
                player.addScore(Score.MJ_CUR_WYPN_SCORE, 2, false);
            }
            if (null != bird) {
                int value = bird.calcNiaoScore(takePlayer, player, this.allMissIsAllHit, this.allHitMul, this.niaoScore);
                player.addScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                takePlayer.addScore(Score.MJ_CUR_NIAO_SCORE, -value, false);
            }
        }
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        IMahjongBird bird = null;
        // 抓鸟
        if (this.niaoType > 0) {
            bird = IMahjongBird.get(this.niaoType);
            do {
                if (null == bird) {
                    break;
                }
                int num = this.huType;
                if (null == player2) {
                    // 无多胡
                    if (this.noneHZAdd2 && MahjongUtil.MJ_Z_FENG != huCard && !player1.hasHandCard(MahjongUtil.MJ_Z_FENG, 1)) {
                        num += 2;
                    }
                }
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
            } while (false);
        }

        boolean isZiMo = takePlayer.getUid() == player1.getUid();
        if (null != player1) {
            if (-1 != huCard) {
                player1.addHandCard(huCard);
            }
            this.calcHuScore((IHZMJMahjongPlayer) takePlayer, (IHZMJMahjongPlayer) player1, isZiMo, huCard, bird);
            if (-1 != huCard) {
                player1.delHandCard(huCard);
            }
        }
        if (null != player2) {
            if (-1 != huCard) {
                player2.addHandCard(huCard);
            }
            this.calcHuScore((IHZMJMahjongPlayer) takePlayer, (IHZMJMahjongPlayer) player2, isZiMo, huCard, bird);
            if (-1 != huCard) {
                player2.delHandCard(huCard);
            }
        }
        if (null != player3) {
            if (-1 != huCard) {
                player3.addHandCard(huCard);
            }
            this.calcHuScore((IHZMJMahjongPlayer) takePlayer, (IHZMJMahjongPlayer) player3, isZiMo, huCard, bird);
            if (-1 != huCard) {
                player3.delHandCard(huCard);
            }
        }
        boolean isQGH = !isZiMo && EActionOp.BAR == this.prevAction;
        HZMJResultRecordAction resultRecordAction = (HZMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        resultRecordAction.addNiaoList(this.niaoList);

        if (isQGH && !this.qgUnBp) {
            takePlayer.addShowFlag(EShowFlag.HZMJ_QGH_BP);
        }

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            int score = this.getScore(
                    +player.getScore(Score.MJ_CUR_HU_SCORE, false)
                            + player.getScore(Score.MJ_CUR_NIAO_SCORE, false)
                            + player.getScore(Score.MJ_CUR_PIAO_SCORE, false)
            );
            if (isQGH && !this.qgUnBp) {
                if (takePlayer.getUid() != player.getUid() && score < 0) {
                    takePlayer.addScore(Score.SCORE, score, false);
                    takePlayer.addScore(Score.ACC_TOTAL_SCORE, score, true);
                } else {
                    player.addScore(Score.SCORE, score, false);
                    player.addScore(Score.ACC_TOTAL_SCORE, score, true);
                }
            } else {
                player.addScore(Score.SCORE, score, false);
                player.addScore(Score.ACC_TOTAL_SCORE, score, true);
            }
            score = this.getScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, score, true);

            HZMJResultRecordAction.PlayerInfo playerInfo = new HZMJResultRecordAction.PlayerInfo();

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

            HZMJResultRecordAction.ScoreInfo scoreInfo = new HZMJResultRecordAction.ScoreInfo();
            scoreInfo.setNiaoScore(player.getScore(Score.MJ_CUR_NIAO_SCORE, false));
            scoreInfo.setWypn(player.getScore(Score.MJ_CUR_WYPN_SCORE, false));
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setPiaoScore(player.getScore(Score.MJ_CUR_PIAO_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        if (2 == this.bankerType) {
            this.bankerIndex = (this.bankerIndex + 1) % this.playerNum;
        } else {
            if (null == player2) {
                this.bankerIndex = player1.getIndex();
            } else {
                this.bankerIndex = takePlayer.getIndex();
            }
        }
        onOver();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        HZMJResultRecordAction resultRecordAction = (HZMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.addScore(Score.SCORE, this.getScore(
                    player.getScore(Score.MJ_CUR_GANG_SCORE, false)
            ), false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            HZMJResultRecordAction.PlayerInfo playerInfo = new HZMJResultRecordAction.PlayerInfo();
            HZMJResultRecordAction.ScoreInfo scoreInfo = new HZMJResultRecordAction.ScoreInfo();
            scoreInfo.setNiaoScore(player.getScore(Score.MJ_CUR_NIAO_SCORE, false));
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        //this.bankerIndex = this.lastFumbleIndex;
        onOver();
    }

    private void onOver(){
        this.getRoomHandle().calculateGold();
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByHZMJ info = new PCLIMahjongNtfGameOverInfoByHZMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

        info.niaoList.addAll(this.niaoList);

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByHZMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByHZMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfo.HuInfo temp = new PCLIMahjongNtfGameOverInfo.HuInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByHZMJ.ScoreInfo();
            ((PCLIMahjongNtfGameOverInfoByHZMJ.ScoreInfo) playerInfo.score).niaoScore = player.getScore(Score.MJ_CUR_NIAO_SCORE, false);
            ((PCLIMahjongNtfGameOverInfoByHZMJ.ScoreInfo) playerInfo.score).wypn = player.getScore(Score.MJ_CUR_WYPN_SCORE, false);
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            ((PCLIMahjongNtfGameOverInfoByHZMJ.ScoreInfo) playerInfo.score).piaoScore = player.getScore(Score.MJ_CUR_PIAO_SCORE, false);
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
                //麻将不含梅兰竹菊东南西北发白
                if (j > 27 && j != 32) {
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
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1)  * value;
    }
    @Override
    public void clear() {
        super.clear();
        this.niaoList.clear();
    }
}