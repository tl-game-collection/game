package com.xiuxiu.app.server.room.normal.poker;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfSelectPiaoFixedValueInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfSelectPiaoInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfSelectPiaoValueInfo;
import com.xiuxiu.app.protocol.client.poker.*;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMyHandCardInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.RoomPlayer;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongConstant;
import com.xiuxiu.app.server.room.normal.poker.action.PokerFlutterWaitAction;
import com.xiuxiu.app.server.room.normal.poker.action.PokerPassAction;
import com.xiuxiu.app.server.room.normal.poker.action.PokerTakeAction;
import com.xiuxiu.app.server.room.normal.poker.runFast.ERunFastCardType;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.IPokerXuanPiaoPlayer;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;
import com.xiuxiu.app.server.room.player.poker.RunFastPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.poker.BombScoreRecordAction;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.ResultRecordAction;
import com.xiuxiu.app.server.table.GoodPoker.GoodPokerInfo;
import com.xiuxiu.app.server.table.GoodPokerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class PokerRoom extends Room implements IPokerRoom {
    protected int curTakeIndex = -1;                                    // 当前出牌索引
    protected EPokerCardType curTakeCardType = EPokerCardType.NONE;     // 当前出牌的类型
    protected byte curTakeMaxCard;                                      // 当前牌最大值
    protected byte curTakeCnt;                                          // 当前出牌数量
    protected byte curTakeCardSize;                                     // 当前出牌数量
    protected int passCount = 0;                                        // 跳过次数
    protected boolean firstTake = true;                                 // 首次出牌
    protected List<Byte> lastTakeCard;                                  // 最后打出的牌
    protected IPokerPlayer lastTakeCardPlayer;                          // 最后打出的牌的人
    protected List<Byte> lastTakeCardByRunFast;                         // 最后打出的牌(不销毁，跑得快使用)
    protected IPokerPlayer winPoker;                                    // 赢的玩家

    protected boolean bombTarget = false;                               // 炸弹不能拆
    protected int threeTakeType = 0;                                    // 三带类型
    protected int fourTakeType = 0;                                     // 四带类型
    protected boolean isAAABomb = false;                                // AAA 是炸弹

    protected int firstTakeIndex = 0;
    protected byte firstTakeCard = 0;

    // 斗地主
    protected int landlordType = 0;                                     // 种类：0-经典，1-红黑癞子，2-普通癞子，3 天地癞子，4 运城玩法
    protected int boomScore = 1;                                        // 炸弹积分
    protected int ruleBombTop = 0; // 0-无限制，8-8倍，16-16倍，32-32倍    // 规则-炸弹封顶

    protected int xuanPiaoType = 1;                                     // 选飘类型(1不漂,2每局选漂,3玩家定漂,4固定定漂)
    protected int xuanPiaoValue= 0;                                     // 固定定漂值
    
    public PokerRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public PokerRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }
    
    @Override
    public void init() {
        super.init();
        this.xuanPiaoType = this.info.getRule().getOrDefault(RoomRule.RR_MJ_FLUTTER, 1);
        this.xuanPiaoValue = this.info.getRule().getOrDefault(RoomRule.RR_MJ_FLUTTER_VALUE, 0);
    }

    //检查手牌是否有赖子
    public boolean hasHandLaizi(List<Byte> cards) {
        return false;
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        IPokerPlayer tempPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == tempPlayer) {
            return;
        }
        PCLIPokerNtfDeskInfo deskInfo = new PCLIPokerNtfDeskInfo();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.curBureau = tempPlayer.getBureau();
        deskInfo.card.addAll(tempPlayer.getHandCard());
        deskInfo.lastTakeCard = this.lastTakeCard;
        deskInfo.lastTakePlayerUid = null == this.lastTakeCardPlayer ? -1 : this.lastTakeCardPlayer.getUid();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer temp = (IPokerPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            deskInfo.allScore.put(temp.getUid(), this.getClientScore(temp.getScore()));
            deskInfo.allOnlineState.put(temp.getUid(), temp.isOffline() ? false : true);
            if (temp.getUid() == player.getUid()) {
                continue;
            }
            deskInfo.otherCardCnt.put(temp.getUid(), temp.getHandCard().size());
        }
        tempPlayer.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override
    protected void doFinish(boolean isNormal, boolean isNewBureau) {
        if (!isNormal) {
            if (isNewBureau) {
                if (this.checkIsDestroy()){
                    this.gameOver(false);
                }
            }else {
                this.gameOver(false);
            }
        }
        this.info.setEndTime(System.currentTimeMillis());
        this.saveRoomScore();
    }

    @Override
    public ErrorCode take(Player player, List<Byte> cards, List<Byte> laiZiCards, int cardType) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法打牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法打牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        IAction action = this.action.peek();
        if (action instanceof PokerTakeAction) {
            ErrorCode err = this.checkTake(action, roomPlayer, cards, laiZiCards, cardType);
            if (ErrorCode.OK == err) {
                roomPlayer.clearOperationTimeoutCnt();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是打牌动作, 无法打牌", this);
        return ErrorCode.REQUEST_INVALID;
    }

    protected ErrorCode checkTake(IAction action, IPokerPlayer player, List<Byte> cards, List<Byte> laiZiCards, int cardType) {
        return ErrorCode.REQUEST_INVALID;
    }

    public void onTake(IPokerPlayer player, List<Byte> cards, List<Byte> laiZiCards, EPokerCardType cardType, byte takeMaxCard, byte takeCnt) {
        this.firstTake = false;
        this.passCount = 0;
        player.takeCard(cards);

        this.curTakeMaxCard = takeMaxCard;
        this.curTakeCnt = takeCnt;
        this.curTakeCardSize = (byte) cards.size();
        this.curTakeCardType = cardType;
        this.lastTakeCard = cards;
        this.lastTakeCardPlayer = player;
        this.lastTakeCardByRunFast = cards;

        ((PokerRecord) this.getRecord()).addTakeRecordAction(player.getUid(), cards);

        if (EPokerCardType.BOMB == cardType || EPokerCardType.KING_FRIED == cardType) {
            player.addScore(Score.ACC_POKER_BOMB_CNT, 1, true);
        }

        // check over
        if (player.hasHandCard()) {
            PokerPlayer nextPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
            this.curTakeIndex = nextPlayer.getIndex();

            PokerTakeAction action = new PokerTakeAction(this, nextPlayer, nextPlayer.getTimeout(this.timeout));
            byte[] max = PokerUtil.findLargerThan(nextPlayer.getHandCard(), nextPlayer.getHandCardCnt(), this.curTakeMaxCard, cardType, takeCnt, this.bombTarget, this.isAAABomb);
            Logs.ROOM.debug("%s nextPlayer:%s take %s curTakeMaxCard:%d", this, nextPlayer, Arrays.toString(max), this.curTakeMaxCard);
            boolean isAuto = false;
            if (EPokerCardType.NONE.getValue() == max[0]) {
                action.setCanPass(true);
            } else {
                boolean hasBomb = false;
                action.setCardType(EPokerCardType.parse(max[0]));
                action.setTakeMaxCard(max[max.length - 1]);
                List<Byte> takeCard = new ArrayList<>(max.length - 2);
                for (int i = 1, len = max.length - 1; i < len; ++i) {
                    takeCard.add(max[i]);
                    if (!hasBomb && nextPlayer.isBomb(max[i])) {
                        hasBomb = true;
                    }
                }
                if (hasBomb && nextPlayer.getHandCard().size() == 4) {
                    isAuto = true;
                }
                action.setTakeCnt(takeCnt);
                action.setCards(takeCard);
                if (!hasBomb && takeCard.size() == nextPlayer.getHandCard().size()) {
                    isAuto = true;
                    // 干瞪眼 手牌最后一张自动出
                    if (this.getGameType() == GameType.GAME_TYPE_GDY) {
                        byte tCard = takeCard.get(0);
                        byte nCard = nextPlayer.getHandCard().get(0);
                        if (PokerUtil.getCardValue(tCard) - PokerUtil.getCardValue(nCard) != 1) {
                            isAuto = false;
                        }
                    }
                }
                //跑得快
                if (this.getGameType() == GameType.GAME_TYPE_RUN_FAST) {
                    if (this.curPlayerCnt == 2) {
                        if (player.getHandCard().size() == 1 && takeCard.size() == 1) {
                            takeCard.clear();
                            byte m_maxCard = nextPlayer.getHandCard().get(nextPlayer.getHandCard().size() - 1);
                            takeCard.add(m_maxCard);
                            action.setCards(takeCard);
                            action.setTakeMaxCard(takeCard.get(0));
                        }
                    } else {
                        PokerPlayer lastPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
                        if (lastPlayer.getHandCard().size() == 1 && takeCard.size() == 1) {
                            takeCard.clear();
                            byte m_maxCard = nextPlayer.getHandCard().get(nextPlayer.getHandCard().size() - 1);
                            takeCard.add(m_maxCard);
                            action.setCards(takeCard);
                            action.setTakeMaxCard(takeCard.get(0));
                        }
                    }
                }
                if (this.isAAABomb && nextPlayer.hasCardCnt(PokerUtil._A, 3) && nextPlayer.getHandCard().size() > 3) {
                    isAuto = false;
                }
            }

            if (0 == this.info.getRule().getOrDefault(RoomRule.RR_RF_MUST_PUT, 1)) {
                action.setCanPass(true);
            } else {
                if (action.isCanPass()) {
                    action.setTimeout(500);
                    action.setAuto(Boolean.TRUE);
                }
            }
            if (isAuto) {
                action.setCanPass(false);
                action.setTimeout(500);
                action.setAuto(Boolean.TRUE);
            }
            this.broadcast2Client(CommandId.CLI_NTF_POKER_TAKE, new PCLIPokerNtfTakeInfo(player.getUid(), cards, cardType.getValue(), nextPlayer.getUid(), laiZiCards));
            this.broadcast2Client(CommandId.CLI_NTF_POKER_AUTO_MODE, new PCLIPokerNetAutoMode(player.getUid(), ((RoomPlayer) player).isAutoMode()));
            this.addAction(action);
            this.sendMyCard();
        } else {
            this.broadcast2Client(CommandId.CLI_NTF_POKER_TAKE, new PCLIPokerNtfTakeInfo(player.getUid(), cards, cardType.getValue(), -1, laiZiCards));
            //最后一手为炸弹
            if (EPokerCardType.BOMB == this.curTakeCardType || EPokerCardType.KING_FRIED == this.curTakeCardType) {
                // calc bomb score
                BombScoreRecordAction bombScoreRecordAction = ((PokerRecord) this.getRecord()).addBombScoreRecordAction();
                for (int i = 0; i < this.playerNum; ++i) {
                    IPokerPlayer tempPlayer = (IPokerPlayer) this.allPlayer[i];
                    if (null == tempPlayer || tempPlayer.isGuest()) {
                        continue;
                    }
                    if (this.lastTakeCardPlayer.getUid() != tempPlayer.getUid()) {
                        int score = this.getBombScore(this.lastTakeCardPlayer, tempPlayer);
                        this.lastTakeCardPlayer.addScore(Score.POKER_BOMB_SCORE, score, false);
                        tempPlayer.addScore(Score.POKER_BOMB_SCORE, -score, false);

                        bombScoreRecordAction.addScore(this.lastTakeCardPlayer.getUid(), score);
                        bombScoreRecordAction.addScore(tempPlayer.getUid(), -score);
                    }
                }
            }

            // TODO finish
            this.winPoker = player;
            this.gameOver(this.checkAgain());
            this.bankerIndex = this.winPoker.getIndex();
            this.stop();
        }
    }

    @Override
    public ErrorCode pass(Player player) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法跳过", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法跳过", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof PokerPassAction) {
            if (player.getUid() != ((PokerPassAction) action).getPlayer().getUid()) {
                Logs.ROOM.warn("%s 当前轮跳过人是:%s 而不是你:%s 无效跳过", this, ((PokerPassAction) action).getPlayer(), player);
                return ErrorCode.REQUEST_INVALID;
            }
            this.tick();
            return ErrorCode.OK;
        }
        if (action instanceof PokerTakeAction) {
            if (player.getUid() != ((PokerTakeAction) action).getPlayer().getUid()) {
                Logs.ROOM.warn("%s 当前轮跳过人是:%s 而不是你:%s 无效跳过", this, ((PokerTakeAction) action).getPlayer(), player);
                return ErrorCode.REQUEST_INVALID;
            }

            if (((PokerTakeAction) action).isCanPass()) {
                ((PokerTakeAction) action).getPlayer().clearOperationTimeoutCnt();
                this.tick();
                return ErrorCode.OK;
            }
        }
        Logs.ROOM.warn("%s 本轮不是跳过动作, 无法跳过", this);
        return ErrorCode.REQUEST_INVALID;
    }

    public void onPass(PokerPlayer player) {
        ++this.passCount;
        ((PokerRecord) this.getRecord()).addPassRecordAction(player.getUid());

        if (this.getCurPlayerCnt() - 1 == this.passCount) {
            // 跳过 一个轮回
            if (EPokerCardType.BOMB == this.curTakeCardType || EPokerCardType.KING_FRIED == this.curTakeCardType) {
                // calc bomb score
                BombScoreRecordAction bombScoreRecordAction = ((PokerRecord) this.getRecord()).addBombScoreRecordAction();
                //PCLIPokerNtfBombScoreInfo bombScoreInfo = new PCLIPokerNtfBombScoreInfo();
                for (int i = 0; i < this.playerNum; ++i) {
                    IPokerPlayer tempPlayer = (IPokerPlayer) this.allPlayer[i];
                    if (null == tempPlayer || tempPlayer.isGuest()) {
                        continue;
                    }
                    if (this.lastTakeCardPlayer.getUid() != tempPlayer.getUid()) {
                        int score = this.getBombScore(this.lastTakeCardPlayer, tempPlayer);
                        this.lastTakeCardPlayer.addScore(Score.POKER_BOMB_SCORE, score, false);
                        tempPlayer.addScore(Score.POKER_BOMB_SCORE, -score, false);

                        bombScoreRecordAction.addScore(this.lastTakeCardPlayer.getUid(), score);
                        bombScoreRecordAction.addScore(tempPlayer.getUid(), -score);
                    }
                }
            }

            this.curTakeMaxCard = -1;
            this.curTakeCnt = 0;
            this.curTakeCardSize = 0;
            this.curTakeCardType = EPokerCardType.NONE;
            this.lastTakeCardPlayer = null;
            this.lastTakeCard = null;
        }
        // next take
        PokerPlayer nextPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
        this.curTakeIndex = nextPlayer.getIndex();

        PokerTakeAction action = new PokerTakeAction(this, nextPlayer, nextPlayer.getTimeout(this.timeout));
        boolean isAuto = false;
        if (this.getCurPlayerCnt() - 1 != this.passCount) {
            byte[] max = PokerUtil.findLargerThan(nextPlayer.getHandCard(), nextPlayer.getHandCardCnt(), this.curTakeMaxCard, this.curTakeCardType, this.curTakeCnt, this.bombTarget, this.isAAABomb);
            Logs.ROOM.debug("%s nextPlayer:%s take %s curTakeMaxCard:%d", this, nextPlayer, Arrays.toString(max), this.curTakeMaxCard);
            if (EPokerCardType.NONE.getValue() == max[0]) {
                action.setCanPass(true);
            } else {
                action.setCardType(EPokerCardType.parse(max[0]));
                action.setTakeMaxCard(max[max.length - 1]);
                List<Byte> takeCard = new ArrayList<>(max.length - 1);
                for (int i = 1, len = max.length - 1; i < len; ++i) {
                    takeCard.add(max[i]);
                }
                action.setTakeCnt(this.curTakeCnt);
                action.setCards(takeCard);
                //跑得快
                if (this.getGameType() == GameType.GAME_TYPE_RUN_FAST) {
                    if (this.curPlayerCnt == 2) {
                        if (player.getHandCard().size() == 1 && takeCard.size() == 1) {
                            takeCard.clear();
                            byte m_maxCard = nextPlayer.getHandCard().get(nextPlayer.getHandCard().size() - 1);
                            takeCard.add(m_maxCard);
                            action.setCards(takeCard);
                            action.setTakeMaxCard(takeCard.get(0));
                        }
                    } else {
                        PokerPlayer lastPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
                        if (lastPlayer.getHandCard().size() == 1 && takeCard.size() == 1) {
                            takeCard.clear();
                            byte m_maxCard = nextPlayer.getHandCard().get(nextPlayer.getHandCard().size() - 1);
                            takeCard.add(m_maxCard);
                            action.setCards(takeCard);
                            action.setTakeMaxCard(takeCard.get(0));
                        }
                    }
                }
            }
        } else {
            // TODO 跳过一轮
            byte[] cardInfo = PokerUtil.getCardType(nextPlayer.getHandCard(), bombTarget, this.threeTakeType, this.fourTakeType, true, 2, false, this.isAAABomb);
            EPokerCardType type = EPokerCardType.parse(cardInfo[0]);

            do {
                if (EPokerCardType.NONE == type || EPokerCardType.FOUR_TAKE_THREE == type || EPokerCardType.WUSHIK_WSKBOMB == type) {
                    break;
                }
                if (nextPlayer.hasBomb() && nextPlayer.getHandCard().size() > 4) {
                    break;
                }
                if (this.isAAABomb && nextPlayer.hasCardCnt(PokerUtil._A, 3) && nextPlayer.getHandCard().size() > 3) {
                    break;
                }
                //斗地主3连队才能出
                if (getGameType() == GameType.GAME_TYPE_LANDLORD && EPokerCardType.DOUBLE_LINE == type && nextPlayer.getHandCard().size() < 6)
                    break;
                //3带对  连对 飞机包含赖子不能出
                if ((EPokerCardType.THREE_TAKE_ONE == type || EPokerCardType.THREE_TAKE_11 == type || EPokerCardType.DOUBLE_LINE == type || EPokerCardType.THREE_LINE == type) && hasHandLaizi(nextPlayer.getHandCard()))
                    break;
                //斗地主红黑3包含对黑3就不能自动出牌
                if (2 == this.info.getRule().getOrDefault(RoomRule.RR_LANDLORD_PLAY, 0)) {
                    if (nextPlayer.getHandCard().size() > 2 && nextPlayer.getHandCard().contains((byte) 39) && nextPlayer.getHandCard().contains((byte) 13))
                        break;
                }
                isAuto = true;
            } while (false);
            if (isAuto) {
                List<Byte> takeCard = new ArrayList<>();
                takeCard.addAll(nextPlayer.getHandCard());
                action.setCards(takeCard);
                //跑得快
                if (this.getGameType() == GameType.GAME_TYPE_RUN_FAST) {
                    if (this.curPlayerCnt == 2) {
                        if (player.getHandCard().size() == 1 && takeCard.size() == 1) {
                            takeCard.clear();
                            byte m_maxCard = nextPlayer.getHandCard().get(nextPlayer.getHandCard().size() - 1);
                            takeCard.add(m_maxCard);
                            action.setCards(takeCard);
                        }
                    } else {
                        PokerPlayer lastPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
                        if (lastPlayer.getHandCard().size() == 1 && takeCard.size() == 1) {
                            takeCard.clear();
                            byte m_maxCard = nextPlayer.getHandCard().get(nextPlayer.getHandCard().size() - 1);
                            takeCard.add(m_maxCard);
                            action.setCards(takeCard);
                        }
                    }
                }
                action.setCardType(EPokerCardType.parse(cardInfo[0]));
                action.setTakeMaxCard(cardInfo[cardInfo.length - 1]);
                action.setTakeCnt(this.curTakeCnt);
                // 斗地主 红黑癞子玩法 最后一手牌为两黑3
                if (1 == this.landlordType && 2 == nextPlayer.getHandCard().size() && nextPlayer.getHandCard().contains(PokerUtil.THREE_PLUM) && nextPlayer.getHandCard().contains(PokerUtil.THREE_SPADES)) {
                    boomScore = Math.min(boomScore * 2, this.ruleBombTop);
                }
            } else {    // 要不起的情况下 最后一手牌 1S后自动出
                this.getCanTakeCard(action, (PokerPlayer) this.allPlayer[this.curTakeIndex]);
            }
        }
        if (0 == this.info.getRule().getOrDefault(RoomRule.RR_RF_MUST_PUT, 1)) {
            action.setCanPass(true);
        } else if (action.isCanPass()) {
            action.setTimeout(500);
            action.setAuto(Boolean.TRUE);
        }
        if (this.getCurPlayerCnt() - 1 == this.passCount) {
            action.setCanPass(false);
            action.setTimeout(nextPlayer.getTimeout(this.timeout));
        }
        if (isAuto) {
            action.setCanPass(false);
            action.setTimeout(500);
            action.setAuto(Boolean.TRUE);
        }

        this.broadcast2Client(CommandId.CLI_NTF_POKER_PASS, new PCLIPokerNtfPassInfo(player.getUid(), nextPlayer.getUid()));
        this.broadcast2Client(CommandId.CLI_NTF_POKER_AUTO_MODE, new PCLIPokerNetAutoMode(player.getUid(), player.isAutoMode()));
        this.addAction(action);
    }

    protected int getBombScore(IPokerPlayer takePlayer, IPokerPlayer losePlayer) {
        return 10 * this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
    }

    protected void gameOver(boolean next) {
        if (null != this.winPoker) {
            for (int i = 0; i < this.playerNum; ++i) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (this.winPoker.getUid() != player.getUid()) {
                    int score = this.getScore(player.getHandCard().size() * (0 == player.getTakeCnt() ? 2 : 1));
                    player.addScore(Score.SCORE, -score, false);
                    this.winPoker.addScore(Score.SCORE, score, false);
                    player.addScore(Score.ACC_LOST_CNT, 1, true);
                    if (player.getScore(Score.SCORE, false) < 0) {
                    	player.addScore(Score.ACC_LOST_CNT_CONTINUE, 1, true);
                    }
                } else {
                    this.winPoker.addScore(Score.ACC_WIN_CNT, 1, true);
                    this.winPoker.setScore(Score.ACC_LOST_CNT_CONTINUE, 0, true);
                }
            }
        }

        ResultRecordAction resultRecordAction = ((PokerRecord) this.getRecord()).addResultRecordAction();

        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            ResultRecordAction.GameOverInfo actionGameOverInfo = new ResultRecordAction.GameOverInfo();
            actionGameOverInfo.getCard().addAll(player.getHandCard());
            actionGameOverInfo.setScore(this.getClientScore(player.getScore(Score.SCORE, false)));
            actionGameOverInfo.setTotalScore(this.getClientScore(player.getScore(Score.ACC_TOTAL_SCORE, true)));
            actionGameOverInfo.setCloseDoor(0 == player.getTakeCnt());
            resultRecordAction.getAllGameOverInfo().put(player.getUid(), actionGameOverInfo);
        }

        this.record();
        this.getRecord().save();
    }

    @Override
    protected void doSendGameOver(boolean next) {
        PCLIPokerNtfGameOverInfo gameOverInfo = new PCLIPokerNtfGameOverInfo();
        gameOverInfo.next = next;
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIPokerNtfGameOverInfo.GameOverInfo info = new PCLIPokerNtfGameOverInfo.GameOverInfo();
            info.card.addAll(player.getHandCard());
            info.score = this.getClientScore(player.getScore(Score.SCORE, false));
            info.gold =this.getFormatScore((int)this.getPlayerGold(player));
            info.totalScore = this.getClientScore(player.getScore());
            info.cardType = ((RunFastPlayer) player).getCardType().getValue();
            info.isCloseDoor = ((RunFastPlayer) player).getCardType() == ERunFastCardType.NORMAL && 0 == player.getTakeCnt();

            if (!next) {
                PCLIPokerNtfGameOverInfo.TotalCnt totalCnt = new PCLIPokerNtfGameOverInfo.TotalCnt();
                totalCnt.lostCnt = player.getScore(Score.ACC_LOST_CNT, true);
                totalCnt.winCnt = player.getScore(Score.ACC_WIN_CNT, true);
                totalCnt.bombCnt = player.getScore(Score.ACC_POKER_BOMB_CNT, true);
                info.totalCnt = totalCnt;
//                gameOverInfo.bureau = totalCnt.winCnt + totalCnt.lostCnt;
                gameOverInfo.bureau = this.curBureau;
            }
            gameOverInfo.allGameOverInfo.put(player.getUid(), info);
        }

        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, gameOverInfo);
    }

    @Override
    public void clear() {
        super.clear();
        this.curTakeIndex = -1;
        this.curTakeMaxCard = -1;
        this.curTakeCardType = EPokerCardType.NONE;
        this.curTakeCnt = 0;
        this.curTakeCardSize = 0;
        this.passCount = 0;
        this.lastTakeCard = null;
        this.lastTakeCardPlayer = null;
        this.lastTakeCardByRunFast = null;
        this.winPoker = null;
        this.firstTakeCard = 0;
        this.firstTakeIndex = 0;
        if (this.xuanPiaoType == 2) {
            // 每局选漂前重置
            for (int i = 0; i < this.playerNum; ++i) {
                IPokerXuanPiaoPlayer player = (IPokerXuanPiaoPlayer) this.allPlayer[i];
                if (null == player) {
                    continue;
                }
                player.setPiaoScore(0);
            }
        } else if((this.xuanPiaoType == 3 || this.xuanPiaoType == 4) && this.curBureau == 1) {
            for (int i = 0; i < this.playerNum; ++i) {
                IPokerXuanPiaoPlayer player = (IPokerXuanPiaoPlayer) this.allPlayer[i];
                if (null == player) {
                    continue;
                }
                // 是否选过漂
                if (getTemporaryPropertyValue(this.allPlayer[i].getUid(), RoomRule.RR_NONE) != 0) {
                    continue;
                }
                player.setPiaoScore(0);
            }
        }
    }

    protected void sendMyCard() {
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (Config.checkWhiteHas(player.getUid(), 1)) {
                PCLIRoomNtfMyHandCardInfo info = new PCLIRoomNtfMyHandCardInfo();
                info.handCard.addAll(player.getHandCard());
                for (int k = 0; k < this.playerNum; ++k) {
                    IPokerPlayer temp = (IPokerPlayer) this.allPlayer[k];
                    if (null == temp || temp.isGuest() || temp.getUid() == player.getUid()) {
                        continue;
                    }
                    List<Byte> tempLst = new ArrayList<>();
                    tempLst.addAll(temp.getHandCard());
                    info.ohc.put(temp.getUid(), tempLst);
                }
                info.rc.addAll(this.allCard);
                Collections.sort(info.rc);
                player.send(CommandId.CLI_NTF_ROOM_MY_CARD, info);
            }
        }
    }

    public void getCanTakeCard(PokerTakeAction action, PokerPlayer player) {
        List<Byte> hand = player.getHandCard();
        for (byte card : hand) {
            if (player.isBomb(card)) {
                continue;
            }
            if (this.isAAABomb && PokerUtil._A == PokerUtil.getCardValue(card) && player.hasCardCnt(card, 3)) {
                continue;
            }
            //跑得快
            if (this.getGameType() == GameType.GAME_TYPE_RUN_FAST) {
                PokerPlayer passPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
                if (passPlayer.getHandCard().size() == 1 && Arrays.asList(card).size() == 1) {
                    if (!player.isCurMaxCard(card)) {
                        continue;
                    }
                }
//                if (this.curPlayerCnt == 2) {
//                    PokerPlayer passPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
//                    if (passPlayer.getHandCard().size() == 1 && Arrays.asList(card).size() == 1) {
//                        if (!player.isCurMaxCard(card)) {
//                            continue;
//                        }
//                    }
//                } else {
//                    PokerPlayer passPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
//                    if (passPlayer.getHandCard().size() == 1 && Arrays.asList(card).size() == 1) {
//                        if (!player.isCurMaxCard(card)) {
//                            continue;
//                        }
//                    }
//                }
            }
            action.setCards(Arrays.asList(card));
            action.setTakeMaxCard(PokerUtil.getCardValue(card));
            action.setCardType(EPokerCardType.SINGLE);
            action.setTakeCnt((byte) 1);
            action.setAuto(Boolean.FALSE);
            return;
        }
        byte card = PokerUtil.getCardValue(player.getHandCard().get(0));
        if (PokerUtil._A == card) {
            List<Byte> takeCard = new ArrayList<>(3);
            for (byte c : hand) {
                if (PokerUtil._A == PokerUtil.getCardValue(c)) {
                    takeCard.add(c);
                    if (3 == takeCard.size()) {
                        break;
                    }
                }
            }
            action.setCards(takeCard);
            action.setTakeMaxCard(card);
            action.setCardType(EPokerCardType.BOMB);
            action.setTakeCnt((byte) 3);
        } else {
            List<Byte> takeCard = new ArrayList<>(4);
            for (byte c : hand) {
                if (card == PokerUtil.getCardValue(c)) {
                    takeCard.add(c);
                    if (4 == takeCard.size()) {
                        break;
                    }
                }
            }
            action.setCards(takeCard);
            action.setTakeMaxCard(card);
            action.setCardType(EPokerCardType.BOMB);
            action.setTakeCnt((byte) 4);
        }
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new PokerRecord(this);
        }
        return this.record;
    }

    public List<Byte> getSortCard(List<Byte> cards) {
        List<Byte> sortCard = new ArrayList<>();
        for (byte i : cards) {
            sortCard.add(PokerUtil.getCardValue(i));
        }
        PokerUtil.sort(sortCard);
        return sortCard;
    }

    public void changeAutoModePoker(IPlayer player, boolean auto) {
        if (!auto) {
            IRoomPlayer player1 = getRoomPlayer(player.getUid());
            if (null != player1) {
                player1.clearOperationTimeoutCnt();
            }
        }
        player.send(CommandId.CLI_NTF_POKER_AUTO_MODE_OK, null);
    }

    
    @Override
    public void beginXuanPiao() {

        // 每局选漂为玩家自行在每局开始时进行选项，确定加漂多少（分类选项：麻将 0/1/2 跑得快0/1/2/3）
        // 玩家定漂为玩家自行在第一局开始时进行选项，确定之后所有局数加漂多少（分类选项：麻将 0/1/2 跑得快0/1/2/3）
        // 固定定漂为创建玩法或房间的玩家选择的固定的加漂数，之后进行游戏的所有玩家固定加漂
        // (1不漂,2每局选漂,3玩家定漂,4固定定漂)
        if (this.xuanPiaoType > 1) {
            // 2每局选漂
            if (this.xuanPiaoType == 2) {
                PokerFlutterWaitAction flutterWaitAction = null;
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer temp = (IRoomPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    if (null == flutterWaitAction) {
                        flutterWaitAction = new PokerFlutterWaitAction(this, null, -1 == this.timeout ? MahjongConstant.MJ_ROOM_FLUTTER_DEFAULT_WAIT_TIME : MahjongConstant.MJ_ROOM_FLUTTER_WAIT_TIME);
                    }
                    flutterWaitAction.addPlayer(temp.getUid());
                }
                if (flutterWaitAction != null) {
                    this.addAction(flutterWaitAction);
                    PCLIMahjongNtfSelectPiaoInfo piaoInfo = new PCLIMahjongNtfSelectPiaoInfo();
                    piaoInfo.op = 1;
                    this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO, piaoInfo, false);
                    return;
                }
            } else if (this.xuanPiaoType == 3) {
                // 3玩家定漂
                PokerFlutterWaitAction flutterWaitAction = null;
                List<Integer> playerIndexList = null;
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer temp = (IRoomPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    if (getTemporaryPropertyValue(temp.getUid(), RoomRule.RR_NONE) == 1) {
                        continue;
                    }
                    if (null == flutterWaitAction) {
                        flutterWaitAction = new PokerFlutterWaitAction(this, null, -1 == this.timeout ? MahjongConstant.MJ_ROOM_FLUTTER_DEFAULT_WAIT_TIME : MahjongConstant.MJ_ROOM_FLUTTER_WAIT_TIME);
                    }
                    flutterWaitAction.addPlayer(temp.getUid());
                    if (null == playerIndexList) {
                        playerIndexList = new ArrayList<Integer>();
                    }
                    playerIndexList.add(i);
                }
                if (flutterWaitAction != null && playerIndexList != null) {
                    this.addAction(flutterWaitAction);
                    PCLIMahjongNtfSelectPiaoInfo piaoInfo = new PCLIMahjongNtfSelectPiaoInfo();
                    piaoInfo.op = 1;
                    for (Integer playerIndex : playerIndexList) {
                        IRoomPlayer temp = (IRoomPlayer) this.allPlayer[playerIndex];
                        if (null == temp || temp.isGuest()) {
                            continue;
                        }
                        temp.send(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO, piaoInfo);
                    }
                    return;
                }
            } else if (this.xuanPiaoType == 4) {
                // 4固定定漂
                List<Long> playerIds = null;
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer temp = (IRoomPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    ((IPokerXuanPiaoPlayer)temp).setPiaoScore(xuanPiaoValue);
                    if (null == playerIds) {
                        playerIds = new ArrayList<Long>();
                    }
                    playerIds.add(temp.getUid());
                    setTemporaryPropertyValue(temp.getUid(), RoomRule.RR_NONE, 1);
                }
                if (playerIds != null) {
                    PCLIMahjongNtfSelectPiaoFixedValueInfo valueInfo = new PCLIMahjongNtfSelectPiaoFixedValueInfo();
                    valueInfo.playerUids = playerIds;
                    valueInfo.value = xuanPiaoValue;
                    this.broadcast2Client(CommandId.CLI_NTF_FIXED_PIAO_VALUE, valueInfo);
                }
            }
        }
        this.endXuanPiao();
    
    }

    @Override
    public void endXuanPiao() {
        
    }

    public void setTemporaryProperty(long playerUid) {
        if (xuanPiaoType == 3) {
            setTemporaryPropertyValue(playerUid, RoomRule.RR_NONE, 1);
        }
    }

    @Override
    public ErrorCode xuanPiao(IPlayer player, int value) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法选飘", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法选飘", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (value < 0 || value > 10) {
            Logs.ROOM.warn("%s %s 无效数据:%d, 无法选飘", this, player, value);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof PokerFlutterWaitAction) {
            Logs.ROOM.debug("%s %s 选飘中", this, player);
            IRoomPlayer roomPlayer = this.getRoomPlayer(player.getUid());
            if (null == roomPlayer || roomPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法选飘", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((PokerFlutterWaitAction) action).playerSelect(player.getUid(), value);
            if (ErrorCode.OK == err) {
                ((IPokerXuanPiaoPlayer)roomPlayer).setPiaoScore(value);
                // 3玩家定漂(玩家定漂为玩家自行在第一局开始时进行选项，确定之后所有局数加漂多少（分类选项：麻将 0/1/2 跑得快0/1/2/3）)
                if (xuanPiaoType == 3) {
                    setTemporaryPropertyValue(player.getUid(), RoomRule.RR_NONE, 1);
                }
                PCLIMahjongNtfSelectPiaoValueInfo valueInfo = new PCLIMahjongNtfSelectPiaoValueInfo();
                valueInfo.playerUid = player.getUid();
                valueInfo.value = value;
                this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO_VALUE, valueInfo);
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s %s 不是选飘动作, 无法选飘", this, player);
        return ErrorCode.REQUEST_INVALID;
    }

}