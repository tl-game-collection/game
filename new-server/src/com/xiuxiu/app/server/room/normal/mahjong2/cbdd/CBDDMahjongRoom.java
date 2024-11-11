package com.xiuxiu.app.server.room.normal.mahjong2.cbdd;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByCBDD;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByCBDD;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;
import com.xiuxiu.app.server.room.normal.mahjong2.EPaiXing;
import com.xiuxiu.app.server.room.normal.mahjong2.HuInfo;
import com.xiuxiu.app.server.room.normal.mahjong2.MahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.bird.IMahjongBird;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHu;
import com.xiuxiu.app.server.room.player.mahjong2.CBDDMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IXuanZeng;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.CBDDMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.CBDDResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.ResultRecordAction;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//@GameInfo(gameType = GameType.GAME_TYPE_CBDD)
public class CBDDMahjongRoom extends MahjongRoom {
    protected int zengType = 0;                         // 选增
    protected int niaoType = 0;                         // 抓鸟类型, 0: 不抓鸟
    protected int niaoNum = 1;                          // 抓鸟张数
    protected boolean seven = false;                    // 是否胡7对
    protected boolean bankerMul = false;                // 庄家+1
    protected boolean onlyZiMo = true;                  // 可接跑
    protected boolean isHongZhong = false;              // 红中
    protected boolean isEatCard = false;                // 是否可以吃牌
    protected boolean isFengCard = false;               // 是否有东、南、西、北、发、白(是否胡风一色)

    protected List<Byte> niaoList = new ArrayList<>();// 鸟列表

    public CBDDMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public CBDDMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.fangGangScore = 2;

