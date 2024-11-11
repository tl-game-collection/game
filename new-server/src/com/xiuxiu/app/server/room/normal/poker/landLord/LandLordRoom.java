package com.xiuxiu.app.server.room.normal.poker.landLord;

import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.*;
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
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.PokerCallScoreAction;
import com.xiuxiu.app.server.room.normal.poker.action.PokerPassAction;
import com.xiuxiu.app.server.room.normal.poker.action.PokerTakeAction;
import com.xiuxiu.app.server.room.normal.poker.runFast.ERunFastPlayRule;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.LandLordPlayer;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;
import com.xiuxiu.app.server.room.record.poker.*;
import com.xiuxiu.core.KeyValue;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 扑克斗地主
 */
@GameInfo(gameType = GameType.GAME_TYPE_LANDLORD)
public class LandLordRoom extends PokerRoom {
    private int callIndex = -1;                                   // 第一个叫分
    private boolean sprint = true;                                // 春天
    private int callScore = 0;                                    // 叫分
    private int bankerPlayerTakeTime = 0;                         // 地主庄家出牌次数
    private boolean showLastCard = false;                         // 底牌
    private HashMap<Long, Integer> allShowCards = new HashMap<>();
    private IPokerPlayer firstShowPlayer = null;                  // 第一个明牌
    protected LandLordLastCardRecordAction lastCardRecordAction;
    protected LandLordShowAllCardRecordAction showAllCardRecordAction;
    protected LandLordCallScoreRecordAction callScoreRecordAction;
    protected PokerCallScoreAction pokerCallScoreAction;
    private List<Byte> lastCard = new ArrayList<>();              //明牌信息
    private boolean bankShowCard = false;                         //地主明牌

    private int laizicnt;                                         //癞子 数量；
    private List<Byte> laiziCards = new ArrayList<>();            //癞子
    private int isSoftBOMB = 0;                                  //出牌是否是软炸 0 硬炸 1 软炸；
    private List<Byte> lastTakeLaiziCard;                         //出的癞子牌

    // 规则-玩法
    private int rulePlay = 0;
    private static final int RULE_WITH_TIMER = 0x0001; // 带计时
    private static final int RULE_WITH_MULTIPLE = 0x0002; // 有加倍
    private static final int RR_DETECTION_IP = 0x004;// 同ip
    
    private static final int RULE_ANPAI = 8;// 暗牌
    private static final int RULE_BIJIAO = 16;// 双王或4个2必叫
    private static final int RULE_ANY_3 = 32;// 任两3为炸弹
    private static final int RULE_SAME_3 = 64;// 同色3为炸弹
    
    private static final int RULE_KICK_SINGLE = 128;// 单独踢
    private static final int RULE_KICK_TOGETHER = 256;// 一起踢
    private static final int RULE_KICK_BACK = 512;// 可回踢
    
    private int callLoopingTimes = 0; // 叫牌轮次，有人叫牌时归零
    
    // 人数
    private int loop;
    
    private boolean anpai = false;
    private boolean bijiao = false;
    private boolean any_3 = false;
    private boolean same_3 = false;
    
    private boolean kickSingle = false;
    private boolean kickTogether = false;
    private boolean kickBack = false;
    
    private AtomicBoolean selectKillTimeOutFlag = new AtomicBoolean(false);
    private AtomicBoolean selectKillBackTimeOutFlag = new AtomicBoolean(false);

    public LandLordRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public LandLordRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.rulePlay = this.getRule().getOrDefault(RoomRule.RR_PLAY, RULE_WITH_TIMER | RULE_WITH_MULTIPLE);
        if ((this.rulePlay & RULE_WITH_TIMER) != 0) {
            this.timeout = 15 * 1000;
        }
        
        this.detectionIP = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RR_DETECTION_IP);
        
        this.anpai = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_ANPAI);
        this.bijiao = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_BIJIAO);
        this.any_3 = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_ANY_3);
        this.same_3 = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_SAME_3);
        
        // 单独踢
        this.kickSingle = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_KICK_SINGLE);
        if (this.kickSingle) {
            // 可回踢
            this.kickBack = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_KICK_BACK);
        } else {
            // 一起踢
            this.kickTogether = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_KICK_TOGETHER);
            if (this.kickTogether) {
                // 可回踢
                this.kickBack = 0 != (this.info.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & RULE_KICK_BACK);
            }
        }
        
        this.ruleBombTop = this.getRule().getOrDefault(RoomRule.RR_LANDLORD_BOMB_TOP, 8);
        if (this.ruleBombTop <= 0) {
            this.ruleBombTop = Integer.MAX_VALUE;
        }

        this.landlordType = this.info.getRule().getOrDefault(RoomRule.RR_LANDLORD_PLAY, 0);
        if (this.landlordType == 2 || this.landlordType == 3) {
            this.laizicnt = this.landlordType - 1;
        }
        this.threeTakeType = PokerUtil.THREE_TAKE_TYPE_0 | PokerUtil.THREE_TAKE_TYPE_1 | PokerUtil.THREE_TAKE_TYPE_11;
        this.fourTakeType = PokerUtil.FOUR_TAKE_TYPE_2 | PokerUtil.FOUR_TAKE_TYPE_11_22;
        this.bombTarget = true;
        this.loop = Math.max(3, this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_NUM, 3));
    }

    @Override
    public IRoomPlayer createPlayer() {
        return new LandLordPlayer(this.getGameType(),this.getRoomUid(), this.getRoomId());
    }
    
    //配牌
//    public byte[] peipaiByte() {
//        byte[] a= {0,29,8,13,42,21,26,30,34,39,43,47,54,5,9,1,18,22,14,31,35,27,44,48,2,6,10,15,19,23,28,32,36,41,45,49,3,7,24,16,20,37,4,33,38,17,46,52,11,12,53,51,50,40,25};
//        return a;
//    }

    @Override
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB_POKER) {
            this.allCard.addAll(CardLibraryManager.I.getPokerCard());
            return;
        }
        for (byte i = 0; i < 54; ++i) {
            this.allCard.add(i);
        }
        // 运城暗牌玩法
//        if (anpai && this.landlordType == 4) {
//              this.allCard.clear();
//              byte[] a= peipaiByte();
//              for(int i=0;i<a.length;i++) {
//                this.allCard.add(a[i]);      
//              }
//          
//            return;   
//        }
        
        //正常
        if (anpai && this.landlordType == 4) {
            this.allCard.add(PokerUtil.ANPAI);
        }
    
        
        
        ShuffleUtil.shuffle(this.allCard);
    }

    @Override
    protected void doDeal() {
        int index = 0;
        for (byte card : this.allCard) {
            int temp = index % loop;
            if (temp < this.playerNum) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[temp];
                if (null == player || player.isGuest()) {
                    ++index;
                    continue;
                }
                player.addHandCard(card);
            }
            if (++index >= 51) {
                break;
            }
        }

        this.firstTakeCard = 0;
        this.firstTakeIndex = 0;
//        if (-1 == this.callIndex) {
            List<Integer> indexs = new ArrayList<Integer>();
            for (int i = 0; i < this.playerNum; ++i) {
                IPokerPlayer tempPlayer = (IPokerPlayer) this.allPlayer[i];
                if (null == tempPlayer || tempPlayer.isGuest()) {
                    continue;
                }
                indexs.add(i);
            }
            int randomIndex = RandomUtil.random(0, indexs.size() - 1);
            int rand1 = indexs.get(randomIndex);
            List<Byte> card = ((IPokerPlayer) this.allPlayer[rand1]).getHandCard();
            int rand2 = RandomUtil.random(0, card.size() - 1);
            this.firstTakeCard = card.get(rand2);
            this.firstTakeIndex = this.allCard.indexOf(this.firstTakeCard);
            this.callIndex = rand1;
            Logs.ROOM.debug("%s callIndex:%s 随机叫牌 ", this, this.firstTakeIndex);
