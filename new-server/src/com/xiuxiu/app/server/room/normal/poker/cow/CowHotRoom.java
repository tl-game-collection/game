package com.xiuxiu.app.server.room.normal.poker.cow;

import com.alibaba.fastjson.JSONObject;
import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfCowHotLoopInfo;
import com.xiuxiu.app.protocol.client.poker.cow.*;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.impl.BoxArenaCowHotRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.DelayAction;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.action.cow.CowReBetAction;
import com.xiuxiu.app.server.room.normal.poker.action.cow.CowHotAgainAction;
import com.xiuxiu.app.server.room.normal.poker.action.cow.CowHotOutAction;
import com.xiuxiu.app.server.room.normal.poker.action.cow.CowReadyAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.app.server.room.record.poker.RecordPokerPlayerBriefInfo;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.KeyValue;
import com.xiuxiu.core.log.Log;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 斗公牛
 * @author MyPC
 *
 */
@GameInfo(gameType = GameType.GAME_TYPE_COW,gameSubType = 1)
public class CowHotRoom extends AbstractCowRoom{
    enum EPhase {
        INIT, READY,SEND_CARD ,REB, OPEN_CARD
    }

    /**
     * 等待客户端显示先发牌的表现时间
     */
    private static final long HOT_SHOW_CARD_TS = 2 * 1000;
    /**
     * 准备时间
     * 2020-08-22修改8->5
     */
    private static final long HOT_READY_TS = 5 * 1000;
    /**
     * 等待下注时间
     */
    private static final long HOT_REB_TS = 8 * 1000;
    /**
     * 等待续锅时间
     */
    private static final long HOT_AGAIN_TS = 6 * 1000;
    /**
     * 等待下庄时间
     */
    private static final long HOT_STOP_TS = 6 * 1000;
    /**
     * 射门比例
     * 2020-08-22修改3->4
     */
    private static final int SHE_MEN_PRE = 4;
    /**
     * 几局提示提示下庄
     */
    private static final int HOT_OUT_GAME_CNT_TIP = 3;

    /**
     * 当前阶段
     */
    protected EPhase curPhase = EPhase.INIT;

    /**
     * 上一局的庄index
     */
    private int preBankIndex = -1;

    public CowHotRoom(RoomInfo roomInfo) {
        super(roomInfo);
    }

    public CowHotRoom(RoomInfo roomInfo, ERoomType roomType) {
        super(roomInfo, roomType);
    }

    /**
     * 获取基础下注值
     * @return
     */
    public int getBaseRetValue(){
        int percentage = 0;
        //此庄首轮
        if (this.getCowInfo().getCurHotBankerLoop() == 1){
            percentage = this.getCowInfo().getFirstBaseBetPre();
        }else {
            percentage = this.getCowInfo().getNextBaseBetPre();
        }
        return getBankMinNote(this.getCowInfo().getKeepCount()) * percentage / 100;
    }
    /**
     * 获取射门下注值
     * @param pokerPlayer
     * @return
     */
    public int  getSheMenRetValue(IPokerPlayer pokerPlayer){
    	//比较下注玩家score和当前桌面端火锅已经总筹码score
        int minValue = Math.min(getExchangeGoldForScore(this.getPlayerGold(pokerPlayer.getUid())),this.getCowInfo().getCurHotDeskNote());
        //选出俩者中最小的score/4
        return minValue / SHE_MEN_PRE;
    }
    /**
     * 获取下注最小值
     * @param pokerPlayer
     * @return
     */
    public int getMinReb(IPokerPlayer pokerPlayer){
        return Math.min(getBaseRetValue(),getExchangeGoldForScore(this.getPlayerGold(pokerPlayer.getUid())));
    }
    /**
     * 斗公牛 检查下注值是否合法
     * 斗公牛下整注，无小数注
     * @return
     */
    @Override
    public boolean checkRebValue(IPokerPlayer pokerPlayer, int rebValue,int pushNoteValue,boolean isDoubling){
//注释        return rebValue == this.getScore(getBaseRetValue()) || rebValue == this.getScore(getSheMenRetValue(pokerPlayer)) || rebValue == this.getScore(getMinReb(pokerPlayer));
//    	return rebValue == getBaseRetValue() || rebValue == getSheMenRetValue(pokerPlayer) || rebValue == getMinReb(pokerPlayer);
    	//下注筹码在基础下注值到射门值之间 PS:射门值有小于底注和最小注的时候
    	if(getSheMenRetValue(pokerPlayer)>=getMinReb(pokerPlayer) && getMinReb(pokerPlayer)>=getBaseRetValue()) {
    		return (rebValue>=getMinReb(pokerPlayer) || rebValue>=getBaseRetValue()) && rebValue <= getSheMenRetValue(pokerPlayer);
    	}else {
    		return (rebValue<=getMinReb(pokerPlayer) || rebValue<=getBaseRetValue()) && rebValue >= getSheMenRetValue(pokerPlayer);
    	}
    }

