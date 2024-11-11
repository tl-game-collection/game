package com.xiuxiu.app.server.room.normal.poker.arch;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.*;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByPokerWithArch;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.RoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.ArchBidAction;
import com.xiuxiu.app.server.room.normal.poker.action.PokerTakeAction;
import com.xiuxiu.app.server.room.normal.poker.arch.locale.ArchChiBi;
import com.xiuxiu.app.server.room.normal.poker.arch.locale.ArchDaYe;
import com.xiuxiu.app.server.room.normal.poker.arch.locale.ArchLocale;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.ArchPlayer;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;
import com.xiuxiu.app.server.room.record.poker.*;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 扑克 打拱
 */
@GameInfo(gameType = GameType.GAME_TYPE_ARCH)
public class ArchRoom extends PokerRoom {
    // 赖子单出
    private int laiziAsValue;

    private int rules;

    private ArchBidRecordAction bidRecordAction;
    private Round currentRound = new Round();
    private List<ArchPlayer> winners = new ArrayList<>();
    private int contract = ArchRule.CONTRACT_UNDEFINED;
    private List<Byte> lastTakeLaizi = new ArrayList<>(); // 出的癞子牌
    private List<Byte> reservedCards = new ArrayList<>();
    private PCLIPokerNtfArchGameOverInfo bureauResult = null; // 当局结果

    private ArchLocale locale; // 地方化特色

    private static class Round {
        List<Byte> cards = new ArrayList<>();

        void reset() {
            this.cards.clear();
        }
    }

