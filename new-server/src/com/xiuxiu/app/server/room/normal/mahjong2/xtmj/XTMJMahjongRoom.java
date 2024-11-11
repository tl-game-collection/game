package com.xiuxiu.app.server.room.normal.mahjong2.xtmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfDeskInfoByXTMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGameOverInfoByXTMJ;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfGangScoreInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJWithXTMJ;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.*;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongTakeAction;
import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;
import com.xiuxiu.app.server.room.normal.mahjong2.bird.IMahjongBird;
import com.xiuxiu.app.server.room.normal.mahjong2.bird._159MahjongBird;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongFeatures;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHelper;
import com.xiuxiu.app.server.room.normal.mahjong2.utils.MahjongHu;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.MahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.XTMJMahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong2.*;

import java.util.*;

/**
 * 仙桃麻将
 */
//@GameInfo(gameType = GameType.GAME_TYPE_XTHH)
public class XTMJMahjongRoom extends MahjongRoom {
    private long features;

    private byte fangPai = -1;                              // 翻出的牌
    protected ArrayList<Byte> piList = new ArrayList<>();   // 皮列表
    protected List<Byte> horseList = new ArrayList<>();     // 马牌列表
    private int laiZiRule = 1;                                  //  1: (默认) 一赖到底  0: 必掷
    private int buyHorse = 2;                                   //  1: 159 买马  2: (默认) 无马
    private int barLaiZi = 0;                                   //  1: 所有玩家飘癞子的个数;
    // 配置：玩法
    private int playingRules = 0;
    private static final int RULE_WITH_TIMER = 0x0001;          // 计时器
    private static final int RULE_ZUO_CHONG_KE_HU = 0x0002;     // 捉铳可胡 （点炮）
    private static final int RULE_GANG_SHANG_KAI_HUA = 0x0004;  // 杠上开花翻倍
    private static final int RULE_PIAO_LAI_JIANG = 0x0008;      // 飘赖有奖
    private static final int RULE_HEI_SHANG_HEI = 0x0010;       // 黑上黑

