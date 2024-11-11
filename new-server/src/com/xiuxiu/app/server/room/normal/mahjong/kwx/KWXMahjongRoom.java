package com.xiuxiu.app.server.room.normal.mahjong.kwx;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByKWX;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByKWX;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong.*;
import com.xiuxiu.app.server.room.normal.mahjong.action.MahjongFlutterWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong.action.MahjongShuKanWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.player.mahjong.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;
import com.xiuxiu.app.server.room.record.mahjong.HuRecordAction;
import com.xiuxiu.app.server.room.record.mahjong.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong.ResultRecordAction;
import com.xiuxiu.app.server.table.TbKWXFangManager;
import com.xiuxiu.core.Pair;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class KWXMahjongRoom extends MahjongRoom {

    /**
     * 对亮对番
     */
    protected boolean dldf;

    /** 上一局是否是一炮多响 */
    protected boolean prevTwoWiner = false;

    public KWXMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public KWXMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.switchBright = true;
        this.switchHalfBright = this.isBanLiang();
        this.minHuValue = 2;
        this.detectionIP = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.RR_DETECTION_IP.getValue());
        this.dldf = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.DLDF.getValue());
    }

    @Override
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB) {
            this.allCard.addAll(CardLibraryManager.I.getMahjongCard());
            return;
        }
        for (int i = 0; i < 4; ++i) {
            for (int j = 10; j <= 18; ++j) {
                this.allCard.add((byte) j);
            }
            for (int j = 19; j <= 27; ++j) {
                this.allCard.add((byte) j);
            }
            for (int j = 32; j <= 34; ++j) {
                this.allCard.add((byte) j);
            }
        }
        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doStart1() {
        if (this.isShuKan()) {
            this.beginShuKan();
        } else {
            this.isBeginXuanPiao();
        }
    }

    public void isBeginXuanPiao(){
         if((this.xuanPiaoType == 3 && this.curBureau == 1) ||(this.xuanPiaoType!=3)) {
             this.beginXuanPiao();
         }else{
             endXuanPiao();
         }
    }
    @Override
    public void endShuKan() {
        this.isBeginXuanPiao();
    }

    @Override
    public void endXuanPiao() {
        this.doStartTake();
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        IAction action = this.action.isEmpty() ? null : this.action.peek();

        MahjongPlayer temp = (MahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == temp && !this.watchList.contains(player.getUid())) {
            return;
        }

        PCLIMahjongNtfDeskInfoByKWX deskInfo = new PCLIMahjongNtfDeskInfoByKWX();
        deskInfo.remainCard = this.allCard.size();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.crap1 = this.crap1;
        deskInfo.crap2 = this.crap2;
        deskInfo.curBureau = temp.getRoomPlayerHelper().getCurBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        deskInfo.bankerPlayerUid = -1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest() ? -1L : this.allPlayer[this.bankerIndex].getUid();
        deskInfo.flutterWait = null == action ? false : (action instanceof MahjongFlutterWaitAction);
        //deskInfo.timeout = this.timeout > 0 ? this.timeout : Constant.ROOM_TAKE_TIMEOUT;
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();


        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
//                int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
                for (int i = 0; i < this.playerNum; ++i) {
                    MahjongPlayer other = (MahjongPlayer) this.allPlayer[i];
                    if (null == other || other.isGuest()) {
                        continue;
                    }
                    deskInfo.allOnlineState.put(other.getUid(), other.isOffline() ? false : true);
                    PCLIMahjongNtfDeskInfoByKWX.DeskPlayerInfoByKWX deskPlayerInfo = new PCLIMahjongNtfDeskInfoByKWX.DeskPlayerInfoByKWX();
                    deskPlayerInfo.flutter = other.getPiaoScore();
                    deskPlayerInfo.shuKan = other.getShuKanPoint();
                    if(other.getShuKanPoint().size()==0){
                        deskPlayerInfo.shuKan.add(0);
                    }
                   // deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                    deskPlayerInfo.totalScore = this.getFormatScore((int)this.getPlayerGold(other));
                    for (int j = 1; j < MahjongConstant.MJ_CARD_KINDS; ++j) {
                        deskPlayerInfo.remainCard += other.getHandCard()[j];
                    }
                    other.addBrightCardTo(deskPlayerInfo.brightCard);
                    for (MahjongCardNode node : other.getCpgCards()) {
                        PCLIMahjongNtfDeskInfo.CardNode cardNode = new PCLIMahjongNtfDeskInfo.CardNode();
                        cardNode.type = node.type;
                        if (MahjongConstant.MJ_NODE_TYPE_BUMP == node.type) {
                            cardNode.a = node.card;
                            cardNode.b = node.card;
                            cardNode.c = node.card;
                        } else if (MahjongConstant.MJ_NODE_TYPE_BAR == node.type || MahjongConstant.MJ_NODE_TYPE_BAR_DARK == node.type) {
                            cardNode.a = node.card;
                            cardNode.b = node.card;
                            cardNode.c = node.card;
                            cardNode.d = node.card;
                        } else if (MahjongConstant.MJ_NODE_TYPE_EAT == node.type) {
                            cardNode.a = node.card;
                            cardNode.b = (byte) (node.card + 1);
                            cardNode.c = (byte) (node.card + 2);
                        }
                        deskPlayerInfo.cpgCard.add(cardNode);
                    }
                    deskPlayerInfo.deskCard.addAll(other.getDeskCards());
                    deskPlayerInfo.laiZiAndPiCard.addAll(other.getLaiZiAndPiCard());
                    if (null != action) {
                        if (action instanceof MahjongTakeAction) {
                            if (other.getUid() == ((MahjongTakeAction) action).getRoomPlayer().getUid()) {
                                if (null == temp || temp.isGuest()) {
                                    deskPlayerInfo.fumble = 0;
                                } else {
                                    deskPlayerInfo.fumble = temp.getUid() == other.getUid() ? ((MahjongTakeAction) action).getCardValue() : 0;
                                }
                            }
                        } else if (action instanceof MahjongFlutterWaitAction) {
                            deskPlayerInfo.flutter = ((MahjongFlutterWaitAction) action).getFlutter(other.getUid());
                        } else if (action instanceof MahjongShuKanWaitAction) {
                            deskPlayerInfo.shuKan = ((MahjongShuKanWaitAction) action).getShuKan(other.getUid());
                        }
                    }
                    if (null != temp && !temp.isGuest()) {
                        if (temp.getUid() == other.getUid()) {
                            other.addHandCardTo(deskPlayerInfo.card, deskPlayerInfo.fumble);
                            if (temp.isBright()) {
                                for (byte j = 1; j < MahjongConstant.MJ_CARD_KINDS; ++j) {
                                    if (temp.getKouCard()[j] > 0) {
                                        deskPlayerInfo.kou.add(j);
                                    }
                                }
                            }
                        }
                    }
                    if (other.isBright()) {
                        for (Map.Entry<Byte, Integer> ting : other.getHalfBrightInfo().huCard.entrySet()) {
                            deskPlayerInfo.pao.put(ting.getKey(), this.paoZiCnt[ting.getKey()]);
                        }
                    }
                    deskInfo.other.put(other.getUid(), deskPlayerInfo);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override
    protected void doFinish(boolean isNormal, boolean isNewBureau) {
        int count = 0;
        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer player = (MahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            ++count;
        }
        if (count > 0) {
            Logs.ROOM.debug("%s finish", this);
            if (!isNormal) {
                // 解散提前结束
                //if (!isNewBureau) {
                //this.sendHuInfo(-1);
                //}
                if (!isNewBureau) {
                    for (int i = 0; i < this.playerNum; ++i) {
                        MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                        if (null == temp || temp.isGuest()) {
                            continue;
                        }
                        temp.addScore(Score.MJ_CALC_GANG_SCORE, temp.getScore(Score.MJ_CUR_GANG_SCORE, false), false);
                        temp.addScore(Score.SCORE, this.getScore(temp.getScore(Score.MJ_CALC_FANG_SCORE, false) + temp.getScore(Score.MJ_CALC_GANG_SCORE, false)), false);
                        temp.addScore(Score.ACC_TOTAL_SCORE, temp.getScore(Score.SCORE, false), true);
                        if (this.isShangLou() && temp.getScore(Score.ACC_TOTAL_SCORE, true) > 0) {
                            temp.getHuInfo().paixing[EHuType.SHANG_LOU.ordinal()] = 1;
                        }
                    }

                    this.record();
                } else if (isNewBureau && this.checkIsDestroy()) {
                    for (int i = 0; i < this.playerNum; ++i) {
                        MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                        if (null == temp || temp.isGuest()) {
                            continue;
                        }
                        temp.addScore(Score.MJ_CALC_GANG_SCORE, temp.getScore(Score.MJ_CUR_GANG_SCORE, false), false);
                        temp.addScore(Score.SCORE, this.getScore(temp.getScore(Score.MJ_CALC_FANG_SCORE, false) + temp.getScore(Score.MJ_CALC_GANG_SCORE, false)), false);
                        temp.addScore(Score.ACC_TOTAL_SCORE, temp.getScore(Score.SCORE, false), true);
                        if (this.isShangLou() && temp.getScore(Score.ACC_TOTAL_SCORE, true) > 0) {
                            temp.getHuInfo().paixing[EHuType.SHANG_LOU.ordinal()] = 1;
                        }
                    }
                }

                //ResultRecordAction resultRecordAction = isNewBureau ? null : ((MahjongRecord) this.record).addResultRecordAction();
                ResultRecordAction resultRecordAction = ((MahjongRecord) this.getRecord()).addResultRecordAction();
                PCLIMahjongNtfGameOverInfoByKWX gameOverInfo = new PCLIMahjongNtfGameOverInfoByKWX();
                gameOverInfo.roomType = this.roomType.ordinal();

                gameOverInfo.bureau = curBureau;
                gameOverInfo.fangPaoUid = -1;
                gameOverInfo.ziMoUid = -1;
                gameOverInfo.next = false;
                for (int i = 0; i < this.playerNum; ++i) {
                    MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    List<Byte> card = new ArrayList<>();
                    temp.addHandCardTo(card);
                    List<Byte> kou = new ArrayList<>();
                    temp.addKouCardTo(kou);
                    gameOverInfo.allCard.put(temp.getUid(), card);
                    gameOverInfo.allCardKou.put(temp.getUid(), kou);

                    HashMap<Integer, Integer> paiXing = new HashMap<>();
                    for (int j = 0, len = EHuType.MAX.ordinal(); j < len; ++j) {
                        int value = temp.getHuInfo().paixing[j];
                        if (0 != value) {
                            paiXing.put(EHuType.values()[j].getValue(), value);
                        }
                    }
                    gameOverInfo.allPaiXing.put(temp.getUid(), paiXing);

                    PCLIMahjongNtfGameOverInfo.FinalResult result = new PCLIMahjongNtfGameOverInfo.FinalResult();
//                    result.anGangCnt = temp.getScore(Score.ACC_MJ_AN_GANG_CNT, true);
//                    result.fangPaoCnt = temp.getScore(Score.ACC_MJ_DIAN_PAO_CNT, true);
//                    result.huCnt = temp.getScore(Score.ACC_MJ_HU_CNT, true);
//                    result.mingGangCnt = temp.getScore(Score.ACC_MJ_MING_GANG_CNT, true);
//                    result.ziMoCnt = temp.getScore(Score.ACC_MJ_ZIMO_CNT, true);
                    result.anGangCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_AN_GANG_CNT);
                    result.fangPaoCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_DIAN_PAO_CNT);
                    result.huCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_HU_CNT);
                    result.mingGangCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_MING_GANG_CNT);
                    result.ziMoCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_ZIMO_CNT);
                    result.score = temp.getScore(Score.ACC_TOTAL_SCORE, true);
                    gameOverInfo.allFinalResult.put(temp.getUid(), result);

                    PCLIMahjongNtfGameOverInfoByKWX.ScoreInfo scoreInfo = new PCLIMahjongNtfGameOverInfoByKWX.ScoreInfo();
                    scoreInfo.barCnt = this.joinBarCnt;
                    scoreInfo.piaoScore = temp.getScore(Score.MJ_CALC_PIAO_SCORE, false);
                    scoreInfo.horseScore = temp.getScore(Score.MJ_CUR_HORSE_SCORE, false);
                    scoreInfo.gangScore = temp.getScore(Score.MJ_CALC_GANG_SCORE, false);
                    scoreInfo.fangScore = temp.getScore(Score.MJ_CALC_FANG_SCORE, false);
                    scoreInfo.huScore = this.getScoreByHu(temp);
                    scoreInfo.score = this.getFormatScore(temp.getScore(Score.SCORE, false));
                    scoreInfo.totalScore = this.getFormatScore(temp.getScore());
                    gameOverInfo.allScore.putIfAbsent(temp.getUid(), scoreInfo);

                    if (null != resultRecordAction) {
                        resultRecordAction.addCard(temp.getUid(), card);
                        resultRecordAction.setDestroy(this.checkIsDestroy());
                        resultRecordAction.setDestroyUid(this.isDestroyUid);
                        resultRecordAction.addCardKou(temp.getUid(), kou);
                        resultRecordAction.addScore(temp.getUid(), new ResultRecordAction.ScoreInfo(scoreInfo.barCnt, scoreInfo.gangScore, scoreInfo.fangScore, scoreInfo.horseScore, scoreInfo.piaoScore, scoreInfo.huScore, scoreInfo.score, scoreInfo.totalScore));
                    }

                    //if (Switch.DEBUG) {
                        Logs.ROOM.error("结算:%s \n%s \n%s \n%s", this, temp, scoreInfo, temp.getScore(Score.ACC_TOTAL_SCORE, true));
                    //}
                }
                if (!isNewBureau) {
                    this.record.save();
                } else if (isNewBureau && this.checkIsDestroy()) {
                    this.record.save();
                    this.record();
                }
                this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, gameOverInfo);
                this.info.setDirty(true);
            }
            this.info.setEndTime(System.currentTimeMillis());
            this.saveRoomScore();
        }
    }

    @Override
    public boolean checkCanTake(MahjongPlayer player, byte card) {
        if (this.liangPaiPlayer.isEmpty()) {
            return true;
        }
        if (player.isBright()) {
            return true;
        }
        if (0 == this.paoZi[card]) {
            return true;
        }
        boolean hasFirstBrightCard = false;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            byte cnt = player.getHandCard()[i];
            if (cnt < 1) {
                continue;
            }
            if (!hasFirstBrightCard && null != this.liangPaiPlayer.get(0).getHalfBrightInfo().huCard.get((byte) i)) {
                hasFirstBrightCard = true;
            }
            if (0 == this.paoZi[i]) {
                return false;
            }
        }
        return null != this.liangPaiPlayer.get(0).getHalfBrightInfo().huCard.get(card) ? true : !hasFirstBrightCard;
    }

    @Override
    protected int hu(MahjongPlayer player, byte huCardValue, boolean addHuCard, byte lastTakeCard) {
        return MahjongUtils.isHuWithKWX(player.getHandCard(), player.getPgCardCnt(), huCardValue, player.isBright(), this.isQuanPingDao(), player.getHuInfo(), addHuCard, this.isFenSanDui(), this.getGameSubType());
    }

    @Override
    public void onHu(MahjongPlayer takeRoomPlayer, MahjongPlayer hu1RoomPlayer, MahjongPlayer hu2RoomPlayer, MahjongPlayer hu3RoomPlayer, byte huCard, int huType) {
        for (int i = 0; i < this.playerNum; ++i) {
            if (null == this.allPlayer[i] || this.allPlayer[i].isGuest()) {
                continue;
            }
            ((MahjongPlayer) this.allPlayer[i]).getHuInfo().clear();
        }
        if (MahjongConstant.MJ_HU_TYPE_BAR == huType) {
            // 抢杠胡
            this.joinBarCnt = 0;
        }

        HuRecordAction huRecordAction = ((MahjongRecord) this.record).addHuRecordAction();
        huRecordAction.addHu(hu1RoomPlayer.getUid());
        if (null != hu2RoomPlayer) {
            huRecordAction.addHu(hu2RoomPlayer.getUid());
        }
        if (null != hu3RoomPlayer) {
            huRecordAction.addHu(hu3RoomPlayer.getUid());
        }

        if (null != hu1RoomPlayer && null != hu2RoomPlayer) {
            // 一炮多响
            this.bankerIndex = takeRoomPlayer.getIndex();
            prevTwoWiner = true;
        } else {
            this.bankerIndex = hu1RoomPlayer.getIndex();
            prevTwoWiner = false;
        }

        this.onHu(takeRoomPlayer, hu1RoomPlayer, huCard, huType);
        if (null != hu2RoomPlayer) {
            this.onHu(takeRoomPlayer, hu2RoomPlayer, huCard, huType);
        }
        this.shuKan(takeRoomPlayer, hu1RoomPlayer, huCard);
        if (null != hu2RoomPlayer) {
            this.shuKan(takeRoomPlayer, hu2RoomPlayer, huCard);
        }
        this.pqmbScore(hu1RoomPlayer, huCard);
        if (null != hu2RoomPlayer) {
            this.pqmbScore(hu2RoomPlayer, huCard);
        }

        //this.sendHuInfo(takeRoomPlayer.getUid() != hu1RoomPlayer.getUid() ? takeRoomPlayer.getUid() : -1);

        List<Integer> buyHorse = null;
        MahjongPlayer buyHorsePlayer = null;
        do {
            if (this.isBuyHorse(hu1RoomPlayer.getHuInfo().isZiMo, hu1RoomPlayer.isBright())) {
                buyHorsePlayer = hu1RoomPlayer;
                //} else if (null != hu2RoomPlayer && this.isBuyHorse(hu1RoomPlayer.getHuInfo().isZiMo, hu1RoomPlayer.isLiang())) {
                //    buyHorsePlayer = hu2RoomPlayer;
            }
            if (null == buyHorsePlayer) {
                break;
            }
            if (this.isBuyOneHorse()) {
                // 买一马
                buyHorse = new ArrayList<>();
                byte horseCard = this.allCard.removeFirst();
                buyHorse.add((int) horseCard);
                buyHorsePlayer.addScore(Score.MJ_CUR_HORSE_SCORE, MahjongConstant.HORSE_SCORE[horseCard], false);
            } else if (this.isBuySixHorse()) {
                // 买六马
                buyHorse = new ArrayList<>();
                int temp = (huCard - 1 - (huCard >= MahjongConstant.MJ_ZHONG ? 1 : 0)) % 3;
                boolean zhong = this.allCard.size() < 6;
                for (int i = 0; i < 6 && !this.allCard.isEmpty(); ++i) {
                    byte horseCard = this.allCard.removeFirst();
                    if ((horseCard >= MahjongConstant.MJ_ZHONG && horseCard <= MahjongConstant.MJ_BAI) || temp == ((horseCard - 1) % 3)) {
                        zhong = true;
                        buyHorsePlayer.addScore(Score.MJ_CUR_HORSE_SCORE, 1, false);
                        buyHorse.add((1 << 16) | horseCard);
                    } else {
                        buyHorse.add((int) horseCard);
                    }
                }
                if (!zhong) {
                    buyHorsePlayer.addScore(Score.MJ_CUR_HORSE_SCORE, 6, false);
                }
            } else if (this.isBuyOneGiveOneHorse()) {
                // 买一送一
                buyHorse = new ArrayList<>();
                byte horseCard = this.allCard.removeFirst();
                buyHorse.add((int) horseCard);
                buyHorsePlayer.addScore(Score.MJ_CUR_HORSE_SCORE, MahjongConstant.HORSE_SCORE[horseCard], false);
                if (!this.allCard.isEmpty() && (horseCard == MahjongConstant.MJ_ONE_TIAO || horseCard == MahjongConstant.MJ_ONE_TONG)) {
                    horseCard = this.allCard.removeFirst();
                    buyHorse.add((int) horseCard);
                    buyHorsePlayer.addScore(Score.MJ_CUR_HORSE_SCORE, MahjongConstant.HORSE_SCORE[horseCard], false);
                }
            }
        } while (false);

        int maxPfki = this.info.getRule().getOrDefault(RoomRule.RR_MJ_MAX_FANG, Integer.MAX_VALUE);
        if (takeRoomPlayer.getUid() == hu1RoomPlayer.getUid()) {
            // 自摸
            int pfki = hu1RoomPlayer.getScore(Score.MJ_CUR_FANG_SCORE, false);

            for (int i = 0; i < this.playerNum; ++i) {
                MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                if (temp.getUid() != hu1RoomPlayer.getUid()) {
                    int tempPfki = pfki;
                    // 是否是对亮对番
                    if (dldf) {
                        if (temp.isBright() && hu1RoomPlayer.isBright()) {
                            tempPfki *= 4;

                        } else if (temp.isBright() || hu1RoomPlayer.isBright()) {
                            tempPfki *= 2;
                        }
                    } else {
                        if (temp.isBright() || hu1RoomPlayer.isBright()) {
                            tempPfki *= 2;
                        }
                    }
                    if (tempPfki > maxPfki) {
                        tempPfki = maxPfki;
                    }
                    temp.addScore(Score.MJ_CALC_FANG_SCORE, -tempPfki, false);
                    temp.addScore(Score.MJ_CALC_PIAO_SCORE, -temp.getPiaoScore() - hu1RoomPlayer.getPiaoScore(), false);
                    //temp.addCalcGangScore(temp.getCurGangScore());
                    temp.addScore(Score.MJ_CALC_HORSE_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_HORSE_SCORE, false), false);
                    temp.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false), false);
                    temp.addScore(Score.MJ_CALC_PQMB_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false), false);
                    hu1RoomPlayer.addScore(Score.MJ_CALC_FANG_SCORE, tempPfki, false);
                    hu1RoomPlayer.addScore(Score.MJ_CALC_PIAO_SCORE, temp.getPiaoScore() + hu1RoomPlayer.getPiaoScore(), false);
                    hu1RoomPlayer.addScore(Score.MJ_CALC_HORSE_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_HORSE_SCORE, false), false);
                    hu1RoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false), false);
                    hu1RoomPlayer.addScore(Score.MJ_CALC_PQMB_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false), false);
                }
            }
            //hu1RoomPlayer.addCalcGangScore(hu1RoomPlayer.getCurGangScore());
        } else if (null != hu2RoomPlayer) {
            // 一炮双响
            int pfki1 = hu1RoomPlayer.getScore(Score.MJ_CUR_FANG_SCORE, false);
            if (dldf && takeRoomPlayer.isBright() && hu1RoomPlayer.isBright()) {
                pfki1 *= 4;
            } else if (takeRoomPlayer.isBright() || hu1RoomPlayer.isBright()) {
                pfki1 *= 2;
            }
            if (pfki1 > maxPfki) {
                pfki1 = maxPfki;
            }
            int pfki2 = hu2RoomPlayer.getScore(Score.MJ_CUR_FANG_SCORE, false);
            if (dldf && takeRoomPlayer.isBright() && hu2RoomPlayer.isBright()) {
                pfki2 *= 4;
            } else if (takeRoomPlayer.isBright() || hu2RoomPlayer.isBright()) {
                pfki2 *= 2;
            }
            if (pfki2 > maxPfki) {
                pfki2 = maxPfki;
            }
            takeRoomPlayer.addScore(Score.MJ_CALC_FANG_SCORE, -pfki1 - pfki2, false);
            takeRoomPlayer.addScore(Score.MJ_CALC_PIAO_SCORE, -takeRoomPlayer.getPiaoScore() * 2 - hu1RoomPlayer.getPiaoScore() - hu2RoomPlayer.getPiaoScore(), false);
            //takeRoomPlayer.addCalcGangScore(takeRoomPlayer.getCurGangScore());
            takeRoomPlayer.addScore(Score.MJ_CALC_HORSE_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_HORSE_SCORE, false) - hu2RoomPlayer.getScore(Score.MJ_CUR_HORSE_SCORE, false), false);
            takeRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false) - hu2RoomPlayer.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false), false);
            takeRoomPlayer.addScore(Score.MJ_CALC_PQMB_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false) - hu2RoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false), false);

            hu1RoomPlayer.addScore(Score.MJ_CALC_FANG_SCORE, pfki1/*hu1RoomPlayer.getCurFangScore()*/, false);
            hu1RoomPlayer.addScore(Score.MJ_CALC_PIAO_SCORE, takeRoomPlayer.getPiaoScore() + hu1RoomPlayer.getPiaoScore(), false);
            hu1RoomPlayer.addScore(Score.MJ_CALC_HORSE_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_HORSE_SCORE, false), false);
            hu1RoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false), false);
            hu1RoomPlayer.addScore(Score.MJ_CALC_PQMB_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false), false);
            //hu1RoomPlayer.addCalcGangScore(hu1RoomPlayer.getCurGangScore());

            hu2RoomPlayer.addScore(Score.MJ_CALC_FANG_SCORE, pfki2/*hu2RoomPlayer.getCurFangScore()*/, false);
            hu2RoomPlayer.addScore(Score.MJ_CALC_PIAO_SCORE, takeRoomPlayer.getPiaoScore() + hu2RoomPlayer.getPiaoScore(), false);
            hu2RoomPlayer.addScore(Score.MJ_CALC_HORSE_SCORE, hu2RoomPlayer.getScore(Score.MJ_CUR_HORSE_SCORE, false), false);
            hu2RoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, hu2RoomPlayer.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false), false);
            hu2RoomPlayer.addScore(Score.MJ_CALC_PQMB_SCORE, hu2RoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false), false);
            //hu2RoomPlayer.addCalcGangScore(hu2RoomPlayer.getCurGangScore());
        } else {
            // 放炮
            int pfki1 = hu1RoomPlayer.getScore(Score.MJ_CUR_FANG_SCORE, false);
            if (dldf && takeRoomPlayer.isBright() && hu1RoomPlayer.isBright()) {
                pfki1 *= 4;
            } else if (takeRoomPlayer.isBright() || hu1RoomPlayer.isBright()) {
                pfki1 *= 2;
            }
            if (pfki1 > maxPfki) {
                pfki1 = maxPfki;
            }
            takeRoomPlayer.addScore(Score.MJ_CALC_FANG_SCORE, -pfki1/*hu1RoomPlayer.getCurFangScore()*/, false);
            takeRoomPlayer.addScore(Score.MJ_CALC_PIAO_SCORE, -takeRoomPlayer.getPiaoScore() - hu1RoomPlayer.getPiaoScore(), false);
            //takeRoomPlayer.addCalcGangScore(takeRoomPlayer.getCurGangScore());
            takeRoomPlayer.addScore(Score.MJ_CALC_HORSE_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_HORSE_SCORE, false), false);
            takeRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false), false);
            takeRoomPlayer.addScore(Score.MJ_CALC_PQMB_SCORE, -hu1RoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false), false);

            hu1RoomPlayer.addScore(Score.MJ_CALC_FANG_SCORE, pfki1/*hu1RoomPlayer.getCurFangScore()*/, false);
            hu1RoomPlayer.addScore(Score.MJ_CALC_PIAO_SCORE, takeRoomPlayer.getPiaoScore() + hu1RoomPlayer.getPiaoScore(), false);
            hu1RoomPlayer.addScore(Score.MJ_CALC_HORSE_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_HORSE_SCORE, false), false);
            hu1RoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false), false);
            hu1RoomPlayer.addScore(Score.MJ_CALC_PQMB_SCORE, hu1RoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false), false);
            //hu1RoomPlayer.addCalcGangScore(hu1RoomPlayer.getCurGangScore());
        }
        // TODO 结果记录
        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            temp.addScore(Score.MJ_CALC_GANG_SCORE, temp.getScore(Score.MJ_CUR_GANG_SCORE, false), false);
            temp.addScore(Score.SCORE, this.getScore(temp), false);
            if (temp.getHuInfo().isHu) {
                if (temp.getHuInfo().isZiMo) {
                    temp.addScore(Score.ACC_MJ_ZIMO_CNT, 1, true);
                    setTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_ZIMO_CNT, 1);
                } else {
                    temp.addScore(Score.ACC_MJ_HU_CNT, 1, true);
                    setTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_HU_CNT, 1);
                }
            } else if (takeRoomPlayer.getUid() == temp.getUid()) {
                temp.addScore(Score.ACC_MJ_DIAN_PAO_CNT, 1, true);
                setTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_DIAN_PAO_CNT, 1);
            }
            if (temp.isBright()) {
                temp.getHuInfo().paixing[EHuType.LP.ordinal()] = 2;
                temp.getHuInfo().paixing[EHuType.STATE_LP.ordinal()] = 1;
            }
            int tempScore = temp.getScore(Score.SCORE, false);
            if (tempScore > 0) {
            	temp.setScore(Score.ACC_LOST_CNT_CONTINUE, 0, true);
            } else if (tempScore < 0) {
            	temp.addScore(Score.ACC_LOST_CNT_CONTINUE, 1, true);
            }
        }

        this.getRoomHandle().calculateGold();
        
        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            int tempScore = temp.getScore(Score.SCORE, false);
            temp.addScore(Score.ACC_TOTAL_SCORE, tempScore, true);
            if (this.isShangLou() && temp.getScore(Score.ACC_TOTAL_SCORE, true) > 0) {
                temp.getHuInfo().paixing[EHuType.SHANG_LOU.ordinal()] = 1;
            }
        }

        this.record();

        boolean next = this.checkAgain() && this.getRoomHandle().checkAgain(Boolean.FALSE);

        ResultRecordAction resultRecordAction = ((MahjongRecord) this.record).addResultRecordAction();

        PCLIMahjongNtfGameOverInfoByKWX gameOverInfo = new PCLIMahjongNtfGameOverInfoByKWX();
        gameOverInfo.roomType = this.roomType.ordinal();
        gameOverInfo.bureau = curBureau;
        gameOverInfo.buyHorse = buyHorse;
        gameOverInfo.buyHorseScore = null != buyHorsePlayer ? buyHorsePlayer.getScore(Score.MJ_CALC_HORSE_SCORE, false) : 0;
        gameOverInfo.fangPaoUid = takeRoomPlayer.getUid() == hu1RoomPlayer.getUid() ? -1 : takeRoomPlayer.getUid();
        gameOverInfo.ziMoUid = takeRoomPlayer.getUid() == hu1RoomPlayer.getUid() ? hu1RoomPlayer.getUid() : -1;
        gameOverInfo.huCard = huCard;
        gameOverInfo.huPlayerUids.add(hu1RoomPlayer.getUid());
        gameOverInfo.next = next;
        if (null != hu2RoomPlayer) {
            gameOverInfo.huPlayerUids.add(hu2RoomPlayer.getUid());
        }

        resultRecordAction.addBuyHorse(buyHorse);
        resultRecordAction.setBuyHorseScore(gameOverInfo.buyHorseScore);
        resultRecordAction.setFangPaoUid(gameOverInfo.fangPaoUid);
        resultRecordAction.setZiMoUid(gameOverInfo.ziMoUid);
        resultRecordAction.setHuCard(huCard);
        resultRecordAction.addHu(gameOverInfo.huPlayerUids);

        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            HashMap<Integer, Integer> paiXing = new HashMap<>();
            for (int j = 0, len = EHuType.MAX.ordinal(); j < len; ++j) {
                int value = temp.getHuInfo().paixing[j];
                if (0 != value) {
                    paiXing.put(EHuType.values()[j].getValue(), value);
                }
            }
            gameOverInfo.allPaiXing.put(temp.getUid(), paiXing);

            List<Byte> card = new ArrayList<>();
            List<Byte> kou = new ArrayList<>();
            temp.addHandCardTo(card);
            temp.addKouCardTo(kou);
            gameOverInfo.allCard.put(temp.getUid(), card);
            gameOverInfo.allCardKou.put(temp.getUid(), kou);

            if (!next) {
                PCLIMahjongNtfGameOverInfo.FinalResult result = new PCLIMahjongNtfGameOverInfo.FinalResult();
//                result.anGangCnt = temp.getScore(Score.ACC_MJ_AN_GANG_CNT, true);
//                result.fangPaoCnt = temp.getScore(Score.ACC_MJ_DIAN_PAO_CNT, true);
//                result.huCnt = temp.getScore(Score.ACC_MJ_HU_CNT, true);
//                result.mingGangCnt = temp.getScore(Score.ACC_MJ_MING_GANG_CNT, true);
//                result.ziMoCnt = temp.getScore(Score.ACC_MJ_ZIMO_CNT, true);
                result.anGangCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_AN_GANG_CNT);
                result.fangPaoCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_DIAN_PAO_CNT);
                result.huCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_HU_CNT);
                result.mingGangCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_MING_GANG_CNT);
                result.ziMoCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_ZIMO_CNT);
                result.score = temp.getScore(Score.ACC_TOTAL_SCORE, true);
                gameOverInfo.allFinalResult.put(temp.getUid(), result);
            }

            PCLIMahjongNtfGameOverInfoByKWX.ScoreInfo scoreInfo = new PCLIMahjongNtfGameOverInfoByKWX.ScoreInfo();
            scoreInfo.barCnt = this.joinBarCnt;
            scoreInfo.piaoScore = temp.getScore(Score.MJ_CALC_PIAO_SCORE, false);
            scoreInfo.horseScore = temp.getScore(Score.MJ_CALC_HORSE_SCORE, false);
            scoreInfo.gangScore = temp.getScore(Score.MJ_CALC_GANG_SCORE, false);
            scoreInfo.fangScore = temp.getScore(Score.MJ_CALC_FANG_SCORE, false) +
                    temp.getScore(Score.MJ_CALC_SHU_KAN_SCORE, false) +
                    temp.getScore(Score.MJ_CALC_PQMB_SCORE, false);
            scoreInfo.score = this.getFormatScore(temp.getScore(Score.SCORE, false));
            scoreInfo.huScore = this.getScoreByHu(temp);
            scoreInfo.totalScore = this.getFormatScore((int)this.getPlayerGold(temp));
            scoreInfo.totalScore1 = this.getFormatScore(temp.getScore());
            gameOverInfo.allScore.putIfAbsent(temp.getUid(), scoreInfo);

            resultRecordAction.addCard(temp.getUid(), card);
            resultRecordAction.addCardKou(temp.getUid(), kou);
            resultRecordAction.addPaiXing(temp.getUid(), paiXing);
            resultRecordAction.setDestroy(this.checkIsDestroy());
            resultRecordAction.setDestroyUid(this.isDestroyUid);
            resultRecordAction.addScore(temp.getUid(), new ResultRecordAction.ScoreInfo(scoreInfo.barCnt, scoreInfo.gangScore, scoreInfo.fangScore, scoreInfo.horseScore, scoreInfo.piaoScore, scoreInfo.huScore, scoreInfo.score, scoreInfo.totalScore));

            if (Switch.DEBUG) {
                Logs.ROOM.error("结算:%s \n%s \n%s \n%s", this, temp, scoreInfo, temp.getScore(Score.ACC_TOTAL_SCORE, true));
            }
        }

        this.record.save();

        this.info.setDirty(true);
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, gameOverInfo);
        this.stop();
    }

    protected void onHu(MahjongPlayer takeRoomPlayer, MahjongPlayer huRoomPlayer, byte huCard, int huType) {
        int fangScore = this.hu(huRoomPlayer, huCard, takeRoomPlayer.getUid() != huRoomPlayer.getUid(), this.lastTakeCardValue);
        if (takeRoomPlayer.getUid() == huRoomPlayer.getUid()) {
            // 自摸
            huRoomPlayer.getHuInfo().isZiMo = true;
        }
        if (this.joinBarCnt > 0) {
            if (huRoomPlayer.getHuInfo().isZiMo) {
                huRoomPlayer.getHuInfo().isGangShangHu = true;
            } else {
                if (MahjongConstant.MJ_HU_TYPE_BAR != huType) {
                    huRoomPlayer.getHuInfo().isGangShangPao = true;
                }
            }
        }

        if (huRoomPlayer.getHuInfo().isKWX && this.isKWXFourScore()) {
            int fourFang = this.getKwxFourFang(huRoomPlayer.getHuInfo());
            if (fangScore < fourFang) {
                fangScore = fourFang;
            }
        }

        if (huRoomPlayer.getHuInfo().isPengPengHu && this.isPengPengHuFour()) {
            int fourFang = this.getPPHFourFang(huRoomPlayer.getHuInfo());
            if (fangScore < fourFang) {
                fangScore = fourFang;
            }
        }

        if (this.isXiaoSanYuanQiDui() && huRoomPlayer.getHuInfo().isXiaoSanYuanQiDui) {
            int fang = TbKWXFangManager.I.getFang(this.getGameSubType(), EHuType.XSY_QD);
            if (fangScore < fang) {
                fangScore = fang;
            }
            huRoomPlayer.getHuInfo().paixing[EHuType.QD.ordinal()] = 0;
            huRoomPlayer.getHuInfo().paixing[EHuType.XSY.ordinal()] = 0;
            huRoomPlayer.getHuInfo().paixing[EHuType.XSY_QD.ordinal()] = fang;
        }

        if (MahjongConstant.MJ_HU_TYPE_BAR == huType) {
            int score = (int) ((this.isGangFour() ? 4 : 2) * Math.pow(2, this.joinBarCnt));
            fangScore *= score;
            huRoomPlayer.getHuInfo().isQiangGangHu = true;
            huRoomPlayer.getHuInfo().paixing[EHuType.QGH.ordinal()] = score;
        }
        if (huRoomPlayer.getHuInfo().isGangShangHu) {
            int score = (int) ((this.isGangFour() ? 4 : 2) * Math.pow(2, this.joinBarCnt - 1));
            fangScore *= score;
            huRoomPlayer.getHuInfo().paixing[EHuType.GSKH.ordinal()] = score;
        }
        if (huRoomPlayer.getHuInfo().isGangShangPao) {
            int score = (int) ((this.isGangFour() ? 4 : 2) * Math.pow(2, this.joinBarCnt - 1));
            fangScore *= score;
            huRoomPlayer.getHuInfo().paixing[EHuType.GSP.ordinal()] = score;
        }

        if (this.isHaiDiLaoYue() && this.allCard.isEmpty() && huRoomPlayer.getHuInfo().isZiMo) {
            fangScore *= 2;
            huRoomPlayer.getHuInfo().paixing[EHuType.HDLY.ordinal()] = 2;
        }
        if (this.isHaiDiPao() && this.allCard.isEmpty() && !huRoomPlayer.getHuInfo().isZiMo) {
            fangScore *= 2;
            huRoomPlayer.getHuInfo().paixing[EHuType.HDP.ordinal()] = 2;
        }

        if (huRoomPlayer.getHuInfo().isGangShangHu || huRoomPlayer.getHuInfo().isGangShangPao || huRoomPlayer.getHuInfo().isQiangGangHu) {
            if (this.joinBarCnt > 1) {
                huRoomPlayer.getHuInfo().paixing[EHuType.BAR_CNT.ordinal()] = this.joinBarCnt - 1;
            }
        }

        //if (huRoomPlayer.isLiang()) {
        //    fangScore *= 2;
        //    huRoomPlayer.getHuInfo().paixing[KWXHuType.LP.ordinal()] = 2;
        //}

        //int maxPfki = this.rule.getOrDefault(ERoomRule.MAXPFKI, Integer.MAX_VALUE);
        //if (fangScore > maxPfki) {
        //    fangScore = maxPfki;
        //}
        huRoomPlayer.addScore(Score.MJ_CUR_FANG_SCORE, fangScore, false);
    }

    @Override
    public void onHuangZhuang(boolean next) {
        this.bankerIndex = this.lastFumbleIndex;
        for (int i = 0; i < this.playerNum; ++i) {
            if (null == this.allPlayer[i] || this.allPlayer[i].isGuest()) {
                continue;
            }
            ((MahjongPlayer) this.allPlayer[i]).getHuInfo().clear();
        }

        boolean chaDaJiao = false;
        boolean huangZhuangPeiFu = false;
        List<Long> chaDaJiaoList = new ArrayList<>();
        if (this.isChaDaJiao()) {
            // 查大叫
            for (int i = 0; i < this.playerNum; ++i) {
                MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                int maxFan = 0;
                for (int j = 0; j < MahjongConstant.MJ_CARD_KINDS; ++j) {
                    int fan = this.hu(temp, (byte) j, true, this.lastTakeCardValue);

                    if (temp.getHuInfo().isKWX && this.isKWXFourScore()) {
                        int fourFang = this.getKwxFourFang(temp.getHuInfo());
                        if (fan < fourFang) {
                            fan = fourFang;
                        }
                    }

                    if (temp.getHuInfo().isPengPengHu && this.isPengPengHuFour()) {
                        int fourFang = this.getPPHFourFang(temp.getHuInfo());
                        if (fan < fourFang) {
                            fan = fourFang;
                        }
                    }

                    if (fan > maxFan) {
                        maxFan = fan;
                    }
                }
                this.shuKan(temp, temp, (byte) 0);
                temp.setTingFang(maxFan);
            }
            int maxPfki = this.info.getRule().getOrDefault(RoomRule.RR_MJ_MAX_FANG, Integer.MAX_VALUE);
            for (int i = 0; i < this.playerNum; ++i) {
                MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                if (temp.isBright()) {
                    temp.getHuInfo().paixing[EHuType.STATE_LP.ordinal()] = 1;
                }
                if (temp.getTingFang() > 0) {
                    temp.getHuInfo().paixing[EHuType.STATE_TING.ordinal()] = 1;
                }
                for (int j = 0; j < this.playerNum; ++j) {
                    MahjongPlayer temp2 = (MahjongPlayer) this.allPlayer[j];
                    if (null == temp2 || temp2.isGuest()) {
                        continue;
                    }
                    if (temp.getUid() == temp2.getUid()) {
                        continue;
                    }
                    // 没听牌 赔听牌和亮牌  亮牌 赔听牌且没亮牌
                    if ((temp.getTingFang() < 1 && (temp2.isBright() || temp2.getTingFang() > 0)) ||
                            temp.isBright() && (temp2.getTingFang() > 0 && !temp2.isBright())) {
                        chaDaJiaoList.add(temp.getUid());
                        int pfki = temp2.getTingFang();
                        if (temp.isBright() || temp2.isBright()) {
                            pfki *= 2;
                        }
                        pfki += temp2.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false);
                        pfki += temp2.getPiaoScore() + temp.getPiaoScore();
                        if (pfki > maxPfki) {
                            pfki = maxPfki;
                        }
                        temp2.addScore(Score.MJ_CALC_FANG_SCORE, pfki, false);
                        temp.addScore(Score.MJ_CALC_FANG_SCORE, -pfki, false);
                        chaDaJiao = true;
                    }
                }
            }
        }
        if (this.isHuangZhuangPeiFu()) {
            // 荒庄赔付
            if (this.liangPaiPlayer.size() > 0) {
                IMahjongPlayer first = this.liangPaiPlayer.get(0);
                for (int i = 0; i < this.playerNum; ++i) {
                    MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    if (first.getUid() == temp.getUid()) {
                        temp.addScore(Score.MJ_CALC_FANG_SCORE, -(this.getCurPlayerCnt() - 1), false);
                    } else {
                        temp.addScore(Score.MJ_CALC_FANG_SCORE, 1, false);
                    }
                }
                huangZhuangPeiFu = true;
            }
        }

        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            temp.addScore(Score.MJ_CALC_GANG_SCORE, temp.getScore(Score.MJ_CUR_GANG_SCORE, false), false);
            temp.addScore(Score.SCORE, this.getScore(temp), false);
        }
        
        this.getRoomHandle().calculateGold();
        
        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            temp.addScore(Score.ACC_TOTAL_SCORE, temp.getScore(Score.SCORE, false), true);

            if (this.isShangLou() && temp.getScore(Score.ACC_TOTAL_SCORE, true) > 0) {
                temp.getHuInfo().paixing[EHuType.SHANG_LOU.ordinal()] = 1;
            }
        }

        if (next) {
            next = this.checkAgain() && this.getRoomHandle().checkAgain(Boolean.FALSE);
        }

        this.record();

        ResultRecordAction resultRecordAction = ((MahjongRecord) this.record).addResultRecordAction();
        resultRecordAction.setHuangZhuang(true);
        resultRecordAction.setChaDaJiao(chaDaJiao);
        resultRecordAction.setHuangZhuangPeiFu(huangZhuangPeiFu);
        resultRecordAction.addCahDaJiao(chaDaJiaoList);

        PCLIMahjongNtfGameOverInfoByKWX gameOverInfo = new PCLIMahjongNtfGameOverInfoByKWX();
        gameOverInfo.huangZhuang = true;
        gameOverInfo.chaDaJiao = chaDaJiao;
        gameOverInfo.huangZhuangPeiFu = huangZhuangPeiFu;
        gameOverInfo.roomType = this.roomType.ordinal();
        gameOverInfo.bureau = curBureau;
        gameOverInfo.fangPaoUid = -1;
        gameOverInfo.ziMoUid = -1;
        gameOverInfo.chaDaJiaoList.addAll(chaDaJiaoList);
        gameOverInfo.next = next;
        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }

            HashMap<Integer, Integer> paiXing = new HashMap<>();
            for (int j = 0, len = EHuType.MAX.ordinal(); j < len; ++j) {
                int value = temp.getHuInfo().paixing[j];
                if (0 != value) {
                    paiXing.put(EHuType.values()[j].getValue(), value);
                }
            }
            gameOverInfo.allPaiXing.put(temp.getUid(), paiXing);

            List<Byte> card = new ArrayList<>();
            List<Byte> kou = new ArrayList<>();
            temp.addHandCardTo(card);
            temp.addKouCardTo(kou);
            gameOverInfo.allCard.put(temp.getUid(), card);
            gameOverInfo.allCardKou.put(temp.getUid(), kou);

            if (!next) {
                PCLIMahjongNtfGameOverInfo.FinalResult result = new PCLIMahjongNtfGameOverInfo.FinalResult();
//                result.anGangCnt = temp.getScore(Score.ACC_MJ_AN_GANG_CNT, true);
//                result.fangPaoCnt = temp.getScore(Score.ACC_MJ_DIAN_PAO_CNT, true);
//                result.huCnt = temp.getScore(Score.ACC_MJ_HU_CNT, true);
//                result.mingGangCnt = temp.getScore(Score.ACC_MJ_MING_GANG_CNT, true);
//                result.ziMoCnt = temp.getScore(Score.ACC_MJ_ZIMO_CNT, true);
                result.anGangCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_AN_GANG_CNT);
                result.fangPaoCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_DIAN_PAO_CNT);
                result.huCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_HU_CNT);
                result.mingGangCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_MING_GANG_CNT);
                result.ziMoCnt = getTemporaryPropertyValue(temp.getUid(), Score.ACC_MJ_ZIMO_CNT);
                result.score = temp.getScore(Score.ACC_TOTAL_SCORE, true);
                gameOverInfo.allFinalResult.put(temp.getUid(), result);
            }

            PCLIMahjongNtfGameOverInfoByKWX.ScoreInfo scoreInfo = new PCLIMahjongNtfGameOverInfoByKWX.ScoreInfo();
            scoreInfo.barCnt = this.joinBarCnt;
            scoreInfo.piaoScore = temp.getScore(Score.MJ_CALC_PIAO_SCORE, false);
            scoreInfo.horseScore = temp.getScore(Score.MJ_CALC_HORSE_SCORE, false);
            scoreInfo.gangScore = temp.getScore(Score.MJ_CALC_GANG_SCORE, false);
            scoreInfo.fangScore = temp.getScore(Score.MJ_CALC_FANG_SCORE, false) +
                    temp.getScore(Score.MJ_CALC_SHU_KAN_SCORE, false) +
                    temp.getScore(Score.MJ_CALC_PQMB_SCORE, false);
            scoreInfo.score = this.getFormatScore(temp.getScore(Score.SCORE, false));
            scoreInfo.huScore = this.getScoreByHu(temp);
            scoreInfo.totalScore = this.getFormatScore(temp.getScore());
            gameOverInfo.allScore.putIfAbsent(temp.getUid(), scoreInfo);


            resultRecordAction.addCard(temp.getUid(), card);
            resultRecordAction.addCardKou(temp.getUid(), kou);
            resultRecordAction.addPaiXing(temp.getUid(), paiXing);
            resultRecordAction.setDestroy(this.checkIsDestroy());
            resultRecordAction.setDestroyUid(this.isDestroyUid);
            resultRecordAction.addScore(temp.getUid(), new ResultRecordAction.ScoreInfo(scoreInfo.barCnt, scoreInfo.gangScore, scoreInfo.fangScore, scoreInfo.horseScore, scoreInfo.piaoScore, scoreInfo.huScore, scoreInfo.score, scoreInfo.totalScore));

            if (Switch.DEBUG) {
                Logs.ROOM.error("结算:%s \n%s \n%s \n%s", this, temp, scoreInfo, temp.getScore(Score.ACC_TOTAL_SCORE, true));
            }
        }
        this.record.save();
        this.info.setDirty(true);