        this.zengType = this.info.getRule().getOrDefault(RoomRule.RR_MJ_CBDD_ZENG, 0);
        this.niaoType = this.info.getRule().getOrDefault(RoomRule.RR_MJ_CBDD_NIAO, 0);
        this.niaoNum = this.info.getRule().getOrDefault(RoomRule.RR_MJ_CBDD_NIAO_NUM, 1);
        this.seven = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ECBDDPlayRule.SEVEN.getValue());
        this.bankerMul = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ECBDDPlayRule.BANKERMUL.getValue());
        this.onlyZiMo = 0 == (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ECBDDPlayRule.ONLYZIMO.getValue());
        this.isHongZhong = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ECBDDPlayRule.HONGZHOU.getValue());
        this.isEatCard = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ECBDDPlayRule.ALLOWEAT.getValue());
        this.isFengCard = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ECBDDPlayRule.FENGCARD.getValue());
    }

    @Override
    protected void doShuffle() {
        Logs.ROOM.debug("isEatCard   " + this.isEatCard);
        Logs.ROOM.debug("!this.isEatCard   " + !this.isFengCard);
        Logs.ROOM.debug("isFengCard   " + this.isFengCard);
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
            if (this.isHongZhong) {
                this.allCard.add(MahjongUtil.MJ_Z_FENG);
            }
            if (this.isFengCard) {
                this.allCard.add(MahjongUtil.MJ_D_FENG);
                this.allCard.add(MahjongUtil.MJ_N_FENG);
                this.allCard.add(MahjongUtil.MJ_X_FENG);
                this.allCard.add(MahjongUtil.MJ_B_FENG);
                this.allCard.add(MahjongUtil.MJ_F_FENG);
                this.allCard.add(MahjongUtil.MJ_BAI_FENG);
            }
        }

        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doDealAfter() {
        super.doDealAfter();
        if (this.isHongZhong) {
            this.laiZiCard = MahjongUtil.MJ_Z_FENG;
        }
    }

    @Override
    protected void doStart1() {
        this.beginXuanZeng(this.zengType);
    }

    @Override
    public void endXuanZeng() {
        super.endXuanZeng();
        this.doStartTake();
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new CBDDMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (!fangGang) {
            if (player.hasHandCard(takeCard, 4)) {
                // 暗杠
                if (EActionOp.BUMP == this.curAction) {
                    return false;
                }
                return true;
            }
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        if (!this.isEatCard) {
            return false;
        }
        return !this.isPiOrLaiZi(takeCard) && super.isEat(player, takeCard);
    }

    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        if (!ziMo && player.isPass(EActionOp.HU)) {
            return false;
        }

        if (!ziMo && this.onlyZiMo) {
            return false;
        }
        int eatCnt = 0;         // 吃牌计数
        int normalCPGCnt = 0;   // 常规杠牌的次数（非皮非赖）
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
            if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                normalCPGCnt++;
            }
        }

        long hu = 0;
        do {
            // 风一色
            if (this.isFengCard && eatCnt == 0 && MahjongUtil.isFengYiSe(tempCards.get(), this.laiZiCard)) {
                Logs.ROOM.debug(" 风一色 ");
                hu |= MahjongHu.FENG_YI_SE;
            }
            if (hu == 0) {
                if (!MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, this.seven, this.laiZiCard, Integer.MAX_VALUE)) {
                    break;
                } else {
                    hu |= MahjongHu.PI_HU;
                }
            }
            if (ziMo) {
                hu |= MahjongHu.ZI_MO;
            }
        } while (false);
        return hu != 0;
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
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);

        if (0 != this.niaoType) {
            IMahjongBird bird = IMahjongBird.get(this.niaoType);
            if (null != bird) {
                int cnt = Math.max(0, this.niaoNum);
                cnt = Math.min(this.allCard.size(), cnt);
                bird.setCnt(cnt);
                for (int i = 0; i < cnt; ++i) {
                    byte card = this.allCard.removeFirst();
                    if (null != player2) {
                        bird.isHit(this, takePlayer, card);
                    } else {
                        bird.isHit(this, player1, card);
                    }
                    this.niaoList.add(card);
                }
                if (takePlayer.getUid() == player1.getUid()) {
                    // 自摸
                    int niaoScore = 0;
                    for (int i = 0; i < this.playerNum; ++i) {
                        IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                        if (null == player || player.isGuest() || player.getUid() == takePlayer.getUid()) {
                            continue;
                        }
                        int value = bird.calcNiaoScore(takePlayer, player);
                        player.setScore(Score.MJ_CUR_NIAO_SCORE, -value, false);
                        niaoScore += value;
                    }
                    takePlayer.setScore(Score.MJ_CUR_NIAO_SCORE, niaoScore, false);
                } else {
                    // 点炮
                    int niaoScore = 0;
                    if (null != player1) {
                        int value = bird.calcNiaoScore(takePlayer, player1);
                        player1.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        niaoScore += value;
                    }
                    if (null != player2) {
                        int value = bird.calcNiaoScore(takePlayer, player2);
                        player2.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        niaoScore += value;
                    }
                    if (null != player3) {
                        int value = bird.calcNiaoScore(takePlayer, player3);
                        player3.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        niaoScore += value;
                    }
                    takePlayer.setScore(Score.MJ_CUR_NIAO_SCORE, -niaoScore, false);
                }
            }
        }

        if (takePlayer.getUid() == player1.getUid()) {
            // 自摸
            player1.addHu(EPaiXing.CBDD_NORMAL, huCard);
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.allPlayer[i];
                if (null == otherPlayer || otherPlayer.isGuest() || otherPlayer.getUid() == player1.getUid()) {
                    continue;
                }
                int value = 2;
                if (this.bankerMul && (this.bankerIndex == otherPlayer.getIndex() || this.bankerIndex == player1.getIndex())) {
                    value += 1;
                }
                int zengValue=0;
                if(this.zengType!=0) {
                     zengValue = ((IXuanZeng) player1).getZeng() + ((IXuanZeng) otherPlayer).getZeng();
                }

                otherPlayer.addScore(Score.MJ_CUR_HU_SCORE, -value, false);
                otherPlayer.addScore(Score.MJ_CUR_ZENG_SCORE, -zengValue, false);
                player1.addScore(Score.MJ_CUR_HU_SCORE, value, false);
                player1.addScore(Score.MJ_CUR_ZENG_SCORE, zengValue, false);
            }
        } else {
            // 点炮
            if (null != player1) {
                player1.addHu(takePlayer.getUid(), EPaiXing.CBDD_NORMAL, huCard);
                int value = 2;
                if (this.bankerMul && (this.bankerIndex == takePlayer.getIndex() || this.bankerIndex == player1.getIndex())) {
                    value += 1;
                }
                int zengValue=0;
                if(this.zengType!=0) {
                     zengValue = ((IXuanZeng) player1).getZeng() + ((IXuanZeng) takePlayer).getZeng();
                }

                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -value, false);
                takePlayer.addScore(Score.MJ_CUR_ZENG_SCORE, -zengValue, false);
                player1.addScore(Score.MJ_CUR_HU_SCORE, value, false);
                player1.addScore(Score.MJ_CUR_ZENG_SCORE, zengValue, false);
            }
            if (null != player2) {
                player2.addHu(takePlayer.getUid(), EPaiXing.CBDD_NORMAL, huCard);
                int value = 2;
                if (this.bankerMul && (this.bankerIndex == takePlayer.getIndex() || this.bankerIndex == player2.getIndex())) {
                    value += 1;
                }
                int zengValue=0;
                if(this.zengType!=0) {
                     zengValue = ((IXuanZeng) player2).getZeng() + ((IXuanZeng) takePlayer).getZeng();
                }

                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -value, false);
                takePlayer.addScore(Score.MJ_CUR_ZENG_SCORE, -zengValue, false);
                player2.addScore(Score.MJ_CUR_HU_SCORE, value, false);
                player2.addScore(Score.MJ_CUR_ZENG_SCORE, zengValue, false);
            }
            if (null != player3) {
                player3.addHu(takePlayer.getUid(), EPaiXing.CBDD_NORMAL, huCard);
                int value = 2;
                if (this.bankerMul && (this.bankerIndex == takePlayer.getIndex() || this.bankerIndex == player3.getIndex())) {
                    value += 1;
                }
                int zengValue=0;
                if(this.zengType!=0) {
                     zengValue = ((IXuanZeng) player3).getZeng() + ((IXuanZeng) takePlayer).getZeng();
                }

                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -value, false);
                takePlayer.addScore(Score.MJ_CUR_ZENG_SCORE, -zengValue, false);
                player3.addScore(Score.MJ_CUR_HU_SCORE, value, false);
                player3.addScore(Score.MJ_CUR_ZENG_SCORE, zengValue, false);
            }
        }

        CBDDResultRecordAction resultRecordAction = (CBDDResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        resultRecordAction.addNiaoList(this.niaoList);

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.addScore(Score.SCORE, this.getScore(
                    player.getScore(Score.MJ_CUR_GANG_SCORE, false)
                            + player.getScore(Score.MJ_CUR_HU_SCORE, false)
                            + player.getScore(Score.MJ_CUR_NIAO_SCORE, false)
                            + player.getScore(Score.MJ_CUR_ZENG_SCORE, false)
            ), false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            CBDDResultRecordAction.PlayerInfo playerInfo = new CBDDResultRecordAction.PlayerInfo();

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

            CBDDResultRecordAction.ScoreInfo scoreInfo = new CBDDResultRecordAction.ScoreInfo();
            scoreInfo.setNiaoScore(player.getScore(Score.MJ_CUR_NIAO_SCORE, false));
            scoreInfo.setZengScore(player.getScore(Score.MJ_CUR_ZENG_SCORE, false));
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }
       if(player2!=null){
           this.bankerIndex = takePlayer.getIndex();
       }else {
           this.bankerIndex = player1.getIndex();
       }
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        CBDDResultRecordAction resultRecordAction = (CBDDResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.addScore(Score.SCORE, this.getScore(
                    player.getScore(Score.MJ_CUR_GANG_SCORE, false)
            ), false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            CBDDResultRecordAction.PlayerInfo playerInfo = new CBDDResultRecordAction.PlayerInfo();
            CBDDResultRecordAction.ScoreInfo scoreInfo = new CBDDResultRecordAction.ScoreInfo();
            scoreInfo.setNiaoScore(player.getScore(Score.MJ_CUR_NIAO_SCORE, false));
            scoreInfo.setZengScore(player.getScore(Score.MJ_CUR_ZENG_SCORE, false));
            scoreInfo.setFangScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            scoreInfo.setGangScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            scoreInfo.setScore(this.getFormatScore(player.getScore(Score.SCORE, false)));
            scoreInfo.setTotalScore(this.getFormatScore(player.getScore()));
            playerInfo.setScoreInfo(scoreInfo);

            resultRecordAction.addResult(player.getUid(), playerInfo);
        }

        this.bankerIndex = this.lastFumbleIndex;
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByCBDD info = new PCLIMahjongNtfGameOverInfoByCBDD();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

        info.niaoList.addAll(this.niaoList);

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByCBDD.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByCBDD.PlayerInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByCBDD.ScoreInfo();
            ((PCLIMahjongNtfGameOverInfoByCBDD.ScoreInfo) playerInfo.score).niaoScore = player.getScore(Score.MJ_CUR_NIAO_SCORE, false);
            ((PCLIMahjongNtfGameOverInfoByCBDD.ScoreInfo) playerInfo.score).zengScore = player.getScore(Score.MJ_CUR_ZENG_SCORE, false);
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
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
        
        PCLIMahjongNtfDeskInfoByCBDD deskInfo = new PCLIMahjongNtfDeskInfoByCBDD();
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
                PCLIMahjongNtfDeskInfoByCBDD.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByCBDD.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByCBDD.CardNode cardNode = new PCLIMahjongNtfDeskInfoByCBDD.CardNode();
                    cardNode.type = node.getType().ordinal();
                    cardNode.playerId=this.allPlayer[node.getTakePlayerIndex()].getUid();
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
                deskPlayerInfo.zeng = ((IXuanZeng) other).getZeng();
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
        return new CBDDMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    public void clear() {
        super.clear();
        this.niaoList.clear();
    }
}