//        } else {
//            Logs.ROOM.debug("%s  ===贏家叫牌===", this);
//        }

        this.bankerIndex = this.callIndex;
        this.curTakeIndex = this.bankerIndex;

        setLaiziCard();
        for (IRoomPlayer iRoomPlayer : this.allPlayer) {
            IPokerPlayer player = (IPokerPlayer) iRoomPlayer;
            if (null == player || player.isGuest()) {
                continue;
            }
            PokerUtil.sort(player.getHandCard());
            player.initHandCard();

            this.getRecord().addPlayer(new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getBureau(), player.getHandCard()));
        }
    }


    @Override
    protected void doStart1() {
        this.firstTake = true;

        IPokerPlayer bPlayer = (IPokerPlayer) this.allPlayer[this.bankerIndex % this.playerNum];

        PCLIPokerNtfCallScoreInfo info = new PCLIPokerNtfCallScoreInfo();
        info.callPlayerUid = -1;
        info.score = 0;
        info.maxScore = 0;
        info.nextCallPlayerUid = bPlayer.getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_CALL_SCORE, info);
        this.pokerCallScoreActionHandler();
    }

    @Override
    public boolean hasHandLaizi(List<Byte> cards) {
        if (this.laiziCards != null && !this.laiziCards.isEmpty()) {
            for (Byte card : cards) {
                if (this.laiziCards.indexOf(card) != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean haslaizi(List<Byte> cards, List<Byte> noLaiziCard) {
        if (this.laiziCards.isEmpty()) {
            return false;
        }
        boolean has = false;
        for (Byte card : cards) {
            if (this.laiziCards.indexOf(card) != -1) {
                has = true;
            } else {
                noLaiziCard.add(card);
            }
        }
        return has;
    }

    private void setLaiziCard() {
        this.laiziCards.clear();
        if (this.landlordType == 1) {          //红桃3 + 方块3 玩法；
            this.laiziCards.add((byte) 0);    //方块3
            this.laiziCards.add((byte) 26);   //红桃3
            return;
        } else if (this.landlordType == 4 && anpai) {
             this.laiziCards.add(PokerUtil.ANPAI);
             return;
        }
        
        List<Byte> randoms = new ArrayList<>();
        for (int i = 0; i < laizicnt; i++) {
            byte rCardValue;
            do {
                int randomValue = RandomUtil.random(0, 13);
                rCardValue = (byte) randomValue;
            } while (randoms.indexOf(rCardValue) != -1 || (this.landlordType == 2 && rCardValue == 13));//普通场 不能用 王 来当癞子
            randoms.add(rCardValue);
        }

        for (Byte value : randoms) {
            if (value == 13) {
                this.laiziCards.add(PokerUtil.KINGLET);
                this.laiziCards.add(PokerUtil.KING);
            } else {
                this.laiziCards.add(value);
                this.laiziCards.add((byte) (value + 13));
                this.laiziCards.add((byte) (value + 26));
                this.laiziCards.add((byte) (value + 39));
            }
        }

        ((PokerRecord) this.getRecord()).addLanLordLaiziCardRecordAction(this.laiziCards);
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
            roomBeginInfo.bureau = player.getBureau();
            roomBeginInfo.firstTakeIndex = this.firstTakeIndex;
            roomBeginInfo.firstTakeCard = this.firstTakeCard;
            roomBeginInfo.laiziCards = this.laiziCards;
            Logs.ROOM.info("%s 游戏开始: 癞子牌:%s ", this, this.laiziCards);
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
    }

    @Override
    protected ErrorCode checkTake(IAction action, IPokerPlayer player, List<Byte> cards, List<Byte> lzCards, int cardType) {
        if (player.getUid() != ((PokerTakeAction) action).getPlayer().getUid()) {
            Logs.ROOM.warn("%s 当前轮跳过人是:%s 而不是你:%s 无效摸牌", this, ((PokerTakeAction) action).getPlayer(), player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (!player.verifyCard(cards)) {
            Logs.ROOM.warn("%s %s 无效打牌, 牌子无效 card:%s cardType:%s", this, player, cards, cardType);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (null != lzCards && lzCards.size() > 0) {
            if (lzCards.contains(PokerUtil.KINGLET) || lzCards.contains(PokerUtil.KING)) {
                Logs.ROOM.warn("%s %s 无效打牌, 牌子无效 card:%s cardType:%s", this, player, cards, cardType);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            if (this.landlordType == 4 && anpai &&(lzCards.contains(PokerUtil.KINGLET) || lzCards.contains(PokerUtil.KING) 
                || lzCards.contains(PokerUtil.THREE_SPADES) || lzCards.contains(PokerUtil.THREE_BOX) || lzCards.contains(PokerUtil.THREE_PEACH) || lzCards.contains(PokerUtil.THREE_PLUM)
                || lzCards.contains(PokerUtil.TWO_BOX) || lzCards.contains(PokerUtil.TWO_PLUM) || lzCards.contains(PokerUtil.TWO_RED_PEACH) || lzCards.contains(PokerUtil.TWO_SPADES))) {
                Logs.ROOM.warn("%s %s 无效打牌, 牌子无效 card:%s cardType:%s", this, player, cards, cardType);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        }
        byte[] cardInfo;
        boolean haslaizi = false;
        if (null != lzCards && lzCards.size() > 0) {
            List<Byte> noLaiziCard = new ArrayList<>();
            haslaizi = this.haslaizi(cards, noLaiziCard);
            noLaiziCard.addAll(lzCards);
            cardInfo = PokerUtil.getCardType(noLaiziCard, bombTarget, this.threeTakeType, this.fourTakeType, false, 3, false, false);
        } else if (1 == this.landlordType && 2 == cards.size() && cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_SPADES)) {
            cardInfo = new byte[]{EPokerCardType.BOMB.getValue(), PokerUtil.TWO, 1};
        } else if(isBomb(cards)) {
            //TODO 不确认逻辑
            cardInfo = new byte[]{EPokerCardType.BOMB.getValue(), PokerUtil.THREE_BOX, 2};
        } else if(4 == this.landlordType && anpai){
            if (1 == cards.size()) {
                if (hasHandLaizi(cards)) {
                    if(player.getHandCard().size() >= 1) {
                        Logs.ROOM.warn("%s %s 无效打牌 , 牌子无效 card:%s cardType:%s", this, player, cards, cardType);
                        return ErrorCode.REQUEST_INVALID_DATA;
                    }else {
                        cardInfo = new byte[]{EPokerCardType.SINGLE.getValue(), PokerUtil.THREE_BOX, 1};
                    }
                }else {
                    cardInfo = new byte[]{EPokerCardType.SINGLE.getValue(),PokerUtil.getCardValue(cards.get(0)), 1};
                }
            
                //cardInfo = PokerUtil.getCardType(cards, bombTarget, this.threeTakeType, this.fourTakeType, false, 3, false, false);
            } else {
                List<Byte> noLaiziCard = new ArrayList<>();
                haslaizi = this.haslaizi(cards, noLaiziCard);
                if(lzCards != null) {
                    noLaiziCard.addAll(lzCards);
                }
               
                cardInfo = PokerUtil.getCardType(noLaiziCard, bombTarget, this.threeTakeType, this.fourTakeType, false, 3, false, false);
            }
        } else {
            cardInfo = PokerUtil.getCardType(cards, bombTarget, this.threeTakeType, this.fourTakeType, false, 3, false, false);
        }

        ErrorCode err = checkTakeCard(player, cards, cardType, cardInfo, haslaizi);
        if (err != ErrorCode.OK) {
            return err;
        }
        EPokerCardType type = EPokerCardType.parse(cardInfo[0]);
//        if (EPokerCardType.BOMB == type || EPokerCardType.KING_FRIED == type) {
//            //炸弹统计
//            if (1 == this.landlordType && 4 == cards.size() && cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_SPADES)
//                    && cards.contains(PokerUtil.THREE_BOX) && cards.contains(PokerUtil.THREE_PEACH)) { //3炸的倍数；
//                boomScore = Math.min(boomScore * 8, this.ruleBombTop);
//            } else {
//                boomScore = Math.min(boomScore * 2, this.ruleBombTop);
//            }
//        }
        ((PokerTakeAction) action).setOp(EActionOp.TAKE);
        ((PokerTakeAction) action).setCards(cards);
        ((PokerTakeAction) action).setLaiZiCards(lzCards);
        ((PokerTakeAction) action).setCardType(type);
        ((PokerTakeAction) action).setTakeMaxCard(cardInfo[1]);
        ((PokerTakeAction) action).setTakeCnt(cardInfo[2]);
        this.tick();

        this.isSoftBOMB = (EPokerCardType.BOMB == type && haslaizi) ? 1 : 0;
        return ErrorCode.OK;
    }

    private ErrorCode checkTakeCard(IPokerPlayer player, List<Byte> cards, int cardType, byte[] cardInfo, boolean hasLaizi) {
        // check card
        EPokerCardType type = EPokerCardType.parse(cardInfo[0]);
        int play = this.getRule().getOrDefault(RoomRule.RR_PLAY, 0);

        if (EPokerCardType.NONE == type) {
            Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (EPokerCardType.THREE == type) {
            if (cards.size() > 3) {
                Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        } else if (EPokerCardType.THREE_TAKE_ONE == type) {
            if (cards.size() > 4) {
                Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        } else if (EPokerCardType.THREE_LINE == type) {
            if (cards.size() >= (5 * cardInfo[2])) {
                if (cards.size() < (5 * cardInfo[2])) {
                    Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
            }
        } else if (EPokerCardType.FOUR_TAKE_TWO_SINGLE == type) {
            if (cards.size() > 6) {
                Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        } else if (EPokerCardType.FOUR_TAKE_TWO_DOUBLE == type) {
            if (cards.size() > 8) {
                Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        }

        if (EPokerCardType.FOUR_TAKE_THREE == type) {
            // 不能四代三
            if (0 == (play & ERunFastPlayRule.FOUR_THREE.getValue())) {
                Logs.ROOM.warn("%s %s 无效打牌, 不能四代三 card:%s cardType:%s", this, player, cards, cardType);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        }

        if (EPokerCardType.NONE != this.curTakeCardType) {
            if ((EPokerCardType.BOMB != type && EPokerCardType.KING_FRIED != type && type != this.curTakeCardType) || EPokerCardType.KING_FRIED == this.curTakeCardType) {
                Logs.ROOM.warn("%s %s 无效打牌, 牌型无效 card:%s cardType:%s", this, player, cards, type);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            if (EPokerCardType.BOMB == this.curTakeCardType) {
                if (EPokerCardType.KING_FRIED != type) {
                    int lastTakeCardsSize = this.lastTakeCard.size();
                    int cardsSize = cards.size();
                    boolean isThreePlumBomb = false; //是否是红黑癞子炸；
                    if (1 == this.landlordType) { //     红黑癞子特殊的地方；
                        if (2 == this.lastTakeCard.size() && this.lastTakeCard.contains(PokerUtil.THREE_PLUM) && this.lastTakeCard.contains(PokerUtil.THREE_SPADES)) {
                            lastTakeCardsSize = 4;
                        }
                        if (2 == cards.size() && cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_SPADES)) {
                            isThreePlumBomb = true;
                            cardsSize = 4;
                        }
                    }
                    if (lastTakeCardsSize > cardsSize) {   //癞子玩法中的判断；
                        Logs.ROOM.warn("%s %s 无效打牌, 牌子较小 card:%s cardType:%s", this, player, cards, type);
                        return ErrorCode.REQUEST_INVALID_DATA;
                    }
                    if (lastTakeCardsSize == cardsSize) {
                        int softBomo = hasLaizi ? 1 : 0;//红黑玩法中的33 就是硬炸；
                        if (this.isSoftBOMB < softBomo) {//0 硬炸 1 软炸；
                            Logs.ROOM.warn("%s %s 4软炸不能打4硬炸 card:%s cardType:%s", this, player, cards, type);
                            return ErrorCode.REQUEST_INVALID_DATA;
                        }

                        if (isThreePlumBomb && this.isSoftBOMB == softBomo && cardInfo[1] < this.curTakeMaxCard) {
                            Logs.ROOM.warn("%s %s 无效打牌, 牌子较小 card:%s cardType:%s", this, player, cards, type);
                            return ErrorCode.REQUEST_INVALID_DATA;
                        }

                        if (this.isSoftBOMB == softBomo && cardInfo[1] <= this.curTakeMaxCard) {
                            Logs.ROOM.warn("%s %s 无效打牌, 牌子较小 card:%s cardType:%s", this, player, cards, type);
                            return ErrorCode.REQUEST_INVALID_DATA;
                        }
                    }
                }
            } else {
                if (EPokerCardType.BOMB != type && EPokerCardType.KING_FRIED != type) {
                    if (cardInfo[1] <= this.curTakeMaxCard) {
                        Logs.ROOM.warn("%s %s 无效打牌, 牌子较小 card:%s cardType:%s", this, player, cards, type);
                        return ErrorCode.REQUEST_INVALID_DATA;
                    }
                    if (cardInfo[2] != this.curTakeCnt) {
                        Logs.ROOM.warn("%s %s 无效打牌, 牌型的数量不对 card:%s cardType:%s", this, player, cards, type);
                        return ErrorCode.REQUEST_INVALID_DATA;
                    }
                    if (this.lastTakeCard.size() != cards.size()) {
                        Logs.ROOM.warn("%s %s 无效打牌, 牌型的数量不对 card:%s cardType:%s", this, player, cards, type);
                        return ErrorCode.REQUEST_INVALID_DATA;
                    }
                }
            }
        }
        return ErrorCode.OK;
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
                PokerPlayer lastPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
                if (lastPlayer.getHandCard().size() == 1 && takeCard.size() == 1) {
                	takeCard.clear();
                	byte m_maxCard = nextPlayer.getHandCard().get(nextPlayer.getHandCard().size() - 1);
                	takeCard.add(m_maxCard);
                	action.setCards(takeCard);
                	action.setTakeMaxCard(takeCard.get(0));
                }
            }
          
        } else {
            // TODO 跳过一轮
            byte[] cardInfo = PokerUtil.getCardType(nextPlayer.getHandCard(), bombTarget, this.threeTakeType, this.fourTakeType, false, 2, false, this.isAAABomb);
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
                if (4 == this.info.getRule().getOrDefault(RoomRule.RR_LANDLORD_PLAY, 0) && nextPlayer.getHandCard().size() > 2) {
                    if (same_3) {
                        if (nextPlayer.getHandCard().contains(PokerUtil.THREE_PLUM) && nextPlayer.getHandCard().contains(PokerUtil.THREE_SPADES) || nextPlayer.getHandCard().contains(PokerUtil.THREE_PEACH) && nextPlayer.getHandCard().contains(PokerUtil.THREE_BOX)){
                            break;
                        }
                    }
                    if (any_3) {
                        if (nextPlayer.getHandCard().contains(PokerUtil.THREE_PLUM) && nextPlayer.getHandCard().contains(PokerUtil.THREE_SPADES)
                                || nextPlayer.getHandCard().contains(PokerUtil.THREE_PLUM) && nextPlayer.getHandCard().contains(PokerUtil.THREE_PEACH)
                                || nextPlayer.getHandCard().contains(PokerUtil.THREE_PLUM) && nextPlayer.getHandCard().contains(PokerUtil.THREE_BOX)
                                || nextPlayer.getHandCard().contains(PokerUtil.THREE_SPADES) && nextPlayer.getHandCard().contains(PokerUtil.THREE_PEACH)
                                || nextPlayer.getHandCard().contains(PokerUtil.THREE_SPADES) && nextPlayer.getHandCard().contains(PokerUtil.THREE_BOX)
                                || nextPlayer.getHandCard().contains(PokerUtil.THREE_PEACH) && nextPlayer.getHandCard().contains(PokerUtil.THREE_BOX)){
                            break;
                        }
                    }
                }
                // 癞子牌不能充当大小王、2和3使用
                if (this.landlordType == 4 && anpai && hasHandLaizi(nextPlayer.getHandCard())  && nextPlayer.getHandCard().size() > 1 && (nextPlayer.getHandCard().contains(PokerUtil.THREE_SPADES) 
                    || nextPlayer.getHandCard().contains(PokerUtil.THREE_BOX) || nextPlayer.getHandCard().contains(PokerUtil.THREE_PEACH) || nextPlayer.getHandCard().contains(PokerUtil.THREE_PLUM)
                    || nextPlayer.getHandCard().contains(PokerUtil.TWO_BOX) || nextPlayer.getHandCard().contains(PokerUtil.TWO_PLUM) || nextPlayer.getHandCard().contains(PokerUtil.TWO_RED_PEACH) || nextPlayer.getHandCard().contains(PokerUtil.TWO_SPADES)
                    || nextPlayer.getHandCard().contains(PokerUtil.KINGLET) || nextPlayer.getHandCard().contains(PokerUtil.KING))) {
                    break;
                }

                isAuto = true;
            } while (false);
            if (isAuto) {
                List<Byte> takeCard = new ArrayList<>();
                takeCard.addAll(nextPlayer.getHandCard());
                action.setCards(takeCard);
            
                PokerPlayer lastPlayer = (PokerPlayer) this.getNextRoomPlayer(this.curTakeIndex);
                if (lastPlayer.getHandCard().size() == 1 && takeCard.size() == 1) {
                    takeCard.clear();
                    byte m_maxCard = nextPlayer.getHandCard().get(nextPlayer.getHandCard().size() - 1);
                    takeCard.add(m_maxCard);
                    action.setCards(takeCard);
                }
            
  
                action.setCardType(EPokerCardType.parse(cardInfo[0]));
                action.setTakeMaxCard(cardInfo[cardInfo.length - 1]);
                action.setTakeCnt(this.curTakeCnt);
                // 斗地主 红黑癞子玩法 最后一手牌为两黑3
                if (1 == this.landlordType && 2 == nextPlayer.getHandCard().size() && nextPlayer.getHandCard().contains(PokerUtil.THREE_PLUM) && nextPlayer.getHandCard().contains(PokerUtil.THREE_SPADES)) {
                    boomScore = Math.min(boomScore * 2, this.ruleBombTop);
                } else if(isBomb(nextPlayer.getHandCard())) {
                    //boomScore = Math.min(boomScore * 2, this.ruleBombTop);
                }
            } else {    // 要不起的情况下 最后一手牌 1S后自动出
                
                //3带对  连对 飞机包含赖子不能出  ysy
                if ((EPokerCardType.THREE_TAKE_ONE == type || EPokerCardType.THREE_TAKE_11 == type || EPokerCardType.DOUBLE_LINE == type || EPokerCardType.THREE_LINE == type) && hasHandLaizi(nextPlayer.getHandCard())) {
                    
                }else {
                    this.getCanTakeCard(action, (PokerPlayer) this.allPlayer[this.curTakeIndex]);
                }
   
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
    
    private boolean isBomb(List<Byte> cards) {
        if (4 == this.landlordType && 2 == cards.size()) {
            if (same_3) {
                return cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_SPADES) || cards.contains(PokerUtil.THREE_PEACH) && cards.contains(PokerUtil.THREE_BOX);
            }
            if (any_3) {
                return cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_SPADES)
                        || cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_PEACH)
                        || cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_BOX)
                        || cards.contains(PokerUtil.THREE_SPADES) && cards.contains(PokerUtil.THREE_PEACH)
                        || cards.contains(PokerUtil.THREE_SPADES) && cards.contains(PokerUtil.THREE_BOX)
                        || cards.contains(PokerUtil.THREE_PEACH) && cards.contains(PokerUtil.THREE_BOX);
            }
        }
        return false;
    }
    
    @Override
    public void onTake(IPokerPlayer player, List<Byte> cards, List<Byte> laiZiCards, EPokerCardType cardType, byte takeMaxCard, byte takeCnt) {
        if (player.getIndex() == this.bankerIndex) {
            this.bankerPlayerTakeTime++;
        }
        // 种类：0-经典，1-红黑癞子，2-普通癞子，3 天地癞子，4 运城玩法
        if (1 == this.landlordType && 2 == cards.size() && cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_SPADES)
            || (isBomb(cards))) {
            cardType = EPokerCardType.BOMB;
        }
        //super.onTake(player, cards, laiZiCards, cardType, takeMaxCard, takeCnt);
        this.firstTake = false;
        this.passCount = 0;
        player.takeCard(cards);

        this.curTakeMaxCard = takeMaxCard;
        this.curTakeCnt = takeCnt;
        this.curTakeCardSize = (byte) cards.size();
        this.curTakeCardType = cardType;
        this.lastTakeCard = cards;
        this.lastTakeCardPlayer = player;

        ((PokerRecord) this.getRecord()).addTakeRecordAction(player.getUid(), cards);

        if (EPokerCardType.BOMB == cardType || EPokerCardType.KING_FRIED == cardType) {
            player.addScore(Score.ACC_POKER_BOMB_CNT, 1, true);
        }

        if (EPokerCardType.BOMB == cardType || EPokerCardType.KING_FRIED == cardType) {
            //炸弹统计
            if (1 == this.landlordType && 4 == cards.size() && cards.contains(PokerUtil.THREE_PLUM) && cards.contains(PokerUtil.THREE_SPADES)
                    && cards.contains(PokerUtil.THREE_BOX) && cards.contains(PokerUtil.THREE_PEACH)) { //3炸的倍数；
                boomScore = Math.min(boomScore * 8, this.ruleBombTop);
            } else {
                boomScore = Math.min(boomScore * 2, this.ruleBombTop);
            }
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
                if(EPokerCardType.KING_FRIED == EPokerCardType.parse(max[0]) && takeCard.contains(PokerUtil.getCardValue(PokerUtil.KING)) && takeCard.contains(PokerUtil.getCardValue(PokerUtil.KINGLET))){
                    takeCard.clear();
                    takeCard.add(PokerUtil.KING);
                    takeCard.add(PokerUtil.KINGLET);
                }
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
                if (this.isAAABomb && nextPlayer.hasCardCnt(PokerUtil._A, 3) && nextPlayer.getHandCard().size() > 3) {
                    isAuto = false;
                }
            }
               if(this.hasHandLaizi(nextPlayer.getHandCard())){
                   isAuto=false;
               }
            if (0 == this.info.getRule().getOrDefault(RoomRule.RR_RF_MUST_PUT, 1)) {
                action.setCanPass(true);
            } else {
                if (action.isCanPass()) {
                    action.setTimeout(1000);
                }
            }
            if (isAuto) {
                action.setCanPass(false);
                action.setTimeout(1000);
            }
            this.broadcast2Client(CommandId.CLI_NTF_POKER_TAKE, new PCLIPokerNtfTakeInfo(player.getUid(), cards, cardType.getValue(), nextPlayer.getUid(), laiZiCards));
            this.broadcast2Client(CommandId.CLI_NTF_POKER_AUTO_MODE, new PCLIPokerNetAutoMode(player.getUid(), ((RoomPlayer) player).isAutoMode()));
            this.addAction(action);
            this.sendMyCard();
        } else {
            this.broadcast2Client(CommandId.CLI_NTF_POKER_TAKE, new PCLIPokerNtfTakeInfo(player.getUid(), cards, cardType.getValue(), -1, laiZiCards));
            //最后一手为炸弹
            if (EPokerCardType.BOMB == this.curTakeCardType || EPokerCardType.KING_FRIED == this.curTakeCardType) {
                //boomScore = Math.min(boomScore * 2, this.ruleBombTop);
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
                //
            }

            // TODO finish
            this.winPoker = player;
            this.gameOver(this.checkAgain());
            this.bankerIndex = this.winPoker.getIndex();
            this.stop();
        }
        this.lastTakeLaiziCard = laiZiCards;
    }
    
    /**
     * 获取叫了地主的玩家数量
     * @return
     */
    private int getCallScorePlayerNum(List<KeyValue<Long, Integer>> allCallScore) {
        int num = 0;
        for(KeyValue<Long, Integer> item :allCallScore) {
            if (item.getValue()>0) {
                ++num;
            }
        }
        return num;
    }
    
    private int prevIndex = -1;
    private int allCallScoreNum = -1;
    
    //通知底牌
    public void onLastCard(IPokerPlayer player, int callScore, List<KeyValue<Long, Integer>> allCallScore) {
        this.callScore = callScore;
        this.callLoopingTimes = 0;
        this.showLastCard = true;
        List<Byte> lastThreeCard = new ArrayList<>();
        if(this.landlordType==4 && this.anpai) {
            for (int i = 51; i < 55; ++i) {
                lastThreeCard.add(this.allCard.get(i));
                player.addHandCard(this.allCard.get(i));
            }
            PokerUtil.sort(player.getHandCard());
        }else{
            for (int i = 51; i < 54; ++i) {
                lastThreeCard.add(this.allCard.get(i));
                player.addHandCard(this.allCard.get(i));
            }
        }
       

        //playerUid card
        this.lastCardRecordAction = ((PokerRecord) this.getRecord()).addLandLordLastCardRecordAction(player.getUid(), lastThreeCard);

        this.bankerIndex = player.getIndex();
        this.curTakeIndex = this.bankerIndex;

        PCLIPokerNtfLastCardInfo info = new PCLIPokerNtfLastCardInfo();
        info.PlayerUid = player.getUid();
        info.cards = lastThreeCard;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_LAST_CARD, info);

        this.lastCard = lastThreeCard;
        if ((this.rulePlay & RULE_WITH_MULTIPLE) != 0) {
            // 通知开始翻倍
            this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_MULTIPLE_BEGIN, null);

            // 倒计时
            LandLordRoom self = this;
            DelayAction delayAction = new DelayAction(this, 10 * 1000);
            delayAction.setCallback(args -> self.onMultipleTimeout());
            this.addAction(delayAction);
        } else if ((this.kickSingle || this.kickTogether)) {
            if (this.playerNum == 3) {
                allCallScoreNum = allCallScore.size();
                if (getCallScorePlayerNum(allCallScore) == 1 && (allCallScoreNum == 3)) {
                    // 开始打牌
                    this.startTake();
                } else {
                    IRoomPlayer roomPlayer = getNextRoomPlayer(this.bankerIndex);
                    if (roomPlayer != null) {
                        IRoomPlayer tempRoomPlayer = getNextRoomPlayer(roomPlayer.getIndex());
                        if (tempRoomPlayer!=null) {
                            prevIndex = tempRoomPlayer.getIndex(); 
                        }
                        if (allCallScoreNum == 2) {
                            roomPlayer.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK,null);
                        } else {
                            // 通知开始选择踢
                            this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_KICK,null, player.getUid(), false);
                        }
                        // 倒计时
                        LandLordRoom self = this;
                        DelayAction delayAction = new DelayAction(this, 30 * 1000);
                        delayAction.setCallback(args -> self.onKickSelcteTimeout());
                        this.addAction(delayAction);
                    } else {
                        // 开始打牌
                        this.startTake();
                    }
                }
            } else {
                // 通知开始选择踢
                this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_KICK,null, player.getUid(), false);
                LandLordRoom self = this;
                DelayAction delayAction = new DelayAction(this, 30 * 1000);
                delayAction.setCallback(args -> self.onKickSelcteTimeout());
                this.addAction(delayAction);
            }
        } else {
            // 开始打牌
            this.startTake();
        }
    }

    /**
     * 选择踢超时处理
     */
    private void onKickSelcteTimeout() {
        if(selectKillTimeOutFlag.get()) {
            return;
        }
        boolean needKickBack = false;
        if (this.kickSingle) {
            for (IRoomPlayer p : this.allPlayer) {
                if (p != null && !p.isGuest()) {
                    LandLordPlayer landLordPlayer = (LandLordPlayer)p;
                    if (landLordPlayer.isKick()) {
                        needKickBack = true;
                        continue;
                    }
                    landLordPlayer.callKickSelected(false);
                }
            }
        } else {
            for (IRoomPlayer p : this.allPlayer) {
                if (p != null && !p.isGuest()) {
                    LandLordPlayer landLordPlayer = (LandLordPlayer)p;
                    landLordPlayer.callKickSelected(false);
                }
            }
        }
        if (needKickBack && this.kickBack) {
            IRoomPlayer roomPlayer = this.allPlayer[this.bankerIndex];
            if (roomPlayer != null) {
                int tickfalg=0;
                for (int i = 0; i < this.playerNum; ++i) {
                    IPokerPlayer p = (IPokerPlayer) this.allPlayer[i];
                    if (null == p || p.isGuest() || this.bankerIndex == p.getIndex()) {
                            continue;
                    }
                    LandLordPlayer tickplayer = (LandLordPlayer) p;
                     if(tickplayer.isKickSelected() && tickplayer.isKick()) {
                         tickfalg++;
                     }
                }
                
                if (tickfalg > 0) {
                    // 通知开始选择回踢
                    roomPlayer.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK, null);
                    // 倒计时
                    LandLordRoom self = this;
                    DelayAction delayAction = new DelayAction(this, 30 * 1000);
                    delayAction.setCallback(args -> self.onKickBackSelectTimeout());
                    this.addAction(delayAction);
                } else {
                    this.startTake();
                }
            } else {
                this.startTake();
            }
        } else {
            this.startTake();
        }
    }

    private void onKickBackSelectTimeout() {
        if (selectKillBackTimeOutFlag.get()) {
            return;
        }
        IRoomPlayer roomPlayer = this.allPlayer[this.bankerIndex];
        if (roomPlayer != null) {
            ((LandLordPlayer)roomPlayer).callKickBackSelected(false);
        }
        this.startTake();
    }

    private void onMultipleTimeout() {
        for (IRoomPlayer p : this.allPlayer) {
            if (p != null && !p.isGuest() && !((LandLordPlayer) p).isCalledMultiple()) {
                this.playerCallMultiple((Player) p.getPlayer(), 0);
            }
        }
    }

    public void playerCallMultiple(Player player, int value) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法加倍", this, player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_MULTIPLE_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        if ((this.rulePlay & RULE_WITH_MULTIPLE) == 0) {
            Logs.ROOM.warn("%s %s 房间未开启加倍, 无法加倍", this, player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_MULTIPLE_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        if (value != 0 && value != 2 && value != 4) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_MULTIPLE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return;
        }

        LandLordPlayer llPlayer = (LandLordPlayer) this.getRoomPlayer(player.getUid());
        if (!this.showLastCard || llPlayer.isCalledMultiple()) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_MULTIPLE_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        llPlayer.callMultiple(value == 0 ? 1 : value);
        player.send(CommandId.CLI_NTF_POKER_LAND_LORD_MULTIPLE_OK, null);

        // 检测是否所有人都叫过加倍了
        boolean allCalled = true;
        for (IRoomPlayer p : this.allPlayer) {
            if (p != null && !p.isGuest() && !((LandLordPlayer) p).isCalledMultiple()) {
                allCalled = false;
                break;
            }
        }

        PCLIPokerNtfLandLordMultiple ntf = new PCLIPokerNtfLandLordMultiple();
        ntf.playerUid = player.getUid();
        ntf.value = value;
        ntf.allCalled = allCalled;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_MULTIPLE, ntf);

        if (allCalled) { // 都叫过了，开始打牌
            this.startTake();
        }
    }

    private void startTake() {
        selectKillBackTimeOutFlag.set(true);
        PokerPlayer player = (PokerPlayer) this.allPlayer[this.curTakeIndex];
        PokerTakeAction action = new PokerTakeAction(this, player, player.getTimeout(this.timeout));
        action.setOp(EActionOp.TAKE);
        List<Byte> takeCard = new ArrayList<>();
        takeCard.add(((PokerPlayer) this.allPlayer[this.curTakeIndex]).getHandCard().get(0));
        action.setCards(takeCard);
        action.setCardType(EPokerCardType.SINGLE);
        action.setTakeMaxCard(PokerUtil.getCardValue(((PokerPlayer) this.allPlayer[this.curTakeIndex]).getHandCard().get(0)));
        action.setTakeCnt((byte) 1);
        this.addAction(action);
        IRoomPlayer roomPlayer = this.allPlayer[this.bankerIndex];
        PCLIPokerNtfCanTakeInfo info =new PCLIPokerNtfCanTakeInfo();
        info.takePlayerUid = roomPlayer != null ? roomPlayer.getUid() : 0;
        this.broadcast2Client(CommandId.CLI_NTF_POKER_CAN_TAKE, info);
    }
    //叫分
    public ErrorCode callScore(Player player, int score) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法叫分", this, player);
            return ErrorCode.ROOM_NOT_START;
        }

        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法叫分", this, player);
            return ErrorCode.REQUEST_INVALID;
        }

        if (score > this.callScore) {
            this.callScore = score;
        }

        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());

        if (null != this.callScoreRecordAction) {
            this.callScoreRecordAction.addHandCard(player.getUid(), roomPlayer.getHandCard());
        }

        IAction action = this.action.peek();
        if (action instanceof PokerCallScoreAction) {
            ErrorCode err = ((PokerCallScoreAction) action).callScore(roomPlayer.getUid(), score);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        } else {
            Logs.ROOM.debug("action Score is ok");
        }
        return ErrorCode.REQUEST_INVALID;
    }

    //明牌
    public ErrorCode showCard(Player player) {
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());

        if (this.allShowCards.containsKey(player.getUid())) {
            Logs.ROOM.warn("%s playerUid:%d 已经明过牌了", this, player.getUid());
            return ErrorCode.REPEAT_OPERATE;
        }

        List<Byte> showCards = new ArrayList<>(roomPlayer.getHandCard());
