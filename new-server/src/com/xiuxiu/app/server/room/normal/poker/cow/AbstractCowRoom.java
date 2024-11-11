package com.xiuxiu.app.server.room.normal.poker.cow;

import com.alibaba.fastjson.JSONObject;
import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.*;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMyHandCardInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.action.cow.CowLordBankerAction;
import com.xiuxiu.app.server.room.normal.action.cow.CowReBetAction;
import com.xiuxiu.app.server.room.normal.action.cow.CowRobBankerAction;
import com.xiuxiu.app.server.room.normal.action.cow.CowTakeAction;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.RecordPokerPlayerBriefInfo;
import com.xiuxiu.app.server.room.record.poker.cow.CowResultRecordAction;
import com.xiuxiu.app.server.table.CowMultipleManager;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractCowRoom extends PokerRoom implements ICowRoom{

    /**
     * 等待客户端显示先发牌的表现时间
     */
    private static final long SHOW_CARD_TS = 2 * 1000;
    /**
     * 等待抢庄庄时间
     */
    private static final long ROB_BANK_TS = 7 * 1000;
    /**
     * 等待下注时间
     */
    private static final long REB_TS = 8 * 1000;
    /**
     * 快速时间
     */
    private static final int QUIKE_TS = 2 * 1000;

    /**
     * 等待开牌时间
     * 2020-08-22修改15->7
     */
    private static final int OPEN_CARD_TS = 7 * 1000;

    private CowInfo cowInfo;


    public AbstractCowRoom(RoomInfo roomInfo) {
        this(roomInfo, ERoomType.NORMAL);
    }

    public AbstractCowRoom(RoomInfo roomInfo, ERoomType roomType) {
        super(roomInfo, roomType);
    }

    @Override
    public void init() {
        super.init();
        this.cowInfo = new CowInfo();
        this.cowInfo.init(this.getRule());
        //this.detectionIP = 0 != (this.getRule().getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.RR_DETECTION_IP.getValue());
    }

    public CowInfo getCowInfo() {
        return cowInfo;
    }

    @Override
    public void replaceHandCard(IRoomPlayer player, int card) {
        if (!Config.checkWhiteHas(player.getUid(), 2)) {
            return;
        }
        if (this.isOver) {
            return;
        }
        byte c1 = (byte) (card >> 16);
        byte c2 = (byte) (card & 0xffff);
        if (-1 == this.allCard.indexOf(c2)) {
            return;
        }
        if (((IPokerPlayer) player).setHandCard(c1, c2)) {
            this.setPlayerPokerCardInfo((CowPlayer) player);
            this.allCard.remove((Byte) c2);

            if (((CowPlayer) player).getLastCard() == c1) {
                ((CowPlayer) player).setLastCard(c2);
            }
        }
    }

    @Override
    protected void doShuffle() {
        if (Switch.USE_CARD_LIB_POKER) {
            this.allCard.addAll(CardLibraryManager.I.getPokerCard());
            if (2 == this.cowInfo.getLaiType() || 3 == this.cowInfo.getLaiType()) {
                this.allCard.add(PokerUtil.KINGLET);
                this.allCard.add(PokerUtil.KING);
            }else if (4 == this.cowInfo.getLaiType()){
//                byte i = 3;
//                this.getCowInfo().setLaiZiCard(i);
                this.getCowInfo().setLaiZiCard(this.allCard.get(RandomUtil.random(this.allCard.size())));
            }
            return;
        }
        for (byte i = 0; i < 52; ++i) {
            if (this.getCowInfo().isWuHuaPai()) {
                int temp = PokerUtil.getCardValue(i);
                if (temp >= PokerUtil.J && temp <= PokerUtil.K) {
                    continue;
                }
                if (this.isColorPoker(PokerUtil.getCardValue(i))) {
                    continue;
                }
            }
            this.allCard.add(i);
        }
        if (2 == this.cowInfo.getLaiType() || 3 == this.cowInfo.getLaiType()) {
            this.allCard.add(PokerUtil.KINGLET);
            this.allCard.add(PokerUtil.KING);
        }else if (4 == this.cowInfo.getLaiType()){
            this.getCowInfo().setLaiZiCard(this.allCard.get(RandomUtil.random(this.allCard.size())));
//        	this.getCowInfo().setLaiZiCard((byte)5);
        }

        ShuffleUtil.shuffle(this.allCard);
        this.getCowInfo().addRoundCount();
        if (this.getCowInfo().getRoundCount() % 8 == 0) { // 每对局8就洗牌
            this.getCowInfo().setRoundCount(0);
        }
    }

    protected void doOnlyDeal(){
        //for (int i = 0; i < 5; ++i) {
            //int start = RandomUtil.random(this.playerNum);
        for (int j = 0; j < this.playerNum; ++j) {
            //int index = (start + j) % this.playerNum;
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            for (int i = 0; i < 5; ++i) {
                byte card;
                if (2 == this.getCowInfo().getLaiType()) { // 经典王癞
                    while (true) {
                        card = this.allCard.removeFirst();
                        if (i < 4 && (PokerUtil.KINGLET == card || PokerUtil.KING == card)) {
                            this.allCard.add(RandomUtil.random(0, this.allCard.size() - 1), card);
                        } else {
                            break;
                        }
                    }
                } else if (3 == this.getCowInfo().getLaiType()) {  // 疯狂王癞
                    card = this.allCard.removeFirst();
                } else if (4 == this.getCowInfo().getLaiType()){
//                	if(j==0) {
//                		if(i==0) {
//                			card = (byte)0;
//                		}else if(i==1) {
//                			card = (byte)2;
//                		}else if(i==2) {
//                			card = (byte)5;
//                		}else if(i==3) {
//                			card = (byte)20;
//                		}else{
//                			card = (byte)22;
//                		}
//                	}else {
                		card = this.allCard.removeFirst();
//                	}
                    // 保证每个人最多有两张赖子牌
                    if (PokerUtil.getCardValue(card) == PokerUtil.getCardValue(this.getCowInfo().getLaiZiCard())
                            && player.hasCardCnt(card,2)){
                        byte tempCard = card;
                        card = this.allCard.removeFirst();
                        if (PokerUtil.getCardValue(card) == PokerUtil.getCardValue(this.getCowInfo().getLaiZiCard())){
                            byte tempCardTwo = card;
                            card = this.allCard.removeFirst();
                            this.allCard.add(tempCardTwo);
                        }
                        this.allCard.add(tempCard);
                    }
                } else {    // 无王癞
                    card = this.allCard.removeFirst();
                }
                player.addHandCard(card);
                if (4 == i) {
                    ((CowPlayer) player).setLastCard(card);
                }
            }
        }
    }

    /**
     * 明牌抢庄发牌
     * playerNum=10(玩法房间最大玩家数)
     */
    @Override
    protected void doDeal() {
        this.doOnlyDeal();
        this.getCowInfo().setFirstIndex(Integer.MAX_VALUE);
        
        //好牌
        if(isDoDeal()) {
        	//玩家位置+牌型
        	Map<Integer, Byte> m = new ConcurrentHashMap<Integer, Byte>();
        	//牌型list
        	List<Byte> l = new ArrayList<Byte>();
        	for (int i = 0; i < this.playerNum; ++i) {
        		CowPlayer player = (CowPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                //斗公牛，明牌抢庄牌型判断返回
                JSONObject json = this.getPlayerPokerCardInfo(player);
                EPokerCardType cardType = (EPokerCardType) json.get("cardType");
                byte value = cardType.getValue();
                m.put(i, value);
                l.add(value);
        	}
        	//牌型list排序
        	l.sort(new Comparator<Byte>() {
        		@Override
    			public int compare(Byte o1, Byte o2) {
        			// 返回值为int类型，大于0表示正序，小于0表示逆序
                    return o2-o1;
    			}
			});
        	//所有申请的机器人
        	List<Integer> goodPlayer = this.getGoodPlayer();
        	for(int i=0;i<goodPlayer.size();i++) {
        		if(i>=l.size()) {
        			break;
        		}
        		//好牌高到底展示
        		byte cardType = l.get(i);
        		//换牌机器人位子
        		int weizi = goodPlayer.get(i);
        		if(m.get(weizi)==null) {
        			continue;
        		}
        		byte good = m.get(weizi);
        		for(Map.Entry<Integer, Byte> e : m.entrySet()) {
            		byte value = e.getValue();
            		//被换手牌玩家位子
            		int wz = e.getKey();
            		if(cardType==value) {
            			//机器人
            			IPokerPlayer player1 = (IPokerPlayer) this.allPlayer[weizi];
            			//临时存储机器人手牌
            			List<Byte> tmpList = new ArrayList<Byte>(player1.getHandCard());
            			//临时存储机器人最后一张
            			byte tmpCard = new Byte(((CowPlayer) player1).getLastCard());
            			//被换玩家
            			IPokerPlayer player2 = (IPokerPlayer) this.allPlayer[wz];
            			//清空机器人手牌
            			player1.getHandCard().clear();
            			//把被换玩家手牌放入机器人手牌
            			player1.getHandCard().addAll(player2.getHandCard());
            			//清空被换玩家手牌
            			player2.getHandCard().clear();
            			//把临时存储机器人手牌放入被换玩家手牌
            			player2.getHandCard().addAll(tmpList);
            			
            			((CowPlayer) player1).setLastCard(((CowPlayer) player2).getLastCard());
            			((CowPlayer) player2).setLastCard(tmpCard);
            			
            			byte tmp = new Byte(good);
            			good = value;
            			value = tmp;
            			m.put(weizi, good);
            			m.put(wz, value);
            			break;
            		}
            	}
        	}
        	//关闭
        	setDoDeal(false);
        	setGoodPlayer(null);
        }
        
//        if(true) {
//        	IPokerPlayer player1 = (IPokerPlayer) this.allPlayer[0];
//        	//清空机器人手牌
//			player1.getHandCard().clear();
//			//把被换玩家手牌放入机器人手牌
//			player1.getHandCard().add((byte) 0);
//			player1.getHandCard().add((byte) 2);
//			player1.getHandCard().add((byte) 5);
//			player1.getHandCard().add((byte) 20);
//			player1.getHandCard().add((byte) 22);
//        }
        
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            this.getRecord().addPlayer(new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getRoomPlayerHelper().getCurBureau()));
            if (this.getCowInfo().getFirstIndex() > i) { // 玩家加入顺序
                this.getCowInfo().setFirstIndex(i);
            }
        }
    }

    @Override
    protected void doStart1() {
        this.getCowInfo().setPrevBankerIndex(this.bankerIndex);
        if (ECowPlayTypes.ZY_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType()) {
            this.freedomBanker();
        } else if (ECowPlayTypes.HS_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType()) {
            this.sendFirstCard();
            this.fancyBanker();
        } else if (ECowPlayTypes.OVERLORD_BANKER.ordinal() == this.getCowInfo().getBankerType()) {
            this.overlordBanker();
        } else if (ECowPlayTypes.MP_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType()) {
            this.getCowInfo().setDealCardOkCnt((byte) 0);
            this.getCowInfo().setSendRobBanker(false);
            this.sendFirstCard();
        } else if (ECowPlayTypes.COMMON_PLAYING.ordinal() == this.getCowInfo().getBankerType()) {
            this.onRebet(); // 不用抢庄 自动开始下注；
        }
    }

    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.setLook(false);
            PCLIRoomNtfBeginInfoByCow beginInfoByCow = new PCLIRoomNtfBeginInfoByCow();
            beginInfoByCow.laiZiCard = this.getCowInfo().getLaiZiCard();
            beginInfoByCow.bureau = getBureau();
            beginInfoByCow.roomBriefInfo = this.getRoomBriefInfo(player);
            beginInfoByCow.roomBriefInfo.curBureau = this.getRoomBriefInfo(player).curBureau + 1;
            beginInfoByCow.d = Config.checkWhiteHas(player.getUid(), 2);
            beginInfoByCow.roundCount = this.getCowInfo().getRoundCount();
            for (int j = 0; j < this.playerNum; ++j) {
                IPokerPlayer tempPlayer = (IPokerPlayer) this.allPlayer[j];
                if (null == tempPlayer || tempPlayer.isGuest()) {
                    continue;
                }
                beginInfoByCow.pushNoteScore.put(tempPlayer.getUid(), this.getPushNoteScore((CowPlayer) tempPlayer, true));
            }
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, beginInfoByCow);
        }
        PCLIRoomNtfBeginInfoByCow beginInfoByCow = new PCLIRoomNtfBeginInfoByCow();
        beginInfoByCow.bureau = 0;
        beginInfoByCow.roomBriefInfo = this.getRoomBriefInfo();
        beginInfoByCow.roomBriefInfo.curBureau = 0;
        beginInfoByCow.roundCount = this.getCowInfo().getRoundCount();
        beginInfoByCow.laiZiCard = this.getCowInfo().getLaiZiCard();
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, beginInfoByCow);
        //this.sendMyCard();

        doSendWhiteInfo();
    }

    protected void doSendWhiteInfo(){
        for (int i = 0; i < this.playerNum; ++i) {
            IPokerPlayer player = (IPokerPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (Config.checkWhiteHas(player.getUid(), 2)) {
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

    @Override
    protected boolean checkAgain() {
        return super.checkAgain();
    }

    public boolean isCheckAgain() {
        return this.checkAgain();
    }

    @Override
    protected void gameOver(boolean next) {
//        if (!next && this.roomType != ERoomType.BOX && ((IBoxRoomHandle) this.getRoomHandle()).getBoxType() == EBoxType.ARENA) {
//            this.doHotDeskNote();
//        }

        this.getRoomHandle().calculateGold();//玩家竞技值计算

        this.record();
        this.getRecord().save();
    }

    /**
     * 明牌抢庄结算信息发送
     */
    @Override
    protected void doSendGameOver(boolean next) {
        PCLIPokerNtfCowGameOverInfo info = new PCLIPokerNtfCowGameOverInfo();
        info.next = next;
        List<PCLIPokerNtfCowGameOverInfo.GameOverInfo> overInfos = new ArrayList<>();
        for (int j = 0; j < this.playerNum; ++j) {
            CowPlayer player = (CowPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.setPrevCardType(player.getCurCardType());
            PCLIPokerNtfCowGameOverInfo.GameOverInfo gameOverInfo = new PCLIPokerNtfCowGameOverInfo.GameOverInfo();
            IPlayer iPlayer = player.getPlayer();
            if (null != iPlayer) {
                gameOverInfo.name = iPlayer.getName();
                gameOverInfo.icon = iPlayer.getIcon();
            }
            if (player.getResultCard() != null) {
                gameOverInfo.card.addAll(player.getResultCard());
            }
            gameOverInfo.handCard.addAll(player.getHandCard());
            gameOverInfo.score = this.getClientScore(player.getScore(Score.SCORE, false));
            gameOverInfo.scoreValue = player.getScore(Score.SCORE, false);
            //玩家自身的竞技值
            int m_total = 0;
            long formClubUid = this.getFromClubUid(player.getUid());
            IClub iClub = ClubManager.I.getClubByUid(formClubUid);
            if (iClub != null) {
                m_total = (int)iClub.getMemberExt(player.getUid(),true).getGold();
            }
            gameOverInfo.totalScore = this.getClientScore(m_total);
//            gameOverInfo.totalScore = this.getClientScore(player.getScore());
//            //当为庄并且是端火锅
//            if (this.bankerIndex == player.getIndex() && ECowPlayTypes.HOT_POT.ordinal() == this.getCowInfo().getBankerType()) {
//                gameOverInfo.totalScore = this.getClientScore(player.getScore() - this.getCowInfo().getCurHotDeskNote() * 10);
//            }
            gameOverInfo.cardType = player.getCurCardType().getValue();
            gameOverInfo.robBankerMul = player.getScore(Score.POKER_COW_ROB_BANKER_MUL, false);
            gameOverInfo.cardDouble = this.getMultiple(player.getCurCardType());
            gameOverInfo.playerUid = player.getUid();
            if (!player.getHandCard().isEmpty()) {
                gameOverInfo.lastCardValue = player.getLastCard();
            }
            if (!next) {
                PCLIPokerNtfCowGameOverInfo.TotalCnt totalCnt = new PCLIPokerNtfCowGameOverInfo.TotalCnt();
                totalCnt.maxScore = player.getScore(Score.ACC_MAX_SCORE, true) / 100;
                totalCnt.maxCardType = player.getScore(Score.ACC_POKER_MAX_CARD_TYPE, true);
                totalCnt.winCnt = player.getScore(Score.ACC_WIN_CNT, true);
                totalCnt.lostCnt = player.getScore(Score.ACC_LOST_CNT, true);
                gameOverInfo.totalCnt = totalCnt;
            }
            info.allGameOverInfo.put(player.getUid(), gameOverInfo);
            overInfos.add(gameOverInfo);
        }
        if (ECowPlayTypes.HOT_POT.ordinal() == this.getCowInfo().getBankerType()) {// 端火锅
            info.hotDeskNote = this.getCowInfo().getCurHotDeskNote();
        } else {
            info.hotDeskNote = -1;
        }
        // 积分排序 第一 第二。。。
        overInfos.sort((o1, o2) -> o2.scoreValue - o1.scoreValue);
        for (PCLIPokerNtfCowGameOverInfo.GameOverInfo gInfo : overInfos) {
            info.sortScorePlayerUidList.add(gInfo.playerUid);
        }
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, info);
    }

    @Override
    public void syncDeskInfo(IPlayer iPlayer) {
        CowPlayer player = (CowPlayer) this.getRoomPlayer(iPlayer.getUid());
        if (null != player || this.watchList.contains(iPlayer.getUid())) {
            PCLIPokerNtfCowDeskInfo deskInfo = new PCLIPokerNtfCowDeskInfo();
            deskInfo.laiZiCard = this.getCowInfo().getLaiZiCard();
            deskInfo.roomInfo = this.getRoomInfo();
            if (this.watchList.contains(iPlayer.getUid())) {
                deskInfo.roomInfo.roomBriefInfo.curBureau = 0;
            }
            deskInfo.curBureau = null == player ? 0 : player.getRoomPlayerHelper().getCurBureau();
            deskInfo.sendCardCount = this.getCowInfo().getSendCardCount();
            deskInfo.gameing = this.roomState.get() == ERoomState.START;
            deskInfo.bankerIndex = this.bankerIndex;
            deskInfo.hotBankerLoop = this.getCowInfo().getCurHotBankerLoop();
            deskInfo.hotDeskNote = this.getCowInfo().getCurHotDeskNote();
            deskInfo.keepCount = this.getCowInfo().getKeepCount();
            deskInfo.curPhase = this.getCurPhase();
            deskInfo.totalLoop = this.getCowInfo().getTotalLoop();
            deskInfo.curBankCnt = this.bankerIndex +1;
            deskInfo.readyTime = ((IBoxRoomHandle)this.getRoomHandle()).getReadyTime();
            if (null != player && !player.isGuest()) {
                if (1 == deskInfo.sendCardCount) {
                    if (player.getHandCard().size() >= this.getCowInfo().getCardNum()) {
                        deskInfo.card.addAll(player.getHandCard().subList(0, this.getCowInfo().getCardNum()));
                    }else {
                        deskInfo.card.addAll(player.getHandCard());
                    }
                } else if (2 == deskInfo.sendCardCount){
                    deskInfo.card.addAll(player.getHandCard());
                }else {
                    deskInfo.card.addAll(player.getResultCard());
                }
                deskInfo.firstShowCard.addAll(player.getFirstShowCard());
            }

            if (!this.action.isEmpty() && (this.action.peek() instanceof CowRobBankerAction)) {
                deskInfo.bankerIndex = -1;
            }
            for (int i = 0; i < this.playerNum; ++i) {
                CowPlayer temp = (CowPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                if (temp.isLook()) {
                    deskInfo.lookCardPlayers.add(temp.getUid());
                }
                deskInfo.allScore.put(temp.getUid(), this.getClientScore(temp.getScore()));
                deskInfo.allOnlineState.put(temp.getUid(), !temp.isOffline());
                deskInfo.allRebet.put(temp.getUid(), temp.getScore(Score.POKER_COW_REBET, false));
                deskInfo.allRobBank.put(temp.getUid(), temp.getScore(Score.POKER_COW_ROB_BANKER_MUL, false));
                deskInfo.isLookCard.put(temp.getUid(), this.isLookCard(temp.getUid()));
                deskInfo.pushNoteScore.put(temp.getUid(), this.getPushNoteScore(temp, false));
            }
            iPlayer.send(CommandId.CLI_NTF_ROOM_DESK_INFO, deskInfo);
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.getCowInfo().setDealCardOkCnt((byte) 0);
        this.getCowInfo().setSendRobBanker(false);
        this.getCowInfo().setSendCardCount(-1);
        this.bankerIndex = -1;
    }

    @Override
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 * value / 10;
    }

    public void onBankerToPlayer(long bankerplayerID) {
        this.bankerIndex = this.getRoomPlayer(bankerplayerID).getIndex();
    }

    private void freedomBanker() {  // 开始抢庄
        CowRobBankerAction action = new CowRobBankerAction(this, ROB_BANK_TS);
        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer player = this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addRobBanker(player.getUid());
        }
        this.addAction(action);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_BEGIN, null);
    }

    private void sendFirstCard() {   // 发第一手牌
        if (-1 == this.bankerIndex) {
            this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            this.setBankerIndex();
        }
        sendFirstCardToClient();
        if (ECowPlayTypes.MP_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType()) {// 明牌开始抢庄；
            //int delayTime = 2500 + 300 * this.curPlayerCnt;
//            if (this.getCowInfo().getRoundCount() == 0) {   // 8回合洗牌时间
//                delayTime += 2500;
//            }
            DelayAction action = new DelayAction(this, SHOW_CARD_TS);
            final ICowRoom self = this;
            action.setCallback(o -> self.showCardRobBanker());
            this.addAction(action);
        }
//        // 端火锅 在发完第一手牌后开始下注；
//        if (ECowPlayTypes.HOT_POT.ordinal() == this.getCowInfo().getBankerType()) {
//            this.onRebet();
//        }
    }

    protected void sendFirstCardToClient(){
        int sendCardNum = this.getCowInfo().getCardNum();
        if (sendCardNum != 0) {
            this.getCowInfo().setSendCardCount(1);
            for (int j = 0; j < this.playerNum; ++j) {
                CowPlayer player = (CowPlayer) this.allPlayer[(j + this.bankerIndex) % this.playerNum];
                if (null == player || player.isGuest()) {
                    continue;
                }
                PCLIPokerNtfCowHandCardInfo info = new PCLIPokerNtfCowHandCardInfo();
                info.handCard.addAll(player.getHandCard().subList(0, this.getCowInfo().getCardNum()));
                player.setFirstShowCard(info.handCard);
                info.sendCardCount = 1;
                player.send(CommandId.CLI_NTF_POKER_COW_DEAL_CARD, info);
            }
            PCLIPokerNtfCowHandCardInfo info = new PCLIPokerNtfCowHandCardInfo();
            info.sendCardCount = 1;
            this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_COW_DEAL_CARD, info);
        }
    }

    protected void sendLastCards() { // 发最后一手牌
        int sendCardNum = 5 - this.getCowInfo().getCardNum();
        if (sendCardNum != 0) {
            this.getCowInfo().setSendCardCount(2);
            for (int j = 0; j < this.playerNum; ++j) {
                CowPlayer player = (CowPlayer) this.allPlayer[(j + this.bankerIndex) % this.playerNum];
                if (null == player || player.isGuest()) {
                    continue;
                }
                try {
                    if (this.getGameSubType() != 1) {
                        int currentHandCardSize = player.getHandCard().size();
                        for (int i = currentHandCardSize; i < 5; ++i) {
                            byte card;
                            if (2 == this.getCowInfo().getLaiType()) { // 经典王癞
                                while (true) {
                                    card = this.allCard.removeFirst();
                                    if (i < 4 && (PokerUtil.KINGLET == card || PokerUtil.KING == card)) {
                                        this.allCard.add(RandomUtil.random(0, this.allCard.size() - 1), card);
                                    } else {
                                        break;
                                    }
                                }
                            } else if (3 == this.getCowInfo().getLaiType()) {  // 疯狂王癞
                                card = this.allCard.removeFirst();
                            } else {    // 无王癞
                                card = this.allCard.removeFirst();
                            }
                            player.addHandCard(card);
                        }
                    }
                    PCLIPokerNtfCowHandCardInfo info = new PCLIPokerNtfCowHandCardInfo();
                    info.handCard.addAll(player.getHandCard().subList(this.getCowInfo().getCardNum(), 5));
                    //Logs.API.error("yyyyyyyy " + "最后一手牌"+player.getUid() + player.getHandCard());
                    info.sendCardCount = 2;
                    player.send(CommandId.CLI_NTF_POKER_COW_DEAL_CARD, info);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }

            PCLIPokerNtfCowHandCardInfo info = new PCLIPokerNtfCowHandCardInfo();
            info.sendCardCount = 2;
            this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_COW_DEAL_CARD, info);
        }
//        int timeout = 8000 + this.curPlayerCnt * 300;// 6000 + (150 * 5 *
//        if (this.getCowInfo().isQuick()) {
//            // 快速
//            //timeout -= 3000;
//        }
        this.setPlayerPokerCardInfo();
        this.setSpellTenTakeAction(0);
    }

    private void fancyBanker() { // 花式抢庄
        if (-1 == this.bankerIndex) {
            this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            this.setBankerIndex();
        }
        if (1 == this.getCowInfo().getPushBankerType()) { // 房主坐庄
            if (this.roomType == ERoomType.BOX && this.curBureau == 1) {
                this.startLordBanker();
                return;
            } else {
                if (-1 == this.bankerIndex) {
                    long playerUid = this.info.getOwnerPlayerUid();
                    if (playerUid == -1) {
                        this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
                        this.setBankerIndex();
                    } else {
                        this.bankerIndex = this.getRoomPlayer(this.info.getOwnerPlayerUid()).getIndex();
                    }
                }
            }
        } else if (2 == this.getCowInfo().getPushBankerType()) {  // 轮流
            do {
                this.bankerIndex = (++this.bankerIndex) % this.playerNum;
            } while (null == (this.allPlayer[this.bankerIndex]) || this.allPlayer[this.bankerIndex].isGuest());
        } else if (3 == this.getCowInfo().getPushBankerType()) {  // 3： 连庄玩法 （不勾选子类）
            this.bankerIndex = this.getCowInfo().getPrevBankerIndex();
        } else if (4 == this.getCowInfo().getPushBankerType()) {   // 牌大
            if (-1 != this.getCowInfo().getPrevMaxCardPlayerIndex()) {
                this.bankerIndex = this.getCowInfo().getPrevMaxCardPlayerIndex();
            }
        } else if (5 == this.getCowInfo().getPushBankerType()) {// 5 ：连庄 （只勾选 没牛下庄）
            if (this.getCowInfo().isPreBankerHas()) {
                this.bankerIndex = this.getCowInfo().getPrevBankerIndex();
            } else {
                do {
                    this.bankerIndex = (++this.bankerIndex) % this.playerNum;
                } while (null == (this.allPlayer[this.bankerIndex]) || this.allPlayer[this.bankerIndex].isGuest());
            }
        } else if (6 == this.getCowInfo().getPushBankerType()) {// 6 连庄（只勾选牛牛上庄 ）
            if (-1 != this.getCowInfo().getPrevMaxPlayerIndex()) {
                this.bankerIndex = this.getCowInfo().getPrevMaxPlayerIndex();
            } else {
                this.bankerIndex = this.getCowInfo().getPrevBankerIndex();
            }
        } else if (8 == this.getCowInfo().getPushBankerType()) {  // 8 ： 连庄（都勾选了）
            if (-1 != this.getCowInfo().getPrevMaxPlayerIndex()) {
                this.bankerIndex = this.getCowInfo().getPrevMaxPlayerIndex();
            } else {
                this.bankerIndex = (this.getCowInfo().addPrevBankerIndex()) % this.playerNum;
            }
        }
        while (null == (this.allPlayer[this.bankerIndex]) || this.allPlayer[this.bankerIndex].isGuest()) {
            this.bankerIndex = (++this.bankerIndex) % this.playerNum;
        }
        PCLIPokerNtfCowRobBankerResultInfo info = new PCLIPokerNtfCowRobBankerResultInfo();
        info.bankerPlayerUid = this.allPlayer[this.bankerIndex].getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_RESULT, info);
        this.onRebet();
    }



    private void overlordBanker() {
        if (this.roomType == ERoomType.BOX && this.curBureau == 1) {
            this.startLordBanker();
        } else {
            if (-1 == this.bankerIndex) {
                long playerUid = this.info.getOwnerPlayerUid();
                if (playerUid == -1) {
                    this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
                } else {
                    this.bankerIndex = this.getRoomPlayer(this.info.getOwnerPlayerUid()).getIndex();
                }
            }
            this.setBankerIndex();
            PCLIPokerNtfCowRobBankerResultInfo info = new PCLIPokerNtfCowRobBankerResultInfo();
            info.bankerPlayerUid = this.allPlayer[this.bankerIndex].getUid();
            this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_RESULT, info);
            this.sendFirstCard();
        }
    }

    public void onRebet() {
        if (-1 == this.bankerIndex) {
            this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            this.setBankerIndex();
        }
//        if (ECowPlayTypes.HOT_POT.ordinal() == this.getCowInfo().getBankerType()) {// 端火锅；
//            ((PokerRecord) this.getRecord()).addBankerRecordAction(this.allPlayer[this.bankerIndex].getUid(), this.getCowInfo().getCurHotBankerLoop(),
//                    this.getCowInfo().getCurHotDeskNote());
//        } else if (ECowPlayTypes.COMMON_PLAYING.ordinal() != this.getCowInfo().getBankerType()) {// 有庄记录
//            ((PokerRecord) this.getRecord()).addBankerRecordAction(this.allPlayer[this.bankerIndex].getUid());
//        }
        if (ECowPlayTypes.COMMON_PLAYING.ordinal() != this.getCowInfo().getBankerType()) {// 有庄记录
            ((PokerRecord) this.getRecord()).addBankerRecordAction(this.allPlayer[this.bankerIndex].getUid());
        }
        // 下注
        CowReBetAction action = new CowReBetAction(this, REB_TS);
        this.getCowInfo().setMaxPushNote((this.getCowInfo().getPushNoteType() == 3) ? 0 : this.getCowInfo().getMaxPushNote());
        action.setBase(this.getCowInfo().getBaseMinRebet());
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (i == this.bankerIndex && ECowPlayTypes.COMMON_PLAYING.ordinal() != this.getCowInfo().getBankerType()) {// 不是通比玩法；
                continue;
            }
            int score = this.getPushNoteScore(player, false);
            boolean isPushNote = score > 0;

            boolean candoubling = false; // 是否可以下注翻倍；
            int bankMul = player.getScore(Score.POKER_COW_ROB_BANKER_MUL, false);
            if (this.getCowInfo().isDoubling() && bankMul >= this.getCowInfo().getRobBankerMul()) {
                candoubling = true;
            }

            action.addPushNote(player.getUid(), score);
            action.setDoubling(player.getUid(), candoubling);
            player.setCurPushNote(isPushNote);

            PCLIPokerNtfCowReBetBeginInfo info = new PCLIPokerNtfCowReBetBeginInfo();
            info.doubling = candoubling;
            info.baseRebet = this.getCowInfo().getBaseMinRebet();
            info.pushNote = score;
            info.isPushNote = isPushNote;
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_BEGIN, info);
        }
        this.addAction(action);
    }
    /**
     *检查下注值是否合法
     * @return
     */
    public boolean checkRebValue(IPokerPlayer pokerPlayer, int rebValue,int pushNoteValue,boolean isDoubling){
        return false;
    }

    public void setMaxRobBanker(Long[] maxRobBanker, int max, boolean darkRob) {
        this.bankerIndex = this.getRoomPlayer(maxRobBanker[RandomUtil.random(0, max - 1)]).getIndex();
        PCLIPokerNtfCowRobBankerResultInfo info = new PCLIPokerNtfCowRobBankerResultInfo();
        if (darkRob) {
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player = this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                info.allRobBankerInfo.put(player.getUid(), player.getScore(Score.POKER_COW_ROB_BANKER_MUL, false));
            }
        }
        info.bankerPlayerUid = this.allPlayer[this.bankerIndex].getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_RESULT, info);
    }

    public void onDealCard() {
        if (ECowPlayTypes.MP_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType() || ECowPlayTypes.HS_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType()) {
            // 明牌抢庄玩法 花式 端火锅；
            this.sendLastCards();
            return;
        }
        this.getCowInfo().setSendCardCount(2);
//        int timeout = 8000 + this.curPlayerCnt * 300;// + (150 * 5 *
//        if (this.getCowInfo().getRoundCount() == 0) {
//            timeout += 2500;
//        }
//        if (this.getCowInfo().isQuick()) { // 快速
//            //timeout -= 3000;
//        }
        for (int j = 0; j < this.playerNum; ++j) {
            CowPlayer player = (CowPlayer) this.allPlayer[(j + this.bankerIndex) % this.playerNum];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIPokerNtfCowHandCardInfo info = new PCLIPokerNtfCowHandCardInfo();
            info.handCard.addAll(player.getHandCard());
            player.send(CommandId.CLI_NTF_POKER_COW_DEAL_CARD, info);
        }
        PCLIPokerNtfCowHandCardInfo info = new PCLIPokerNtfCowHandCardInfo();
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_COW_DEAL_CARD, info);
        this.setPlayerPokerCardInfo();
        this.setSpellTenTakeAction(0);
    }

    public void sendLordBankerResult(long playerUid) {
        if (playerUid == 0) {
            this.bankerIndex = this.getRoomPlayer(this.getCowInfo().getFirstIndex()).getIndex();
        } else {
            this.bankerIndex = this.getRoomPlayer(playerUid).getIndex();
        }
        PCLIPokerNtfCowRobBankerResultInfo info = new PCLIPokerNtfCowRobBankerResultInfo();
        info.bankerPlayerUid = this.allPlayer[this.bankerIndex].getUid();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_RESULT, info);
        this.sendFirstCard();
    }

    public void setNextLordBaner(long playerUid) {
        int index = this.getRoomPlayer(playerUid).getIndex();
        long nextPlayerUid = this.getNextRoomPlayer(index).getUid();
        IAction action = this.action.peek();
        if (action instanceof CowLordBankerAction) {
            ((CowLordBankerAction) action).resetTimeout(this.getCowInfo().isQuick() ? ROB_BANK_TS - QUIKE_TS : ROB_BANK_TS);
            ((CowLordBankerAction) action).startSelectBanker(nextPlayerUid);
        }
    }

    /**
     * 明牌抢庄结算
     */
    public void onOver() {
    	//上一把最大牛牛牌型的玩家index
        this.getCowInfo().setPrevMaxPlayerIndex(-1);
        //上一把最大牌型的玩家index
        this.getCowInfo().setPrevMaxCardPlayerIndex(-1);
        //上一把庄家是否有牛
        this.getCowInfo().setPreBankerHas(Boolean.FALSE);
        this.isOver = true;
        //是不是通比玩法
        if (ECowPlayTypes.COMMON_PLAYING.ordinal() == this.getCowInfo().getBankerType()) {
        	//结算分 通比玩法的每个玩家相互比较算分
        	this.onAllCompare();
        }  else {
        	//结算分
            this.onBankCompare();
        }
        this.gameOver(this.checkAgain());
        this.stop();
    }

    @Override
    public int getExchangeGoldForScore(long gold) {
        return (int) gold;
    }

    @Override
    public int getCurPhase() {
        return 0;
    }

    private void onAllCompare() {
        CowResultRecordAction action = ((PokerRecord) this.getRecord()).addCowResultRecordAction();
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            for (int j = i; j < this.playerNum; ++j) {
                CowPlayer tempPlayer = (CowPlayer) this.allPlayer[j];
                if (null == tempPlayer || tempPlayer.isGuest() || player.getUid() == tempPlayer.getUid()) {
                    continue;
                }
                int rebet = this.getRebetScore(player);
                boolean bankerWin = this.comparePlayer(tempPlayer, player);
                EPokerCardType winCardType = bankerWin ? tempPlayer.getCurCardType() : player.getCurCardType();
                int multiple = this.getMultiple(winCardType);
                tempPlayer.addRebet(rebet * multiple * (bankerWin ? 1 : -1), false);
                player.addRebet(rebet * multiple * (bankerWin ? -1 : 1), true);
                if (player.getScore(Score.SCORE, false) > 0) {
                    player.addScore(Score.ACC_WIN_CNT, 1, true);
                } else if (player.getScore(Score.SCORE, false) < 0) {
                    player.addScore(Score.ACC_LOST_CNT, 1, true);
                }
                player.maxScore(Score.ACC_MAX_SCORE, player.getScore(Score.SCORE, false), true);
            }
            for (int j = 0; j < this.playerNum; ++j) {
                CowPlayer sPlayer = (CowPlayer) this.allPlayer[j];
                if (null == sPlayer || sPlayer.isGuest()) {
                    continue;
                }
                action.addResult(sPlayer.getUid(), sPlayer.getResultCard().size() == 0 ? sPlayer.getHandCard() : sPlayer.getResultCard(),
                        this.getClientScore(sPlayer.getScore(Score.SCORE, false)),
                        this.getClientScore(sPlayer.getScore() + sPlayer.getScore(Score.SCORE, false)),
                        sPlayer.getCurCardType().getValue());
            }
        }
    }

    private void onBankCompare() {
    	//通过庄家索引在allPlayer获取到庄家
        CowPlayer bankerPlayer = (CowPlayer) this.allPlayer[this.bankerIndex];
        CowResultRecordAction action = ((PokerRecord) this.getRecord()).addCowResultRecordAction();
        //上一把最大牌型的玩家index
        this.getCowInfo().setPrevMaxCardPlayerIndex(this.bankerIndex);
        //明牌抢庄算分
        this.setPlayerScore(bankerPlayer, action);
        int bankerScore = bankerPlayer.getScore(Score.SCORE, false);
        if (bankerScore > 0) {
        	//庄赢次数+1
            bankerPlayer.addScore(Score.ACC_WIN_CNT, 1, true);
        } else if (bankerScore < 0) {
        	//庄输次数+1
            bankerPlayer.addScore(Score.ACC_LOST_CNT, 1, true);
        }
        bankerPlayer.maxScore(Score.ACC_MAX_SCORE, bankerScore, true);
        //如果庄家牌型有牛
        if (bankerPlayer.getCurCardType().getValue() >= EPokerCardType.COW_1.getValue()) {
        	//上一把庄家是否有牛->有牛
            this.getCowInfo().setPreBankerHas(Boolean.TRUE);
            if (this.getCowInfo().getPrevMaxPlayerIndex() == -1) {
                this.getCowInfo().setPrevMaxPlayerIndex(this.bankerIndex);
            }
        }
        if (this.getCowInfo().isPreBankerHas() && this.bankerIndex != this.getCowInfo().getPrevMaxPlayerIndex()) {
            if (this.comparePlayer(bankerPlayer, (CowPlayer) this.allPlayer[this.getCowInfo().getPrevMaxPlayerIndex()])) {
                this.getCowInfo().setPrevMaxPlayerIndex(this.bankerIndex);
            }
        }
        action.addResult(bankerPlayer.getUid(),
                bankerPlayer.getResultCard().size() == 0 ? bankerPlayer.getHandCard() : bankerPlayer.getResultCard(),
                this.getClientScore(bankerPlayer.getScore(Score.SCORE, false)),
                this.getClientScore(bankerPlayer.getScore() + bankerPlayer.getScore(Score.SCORE, false)),
                bankerPlayer.getCurCardType().getValue());
    }

    private boolean isColorPoker(byte cardNum) {
        return cardNum == PokerUtil._J || cardNum == PokerUtil._Q || cardNum == PokerUtil._K;
    }

    private void setBankerIndex() {
        int count = 0;
        while ((null == (this.allPlayer[this.bankerIndex]) || this.allPlayer[this.bankerIndex].isGuest()) && count <= 16) {
            count++;
            this.bankerIndex = (++this.bankerIndex) % this.playerNum;
        }
    }

    public void showCardRobBanker() {
        if (ECowPlayTypes.MP_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType() && !this.getCowInfo().isSendRobBanker()) {
            this.freedomBanker();
            this.getCowInfo().setSendRobBanker(Boolean.TRUE);
        }
    }

    private void startLordBanker() {
        CowLordBankerAction action = new CowLordBankerAction(this, this.getCowInfo().isQuick() ? ROB_BANK_TS - QUIKE_TS : ROB_BANK_TS);// 5000
        action.setOwnPlayerUid(this.allPlayer[this.getCowInfo().getFirstIndex()].getUid());
        for (int i = 0; i < this.playerNum; i++) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addLordSelectBanker(player.getUid());
        }
        action.startSelectBanker(this.getRoomPlayer(this.getCowInfo().getFirstIndex()).getUid());
        this.addAction(action);
    }

    protected int getPushNoteScore(CowPlayer player, boolean force) {
        int score = 0;
        if (this.getCowInfo().getPushNoteType() == 1) { // 抢庄推注
            if (player.getScore(Score.POKER_COW_ROB_BANKER_MUL, false) >= this.getCowInfo().getRobBankerMul()) {
                score = this.getCowInfo().getMaxPushNote();
            }
        } else if (this.getCowInfo().getPushNoteType() == 2) { // 闲家推注
            if (!this.getCowInfo().isNotRobNotPush() || player.getScore(Score.POKER_COW_ROB_BANKER_MUL, false) >= 1) {
                if ((player.getIndex() != (force ? this.bankerIndex : this.getCowInfo().getPrevBankerIndex())) && player.isPrevWin()) {
                    score = player.getPrevWinValue() / 100;
                    if (score > this.getCowInfo().getMaxPushNote()) {
                        score = this.getCowInfo().getMaxPushNote();
                    }
                    if (score <= 0 || player.isPrePushNote()) {
                        score = 0;
                    }
                }
            }
        } else if (this.getCowInfo().getPushNoteType() == 4 && this.roomType == ERoomType.BOX && ((IBoxRoomHandle) this.getRoomHandle()).getBoxType() == EBoxType.ARENA) { // 闲家推注；经典
            if (ECowPlayTypes.MP_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType() || ECowPlayTypes.ZY_ROB_BANKER.ordinal() == this.getCowInfo().getBankerType()) {
                if (!this.getCowInfo().isNotRobNotPush() || player.getScore(Score.POKER_COW_ROB_BANKER_MUL, false) >= this.getCowInfo().getRobBankerMul()) {
                    if ((player.getIndex() != (force ? this.bankerIndex : this.getCowInfo().getPrevBankerIndex())) && player.isPrevWin()) {
                        if (player.getPrevCardType().getValue() >= EPokerCardType.COW_10.getValue()) {
                            score = this.getCowInfo().getBaseMinRebet() * 10;
                        } else if (player.getPrevCardType().getValue() >= EPokerCardType.COW_7.getValue()) {
                            score = this.getCowInfo().getBaseMinRebet() * 8;
                        } else if (player.getPrevCardType().getValue() >= EPokerCardType.COW_4.getValue()) {
                            score = this.getCowInfo().getBaseMinRebet() * 4;
                        } else {
                            score = this.getCowInfo().getBaseMinRebet() * 3;
                        }
                        if (score <= 0 || player.isPrePushNote()) {
                            score = 0;
                        }
                    }
                }
            }
        }
        if (this.getCowInfo().getMaxPushNote() < 1) {
            score = 0;
        }
        return score;
    }

    protected int getRebetScore(CowPlayer player) {
        return this.getScore(player.getScore(Score.POKER_COW_REBET, false));
    }

    /**
     * 明牌抢庄 庄闲牌型比较
     * @param bankerPlayer 庄家
     * @param player 闲家
     * @return
     */
    public boolean comparePlayer(CowPlayer bankerPlayer, CowPlayer player) {
    	//庄家当前牌型
        EPokerCardType bankerCardType = bankerPlayer.getCurCardType();
        //闲家当前牌型
        EPokerCardType cardType = player.getCurCardType();
        if (bankerCardType.getValue() == cardType.getValue()) {
        	//庄闲牌型值比较
            if (bankerPlayer.getCardValue() == player.getCardValue()) {
            	//庄家最大牌
                byte bankerMaxCard = this.getMaxCard(bankerPlayer);
                //闲家最大牌
                byte maxCard = this.getMaxCard(player);
                //庄家最大牌值
                byte bankerMaxCardValue = PokerUtil.getCardValueByCow2(bankerMaxCard);
                //闲家最大牌值
                byte maxCardValue = PokerUtil.getCardValueByCow2(maxCard);
                //庄闲最大牌值比较
                if (bankerMaxCardValue == maxCardValue) {
                	//庄闲最大牌值花色比较
                    return PokerUtil.getCardColor(bankerMaxCard) > PokerUtil.getCardColor(maxCard);
                }
                return bankerMaxCardValue > maxCardValue;
            } else {
                return bankerPlayer.getCardValue() > player.getCardValue();
            }
        }
        return bankerCardType.getValue() > cardType.getValue();
    }

    private byte getMaxCard(CowPlayer player) {
        byte maxCard = 0;
        for (int i = 0; i < player.getHandCard().size(); i++) {
            byte temp = player.getHandCard().get(i);
            if (PokerUtil.getCardValueByCow2(temp) > PokerUtil.getCardValueByCow2(maxCard)) {
                maxCard = temp;
            } else if (PokerUtil.getCardValueByCow2(temp) == PokerUtil.getCardValueByCow2(maxCard)) {
                if (PokerUtil.getCardColor(temp) > PokerUtil.getCardColor(maxCard)) {
                    maxCard = temp;
                }
            }
        }
        return maxCard;
    }

    public int getMultiple(EPokerCardType type) {
        int multiple = 1;
        //牛-同花顺31-传入牌型的牌型值+1
        int temp1 = EPokerCardType.COW_WITH_THE_FLOWER.getValue() - type.getValue() + 1;
        //小于10说明传入牌型牛牛以上
        if (temp1 < 10) {
        	//翻倍规则
            multiple = CowMultipleManager.I.getMultiple(this.getCowInfo().getMultiple(), temp1);
        } else {
            if (1 == this.getCowInfo().getMultiple()) {
                if (EPokerCardType.COW_10 == type) {
                    multiple = 3;
                } else if (EPokerCardType.COW_8 == type || EPokerCardType.COW_9 == type) {
                    multiple = 2;
                }
            } else if (2 == this.getCowInfo().getMultiple()) {
                if (EPokerCardType.COW_10 == type) {
                    multiple = 3;
                } else if (EPokerCardType.COW_7 == type || EPokerCardType.COW_8 == type
                        || EPokerCardType.COW_9 == type) {
                    multiple = 2;
                }
            } else if (3 == this.getCowInfo().getMultiple()) {
                if (EPokerCardType.COW_10 == type) {
                    multiple = 4;
                } else if (EPokerCardType.COW_7 == type || EPokerCardType.COW_8 == type) {
                    multiple = 2;
                } else if (EPokerCardType.COW_9 == type) {
                    multiple = 3;
                }
            } else if (4 == this.getCowInfo().getMultiple()) {
                if (EPokerCardType.COW_NONE != type) {
                    multiple = type.getValue() - EPokerCardType.COW_NONE.getValue();
                }
            }
        }
        return multiple;
    }

    /**
     * 明牌抢庄算分
     * @param bankerPlayer 庄家
     * @param action
     */
    private void setPlayerScore(CowPlayer bankerPlayer, CowResultRecordAction action) {
        List<CowPlayer> playerList = this.getPlayerSortList();
        int score = bankerPlayer.getScore(Score.POKER_COW_ROB_BANKER_MUL, false);
        int bankerRobMultiple = score > 1 ? score : 1;
        //上一把最大牌型的玩家index
        int prevMaxCardPlayerIndex = this.getCowInfo().getPrevMaxCardPlayerIndex();
        //上一把最大牌型的玩家
        CowPlayer cowPlayer = (CowPlayer) this.allPlayer[prevMaxCardPlayerIndex];
        CowPlayer tempPlayer = prevMaxCardPlayerIndex==-1 || cowPlayer==null ? bankerPlayer:cowPlayer;
        for (CowPlayer player : playerList) {
            if (null == player) {
                continue;
            }
            if (player.getUid() == bankerPlayer.getUid()) {
                continue;
            }
            if (this.comparePlayer(player, tempPlayer)) {
                this.getCowInfo().setPrevMaxCardPlayerIndex(player.getIndex());
            }
            //如果有牛
            if (player.getCurCardType().getValue() >= EPokerCardType.COW_1.getValue()) {
                if (-1 == this.getCowInfo().getPrevMaxPlayerIndex()
                        || this.comparePlayer(player, (CowPlayer)this.allPlayer[this.getCowInfo().getPrevMaxPlayerIndex()])) {
                    this.getCowInfo().setPrevMaxPlayerIndex(player.getIndex());
                }
            }
            //下的注
            int rebet = this.getRebetScore(player);
            //庄闲输赢 true庄赢 false闲赢
            boolean bankerWin = this.comparePlayer(bankerPlayer, player);
            player.setPrevWin(!bankerWin);
            //赢家牌型
            EPokerCardType winCardType = bankerWin ? bankerPlayer.getCurCardType() : player.getCurCardType();
            //获取赢家牌型倍数
            int multiple = this.getMultiple(winCardType);
            //底分*下注值*牌型倍数=输赢分
            int rebetNum = bankerRobMultiple * rebet * multiple * (bankerWin ? 1 : -1);
//            if (ECowPlayTypes.HOT_POT.ordinal() == this.getCowInfo().getBankerType()) {
//                if (rebetNum > 0) {// 庄家赢的分数；
//                    this.getCowInfo().setCurHotDeskNote(this.getCowInfo().getCurHotDeskNote() + rebetNum * 10 / 100);
//                    player.addRebet((rebetNum * -1), false);
//                } else {// 庄家输的分数；
//                    if (this.getCowInfo().getCurHotDeskNote() > (rebetNum * 10 / 100 * -1)) {
//                        this.getCowInfo().setCurHotDeskNote(this.getCowInfo().getCurHotDeskNote() - (rebetNum * 10 / 100 * -1));
//                        player.addRebet((rebetNum * -1), false);
//                        player.maxScore(Score.ACC_MAX_SCORE, rebetNum * -1, true);
//                    } else {
//                        if (this.getCowInfo().getCurHotDeskNote() == 0) {
//                            player.addScore(Score.ACC_WIN_CNT, 1, true);
//                        }
//                        player.addRebet(this.getScore(this.getCowInfo().getCurHotDeskNote()), false);
//                        player.maxScore(Score.ACC_MAX_SCORE, this.getScore(this.getCowInfo().getCurHotDeskNote()), true);
//                        this.getCowInfo().setCurHotDeskNote(0);
//                    }
//                }
//                player.maxScore(Score.ACC_POKER_MAX_CARD_TYPE, player.getCurCardType().getValue(), true);
//                player.maxScore(Score.ACC_MAX_SCORE, rebetNum * -1, true);
//            } else {
                int playerWinScore = rebetNum * -1;
                rebetNum = playerWinScore * -1;
                bankerPlayer.addRebet(rebetNum, true);
                player.addRebet(playerWinScore, false);
                player.setPrevWinValue(playerWinScore);// 设置分数；
           // }
            action.addResult(player.getUid(), player.getResultCard().size() == 0 ? player.getHandCard() : player.getResultCard(),
                    this.getClientScore(player.getScore(Score.SCORE, false)),
                    this.getClientScore(player.getScore() + player.getScore(Score.SCORE, false)),
                    player.getCurCardType().getValue());
        }
    }

    protected List<CowPlayer> getPlayerSortList() {
        CowPlayer bankerPlayer = (CowPlayer) this.allPlayer[this.bankerIndex];
        List<CowPlayer> lostPlayerList = new ArrayList<>();
        List<CowPlayer> winPlayerList = new ArrayList<>();
        for (int j = 0; j < this.playerNum; ++j) {
            CowPlayer player = (CowPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (this.comparePlayer(bankerPlayer, player)) {
                lostPlayerList.add(player);
            } else {
                winPlayerList.add(player);
            }
        }
        winPlayerList.sort((o1, o2) -> this.comparePlayer(o1, o2) ? 1 : -1);
        lostPlayerList.addAll(winPlayerList);
        return lostPlayerList;
    }

    protected void setPlayerPokerCardInfo() {
        for (int j = 0; j < this.playerNum; ++j) {
            CowPlayer player = (CowPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            this.setPlayerPokerCardInfo(player);
        }
    }

    protected void setSpellTenTakeAction(long timeout) {
        timeout = OPEN_CARD_TS;
        //斗公牛
        if (this.getGameSubType() == 1){
            timeout = OPEN_CARD_TS;
        }
        CowTakeAction action = new CowTakeAction(this, timeout);
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            action.addLookPlayer(player.getUid(), false);
        }
        this.addAction(action);
    }

    public void addLookPlayer(Player player) {
        if (this.action.empty()){
            return;
        }
        IAction action = this.action.peek();
        if (action instanceof CowTakeAction) {
            ((CowTakeAction) action).addLookPlayer(player.getUid(), true);
            this.tick();
        }
    }

    private boolean isLookCard(long uid) {
        if (!this.action.isEmpty() && this.action.peek() instanceof CowTakeAction) {
            return ((CowTakeAction) this.action.peek()).getLookPlayer(uid);
        }
        return false;
    }

    public void setRobBankerFail(Player player) {
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        IAction action = this.action.peek();
        if (action instanceof CowRobBankerAction) {
            ErrorCode err = ((CowRobBankerAction) action).selectRobBaker(player.getUid(), 0);
            if (ErrorCode.OK == err) {
                roomPlayer.setScore(Score.POKER_COW_ROB_BANKER_MUL, 0, false);
                this.tick();
            }
        }
    }


    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    /**
     * 结算设置玩家牌型
     * 斗公牛，明牌抢庄
     * @param player 玩家
     */    
    private void setPlayerPokerCardInfo(CowPlayer player) {
    	//上局是否是推注；
        player.setPrePushNote(player.isCurPushNote());
        //当前局是否是推注；
        player.setCurPushNote(false);
        //排序
        PokerUtil.sortByCow(player.getHandCard());
        //初始化手牌
        player.initHandCard();
        List<Byte> result = new ArrayList<>(5);
        List<Byte> resultWithLaiZi = new ArrayList<>(5);
        //默认无牛
        EPokerCardType cardType = EPokerCardType.COW_NONE;
        double cardValue = -1;
        List<Byte> handCardWithLaiZi = new ArrayList<>();
        //赖子牌
        Byte laiZiCard = this.getCowInfo().getLaiZiCard();
        //玩家是否有癞子
        if (laiZiCard != -1 && player.hasCardCnt(laiZiCard,1)){
        	//循环玩家手牌
            for (int i = 0; i < player.getHandCard().size(); i++){
            	//手牌
                byte card = player.getHandCard().get(i);
                //判断是不是癞子
                if (PokerUtil.getCardValue(card) == PokerUtil.getCardValue(laiZiCard)){
                	//变大王
                    handCardWithLaiZi.add(PokerUtil.KING);
                }else {
                    handCardWithLaiZi.add(card);
                }
            }
            //排序从小到大
            PokerUtil.sortByCow(handCardWithLaiZi);
        }
        do {
        	//同花顺
            if (this.getCowInfo().isTongHuaShun()) {
                cardValue = PokerUtil.isCowWithTheFlower(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_WITH_THE_FLOWER;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowWithTheFlower(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_WITH_THE_FLOWER;
                        cardValue = cardTempValue;
                        for (int i = 0; i < resultWithLaiZi.size();i++){
                            if (resultWithLaiZi.get(i) == PokerUtil.KING){
                                if (i == 0){
                                    if (resultWithLaiZi.get(1) != PokerUtil.KING){
                                        resultWithLaiZi.set(0, (byte) (resultWithLaiZi.get(1) - 1));
                                    }else {
                                        resultWithLaiZi.set(0, (byte) (resultWithLaiZi.get(2) - 2));
                                    }
                                }else {
                                    resultWithLaiZi.set(i, (byte) (resultWithLaiZi.get(i-1) +1));
                                }
                            }
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }
            }
            // 一条龙
            if (this.getCowInfo().isYiTiaoLong()) {
                cardValue = PokerUtil.isCowDragon(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_DRAGON;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowDragon(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_DRAGON;
                        cardValue = cardTempValue;
                        for (int i = 0; i < resultWithLaiZi.size();i++){
                            if (resultWithLaiZi.get(i) == PokerUtil.KING){
                                if (i == 0){
                                    if (resultWithLaiZi.get(1) != PokerUtil.KING){
                                        resultWithLaiZi.set(0, (byte) (resultWithLaiZi.get(1) - 1));
                                    }else {
                                        resultWithLaiZi.set(0, (byte) (resultWithLaiZi.get(2) - 2));
                                    }
                                }else {
                                    resultWithLaiZi.set(i, (byte) (resultWithLaiZi.get(i-1) +1));
                                }
                            }
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }
            }
            //炸弹牛
            if (this.getCowInfo().isZhaDanNiu()) {
                cardValue = PokerUtil.isCowBomb(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_BOMB;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowBomb(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_BOMB;
                        cardValue = cardTempValue;
                        byte bombCardValue = PokerUtil.getCardValue(resultWithLaiZi.get(3));
                        for (int i = 0; i < (resultWithLaiZi.size() -1);i++){
                            if (resultWithLaiZi.get(i) == PokerUtil.KING){
                                for (int j = 0; j < 4; j++){
                                    Byte tempCard = Byte.valueOf((byte) (bombCardValue + 13 * j));
                                    if (!resultWithLaiZi.contains(tempCard)){
                                        resultWithLaiZi.set(i,tempCard);
                                        break;
                                    }
                                }
                            }
                        }
                        if (resultWithLaiZi.get(1) == PokerUtil.KING){
                            resultWithLaiZi.set(1,resultWithLaiZi.get(4));
                            resultWithLaiZi.set(4,this.getCowInfo().getLaiZiCard());
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }
            }
            //五小牛
            if (this.getCowInfo().isWuXiaoNiu()) {
                cardValue = PokerUtil.isCowFiveSmall(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_FIVE_SMALL;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowFiveSmall(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_FIVE_SMALL;
                        cardValue = cardTempValue;
                        for (int i = 0; i < resultWithLaiZi.size();i++){
                            if (resultWithLaiZi.get(i) == PokerUtil.KING){
                                for (int j = 0; j < 5; j++){
                                    byte tempCardValue = (byte) j;
                                    if (tempCardValue == 0){
                                        tempCardValue = PokerUtil._A;
                                    }else if (tempCardValue == 1){
                                        tempCardValue = PokerUtil._2;
                                    }else {
                                        tempCardValue = (byte) (j - 2);
                                    }
                                    for (int n = 0; n < 4; n++){
                                        Byte tempCard = Byte.valueOf((byte) (tempCardValue + 13 * n));
                                        if (!resultWithLaiZi.contains(tempCard)){
                                            resultWithLaiZi.set(i,tempCard);
                                            break;
                                        }
                                    }
                                    if (resultWithLaiZi.get(i) != PokerUtil.KING){
                                        break;
                                    }
                                }
                            }
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }
            }
            //葫芦牛
            if (this.getCowInfo().isHuLuNiu()) {
                cardValue = PokerUtil.isCowCucurbit(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_CUCURBIT;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowCucurbit(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_CUCURBIT;
                        cardValue = cardTempValue;
                        for (int i = 0; i < resultWithLaiZi.size();i++){
                            if (resultWithLaiZi.get(i) == PokerUtil.KING){
                                byte tempCardValue = -1;
                                if (i <= 2){
                                    tempCardValue = PokerUtil.getCardValue(resultWithLaiZi.get(2));
                                } else if (i == 3 && resultWithLaiZi.get(4) != PokerUtil.KING){
                                    tempCardValue = PokerUtil.getCardValue(resultWithLaiZi.get(4));
                                }else{
                                    tempCardValue = PokerUtil.getCardValue(this.getCowInfo().getLaiZiCard());
                                }
                                for (int n = 0; n < 4; n++){
                                    Byte tempCard = Byte.valueOf((byte) (tempCardValue + 13 * n));
                                    if (!resultWithLaiZi.contains(tempCard)){
                                        resultWithLaiZi.set(i,tempCard);
                                        break;
                                    }
                                }
                                if (resultWithLaiZi.get(i) != PokerUtil.KING){
                                    break;
                                }
                            }
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }
            }
            //金牛
            if (this.getCowInfo().isJinNiu()) {
                cardValue = PokerUtil.isCowGold(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_GOLD;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowGold(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_GOLD;
                        cardValue = cardTempValue;
                        for (int i = 0; i < resultWithLaiZi.size();i++){
                            if (resultWithLaiZi.get(i) == PokerUtil.KING){
                                for (int j = 8; j < 11; j++){
                                    byte tempCardValue = (byte) j;
                                    for (int n = 0; n < 4; n++){
                                        Byte tempCard = Byte.valueOf((byte) (tempCardValue + 13 * n));
                                        if (!resultWithLaiZi.contains(tempCard)){
                                            resultWithLaiZi.set(i,tempCard);
                                            break;
                                        }
                                    }
                                    if (resultWithLaiZi.get(i) != PokerUtil.KING){
                                        break;
                                    }
                                }
                            }
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }
            }
            //同花牛
            if (this.getCowInfo().isTongHuaNiu()) {
                cardValue = PokerUtil.isCowSameColor(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_SAME_COLOR;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowSameColor(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_SAME_COLOR;
                        cardValue = cardTempValue;
                        for (int i = 0; i < resultWithLaiZi.size();i++){
                            if (resultWithLaiZi.get(i) == PokerUtil.KING){
                                for (int j = 12; j >= 0; j--){
                                    byte tempCard = (byte) (j - PokerUtil.getCardValue(resultWithLaiZi.get(2)) + resultWithLaiZi.get(2));
                                    if (!resultWithLaiZi.contains(tempCard)){
                                        resultWithLaiZi.set(i, tempCard);
                                        break;
                                    }
                                }
                            }
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }
            }
            //银牛
            if (this.getCowInfo().isYinNiu()) {
                cardValue = PokerUtil.isCowSilver(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_SILVER;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowSilver(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_SILVER;
                        cardValue = cardTempValue;

                        for (int i = 0; i < 2; i++){
                            if (resultWithLaiZi.get(i) != PokerUtil.KING){
                                continue;
                            }
                            if (i == 0 && !player.hasCardCnt(PokerUtil._10,1)){
                                resultWithLaiZi.set(0, (byte) (PokerUtil.K_SPADES - 3));
                                continue;
                            }
                            for (int j = 8; j < 11; j++){
                                byte tempCardValue = (byte) j;
                                for (int n = 0; n < 4; n++){
                                    Byte tempCard = Byte.valueOf((byte) (tempCardValue + 13 * n));
                                    if (!resultWithLaiZi.contains(tempCard)){
                                        resultWithLaiZi.set(i,tempCard);
                                        break;
                                    }
                                }
                                if (resultWithLaiZi.get(i) != PokerUtil.KING){
                                    break;
                                }
                            }
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }
            }
            //顺子牛
            if (this.getCowInfo().isShunZiNiu()) {
                cardValue = PokerUtil.isCowStraight(player.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_STRAIGHT;
                }
                if (handCardWithLaiZi.size() > 0){
                    double cardTempValue = PokerUtil.isCowDragon(handCardWithLaiZi, resultWithLaiZi);
                    if (-1 != cardTempValue && cardTempValue > cardValue) {
                        cardType = EPokerCardType.COW_STRAIGHT;
                        cardValue = cardTempValue;
                        for (int i = 0; i < resultWithLaiZi.size();i++){
                            if (resultWithLaiZi.get(i) == PokerUtil.KING){
                                if (i == 0){
                                    if (resultWithLaiZi.get(1) != PokerUtil.KING){
                                        resultWithLaiZi.set(0, (byte) (resultWithLaiZi.get(1) - 1));
                                    }else {
                                        resultWithLaiZi.set(0, (byte) (resultWithLaiZi.get(2) - 2));
                                    }
                                }else {
                                    resultWithLaiZi.set(i, (byte) (resultWithLaiZi.get(i-1) +1));
                                }
                            }
                        }
                    }else{
                        resultWithLaiZi.clear();
                    }
                }
                if (cardType != EPokerCardType.COW_NONE){
                    break;
                }

            }
        } while (false);

        //无牛
        if (cardType == EPokerCardType.COW_NONE) {
            if (handCardWithLaiZi.size() == 0) {
                cardType = PokerUtil.findCow(player.getHandCard(), result);
                byte value = player.getHandCard().get(4);
                if (cardType == EPokerCardType.COW_NONE) {
                    cardValue = PokerUtil.generateCardValueByCow2WithColor(PokerUtil.getCardValueByCow2(value),
                            (byte) (PokerUtil.getCardColor(value) + 1)) * Math.pow(128, 0);
                } else {
                    cardValue = PokerUtil.generateCardValueByCow2WithColor(PokerUtil.getCardValueByCow2(value),
                            (byte) (PokerUtil.getCardColor(value) + 1)) * Math.pow(128, 1);
                }
            }else {
            	//判定牌型
                cardType = PokerUtil.findCow(handCardWithLaiZi, resultWithLaiZi);
                byte value = player.getHandCard().get(4);
                if (cardType == EPokerCardType.COW_NONE) {
                    cardValue = PokerUtil.generateCardValueByCow2WithColor(PokerUtil.getCardValueByCow2(value),
                            (byte) (PokerUtil.getCardColor(value) + 1)) * Math.pow(128, 0);
                } else {
                    cardValue = PokerUtil.generateCardValueByCow2WithColor(PokerUtil.getCardValueByCow2(value),
                            (byte) (PokerUtil.getCardColor(value) + 1)) * Math.pow(128, 1);
                    if (player.hasCardCnt(this.getCowInfo().getLaiZiCard(),2)){
                        for (int i = 2; i < 4; i++){
                            byte newCard = -1;
                            byte tempValue = -1;
                            if (i == 2) {
                                tempValue = (byte) (10 - (PokerUtil.getCardValueByCow(resultWithLaiZi.get(1)) +PokerUtil.getCardValueByCow(resultWithLaiZi.get(2))) % 10);
                            }else {
                                tempValue = (byte) (10 - PokerUtil.getCardValueByCow(resultWithLaiZi.get(4)));
                            }
                            if (tempValue == 0){
                                newCard = PokerUtil._K;
                            }else {
                                if (tempValue == 1){
                                    newCard = PokerUtil._A;
                                }else if (tempValue == 2){
                                    newCard = PokerUtil._2;
                                }else {
                                    newCard = (byte) (PokerUtil._3 + tempValue - 3);
                                }
                            }
                            for (int n = 0; n < 4; n++){
                                Byte tempCard = Byte.valueOf((byte) (newCard + 13 * n));
                                if (!resultWithLaiZi.contains(tempCard)){
                                    resultWithLaiZi.set(i,tempCard);
                                    break;
                                }
                            }
                        }
                    }else {
                        byte newCard = -1;
                        byte tempValue = -1;
                        int laiZiCardIndex = 0;
                        if (resultWithLaiZi.get(3) == PokerUtil.KING){
                            tempValue = (byte) (10 - PokerUtil.getCardValueByCow(resultWithLaiZi.get(4)));
                            laiZiCardIndex = 3;
                        }else {
                            tempValue = (byte) (10 - (PokerUtil.getCardValueByCow(resultWithLaiZi.get(1)) +PokerUtil.getCardValueByCow(resultWithLaiZi.get(2))) % 10);
                            laiZiCardIndex = 0;
                        }
                        if (tempValue == 0){
                            newCard = PokerUtil._K;
                        }else {
                            if (tempValue == 1){
                                newCard = PokerUtil._A;
                            }else if (tempValue == 2){
                                newCard = PokerUtil._2;
                            }else {
                                newCard = (byte) (PokerUtil._3 + tempValue - 3);
                            }
                        }
                        for (int n = 0; n < 4; n++){
                            Byte tempCard = Byte.valueOf((byte) (newCard + 13 * n));
                            if (!resultWithLaiZi.contains(tempCard)){
                                resultWithLaiZi.set(laiZiCardIndex,tempCard);
                                break;
                            }
                        }
                    }
                }
            }
        }
        player.setCurCardType(cardType);
        player.setCardValue(cardValue);
        player.setMaxCardType();
        if (result.isEmpty()) {
            player.setResultCard(player.getHandCard());
        } else {
            player.setResultCard(result);
        }

        if (!resultWithLaiZi.isEmpty()){
            player.setResultCard(resultWithLaiZi);
        }
    }
}