    public ArchRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public ArchRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
        this.firstTakeCard = -1;
        this.firstTakeIndex = -1;
    }

    @Override
    public void init() {
        super.init();

        this.locale = info.getGameSubType() == 2 ? new ArchChiBi(this) : new ArchDaYe(this);

        this.rules = this.getRule().getOrDefault(RoomRule.RR_PLAY, 0);
        this.detectionIP=0 !=this.getRule().getOrDefault(RoomRule.RR_PLAY,0);
        // 20秒场
        int RULE_TIMEOUT = 0x08;
        if ((this.rules & RULE_TIMEOUT) != 0) {
            this.timeout = 20 * 1000;
        }

        int laizi = this.getRule().getOrDefault(RoomRule.RR_ARCH_LAIZI_AS, 1);
        this.laiziAsValue = laizi == 1 ? PokerUtil._2 : PokerUtil._3;
    }

    /**
     * 玩家进行叫牌
     *
     * @param player   进行叫牌的玩家
     * @param contract 定约
     */
    public ErrorCode bid(Player player, int contract) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 当前房间状态无法叫牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法叫牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }

        IAction action = this.action.peek();
        if (action instanceof ArchBidAction) {
            ErrorCode err = ((ArchBidAction) action).bid(player.getUid(), contract);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 获取盟友的手牌
     */
    public void getPartnerHandCards(Player player) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s 不具备OB条件，房间状态不允许", player);
            player.send(CommandId.CLI_NTF_POKER_ARCH_OB_FAIL, ErrorCode.ROOM_NOT_START);
            return;
        }
        // 先跑可看队友牌
        int RULE_ENABLE_OB = 0x04;
        if ((this.rules & RULE_ENABLE_OB) == 0 || this.contract == ArchRule.CONTRACT_1V3) {
            Logs.ROOM.warn("%s 不具备OB条件，模式不允许", player);
            player.send(CommandId.CLI_NTF_POKER_ARCH_OB_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }
        ArchPlayer archPlayer = (ArchPlayer) this.getRoomPlayer(player.getUid());
        if (archPlayer == null || archPlayer.isGuest() || archPlayer.hasHandCard()) {
            Logs.ROOM.warn("%s 不具备OB条件，玩家条件不满足", player);
            player.send(CommandId.CLI_NTF_POKER_ARCH_OB_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        ArchPlayer partner = null;
        if (this.contract == ArchRule.CONTRACT_2V2) {
            ArchPlayer banker = (ArchPlayer) this.allPlayer[this.bankerIndex];
            if (banker.getPartner().getHandCard().contains(this.firstTakeCard)) {
                Logs.ROOM.warn("%s 不具备OB条件，尚未明鸡", player);
                player.send(CommandId.CLI_NTF_POKER_ARCH_OB_FAIL, ErrorCode.REQUEST_INVALID);
                return;
            }
            partner = archPlayer.getPartner();
        } else {
            if (this.allPlayer[this.bankerIndex].getUid() == player.getUid()) {
                Logs.ROOM.warn("%s 不具备OB条件，1V2模式庄家无法OB", player);
                player.send(CommandId.CLI_NTF_POKER_ARCH_OB_FAIL, ErrorCode.REQUEST_INVALID);
                return;
            }
            for (IRoomPlayer p : this.allPlayer) {
                if (p.getIndex() != archPlayer.getIndex() && p.getIndex() != this.bankerIndex) {
                    partner = (ArchPlayer) p;
                    break;
                }
            }
        }

        PCLIPokerNtfArchObInfo obInfo = new PCLIPokerNtfArchObInfo();
        obInfo.playerUid = null == partner ? -1 : partner.getUid();
        obInfo.cards = null == partner ? null : partner.getHandCard();
        player.send(CommandId.CLI_NTF_POKER_ARCH_OB_OK, obInfo);
    }

    /**
     * 叫牌结束
     *
     * @param bankerUid 庄家UID
     * @param contract  定约
     */
    public void onBidFinish(long bankerUid, int contract) {
        Logs.ROOM.debug(">>> onBidFinish bankerUid:%d, contract:%d", bankerUid, contract);
        ArchPlayer banker = (ArchPlayer) this.getRoomPlayer(bankerUid);
        this.bankerIndex = banker.getIndex();
        this.curTakeIndex = this.bankerIndex;
        this.contract = contract;

        // 发底牌给庄家
        if (!this.reservedCards.isEmpty()) {
            for (Byte card : this.reservedCards) {
                banker.addHandCard(card);
            }
            this.getRecord().addAction(new ArchLastCardRecordAction(bankerUid, this.reservedCards));
        }

        for (IRoomPlayer player : this.allPlayer) {
            if (player != null && !player.isGuest()) {
                ArchPlayer archPlayer = (ArchPlayer) player;
                archPlayer.resetDealtCards(archPlayer.getHandCard());
            }
        }

        // 2V2模式，确定盟友关系
        if (this.contract == ArchRule.CONTRACT_2V2) {
            ArchPlayer firstEnemy = null;
            for (IRoomPlayer iRoomPlayer : this.allPlayer) {
                ArchPlayer player = (ArchPlayer) iRoomPlayer;
                if (player == null || player.isGuest() || player.getUid() == bankerUid) {
                    continue;
                }
                if (banker.getPartner() == null && player.getHandCard().indexOf(this.firstTakeCard) != -1) {
                    player.setPartner(banker);
                    banker.setPartner(player);
                    Logs.ROOM.debug("banker:%d, partner:%d", banker.getUid(), player.getUid());
                } else if (firstEnemy == null) {
                    firstEnemy = player;
                } else {
                    player.setPartner(firstEnemy);
                    firstEnemy.setPartner(player);
                    Logs.ROOM.debug("enemy:%d, partner:%d", firstEnemy.getUid(), player.getUid());
                }
            }
        }

        // 通告叫牌结果以及底牌
        PCLIPokerNtfArchBidResultInfo result = new PCLIPokerNtfArchBidResultInfo();
        result.bankerUid = bankerUid;
        result.contract = contract;
        result.reservedCards = this.reservedCards;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_ARCH_BID_RESULT, result);

        PokerTakeAction firstTake = createTimeoutTake(banker, false);
        this.addAction(firstTake);
    }

    /**
     * 按照指定要求对手牌进行牌序
     *
     * @param type 1-默认序，2-五十K优先
     */
    public void sortPlayerCards(IPlayer player, int type) {
        if (ERoomState.START != this.roomState.get()) {
            player.send(CommandId.CLI_NTF_POKER_ARCH_SORT_CARD_FAIL, ErrorCode.ROOM_NOT_START);
            return;
        }

        PCLIPokerNtfArchSortCardInfo ntf = new PCLIPokerNtfArchSortCardInfo();
        ArchPlayer archPlayer = (ArchPlayer) this.getRoomPlayer(player.getUid());
        ntf.cards = ArchRule.sortCards(archPlayer.getHandCard(), type);
        archPlayer.getHandCard().clear();
        archPlayer.getHandCard().addAll(ntf.cards);
        player.send(CommandId.CLI_NTF_POKER_ARCH_SORT_CARD_OK, ntf);
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new ArchPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }

    @Override
    public void clear() {
        Logs.ROOM.debug(">>>> calling clear()...");
        super.clear();

        this.contract = 0;
        this.currentRound.reset();
        this.reservedCards.clear();
        this.firstTakeCard = -1;
        this.firstTakeIndex = -1;
        this.lastTakeLaizi.clear();
        this.winners.clear();
        for (IRoomPlayer player : this.allPlayer) {
            if (player != null && !player.isGuest()) {
                player.setScore(Score.SCORE, 0, false);
                ((ArchPlayer) player).reset();
            }
        }
    }

    @Override
    protected void doShuffle() {
        Logs.ROOM.debug(">>>> calling doShuffle()...");
        if (Switch.USE_CARD_LIB_POKER) {
            this.allCard.addAll(CardLibraryManager.I.getPokerCard());
            return;
        }
        this.locale.shuffle(this.allCard);
    }

    @Override
    protected void doDeal() {
        Logs.ROOM.debug(">>>> calling doDeal()...");
        if (this.bankerIndex != -1) {   // 庄家站起或退出 重新定庄
            if (null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest()) {
                this.bankerIndex = -1;
            }
        }
        // 首叫玩家待定时，随机一位玩家进行首叫
        if (this.bankerIndex < 0) {
            int index = RandomUtil.random(0, this.getCurPlayerCnt() - 1);
            for (IRoomPlayer player : this.allPlayer) {
                if (player != null && !player.isGuest() && index-- == 0) {
                    this.bankerIndex = player.getIndex();
                    break;
                }
            }
        }

        // 4人时，重复洗牌一直到找出一个有效牌型
        int countOfPlayers = this.getCurPlayerCnt();
        if (countOfPlayers == 4) {
            int countOfHandCards = this.allCard.size() / countOfPlayers;
            // 4人时，确保该明牌可以找到有效盟友
            int handCardIndex = RandomUtil.random(0, countOfHandCards - 1);
            while (this.firstTakeIndex < 0) {
                Logs.ROOM.debug("洗牌...");
                for (int i = 0; i < countOfHandCards; i++) {
                    Byte card = this.allCard.get(handCardIndex * countOfPlayers);
                    if ((this.allCard.lastIndexOf(card) - this.allCard.indexOf(card)) % countOfPlayers != 0) {
                        this.firstTakeIndex = handCardIndex;
                        break;
                    }
                    handCardIndex = (handCardIndex + 1) % countOfHandCards;
                }
            }
        } else {
            this.reservedCards.addAll(this.allCard.subList(this.allCard.size() - ArchRule.NUM_RESERVE_CARDS, this.allCard.size()));
            int countOfHandCards = (this.allCard.size() - this.reservedCards.size()) / countOfPlayers;
            this.firstTakeIndex = RandomUtil.random(0, countOfHandCards - 1);
        }

        // 发牌
        int dealingCardIndex = 0;
        int numCardsToDeal = this.allCard.size() - this.reservedCards.size();
        while (dealingCardIndex < numCardsToDeal) {
            for (int i = 0; dealingCardIndex < numCardsToDeal && i < this.playerNum; i++) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[(i + this.bankerIndex) % this.playerNum];
                if (player != null && !player.isGuest()) {
                    player.addHandCard(this.allCard.get(dealingCardIndex++));
                }
            }
        }

        // 明牌
        IPokerPlayer bidder = (IPokerPlayer) this.allPlayer[this.bankerIndex];
        this.firstTakeCard = bidder.getHandCard().get(this.firstTakeIndex);
        Logs.ROOM.debug("发牌完毕，firstTakeCard:%d", this.firstTakeCard);

        for (IRoomPlayer p : this.allPlayer) {
            if (p == null || p.isGuest()) {
                continue;
            }

            IPokerPlayer player = (IPokerPlayer) p;
            Logs.ROOM.debug("player:%d, cards:%s", player.getUid(), player.getHandCard());
            if (player.getIndex() == this.firstTakeIndex) {
                Logs.ROOM.debug("player:%d, firstTakeIndex:%d, firstTakeCard:%d",
                        player.getUid(), this.firstTakeIndex, this.firstTakeCard);
            }
            PokerUtil.sort(player.getHandCard());
            player.initHandCard();

            this.getRecord().addPlayer(new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(),
                    player.getBureau(), player.getHandCard()));
        }
    }

    @Override
    protected void doStart1() {
        Logs.ROOM.debug(">>>> calling doStart1()...");
        this.firstTake = true;

        if (this.bidRecordAction == null) {
            this.bidRecordAction = new ArchBidRecordAction();
            this.getRecord().addAction(this.bidRecordAction);
        }

        this.curTakeIndex = this.bankerIndex;
        PokerPlayer banker = (PokerPlayer) this.allPlayer[this.curTakeIndex];
        ArchBidAction bidAction = new ArchBidAction(this, banker, 20 * 1000, this.bidRecordAction);
        bidAction.begin(banker.getUid());
        this.addAction(bidAction);
    }

    @Override
    protected void doSendGameStart() {
        Logs.ROOM.debug(">>>> calling sendGameStart()...");
        for (IRoomPlayer iRoomPlayer : this.allPlayer) {
            IPokerPlayer player = (IPokerPlayer) iRoomPlayer;
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByPokerWithArch roomBeginInfo = new PCLIRoomNtfBeginInfoByPokerWithArch();
            roomBeginInfo.bankerIndex = this.bankerIndex;
            roomBeginInfo.myIndex = player.getIndex();
            roomBeginInfo.myCards = player.getHandCard();
            roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
            roomBeginInfo.bureau = player.getBureau();
            roomBeginInfo.firstTakeIndex = this.firstTakeIndex;
            roomBeginInfo.firstTakeCard = this.firstTakeCard;
            roomBeginInfo.laiziCards = ArchRule.getLAIZI();
            for (IRoomPlayer roomPlayer : this.allPlayer) {
                if (roomPlayer != null && !roomPlayer.isGuest()) {
                    roomBeginInfo.playerCardCount.put(roomPlayer.getUid(), ((ArchPlayer)roomPlayer).getHandCard().size());
                }
            }
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
        }
        PCLIRoomNtfBeginInfoByPokerWithArch roomBeginInfo = new PCLIRoomNtfBeginInfoByPokerWithArch();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.firstTakeIndex = this.firstTakeIndex;
        roomBeginInfo.firstTakeCard = this.firstTakeCard;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
    }

    @Override
    protected ErrorCode checkTake(IAction action, IPokerPlayer player, List<Byte> cards, List<Byte> lzCards, int cardType) {
        if (player.getUid() != ((PokerTakeAction) action).getPlayer().getUid()) {
            Logs.ROOM.warn("%s 当前轮跳过人是:%s 而不是你:%s 无效摸牌", this, ((PokerTakeAction) action).getPlayer(), player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (!player.getHandCard().containsAll(cards)) {
            Logs.ROOM.warn("%s %s 无效打牌, 牌子无效 card:%s cardType:%s", this, player, cards, cardType);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (lzCards != null && cards.size() == lzCards.size() && lzCards.size() == 1 && PokerUtil.getCardValue(lzCards.get(0)) != this.laiziAsValue) {
            Logs.ROOM.warn("%s %s 无效打牌, 单出赖子牌值不符 card:%s cardType:%s", this, player, cards, cardType);
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        List<Byte> laiziAsCards = lzCards == null ? new ArrayList<>() : lzCards;
        if (!ArchRule.canTake(cards, laiziAsCards, this.locale.features())) {
            Logs.ROOM.warn("%s %s 无效打牌, 不符合规则 card:%s cardType:%s", this, player, cards, cardType);
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        List<Byte> virtualCards = ArchRule.virtualCards(cards, laiziAsCards);
        ArchCardTypeEnum type = ArchRule.detectCardType(virtualCards, this.locale.features());
        if (type == ArchCardTypeEnum.NONE) {
            Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        // 大小比较：
        //  * 五十K作为炸弹分同花和杂色，同色时按照桃心梅方大小排序
        //  * 其他炸弹，牌多的大，相同数量时按照2>A>K>Q>J>10...排序
        if (this.curTakeCardType != EPokerCardType.NONE) {
            byte maxCard = virtualCards.get(virtualCards.size() - 1);
            EPokerCardType prevCardType = this.curTakeCardType;
            byte prevMaxCard = this.curTakeMaxCard;
            int prevCardSize = this.curTakeCardSize;
            if (prevCardType == EPokerCardType.WUSHIK_WSKBOMB) {
                if (type == ArchCardTypeEnum.WUSHIK_WSKBOMB) {
                    Logs.ROOM.warn("%s %s 无效打牌, 都是杂色五十K card:%s cardType:%s", this, player, cards, type);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
            } else if (prevCardType == EPokerCardType.WUSHIK_WSKBIGBOMB) {
                if (type == ArchCardTypeEnum.WUSHIK_WSKBOMB
                        || (type == ArchCardTypeEnum.WUSHIK_WSKBIGBOMB && compareCardColor(maxCard, prevMaxCard) <= 0)) {
                    Logs.ROOM.warn("%s %s 无效打牌, 五十K没上家大 card:%s cardType:%s", this, player, cards, type);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
            } else if (prevCardType == EPokerCardType.BOMB) {
                List<Byte> lastLaiziAsCards = this.lastTakeLaizi;
                List<Byte> lastVirtualCards = ArchRule.virtualCards(this.lastTakeCard, lastLaiziAsCards);
                ArchCardTypeEnum lastCardType = ArchRule.detectCardType(lastVirtualCards, this.locale.features());
                if (lastCardType == ArchCardTypeEnum.COMBO_BOMB && !laiziAsCards.isEmpty()) {
                    Logs.ROOM.warn("%s %s 无效打牌, 连炸不能有赖子", this, player);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
                int compare = ArchRule.compareBomb(lastVirtualCards, this.lastTakeLaizi, lastCardType, virtualCards, lzCards, type);
                if (compare >= 0) {
                    Logs.ROOM.warn("%s %s 无效打牌, 炸弹没上家大 card:%s cardType:%s", this, player, cards, type);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
            } else if (!ArchRule.isBomb(type, this.locale.features())) {
                if (prevCardType.getValue() != type.getValue() || prevCardSize != virtualCards.size()) {
                    Logs.ROOM.warn("%s %s 无效打牌, 牌型不匹配 card:%s cardType:%s", this, player, cards, type);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
                if (ArchRule.compareCardValue(maxCard, prevMaxCard) <= 0) {
                    Logs.ROOM.warn("%s %s 无效打牌, 大小不对 cards:%s, maxCard:%d, prevMaxCard:%d, cardType:%s",
                            this, player, cards, maxCard, prevMaxCard, type);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
            }
        }

        this.lastTakeLaizi.clear();
        this.lastTakeLaizi.addAll(laiziAsCards);

        PokerTakeAction actionTake = (PokerTakeAction) action;
        actionTake.setOp(EActionOp.TAKE);
        actionTake.setCards(cards);
        actionTake.setLaiZiCards(laiziAsCards);
        actionTake.setCardType(type.toPokerCardType());
        actionTake.setTakeMaxCard(virtualCards.get(virtualCards.size() - 1));
        actionTake.setTakeCnt((byte) virtualCards.size());
        this.tick();
        return ErrorCode.OK;
    }

    @Override
    public void onTake(IPokerPlayer player, List<Byte> cards, List<Byte> laiZiCards, EPokerCardType cardType, byte takeMaxCard, byte takeCnt) {
        Logs.ROOM.debug("onTake player:%d, cards:%s, laizi:%s", player.getUid(), cards, laiZiCards != null ? laiZiCards : "[]");

        this.firstTake = false;
        this.passCount = 0;
        player.takeCard(cards);
        Logs.ROOM.debug("player:%d handCard:%s", player.getUid(), player.getHandCard());

        this.curTakeMaxCard = takeMaxCard;
        this.curTakeCnt = takeCnt;
        this.curTakeCardSize = (byte) cards.size();
        this.curTakeCardType = cardType;
        this.lastTakeCard = cards;
        this.lastTakeCardPlayer = player;
        this.lastTakeLaizi.clear();
        if (laiZiCards != null && !laiZiCards.isEmpty()) {
            this.lastTakeLaizi.addAll(laiZiCards);
        }

        ((PokerRecord) this.getRecord()).addTakeRecordAction(player.getUid(), cards);

        // 本轮出牌追加
        this.currentRound.cards.addAll(cards);

        // 下一个出牌的玩家
        ArchPlayer nextPlayer;
        do {
            nextPlayer = (ArchPlayer) this.getNextRoomPlayer(this.curTakeIndex);
            this.curTakeIndex = nextPlayer.getIndex();
        } while (!nextPlayer.hasHandCard());

        boolean isGameOver = false;
        if (!player.hasHandCard()) {
            ArchPlayer lastTaker = (ArchPlayer) player;
            this.winners.add(lastTaker);

            if (this.getCurPlayerCnt() == 3) { // 3人局
                if (this.winners.size() == this.getCurPlayerCnt() - 1) { // 所有名次已决出，牌局结束
                    isGameOver = true;
                    this.roundOver();
                    lastTaker.addCardScore(ArchRule.getCardsScore(nextPlayer.getHandCard()));
                } else if (lastTaker.getIndex() == this.bankerIndex) { // 庄家头游，牌局结束
                    isGameOver = true;
                    this.roundOver();
                    // 手上的分归庄家所有
                    for (IRoomPlayer p : this.allPlayer) {
                        if (p != null && !p.isGuest() && p.getUid() != lastTaker.getUid()) {
                            lastTaker.addCardScore(ArchRule.getCardsScore(((ArchPlayer) p).getHandCard()));
                        }
                    }
                }
            } else { // 4人局
                if (this.contract == ArchRule.CONTRACT_1V3) { // 独庄，任何一人出完，牌局结束，分数*4
                    isGameOver = true;
                    this.roundOver();
                } else if (this.winners.size() > 1
                        && this.winners.get(0).getPartner() == this.winners.get(1)) { // 1/2 vs 3/4
                    isGameOver = true;
                    this.roundOver();
                    for (IRoomPlayer p : this.allPlayer) {
                        if (p != null && !p.isGuest()) {
                            ArchPlayer archPlayer = (ArchPlayer) p;
                            if (!this.winners.contains(archPlayer)) {
                                lastTaker.addCardScore(ArchRule.getCardsScore(archPlayer.getHandCard()));
                            }
                        }
                    }
                } else if (this.winners.size() == this.getCurPlayerCnt() - 1) { // 所有名次已决出，牌局结束
                    isGameOver = true;
                    this.roundOver();
                    this.winners.get(0).addCardScore(nextPlayer.getCardScore());
                    nextPlayer.setCardScore(0);
                    lastTaker.addCardScore(ArchRule.getCardsScore(nextPlayer.getHandCard()));
                }
            }
        }

        PCLIPokerNtfTakeInfo ntfTake = new PCLIPokerNtfTakeInfo(player.getUid(), cards, cardType.getValue(), isGameOver ? -1 : nextPlayer.getUid(), laiZiCards);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_TAKE, ntfTake);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_AUTO_MODE, new PCLIPokerNetAutoMode(player.getUid(), ((RoomPlayer) player).isAutoMode()));

        if (isGameOver) {
            this.winPoker = this.winners.get(0);
            this.gameOver(this.checkAgain());
            this.bankerIndex = this.winPoker.getIndex();
            this.stop();
        } else {
            this.sendMyCard();
            this.addAction(createTimeoutTake(nextPlayer, true));
        }
    }

    @Override
    public void onPass(PokerPlayer player) {
        Logs.ROOM.debug("onPass player:%d", player.getUid());

        ((PokerRecord) this.getRecord()).addPassRecordAction(player.getUid());

        ++this.passCount;
        PokerPlayer nextPlayer = null; // 下一个出牌的人

        boolean isRoundOver; // 一轮结束否
        if (this.lastTakeCardPlayer.hasHandCard()) {
            isRoundOver = this.passCount == this.getCurPlayerCnt() - this.winners.size() - 1;
        } else {
            isRoundOver = this.passCount == this.getCurPlayerCnt() - this.winners.size();
        }

        if (isRoundOver) {
            this.roundOver();

            if (!this.lastTakeCardPlayer.hasHandCard()) {
                if (this.contract == ArchRule.CONTRACT_2V2) {
                    // 如果已明鸡，盟友接风
                    ArchPlayer banker = (ArchPlayer) this.getRoomPlayer(this.bankerIndex);
                    if (!banker.getPartner().getHandCard().contains(this.firstTakeCard)) {
                        ArchPlayer lastTakerPartner = ((ArchPlayer) this.lastTakeCardPlayer).getPartner();
                        Logs.ROOM.debug("已明鸡，盟友接风:%d", lastTakerPartner.getUid());
                        nextPlayer = lastTakerPartner;
                        this.curTakeIndex = nextPlayer.getIndex();
                    }
                } else if (this.contract == ArchRule.CONTRACT_1V2) {
                    // 盟友接风
                    while (nextPlayer == null || !nextPlayer.hasHandCard() || nextPlayer.getIndex() == this.bankerIndex) {
                        nextPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
                        this.curTakeIndex = nextPlayer.getIndex();
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

        while (nextPlayer == null || !nextPlayer.hasHandCard()) {
            nextPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
            this.curTakeIndex = nextPlayer.getIndex();
        }
        this.broadcast2Client(CommandId.CLI_NTF_POKER_PASS, new PCLIPokerNtfPassInfo(player.getUid(), nextPlayer.getUid()));
        this.broadcast2Client(CommandId.CLI_NTF_POKER_AUTO_MODE, new PCLIPokerNetAutoMode(player.getUid(), player.isAutoMode()));
        this.addAction(createTimeoutTake(nextPlayer, !isRoundOver));
    }

    private PCLIPokerNtfArchGameOverInfo.GameOverInfo createGameOverInfo(IPokerPlayer player) {
        PCLIPokerNtfArchGameOverInfo.GameOverInfo info = new PCLIPokerNtfArchGameOverInfo.GameOverInfo();
        info.card.addAll(player.getHandCard());
        info.cardScore = ((ArchPlayer) player).getCardScore();
        return info;
    }

    @Override
    protected void gameOver(boolean next) {
        Logs.ROOM.debug(">>>> calling gameOver()...");

        this.bureauResult = new PCLIPokerNtfArchGameOverInfo();
        PCLIPokerNtfArchGameOverInfo roomResult = this.bureauResult;
        roomResult.next = next;
        roomResult.bureau = this.curBureau;

        if (next || this.roomState.get() != ERoomState.DESTROY) {
            if (this.getCurPlayerCnt() == 3) { // 3人局
                ArchPlayer banker = (ArchPlayer) this.getRoomPlayer(this.bankerIndex);
                boolean bankerWin = false;
                if (this.winners.get(0).getIndex() == this.bankerIndex) { // 庄家头游
                    roomResult.overType = banker.getCardScore() == 200 ? 4 : 2;
                    bankerWin = true;
                } else if (this.winners.get(1).getIndex() == this.bankerIndex) { // 庄家二游
                    int bankerCardScore = banker.getCardScore();
                    bankerWin = bankerCardScore > 100;
                    roomResult.overType = bankerCardScore == 200 || bankerCardScore == 0 ? 2 : 1;
                } else { // 庄家尾游
                    roomResult.overType = banker.getCardScore() > 0 ? 2 : 4;
                }

                for (IRoomPlayer p : this.allPlayer) {
                    if (p != null && !p.isGuest()) {
                        PCLIPokerNtfArchGameOverInfo.GameOverInfo info = this.createGameOverInfo((IPokerPlayer) p);
                        roomResult.allGameOverInfo.put(p.getUid(), info);
                        int score = this.contract == ArchRule.CONTRACT_1V2_X2 ? roomResult.overType * 2 : roomResult.overType;
                        if (p.getUid() == banker.getUid()) {
                            this.setPlayerScore(p, bankerWin ? score * 2 : -score * 2);
                        } else {
                            this.setPlayerScore(p, bankerWin ? -score : score);
                        }
                    }
                }
            } else { // 4人局
                if (this.contract == ArchRule.CONTRACT_1V3) { // 独庄，分数*4
                    roomResult.overType = 4;
                    boolean bankerWin = this.winners.get(0).getIndex() == this.bankerIndex;
                    for (IRoomPlayer p : this.allPlayer) {
                        if (p != null && !p.isGuest()) {
                            PCLIPokerNtfArchGameOverInfo.GameOverInfo info = this.createGameOverInfo((IPokerPlayer) p);
                            roomResult.allGameOverInfo.put(p.getUid(), info);
                            if (p.getIndex() == this.bankerIndex) {
                                this.setPlayerScore(p, bankerWin ? roomResult.overType * 3 : -roomResult.overType * 3);
                            } else {
                                this.setPlayerScore(p, bankerWin ? -roomResult.overType : roomResult.overType);
                            }
                        }
                    }
                } else if (this.winners.get(0).getPartner().getUid() == this.winners.get(1).getUid()) { // 1/2 vs 3/4
                    int score_1_2 = this.winners.get(0).getCardScore() + this.winners.get(1).getCardScore();
                    int score_3_4 = 0;
                    for (IRoomPlayer iRoomPlayer : this.allPlayer) {
                        ArchPlayer archPlayer = (ArchPlayer) iRoomPlayer;
                        if (archPlayer != null && !archPlayer.isGuest()) {
                           if (!this.winners.contains(archPlayer)){
                               score_3_4 += archPlayer.getCardScore();
                           }
                        }
                    }
                    roomResult.overType = score_1_2 >= 0 && score_3_4 == 0 ? 4 : 2;
                    for (IRoomPlayer roomPlayer : this.allPlayer) {
                        ArchPlayer player = (ArchPlayer) roomPlayer;
                        if (player != null && !player.isGuest()) {
                            PCLIPokerNtfArchGameOverInfo.GameOverInfo info = this.createGameOverInfo(player);
                            roomResult.allGameOverInfo.put(player.getUid(), info);
                            this.setPlayerScore(player, this.winners.contains(player) ? roomResult.overType : -roomResult.overType);
                        }
                    }
                } else if (this.winners.get(0).getPartner().getUid() == this.winners.get(2).getUid()) { // 1/3 vs 2/4
                    int score_1_3 = this.winners.get(0).getCardScore() + this.winners.get(2).getCardScore();
                    if (score_1_3 >= 100) { // 1/3胜
                        roomResult.overType = score_1_3 == 200 ? 2 : 1;
                    } else {
                        roomResult.overType = score_1_3 == 0 ? 2 : 1;
                    }
                    for (IRoomPlayer p : this.allPlayer) {
                        if (p != null && !p.isGuest()) {
                            ArchPlayer player = (ArchPlayer) p;
                            PCLIPokerNtfArchGameOverInfo.GameOverInfo info = this.createGameOverInfo(player);
                            roomResult.allGameOverInfo.put(player.getUid(), info);
                            if (player == this.winners.get(0) || player == this.winners.get(2)) {
                                this.setPlayerScore(player, score_1_3 >= 100 ? roomResult.overType : -roomResult.overType);
                            } else {
                                this.setPlayerScore(player, score_1_3 >= 100 ? -roomResult.overType : roomResult.overType);
                            }
                        }
                    }
                } else { // 1/4 vs 2/3
                    int score_2_3 = this.winners.get(1).getCardScore() + this.winners.get(2).getCardScore();
                    roomResult.overType = score_2_3 == 200 || score_2_3 == 0 ? 2 : 1;
                    for (IRoomPlayer p : this.allPlayer) {
                        if (p != null && !p.isGuest()) {
                            ArchPlayer player = (ArchPlayer) p;
                            PCLIPokerNtfArchGameOverInfo.GameOverInfo info = this.createGameOverInfo(player);
                            roomResult.allGameOverInfo.put(player.getUid(), info);
                            if (player == this.winners.get(1) || player == this.winners.get(2)) {
                                this.setPlayerScore(player, score_2_3 > 100 ? roomResult.overType : -roomResult.overType);
                            } else {
                                this.setPlayerScore(player, score_2_3 > 100 ? -roomResult.overType : roomResult.overType);
                            }
                        }
                    }
                }
            }

            this.locale.basicScoreSettled(this.contract);
        }

        ResultRecordAction resultRecordAction = ((PokerRecord) this.getRecord()).addResultRecordAction();
        for (IRoomPlayer p : this.allPlayer) {
            if (p == null || p.isGuest()) {
                continue;
            }

            int score = this.getPlayerScore(p);
            if (score != 0) {
                p.addScore(score > 0 ? Score.ACC_WIN_CNT : Score.ACC_LOST_CNT, 1, true);
                p.addScore(Score.ACC_TOTAL_SCORE, score, true);
            }

            ResultRecordAction.GameOverInfo actionGameOverInfo = new ResultRecordAction.GameOverInfo();
            actionGameOverInfo.getCard().addAll(((IPokerPlayer) p).getHandCard());
            actionGameOverInfo.setScore(this.getClientScore(score));
            actionGameOverInfo.setTotalScore(this.getClientScore(p.getScore(Score.ACC_TOTAL_SCORE, true)));
            resultRecordAction.getAllGameOverInfo().put(p.getUid(), actionGameOverInfo);
        }

        this.record();
        this.getRecord().save();
    }

    @Override
    protected void doSendGameOver(boolean next) {
        Logs.ROOM.debug(">>>> calling doSendGameOver()...");

        if (this.bureauResult == null) {
            this.bureauResult = new PCLIPokerNtfArchGameOverInfo();
        }
        PCLIPokerNtfArchGameOverInfo roomResult = this.bureauResult;
        roomResult.next = next;
        roomResult.bureau = this.curBureau;

        for (IRoomPlayer p : this.allPlayer) {
            if (p == null || p.isGuest()) {
                continue;
            }

            PCLIPokerNtfArchGameOverInfo.GameOverInfo info = roomResult.allGameOverInfo.get(p.getUid());
            if (info == null) {
                info = this.createGameOverInfo((IPokerPlayer) p);
                roomResult.allGameOverInfo.put(p.getUid(), info);
            }
            Player player = PlayerManager.I.getPlayer(p.getUid());
            info.name = player.getName();
            info.icon = player.getIcon();
            if ((this.locale.features() & ArchRule.FEATURE_HUA_PAI) != 0) {
                for (byte card : ((ArchPlayer) p).getDealtCards()) {
                    if (card == ArchRule.HUA_PAI) {
                        info.huaCnt++;
                    }
                }
            }

            int score = this.getPlayerScore(p);
            info.score = this.getFormatScore(score);
            info.totalScore = this.getFormatScore(p.getScore(Score.ACC_TOTAL_SCORE, true));

            if (!next) {
                info.totalCnt = new PCLIPokerNtfArchGameOverInfo.TotalCnt();
                info.totalCnt.lostCnt = p.getScore(Score.ACC_LOST_CNT, true);
                info.totalCnt.winCnt = p.getScore(Score.ACC_WIN_CNT, true);
            }
        }

        Logs.ROOM.debug("CLI_NTF_ROOM_GAMEOVER %s", roomResult);
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, roomResult);
        this.bureauResult = null;
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        IPokerPlayer pokerPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == pokerPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIPokerNtfArchDeskInfo deskInfo = new PCLIPokerNtfArchDeskInfo();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.curBureau = null == pokerPlayer ? 0 : pokerPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        deskInfo.curTakeIndex = this.curTakeIndex;
        deskInfo.contract = this.contract;
        deskInfo.lastTakePlayerUid = this.lastTakeCardPlayer == null ? -1 : this.lastTakeCardPlayer.getUid();
        deskInfo.lastTakeCards = this.lastTakeCard;
        deskInfo.lastTakeLaizi = this.lastTakeLaizi;
        deskInfo.cards = null == pokerPlayer ? null : pokerPlayer.getHandCard();
        deskInfo.reservedCards = this.contract != 0 ? this.reservedCards : new ArrayList<>();
        deskInfo.firstTakeCard = this.firstTakeCard;
        deskInfo.bankerPartnerUid = -1;
        if (this.contract == ArchRule.CONTRACT_2V2) {
            // 已明鸡，告知盟友信息
            ArchPlayer banker = (ArchPlayer) this.allPlayer[this.bankerIndex];
            if (!banker.getPartner().getHandCard().contains(this.firstTakeCard)) {
                deskInfo.bankerPartnerUid = banker.getPartner().getUid();
            }
        }
        for (ArchPlayer p : this.winners) {
            deskInfo.winners.add(p.getUid());
        }
        // TODO 花牌癞子
        deskInfo.laizi = ArchRule.getLAIZI();
        for (byte card : this.currentRound.cards) {
            deskInfo.onDeskScore += ArchRule.getCardScore(card);
        }

        for (IRoomPlayer p : this.allPlayer) {
            if (p != null && !p.isGuest()) {
                ArchPlayer archPlayer = (ArchPlayer) p;
                deskInfo.curScores.put(p.getUid(), archPlayer.getCardScore());
                deskInfo.scores.put(p.getUid(), this.getClientScore(archPlayer.getScore(Score.ACC_TOTAL_SCORE, true)));
                deskInfo.onlineStates.put(p.getUid(), !p.isOffline());
                deskInfo.playerCardCount.put(p.getUid(), archPlayer.getHandCard().size());
                deskInfo.autoMode.put(p.getUid(), ((RoomPlayer) p).isAutoMode());
            }
        }

        Logs.ROOM.debug("syncDeskInfo %s", deskInfo);
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

    private PokerTakeAction createTimeoutTake(PokerPlayer player, boolean canPass) {
        PokerTakeAction action = new PokerTakeAction(this, player, player.getTimeout(this.timeout));
        action.setCanPass(canPass);
        if (!canPass) {
            byte card = player.getHandCard().get(0);
            action.setCards(Collections.singletonList(card));
            if (ArchRule.getLAIZI().contains(card)) {
                // 如果是癞子
                byte asCard = this.laiziAsValue == PokerUtil._2 ? PokerUtil.TWO_SPADES : PokerUtil.THREE_SPADES;
                action.setLaiZiCards(Collections.singletonList(asCard));
            }
            action.setCardType(EPokerCardType.SINGLE);
            action.setTakeCnt((byte) 1);
        }
        return action;
    }

    private int compareCardColor(byte left, byte right) {
        byte l = PokerUtil.getCardColor(left);
        byte r = PokerUtil.getCardColor(right);
        return Byte.compare(l, r);
    }

    private void roundOver() {
        int score = ArchRule.getCardsScore(this.currentRound.cards);
        if (score > 0) {
            PCLIPokerNtfArchScoreInfo scoreInfo = new PCLIPokerNtfArchScoreInfo();
            scoreInfo.playerUid = this.lastTakeCardPlayer.getUid();
            scoreInfo.source = -1;
            scoreInfo.score = score;
            this.broadcast2Client(CommandId.CLI_NTF_POKER_ARCH_SCORE, scoreInfo);

            ((ArchPlayer) this.lastTakeCardPlayer).addCardScore(score);
        }
        this.currentRound.reset();
//        return score;
    }

    public int getPlayerScore(IRoomPlayer player) {
        return player.getScore(Score.SCORE, false);
    }

    private void setPlayerScore(IRoomPlayer player, int score) {
        player.setScore(Score.SCORE, this.getScore(score), false);
    }

    public void addPlayerScore(IRoomPlayer player, int score) {
        player.addScore(Score.SCORE, this.getScore(score), false);
    }

    public int getScoreWithBase(int value) {
        return this.getScore(value) / 100;
    }

    public PCLIPokerNtfArchGameOverInfo getBureauResult() {
        return bureauResult;
    }

	@Override
	protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {
		// TODO Auto-generated method stub
		
	}
}
