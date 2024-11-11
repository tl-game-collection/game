package com.xiuxiu.app.server.room.normal.mahjong2.ycxl;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.*;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongConstant;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongHuanPaiActionByYCXL;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.player.mahjong2.*;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.*;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.*;

//@GameInfo(gameType = GameType.GAME_TYPE_YCXL)
public class YCXLMahjongRoom extends MahjongRoom implements IMahjongHuanPai, IMahjongShuaiPai, IMahjongDingQue {
    protected int huanPaiType;                                                                                          // 0: 换三张, 1: 甩三张, 2: 先换在甩
    protected int dingPiaoType;                                                                                         // 定漂, 0, 1, 2, 3, 4, 5, 6
    protected boolean dingque;
    protected boolean isTen;                    // 是否十秒
    private static final int RULE_WITH_TIMER = 0x01;
    private static final int RULE_WITH_DING_QUE = 0x02;

    public YCXLMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public YCXLMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.huanPaiType = this.info.getRule().getOrDefault(RoomRule.RR_MJ_HUAN_PAI_TYPE, 0);
        this.dingPiaoType = this.info.getRule().getOrDefault(RoomRule.RR_MJ_DING_PIAO, 0);
        this.dingque = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_WITH_DING_QUE);
        this.isTen = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_WITH_TIMER);
        if (isTen) this.timeout = 10 * 1000;
        this.fangGangScore = 1;
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
            for (int j = MahjongUtil.MJ_1_TONG; j <= MahjongUtil.MJ_9_TONG; ++j) {
                this.allCard.add((byte) j);
            }
        }

        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doStart1() {
        if (1 == this.huanPaiType) {
            this.beginShuaiPai();
        } else {
            this.beginHuanPai();
        }
    }
    @Override
    public ErrorCode huanPai(IPlayer player, List<Byte> cards) {
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法换牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法换牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (null == cards || 3 != cards.size()) {
            Logs.ROOM.warn("%s %s 牌不对:%s, 无法换牌", this, player, cards);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongHuanPaiActionByYCXL) {
            IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == mahjongPlayer || mahjongPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法换牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            int card = cards.get(0) & 0x3F;
            card |= ((cards.get(1) & 0x3F) << 6);
            card |= ((cards.get(2) & 0x3F) << 12);
            ErrorCode err = ((MahjongHuanPaiActionByYCXL) action).huanPai(mahjongPlayer, card);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是换牌动作, 无法换牌", this);
        return ErrorCode.REQUEST_INVALID;
    }
    @Override
    public void beginHuanPai() {
        MahjongHuanPaiActionByYCXL action = new MahjongHuanPaiActionByYCXL(this, HUANG_PAI_TIMEOUT);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addPlayer(player);
        }
        this.addAction(action);
        PCLIMahjongNtfBeginHuanPai info = new PCLIMahjongNtfBeginHuanPai();
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_HUAN_PAI, info,false);
    }
    @Override
    public void endHuanPai() {
        HuanPaiRecordAction action = ((MahjongRecord) this.getRecord()).addHuanPaiAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            List<Byte> handCard = new ArrayList<>();
            player.addHandCardTo(handCard);
            ((YCXLMahjongPlayer) player).setHuanPai(true);
            action.addHuanPai(player.getUid(), handCard);
        }
        if (2 == this.huanPaiType) {
            this.beginShuaiPai();
        } else {
            if (this.dingque) {
                this.beginDingQue();
            } else {
                this.doStartTake();
            }
        }
    }

    @Override
    public void endShuaiPai() {
        super.endShuaiPai();
        if (this.dingque) {
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
    protected void doStartTake() {
        this.isStartTake = true;
        IMahjongPlayer bankerPlayer = (IMahjongPlayer) this.allPlayer[this.bankerIndex];
        this.generateTingInfo(bankerPlayer);
        MahjongTakeAction action = this.getTakeAction(bankerPlayer, bankerPlayer.getLastHandCard());
        this.addAction(action);
        this.doSendStarTake(bankerPlayer);
    }

    @Override
    public boolean isCanTakeCard(IMahjongPlayer player, byte card) {
        if(super.isCanTakeCard(player, card)) {
            if (player.isHu()) {
                return player.getLastFumbleCard() == card;
            }
            if (-1 == ((IDingQue) player).getQue() || ((IDingQue) player).getQue() == MahjongUtil.getColor(card)) {
                return true;
            }
            return true;
        }
       return  super.isCanTakeCard(player, card);
    }

    public boolean hasQueCard(IMahjongPlayer player) {
        Set<Integer> temp = new HashSet<>();
        for (CPGNode node : player.getCPGNode()) {
            if (CPGNode.EType.ANY_THREE != node.getType()) {
                if (!temp.contains(MahjongUtil.getColor((node.getCard1())))) {
                    temp.add(MahjongUtil.getColor(node.getCard1()));
                }
            }

        }
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (player.getHandCardRaw()[i] < 1) {
                continue;
            }
            int color = MahjongUtil.getColor((byte) i);
            if (!temp.contains(color))
                temp.add(color);
            if (temp.size() >= 3)
                return true;
        }

        return false;
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
            byte takeCard = -1;
            for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                if (player.getHandCardRaw()[i] < 1) {
                    continue;
                }
                if (((YCXLMahjongPlayer) player).getQue() == MahjongUtil.getColor((byte) i)) {
                    takeCard = (byte) i;
                    break;
                }
            }
            MahjongTakeAction action = null;
            if (takeCard != -1) {
                action = this.getTakeAction(player, takeCard);

            } else {
                action = this.getTakeAction(player, card);
            }
            this.addAction(action);
        }
    }
    @Override
    public void doSendFumble(IMahjongPlayer player, byte card) {
        PCLIMahjongNtfFumbleInfoYCXL info = new PCLIMahjongNtfFumbleInfoYCXL();
        info.uid = player.getUid();
        info.index = player.getIndex();
        info.auto = true;
        info.value = card;
        info.remainCard = this.allCard.size();
        info.bar=this.isBar(player,card,false);
        player.addHandCardTo(info.handCard, card);
        info.tingInfo.putAll(player.getTingInfo().getTing());
        player.send(CommandId.CLI_NTF_MAHJONG_FUMBLE, info);

        info = new PCLIMahjongNtfFumbleInfoYCXL();
        info.uid = player.getUid();
        info.index = player.getIndex();
        info.auto = true;
        info.remainCard = this.allCard.size();
        info.value = -1;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_FUMBLE, info, player.getUid());

        ((MahjongRecord) this.getRecord()).addFumbleRecordAction(player.getUid(), card);
    }

    /**
     * 打牌
     *
     * @param player
     * @param defaultTakeCard
     * @return
     */
    @Override
    protected MahjongTakeAction getTakeAction(IMahjongPlayer player, byte defaultTakeCard) {
        long timeout = player.getTimeout(this.timeout);
        if (this.isLastCard) {
            timeout = BaseMahjongRoom.LAST_CARD_TAKE_TIMEOUT;
        } else if (player.isAutoTake()) {
            if (this.isBar(player,defaultTakeCard,false)) {
                timeout = 10 * 1000L;
            } else {
                timeout = BaseMahjongRoom.AUTO_TAKE_TIMEOUT;
            }
        }
        MahjongTakeAction action = new MahjongTakeAction(this, player, timeout);
        action.setParam(defaultTakeCard);
        return action;
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        //Logs.ROOM.info("$$$$$$$$$$$%d : %s",player.getUid(),player.getAllPaiXing());
        player.clearPaiXing();
        if (dingque) {
            if (((IDingQue) player).hasQueCard()) {
                return false;
            }
        } else {
            if (this.hasQueCard(player)) return false;
        }
        if (!ziMo && player.isPassCard(EActionOp.HU, huCard)) {
            return false;
        }
//        TingInfo tingInfo = player.getTingInfo();
//        if (tingInfo.isBuild()) {
//            if (ziMo) {
//                return tingInfo.isHuCard(player.isHu() ? player.getFirstHuTakeCard() : huCard, huCard);
//            }
//            return tingInfo.isHuCard(player.isHu() ? player.getFirstHuTakeCard() : player.getLastTakeCard(), huCard);
//        }
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        int bumpOrBarWithoutDarkCnt = 0;
        int barCnt = 0;
        List<CPGNode> cpgNodes = player.getCPGNode();
        for (CPGNode node : cpgNodes) {
            if (CPGNode.EType.ANY_THREE == node.getType()) {
                continue;
            }
            tempCards.get()[node.getCard1()] += 1;
            if (CPGNode.EType.BUMP == node.getType()) {
                ++bumpOrBarWithoutDarkCnt;
            } else if (CPGNode.EType.BAR_AN == node.getType()
                    || CPGNode.EType.BAR_MING == node.getType()
                    || CPGNode.EType.BAR_FANG == node.getType()) {
                ++barCnt;
                if (CPGNode.EType.BAR_AN != node.getType()) {
                    ++bumpOrBarWithoutDarkCnt;
                }
            }
        }
        // 3. 对对胡
        boolean isPPH = MahjongUtil.isPengPengHu(player.getHandCardRaw());
        // 4. 清一色
        boolean isQingYiSe = MahjongUtil.isQingYiSe(tempCards.get());
        // 1. 屁胡
        if (MahjongUtil.isHu(player.getHandCardRaw(), player.getCPGNodeCnt(), false)) {
            // 2. 门清 玩家的手牌为任意的两门，并且没有碰牌或杠牌（暗杠除外）
            // 5. 清对
            if (isQingYiSe && isPPH) {
                if (0 == bumpOrBarWithoutDarkCnt && ziMo) {
                    player.addPaiXing(EPaiXing.YCXL_QING_DUI_MEN_QING);
                    return true;
                }
                player.addPaiXing(EPaiXing.YCXL_QING_DUI);
                return true;
            }
            if (isQingYiSe) {
                if (0 == bumpOrBarWithoutDarkCnt && ziMo) {
                    player.addPaiXing(EPaiXing.YCXL_QING_YI_SE_MEN_QING);
                    return true;
                }
                player.addPaiXing(EPaiXing.YCXL_QING_YI_SE);
                return true;
            }
            if (isPPH) {
                if (0 == bumpOrBarWithoutDarkCnt && ziMo) {
                    player.addPaiXing(EPaiXing.YCXL_DUIDUI_HU_MEN_QING);
                    return true;
                }
                player.addPaiXing(EPaiXing.YCXL_DUIDUI_HU);
                return true;
            }
            if (0 == bumpOrBarWithoutDarkCnt && ziMo) {
                player.addPaiXing(EPaiXing.YCXL_MEN_QING);
                return true;
            }
            player.addPaiXing(EPaiXing.YCXL_NORMAL);
            return true;
        }

        return false;
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
        EPaiXing px = player.getAllPaiXing().get(0);
        return px.getDefaultValue();
    }


    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
       // Logs.ROOM.info("##########%s",player1.getAllPaiXing());

        super.onHu(takePlayer, player1, player2, player3, huCard);

        HuRecordAction huRecordAction = ((MahjongRecord) this.getRecord()).addHuRecordAction(takePlayer.getUid());

        PCLIMahjongNtfHuInfo info = new PCLIMahjongNtfHuInfo();
        info.takePlayerUid = takePlayer.getUid();
        info.huCard = huCard;

        if (takePlayer.getUid() == player1.getUid()) {
            // 自摸
            if (EActionOp.BAR == this.prevAction) {
                // 杠上花
            }
            EPaiXing px = player1.getAllPaiXing().get(0);
            player1.addHandCard(huCard);
            player1.delHandCard(huCard);
            //++this.deskCard[huCard];
            int fang = px.getDefaultValue();
            player1.addHu(px, huCard);
            if(((YCXLMahjongPlayer) player1).getAllHuCard().size() == 0) {
                TingInfo ting = player1.getTingInfo();
                ((YCXLMahjongPlayer) player1).setAllHuCard(ting.getHuCard(player1.getLastTakeCard()));
            }
            huRecordAction.addHu(player1.getUid(), true, px, huCard, px.getDefaultValue());

            int darkBarCnt = player1.getScore(Score.MJ_CUR_AN_GANG_CNT, false) * 2;
            int otherBarCnt = player1.getScore(Score.MJ_CUR_MING_GANG_CNT, false);

            PCLIMahjongNtfHuInfo.HuInfo huInfo = new PCLIMahjongNtfHuInfo.HuInfo();
            huInfo.paiXing = px.getClientValue();
            huInfo.paiXingValue = fang;
            info.allHuInfo.put(player1.getUid(), huInfo);

            PCLIMahjongNtfHuInfo.ScoreInfo scoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
            info.allScoreInfo.put(player1.getUid(), scoreInfo);

            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer temp = (IMahjongPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                if (player1.getUid() == temp.getUid()) {
                    continue;
                }

                int tempDarkBarCnt = temp.getScore(Score.MJ_CUR_AN_GANG_CNT, false) * 2;
                int tempOtherBarCnt = temp.getScore(Score.MJ_CUR_MING_GANG_CNT, false);

                int extraGangScore = darkBarCnt + otherBarCnt + tempDarkBarCnt + tempOtherBarCnt;

                temp.addScore(Score.MJ_CUR_HU_SCORE, -fang, false);
                temp.addScore(Score.MJ_CUR_PIAO_SCORE, -this.dingPiaoType * 2, false);
                temp.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, -extraGangScore, false);
                player1.addScore(Score.MJ_CUR_HU_SCORE, fang, false);
                player1.addScore(Score.MJ_CUR_PIAO_SCORE, this.dingPiaoType * 2, false);
                player1.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, extraGangScore, false);

                int score = (fang + this.dingPiaoType * 2 + extraGangScore) * endPoint;
                int score1 = fang + this.dingPiaoType * 2 + extraGangScore;
                scoreInfo.paiXingValue += score;
                player1.addScore(Score.ACC_TOTAL_SCORE, this.getScore(score1), true);
                temp.addScore(Score.ACC_TOTAL_SCORE, this.getScore(-score1), true);
                player1.addScore(Score.SCORE,  this.getScore(score1), false);
                temp.addScore(Score.SCORE,this.getScore(-score1), false);

                PCLIMahjongNtfHuInfo.ScoreInfo tempScoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
                tempScoreInfo.paiXingValue -= score;
                tempScoreInfo.totalScore = this.getFormatScore(temp.getScore());
                info.allScoreInfo.put(temp.getUid(), tempScoreInfo);

                huRecordAction.addScore(temp.getUid(), -score);
                huRecordAction.addScore(player1.getUid(), score);
            }
            scoreInfo.totalScore = this.getFormatScore(player1.getScore());
        } else {
            // 点炮
            if (EActionOp.BAR == this.prevAction) {
                // 杠开
            }

            PCLIMahjongNtfHuInfo.ScoreInfo scoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
            info.allScoreInfo.put(takePlayer.getUid(), scoreInfo);

            int darkBarCnt = takePlayer.getScore(Score.MJ_CUR_AN_GANG_CNT, false) * 2;
            int otherBarCnt = takePlayer.getScore(Score.MJ_CUR_MING_GANG_CNT, false);

            if (null != player1) {
                EPaiXing px = player1.getAllPaiXing().get(0);
                player1.addHandCard(huCard);
                player1.addHu(takePlayer.getUid(), px, huCard);
                if(((YCXLMahjongPlayer) player1).getAllHuCard().size() == 0&&player1.getFumbleCnt()>0) {
                    TingInfo ting = player1.getTingInfo();
                    ((YCXLMahjongPlayer) player1).setAllHuCard(ting.getHuCard(player1.getLastTakeCard()));
                }
                player1.delHandCard(huCard);
                int fang = px.getDefaultValue();

                int tempDarkBarCnt = player1.getScore(Score.MJ_CUR_AN_GANG_CNT, false) * 2;
                int tempOtherBarCnt = player1.getScore(Score.MJ_CUR_MING_GANG_CNT, false);

                int extraGangScore = darkBarCnt + otherBarCnt + tempDarkBarCnt + tempOtherBarCnt;


                player1.addScore(Score.MJ_CUR_HU_SCORE, fang, false);
                player1.addScore(Score.MJ_CUR_PIAO_SCORE, this.dingPiaoType * 2, false);
                player1.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, extraGangScore, false);
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -fang, false);
                takePlayer.addScore(Score.MJ_CUR_PIAO_SCORE, -this.dingPiaoType * 2, false);
                takePlayer.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, -extraGangScore, false);

                PCLIMahjongNtfHuInfo.HuInfo huInfo = new PCLIMahjongNtfHuInfo.HuInfo();
                huInfo.paiXing = px.getClientValue();
                huInfo.paiXingValue = fang;
                info.allHuInfo.put(player1.getUid(), huInfo);

                int score = (fang + this.dingPiaoType * 2 + extraGangScore) * endPoint;
                int score1 = fang + this.dingPiaoType * 2 + extraGangScore;
                player1.addScore(Score.ACC_TOTAL_SCORE, this.getScore(score1), true);
                takePlayer.addScore(Score.ACC_TOTAL_SCORE, this.getScore(-score1), true);
                player1.addScore(Score.SCORE,  this.getScore(score1), false);
                takePlayer.addScore(Score.SCORE,  this.getScore(-score1), false);
                PCLIMahjongNtfHuInfo.ScoreInfo tempScoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
                tempScoreInfo.paiXingValue += score;
                tempScoreInfo.totalScore = this.getFormatScore(player1.getScore());
                info.allScoreInfo.put(player1.getUid(), tempScoreInfo);

                scoreInfo.paiXingValue -= score;

                huRecordAction.addHu(player1.getUid(), false, px, huCard, px.getDefaultValue());
                huRecordAction.addScore(takePlayer.getUid(), -score);
                huRecordAction.addScore(player1.getUid(), score);
            }
            if (null != player2) {
                EPaiXing px = player2.getAllPaiXing().get(0);
                player2.addHandCard(huCard);
                player2.addHu(takePlayer.getUid(), px, huCard);
                if(((YCXLMahjongPlayer) player2).getAllHuCard().size() == 0&&player2.getFumbleCnt()>0) {
                    TingInfo ting = player2.getTingInfo();
                    ((YCXLMahjongPlayer) player2).setAllHuCard(ting.getHuCard(player2.getLastTakeCard()));
                }
                player2.delHandCard(huCard);
                int fang = px.getDefaultValue();

                int tempDarkBarCnt = player2.getScore(Score.MJ_CUR_AN_GANG_CNT, false) * 2;
                int tempOtherBarCnt = player2.getScore(Score.MJ_CUR_MING_GANG_CNT, false);

                int extraGangScore = darkBarCnt + otherBarCnt + tempDarkBarCnt + tempOtherBarCnt;


                player2.addScore(Score.MJ_CUR_HU_SCORE, fang, false);
                player2.addScore(Score.MJ_CUR_PIAO_SCORE, this.dingPiaoType * 2, false);
                player2.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, extraGangScore, false);
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -fang, false);
                takePlayer.addScore(Score.MJ_CUR_PIAO_SCORE, -this.dingPiaoType * 2, false);
                takePlayer.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, -extraGangScore, false);

                PCLIMahjongNtfHuInfo.HuInfo huInfo = new PCLIMahjongNtfHuInfo.HuInfo();
                huInfo.paiXing = px.getClientValue();
                huInfo.paiXingValue = fang;
                info.allHuInfo.put(player2.getUid(), huInfo);

                int score = (fang + this.dingPiaoType * 2 + extraGangScore) * endPoint;
                int score1 = fang + this.dingPiaoType * 2 + extraGangScore;
                player2.addScore(Score.ACC_TOTAL_SCORE, this.getScore(score1), true);
                takePlayer.addScore(Score.ACC_TOTAL_SCORE, this.getScore(-score1), true);
                player2.addScore(Score.SCORE, this.getScore(score1), false);
                takePlayer.addScore(Score.SCORE, this.getScore(-score1), false);

                PCLIMahjongNtfHuInfo.ScoreInfo tempScoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
                tempScoreInfo.paiXingValue += score;
                tempScoreInfo.totalScore = this.getFormatScore(player2.getScore());
                info.allScoreInfo.put(player2.getUid(), tempScoreInfo);

                scoreInfo.paiXingValue -= score;

                huRecordAction.addHu(player2.getUid(), false, px, huCard, px.getDefaultValue());
                huRecordAction.addScore(takePlayer.getUid(), -score);
                huRecordAction.addScore(player2.getUid(), score);
            }
            if (null != player3) {
                EPaiXing px = player3.getAllPaiXing().get(0);
                player3.addHandCard(huCard);
                player3.addHu(takePlayer.getUid(), px, huCard);
                if(((YCXLMahjongPlayer) player3).getAllHuCard().size() == 0&&player3.getFumbleCnt()>0) {
                    TingInfo ting = player3.getTingInfo();
                    ((YCXLMahjongPlayer) player3).setAllHuCard(ting.getHuCard(player3.getLastTakeCard()));
                }
                player3.delHandCard(huCard);

                int fang = px.getDefaultValue();

                int tempDarkBarCnt = player3.getScore(Score.MJ_CUR_AN_GANG_CNT, false) * 2;
                int tempOtherBarCnt = player3.getScore(Score.MJ_CUR_MING_GANG_CNT, false);

                int extraGangScore = darkBarCnt + otherBarCnt + tempDarkBarCnt + tempOtherBarCnt;


                player3.addScore(Score.MJ_CUR_HU_SCORE, fang, false);
                player3.addScore(Score.MJ_CUR_PIAO_SCORE, this.dingPiaoType * 2, false);
                player3.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, extraGangScore, false);
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -fang, false);
                takePlayer.addScore(Score.MJ_CUR_PIAO_SCORE, -this.dingPiaoType * 2, false);
                takePlayer.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, -extraGangScore, false);

                PCLIMahjongNtfHuInfo.HuInfo huInfo = new PCLIMahjongNtfHuInfo.HuInfo();
                huInfo.paiXing = px.getClientValue();
                huInfo.paiXingValue = fang;
                info.allHuInfo.put(player3.getUid(), huInfo);

                int score = (fang + this.dingPiaoType * 2 + extraGangScore) * endPoint;
                int score1 = fang + this.dingPiaoType * 2 + extraGangScore;
                player3.addScore(Score.ACC_TOTAL_SCORE, this.getScore(score1), true);
                takePlayer.addScore(Score.ACC_TOTAL_SCORE, this.getScore(-score1), true);
                player3.addScore(Score.SCORE,  this.getScore(score1), false);
                takePlayer.addScore(Score.SCORE, this.getScore(-score1), false);


                PCLIMahjongNtfHuInfo.ScoreInfo tempScoreInfo = new PCLIMahjongNtfHuInfo.ScoreInfo();
                tempScoreInfo.paiXingValue += score;
                tempScoreInfo.totalScore = this.getFormatScore(player3.getScore());
                info.allScoreInfo.put(player3.getUid(), tempScoreInfo);

                scoreInfo.paiXingValue -= score;

                huRecordAction.addHu(player3.getUid(), false, px, huCard, px.getDefaultValue());
                huRecordAction.addScore(takePlayer.getUid(), -score);
                huRecordAction.addScore(player3.getUid(), score);
            }

            scoreInfo.totalScore = this.getFormatScore(takePlayer.getScore());
        }

        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_HU_INFO, info);

        if (this.onCheckOver()) {
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
    }

    @Override
    public void onHuangZhuang(boolean next) {
        // 杠分, 胡分, 花猪分, 票分, 大叫分, 额外杠分
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            boolean isHuaZhu = false;
            boolean m_isTing = false;
            //查看玩家是否听牌
            for (int j = 1; j < MahjongUtil.MJ_CARD_KINDS; j++) {
                if (this.isHu(player,player.getUid(),(byte)j)) {
                    m_isTing = true;
                    break;
                }
            }
            if (!m_isTing) {
                if (dingque) {
                    isHuaZhu = ((IDingQue) player).hasQueCard();
                } else {
                    isHuaZhu = this.hasQueCard(player);
                }
                // 未听牌
                // 查花猪 * 3, 查大叫
                for (int j = 0; j < this.playerNum; ++j) {
                    IMahjongPlayer temp = (IMahjongPlayer) this.allPlayer[j];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    if (temp.getUid() == player.getUid()) {
                        continue;
                    }
                    boolean m_istempTing = false;
                    //查看玩家是否听牌
                    for (int k = 1; k < MahjongUtil.MJ_CARD_KINDS; k++) {
                        if (this.isHu(temp,temp.getUid(),(byte)k)) {
                            m_istempTing = true;
                            break;
                        }
                    }
                    if (!m_istempTing) {
                        continue;
                    }
                    int tempFang = temp.getTingInfo().getMaxFang();
                    if (tempFang < 1) {
                        continue;
                    }
                    ((YCXLMahjongPlayer) temp).setTing(true);
                    int tempDarkBarCnt = temp.getScore(Score.MJ_CUR_AN_GANG_CNT, false) * 2;
                    int tempOtherBarCnt = temp.getScore(Score.MJ_CUR_MING_GANG_CNT, false);
                    int darkBarCnt = player.getScore(Score.MJ_CUR_AN_GANG_CNT, false) * 2;
                    int otherBarCnt = player.getScore(Score.MJ_CUR_MING_GANG_CNT, false);
                    temp.addScore(Score.MJ_CUR_PIAO_SCORE, this.dingPiaoType * 2, false);
                    player.addScore(Score.MJ_CUR_PIAO_SCORE, -this.dingPiaoType * 2, false);
                    int extraGangScore = darkBarCnt + otherBarCnt + tempDarkBarCnt + tempOtherBarCnt;
                    tempFang = (tempFang + extraGangScore + this.dingPiaoType * 2) * endPoint;
                    if (isHuaZhu) {
                        // 花猪
                        temp.addScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, 3 * tempFang, false);
                        player.addScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, -3 * tempFang, false);
                        temp.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, extraGangScore, false);
                        player.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, -extraGangScore, false);
                        ((IYCXLMahjongPlayer) player).setChaHuaZhu(true);
                        ((IYCXLMahjongPlayer) player).addChaValue(-3 * tempFang);
                        ((IYCXLMahjongPlayer) temp).addChaValue(3 * tempFang);
                        temp.addScore(Score.ACC_TOTAL_SCORE, tempFang * 100, true);
                        player.addScore(Score.ACC_TOTAL_SCORE, -tempFang * 100, true);
                        temp.addScore(Score.SCORE, tempFang * 100, false);
                        player.addScore(Score.SCORE, -tempFang * 100, false);

                    } else {
                        temp.addScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, tempFang, false);
                        player.addScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, -tempFang, false);
                        temp.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, extraGangScore, false);
                        player.addScore(Score.MJ_CUR_EXTRA_GANG_SCORE, -extraGangScore, false);
                        ((IYCXLMahjongPlayer) player).setChaDaJiao(true);
                        ((IYCXLMahjongPlayer) player).addChaValue(-tempFang);
                        ((IYCXLMahjongPlayer) temp).addChaValue(tempFang);
                        temp.addScore(Score.ACC_TOTAL_SCORE, tempFang * 100, true);
                        player.addScore(Score.ACC_TOTAL_SCORE, -tempFang * 100, true);
                        temp.addScore(Score.SCORE, tempFang * 100, false);
                        player.addScore(Score.SCORE, -tempFang * 100, false);
                    }
                }
            }
        }

        YCXLResultRecordAction resultRecordAction = (YCXLResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            YCXLResultRecordAction.PlayerInfo playerInfo = new YCXLResultRecordAction.PlayerInfo();

            playerInfo.setChaDaJiao(((IYCXLMahjongPlayer) player).isChaDaJiao());
            playerInfo.setChaHuaZhu(((IYCXLMahjongPlayer) player).isChaHuZhu());
            playerInfo.setChaValue(((IYCXLMahjongPlayer) player).getChaValue());

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


            YCXLResultRecordAction.ScoreInfo scoreInfo = new YCXLResultRecordAction.ScoreInfo();
            scoreInfo.setPiaoScore(player.getScore(Score.MJ_CUR_PIAO_SCORE, false));
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false)
                    + player.getScore(Score.MJ_CUR_EXTRA_GANG_SCORE, false)
                    + player.getScore(Score.MJ_CUR_CHA_HUA_ZHU_SCORE, false)
                    + player.getScore(Score.MJ_CUR_CHA_DA_JIAO_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_EXTRA_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        this.bankerIndex = (++this.bankerIndex) % this.playerNum;

        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (((IDingQue) player).isQueCard(takeCard)) {
            return false;
        }
        if (player.isHu()) {
            if (fangGang) {
                return false;
            }
            if (player.hasHandCard(takeCard, 4)) {
                player.delHandCard(takeCard, 4);

                Set<Byte> allHuCard = ((YCXLMahjongPlayer)player).getAllHuCard();
                boolean value = true;
                do {
                    if (allHuCard.size()==0) {
                        value = false;
                        break;
                    }
                    for (Byte huCard : allHuCard) {
                        player.addHandCard(huCard);
                        if (!MahjongUtil.isHu(player.getHandCardRaw(), player.getCPGNodeCnt() + 1, false)) {
                            value = false;
                        }
                        player.delHandCard(huCard);
                        if (!value) {
                            break;
                        }
                    }
                } while (false);

                player.addHandCard(takeCard, 4);
                return value;
            }
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if (((IDingQue) player).isQueCard(takeCard)) {
            return false;
        }
        if (player.isHu()) {
            return false;
        }
        return super.isBump(player, takeCard);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        return false;
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new YCXLMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    protected boolean onCheckOver() {
        if (3 == this.getCurPlayerCnt()) {
            return this.allCard.size() <= 6;
        }
        return this.allCard.isEmpty();
    }

    @Override
    protected String getCurScore(IMahjongPlayer player) {
        return this.getFormatScore(player.getScore()
                + (player.getScore(Score.MJ_CUR_GANG_SCORE, false)
                + player.getScore(Score.MJ_CUR_HU_SCORE, false)
                + player.getScore(Score.MJ_CUR_PIAO_SCORE, false)
                + player.getScore(Score.MJ_CUR_EXTRA_GANG_SCORE, false)) * endPoint * 100);
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new YCXLMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByYCXL info = new PCLIMahjongNtfGameOverInfoByYCXL();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByYCXL.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByYCXL.PlayerInfo();
            playerInfo.isChaDaJiao = ((IYCXLMahjongPlayer) player).isChaDaJiao();
            playerInfo.isChaHuaZhu = ((IYCXLMahjongPlayer) player).isChaHuZhu();
            playerInfo.chaValue = (((IYCXLMahjongPlayer) player).getChaValue());
            playerInfo.ting = ((YCXLMahjongPlayer) player).isTing();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByYCXL.ScoreInfo();
            ((PCLIMahjongNtfGameOverInfoByYCXL.ScoreInfo) playerInfo.score).piaoScore = player.getScore(Score.MJ_CUR_PIAO_SCORE, false);
            playerInfo.score.fangScore =player.getScore(Score.SCORE, false)/100;
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_EXTRA_GANG_SCORE, false);
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

        PCLIMahjongNtfDeskInfoByYCXL deskInfo = new PCLIMahjongNtfDeskInfoByYCXL();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = null == mahjongPlayer ? 0 : mahjongPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        deskInfo.curPlayerId=this.curOpIndex!=-1?this.allPlayer[this.curOpIndex].getUid():0;
        deskInfo.bankerPlayerUid = -1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest() ? -1L : this.allPlayer[this.bankerIndex].getUid();
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();
        for(byte k=0;k<MahjongUtil.MJ_CARD_KINDS;k++){
            deskInfo.deskCard[k]=(short)this.deskCard[k];
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
                PCLIMahjongNtfDeskInfoByYCXL.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByYCXL.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore());
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                deskPlayerInfo.huanpai = ((YCXLMahjongPlayer) other).isHuanPai();
                deskPlayerInfo.huanPaiRecord=((YCXLMahjongPlayer) other).getHuahuanPaiRecordnpai();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByYCXL.CardNode cardNode = new PCLIMahjongNtfDeskInfoByYCXL.CardNode();
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
                    deskPlayerInfo.tingInfo.putAll(other.getTingInfo().getTing());
                    deskPlayerInfo.lastTakeCard=other.getLastTakeCard();
                }
                deskPlayerInfo.queColor = ((IDingQue) other).getQue();
                deskPlayerInfo.listHuInfo.add(other.getHuList());
                other.addHuCardTo(deskPlayerInfo.huCard);
                deskInfo.other.put(other.getUid(), deskPlayerInfo);
            }
        } finally {
            this.rwLock.readLock().unlock();
        }
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }
}
