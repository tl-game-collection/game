package com.xiuxiu.app.server.room.normal.mahjong2.zzmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByZZMJ;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.bird.IMahjongBird;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IXuanPiao;
import com.xiuxiu.app.server.room.player.mahjong2.ZZMJMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.ResultRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.ZZMJMahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong2.ZZMJResultRecordAction;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//@GameInfo(gameType = GameType.GAME_TYPE_ZZMJ)
public class ZZMJMahjongRoom extends MahjongRoom {
    protected int bankerType = 0;                               // 庄类型
    protected int niaoScore = 1;                                // 1鸟分
    protected int niaoType = 0;                                 // 转鸟类型
    protected int huType = 2;                                   // 胡牌类型

    protected boolean seven = false;                            // 胡七对
    protected boolean qgh = false;                              // 抢杠胡
    protected boolean mustHu = false;                           // 必胡
    protected boolean bankerInc = false;                        // 庄家加1
    protected boolean onlyZiMo = false;                         // 自摸
    protected boolean lessTong = false;                         // 缺筒
    protected boolean _eat = false;                             // 可吃牌
    protected boolean hzLaiZi = false;                          // 红中癞子
    protected boolean _258Eye = false;                          // 258将
    protected boolean piaoScore = false;                        // 飘分
    protected boolean louPeng = false;                          // 漏碰
    protected boolean wypn = false;                             // 围一票鸟
    protected boolean fangBar = false;                          // 放杠1番
    protected boolean gen = false;                              // 根

    protected List<Byte> niaoList = new ArrayList<>();// 鸟列表

    public ZZMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public ZZMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.fumbleWithBarOnFrontend = false;

        this.bankerType = this.getRule().getOrDefault(RoomRule.RR_MJ_ZZ_BANKER_TYPE, 0);
        this.niaoScore = this.getRule().getOrDefault(RoomRule.RR_MJ_ZZ_NIAO_SCORE, 0);
        this.niaoType = this.getRule().getOrDefault(RoomRule.RR_MJ_ZZ_NIAO_TYPE, 0);
        this.huType = this.getRule().getOrDefault(RoomRule.RR_MJ_ZZ_HU_TYPE, 2);

