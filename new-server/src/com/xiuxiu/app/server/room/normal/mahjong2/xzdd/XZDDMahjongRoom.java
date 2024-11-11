package com.xiuxiu.app.server.room.normal.mahjong2.xzdd;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.*;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.player.mahjong2.IDingQue;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IXZDDMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.XZDDMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.*;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//@GameInfo(gameType = GameType.GAME_TYPE_XZDD)
public class XZDDMahjongRoom extends MahjongRoom {
    protected int top = 2;                      // 封顶
    protected int ziMoType = 0;                 // 自摸类型
    protected boolean huanSanZhang = false;     // 换三张
    protected boolean tianDiHu = false;         // 天地胡
    protected boolean xuanJiuJiangDui = false;  // 玄九将对
    protected boolean menQingZhongZhang = false;// 门清中张
    protected boolean qym = true;               // 缺一门
    protected boolean wang = true;              // 万
    protected boolean dgh = false;              // 点杠花
    protected int firstHuIndex = -1;            // 每局第一个胡牌人的index

    protected int overCnt = 0;                  // 已经结束次数

    public XZDDMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public XZDDMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }


    @Override
    public void init() {
        super.init();

        this.fangGangScore = 2;

        this.top = this.getRule().getOrDefault(RoomRule.RR_MJ_TOP, 2);
        this.ziMoType = this.getRule().getOrDefault(RoomRule.RR_MJ_XZDD_ZIMO_TYPE, 0);
        this.huanSanZhang = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EXZDDPlayRule.HUANGPAI.getValue());
        this.tianDiHu = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EXZDDPlayRule.TDH.getValue());
        this.xuanJiuJiangDui = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EXZDDPlayRule.XJJD.getValue());
        this.menQingZhongZhang = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EXZDDPlayRule.MQZZ.getValue());
        this.qym = 0 == (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EXZDDPlayRule.QYM.getValue());
        this.wang = 0 == (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EXZDDPlayRule.WANG.getValue());
        this.dgh = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EXZDDPlayRule.WANG.getValue());
    }

    @Override
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB) {
            this.allCard.addAll(CardLibraryManager.I.getMahjongCard());
            return;
        }
        for (int i = 0; i < 4; ++i) {
            if (this.wang) {
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
        }

        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doStart1() {
        this.firstHuIndex = -1;
        if (this.huanSanZhang) {
            this.beginHuanPai();
        } else {
            if (this.qym) {
                this.beginDingQue();
            } else {
                this.doStartTake();
            }
        }
    }

    @Override
    public void endHuanPai() {
        super.endHuanPai();
        if (this.qym) {
            this.beginDingQue();
        } else {
            this.doStartTake();
        }
    }

    @Override
    public void endDingQue() {
        super.endDingQue();
        this.doStartTake();
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new XZDDMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    public boolean isCanTakeCard(IMahjongPlayer player, byte card) {
        if (player.isHu()) {
            return player.getLastFumbleCard() == card;
        }
        if (-1 == ((IDingQue) player).getQue() || ((IDingQue) player).getQue() == MahjongUtil.getColor(card)) {
            return true;
        }
        return super.isCanTakeCard(player, card);
    }

    @Override
    public void onBar(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        super.onBar(takePlayer, player, param);
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (((IDingQue) player).isQueCard(takeCard)) {
            return false;
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if (((IDingQue) player).isQueCard(takeCard)) {
            return false;
        }
        return super.isBump(player, takeCard);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        return false;
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        if (((IDingQue) player).hasQueCard()) {
            return false;
        }
        if (!ziMo && player.isPass(EActionOp.HU)) {
            return false;
        }
        return MahjongUtil.isHu(player.getHandCardRaw(), player.getCPGNodeCnt());
    }

    @Override
    protected EPaiXing getPaiXing(IMahjongPlayer player) {
        boolean isAll258 = true;
        boolean hasFour = false;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
            if (tempCards.get()[i] < 1) {
                continue;
            }
            if (isAll258 && !MahjongUtil.is258((byte) i)) {
                isAll258 = false;
            }
            if (4 == tempCards.get()[i]) {
                hasFour = true;
            }
        }
        int bumpOrBarCnt = 0;
        List<CPGNode> cpgNodes = player.getCPGNode();
        for (CPGNode node : cpgNodes) {
            if (CPGNode.EType.ANY_THREE == node.getType()) {
                continue;
            }
            if (isAll258 && !MahjongUtil.is258(node.getCard1())) {
                isAll258 = false;
            }
            ++bumpOrBarCnt;
            tempCards.get()[node.getCard1()] += 1;
        }
        EPaiXing px = EPaiXing.XZDD_NORMAL;
        boolean isPPH = MahjongUtil.isPengPengHu(player.getHandCardRaw());
        if (isPPH) {
            px = EPaiXing.XZDD_PPH;
        }
        boolean isQYS = MahjongUtil.isQingYiSeAll(tempCards.get());
        boolean isQD = MahjongUtil.isSevenPair(player.getHandCardRaw());
        if (isQYS) {
            px = EPaiXing.XZDD_QYS;
        }
        if (isQD) {
            px = EPaiXing.XZDD_QD;
        }
        boolean isXuanJiu = false;
        if (this.xuanJiuJiangDui && 0 == bumpOrBarCnt && MahjongUtil.isXuanJiu(player.getHandCardRaw())) {
            // 幺九
            isXuanJiu = true;
            px = EPaiXing.XZDD_XUAN_JIU;
        }
        if (isPPH && isQYS) {
            // 请对
            px = EPaiXing.XZDD_QING_DUI;
        }
        if (this.xuanJiuJiangDui && isPPH && isAll258) {
            // 将对
            px = EPaiXing.XZDD_JIANG_DUI;
        }
        if (0 == bumpOrBarCnt && isQD && hasFour) {
            // 龙七对
            px = EPaiXing.XZDD_LQD;
        }
        if (isQYS && isQD) {
            // 清七对
            px = EPaiXing.XZDD_QQD;
        }
        if (isQYS && isXuanJiu) {
            // 清玄九
            px = EPaiXing.XZDD_QXJ;
        }
        return px;
    }

    @Override
    protected int getFang(IMahjongPlayer player) {
        if (!this.isHu(player)) {
            return 0;
        }
        EPaiXing px = this.getPaiXing(player);
        return (int) Math.pow(2, px.getDefaultValue());
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        boolean tianHu = EActionOp.NORMAL == this.prevAction;
        EActionOp curOp = this.curAction;
        EActionOp prevOp = this.prevAction;
        EBarType prevBarType = this.prevBarType;
        int prevPlayerIndex = this.prevOpIndex;
        EActionOp prevPrevOp = this.prevPrevAction;
        super.onHu(takePlayer, player1, player2, player3, huCard);

        HuRecordAction huRecordAction = ((MahjongRecord) this.getRecord()).addHuRecordAction(takePlayer.getUid());

        PCLIMahjongNtfHuInfo info = new PCLIMahjongNtfHuInfo();
        info.takePlayerUid = takePlayer.getUid();
        info.huCard = huCard;

        boolean ziMo = false;
        if (takePlayer.getUid() == player1.getUid()) {
            // 自摸
            ziMo = true;
            if (EActionOp.BAR == prevOp) {
                if (EBarType.BAR_FANG == prevBarType) {
                    if (!this.dgh) {
                        ziMo = false;
                        takePlayer = (IMahjongPlayer) this.getRoomPlayer(prevPlayerIndex);
                    }
                }
            }
        }

        if (ziMo) {
            // 自摸
            player1.addHandCard(huCard);
            EPaiXing px = this.getPaiXing(player1);
            player1.delHandCard(huCard);
            player1.setOver(true);
            ++this.overCnt;
            int fang = this.getResultFang(player1, px, tianHu, player1.getFumbleCnt() <= 1 && !this.isOverBanker, EActionOp.BAR == this.prevAction, false, huCard, true);
            int score = 0;
            if (0 == this.ziMoType) {
                score = (int) (Math.pow(2, fang) + 1);
            } else {
                fang += 1;
                if (fang > this.top) {
                    fang = this.top;
                }
                score = (int) Math.pow(2, fang);
            }

            //天胡地胡就去掉平胡
            if (px == EPaiXing.XZDD_NORMAL) {
                if (tianHu || player1.getFumbleCnt() <= 1 && !this.isOverBanker) {

                }else {
                    player1.addHu(px, huCard);

                    huRecordAction.addHu(player1.getUid(), true, px, huCard, px.getDefaultValue());
                }
            }

            PCLIMahjongNtfHuInfo.HuInfo huInfo = new PCLIMahjongNtfHuInfo.HuInfo();
            huInfo.paiXing = px.getClientValue();
            huInfo.paiXingValue = fang;
            info.allHuInfo.put(player1.getUid(), huInfo);

            PCLIMahjongNtfHuInfo.ScoreInfo scoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
            info.allScoreInfo.put(player1.getUid(), scoreInfo);

            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || otherPlayer.getUid() == player1.getUid() || otherPlayer.isOver()) {
                    continue;
                }
                otherPlayer.addScore(Score.MJ_CUR_HU_SCORE, -score, false);
                player1.addScore(Score.MJ_CUR_HU_SCORE, score, false);

                int tempScore = score * endPoint;
                scoreInfo.paiXingValue += tempScore;

                PCLIMahjongNtfHuInfo.ScoreInfo tempScoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
                tempScoreInfo.paiXingValue -= tempScore;
                tempScoreInfo.totalScore = this.getCurScore(otherPlayer);
                info.allScoreInfo.put(otherPlayer.getUid(), tempScoreInfo);

                huRecordAction.addScore(otherPlayer.getUid(), -tempScore);
                huRecordAction.addScore(player1.getUid(), tempScore);
            }
            scoreInfo.totalScore = this.getCurScore(player1);
        } else {
            // 点炮
            boolean isGSP = false;
            boolean isQGH = false;
            if (EActionOp.BAR == prevPrevOp) {
                // 杠上跑
                isGSP = true;
            } else if (EActionOp.BAR == curOp) {
                // 抢杠胡
                isQGH = true;
            }

            PCLIMahjongNtfHuInfo.ScoreInfo scoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
            info.allScoreInfo.put(takePlayer.getUid(), scoreInfo);

            if (null != player1 && !player1.isOver()) {
                boolean rQGH = false;
                if (isQGH) {
                    rQGH = true;
                }
                if (isGSP) {
                    this.hjzy(takePlayer, player1);
                }
                player1.addHandCard(huCard);
                EPaiXing px = this.getPaiXing(player1);
                player1.delHandCard(huCard);
                player1.addHu(takePlayer.getUid(), px, huCard);
                player1.setOver(true);
                ++this.overCnt;
                int fang = this.getResultFang(player1, px, false, player1.getFumbleCnt() <= 1 && !this.isOverBanker, isGSP, rQGH, huCard, false);
                int score = (int) Math.pow(2, fang);
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -score, false);
                player1.addScore(Score.MJ_CUR_HU_SCORE, score, false);

                PCLIMahjongNtfHuInfo.HuInfo huInfo = new PCLIMahjongNtfHuInfo.HuInfo();
                huInfo.paiXing = px.getClientValue();
                huInfo.paiXingValue = fang;

                info.allHuInfo.put(player1.getUid(), huInfo);
                int tempScore = score * endPoint;

                PCLIMahjongNtfHuInfo.ScoreInfo tempScoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
                tempScoreInfo.paiXingValue += tempScore;
                tempScoreInfo.totalScore = this.getCurScore(player1);
                info.allScoreInfo.put(player1.getUid(), tempScoreInfo);

                scoreInfo.paiXingValue -= tempScore;

                huRecordAction.addHu(player1.getUid(), false, px, huCard, px.getDefaultValue());
                huRecordAction.addScore(takePlayer.getUid(), -tempScore);
                huRecordAction.addScore(player1.getUid(), tempScore);
            }
            if (null != player2 && !player2.isOver()) {
                boolean rQGH = false;
                if (isQGH) {
                    rQGH = true;
                }
                player2.addHandCard(huCard);
                EPaiXing px = this.getPaiXing(player2);
                player2.delHandCard(huCard);
                player2.addHu(takePlayer.getUid(), px, huCard);
                player2.setOver(true);
                ++this.overCnt;
                int fang = this.getResultFang(player2, px, false, player1.getFumbleCnt() <= 1 && !this.isOverBanker, isGSP, rQGH, huCard, false);
                int score = (int) Math.pow(2, fang);
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -score, false);
                player2.addScore(Score.MJ_CUR_HU_SCORE, score, false);

                PCLIMahjongNtfHuInfo.HuInfo huInfo = new PCLIMahjongNtfHuInfo.HuInfo();
                huInfo.paiXing = px.getClientValue();
                huInfo.paiXingValue = fang;
                info.allHuInfo.put(player2.getUid(), huInfo);

                int tempScore = score * endPoint;

                PCLIMahjongNtfHuInfo.ScoreInfo tempScoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
                tempScoreInfo.paiXingValue += tempScore;
                tempScoreInfo.totalScore = this.getCurScore(player2);
                info.allScoreInfo.put(player2.getUid(), tempScoreInfo);

                scoreInfo.paiXingValue -= tempScore;

                huRecordAction.addHu(player2.getUid(), false, px, huCard, px.getDefaultValue());
                huRecordAction.addScore(takePlayer.getUid(), -tempScore);
                huRecordAction.addScore(player2.getUid(), tempScore);
            }
            if (null != player3 && !player3.isOver()) {
                boolean rQGH = false;
                if (isQGH) {
                    rQGH = true;
                }
                player3.addHandCard(huCard);
                EPaiXing px = this.getPaiXing(player3);
                player3.delHandCard(huCard);
                player3.addHu(takePlayer.getUid(), px, huCard);
                player3.setOver(true);
                ++this.overCnt;
                int fang = this.getResultFang(player3, px, false, player1.getFumbleCnt() <= 1 && !this.isOverBanker, isGSP, rQGH, huCard, false);
                int score = (int) Math.pow(2, fang);
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -score, false);
                player3.addScore(Score.MJ_CUR_HU_SCORE, score, false);

                PCLIMahjongNtfHuInfo.HuInfo huInfo = new PCLIMahjongNtfHuInfo.HuInfo();
                huInfo.paiXing = px.getClientValue();
                huInfo.paiXingValue = fang;
                info.allHuInfo.put(player3.getUid(), huInfo);

                int tempScore = score * endPoint;

                PCLIMahjongNtfHuInfo.ScoreInfo tempScoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
                tempScoreInfo.paiXingValue += tempScore;
                tempScoreInfo.totalScore = this.getCurScore(player3);
                info.allScoreInfo.put(player3.getUid(), tempScoreInfo);

                scoreInfo.paiXingValue -= tempScore;

                huRecordAction.addHu(player3.getUid(), false, px, huCard, px.getDefaultValue());
                huRecordAction.addScore(takePlayer.getUid(), -tempScore);
                huRecordAction.addScore(player3.getUid(), tempScore);
            }
            scoreInfo.totalScore = this.getCurScore(takePlayer);
        }

        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_HU_INFO, info);

        if (this.overCnt >= (this.getCurPlayerCnt() - 1) || this.onCheckOver()) {
            this.onHuangZhuang(this.curBureau < this.bureau);
        } else {
            int nextTakeIndex = player1.getIndex();
            if (null != player2 && player2.getIndex() > nextTakeIndex) {
                nextTakeIndex = player2.getIndex();
            }
            if (null != player3 && player3.getIndex() > nextTakeIndex) {
                nextTakeIndex = player3.getIndex();
            }
            this.onFumble((IMahjongPlayer) this.getNextRoomPlayer(nextTakeIndex));
        }

        if (this.firstHuIndex == -1) {
            int m_temp = player1.getIndex();
            if (player2 != null && player2.getIndex() == (takePlayer.getIndex()+1)%this.getCurPlayerCnt()) {
                m_temp = player2.getIndex();
            } else if (player3 != null && player3.getIndex() == (takePlayer.getIndex()+1)%this.getCurPlayerCnt()) {
                m_temp = player3.getIndex();
            }
            this.firstHuIndex = m_temp;
            this.bankerIndex = this.firstHuIndex;
        }
    }

    private boolean hjzy(IMahjongPlayer takePlayer, IMahjongPlayer huPlayer) {
        if (this.barScoreRecord.isEmpty()) {
            return false;
        }
        BarScoreRecord lastBarScoreRecord = this.barScoreRecord.get(this.barScoreRecord.size() - 1);
        if (lastBarScoreRecord.getTagPlayerUid() == takePlayer.getUid()) {
            PCLIMahjongNtfGangScoreInfo info = new PCLIMahjongNtfGangScoreInfo();
            info.hjzy = true;
            int barScore = lastBarScoreRecord.getValue();
            if (EBarType.BAR_FANG == lastBarScoreRecord.getType()) {
                // 放杠
                takePlayer.addScore(Score.MJ_CUR_GANG_SCORE, -barScore, false);
                huPlayer.addScore(Score.MJ_CUR_GANG_SCORE, barScore, false);

                int score = barScore * this.endPoint;
                info.gangScore.put(takePlayer.getUid(), -score);
                info.gangScore.put(huPlayer.getUid(), score);
            } else {
                for (int j = 0; j < this.playerNum; ++j) {
                    IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[j];
                    if (null == otherPlayer || otherPlayer.isGuest() || otherPlayer.getUid() == takePlayer.getUid() || otherPlayer.isOver()) {
                        continue;
                    }
                    takePlayer.addScore(Score.MJ_CUR_GANG_SCORE, -barScore, false);
                    huPlayer.addScore(Score.MJ_CUR_GANG_SCORE, barScore, false);

                    int score = barScore * this.endPoint;
                    info.gangScore.put(takePlayer.getUid(), -score + info.gangScore.getOrDefault(takePlayer.getUid(), 0));
                    info.gangScore.put(huPlayer.getUid(), score + info.gangScore.getOrDefault(huPlayer.getUid(), 0));
                }
            }
            lastBarScoreRecord.setTagPlayerUid(huPlayer.getUid());

            info.totalScore.put(takePlayer.getUid(), this.getCurScore(takePlayer));
            info.totalScore.put(huPlayer.getUid(), this.getCurScore(huPlayer));
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_GANG_SCORE_INFO, info);
            return true;
        }
        return false;
    }

    private int getResultFang(IMahjongPlayer player, EPaiXing px, boolean tianHu, boolean diHu, boolean gk, boolean qgh, byte huCard, boolean ziMo) {
        int extra = 0;
        if (this.menQingZhongZhang) {
            int value = this.isMemQinOrZhongZhang(player);
            if (1 == (value & 0x01)) {
                // 中张
                extra += 1;
                player.addPaiXing(EPaiXing.XZDD_ZZ);
            }
            if (1 == ((value >> 1) & 0x01)) {
                // 门清
                extra += 1;
                player.addPaiXing(EPaiXing.XZDD_MQ);
            }
        }
        int gen = this.calcGen(player, huCard, ziMo);
        //是否显示跟
        boolean m_bshowGen = false;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (4 == player.getHandCardCnt((byte) i)) {
                m_bshowGen = true;
                break;
            }
        }
        if (gen > 0 && m_bshowGen) {
            player.addPaiXing(EPaiXing.XZDD_GEN);
        }
        extra += gen;
        if (this.tianDiHu) {
            if (tianHu) {
                // 天胡
                extra += 3;
                player.addPaiXing(EPaiXing.XZDD_TH);
            } else if (diHu) {
                // 地胡
                extra += 2;
                player.addPaiXing(EPaiXing.XZDD_DH);
            }
        }
        if (gk) {
            // 杠上开花
            extra += 1;
            if (ziMo) {
                player.addPaiXing(EPaiXing.XZDD_GK);
            } else {
                player.addPaiXing(EPaiXing.XZDD_GSP);
            }
        } else if (qgh) {
            // 抢杠胡
            extra += 1;
            player.addPaiXing(EPaiXing.XZDD_QGH);
        }
        int fang = extra + px.getDefaultValue();
        if (fang > this.top) {
            fang = this.top;
        }

        for (EPaiXing p : player.getAllPaiXing()) {
            player.addHu(player.getUid(), p, huCard);
        }
        return fang;
    }

    private int isMemQinOrZhongZhang(IMahjongPlayer player) {
        boolean zhongZhang = true;
        boolean memQing = true;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (player.getHandCardCnt((byte) i) < 1) {
                continue;
            }
            if (zhongZhang && MahjongUtil.is19((byte) i)) {
                zhongZhang = false;
            }
        }
        List<CPGNode> cpgNodes = player.getCPGNode();
        for (CPGNode node : cpgNodes) {
            if (CPGNode.EType.ANY_THREE == node.getType()) {
                continue;
            }
            if (zhongZhang && MahjongUtil.is19(node.getCard1())) {
                zhongZhang = false;
            }
            if (memQing && CPGNode.EType.BAR_AN != node.getType()) {
                memQing = false;
            }
        }
        return ((memQing ? 1 : 0) << 1) | (zhongZhang ? 1 : 0);
    }

    private int calcGen(IMahjongPlayer player, byte huCard, boolean ziMo) {
        int gen = 0;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (4 == player.getHandCardCnt((byte) i)) {
                ++gen;
            }
        }
        List<CPGNode> cpgNodes = player.getCPGNode();
        for (CPGNode node : cpgNodes) {
            if (CPGNode.EType.ANY_THREE == node.getType()) {
                continue;
            }
            if (node.isBar()) {
                ++gen;
            } else if (node.isBump()) {
                if (player.hasHandCard(node.getCard1(), 1) || (!ziMo && huCard == node.getCard1())) {
                    ++gen;
                }
            }
        }
        return gen;
    }

    @Override
    public void onHuangZhuang(boolean next) {
        if (this.getCurPlayerCnt() - this.overCnt >= 2) {
            // 退杠
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                if (null == player || player.isGuest() || player.isOver()) {
                    continue;
                }
                int fang = player.getTingInfo().getMaxFang();
                if (fang > 0) {
                    continue;
                }
                for (BarScoreRecord barScoreRecord : this.barScoreRecord) {
                    if (barScoreRecord.getTagPlayerUid() != player.getUid()) {
                        continue;
                    }
                    if (EBarType.BAR_FANG == barScoreRecord.getType()) {
                        // 放杠
                        this.getRoomPlayer(barScoreRecord.getTakePlayerUid()).addScore(Score.MJ_CUR_GANG_SCORE, barScoreRecord.getValue(), false);
                        player.addScore(Score.MJ_CUR_GANG_SCORE, -barScoreRecord.getValue(), false);
                    } else {
                        for (int j = 0; j < this.playerNum; ++j) {
                            IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[j];
                            if (null == otherPlayer || otherPlayer.isGuest() || otherPlayer.getUid() == player.getUid() || otherPlayer.isOver()) {
                                continue;
                            }
                            otherPlayer.addScore(Score.MJ_CUR_GANG_SCORE, barScoreRecord.getValue(), false);
                            player.addScore(Score.MJ_CUR_GANG_SCORE, -barScoreRecord.getValue(), false);
                        }
                    }
                }
            }
            // 花猪(流局才检测，不算中途解散)
            boolean hasHuaZhu = false;
            boolean ballHuaZhu = true;//是否所有人都查花猪
            List<IXZDDMahjongPlayer> m_playerList = new ArrayList<>();
            if (this.allCard.isEmpty()) {
                for (int i = 0; i < this.playerNum; ++i) {
                    IXZDDMahjongPlayer player = (IXZDDMahjongPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest() || player.isOver()) {
                        continue;
                    }
                    if (player.hasAllCardWithColor(MahjongUtil.COLOR_WANG) && player.hasAllCardWithColor(MahjongUtil.COLOR_TIAO) && player.hasAllCardWithColor(MahjongUtil.COLOR_TONG)) {
                        // 花猪
                        hasHuaZhu = true;
                        m_playerList.add(player);//存进列表
                        //player.setHuaZhu(true);
                    }else {
                        ballHuaZhu = false;
                    }
                }
            }
            //有花猪并且不是所有人花猪
            if (hasHuaZhu && !ballHuaZhu) {
                for (int i = 0; i < m_playerList.size(); i++) {
                    m_playerList.get(i).setHuaZhu(true);
                }
                for (int i = 0; i < this.playerNum; ++i) {
                    IXZDDMahjongPlayer player = (IXZDDMahjongPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest() || !player.isHuaZhu()) {
                        continue;
                    }
                    for (int j = 0; j < this.playerNum; ++j) {
                        IXZDDMahjongPlayer otherPlayer = (IXZDDMahjongPlayer) this.allPlayer[j];
                        if (null == otherPlayer || otherPlayer.isGuest() || otherPlayer.isHuaZhu() || otherPlayer.isOver()) {
                            continue;
                        }
                        otherPlayer.addScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, 9, false);
                        player.addScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, -9, false);
                        player.addHuaZhuValue(9);
                    }
                }
            }
            // 查大叫
            for (int i = 0; i < this.playerNum; ++i) {
                IXZDDMahjongPlayer player = (IXZDDMahjongPlayer) this.allPlayer[i];
                if (null == player || player.isGuest() || player.isHuaZhu()) {
                    continue;
                }
                int fang = player.getTingInfo().getMaxFang();
                if (0 == fang) {
                    for (int j = 0; j < this.playerNum; ++j) {
                        IMahjongPlayer temp = (IMahjongPlayer) this.allPlayer[j];
                        if (null == temp || temp.isGuest() || temp.isOver()) {
                            continue;
                        }
                        if (temp.getUid() == player.getUid()) {
                            continue;
                        }
                        int tempFang = temp.getTingInfo().getMaxFang();
                        if (tempFang < 1) {
                            continue;
                        }
                        temp.addScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, tempFang, false);
                        player.addScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, -tempFang, false);
                        player.setChaJiao(true);
                        player.addChaJiaoValue(tempFang);
                    }
                }
            }

        }

        XZDDResultRecordAction resultRecordAction = (XZDDResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IXZDDMahjongPlayer player = (IXZDDMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.addScore(Score.SCORE, this.getScore(
                    player.getScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, false)
                            + player.getScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, false)
                            + player.getScore(Score.MJ_CUR_HU_SCORE, false)
                            + player.getScore(Score.MJ_CUR_GANG_SCORE, false)
            ), false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            XZDDResultRecordAction.PlayerInfo playerInfo = new XZDDResultRecordAction.PlayerInfo();
            playerInfo.setChaDaJiao(player.isChaJiao());
            playerInfo.setChaHuaZhu(player.isHuaZhu());
            playerInfo.setDaJiaoValue(player.getChaJiaoValue());
            playerInfo.setHuaZhuValue(player.getHuaZhuValue());

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

            ResultRecordAction.ScoreInfo scoreInfo = new ResultRecordAction.ScoreInfo();
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
        PCLIMahjongNtfGameOverInfoByXZDD info = new PCLIMahjongNtfGameOverInfoByXZDD();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

        for (int i = 0; i < this.playerNum; ++i) {
            IXZDDMahjongPlayer player = (IXZDDMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByXZDD.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByXZDD.PlayerInfo();
            playerInfo.isChaDaJiao = player.isChaJiao();
            playerInfo.isChaHuaZhu = player.isHuaZhu();
            playerInfo.daJiaoValue = player.getChaJiaoValue();
            playerInfo.huaZhuValue = player.getHuaZhuValue();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByXZDD.ScoreInfo();
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false)
                                       + player.getScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, false)
                                       + player.getScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
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

    @Override
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }

        PCLIMahjongNtfDeskInfoByXZDD deskInfo= new PCLIMahjongNtfDeskInfoByXZDD();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 : mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        deskInfo.bankerPlayerUid = -1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest() ? -1L : this.allPlayer[this.bankerIndex].getUid();
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();

        try {
            this.rwLock.readLock().lock();
            int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer other = (IMahjongPlayer) this.allPlayer[i];
                if (null == other || other.isGuest()) {
                    continue;
                }
                deskInfo.allOnlineState.put(other.getUid(), other.isOffline() ? false : true);
                PCLIMahjongNtfDeskInfoByXZDD.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByXZDD.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByXZDD.CardNode cardNode = new PCLIMahjongNtfDeskInfoByXZDD.CardNode();
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
                deskPlayerInfo.over = other.isOver();
                other.addDeskCardTo(deskPlayerInfo.deskCard);
                if (other.isOver() || player.getUid() == other.getUid()) {
                    other.addHandCardTo(deskPlayerInfo.card, deskPlayerInfo.fumble);
                }
                deskPlayerInfo.queColor = ((IDingQue) other).getQue();
                other.addHuCardTo(deskPlayerInfo.huCard);
                deskInfo.other.put(other.getUid(), deskPlayerInfo);
            }
        } finally {
            this.rwLock.readLock().unlock();
        }
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override
    protected String getCurScore(IMahjongPlayer player) {
        return this.getFormatScore(player.getScore()
                + (player.getScore(Score.MJ_CUR_GANG_SCORE, false)
                 + player.getScore(Score.MJ_CUR_HU_SCORE, false)) * endPoint * 100);
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new XZDDMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    public void clear() {
        super.clear();
        this.overCnt = 0;
    }
}