    /**
     * 获取成为庄家最小竞技分
     * @param keepCount
     * @return
     */
    public int getBankMinNote(int keepCount){
        return this.getCowInfo().getHotLessNote()*(keepCount <= 0 ? 1 : this.getCowInfo().getHotUp());
    }

    @Override
    public void init() {
        this.getRule().put(RoomRule.RR_COSTMODEL,2);
        super.init();
        this.autoReady = true;
        this.autoStartTime = (int) HOT_READY_TS / 3;
    }

    /**
     * 选庄家
     */
    protected void setHotBanker() {
        this.bankerIndex = getNextBankIndex(this.preBankIndex);
        this.preBankIndex = this.bankerIndex;
        if (-1 == this.bankerIndex || null == this.allPlayer[this.bankerIndex]) {
            Logs.ROOM.error("hotBanker club is null ,clubId:%d",this.getGroupUid());
            return;
        }

        this.getCowInfo().setCurHotDeskNote(getBankMinNote(this.getCowInfo().getKeepCount()));
        this.getCowInfo().setCurHotBankerLoop(0);
        this.clearScore();

        PCLIPokerNtfCowRobBankerResultInfo info = new PCLIPokerNtfCowRobBankerResultInfo();
        info.bankerPlayerUid = this.allPlayer[this.bankerIndex].getUid();
        info.hotBankerLoop = this.getCowInfo().getCurHotBankerLoop();
        info.hotDeskNote = this.getCowInfo().getCurHotDeskNote();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_RESULT, info);
    }

    /**
     * 一个庄家的新的一小局开启
     */
    private void newLoop() {
        this.isOver = false;
        if (this.getCowInfo().getCurHotBankerLoop() > 0) {
            getRoomHandle().again();
        }
        this.getCowInfo().addCurHotBankerLoop();
        this.getCowInfo().setTotalLoop(this.getCowInfo().getTotalLoop()+1);
        this.curPhase = EPhase.INIT;
        this.doSendLoopInfo();
        // 准备阶段
        this.beginReady();
    }

    /**
     * 发送小局开局信息
     */
    private void doSendLoopInfo() {
        PCLIPokerNtfCowHotLoopInfo info = new PCLIPokerNtfCowHotLoopInfo();
        info.curLoop = this.getCowInfo().getCurHotBankerLoop();
        info.curNote = this.getCowInfo().getCurHotDeskNote();
        info.curKeepCount = this.getCowInfo().getKeepCount();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_LOOP, info);

        doSendWhiteInfo();
    }

    /**
     * 开始准备
     */
    private void beginReady() {
        this.curPhase = EPhase.READY;
        if (1 == this.getCowInfo().getCurHotBankerLoop() && this.getCowInfo().getKeepCount() == 0) {
            this.onReadyOver();
        } else {
            CowReadyAction action = new CowReadyAction(this,HOT_READY_TS);
            for (int i = 0; i < this.playerNum; ++i) {
                CowPlayer player = (CowPlayer) this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                action.addPlayer(player.getUid());
                player.send(CommandId.CLI_NTF_POKER_COW_BEGIN_READY, null);
            }
            this.addAction(action);
            this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_COW_BEGIN_READY, null);
        }
    }

    /**
     * 准备结束
     */
    public void onReadyOver() {
        this.doShuffleAndDeal();
        PCLIPokerNtfCowReadyInfo readyInfo = new PCLIPokerNtfCowReadyInfo();
        for (int j = 0; j < this.playerNum; ++j) {
            CowPlayer player = (CowPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            readyInfo.readyPlayerUids.add(player.getUid());
        }
        readyInfo.laiZiCard = this.getCowInfo().getLaiZiCard();
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_READY_OVER_INFO,readyInfo);
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_COW_READY_OVER_INFO,readyInfo);

        if (1 == this.getCowInfo().getOptType()) {
            this.curPhase = EPhase.SEND_CARD;
            // 先发牌后下注
            this.sendFirstCardToClient();
            final CowHotRoom self = this;
            DelayAction action = new DelayAction(this, HOT_SHOW_CARD_TS);
            action.setCallback(new ICallback<Object>() {
                @Override
                public void call(Object... args) {
                    self.onRebet();
                }
            });
            this.addAction(action);
        } else {
            this.onRebet();
        }
    }

    /**
     * 小局洗牌发牌
     */
    private void doShuffleAndDeal() {
        this.getCowInfo().setSendCardCount(-1);
        if (this.getCowInfo().getCurHotBankerLoop() == 1 && this.getCowInfo().getKeepCount() == 0){
            return;
        }
        this.allCard.clear();
        this.doShuffle();
        this.doDeal();
    }
    
    

    /**
     * 斗公牛发牌
     * playerNum=6(玩法房间最大玩家数)
     */
    @Override
    protected void doDeal() {
        this.doOnlyDeal();
    	// 当前打牌玩家
        this.curPlayerCnt = this.playerCnt.get();
    	//发五张牌
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            this.getRecord().addPlayer(new RecordPokerPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getRoomPlayerHelper().getCurBureau()));
        }
        //好牌
        if(isDoDeal()) {
//        if(true) {
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
//        	List<Integer> goodPlayer = new ArrayList<Integer>();
//        	goodPlayer.add(1);
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
        	clearGoodPlayer();
        }
    }

    /**
     * 发送大局开始
     */
    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.setLook(false);
            PCLIRoomNtfBeginInfoByCow beginInfoByCow = new PCLIRoomNtfBeginInfoByCow();
            beginInfoByCow.bureau = getBureau();
            beginInfoByCow.roomBriefInfo = this.getRoomBriefInfo(player);
            beginInfoByCow.roomBriefInfo.curBureau = this.getRoomBriefInfo(player).curBureau + 1;
            beginInfoByCow.d = Config.checkWhiteHas(player.getUid(), 2);
            beginInfoByCow.roundCount = this.getCowInfo().getCurHotBankerLoop();
            beginInfoByCow.laiZiCard = this.getCowInfo().getLaiZiCard();
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, beginInfoByCow);
        }
        PCLIRoomNtfBeginInfoByCow beginInfoByCow = new PCLIRoomNtfBeginInfoByCow();
        beginInfoByCow.bureau = 0;
        beginInfoByCow.roomBriefInfo = this.getRoomBriefInfo();
        beginInfoByCow.roomBriefInfo.curBureau = 0;
        beginInfoByCow.roundCount = this.getCowInfo().getCurHotBankerLoop();
        beginInfoByCow.laiZiCard = this.getCowInfo().getLaiZiCard();
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, beginInfoByCow);

        doSendWhiteInfo();
    }

    /**
     * 大局开始
     */
    protected void doStart1(){
        setHotBanker();
        newLoop();
    }

    /**
     * 开始下注
     */
    @Override
    public void onRebet() {
        this.curPhase = EPhase.REB;
        // 下注
        CowReBetAction action = new CowReBetAction(this, HOT_REB_TS);
        action.setBase(this.getBaseRetValue());
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (this.getBankerIndex() != i){
                action.addPushNote(player.getUid(),0);
            }
            PCLIPokerNtfCowReBetBeginInfo info = new PCLIPokerNtfCowReBetBeginInfo();
            info.baseRebet = this.getMinReb(player);
            info.pushNote = this.getSheMenRetValue(player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_BEGIN, info);
        }
        this.addAction(action);
    }


    /**
     * 开牌
     */
    @Override
    public void onDealCard() {
        if (1 == this.getCowInfo().getOptType()) {
            this.sendLastCards();
        }else {
            super.onDealCard();
        }
        this.curPhase = EPhase.OPEN_CARD;
    }

    @Override
    public void onOver() {
        this.onResult();
        // 记录战绩
        this.record();
        this.getRecord().save();
        this.record = null;

        this.doSendGameOverLoop();
        this.clearLoop();

        for (int i = 0; i < this.playerNum; i++) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            if (i == this.bankerIndex) {
                continue;
            }

            IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) getRoomHandle();
            long boxUid = boxRoomHandle.getBoxUid();
            Box box = BoxManager.I.getBox(boxUid);
            if (box != null) {
                Player tempPlayer = PlayerManager.I.getPlayer(player.getUid());
                // 判断身上竞技值是否小于出场分数
                if (tempPlayer != null && getPlayerGold(player.getUid()) <= getRule().getOrDefault(RoomRule.RR_LEAVEGOLD, 0)) {
                    box.sitUp(tempPlayer);
                    continue;
                }
            }
        }

        //不满足开房人数直接结束
        if (getPlayPlayerCount() < playerMinNum) {
            // 快速直接下庄
            fastHotOut();
            return;
        }

        //锅底输完，或者达到最大局数
        if (this.getCowInfo().getCurHotDeskNote() <= 0
                || (-1 != this.getCowInfo().getHotMaxLoop() && this.getCowInfo().getCurHotBankerLoop() >= this.getCowInfo().getHotMaxLoop())) {
            // 计算实际庄家竞技分
            int keepCount = this.getCowInfo().getKeepCount();
            doBankerGold(getScore(this.getCowInfo().getCurHotDeskNote() - this.getBankMinNote(keepCount)));

            IRoomPlayer bankerPlayer = this.allPlayer[this.bankerIndex];
            if (keepCount >= this.getCowInfo().getHotCnt() || bankerPlayer != null && this.getPlayerGold(bankerPlayer.getUid()) < this.getScore(this.getBankMinNote(keepCount+1))){
                // 续锅次数不足直接下庄结束
                this.onGameOver();
                return;
            }
            // 可以续锅
            this.beginHotAgain();
        } else {
            // 手动下庄，或继续游戏
            this.beginHotOut();
        }
    }

    private void onResult(){
    	// 上一把最大牛牛牌型的玩家index
        this.getCowInfo().setPrevMaxPlayerIndex(-1);
        // 上一把最大牌型的玩家index
        this.getCowInfo().setPrevMaxCardPlayerIndex(-1);
        // 上一把庄家是否有牛；
        this.getCowInfo().setPreBankerHas(Boolean.FALSE);
        this.isOver = true;

        // 当前桌面端火锅已经总筹码
        int preHotDeskNote = this.getCowInfo().getCurHotDeskNote();
        // 庄家索引->庄家
        CowPlayer bankerPlayer = (CowPlayer) this.allPlayer[this.bankerIndex];
        List<CowPlayer> playerList = this.getPlayerSortList();
        List<KeyValue<Long,Integer>> winBankerList = new ArrayList<>();
        for (int i = 0, len = playerList.size(); i < len; ++i) {
            CowPlayer player = playerList.get(i);
            //player.isGuest()是否是游客
            if (null == player || player.isGuest()) {
                continue;
            }
            if (player.getUid() == bankerPlayer.getUid()) {
                continue;
            }
            //和庄家比输赢
            boolean bankerWin = this.comparePlayer(bankerPlayer, player);
            EPokerCardType winCardType = bankerWin ? bankerPlayer.getCurCardType() : player.getCurCardType();
            //根据牛几获取倍数
            int multiple = this.getMultiple(winCardType);
            //下注分*牛几的倍数=最后+-分
            int betValue = player.getScore(Score.POKER_COW_REBET, false) * multiple;
            if (bankerWin) { // 庄赢
            	//扣减玩家 通用分数
                player.addScore(Score.SCORE, -this.getScore(betValue), false);
                //扣减玩家 当前一小轮分数
                player.setScore(Score.POKER_COW_LOOP_SCORE, -this.getScore(betValue), false);
                //扣减玩家 通用总分
                player.addScore(Score.ACC_TOTAL_SCORE, -this.getScore(betValue), true);
                //增加玩家 通用输的次数
                player.addScore(Score.ACC_LOST_CNT, 1, true);
                //增加 当前桌面端火锅已经总筹码
                this.getCowInfo().setCurHotDeskNote(this.getCowInfo().getCurHotDeskNote() + betValue);
                //当前自身存储的通用总分-之前玩家当前房间游戏桌临时信息（通用总分）
                int tempValue = player.getScore(Score.ACC_TOTAL_SCORE,true) - this.getTemporaryPropertyValue(player.getUid(),Score.ACC_TOTAL_SCORE);
                //更新玩家当前房间游戏桌临时信息（通用总分）
                this.setTemporaryPropertyValue(player.getUid(),Score.ACC_TOTAL_SCORE,tempValue);
            } else { // 庄输
                winBankerList.add(new KeyValue(player.getUid(),betValue));
            }
        }
        // 赢的多的人先赔付
        winBankerList.sort((o1, o2) -> o2.getValue() - o1.getValue());
        for (int i = 0; i < winBankerList.size(); i++){
            long playerUid = winBankerList.get(i).getKey();
            //赢得score
            int playerWinScore = winBankerList.get(i).getValue();
            CowPlayer player = (CowPlayer) this.getRoomPlayer(playerUid);
            if (null != player) {
            	//判断赢得score是否大于桌上总score,大于的话桌上剩多少就赢多少
                int lostNote = (this.getCowInfo().getCurHotDeskNote() >= playerWinScore) ? playerWinScore : this.getCowInfo().getCurHotDeskNote();
                this.getCowInfo().setCurHotDeskNote(this.getCowInfo().getCurHotDeskNote() - lostNote);
                player.addScore(Score.SCORE, this.getScore(lostNote), false);
                player.setScore(Score.POKER_COW_LOOP_SCORE, this.getScore(lostNote), false);
                player.addScore(Score.ACC_TOTAL_SCORE, this.getScore(lostNote), true);
                player.addScore(Score.ACC_WIN_CNT, 1, true);
                int tempValue = player.getScore(Score.ACC_TOTAL_SCORE, true) - this.getTemporaryPropertyValue(player.getUid(), Score.ACC_TOTAL_SCORE);
                this.setTemporaryPropertyValue(player.getUid(), Score.ACC_TOTAL_SCORE, tempValue);
            }
        }

        int winHotDeskNote = this.getCowInfo().getCurHotDeskNote() - preHotDeskNote;
        bankerPlayer.addScore(Score.SCORE, this.getScore(winHotDeskNote), false);
        bankerPlayer.setScore(Score.POKER_COW_LOOP_SCORE, this.getScore(winHotDeskNote), false);
        bankerPlayer.addScore(Score.ACC_TOTAL_SCORE,this.getScore(winHotDeskNote), true);
        int tempValue = bankerPlayer.getScore(Score.ACC_TOTAL_SCORE,true) - this.getTemporaryPropertyValue(bankerPlayer.getUid(),Score.ACC_TOTAL_SCORE);
        this.setTemporaryPropertyValue(bankerPlayer.getUid(),Score.ACC_TOTAL_SCORE,tempValue);
        if (winHotDeskNote > 0) {
            bankerPlayer.addScore(Score.ACC_WIN_CNT, 1, true);
        } else if (winHotDeskNote <= 0) {
            bankerPlayer.addScore(Score.ACC_LOST_CNT, 1, true);
        }

        // 竞技值计算处理
        this.getRoomHandle().calculateGold();
    }

    /**
     * 斗公牛
     * 小局结束返回客户端信息
     */
    private void doSendGameOverLoop() {
        PCLIPokerNtfCowGameOverInfo info = new PCLIPokerNtfCowGameOverInfo();
        info.next = true;
        info.bankerBureau = this.getCowInfo().getCurHotBankerLoop();
        info.keepHotCount = this.getCowInfo().getKeepCount();

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
            gameOverInfo.score = this.getClientScore((player.getScore(Score.POKER_COW_LOOP_SCORE, false)));
            gameOverInfo.scoreValue = player.getScore(Score.POKER_COW_LOOP_SCORE, false);
            gameOverInfo.totalScore = this.getClientScore((int) (this.getPlayerGold(iPlayer.getUid())));

            //当为庄并且是端火锅
//            if (this.bankerIndex == player.getIndex()) {
//                gameOverInfo.totalScore = this.getClientScore(this.getScore(this.getCowInfo().getCurHotDeskNote()));
//            }
            //牌型
            gameOverInfo.cardType = player.getCurCardType().getValue();
            gameOverInfo.robBankerMul = player.getScore(Score.POKER_COW_ROB_BANKER_MUL, false);
            gameOverInfo.cardDouble = this.getMultiple(player.getCurCardType());
            gameOverInfo.playerUid = player.getUid();
            if (!player.getHandCard().isEmpty()) {
                gameOverInfo.lastCardValue = player.getLastCard();
            }
            //所有玩家解散信息
            info.allGameOverInfo.put(player.getUid(), gameOverInfo);
            overInfos.add(gameOverInfo);
        }
        info.hotDeskNote = this.getCowInfo().getCurHotDeskNote();

        // 积分排序 第一 第二。。。
        overInfos.sort((o1, o2) -> o2.scoreValue - o1.scoreValue);
        for (PCLIPokerNtfCowGameOverInfo.GameOverInfo gInfo : overInfos) {
            info.sortScorePlayerUidList.add(gInfo.playerUid);
        }
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_GAME_OVER_LOOP, info);
    }

    /**
     * 房间销毁发给客户端大结算信息
     * @param next
     */
    @Override
    protected void doSendGameOver(boolean next) {
        if (next){
            int nextBankIndex = getNextBankIndex(this.preBankIndex);
            if (nextBankIndex > 0 && this.allPlayer[nextBankIndex] != null){
                PCLIPokerNtfCowNextBankInfo bankInfo = new PCLIPokerNtfCowNextBankInfo();
                bankInfo.bankerPlayerUid = this.allPlayer[nextBankIndex].getUid();
               this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_NEXT_BANKER_INFO,bankInfo);
               this.broadcast2ClientWithWatch(CommandId.CLI_NTF_POKER_COW_NEXT_BANKER_INFO,bankInfo);
            }
            return;
        }
        PCLIPokerNtfCowGameOverInfo info = new PCLIPokerNtfCowGameOverInfo();
        info.next = false;
        List<PCLIPokerNtfCowGameOverInfo.GameOverInfo> overInfos = new ArrayList<>();