        this.seven = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.SEVEN.getValue());
        this.qgh = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.QGH.getValue());
        this.mustHu = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.HU_MUST.getValue());
        this.bankerInc = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.BANKER_INC.getValue());
        this.onlyZiMo = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.ONLY_ZIMO.getValue());
        this.lessTong = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.TONG_LESS.getValue());
        this._eat = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.EAT.getValue());
        this.hzLaiZi = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.HZ_LAI_ZI.getValue());
        this._258Eye = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule._258_EYE.getValue());
        this.piaoScore = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.PIAO_SCORE.getValue());
        this.louPeng = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.LOU_PENG.getValue());
        this.wypn = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.WYPN.getValue());
        this.fangBar = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.FANGBAR.getValue());
        this.gen = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & EZZMJPlayRule.GEN.getValue());
        if (this.fangBar) {
            this.fangGangScore = 1;
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
            if (this.hzLaiZi) {
                this.allCard.add(MahjongUtil.MJ_Z_FENG);
            }
        }

        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doDealAfter() {
        super.doDealAfter();
        if (this.hzLaiZi) {
            this.laiZiCard = MahjongUtil.MJ_Z_FENG;
        }
    }

    @Override
    protected void initBankerIndex() {
        if (1 == this.curBureau) {
            if (0 == this.bankerType) {
                // 随机
                this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            } else {
                // TODO 第一个不一定是第一个近来
                this.bankerIndex = 0;
            }
        }
    }

    @Override
    protected void doStart1() {
        if (this.piaoScore) {
            this.beginXuanPiao();
        } else {
            this.doStartTake();
        }
    }

    @Override
    public void endXuanPiao() {
        super.endXuanPiao();
        this.doStartTake();
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new ZZMJMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    public boolean isCanTakeCard(IMahjongPlayer player, byte card) {
        if (!super.isCanTakeCard(player, card)) {
            return false;
        }
        return MahjongUtil.MJ_Z_FENG != card;
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (this.hzLaiZi && MahjongUtil.MJ_Z_FENG == takeCard) {
            return false;
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if (this.hzLaiZi && MahjongUtil.MJ_Z_FENG == takeCard) {
            return false;
        }
        if (this.louPeng && player.isPassCard(EActionOp.BUMP, takeCard)) {
            return false;
        }
        return super.isBump(player, takeCard);
    }

    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        if (!this._eat) {
            return false;
        }
        return super.isEat(player, takeCard);
    }

    @Override
    public boolean isMustHu() {
        return this.mustHu;
    }

    @Override
    public boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        if (EBarType.BAR_MING == type) {
            return player.getLastFumbleCard() == barCard;
        }
        return true;
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
                if(j>27&&j!=32)continue;
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
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        if (!ziMo && player.isPass(EActionOp.HU)) {
            return false;
        }
        boolean hu = false;
        do {
            if (EActionOp.BAR == this.curAction) {
                // 抢杠胡
                if (this.qgh) {
                    hu = true;
                    break;
                }
                break;
            }
            if (this.onlyZiMo && !ziMo) {
                break;
            }
            hu = true;
        } while (false);
        if (hu) {
            if (this._258Eye) {
                return MahjongUtil.isHu258Eye(player.getHandCardRaw(), player.getCPGNodeCnt(), this.seven, this.laiZiCard);
            } else {
                return MahjongUtil.isHu(player.getHandCardRaw(), player.getCPGNodeCnt(), this.seven, this.laiZiCard);
            }
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
        return 1;
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        super.onHu(takePlayer, player1, player2, player3, huCard);
        // 抓鸟
        if (this.niaoType > 0) {
            IMahjongBird bird = IMahjongBird.get(this.niaoType);
            do {
                if (null == bird) {
                    break;
                }
                int num = this.huType;
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
                if (takePlayer.getUid() == player1.getUid()) {
                    // 自摸
                    int niaoScore = 0;
                    for (int i = 0; i < this.playerNum; ++i) {
                        IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                        if (null == player || player.isGuest() || player.getUid() == takePlayer.getUid()) {
                            continue;
                        }
                        int value = bird.calcNiaoScore(takePlayer, player, this.niaoScore);
                        player.setScore(Score.MJ_CUR_NIAO_SCORE, -value, false);
                        niaoScore += value;
                    }
                    takePlayer.setScore(Score.MJ_CUR_NIAO_SCORE, niaoScore, false);
                } else {
                    // 点炮
                    int niaoScore = 0;
                    if (null != player1) {
                        int value = bird.calcNiaoScore(takePlayer, player1, this.niaoScore);
                        player1.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        niaoScore += value;
                    }
                    if (null != player2) {
                        int value = bird.calcNiaoScore(takePlayer, player2, this.niaoScore);
                        player2.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        niaoScore += value;
                    }
                    if (null != player3) {
                        int value = bird.calcNiaoScore(takePlayer, player3, this.niaoScore);
                        player3.setScore(Score.MJ_CUR_NIAO_SCORE, value, false);
                        niaoScore += value;
                    }
                    takePlayer.setScore(Score.MJ_CUR_NIAO_SCORE, -niaoScore, false);
                }
            } while (false);
        }

        if (takePlayer.getUid() == player1.getUid()) {
            // 自摸
            int score = 2;
            if (this.gen) {
                score += player1.getCPGNodeCntWithType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI, CPGNode.EType.BAR_MING, CPGNode.EType.BAR_FANG);
            }
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
                if (null == player || player.isGuest() || player.getUid() == player1.getUid()) {
                    continue;
                }
                int temp = score;
                if (this.bankerInc && (this.bankerIndex == player.getIndex() || this.bankerIndex == player1.getIndex())) {
                    ++temp;
                }
                int piao = ((IXuanPiao) player1).getPiao() + ((IXuanPiao) player).getPiao();
                player.addScore(Score.MJ_CUR_HU_SCORE, -temp, false);
                player.addScore(Score.MJ_CUR_PIAO_SCORE, -piao, false);
                player1.addScore(Score.MJ_CUR_HU_SCORE, temp, false);
                player1.addScore(Score.MJ_CUR_PIAO_SCORE, piao, false);
                if (this.wypn) {
                    player.addScore(Score.MJ_CUR_WYPN_SCORE, -2, false);
                    player1.addScore(Score.MJ_CUR_WYPN_SCORE, 2, false);
                }
            }
            player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), EPaiXing.ZZMJ_NORMAL, huCard);
        } else {
            if (null != player1) {
                int score = 1;
                if (this.gen) {
                    score += player1.getCPGNodeCntWithType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI, CPGNode.EType.BAR_MING, CPGNode.EType.BAR_FANG);
                }
                if (this.bankerInc && (this.bankerIndex == takePlayer.getIndex() || this.bankerIndex == player1.getIndex())) {
                    ++score;
                }
                int piao = ((IXuanPiao) takePlayer).getPiao() + ((IXuanPiao) player1).getPiao();
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -score, false);
                takePlayer.addScore(Score.MJ_CUR_PIAO_SCORE, -piao, false);
                player1.addScore(Score.MJ_CUR_HU_SCORE, score, false);
                player1.addScore(Score.MJ_CUR_PIAO_SCORE, piao, false);
                player1.addHu(takePlayer.getUid() == player1.getUid() ? -1 : takePlayer.getUid(), EPaiXing.ZZMJ_NORMAL, huCard);
                if (this.wypn) {
                    takePlayer.addScore(Score.MJ_CUR_WYPN_SCORE, -2, false);
                    player1.addScore(Score.MJ_CUR_WYPN_SCORE, 2, false);
                }
            }
            if (null != player2) {
                int score = 1;
                if (this.gen) {
                    score += player2.getCPGNodeCntWithType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI, CPGNode.EType.BAR_MING, CPGNode.EType.BAR_FANG);
                }
                if (this.bankerInc && (this.bankerIndex == takePlayer.getIndex() || this.bankerIndex == player2.getIndex())) {
                    ++score;
                }
                int piao = ((IXuanPiao) takePlayer).getPiao() + ((IXuanPiao) player2).getPiao();
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -score, false);
                takePlayer.addScore(Score.MJ_CUR_PIAO_SCORE, -piao, false);
                player2.addScore(Score.MJ_CUR_HU_SCORE, score, false);
                player2.addScore(Score.MJ_CUR_PIAO_SCORE, piao, false);
                player2.addHu(takePlayer.getUid() == player2.getUid() ? -1 : takePlayer.getUid(), EPaiXing.ZZMJ_NORMAL, huCard);
                if (this.wypn) {
                    takePlayer.addScore(Score.MJ_CUR_WYPN_SCORE, -2, false);
                    player2.addScore(Score.MJ_CUR_WYPN_SCORE, 2, false);
                }
            }
            if (null != player3) {
                int score = 1;
                if (this.gen) {
                    score += player3.getCPGNodeCntWithType(CPGNode.EType.BAR_AN, CPGNode.EType.BAR_LAIZI, CPGNode.EType.BAR_PI, CPGNode.EType.BAR_MING, CPGNode.EType.BAR_FANG);
                }
                if (this.bankerInc && (this.bankerIndex == takePlayer.getIndex() || this.bankerIndex == player3.getIndex())) {
                    ++score;
                }
                int piao = ((IXuanPiao) takePlayer).getPiao() + ((IXuanPiao) player3).getPiao();
                takePlayer.addScore(Score.MJ_CUR_HU_SCORE, -score, false);
                takePlayer.addScore(Score.MJ_CUR_PIAO_SCORE, -piao, false);
                player3.addScore(Score.MJ_CUR_HU_SCORE, score, false);
                player3.addScore(Score.MJ_CUR_PIAO_SCORE, piao, false);
                player3.addHu(takePlayer.getUid() == player3.getUid() ? -1 : takePlayer.getUid(), EPaiXing.ZZMJ_NORMAL, huCard);
                if (this.wypn) {
                    takePlayer.addScore(Score.MJ_CUR_WYPN_SCORE, -2, false);
                    player3.addScore(Score.MJ_CUR_WYPN_SCORE, 2, false);
                }
            }
        }

        ZZMJResultRecordAction resultRecordAction = (ZZMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        resultRecordAction.addNiaoList(this.niaoList);

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            int score = this.getScore((player.getScore(Score.MJ_CUR_GANG_SCORE, false)
                    + player.getScore(Score.MJ_CUR_NIAO_SCORE, false)
                    + player.getScore(Score.MJ_CUR_WYPN_SCORE, false)
                    + player.getScore(Score.MJ_CUR_PIAO_SCORE, false)
                    + player.getScore(Score.MJ_CUR_HU_SCORE, false)));
            player.addScore(Score.SCORE, score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            ZZMJResultRecordAction.PlayerInfo playerInfo = new ZZMJResultRecordAction.PlayerInfo();

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

            ZZMJResultRecordAction.ScoreInfo scoreInfo = new ZZMJResultRecordAction.ScoreInfo();
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

        if (null == player2) {
            this.bankerIndex = player1.getIndex();
        } else {
            this.bankerIndex = takePlayer.getIndex();
        }
        this.getRecord().save();
        this.record();
        this.stop();
    }

    @Override
    public void onHuangZhuang(boolean next) {
        ZZMJResultRecordAction resultRecordAction = (ZZMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.addScore(Score.SCORE, this.getScore(
                    player.getScore(Score.MJ_CUR_GANG_SCORE, false)
            ), false);
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            ZZMJResultRecordAction.PlayerInfo playerInfo = new ZZMJResultRecordAction.PlayerInfo();
            ZZMJResultRecordAction.ScoreInfo scoreInfo = new ZZMJResultRecordAction.ScoreInfo();
            scoreInfo.setNiaoScore(player.getScore(Score.MJ_CUR_NIAO_SCORE, false));
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
        PCLIMahjongNtfGameOverInfoByZZMJ info = new PCLIMahjongNtfGameOverInfoByZZMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;

        info.niaoList.addAll(this.niaoList);

        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByZZMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByZZMJ.PlayerInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByZZMJ.ScoreInfo();
            ((PCLIMahjongNtfGameOverInfoByZZMJ.ScoreInfo) playerInfo.score).niaoScore = player.getScore(Score.MJ_CUR_NIAO_SCORE, false);
            ((PCLIMahjongNtfGameOverInfoByZZMJ.ScoreInfo) playerInfo.score).wypn = player.getScore(Score.MJ_CUR_WYPN_SCORE, false);
            ((PCLIMahjongNtfGameOverInfoByZZMJ.ScoreInfo) playerInfo.score).piao = player.getScore(Score.MJ_CUR_PIAO_SCORE, false);
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
    public IRoomPlayer createPlayer() {
        return new ZZMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
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
