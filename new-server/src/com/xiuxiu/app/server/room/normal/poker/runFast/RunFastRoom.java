package com.xiuxiu.app.server.room.normal.poker.runFast;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfDeskInfoByRunFast;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPlayerPrimulaResult;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByPoker;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.RoomPlayer;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.PokerFlutterWaitAction;
import com.xiuxiu.app.server.room.normal.poker.action.PokerTakeAction;
import com.xiuxiu.app.server.room.normal.poker.action.RunFastBeginPrimulaAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;
import com.xiuxiu.app.server.room.player.poker.RunFastPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.RecordPokerPlayerBriefInfo;
import com.xiuxiu.app.server.room.record.poker.ResultRecordAction;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 扑克跑得快
 */
@GameInfo(gameType = GameType.GAME_TYPE_RUN_FAST)
public class RunFastRoom extends PokerRoom {
    private IPokerPlayer putSinglePlayer;              // 放单玩家
    private int frame;
    private boolean isFig;
    private boolean isEightPair;
    private boolean primula;
    private boolean isPrimula;
    private int threeSpadesIndex = 0;
    private int loop;
    private int forcingMove;

    public RunFastRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public RunFastRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.timeout = this.info.getRule().getOrDefault(RoomRule.RR_RF_OUT_TIME, 30) * 1000;
        this.bombTarget = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.BOMB_UNDETACHABLE.getValue());
        this.isAAABomb = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.AAA_BOMB.getValue());
        boolean threeTake = 1 == this.info.getRule().getOrDefault(RoomRule.RR_RF_THREE_TAKE, 1);
        boolean notCardNum = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.NOT_CARD_NUM.getValue());
        this.detectionIP = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.RR_DETECTION_IP.getValue());
        this.frame = this.info.getRule().getOrDefault(RoomRule.RR_RF_FRAME, 16);
        this.isFig = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.FIG.getValue());
        this.isEightPair = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.EIGHT_PAIR.getValue());
        this.primula = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.PRIMULA.getValue());
        this.loop = Math.max(3, this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_NUM, 3));
        this.forcingMove = this.info.getRule().getOrDefault(RoomRule.RR_RF_FORCING_MOVE, 1);
        if (threeTake) {
            this.threeTakeType = PokerUtil.THREE_TAKE_TYPE_2;
        } else {
            this.threeTakeType = PokerUtil.THREE_TAKE_TYPE_0 | PokerUtil.THREE_TAKE_TYPE_1 | PokerUtil.THREE_TAKE_TYPE_2;
        }
        this.fourTakeType = PokerUtil.FOUR_TAKE_TYPE_3;
    }

    @Override
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB_POKER) {
            this.allCard.addAll(CardLibraryManager.I.getPokerCard());
            return;
        }
        for (byte i = 0; i < 52; ++i) {
            this.allCard.add(i);
        }
        this.removeCard();
        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doDeal() {
        boolean isThreeSpades = false;
        int index = 0;
        Iterator<Byte> it = this.allCard.iterator();
        while (it.hasNext()) {
            byte card = it.next();
            int temp = index % this.loop;

            if (temp < this.playerNum) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[temp];
                if (null != player && !player.isGuest()) {
                    player.addHandCard(card);
                    if (card == PokerUtil.THREE_SPADES) {
                        isThreeSpades = true;
                        this.threeSpadesIndex = index;
                    }
                }
            }
            ++index;
        }

        if (!isThreeSpades && this.forcingMove != 1) {   // 黑桃三发空的情况下 随机给一个玩家
            List<Integer> validPlayerIndexes = new ArrayList<>();
            for (int i = 0; i < this.allPlayer.length; ++i) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
                if (null != player && !player.isGuest()) {
                    validPlayerIndexes.add(i);
                }
            }
            int validPlayerIndex = validPlayerIndexes.get(RandomUtil.random(validPlayerIndexes.size()));
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[validPlayerIndex];
            int randomFrameIndex = RandomUtil.random(this.frame);
            player.setHandCard(player.getHandCard().get(randomFrameIndex), PokerUtil.THREE_SPADES);
            this.threeSpadesIndex = randomFrameIndex * this.loop + validPlayerIndex;

            Byte tempCard = this.allCard.get(threeSpadesIndex);
            this.allCard.set(this.allCard.indexOf(PokerUtil.THREE_SPADES),tempCard);
            this.allCard.set(threeSpadesIndex,PokerUtil.THREE_SPADES);
        }

        if (1 == this.forcingMove) {
            if (this.bankerIndex != -1) {   // 庄家站起或退出 重新定庄
                if (null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest()) {
                    this.bankerIndex = -1;
                }
            }
            // 赢家先出
            if (-1 == this.bankerIndex) {
                int rand1;
                do {
                    rand1 = RandomUtil.random(0, this.playerNum - 1);
                } while (null == this.allPlayer[rand1] || this.allPlayer[rand1].isGuest());
                List<Byte> card = ((IPokerPlayer) this.allPlayer[rand1]).getHandCard();
                int rand2 = RandomUtil.random(0, card.size() - 1);
                this.firstTakeCard = card.get(rand2);
                this.firstTakeIndex = this.allCard.indexOf(this.firstTakeCard);
                this.bankerIndex = rand1;
            }
        } else {
            this.firstTakeCard = PokerUtil.THREE_SPADES;
            if (!this.primula) {
                // 黑桃3 先出
                this.firstTakeIndex = this.threeSpadesIndex;
                this.bankerIndex = this.firstTakeIndex % this.loop;
            }
        }

        this.curTakeIndex = this.bankerIndex;

        for (IRoomPlayer iRoomPlayer : this.allPlayer) {
            IPokerPlayer player = (IPokerPlayer) iRoomPlayer;
            if (null == player || player.isGuest()) {
                continue;
            }
            PokerUtil.sort(player.getHandCard());
            player.initHandCard();
            this.getRecord().addPlayer(new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getRoomPlayerHelper().getCurBureau(), player.getHandCard()));
        }

        this.firstTake = true;
    }

    /**
     * 好牌发牌处理
     */
    @Override
    protected void doDealGoodCards(Map<Integer,LinkedList<Byte>> playerGoodCards) {
//    	// 替换掉的牌
//    	LinkedList<Byte> allGoodCards = new LinkedList<Byte>();
//    	LinkedList<Byte> oldCards = new LinkedList<Byte>();
//    	oldCards.addAll(this.allCard);
//    	int index = 0;
//		Iterator<Byte> it = this.allCard.iterator();
//		while (it.hasNext()) {
//			Byte oldCard = it.next();
//			int tempIndex = index % this.loop;
//			if (tempIndex < this.playerNum && playerGoodCards.containsKey(tempIndex)) {
//				LinkedList<Byte> tempCard = playerGoodCards.get(tempIndex);
//				if (tempCard.isEmpty()) {
//					continue;
//				}
//				Byte card = tempCard.removeFirst();
//				allGoodCards.add(card);
//				allCard.set(index, card);
//			}
//			++index;
//		}
//		// 取牌差集
//		List<Byte> tempList = oldCards.stream().filter(t-> !allGoodCards.contains(t)).collect(Collectors.toList());
//		LinkedList<Byte> unUsedCards = new LinkedList<Byte>();
//		unUsedCards.addAll(tempList);
//
//		// 重新分配牌
//		index = 0;
//		it = this.allCard.iterator();
//		while (it.hasNext()) {
//			Byte oldCard = it.next();
//			int tempIndex = index % this.loop;
//			if (tempIndex < this.playerNum && !playerGoodCards.containsKey(tempIndex)) {
//				if (unUsedCards.isEmpty()) {
//					continue;
//				}
//				Byte tempCard = unUsedCards.removeFirst();
//				if (tempCard != null) {
//					this.allCard.set(index, tempCard);
//				}
//			}
//			++index;
//		}
	}

	private void doStartTake() {
        PokerPlayer player = (PokerPlayer) this.allPlayer[this.curTakeIndex];
        PokerTakeAction action = new PokerTakeAction(this, player, -1 == this.timeout ? -1 : (1 == this.curBureau ? 5000 : 3000) + this.timeout);
        action.setOp(EActionOp.TAKE);
        List<Byte> takeCard = new ArrayList<>();
        if (1 != this.forcingMove) { // 黑桃三必出
            byte card = player.getHandCard().get(0);
            if (player.isBomb(card)) {
                for (int i = 0; i < 4; i++) {
                    takeCard.add(player.getHandCard().get(i));
                }
                action.setCards(takeCard);
                action.setCardType(EPokerCardType.BOMB);
                action.setTakeMaxCard(PokerUtil.getCardValue(card));
                action.setTakeCnt((byte) 4);
            } else {
                takeCard.add(card);
                action.setCards(takeCard);
                action.setCardType(EPokerCardType.SINGLE);
                action.setTakeMaxCard(PokerUtil.getCardValue(card));
                action.setTakeCnt((byte) 1);
            }
        } else {
            this.getCanTakeCard(action, player);
        }
        this.addAction(action);
    }

    @Override
    public void endXuanPiao() {
        this.specialCardType();
    }

    @Override
    protected void doStart1() {
        this.beginXuanPiao();
    }

    @Override
    protected void doSendGameStart() {
        for (IRoomPlayer iRoomPlayer : this.allPlayer) {
            IPokerPlayer player = (IPokerPlayer) iRoomPlayer;
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByPoker roomBeginInfo = new PCLIRoomNtfBeginInfoByPoker();
            roomBeginInfo.bankerIndex = this.bankerIndex;
            roomBeginInfo.myIndex = player.getIndex();
            roomBeginInfo.myCards = player.getHandCard();
            roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
            roomBeginInfo.bureau = getBureau();
            roomBeginInfo.firstTakeIndex = this.firstTakeIndex;
            roomBeginInfo.firstTakeCard = this.firstTakeCard;
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
        }

        PCLIRoomNtfBeginInfoByPoker roomBeginInfo = new PCLIRoomNtfBeginInfoByPoker();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.firstTakeIndex = this.firstTakeIndex;
        roomBeginInfo.firstTakeCard = this.firstTakeCard;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
        this.sendMyCard();
    }

    private void specialCardType() {
        List<IPokerPlayer> winPlayer = new ArrayList<>();   // 有无花果或者8对的玩家
        boolean specialType = false;                        // 是否有无花果或者8对;
        for (IRoomPlayer roomPlayer : this.allPlayer) {
            IPokerPlayer pokerPlayer = (IPokerPlayer) roomPlayer;
            if (null == pokerPlayer || pokerPlayer.isGuest()) {
                continue;
            }
            boolean isFig = this.frame != 15 && this.isFig && ((RunFastPlayer) pokerPlayer).isFig();    // 无花果
            boolean isEightPair = this.isEightPair && ((RunFastPlayer) pokerPlayer).isEightPair();      // 8对
            if (isFig || isEightPair) {
                winPlayer.add(pokerPlayer);
                specialType = true;
                if (isEightPair) {
                    ((RunFastPlayer) pokerPlayer).setCardType(ERunFastCardType.EightPair);
                }
                if (isFig) {
                    ((RunFastPlayer) pokerPlayer).setCardType(ERunFastCardType.FIG);
                }
            }
        }
        if (specialType) {
            for (IPokerPlayer wPlayer : winPlayer) {
                for (IRoomPlayer roomPlayer : this.allPlayer) {
                    IPokerPlayer player = (IPokerPlayer) roomPlayer;
                    if (null == player || player.isGuest()) {
                        continue;
                    }
                    if (wPlayer != player) {
                        this.addScore(wPlayer, player);
                    }
                }
            }
            this.gameOver(this.checkAgain());
            this.stop();
        } else {
            if (this.primula) {
                this.biginPrimula();
            } else {
                this.doStartTake();
            }
        }
    }

    @Override
    protected ErrorCode checkTake(IAction action, IPokerPlayer player, List<Byte> cards, List<Byte> laiZiCards, int cardType) {
        if (player.getUid() != ((PokerTakeAction) action).getPlayer().getUid()) {
            Logs.ROOM.warn("%s 当前轮跳过人是:%s 而不是你:%s 无效摸牌", this, ((PokerTakeAction) action).getPlayer(), player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (!player.verifyCard(cards)) {
            Logs.ROOM.warn("%s %s 无效打牌, 牌子无效 card:%s cardType:%s", this, player, cards, cardType);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        //下家报单，此时出单张只能出最大的
        PokerPlayer nextPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
        if (nextPlayer.getHandCard().size() == 1 && cards.size() == 1) {
            if (!player.isCurMaxCard(cards.get(0))) {
                Logs.ROOM.warn("%s %s 无效打牌, 不是当前手牌中最大的 card:%s cardType:%s", this, player, cards, cardType);
                return ErrorCode.REQUEST_TAKE_FAIL;
            }
        }

        // check card
        byte[] cardInfo = PokerUtil.getCardType(cards, bombTarget, this.threeTakeType, this.fourTakeType, player.getHandCard().size() == cards.size(), 2, false, this.isAAABomb);
        EPokerCardType type = EPokerCardType.parse(cardInfo[0]);
        int play = this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        if (0 != (play & ERunFastPlayRule.BOMB_UNDETACHABLE.getValue())) {
            if (EPokerCardType.BOMB != type && EPokerCardType.FOUR_TAKE_THREE != type && EPokerCardType.FOUR_TAKE_TWO != type && EPokerCardType.FOUR_TAKE_TWO_DOUBLE != type) {
                // 炸弹不能拆
                for (Byte c : cards) {
                    if (player.isBomb(c)) {
                        Logs.ROOM.warn("%s %s 无效打牌, 炸弹不能拆 card:%s cardType:%s", this, player, cards, cardType);
                        return ErrorCode.REQUEST_INVALID_DATA;
                    }
                }
            }
        }
        if (EPokerCardType.FOUR_TAKE_THREE == type) {
            // 四代三
            if (0 == (play & ERunFastPlayRule.FOUR_THREE.getValue())) {
                Logs.ROOM.warn("%s %s 无效打牌, 不能四代三 card:%s cardType:%s", this, player, cards, cardType);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        }
        if (EPokerCardType.NONE == type) {
            Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        if (this.firstTake && !this.isPrimula) {
            if (2 == (this.forcingMove & 0x0000ffff) && (1 == (this.forcingMove >> 16))) {
                // 黑桃3必出
                if (-1 == cards.indexOf(PokerUtil.THREE_SPADES)) {
                    Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 黑桃3必出 card:%s cardType:%s", this, player, cards, type);
                    return ErrorCode.REQUEST_TAKE_FAIL_MUSTTHREE;
                }
            }
        }

        if (EPokerCardType.NONE != this.curTakeCardType) {
            if (EPokerCardType.BOMB != type && EPokerCardType.KING_FRIED != type && type != this.curTakeCardType) {
                Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            if (EPokerCardType.BOMB == this.curTakeCardType) {
                if (EPokerCardType.KING_FRIED != type) {
                    if (cardInfo[1] <= this.curTakeMaxCard) {
                        Logs.ROOM.warn("%s %s 无效打牌, 牌子较小 card:%s cardType:%s", this, player, cards, type);
                        return ErrorCode.REQUEST_INVALID_DATA;
                    }
                }
            } else {
                if (EPokerCardType.BOMB != type && EPokerCardType.KING_FRIED != type) {
                    if (cardInfo[1] <= this.curTakeMaxCard || cards.size() != this.curTakeCardSize) {
                        Logs.ROOM.warn("%s %s 无效打牌, 牌子较小 card:%s cardType:%s", this, player, cards, type);
                        return ErrorCode.REQUEST_INVALID_DATA;
                    }
                }
            }
        }

        ((PokerTakeAction) action).setOp(EActionOp.TAKE);
        ((PokerTakeAction) action).setCards(cards);
        ((PokerTakeAction) action).setCardType(type);
        ((PokerTakeAction) action).setTakeMaxCard(cardInfo[1]);
        ((PokerTakeAction) action).setTakeCnt(cardInfo[2]);
        this.tick();
        return ErrorCode.OK;
    }

    @Override
    public void onTake(IPokerPlayer player, List<Byte> cards, List<Byte> laiZiCards, EPokerCardType cardType, byte takeMaxCard, byte takeCnt) {
        if (1 == player.getHandCard().size()) {
            // 判断 报单放走包赔
            if (null != this.lastTakeCardPlayer && !this.lastTakeCardPlayer.isCurMaxCard(this.lastTakeCard.get(0))) {
                this.putSinglePlayer = this.lastTakeCardPlayer;
                if (this.curPlayerCnt == 3) {
                    PokerPlayer lastPlayer = (PokerPlayer) this.getNextRoomPlayer(this.getNextRoomPlayer(player.getIndex()).getIndex());
                    if (lastPlayer.getIndex() != this.lastTakeCardPlayer.getIndex()) {
                        byte maxCard = PokerUtil.getCardValue(lastPlayer.getHandCard().get(lastPlayer.getHandCard().size() - 1));
                        byte cardValue = PokerUtil.getCardValue(maxCard);
                        if (cardValue > PokerUtil.getCardValue(cards.get(0))) {
                            this.putSinglePlayer = lastPlayer;
                        }
                    }
                }
            }
            // 找到上家，如果上家要得起但是不要，包赔
            if (this.lastTakeCardPlayer == null && this.curPlayerCnt == 3 && this.lastTakeCardByRunFast.size() == 1) {
                PokerPlayer lastPlayer = (PokerPlayer) this.getNextRoomPlayer(this.getNextRoomPlayer(player.getIndex()).getIndex());
                byte maxCard = PokerUtil.getCardValue(lastPlayer.getHandCard().get(lastPlayer.getHandCard().size() - 1));
                byte cardValue = PokerUtil.getCardValue(maxCard);
                if (cardValue > PokerUtil.getCardValue(this.lastTakeCardByRunFast.get(0))) {
                    this.putSinglePlayer = lastPlayer;
                }
            }
        }
        super.onTake(player, cards, laiZiCards, cardType, takeMaxCard, takeCnt);
        if (player.getIndex() != this.bankerIndex && this.isPrimula) {
            if (cardType == EPokerCardType.BOMB) {
                for (int i = 0; i < this.playerNum; ++i) {
                    IPokerPlayer tempPlayer = (IPokerPlayer) this.allPlayer[i];
                    if (null == tempPlayer || tempPlayer.isGuest()) {
                        continue;
                    }
                    if (player.getUid() != tempPlayer.getUid()) {
                        int score = this.getBombScore(player, tempPlayer);
                        player.addScore(Score.POKER_BOMB_SCORE, score, false);
                        tempPlayer.addScore(Score.POKER_BOMB_SCORE, -score, false);
                    }
                }
            }
            this.isPrimulaTake(player);
        }
    }

    @Override
    protected void gameOver(boolean next) {
        int count = 0;
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            ++count;
        }
        if (count > 0) {
            if (null != this.winPoker) {
                boolean isFig = this.isFig && ((RunFastPlayer) this.winPoker).isFig();
                boolean isEightPair = this.isEightPair && ((RunFastPlayer) this.winPoker).isEightPair();
                boolean isRedPeachTen = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.HEART_TEN_FIRED_BIRD.getValue());
                boolean winHasRedPeachTen = isRedPeachTen && ((RunFastPlayer) this.winPoker).hasRedPeachTen();
                for (int i = 0; i < this.playerNum; ++i) {
                    IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest()) {
                        continue;
                    }
                    if (this.isPrimula) {
                        ((RunFastPlayer) player).setCardType(ERunFastCardType.PRIMULA);
                    }
                    if (this.winPoker.getUid() != player.getUid()) {
                        boolean hasRedPeachTen = isRedPeachTen && ((RunFastPlayer) player).hasRedPeachTen();
                        int score = 0;
                        if (1 != player.getHandCard().size()) {
                            score = this.getScore(player.getHandCard().size() * (0 == player.getTakeCnt() ? 2 : 1) * (hasRedPeachTen || winHasRedPeachTen ? 2 : 1) * (isFig ? 2 : 1) * (isEightPair ? 2 : 1));
                        }
                        // 定飘分数计算
                        int tempPiaoScore = this.getScore(this.winPoker.getPiaoScore() + player.getPiaoScore());
                        if (null != this.putSinglePlayer && player.getUid() != this.putSinglePlayer.getUid()) {
                            this.putSinglePlayer.addScore(Score.SCORE, -(score + tempPiaoScore), false);
                        } else {
                            player.addScore(Score.SCORE, -(score + tempPiaoScore), false);
                        }
                        this.winPoker.addScore(Score.SCORE, score + tempPiaoScore, false);
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
                int bombScore = player.getScore(Score.POKER_BOMB_SCORE, false) * 100;
                player.addScore(Score.SCORE, bombScore, false);
            }

            this.getRoomHandle().calculateGold();
            
            for (int i = 0; i < this.playerNum; ++i) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);
                ResultRecordAction.GameOverInfo actionGameOverInfo = new ResultRecordAction.GameOverInfo();
                actionGameOverInfo.getCard().addAll(player.getHandCard());
                actionGameOverInfo.setScore(this.getClientScore(player.getScore(Score.SCORE, false)));
                actionGameOverInfo.setTotalScore(this.getClientScore(player.getScore()));
                actionGameOverInfo.setCloseDoor(0 == player.getTakeCnt());
                actionGameOverInfo.setDissolve(this.checkIsDestroy());
                actionGameOverInfo.setDissolveUid(this.isDestroyUid);
                resultRecordAction.getAllGameOverInfo().put(player.getUid(), actionGameOverInfo);
            }

            this.record();
            this.getRecord().save();
        }
    }

    @Override
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * value;
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        IPokerPlayer tempPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == tempPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        boolean isRedPeachTen = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.HEART_TEN_FIRED_BIRD.getValue());

        PCLIPokerNtfDeskInfoByRunFast deskInfo = new PCLIPokerNtfDeskInfoByRunFast();
        deskInfo.roomInfo = this.getRoomInfo();
        if (null != tempPlayer) {
            deskInfo.card.addAll(tempPlayer.getHandCard());
        }
        deskInfo.lastTakeCard = this.lastTakeCard;
        deskInfo.lastTakePlayerUid = null == this.lastTakeCardPlayer ? -1 : this.lastTakeCardPlayer.getUid();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();
        deskInfo.curPlayerId = this.curTakeIndex == -1 ? this.curTakeIndex : null == this.allPlayer[this.curTakeIndex] ? -1 : this.allPlayer[this.curTakeIndex].getUid();
        IAction tempAction = this.action.isEmpty() ? null : this.action.peek();
        if (xuanPiaoType != 1 && null != tempAction) {
            deskInfo.flutterWait = (tempAction instanceof PokerFlutterWaitAction);
        }
        deskInfo.isPrimula = this.isPrimula;
        if (this.bankerIndex >= 0) {
            deskInfo.bankerPlayerUid = this.getRoomPlayer(this.bankerIndex).getUid();
        }
        deskInfo.bankerIndex = this.bankerIndex;
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer temp = (IPokerPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            deskInfo.allScore.put(temp.getUid(), this.getClientScore((int) this.getPlayerGold(temp)));
            deskInfo.allOnlineState.put(temp.getUid(), !temp.isOffline());
            boolean hasRedPeachTen = isRedPeachTen && ((RunFastPlayer) temp).hasRedPeachTen();
            if (hasRedPeachTen) {
                deskInfo.redPeachTenUid = temp.getUid();
            }
            if (xuanPiaoType != 1) {
                if (deskInfo.flutterWait) {
                    if (null != tempAction) {
                        deskInfo.piaoScore.put(temp.getUid(), ((PokerFlutterWaitAction) tempAction).getFlutter(temp.getUid()));
                    }
                } else {
                    deskInfo.piaoScore.put(temp.getUid(), temp.getPiaoScore());
                }
            }
            if (temp.getUid() == player.getUid()) {
                continue;
            }
            deskInfo.otherCardCnt.put(temp.getUid(), temp.getHandCard().size());
            deskInfo.autoMode.put(temp.getUid(), ((RoomPlayer) temp).isAutoMode());
        }
        deskInfo.curBureau = tempPlayer.getRoomPlayerHelper().getCurBureau();
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    @Override
    protected int getBombScore(IPokerPlayer takePlayer, IPokerPlayer losePlayer) {
        boolean isRedPeachTen = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.HEART_TEN_FIRED_BIRD.getValue());
        boolean takePlayerHasRedPeachTen = isRedPeachTen && ((RunFastPlayer) takePlayer).hasRedPeachTen();
        boolean losePlayerHasRedPeachTen = isRedPeachTen && ((RunFastPlayer) losePlayer).hasRedPeachTen();
        int mul = 1;
        if (takePlayerHasRedPeachTen || losePlayerHasRedPeachTen) {
            mul = 2;
        }
        return 10 * this.getScore(mul) / 100;//10 * this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * mul;
    }

    @Override
    public void clear() {
        super.clear();
        this.putSinglePlayer = null;
        this.threeSpadesIndex = 0;
    }

    // 开始叫春
    private void biginPrimula() {
        RunFastBeginPrimulaAction action = new RunFastBeginPrimulaAction(this, 10000);
        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer player = this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addPrimula(player.getUid());
        }
        this.addAction(action);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PLAYER_BEGIN_PRIMULA, null);
    }

    public ErrorCode onPrimula(Player player, int primula) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法叫春", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法叫春", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法抢庄", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof RunFastBeginPrimulaAction) {
            ErrorCode err = ((RunFastBeginPrimulaAction) action).selectPrimula(player.getUid(), primula);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是抢庄动作, 无法抢庄", this);
        return ErrorCode.REQUEST_INVALID;
    }

    public void setMaxPrimula(List<Long> maxRobBanker, int max) {
        PCLIPokerNtfPlayerPrimulaResult info = new PCLIPokerNtfPlayerPrimulaResult();
        int banker = this.bankerIndex;
        if (max == 0) {
            // 黑桃3 先出
            if (1 != this.forcingMove) {
                this.firstTakeIndex = this.threeSpadesIndex;
                this.bankerIndex = this.firstTakeIndex % this.loop;
            }
            this.isPrimula = false;
        } else {
            if (max == 1) {
                this.bankerIndex = this.getRoomPlayer(maxRobBanker.get(0)).getIndex();
            } else {
                int count = 0;
                for (Long playerUid : maxRobBanker) {
                    IRoomPlayer roomPlayer = this.getRoomPlayer(playerUid);
                    if (null == roomPlayer) {
                        continue;
                    }
                    if (roomPlayer.getIndex() == banker) {
                        this.bankerIndex = roomPlayer.getIndex();
                        break;
                    }
                    if (roomPlayer.getIndex() == this.getNextRoomPlayer(banker).getIndex() && count <= 0) {
                        this.bankerIndex = roomPlayer.getIndex();
                        count++;
                    }
                }
            }
            this.isPrimula = true;
        }
        this.curTakeIndex = this.bankerIndex;
        info.bankerUid = this.getRoomPlayer(this.bankerIndex).getUid();
        info.bankerIndex = this.bankerIndex;
        info.isPrimula = this.isPrimula;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PLAYER_PRIMULA_RESULT, info);
        this.doStartTake();
    }

    private void isPrimulaTake(IPokerPlayer p) {
        List<IPokerPlayer> winPlayer = new ArrayList<>();
        for (IRoomPlayer roomPlayer : this.allPlayer) {
            IPokerPlayer pokerPlayer = (IPokerPlayer) roomPlayer;
            if (null == pokerPlayer || pokerPlayer.isGuest()) {
                continue;
            }
            if (pokerPlayer.getIndex() != this.bankerIndex) {
                winPlayer.add(pokerPlayer);
                ((RunFastPlayer) pokerPlayer).setCardType(ERunFastCardType.PRIMULA);
            }
        }
        IPokerPlayer bankerPlayer = (PokerPlayer) this.getRoomPlayer(this.bankerIndex);
        ((RunFastPlayer) bankerPlayer).setCardType(ERunFastCardType.PRIMULA);
        int count = 0;
        for (IPokerPlayer player : winPlayer) {
            if (null == player || player.isGuest()) {
                continue;
            }
            this.addScore(player, bankerPlayer);
            count++;
        }
        this.gameOver(this.checkAgain());
        if (count == 1) {
            this.bankerIndex = p.getIndex();
        } else {
            this.bankerIndex = this.getNextRoomPlayer(this.bankerIndex).getIndex();
        }
        this.stop();
    }

    private int getPrimulaScore(IPokerPlayer winPlayer, IPokerPlayer losePlayer) {
        int score = this.frame * 2;
        boolean isRedPeachTen = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ERunFastPlayRule.HEART_TEN_FIRED_BIRD.getValue());
        boolean winPlayerHasRedPeachTen = isRedPeachTen && ((RunFastPlayer) winPlayer).hasRedPeachTen();
        boolean losePlayerHasRedPeachTen = isRedPeachTen && ((RunFastPlayer) losePlayer).hasRedPeachTen();
        if (winPlayerHasRedPeachTen || losePlayerHasRedPeachTen) {
            score = score * 2;
        }
        return score;
    }

    private void addScore(IPokerPlayer winPlayer, IPokerPlayer lostPlayer) {
        int score = this.getScore(this.getPrimulaScore(winPlayer, lostPlayer));
        winPlayer.addScore(Score.SCORE, score, false);
        lostPlayer.addScore(Score.SCORE, -score, false);
        // 定飘分数计算
        int tempPiaoScore = getScore(winPlayer.getPiaoScore() + lostPlayer.getPiaoScore());
        winPlayer.addScore(Score.SCORE, tempPiaoScore, false);
        lostPlayer.addScore(Score.SCORE, -tempPiaoScore, false);
        
        lostPlayer.addScore(Score.ACC_LOST_CNT, 1, true);
        lostPlayer.addScore(Score.ACC_LOST_CNT_CONTINUE, 1, true);
        winPlayer.addScore(Score.ACC_WIN_CNT, 1, true);
        winPlayer.setScore(Score.ACC_LOST_CNT_CONTINUE, 0, true);
    }
    
    private void removeCard(){
        if (15 == this.frame) {
            // 15 张
            this.allCard.remove(PokerUtil.A_PLUM);
            this.allCard.remove(PokerUtil.A_RED_PEACH);
            this.allCard.remove(PokerUtil.A_BOX);
            this.allCard.remove(PokerUtil.TWO_PLUM);
            this.allCard.remove(PokerUtil.TWO_RED_PEACH);
            this.allCard.remove(PokerUtil.TWO_BOX);
            this.allCard.remove(PokerUtil.K_BOX);
        } else {
            // 16 张
            this.allCard.remove(PokerUtil.A_BOX);
            this.allCard.remove(PokerUtil.TWO_PLUM);
            this.allCard.remove(PokerUtil.TWO_RED_PEACH);
            this.allCard.remove(PokerUtil.TWO_BOX);
        }
    }
}