//        for (int j = 0; j < this.playerNum; ++j) {
//            CowPlayer player = (CowPlayer) this.allPlayer[j];
//            if (null == player || player.isGuest()) {
//                continue;
//            }
//            PCLIPokerNtfCowGameOverInfo.GameOverInfo gameOverInfo = new PCLIPokerNtfCowGameOverInfo.GameOverInfo();
//            gameOverInfo.playerUid = player.getUid();
//            IPlayer iPlayer = player.getPlayer();
//            if (null != iPlayer) {
//                gameOverInfo.name = iPlayer.getName();
//                gameOverInfo.icon = iPlayer.getIcon();
//            }
//            gameOverInfo.totalScore = String.valueOf(player.getScore(Score.ACC_TOTAL_SCORE, true)/100);
//            info.allGameOverInfo.put(player.getUid(), gameOverInfo);
//            overInfos.add(gameOverInfo);
//        }
        for (Map.Entry<Long,Map<String,Integer>> entry : this.temporaryPropertyMap.entrySet()){
            Player player = PlayerManager.I.getPlayer(entry.getKey());
            if (null == player){
                continue;
            }
            PCLIPokerNtfCowGameOverInfo.GameOverInfo gameOverInfo = new PCLIPokerNtfCowGameOverInfo.GameOverInfo();
            gameOverInfo.playerUid = player.getUid();
            gameOverInfo.name = player.getName();
            gameOverInfo.icon = player.getIcon();
            gameOverInfo.totalScore = String.valueOf(entry.getValue().getOrDefault(Score.ACC_TOTAL_SCORE,0)/100);
            info.allGameOverInfo.put(player.getUid(), gameOverInfo);
            overInfos.add(gameOverInfo);
        }
        overInfos.sort((o1, o2) -> Integer.valueOf(o2.totalScore) - Integer.valueOf(o1.totalScore));
        for (PCLIPokerNtfCowGameOverInfo.GameOverInfo gInfo : overInfos) {
            info.sortScorePlayerUidList.add(gInfo.playerUid);
        }
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAMEOVER, info);
    }

    @Override
    public void clear() {
        super.clear();
        this.getCowInfo().setCurHotBankerLoop(0);
        this.curPhase = EPhase.INIT;
        this.getCowInfo().setKeepCount(0);
        this.bankerIndex = -1;
    }

    private void clearLoop() {
        for (int i = 0; i < this.playerNum; ++i) {
            if (null != this.allPlayer[i]) {
                int oldScore = this.allPlayer[i].getScore(Score.SCORE, false);
                int oldWinCnt = this.allPlayer[i].getScore(Score.ACC_WIN_CNT, false);
                int oldLostCnt = this.allPlayer[i].getScore(Score.ACC_LOST_CNT, false);
                this.allPlayer[i].clear();
                this.allPlayer[i].setScore(Score.SCORE, oldScore, false);
                this.allPlayer[i].setScore(Score.ACC_WIN_CNT,oldWinCnt,false);
                this.allPlayer[i].setScore(Score.ACC_LOST_CNT,oldLostCnt,false);
            }
        }
    }

    private void clearScore(){
        for (int i = 0; i < this.playerNum; ++i) {
            if (null != this.allPlayer[i]) {
                this.allPlayer[i].setScore(Score.SCORE, 0,false);
            }
        }
    }

    /**
     * 获取参与游戏的玩家人数
     * @return
     */
    private int getPlayPlayerCount() {
        int count = 0;
        for (int i = 0; i < this.playerNum; ++i) {
            CowPlayer player = (CowPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            ++count;
        }
        return count;
    }

    /**
     * 开始续锅
     */
    private void beginHotAgain() {
        CowHotAgainAction action = new CowHotAgainAction(this, HOT_AGAIN_TS);
        this.action.add(action);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_BEGIN_HOT_AGAIN, null);
    }

    /**
     * 执行续锅
     * @param player
     * @param again
     * @param score
     * @return
     */
    public ErrorCode onHotAgain(IPlayer player, boolean again, int score) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法续锅", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法续锅", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (this.bankerIndex != roomPlayer.getIndex()) {
            Logs.ROOM.warn("%s %s 不是庄家, 无法续锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        //身上竞技值小于续锅的锅底分
        if (getPlayerGold(player.getUid()) < this.getScore(this.getBankMinNote(this.getCowInfo().getKeepCount() + 1))){
            Logs.ROOM.warn("%s %s 竞技分不足, 无法续锅", this, player);
            return ErrorCode.CLUB_OWNER_NOT_ARENA_VALUE;
        }

        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法续锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof CowHotAgainAction) {
            ErrorCode err = ((CowHotAgainAction) action).again(again);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是抢庄动作, 无法续锅", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 续锅结束
     * @param again
     */
    public void onHotAgainOver(boolean again) {
        if (again) {
            if (getRoomHandle() instanceof BoxArenaCowHotRoomHandle) {
                BoxArenaCowHotRoomHandle handle = (BoxArenaCowHotRoomHandle)getRoomHandle();
                handle.serviceCharge(false);
            }
            this.getCowInfo().setKeepCount(this.getCowInfo().getKeepCount()+1);
            this.getCowInfo().setCurHotDeskNote(this.getBankMinNote(this.getCowInfo().getKeepCount()));
            this.getCowInfo().setCurHotBankerLoop(0);
            this.clearScore();
            this.newLoop();
        } else {
            this.onGameOver();
        }
    }

    /**
     * 开始揭锅
     */
    private void beginHotOut() {
        if ((this.getCowInfo().isHotOutTip() && this.getCowInfo().getCurHotBankerLoop() == HOT_OUT_GAME_CNT_TIP && this.getCowInfo().getCurHotDeskNote() >= this.getCowInfo().getHotLevelLessNote())){
            CowHotOutAction action = new CowHotOutAction(this, HOT_STOP_TS);
            this.action.add(action);
            this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_BEGIN_HOT_OUT, null);
        } else {
            this.newLoop();
        }
    }

    /**
     * 快速直接揭锅
     */
    private void fastHotOut() {
        CowHotOutAction action = new CowHotOutAction(this, HOT_STOP_TS);
        this.action.add(action);
        this.broadcast2Client(CommandId.CLI_NTF_POKER_COW_BEGIN_HOT_OUT, null);
        this.onHotOut(this.allPlayer[this.bankerIndex].getPlayer(), true);
    }

    /**
     * 执行揭锅
     * @param player
     * @param out
     * @return
     */
    public ErrorCode onHotOut(IPlayer player, boolean out) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法揭锅", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法揭锅", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (this.bankerIndex != roomPlayer.getIndex()) {
            Logs.ROOM.warn("%s %s 不是庄家, 无法揭锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法揭锅", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof CowHotOutAction) {
            ErrorCode err = ((CowHotOutAction) action).out(out);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是抢庄动作, 无法揭锅", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 揭锅结束
     * @param out
     */
    public void onHotOutOver(boolean out) {
        if (out) {
            // 计算实际庄家竞技分
            int keepCount = this.getCowInfo().getKeepCount();
            doBankerGold(getScore(this.getCowInfo().getCurHotDeskNote() - this.getBankMinNote(keepCount)));
            this.onGameOver();
        } else {
            this.newLoop();
        }
    }

    /**
     * 大局结束
     */
    protected void onGameOver() {
        boolean isNext = this.checkAgain();
        this.gameOver(isNext);
        this.stop();
    }

    @Override
    protected boolean checkAgain() {
        if (getNextBankIndex(this.preBankIndex) == -1){
            return false;
        }
        return super.checkAgain();
    }

    private int getNextBankIndex(int nowBankerIndex){
        int result = -1;
        IClub club = ClubManager.I.getClubByUid(this.getGroupUid());
        if (club == null) {
            Logs.ROOM.error("hotBanker club is null ,clubId:%d",this.getGroupUid());
            return result;
        }
        nowBankerIndex++;
        for(int i = nowBankerIndex < 0 ? 0 : nowBankerIndex; i < this.allPlayer.length;i++ ){
            IRoomPlayer iPlayer = this.allPlayer[i];
            if (iPlayer == null || iPlayer.isGuest()){
                continue;
            }

            IClub playerFromClub = club;
            long clubUid = club.getEnterFromClubUid(iPlayer.getUid());
            if (clubUid != club.getClubUid()) {
                playerFromClub = ClubManager.I.getClubByUid(clubUid);
            }

            if (playerFromClub == null || !playerFromClub.hasGold(iPlayer.getUid(),this.getScore(this.getBankMinNote(this.getCowInfo().getKeepCount())))){
                continue;
            }

            result = i;
            break;
        }
        return result;
    }

    /**
     * 大局结束
     */
    @Override
    protected void gameOver(boolean next) {
        if (getRoomHandle() instanceof BoxArenaCowHotRoomHandle) {
            BoxArenaCowHotRoomHandle handle = (BoxArenaCowHotRoomHandle)getRoomHandle();
            handle.serviceCharge(false);
        }
    }

    /**
     * 处理庄家竞技分
     * @param value
     */
    private void doBankerGold(int value) {
        IBoxOwner boxOwner = getBoxOwner();
        if (null == boxOwner) {
            return;
        }
        if (-1 != this.bankerIndex) {
            IRoomPlayer bankerPlayer = this.allPlayer[this.bankerIndex];
            if (null != bankerPlayer) {
                IClub mainClub = (IClub) boxOwner;
                int realValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(bankerPlayer.getUid()), bankerPlayer.getUid(), value, 0);
                if (realValue != Math.abs(value)){
                    Logs.CLUB.error("cowHotRoom bank gold deal error roomId:%d bankerPlayerUid:%d value:%d realValue:%d",this.getRoomId(),bankerPlayer.getUid(),value,realValue);
                }
            }
        }
    }

    /**
     * 游戏分兑换竞技分
     * @return
     */
    @Override
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 * value / 10;
    }
    /**
     * 竞技分兑换游戏分
     * @return
     */
    public int getExchangeGoldForScore(long gold){
    	//gold:100,下注0.1(前端显示的)->1(传入的)
    	//gold:100,下注0.5(前端显示的)->5(传入的)
    	//gold:100,下注1.0(前端显示的)->10(传入的)
    	//gold 100/(1(传入的)*100/10)=10
    	//gold 100/(5(传入的)*100/10)=2
    	//gold 100/(10(传入的)*100/10)=1
//2020-08-24修改        return (int) (gold / (this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 /10));
    	Integer endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
    	return (int) (gold / (endPoint * 10));
    }

    @Override
    public int getCurPhase() {
        return this.curPhase.ordinal();
    }
    @Override
    public boolean isStart() {
        if (ERoomState.START == this.roomState.get()){
            if (getCurPhase() == EPhase.INIT.ordinal() || getCurPhase() == EPhase.READY.ordinal()){
                return false;
            }
            return true;
        }
        return false;
    }
}