    public XTMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public XTMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.playingRules = this.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        if ((this.playingRules & RULE_WITH_TIMER) != 0) {
            this.timeout = 10 * 1000;
        }
        this.laiZiRule = this.getRule().getOrDefault(RoomRule.RR_XTHH_LAIZI_RULE, 1);
        this.buyHorse = this.getRule().getOrDefault(RoomRule.RR_MJ_BUY_HORSE, 2);
    }

    @Override
    protected void doShuffle() {
        // 默认包含序数牌和字牌
        this.features = MahjongFeatures.WITH_TONG_ZI | MahjongFeatures.WITH_TIAO_ZI | MahjongFeatures.WITH_WAN_ZI;
        if (this.getCurPlayerCnt() <= 3) { // 2人和3人时去掉万子
            this.features ^= MahjongFeatures.WITH_WAN_ZI;
        }

        this.features |= MahjongFeatures.ENABLE_PENG | MahjongFeatures.ENABLE_MING_GANG | MahjongFeatures.ENABLE_AN_GANG;
        if (Switch.USE_CARD_LIB) {
            MahjongHelper.fixWithLibrariedCards(this.allCard);
        }
        if (this.allCard.isEmpty()) {
            MahjongHelper.fixWithFeaturedCards(this.allCard, this.features);
            MahjongHelper.shuffle(this.allCard);
        }
    }

    @Override
    protected void doDealAfter() {
        // 发完牌后，牌墙上第一张翻出
        this.fangPai = this.allCard.removeFirst();
        this.laiZiCard = MahjongHelper.getLaiZiCardByFengJian(this.fangPai);   //确定赖子
    }

    @Override
    protected void doStart1() {
        this.doStartTake();
    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJWithXTMJ info = new PCLIRoomNtfBeginInfoByMJWithXTMJ();
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
            info.fangPai = this.fangPai;
            info.laiZi = this.laiZiCard;
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, info);
        }
        PCLIRoomNtfBeginInfoByMJWithXTMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJWithXTMJ();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.d = false;
        roomBeginInfo.piList = this.piList;
        roomBeginInfo.fangPai = this.fangPai;
        roomBeginInfo.laiZi = this.laiZiCard;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }


    @Override
    protected boolean isHu(IMahjongPlayer player, long takePlayerUid, boolean ziMo, byte huCard) {
        XTMJMahjongPlayer player1 = (XTMJMahjongPlayer) player;
        player1.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
        player1.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        player1.clearPaiXing();

        // 打癞子不能胡
        if (!ziMo && this.isLaiZi(huCard)) {
            return false;
        }

        // 漏胡不能胡
        if (!ziMo && player.isPass(EActionOp.HU)) {
            return false;
        }

        // 手持赖子的数量是否满足胡牌要求
        int laiZiCount = player.getHandCardCnt(this.laiZiCard);
        if (laiZiCount > this.laiZiRule) {
            return false;
        }

        int normalCPGCnt = 0;   // 常规杠牌的次数（非皮非赖）
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = (byte) player.getHandCardCnt((byte) i);
        }
        for (CPGNode node : player.getCPGNode()) {
            tempCards.get()[node.getCard1()] += 1;
            if (node.getType() != CPGNode.EType.BAR_PI && node.getType() != CPGNode.EType.BAR_LAIZI) {
                normalCPGCnt++;
            }
        }

        long hu = 0;
        int points = 0;
        do {
            if (hu == 0) {
                if (!MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt, false, this.laiZiCard, Integer.MAX_VALUE)) {
                    break;
                }
                if (!ziMo) {
                    // 未勾选“捉铳可胡”时，只能自摸胡
                    if (!this.enabledZCKH()) {
                        return false;
                    }
                    // 手上有癞子 只能自摸胡
                    if (player.getHandCardCnt(this.laiZiCard) != 0) {
                        return false;
                    }
                    //出现过飘癞 只能自摸胡
                    boolean piaoLaiZi = false;
                    for (int i = 0; i < this.playerNum; i++) {
                        MahjongPlayer mjPlayer = (MahjongPlayer) this.allPlayer[i];
                        if (mjPlayer != null && !mjPlayer.isGuest()) {
                            for (CPGNode node : mjPlayer.getCPGNode()) {
                                if (node.getType() == CPGNode.EType.BAR_LAIZI) {
                                    piaoLaiZi = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (piaoLaiZi) {
                        return false;
                    }
                }

                hu |= MahjongHu.PI_HU;
                player.addPaiXing(EPaiXing.XTMJ_NORMAL);
                if (MahjongUtil.isHu(player.getHandCardRaw(), normalCPGCnt)) {
                    player.addPaiXing(EPaiXing.XTMJ_YING);
                }

                if (ziMo && EActionOp.BAR == this.prevAction) { // 杠上开花
                    hu |= MahjongHu.GANG_SHANG_KAI_HUA;
                    player.addPaiXing(EPaiXing.XTMJ_GANG_KAI);
                } else if (EActionOp.BAR == this.curAction) {    // 抢杠胡
                    hu |= MahjongHu.QIANG_GANG_HU;
                    player.addPaiXing(EPaiXing.XTMJ_QIANG_GANG_HU);
                } else if (EActionOp.BAR == this.prevPrevAction) {    // 杠上炮
                    hu |= MahjongHu.GANG_SHANG_PAO;
                    player.addPaiXing(EPaiXing.XTMJ_GANG_SHANG_PAO);
                }
                if (ziMo) {
                    hu |= MahjongHu.ZI_MO;
                }
            }
            points = this.calcPoints(hu);
        } while (false);

        if (hu != 0) {
            player.setScore(Score.MJ_CUR_FANG_SCORE, points, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, 2, false);
        } else {
            player.clearPaiXing();
            player.setScore(Score.MJ_CUR_FANG_SCORE, 0, false);
            player.setScore(Score.MJ_CUR_HU_TYPE, 0, false);
        }
        return hu != 0;
    }

    @Override
    public void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard) {
        boolean isBird = false;
        super.onHu(takePlayer, player1, player2, player3, huCard);
        IMahjongBird bird = new _159MahjongBird();
        if (this.buyHorse == 1) {
            bird.setCnt(1);
            byte horseCard = this.allCard.removeFirst();
            if (null != player2) {
                isBird = bird.isHit(this, takePlayer, horseCard);
            } else {
                isBird = bird.isHit(this, player1, horseCard);
            }
            this.horseList.add(horseCard);
        }

        List<IMahjongPlayer> winners = Arrays.asList(player1, player2, player3);
        for (IMahjongPlayer winner : winners) {
            if (winner != null) {
                winner.addHandCard(huCard);
                this.calcHuScore(takePlayer, winner, isBird);
                winner.delHandCard(huCard);

                for (EPaiXing px : winner.getAllPaiXing()) {
                    winner.addHu(takePlayer.getUid() == winner.getUid() ? -1 : takePlayer.getUid(), px, huCard);
                }
            }
        }

        for (IRoomPlayer player : this.allPlayer) {
            if (player == null || player.isGuest()) {
                continue;
            }
            int score = this.getScore(player.getScore(Score.MJ_CUR_HU_SCORE, false));
            Logs.ROOM.debug("=== onHu player:：%d，SCORE：%d", player.getUid(), score);
            player.addScore(Score.SCORE, -score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, -score, true);
            player1.addScore(Score.SCORE, score, false);
            player1.addScore(Score.ACC_TOTAL_SCORE, score, true);
            score = this.getScore(player.getScore(Score.MJ_CUR_GANG_SCORE, false));
            player.addScore(Score.SCORE, score, false);
            player.addScore(Score.ACC_TOTAL_SCORE, score, true);
        }

        XTMJResultRecordAction resultRecordAction = (XTMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        resultRecordAction.addHorseList(this.horseList);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            XTMJResultRecordAction.PlayerInfo playerInfo = new XTMJResultRecordAction.PlayerInfo();

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
            XTMJResultRecordAction.ScoreInfo scoreInfo = new XTMJResultRecordAction.ScoreInfo();
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
    protected void doSendGameOver(boolean next) {
        PCLIMahjongNtfGameOverInfoByXTMJ info = new PCLIMahjongNtfGameOverInfoByXTMJ();
        info.roomType = this.roomType.ordinal();
        info.bureau = this.curBureau;
        info.next = next;
        info.horseList.addAll(this.horseList);
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (player == null || player.isGuest()) {
                continue;
            }
            PCLIMahjongNtfGameOverInfoByXTMJ.PlayerInfo playerInfo = new PCLIMahjongNtfGameOverInfoByXTMJ.PlayerInfo();
            player.addHandCardTo(playerInfo.handCard);
            List<HuInfo> huList = player.getHuList();
            for (HuInfo huInfo : huList) {
                PCLIMahjongNtfGameOverInfoByXTMJ.HuInfo temp = new PCLIMahjongNtfGameOverInfoByXTMJ.HuInfo();
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

            playerInfo.score = new PCLIMahjongNtfGameOverInfoByXTMJ.ScoreInfo();
            playerInfo.score.fangScore = player.getScore(Score.MJ_CUR_HU_SCORE, false);
            playerInfo.score.gangScore = player.getScore(Score.MJ_CUR_GANG_SCORE, false);
            playerInfo.score.score = this.getFormatScore(player.getScore(Score.SCORE, false));
            playerInfo.score.totalScore = this.getFormatScore(player.getScore());

            if (!next) {
                playerInfo.finalResult = new PCLIMahjongNtfGameOverInfoByXTMJ.FinalResult();
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
        Logs.ROOM.debug("CLI_NTF_ROOM_GAMEOVER %s", info);
    }

    @Override
    protected boolean onCheckOver() {
        return this.allCard.size() == this.curPlayerCnt;
    }

    @Override
    public void onHuangZhuang(boolean next) {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[(this.curOpIndex + 1) % this.playerNum];
            if (null == player || player.isGuest()) {
                continue;
            }
            byte lastCard = this.allCard.removeFirst();
            this.setCurOp(player, EActionOp.FUMBLE, lastCard);
            if (this.isHu0(player, -1, true, lastCard)) {
                this.onHu(player, player, lastCard);
                return;
            }
        }
        XTMJResultRecordAction resultRecordAction = (XTMJResultRecordAction) ((MahjongRecord) this.getRecord()).addResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }

            XTMJResultRecordAction.PlayerInfo playerInfo = new XTMJResultRecordAction.PlayerInfo();

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
            XTMJResultRecordAction.ScoreInfo scoreInfo = new XTMJResultRecordAction.ScoreInfo();
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
    public void syncDeskInfo(IPlayer player) {
        IMahjongPlayer mahjongPlayer = (IMahjongPlayer) this.getRoomPlayer(player.getUid());
        if (null == mahjongPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }

        PCLIMahjongNtfDeskInfoByXTMJ deskInfo = new PCLIMahjongNtfDeskInfoByXTMJ();
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
                deskInfo.allOnlineState.put(other.getUid(), !other.isOffline());
                PCLIMahjongNtfDeskInfoByXTMJ.DeskPlayerInfo deskPlayerInfo = new PCLIMahjongNtfDeskInfoByXTMJ.DeskPlayerInfo();
                deskPlayerInfo.totalScore = this.getFormatScore(other.getScore() + (other.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint * 100));
                deskPlayerInfo.remainCard = other.getHandCardCnt();
                for (CPGNode node : other.getCPGNode()) {
                    PCLIMahjongNtfDeskInfoByXTMJ.CardNode cardNode = new PCLIMahjongNtfDeskInfoByXTMJ.CardNode();
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
    protected int getFang(IMahjongPlayer player) {
        return this.isHu(player) ? player.getScore(Score.MJ_CUR_FANG_SCORE, false) : 0;
    }

    @Override
    protected EPaiXing getPaiXing(IMahjongPlayer player) {
        return null;
    }

    @Override
    public void onTake(IMahjongPlayer takePlayer, boolean auto, Object... param) {
        byte card = (byte) param[0];
        if (this.isLaiZi(card)) {
            takePlayer.addScore(Score.MJ_CUR_TAKE_LAIZI_CNT, 1, false);
            if (auto) {
                super.onBar(takePlayer, takePlayer, param);
            } else {
                super.onTake(takePlayer, auto, param);
            }
        } else {
            super.onTake(takePlayer, auto, param);
        }
    }

    @Override
    protected boolean isBar(IMahjongPlayer player, byte takeCard, boolean fangGang) {
        if (this.isLaiZi(takeCard)) {
            if (fangGang) {
                return false;
            }
            return player.hasHandCard(takeCard, 1);
        }
        if (takeCard == this.fangPai) {
//            player.addScore(Score.MJ_CUR_AN_GANG_CNT, 1, false);
//            player.addScore(Score.ACC_MJ_AN_GANG_CNT, 1, true);
            if (fangGang) {
                return player.hasHandCard(takeCard, 2);
            } else {
                return player.hasHandCard(takeCard, 3);
            }
        }
        return super.isBar(player, takeCard, fangGang);
    }

    @Override
    protected void doBarAfter(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        MahjongWaitAction waitAction = null;
        if (EBarType.BAR_MING == type) {
            waitAction = this.getWaitAction(player, barCard, true,-1);
        }
        if (null != waitAction) {
            this.addAction(waitAction);
        } else {
            this.checkBarScore(takePlayer, player, type, barCard);
            if (this.onCheckOver()) {
                this.onHuangZhuang(this.curBureau < this.bureau);
            } else {
                if (barCard == this.fangPai) {
                    MahjongTakeAction action = new MahjongTakeAction(this, player, player.getTimeout(this.timeout));
                    this.addAction(action);
                } else {
                    this.onFumble(player);
                }
            }
        }
    }

    @Override
    protected void calcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type) {
        BarScoreRecordAction barScoreRecordAction = ((MahjongRecord) this.getRecord()).addBarScoreRecordAction();

        PCLIMahjongNtfGangScoreInfo info = new PCLIMahjongNtfGangScoreInfo();
        if (EBarType.BAR_FANG == type) {    // 放杠
            int score = 1;
            score *= (int) Math.pow(2, this.barLaiZi);
            this.barScoreRecord.add(new BarScoreRecord(takePlayer.getUid(), player.getUid(), type, score));
            takePlayer.addScore(Score.MJ_CUR_GANG_SCORE, -score, false);
            player.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
            info.totalScore.put(takePlayer.getUid(), this.getCurScore(takePlayer));
            info.totalScore.put(player.getUid(), this.getCurScore(player));
            score *= this.endPoint;
            info.gangScore.put(takePlayer.getUid(), -score);
            info.gangScore.put(player.getUid(), score);
            barScoreRecordAction.addBarScore(takePlayer.getUid(), -score);
            barScoreRecordAction.addBarScore(player.getUid(), score);
        } else if (EBarType.BAR_LAIZI == type) {  // 癞子杠
            if (this.enabledPLYJ()) {
                this.barLaiZi += 1;
                int score = (int) Math.pow(2, this.barLaiZi - 1);
                this.barScoreRecord.add(new BarScoreRecord(takePlayer.getUid(), takePlayer.getUid(), type, score));
                for (int i = 0; i < this.playerNum; ++i) {
                    IMahjongPlayer temp = (IMahjongPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest() || temp.isOver()) {
                        continue;
                    }
                    if (player.getUid() == temp.getUid()) {
                        continue;
                    }
                    player.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
                    info.totalScore.put(player.getUid(), this.getCurScore(player));
                    score *= this.endPoint;
                    info.gangScore.put(player.getUid(), info.gangScore.getOrDefault(player.getUid(), 0) + score);
                    barScoreRecordAction.addBarScore(player.getUid(), score);

                    temp.addScore(Score.MJ_CUR_GANG_SCORE, -score, false);
                    info.totalScore.put(temp.getUid(), this.getCurScore(temp));
                    score *= this.endPoint;
                    info.gangScore.put(temp.getUid(), info.gangScore.getOrDefault(temp.getUid(), 0) - score);
                    barScoreRecordAction.addBarScore(temp.getUid(), -score);
                }
            }
        } else {
            this.barScoreRecord.add(new BarScoreRecord(takePlayer.getUid(), takePlayer.getUid(), type, EBarType.BAR_AN == type ? 2 : 1));
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer temp = (IMahjongPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest() || temp.isOver()) {
                    continue;
                }
                if (player.getUid() == temp.getUid()) {
                    continue;
                }
                int score = EBarType.BAR_AN == type ? 2 : 1;
                score *= (int) Math.pow(2, this.barLaiZi);
                player.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
                info.totalScore.put(player.getUid(), this.getCurScore(player));
                score *= this.endPoint;
                info.gangScore.put(player.getUid(), info.gangScore.getOrDefault(player.getUid(), 0) + score);
                barScoreRecordAction.addBarScore(player.getUid(), score);

                score = EBarType.BAR_AN == type ? -2 : -1;
                score *= (int) Math.pow(2, this.barLaiZi);
                temp.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
                info.totalScore.put(temp.getUid(), this.getCurScore(temp));
                score *= this.endPoint;
                info.gangScore.put(temp.getUid(), info.gangScore.getOrDefault(temp.getUid(), 0) + score);
                barScoreRecordAction.addBarScore(temp.getUid(), score);
            }
        }
        if (EBarType.BAR_LAIZI == type) {
            for (int i = 0; i < this.playerNum; ++i) {
                IMahjongPlayer temp = (IMahjongPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest() || temp.isOver()) {
                    continue;
                }
                if (player.getUid() == temp.getUid()) {
                    continue;
                }
                player.addScore(Score.MJ_CUR_TAKE_LAIZI_CNT, 1, false);
            }
        }
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_GANG_SCORE_INFO, info);
    }

    @Override
    protected boolean isBump(IMahjongPlayer player, byte takeCard) {
        if ((this.features & MahjongFeatures.ENABLE_PENG) == 0) {
            return false;
        }
        if (player.isPass(EActionOp.BUMP)) {
            return false;
        }
        return !this.isLaiZi(takeCard) && super.isBump(player, takeCard);
    }

    @Override
    public void onBump(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param) {
        super.onBump(takePlayer, player, param);
        byte card = (byte) param[0];
        if (this.fangPai == card) {
            BarScoreRecordAction barScoreRecordAction = ((MahjongRecord) this.getRecord()).addBarScoreRecordAction();
            PCLIMahjongNtfGangScoreInfo info = new PCLIMahjongNtfGangScoreInfo();
            int score = 1;
            score *= (int) Math.pow(2, this.barLaiZi);
            this.barScoreRecord.add(new BarScoreRecord(takePlayer.getUid(), player.getUid(), EBarType.BAR_FANG, score));
            takePlayer.addScore(Score.MJ_CUR_GANG_SCORE, -score, false);
            player.addScore(Score.MJ_CUR_MING_GANG_CNT, 1, false);
            player.addScore(Score.ACC_MJ_MING_GANG_CNT, 1, true);
            player.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
            info.totalScore.put(takePlayer.getUid(), this.getCurScore(takePlayer));
            info.totalScore.put(player.getUid(), this.getCurScore(player));
            score *= this.endPoint;
            info.gangScore.put(takePlayer.getUid(), -score);
            info.gangScore.put(player.getUid(), score);
            barScoreRecordAction.addBarScore(takePlayer.getUid(), -score);
            barScoreRecordAction.addBarScore(player.getUid(), score);
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_GANG_SCORE_INFO, info);
        }
    }


    @Override
    protected boolean isEat(IMahjongPlayer player, byte takeCard) {
        return false;
    }

    @Override
    public boolean canLaiZiBar() {
        return true;
    }

    @Override
    public Record getRecord() {
        // 回放数据
        if (this.record == null) {
            this.record = new XTMJMahjongRecord(this);
        }
        return this.record;
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new XTMJMahjongPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    public void clear() {
        super.clear();
        this.piList.clear();
        this.horseList.clear();
        this.fangPai = -1;
        this.laiZiCard = -1;
        this.barLaiZi = 0;
    }

    // 计算胡牌分数
    private void calcHuScore(IMahjongPlayer takePlayer, IMahjongPlayer player, boolean bird) {
        // 基础分
        int points = player.getScore(Score.MJ_CUR_FANG_SCORE, false);
        int fan = this.getHuFang(player);
        if (player.hasPaiXing(EPaiXing.XTMJ_YING) && takePlayer == player) {
            fan += 1;
        }
        if (bird) {
            fan += 1;
        }
        boolean isZiMo = takePlayer.getUid() == player.getUid();
        // 计算输家分数
        for (IRoomPlayer p : this.allPlayer) {
            if (p == null || p.isGuest() || p.getUid() == player.getUid()) {
                continue;
            }
            XTMJMahjongPlayer otherPlayer = (XTMJMahjongPlayer) p;
            int otherPoints = points;
            Logs.ROOM.debug("\t玩家：%d", otherPlayer.getUid());
            if (!isZiMo && p.getUid() != takePlayer.getUid()) {   //只有点炮方输分
                continue;
            }
            int otherFan = this.getHuFang(otherPlayer);
            otherPoints *= (int) Math.pow(2, fan + otherFan);
            Logs.ROOM.debug("\t\totherPoints:%d, fan:%d, otherFan:%d", otherPoints, fan, otherFan);
            otherPlayer.setScore(Score.MJ_CUR_HU_SCORE, otherPoints, false);
            Logs.ROOM.debug("\t\t最终分数：%d", otherPoints);

        }
    }

    // 获取胡牌翻数
    private int getHuFang(IMahjongPlayer player) {
        // 打赖算1番
        int points = player.getScore(Score.MJ_CUR_TAKE_LAIZI_CNT, false);
        if (this.enabledHSH() && points == 4) {
            points += 1;
        }
        if (this.enabledGKFB() && player.hasPaiXing(EPaiXing.XTMJ_GANG_KAI)) {
            points += 1;
        }

        return points;

    }

    // 胡的点数
    private int calcPoints(long hu) {
        int points = 2;
        if ((hu & MahjongHu.GANG_SHANG_PAO) != 0 || (hu & MahjongHu.QIANG_GANG_HU) != 0) {
            points = 3;
        }
        return points;

    }

    // 捉铳可胡 （点炮）
    private boolean enabledZCKH() {
        return (this.playingRules & RULE_ZUO_CHONG_KE_HU) != 0;
    }

    // 杠上开花翻倍
    private boolean enabledGKFB() {
        return (this.playingRules & RULE_GANG_SHANG_KAI_HUA) != 0;
    }

    // 飘赖有奖
    private boolean enabledPLYJ() {
        return (this.playingRules & RULE_PIAO_LAI_JIANG) != 0;
    }

    // 黑上黑
    private boolean enabledHSH() {
        return (this.playingRules & RULE_HEI_SHANG_HEI) != 0;
    }

}