//        //如果是联盟竞技场
//        if (this.roomType == ERoomType.ARENA && !next) {
//            LeagueRoomScore leagueRoomScore = DBManager.I.getLeagueRoomScoreDao().loadByRoomId(this.getRoomId());
//            List<ScoreItemInfo> scoreItemInfoList = leagueRoomScore.getTotalScore().getScore();
//            for (int i = 0; i < scoreItemInfoList.size(); i++) {
//                boolean bHas = false;
//                for (int j = 0; j < this.playerNum; j++) {
//                    MahjongPlayer temp = (MahjongPlayer) this.allPlayer[j];
//                    if (null == temp || temp.isGuest()) {
//                        continue;
//                    }
//                    if (temp.getUid() == scoreItemInfoList.get(i).getPlayerUid()) {
//                        bHas = true;
//                    }
//                }
//                if (!bHas) {
//                    PCLIMahjongNtfGameOverInfo.FinalResult result = new PCLIMahjongNtfGameOverInfo.FinalResult();
//                    result.anGangCnt = temp.getScore(Score.ACC_MJ_AN_GANG_CNT, true);
//                    result.fangPaoCnt = temp.getScore(Score.ACC_MJ_DIAN_PAO_CNT, true);
//                    result.huCnt = temp.getScore(Score.ACC_MJ_HU_CNT, true);
//                    result.mingGangCnt = temp.getScore(Score.ACC_MJ_MING_GANG_CNT, true);
//                    result.ziMoCnt = temp.getScore(Score.ACC_MJ_ZIMO_CNT, true);
//                    result.score = temp.getScore(Score.ACC_TOTAL_SCORE, true) / 100;
//                    gameOverInfo.allFinalResult.put(temp.getUid(), result);
//                }
//            }
//        }

        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, gameOverInfo);
    }

    @Override
    protected void doSendGameOver(boolean next) {
        // TODO game over
    }

    protected void shuKan(MahjongPlayer takeRoomPlayer, MahjongPlayer huRoomPlayer, byte huCard) {
        if (this.isShuKan()) {
            int kanScore = 0;
            if (!huRoomPlayer.getHuInfo().isZiMo) {
                ++huRoomPlayer.getHandCard()[huCard];
            }
            for (int i = 0; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
                if (huRoomPlayer.getHandCard()[i] >= 3 || huRoomPlayer.getPgCardCnt()[i] >= 4) {
                    ++kanScore;
                }
            }
            if (!huRoomPlayer.getHuInfo().isZiMo) {
                --huRoomPlayer.getHandCard()[huCard];
            }
            huRoomPlayer.addScore(Score.MJ_CUR_SHU_KAN_SCORE, kanScore, false);
            huRoomPlayer.getHuInfo().paixing[EHuType.SK.ordinal()] = kanScore;
        }
    }

    protected void pqmbScore(MahjongPlayer huRoomPlayer, byte huCard) {
        if (!this.isPaoQiaMoBa()) {
            return;
        }
        huRoomPlayer.addScore(Score.MJ_CUR_PQMB_SCORE, 1, false);
        int cnt1 = -1;
        int cnt2 = -1;
        int cnt3 = -1;
        int cnt = 0;
        int index = 0;
        if (!huRoomPlayer.getHuInfo().isZiMo) {
            ++huRoomPlayer.getHandCard()[huCard];
        }
        for (int j = 0; j < MahjongConstant.MJ_CARD_KINDS; ++j) {
            if (huRoomPlayer.getHandCard()[j] >= 3 && !huRoomPlayer.getHuInfo().isQiDui) {
                huRoomPlayer.getHandCard()[j] -= 3;
                if (MahjongUtils.isHu(huRoomPlayer.getHandCard(), huRoomPlayer.getCpgCards().size() + 1, false)) {
                    huRoomPlayer.addScore(Score.MJ_CUR_PQMB_SCORE, 1, false);
                }
                huRoomPlayer.getHandCard()[j] += 3;
            }
            if (huRoomPlayer.getPgCardCnt()[j] >= 4) {
                huRoomPlayer.addScore(Score.MJ_CUR_PQMB_SCORE, 1, false);
            }
            if (huRoomPlayer.getHuInfo().isHu) {
                if (j < MahjongConstant.MJ_DONG) {
                    int index2 = (j - 1) / 9;
                    if (index != index2) {
                        index = index2;
                        if (-1 == cnt1) {
                            cnt1 = cnt;
                        } else if (-1 == cnt2) {
                            cnt2 = cnt;
                        } else if (-1 == cnt3) {
                            cnt3 = cnt;
                        }
                        cnt = 0;
                    }
                    cnt += huRoomPlayer.getHandCard()[j];
                    if (huRoomPlayer.getPgCardCnt()[j] >= 3) {
                        cnt += huRoomPlayer.getPgCardCnt()[j];
                    }
                }
            }
        }
        if (-1 == cnt1) {
            cnt1 = cnt;
        } else if (-1 == cnt2) {
            cnt2 = cnt;
        } else if (-1 == cnt3) {
            cnt3 = cnt;
        }
        if (!huRoomPlayer.getHuInfo().isZiMo) {
            --huRoomPlayer.getHandCard()[huCard];
        }
        if (cnt1 >= 8) {
            huRoomPlayer.addScore(Score.MJ_CUR_PQMB_SCORE, cnt1 - 7, false);
        }
        if (cnt2 >= 8) {
            huRoomPlayer.addScore(Score.MJ_CUR_PQMB_SCORE, cnt2 - 7, false);
        }
        if (cnt3 >= 8) {
            huRoomPlayer.addScore(Score.MJ_CUR_PQMB_SCORE, cnt3 - 7, false);
        }
        if (huRoomPlayer.getHuInfo().isZiMo) {
            huRoomPlayer.addScore(Score.MJ_CUR_PQMB_SCORE, 1, false);
        }
        if (huRoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false) > 0) {
            huRoomPlayer.getHuInfo().paixing[EHuType.PQMB.ordinal()] = huRoomPlayer.getScore(Score.MJ_CUR_PQMB_SCORE, false);
        }
    }

    @Override
    protected BrightInfo getBrightInfo(MahjongPlayer player) {
        return this.getLiangInfo(player, player.getHandCard(), player.getPgCardCnt(), this.allTakeCard, player.isBright(), this.isQuanPingDao(), player.getHuInfo(), this.isFenSanDui());
    }

    public HashSet<Pair> split(byte[] card, int dep, byte huCard) {
        HashSet<Pair> set = new HashSet<Pair>();
        int cnt = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            temp9Cards[i] = card[i];
            if (2 == card[i]) {
                ++cnt;
            } else if (4 == card[i]) {
                cnt += 2;
            }
        }
        if (7 == cnt) {
            for (byte i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
                cnt = temp9Cards[i];
                if (2 == cnt) {
                    set.add(new Pair(i, i, (byte) -1));
                } else if (4 == cnt) {
                    set.add(new Pair(i, i, (byte) -1));
                    set.add(new Pair(i, i, (byte) -1));
                }
            }
            return set;
        }
        for (byte i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (temp9Cards[i] >= 2) {
                temp9Cards[i] -= 2;
                Pair temp = new Pair(i, i);
                set.add(temp);
                if (searchSplit(temp9Cards, set, dep, huCard)) {
                    return set;
                }
                temp9Cards[i] += 2;
                set.remove(temp);
            }
        }
        return null;
    }

    private boolean searchSplit(byte[] card, HashSet<Pair> set, int dep, byte huCard) {
        // 刻字
        for (byte i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (card[i] >= 3) {
                Pair temp = new Pair(i, i, i);
                set.add(temp);
                if (dep >= 3) {
                    return true;
                }
                card[i] -= 3;
                if (searchSplit(card, set, dep + 1, huCard)) {
                    return true;
                }
                card[i] += 3;
                set.remove(temp);
            }
        }
        // 顺子
        for (byte i = 0; i < 25; ++i) {
            if (i % 9 < 7 && card[i + 1] >= 1 && card[i + 2] >= 1 && card[i + 3] >= 1) {
                Pair temp = new Pair((byte) (i + 1), (byte) (i + 2), (byte) (i + 3));
                set.add(temp);
                if (dep >= 3) {
                    return true;
                }
                card[i + 1]--;
                card[i + 2]--;
                card[i + 3]--;
                if (searchSplit(card, set, dep + 1, huCard)) {
                    return true;
                }
                card[i + 1]++;
                card[i + 2]++;
                card[i + 3]++;
                set.remove(temp);
            }
        }
        return false;
    }

    protected HashMap<Byte, HalfBrightInfo> getCanTingInfo(MahjongPlayer player, byte[] card, byte[] cpgCard, byte[] deskCard, boolean liang, boolean isFullChannel, HuInfo huInfo, boolean fenSanDui) {
        HashMap<Byte, HalfBrightInfo> tingInfo = new HashMap<Byte, HalfBrightInfo>();
        for (int i = 0; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (card[i] < 1) {
                continue;
            }
            HalfBrightInfo value = new HalfBrightInfo();
            --card[i];
            ++deskCard[i];
            for (int j = 1; j < MahjongConstant.MJ_CARD_KINDS; ++j) {
                if (card[j] >= 4) {
                    continue;
                }
                if (false && (card[j] + /*cpgCard[j] +*/ deskCard[j]) >= 4) {
                    continue;
                }
                int fan = MahjongUtils.isHuWithKWX(card, cpgCard, (byte) j, liang, isFullChannel, huInfo, true, fenSanDui, this.getGameSubType());
                if (fan > 0) {
                    int remain = this.getRemainCarCnt(player, (byte) j);
                    if (player.getHuInfo().isKWX && this.isKWXFourScore()) {
                        int fourFang = this.getKwxFourFang(player.getHuInfo());
                        if (fan < fourFang) {
                            fan = fourFang;
                        }
                    }
                    if (player.getHuInfo().isPengPengHu && this.isPengPengHuFour()) {
                        int fourFang = this.getPPHFourFang(player.getHuInfo());
                        if (fan < fourFang) {
                            fan = fourFang;
                        }
                    }
                    if (player.getHuInfo().isGangShangHu && this.isGangFour()) {
                        if (fan < 4) {
                            fan = 4;
                        }
                    }
                    if (player.getHuInfo().isQiDui && player.getHuInfo().isXiaoSanYuan && this.isXiaoSanYuanQiDui()) {
                        int fang = TbKWXFangManager.I.getFang(this.getGameSubType(), EHuType.XSY_QD);
                        if (fan < fang) {
                            fan = fang;
                        }
                    }
                    if (player.isBright()) {
                        fan *= 2;
                    }
                    value.huCard.put((byte) j, (fan << 16 | remain));
                    value.huKwx.put((byte) j, huInfo.isKWX);
                }
            }
            if (value.huCard.size() > 0) {
                tingInfo.put((byte) i, value);
            }
            ++card[i];
            --deskCard[i];
        }
        return tingInfo;
    }

    private byte[] temp6Cards = new byte[MahjongConstant.MJ_CARD_KINDS];
    private byte[] temp7Cards = new byte[MahjongConstant.MJ_CARD_KINDS];
    private byte[] temp8Cards = new byte[MahjongConstant.MJ_CARD_KINDS];
    private byte[] temp9Cards = new byte[MahjongConstant.MJ_CARD_KINDS];

    public BrightInfo getLiangInfo(MahjongPlayer player, byte[] card, byte[] cpgCard, byte[] deskCard, boolean liang, boolean isFullChannel, HuInfo huInfo, boolean fenSanDui) {
        int dep = 0;
        for (int i = 0; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            temp6Cards[i] = card[i];
            temp7Cards[i] = cpgCard[i];
            temp8Cards[i] = deskCard[i];
            if (cpgCard[i] > 0) {
                ++dep;
            }
        }
        BrightInfo info = new BrightInfo();
        for (int i = 0; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (i > 0 && this.temp6Cards[i] != 3) {
                continue;
            }
            if (i > 0) {
                this.temp6Cards[i] -= 3;
                this.temp7Cards[i] += 3;
            }
            BrightInfo temp = new BrightInfo();
            temp.tingInfo = this.getCanTingInfo(player, temp6Cards, temp7Cards, temp8Cards, liang, isFullChannel, huInfo, fenSanDui);
            if (temp.tingInfo.isEmpty()) {
                if (i > 0) {
                    this.temp6Cards[i] += 3;
                    this.temp7Cards[i] -= 3;
                }
                continue;
            }
            temp.kou = (byte) i;
            info.child.add(temp);
            this.searchLiangInfo(player, temp, temp6Cards, temp7Cards, temp8Cards, i, i > 0 ? dep + 1 : dep, liang, isFullChannel, huInfo, fenSanDui);
            Iterator<Map.Entry<Byte, HalfBrightInfo>> it = temp.tingInfo.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Byte, HalfBrightInfo> entry = it.next();
                --this.temp6Cards[entry.getKey()];
                Iterator<Map.Entry<Byte, Integer>> it2 = entry.getValue().huCard.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<Byte, Integer> entry1 = it2.next();
                    ++this.temp6Cards[entry1.getKey()];
                    HashSet<Pair> pairs = this.split(this.temp6Cards, i > 0 ? dep + 1 : dep, entry1.getKey());
                    if (null != pairs) {
                        if (4 == this.temp6Cards[entry1.getKey()]) {
                            Iterator<Pair> it3 = pairs.iterator();
                            while (it3.hasNext()) {
                                Pair p = it3.next();
                                if (entry1.getKey() == p.a || entry1.getKey() == p.b || entry1.getKey() == p.c) {
                                    it3.remove();
                                }
                            }
                        } else if (entry.getValue().huKwx.getOrDefault(entry1.getKey(), false)) {
                            Iterator<Pair> it3 = pairs.iterator();
                            while (it3.hasNext()) {
                                Pair p = it3.next();
                                byte kwx = entry1.getKey();
                                if (/*(MahjongConstant.MJ_FIVE_TIAO - 1 == p.a && MahjongConstant.MJ_FIVE_TIAO == p.b && MahjongConstant.MJ_FIVE_TIAO + 1 == p.c) ||
                                        (MahjongConstant.MJ_FIVE_TONG - 1 == p.a && MahjongConstant.MJ_FIVE_TONG == p.b && MahjongConstant.MJ_FIVE_TONG + 1 == p.c)*/
                                        (p.a == (kwx - 1)) && (p.b == kwx) && ((p.c == kwx + 1))) {
                                    it3.remove();
                                    break;
                                }
                            }
                        } else if (1 == entry.getValue().huCard.size()) {
                            Iterator<Pair> it3 = pairs.iterator();
                            while (it3.hasNext()) {
                                Pair p = it3.next();
                                if (entry1.getKey() == p.a || entry1.getKey() == p.b || entry1.getKey() == p.c) {
                                    it3.remove();
                                    break;
                                }
                            }
                        }
                        entry.getValue().split.put(entry1.getKey(), pairs);
                    }
                    --this.temp6Cards[entry1.getKey()];
                }
                ++this.temp6Cards[entry.getKey()];
            }
            if (i > 0) {
                this.temp6Cards[i] += 3;
                this.temp7Cards[i] -= 3;
            }
        }
        for (int j = 0; j < MahjongConstant.MJ_CARD_KINDS; ++j) {
            temp8Cards[j] = card[j];
        }
        return info;
    }

    protected boolean searchLiangInfo(MahjongPlayer player, BrightInfo info, byte[] card, byte[] cpgCard, byte[] deskCard, int index, int dep, boolean liang, boolean isFullChannel, HuInfo huInfo, boolean fenSanDui) {
        for (int i = 0; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (card[i] < 3) {
                continue;
            }
            card[i] -= 3;
            cpgCard[i] += 3;
            BrightInfo temp = new BrightInfo();
            temp.tingInfo = getCanTingInfo(player, card, cpgCard, deskCard, liang, isFullChannel, huInfo, fenSanDui);

            if (temp.tingInfo.size() > 0) {
                temp.kou = (byte) i;
                info.child.add(temp);
                searchLiangInfo(player, temp, card, cpgCard, deskCard, i, dep + 1, liang, isFullChannel, huInfo, fenSanDui);
                Iterator<Map.Entry<Byte, HalfBrightInfo>> it = temp.tingInfo.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Byte, HalfBrightInfo> entry = it.next();
                    --card[entry.getKey()];
                    Iterator<Map.Entry<Byte, Integer>> it2 = entry.getValue().huCard.entrySet().iterator();
                    while (it2.hasNext()) {
                        Map.Entry<Byte, Integer> entry1 = it2.next();
                        ++card[entry1.getKey()];
                        HashSet<Pair> pairs = split(card, dep + 1, entry1.getKey());
                        if (null != pairs) {
                            if (4 == card[entry1.getKey()]) {
                                Iterator<Pair> it3 = pairs.iterator();
                                while (it3.hasNext()) {
                                    Pair p = it3.next();
                                    if (entry1.getKey() == p.a || entry1.getKey() == p.b || entry1.getKey() == p.c) {
                                        it3.remove();
                                    }
                                }
                            } else if (entry.getValue().huKwx.getOrDefault(entry1.getKey(), false)) {
                                Iterator<Pair> it3 = pairs.iterator();
                                while (it3.hasNext()) {
                                    Pair p = it3.next();
                                    byte kwx = entry1.getKey();
                                    if (/*(MahjongConstant.MJ_FIVE_TIAO - 1 == p.a && MahjongConstant.MJ_FIVE_TIAO == p.b && MahjongConstant.MJ_FIVE_TIAO + 1 == p.c) ||
                                            (MahjongConstant.MJ_FIVE_TONG - 1 == p.a && MahjongConstant.MJ_FIVE_TONG == p.b && MahjongConstant.MJ_FIVE_TONG + 1 == p.c)*/
                                            (p.a == (kwx - 1)) && (p.b == kwx) && ((p.c == kwx + 1))) {
                                        it3.remove();
                                        break;
                                    }
                                }
                            } else if (1 == entry.getValue().huCard.size()) {
                                Iterator<Pair> it3 = pairs.iterator();
                                while (it3.hasNext()) {
                                    Pair p = it3.next();
                                    if (entry1.getKey() == p.a || entry1.getKey() == p.b || entry1.getKey() == p.c) {
                                        it3.remove();
                                        break;
                                    }
                                }
                            }
                            entry.getValue().split.put(entry1.getKey(), pairs);
                        }
                        --card[entry1.getKey()];
                    }
                    ++card[entry.getKey()];
                }
            }
            card[i] += 3;
            cpgCard[i] -= 3;
        }
        return info.child.size() > 0;
    }

    @Override
    protected boolean isBright(boolean bright) {
        // 少于12张牌不能亮牌
        return bright;
    }

    @Override
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1)  * value * (this.isShangLou() ? 2 : 1);
    }

    protected int getScore(IRoomPlayer player) {
        int value = player.getScore(Score.MJ_CALC_FANG_SCORE, false) +
                player.getScore(Score.MJ_CALC_GANG_SCORE, false) + player.getScore(Score.MJ_CALC_HORSE_SCORE, false) +
                player.getScore(Score.MJ_CALC_PIAO_SCORE, false) + player.getScore(Score.MJ_CALC_PQMB_SCORE, false) +
                player.getScore(Score.MJ_CALC_SHU_KAN_SCORE, false);
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1)  * value * (this.isShangLou() ? 2 : 1);
    }

    protected int getScoreByHu(IRoomPlayer player) {
        int value = player.getScore(Score.MJ_CALC_PIAO_SCORE, false) +
                player.getScore(Score.MJ_CALC_HORSE_SCORE, false) +
                player.getScore(Score.MJ_CALC_FANG_SCORE, false) +
                player.getScore(Score.MJ_CALC_PQMB_SCORE, false) +
                player.getScore(Score.MJ_CALC_SHU_KAN_SCORE, false);
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1)  * value * (this.isShangLou() ? 2 : 1);
    }

    protected int getKwxFourFang(HuInfo huInfo) {
        int fangScore = 0;
        if (0 != huInfo.paixing[EHuType.KWX.ordinal()]) {
            int temp = huInfo.paixing[EHuType.KWX.ordinal()] * 2;
            huInfo.paixing[EHuType.KWX.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.QYS_KWX.ordinal()]) {
            int temp = huInfo.paixing[EHuType.QYS_KWX.ordinal()] * 2;
            huInfo.paixing[EHuType.QYS_KWX.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.XSY_KWX.ordinal()]) {
            int temp = huInfo.paixing[EHuType.XSY_KWX.ordinal()] * 2;
            huInfo.paixing[EHuType.XSY_KWX.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.DSY_KWX.ordinal()]) {
            int temp = huInfo.paixing[EHuType.DSY_KWX.ordinal()] * 2;
            huInfo.paixing[EHuType.DSY_KWX.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.QYS_MSG_KWX.ordinal()]) {
            int temp = huInfo.paixing[EHuType.QYS_MSG_KWX.ordinal()] * 2;
            huInfo.paixing[EHuType.QYS_MSG_KWX.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.QYS_ASG_KWX.ordinal()]) {
            int temp = huInfo.paixing[EHuType.QYS_ASG_KWX.ordinal()] * 2;
            huInfo.paixing[EHuType.QYS_ASG_KWX.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.XSY_MSG_KWX.ordinal()]) {
            int temp = huInfo.paixing[EHuType.XSY_MSG_KWX.ordinal()] * 2;
            huInfo.paixing[EHuType.XSY_MSG_KWX.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.XSY_ASG_KWX.ordinal()]) {
            int temp = huInfo.paixing[EHuType.XSY_ASG_KWX.ordinal()] * 2;
            huInfo.paixing[EHuType.XSY_ASG_KWX.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.KWX_MSG.ordinal()]) {
            int temp = huInfo.paixing[EHuType.KWX_MSG.ordinal()] * 2;
            huInfo.paixing[EHuType.KWX_MSG.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.KWX_ASG.ordinal()]) {
            int temp = huInfo.paixing[EHuType.KWX_ASG.ordinal()] * 2;
            huInfo.paixing[EHuType.KWX_ASG.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.KWX_MSG_S.ordinal()]) {
            int temp = huInfo.paixing[EHuType.KWX_MSG_S.ordinal()] * 2;
            huInfo.paixing[EHuType.KWX_MSG_S.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.KWX_ASG_S.ordinal()]) {
            int temp = huInfo.paixing[EHuType.KWX_ASG_S.ordinal()] * 2;
            huInfo.paixing[EHuType.KWX_ASG_S.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.KWX_MSG_ASG.ordinal()]) {
            int temp = huInfo.paixing[EHuType.KWX_MSG_ASG.ordinal()] * 2;
            huInfo.paixing[EHuType.KWX_MSG_ASG.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.QYS_KWX_MSG_S.ordinal()]) {
            int temp = huInfo.paixing[EHuType.QYS_KWX_MSG_S.ordinal()] * 2;
            huInfo.paixing[EHuType.QYS_KWX_MSG_S.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.QYS_KWX_ASG_S.ordinal()]) {
            int temp = huInfo.paixing[EHuType.QYS_KWX_ASG_S.ordinal()] * 2;
            huInfo.paixing[EHuType.QYS_KWX_ASG_S.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.QYS_KWX_MSG_ASG.ordinal()]) {
            int temp = huInfo.paixing[EHuType.QYS_KWX_MSG_ASG.ordinal()] * 2;
            huInfo.paixing[EHuType.QYS_KWX_MSG_ASG.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        return fangScore;
    }

    protected int getPPHFourFang(HuInfo huInfo) {
        int fangScore = 0;
        if (0 != huInfo.paixing[EHuType.PPH.ordinal()]) {
            int temp = huInfo.paixing[EHuType.PPH.ordinal()] * 2;
            huInfo.paixing[EHuType.PPH.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.QYS_PPH.ordinal()]) {
            int temp = huInfo.paixing[EHuType.QYS_PPH.ordinal()] * 2;
            huInfo.paixing[EHuType.QYS_PPH.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.XSY_PPH.ordinal()]) {
            int temp = huInfo.paixing[EHuType.XSY_PPH.ordinal()] * 2;
            huInfo.paixing[EHuType.XSY_PPH.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        if (0 != huInfo.paixing[EHuType.DSY_PPH.ordinal()]) {
            int temp = huInfo.paixing[EHuType.DSY_PPH.ordinal()] * 2;
            huInfo.paixing[EHuType.DSY_PPH.ordinal()] = temp;
            if (fangScore < temp) {
                fangScore = temp;
            }
        }
        return fangScore;
    }

    protected boolean isChaDaJiao() {
        // 查大叫
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.CDJ.getValue());
    }

    protected boolean isHuangZhuangPeiFu() {
        // 荒庄赔付
        return false;
    }

    protected boolean isXiaoSanYuanQiDui() {
        // 小三元七对
        return false;
    }

    protected boolean isShuKan() {
        // 数坎
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.SK.getValue());
    }

    protected boolean isFenSanDui() {
        // 分3堆
        return false;
    }

    protected boolean isQuanPingDao() {
        // 全频道
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.FULL_CHANNEL.getValue());
    }

    protected boolean isShangLou() {
        // 上楼
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.SL.getValue()) && this.crap1 == this.crap2;
    }

    protected boolean isHaiDiLaoYue() {
        // 海底捞月
        return false;
    }

    protected boolean isHaiDiPao() {
        // 海底跑
        return false;
    }

    protected boolean isBanLiang() {
        // 半亮
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.BFL.getValue());
    }

    protected boolean isBuyHorse(boolean ziMo, boolean liang) {
        // 买马 1, 2, 3
        int value = this.info.getRule().getOrDefault(RoomRule.RR_MJ_BUY_HORSE, 0) >> 16;
        if (value < 2 || value > 3) {
            return false;
        }
        if (this.allCard.isEmpty()) {
            return false;
        }
        if (!ziMo) {
            return false;
        }
        if (3 == value) {
            return liang;
        }
        return 2 == value;
    }

    protected boolean isBuyOneHorse() {
        // 买一马 4
        int value = this.info.getRule().getOrDefault(RoomRule.RR_MJ_BUY_HORSE, 0) & 0x00ff;
        return 4 == value;
    }

    protected boolean isBuySixHorse() {
        // 买6马 4, 5, 6
        int value = this.info.getRule().getOrDefault(RoomRule.RR_MJ_BUY_HORSE, 0) & 0x00ff;
        return 5 == value;
    }

    protected boolean isBuyOneGiveOneHorse() {
        // 买一送一马 6
        int value = this.info.getRule().getOrDefault(RoomRule.RR_MJ_BUY_HORSE, 0) & 0x00ff;
        return 6 == value;
    }

    protected boolean isPaoQiaMoBa() {
        // 跑恰模八
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.PQMB.getValue());
    }

    protected boolean isKWXFourScore() {
        // 卡五星x4
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.KWX.getValue());
    }

    protected boolean isPengPengHuFour() {
        // 碰碰胡x4
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.PPH.getValue());
    }

    protected boolean isGangFour() {
        // 杠x4
        return 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EKWXPlayRule.GSH.getValue());
    }

}