//        showCards.addAll(roomPlayer.getHandCard());
        this.showAllCardRecordAction = ((PokerRecord) this.getRecord()).addLandLordShowAllCardRecordAction(player.getUid(), showCards);

        if (null == this.firstShowPlayer) {
            this.firstShowPlayer = roomPlayer;

            if (!this.showLastCard) {
                //ntf client show card first call score
                this.bankerIndex = roomPlayer.getIndex();
                this.curTakeIndex = this.bankerIndex;
                this.pokerCallScoreAction.setFirstCallScoreIndex(this.curTakeIndex);

                PCLIPokerNtfCallScoreInfo info = new PCLIPokerNtfCallScoreInfo();
                info.callPlayerUid = -1;
                info.score = 0;
                info.maxScore = 0;
                info.nextCallPlayerUid = player.getUid();
                this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_CALL_SCORE, info);
            }
        }

        this.allShowCards.put(roomPlayer.getUid(), -1);

        PCLIPokerNtfShowCardInfo info = new PCLIPokerNtfShowCardInfo();
        info.PlayerUid = roomPlayer.getUid();
        info.cards = roomPlayer.getHandCard();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_SHOW_CARD_INFO, info);

        return ErrorCode.OK;
    }

    //重新发牌
    public void onDealCard() {
        this.callLoopingTimes++;
        this.clear();
        this.doShuffle();
        this.doDeal();
        this.doStart1();
        for (IRoomPlayer iRoomPlayer : this.allPlayer) {
            IPokerPlayer player = (IPokerPlayer) iRoomPlayer;
            if (null == player || player.isGuest()) {
                continue;
            }

            PCLIPokerNtfReCallScoreInfo info = new PCLIPokerNtfReCallScoreInfo();
            info.bankerIndex = this.bankerIndex;
            info.myIndex = player.getIndex();
            info.myCards = player.getHandCard();
            info.laiziCards = this.laiziCards;

            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_RECALL_SCORE, info);
        }
    }

    private void pokerCallScoreActionHandler() {
        if (null == this.callScoreRecordAction) {
            this.callScoreRecordAction = ((PokerRecord) this.getRecord()).addLandLordCallScoreAction();
        }

        this.pokerCallScoreAction = new PokerCallScoreAction(this, (PokerPlayer) this.allPlayer[this.curTakeIndex], -1, this.callScoreRecordAction, this.callLoopingTimes);
        this.pokerCallScoreAction.setPlayerNum(this.playerNum);
        this.pokerCallScoreAction.setFirstCallScoreIndex(this.curTakeIndex);
        this.addAction(this.pokerCallScoreAction);
    }

    private int getScore(LandLordPlayer bankerPlayer, LandLordPlayer otherPlayer, int value) {
        int tempBoomScore = this.boomScore;
        try {
          // 是否是运城玩法
          if (landlordType == 4) {
              if (sprint) {
                  tempBoomScore = Math.min(tempBoomScore * 2, this.ruleBombTop);
              }
              if (kickSingle || kickTogether) {
                  if (bankerPlayer!=null && bankerPlayer.isKickBack() && otherPlayer.isKick()) {
                      tempBoomScore = Math.min(tempBoomScore * 2, this.ruleBombTop);
                  }
                  if (otherPlayer.isKick()) {
                      tempBoomScore = Math.min(tempBoomScore * 2, this.ruleBombTop);
                  }
              }
              return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * value * tempBoomScore * (this.bankShowCard ? 2 : 1);
          }
        } catch(Exception e) {
            e.printStackTrace();
        }
      return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * value * (this.sprint ? 2 : 1) * tempBoomScore * (this.bankShowCard ? 2 : 1);
  }
    @Override
    protected void gameOver(boolean next) {
        if (null != this.winPoker) {
            this.callIndex = this.winPoker.getIndex();
            if (this.allShowCards.containsKey(this.allPlayer[this.bankerIndex].getUid())) {
                Logs.ROOM.debug("%s winPoker uid:%s 地主明牌, show cnt: %s", this, this.allPlayer[this.bankerIndex].getUid(), this.allShowCards.size());
                this.bankShowCard = true;
            }
            IPokerPlayer bankerPlayer = null;
            for (int i = 0; i < this.playerNum; ++i) {
                IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (this.bankerIndex == player.getIndex()) {
                    bankerPlayer = player;
                }
            }
            if (this.winPoker.getIndex() == this.bankerIndex) {
                // 地主赢
                int winScore = 0;
                int falg=0;
                
                Logs.ROOM.debug("%s bankerIndex:%d 地主赢", this, this.bankerIndex);
                
                for(int i=0;i<this.playerNum;i++) {
                    IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest() || this.bankerIndex==player.getIndex()) {
                        
                        continue;
                    }
                    if(player.getTakeCnt()>0) {
                        falg+=1;
                    }
                }
                for (int i = 0; i < this.playerNum; ++i) {
                    IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest()) {
                        continue;
                    }

                    //需求改成 如果地主赢 ，其中有农民一张牌也没出，那么这个农民被春天，另外一个农民如果有出牌，就不被春天；
                    this.sprint = (falg==0);
                    int score = getScore((LandLordPlayer)bankerPlayer,(LandLordPlayer)player,this.callScore);
                    if (this.allShowCards.containsKey(player.getUid())) {
                        player.addScore(Score.SCORE, -2 * score, false);
                        winScore += score * 2;
                    } else {
                        player.addScore(Score.SCORE, -score, false);
                        winScore += score;
                    }
                    player.addScore(Score.ACC_LOST_CNT, 1, true);
                }
                if (null != bankerPlayer) {
                    bankerPlayer.addScore(Score.SCORE, winScore, false);
                    bankerPlayer.addScore(Score.ACC_WIN_CNT, 1, true);
                }
            } else {
                // 农民赢
                int lostScore = 0;
                Logs.ROOM.debug("%s bankerIndex:%d 农民赢", this, this.bankerIndex);
                for (int i = 0; i < this.playerNum; ++i) {
                    IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest()) {
                        continue;
                    }
                    if (this.bankerIndex == player.getIndex()) {
                        bankerPlayer = player;
                    } else {
                        this.sprint = 1 == this.bankerPlayerTakeTime;
                        int score = getScore((LandLordPlayer)bankerPlayer, (LandLordPlayer)player, this.callScore);
                        if (this.allShowCards.containsKey(player.getUid())) {
                            player.addScore(Score.SCORE, 2 * score, false);
                            lostScore += 2 * score * -1;
                        } else {
                            player.addScore(Score.SCORE, score, false);
                            lostScore += score * -1;
                        }
                        player.addScore(Score.ACC_WIN_CNT, 1, true);
                    }
                }
                if (null != bankerPlayer) {
                    bankerPlayer.addScore(Score.SCORE, lostScore, false);
                    bankerPlayer.addScore(Score.ACC_LOST_CNT, 1, true);
                }
            }

            // 加倍
            LandLordPlayer banker = ((LandLordPlayer) this.getRoomPlayer(this.bankerIndex));
            for (IRoomPlayer player : this.allPlayer) {
                if (player == null || player.isGuest() || player.getIndex() == this.bankerIndex) {
                    continue;
                }

                int multiple = ((LandLordPlayer) player).getMultipleValue() * banker.getMultipleValue();
                if (multiple > 1) {
                    int score = player.getScore(Score.SCORE, false);
                    int extraScore = score * (multiple - 1);
                    player.addScore(Score.SCORE, extraScore, false);
                    banker.addScore(Score.SCORE, -extraScore, false);
                }
            }
            
        }

        ResultRecordAction resultRecordAction = ((PokerRecord) this.getRecord()).addResultRecordAction();
        PCLIPokerReqLandLordMultiple pcliPokerReqLandLordMultiple = new PCLIPokerReqLandLordMultiple();
        PCLIPokerNtfBombScoreInfo pcliPokerNtfBombScoreInfo = new PCLIPokerNtfBombScoreInfo();
        PCLIPokerReqLandCallScoreInfo pcliPokerReqLandCallScoreInfo = new PCLIPokerReqLandCallScoreInfo();
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
        }
        this.getRoomHandle().calculateGold();
        for (int i = 0; i < this.playerNum; ++i) {
            LandLordPlayer player = (LandLordPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }

            player.addScore(Score.ACC_TOTAL_SCORE, player.getScore(Score.SCORE, false), true);

            ResultRecordAction.GameOverInfo actionGameOverInfo = new ResultRecordAction.GameOverInfo();
            actionGameOverInfo.setBombScore(pcliPokerNtfBombScoreInfo.bombScore);
            actionGameOverInfo.setPlayerScore(pcliPokerReqLandCallScoreInfo.score);
            actionGameOverInfo.setValueB(pcliPokerReqLandLordMultiple.value);
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

            // 加倍玩法
            PCLIPokerNtfGameOverInfo.GameOverInfo info = new PCLIPokerNtfGameOverInfo.GameOverInfo();
            info.card.addAll(player.getHandCard());
            info.score = this.getClientScore(player.getScore(Score.SCORE, false));
            info.totalScore = this.getClientScore(player.getScore(Score.ACC_TOTAL_SCORE, true));
            info.isCloseDoor = this.sprint;
            
            

            if (!next) {
                PCLIPokerNtfGameOverInfo.TotalCnt totalCnt = new PCLIPokerNtfGameOverInfo.TotalCnt();
                totalCnt.lostCnt = player.getScore(Score.ACC_LOST_CNT, true);
                totalCnt.winCnt = player.getScore(Score.ACC_WIN_CNT, true);
                totalCnt.bombCnt = player.getScore(Score.ACC_POKER_BOMB_CNT, true);
                info.totalCnt = totalCnt;
            }
            gameOverInfo.allGameOverInfo.put(player.getUid(), info);
        }
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, gameOverInfo);
    }

    @Override
    protected int getScore(int value) {
//        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 * value * this.boomScore * (this.sprint ? 2 : 1) * (this.bankShowCard ? 2 : 1);
        int tempBoomScore = this.boomScore;
        // 是否是运城玩法
        if (landlordType == 4 && sprint) {
            tempBoomScore = Math.min(tempBoomScore * 2, this.ruleBombTop);
        }
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * value * tempBoomScore * (this.bankShowCard ? 2 : 1);
    }

    @Override
    public void clear() {
        super.clear();

        this.allShowCards.clear();
        this.firstShowPlayer = null;
        //this.callIndex = -1;
        this.boomScore = 1;
        this.sprint = true;
        this.callScore = 0;
        this.bankerPlayerTakeTime = 0;
        this.showLastCard = false;

        this.lastCardRecordAction = null;
        this.showAllCardRecordAction = null;
        this.callScoreRecordAction = null;
        this.pokerCallScoreAction = null;
        this.lastCard = null;
        this.bankShowCard = false;
        this.lastTakeLaiziCard = null;
        this.selectKillTimeOutFlag.set(false);
        this.selectKillBackTimeOutFlag.set(false);
        this.prevIndex = -1;
        this.allCallScoreNum = -1;
    }

    @Override
    public void syncDeskInfo(IPlayer player) {
        IPokerPlayer landLordPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == landLordPlayer && !this.watchList.contains(player.getUid())) {
            return;
        }
        PCLIPokerNtfLandLordDeskInfo deskInfo = new PCLIPokerNtfLandLordDeskInfo();
        deskInfo.roomInfo = this.getRoomInfo();
        deskInfo.curBureau = null == landLordPlayer ? 0 : landLordPlayer.getBureau();
        deskInfo.gameing = this.roomState.get() == ERoomState.START;
        deskInfo.bankerIndex = this.bankerIndex;
        deskInfo.curTakeIndex = this.curTakeIndex;
        deskInfo.maxScore = this.callScore;
        deskInfo.lastTakeCard = this.lastTakeCard;
        deskInfo.card = null == landLordPlayer ? null : landLordPlayer.getHandCard();
        deskInfo.lastTakePlayerUid = null == this.lastTakeCardPlayer ? -1 : this.lastTakeCardPlayer.getUid();
        deskInfo.lastCard = this.lastCard;
        deskInfo.laiziCard = this.laiziCards;
        deskInfo.lastTakeLaiziCard = this.lastTakeLaiziCard;
        deskInfo.boomScore = this.boomScore;
        deskInfo.timeout = this.action.isEmpty() ? 0 : ((BaseAction) this.action.peek()).getRemain();
        //没有通知底牌，不会产生地主
        if(!this.showLastCard) {
            this.bankerIndex=-1;
        }
        IPokerPlayer bankerPlayer = null;
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player1 = (IPokerPlayer) this.allPlayer[i];
            if (null == player1 || player1.isGuest()) {
                continue;
            }
            if (this.bankerIndex == player1.getIndex()) {
                bankerPlayer = player1;
                deskInfo.bankerPlayerUid=player1.getUid();//--地主uid尹哲伟专用字段
            }
        }
 
        HashMap<Long,Map<String,Boolean>> kicks    = new HashMap<>();
        for (int i = 0; i < this.playerNum; ++i) {
            LandLordPlayer temp = (LandLordPlayer) this.allPlayer[i];
            if (null == temp) {
                continue;
            }
            //kickSingle 踢 ,kickTogether 一起踢,   回踢 kickBack  
            Map<String,Boolean> kick=new HashMap<String,Boolean>();
            kick.put("kick", temp.isKick());
            kick.put("kickBack",temp.isKickBack());
            kick.put("kickSelected",temp.isKickSelected());
            kick.put("kickBackSelected",true);
            if(this.kickBack && bankerPlayer!=null && bankerPlayer.getUid()==temp.getUid()) {
                boolean allSelected = true;
                for (IRoomPlayer p : this.allPlayer) {
                    if (p != null && !p.isGuest() && p.getIndex() != this.bankerIndex && !((LandLordPlayer) p).isKickSelected()) {
                        allSelected = false;
                        break;
                    }
                }
                   if(allSelected && temp.isKickBackSelected()) {
                       kick.put("kickBackSelected",false);
                   }
            }
            

            
            kicks.put(temp.getUid(),kick);
            deskInfo.otherCardCnt.put(temp.getUid(), temp.getHandCard().size());
            deskInfo.allScore.put(temp.getUid(), this.getClientScore(temp.getScore(Score.ACC_TOTAL_SCORE, true)));
            deskInfo.allOnlineState.put(temp.getUid(), !temp.isOffline());
            if (this.allShowCards.containsKey(temp.getUid())) {
                deskInfo.allShowCards.put(temp.getUid(), temp.getHandCard());
            }
            deskInfo.multiples.put(temp.getUid(), ((LandLordPlayer) temp).getMultipleValue());
            deskInfo.autoMode.put(temp.getUid(), ((RoomPlayer) temp).isAutoMode());
        }
        
        deskInfo.kick=kicks;

        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
    }

	@Override
	protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 踢/不踢的选择处理
	 * @param player
	 * @param type
	 */
    public void kickSelect(Player player, int type) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法选择踢", this, player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        if (!(kickSingle || kickTogether)) {
            Logs.ROOM.warn("%s %s 房间未开启踢, 无法选择踢", this, player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        if (type != 0 && type != 1) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return;
        }

        LandLordPlayer llPlayer = (LandLordPlayer) this.getRoomPlayer(player.getUid());
        if (llPlayer.isKickSelected()) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }
        if (selectKillTimeOutFlag.get()) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        llPlayer.callKickSelected(type == 1);
        player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_SELECT_OK, null);
        PCLIPokerNtfLandLordKickResult ntf = new PCLIPokerNtfLandLordKickResult();
        ntf.playerUid = llPlayer.getUid();
        ntf.type = type;
        ntf.value = ((LandLordPlayer)llPlayer).isKick();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_RESULT, ntf);
        if (this.playerNum == 3 && this.prevIndex != -1) {
            if (llPlayer.isKick()) {
                if (allCallScoreNum == 2) {
                    boolean flag = false;
                    for (IRoomPlayer p : this.allPlayer) {
                        if (p != null && !p.isGuest() && p.getIndex() != this.bankerIndex && p.getIndex() != llPlayer.getIndex() &&!((LandLordPlayer)p).isKickSelected()) {
                            p.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK, null);
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        return;
                    }
                } else {
                    if (kickTogether){
                        for (IRoomPlayer p : this.allPlayer) {
                            if (p != null && !p.isGuest() && p.getIndex() != this.bankerIndex) {
                                ((LandLordPlayer) p).callKickSelected(true);
                                PCLIPokerNtfLandLordKickResult cc = new PCLIPokerNtfLandLordKickResult();
                                cc.playerUid = p.getUid();
                                cc.type = 1;
                                cc.value = ((LandLordPlayer) p).isKick();
                                this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_RESULT, cc);
                            }
                        }
                    }
                }
                
            } else {
                for (IRoomPlayer p : this.allPlayer) {
                    if (p != null && !p.isGuest() && p.getIndex() != this.bankerIndex && p.getIndex() != llPlayer.getIndex()&&!((LandLordPlayer)p).isKickSelected()) {
                        ((LandLordPlayer) p).callKickSelected(false);
                    }
                }
            }
        } else {
            if (kickSingle) {
                PCLIPokerNtfLandLordKickResult aa = new PCLIPokerNtfLandLordKickResult();
                aa.playerUid = player.getUid();
                aa.type = 1;
                aa.value = llPlayer.isKick();
                this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_RESULT, aa);
            } else if (kickTogether){
                if (llPlayer.isKick()) {
                    for (IRoomPlayer p : this.allPlayer) {
                        if (p != null && !p.isGuest() && p.getIndex() != this.bankerIndex) {
                            ((LandLordPlayer) p).callKickSelected(true);
                            PCLIPokerNtfLandLordKickResult nn = new PCLIPokerNtfLandLordKickResult();
                            nn.playerUid = p.getUid();
                            nn.type = 1;
                            nn.value = ((LandLordPlayer) p).isKick();
                            this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_RESULT, nn);
                        }
                    }
                }
            }
        }

       
        // 检测是否所有人都选择过踢
        boolean allSelected = true;
        for (IRoomPlayer p : this.allPlayer) {
            if (p != null && !p.isGuest() && p.getIndex() != this.bankerIndex && !((LandLordPlayer) p).isKickSelected()) {
                allSelected = false;
                break;
            }
        }

        // 是否都选择过踢
        if (allSelected) { 
            selectKillTimeOutFlag.set(true);
            if (kickBack) {
                IRoomPlayer roomPlayer = this.allPlayer[this.bankerIndex];
                if (roomPlayer != null) {
                    int tickfalg=0;
                    for (int i = 0; i < this.playerNum; ++i) {
                        IPokerPlayer p = (IPokerPlayer) this.allPlayer[i];
                        if (null == p || p.isGuest() || this.bankerIndex == p.getIndex()) {
                                continue;
                        }
                        LandLordPlayer tickplayer = (LandLordPlayer) p;
                         if(tickplayer.isKickSelected() && tickplayer.isKick()) {
                             tickfalg++;
                         }
                    }
                    
                    if (tickfalg > 0) {
                        // 通知开始选择回踢
                        roomPlayer.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK, null);
                        // 倒计时
                        LandLordRoom self = this;
                        DelayAction delayAction = new DelayAction(this, 30 * 1000);
                        delayAction.setCallback(args -> self.onKickBackSelectTimeout());
                        this.addAction(delayAction);
                    } else {
                        this.startTake();
                    }
                } else {
                    this.startTake();
                }
            } else {
                this.startTake();
            }
        }
    }

    /**
     * 回踢/不回踢的选择处理
     * @param player
     * @param type
     */
    public void kickBackSelect(Player player, int type) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法选择回踢", this, player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        if (!(kickBack)) {
            Logs.ROOM.warn("%s %s 房间未开启回踢, 无法选择回踢", this, player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        if (type != 0 && type != 1) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return;
        }
        IRoomPlayer roomPlayer = this.allPlayer[this.bankerIndex];
        if (null == roomPlayer) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return;
        }
        if (player.getUid() != roomPlayer.getUid()) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return;
        }

        LandLordPlayer llPlayer = (LandLordPlayer)roomPlayer;
        if (llPlayer.isKickBackSelected()) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_FAIL, ErrorCode.REQUEST_INVALID);
            return;
        }

        llPlayer.callKickBackSelected(type == 1);
        player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_BACK_SELECT_OK, null);

        PCLIPokerNtfLandLordKickResult ntf = new PCLIPokerNtfLandLordKickResult();
        ntf.playerUid = player.getUid();
        ntf.type = 2;
        ntf.value = llPlayer.isKickBack();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_RESULT, ntf);
        // 开始打牌
       
        //this.broadcast2Client(CommandId.CLI_NTF_POKER_CAN_TAKE, new PCLIPokerNtfCanTakeInfo());//通知打牌
        this.startTake();
    }
}