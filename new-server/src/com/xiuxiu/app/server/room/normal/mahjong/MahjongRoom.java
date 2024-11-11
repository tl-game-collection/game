package com.xiuxiu.app.server.room.normal.mahjong;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.*;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginInfoByMJ;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMyHandCardInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.*;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.mahjong.action.*;
import com.xiuxiu.app.server.room.player.mahjong.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.room.record.mahjong.BarScoreRecordAction;
import com.xiuxiu.app.server.room.record.mahjong.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong.RecordMahjongPlayerBriefInfo;
import com.xiuxiu.app.server.room.record.mahjong.WaitRecordAction;
import com.xiuxiu.core.Pair;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.*;
import java.util.Map.Entry;

public abstract class MahjongRoom extends Room implements IMahjongRoom {
    protected int crap1;                                                                                                // 骰子数
    protected int crap2;                                                                                                // 骰子数

    protected int joinBarCnt = 0;                                           // 连杠次数

    // 房间记录信息
    protected byte[] allTakeCard = new byte[MahjongConstant.MJ_CARD_KINDS];                                             // 所有打出的牌
    protected byte lastTakeCardValue = -1;                                                                              // 最后打出去的牌
    protected byte lastFumbleCardValue = -1;                                                                            // 最后一张摸到的牌
    protected int lastFumbleIndex = -1;                                                                                 // 最后一张摸到牌的玩家索引

    protected List<MahjongPlayer> liangPaiPlayer = new ArrayList<>();               // 亮牌玩家
    protected byte[] paoZi = new byte[MahjongConstant.MJ_CARD_KINDS];               // 炮子
    protected int[] paoZiCnt = new int[MahjongConstant.MJ_CARD_KINDS];              // 炮子数量/倍数
    protected byte[] allBrightCard = new byte[MahjongConstant.MJ_CARD_KINDS];       // 所有亮牌

    protected boolean switchBright = false;                                         // 亮牌开关
    protected boolean switchHalfBright = false;                                     // 半亮开关
    protected boolean switchOnlyZiMo = false;                                       // 只能自摸胡开关
    protected int minHuValue = 1;                                                   // 最小胡倍数

    // 房间规则
    protected int xuanPiaoType;                                                                                         // 选飘类型(1不漂,2每局选漂,3玩家定漂,4固定定漂)
    protected int xuanPiaoValue;                                                                                        // 固定定漂值
    protected int shuKanType;                                                                                           // 数坎类型

    protected int lastOPType = -1;//上一次操作类型  1.明杠 2.放杠 3.暗杠
    protected int last2OPType = -1;//上2次操作类型
    protected int curOPType = -1;//当前操作类型
    
    /** 玩家好牌 */
    private Map<Integer,LinkedList<Byte>> playerGoodCards = new HashMap<Integer, LinkedList<Byte>>();
    
    public MahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public MahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    public void init() {
        super.init();

        this.timeout = this.info.getRule().getOrDefault(RoomRule.RR_MJ_TIMEOUT, 0)*1000;
        if(this.timeout == 0){
            this.timeout = 9000;
            this.getRule().put(RoomRule.RR_MJ_TIMEOUT, 9);
        }
        this.xuanPiaoType = this.info.getRule().getOrDefault(RoomRule.RR_MJ_FLUTTER, 1);
        this.xuanPiaoValue = this.info.getRule().getOrDefault(RoomRule.RR_MJ_FLUTTER_VALUE, 0);
        this.shuKanType = this.info.getRule().getOrDefault(RoomRule.RR_MJ_SHU_KAN, 0);
    }


    /**
     * 好牌发牌处理
     */
    @Override
    protected void doDealGoodCards(Map<Integer,LinkedList<Byte>> goodCards) {
    	/*this.playerGoodCards.putAll(goodCards);
    	Iterator<Entry<Integer,LinkedList<Byte>>> entries = this.playerGoodCards.entrySet().iterator();
    	// 统计好牌数量
    	Map<Byte, Integer> cardCountMap = new HashMap<Byte, Integer>();
    	while (entries.hasNext()){
    		LinkedList<Byte> tempCards = entries.next().getValue();
			for (Byte card : tempCards) {
				if (cardCountMap.containsKey(card)) {
					cardCountMap.put(card, cardCountMap.get(card) + 1);
				} else {
					cardCountMap.put(card, 1);
				}
			}
		}
    	// 删除牌库中的好牌
    	Iterator<Entry<Byte, Integer>> it = cardCountMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Byte, Integer> cardEntry = it.next();
			int count = cardEntry.getValue();
			for (int i = 0; i < count; i++) {
				this.allCard.remove(cardEntry.getKey());
			}
		}*/
    }
    
    @Override
    protected void doDeal() {
        this.crap1 = RandomUtil.random(1, 6);
        this.crap2 = RandomUtil.random(1, 6);
        if (Switch.USE_SAME_CRAP) {
            this.crap1 = this.crap2;
        }
        if (-1 == this.bankerIndex||null == this.allPlayer[this.bankerIndex]) {
            do {
                this.bankerIndex = RandomUtil.random(0, this.playerNum - 1);
            } while (null == this.allPlayer[this.bankerIndex] || this.allPlayer[this.bankerIndex].isGuest());
        }

        for (int i = 0; i < 13; ++i) {
            for (int j = 0; j < this.playerNum; ++j) {
            	int index = (this.bankerIndex + j) % this.playerNum;
                MahjongPlayer player = (MahjongPlayer) this.allPlayer[index];
                if (null == player || player.isGuest()) {
                    continue;
                }
                if (!playerGoodCards.containsKey(index)) {
	                byte cardValue = this.allCard.removeFirst();
	                ++player.getHandCard()[cardValue];
                }
            }
        }
        
        // 给好牌玩家发牌
        if (!playerGoodCards.isEmpty()) {
        	Iterator<Entry<Integer, LinkedList<Byte>>> entries = playerGoodCards.entrySet().iterator();
        	while (entries.hasNext()){
        		Entry<Integer, LinkedList<Byte>> tempValue = entries.next();
        		int tempIndex = tempValue.getKey();
        		LinkedList<Byte> tempCards = tempValue.getValue();
        		MahjongPlayer player = (MahjongPlayer) this.allPlayer[tempIndex];
        		Iterator<Byte> tempIter = tempCards.iterator();
                while (tempIter.hasNext()) {
                    byte cardValue = tempIter.next();
                    ++player.getHandCard()[cardValue];
                }
        	}
        }

        MahjongPlayer bankerPlayer = (MahjongPlayer) this.allPlayer[this.bankerIndex];
        byte cardValue = this.allCard.removeFirst();
        this.lastFumbleCardValue = cardValue;
        ++bankerPlayer.getHandCard()[cardValue];

        this.doLaiZi();

        this.record = new MahjongRecord(this);

        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            this.record.addPlayer(new RecordMahjongPlayerBriefInfo(player.getPlayer(), player.getIndex(), player.getBureau(), player.getHandCard()));
        }

        this.sendMyCard();
    }

    /**
     * 处理癞子
     */
    protected void doLaiZi() {

    }


    @Override
    public void replaceHandCard(IRoomPlayer player, int card) {
        if (!Config.checkWhiteHas(player.getUid(), 1)) {
            return;
        }
        if (this.isOver) {
            return;
        }
        player.setScore(Score.MJ_NEXT_CARD, card, false);
    }

    protected void sendMyCard() {
        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[j];
            if (null == player || player.isGuest() || player.isOffline()) {
                continue;
            }
            if (Config.checkWhiteHas(player.getUid(), 1)) {
                PCLIRoomNtfMyHandCardInfo info = new PCLIRoomNtfMyHandCardInfo();
                info.rc.addAll(this.allCard);
                for (int i = 0; i < this.allPlayer.length; i++) {
                    IMahjongPlayer m_player = (IMahjongPlayer) this.allPlayer[i];
                    if (null == m_player || m_player.isGuest() || m_player.isOffline()) {
                        continue;
                    }
                    if (player.getUid() == m_player.getUid()) {
                        continue;
                    }
                    List<Byte> m_handCards = new ArrayList<>();
                    m_player.addHandCardTo(m_handCards);
                    info.ohc.put(m_player.getUid(),m_handCards);
                }
                player.send(CommandId.CLI_NTF_ROOM_MY_CARD, info);
            }
        }
    }

    /**
     * 开始数坎
     */
    protected void beginShuKan() {
        if (0 != (this.shuKanType & EShuKanType.POINT.getValue())||(0 != (this.shuKanType & EShuKanType.FIRST_POINT.getValue())&&this.curBureau==1)) {
            MahjongShuKanWaitAction shuKanWaitAction = new MahjongShuKanWaitAction(this, null, -1 == this.timeout ? MahjongConstant.MJ_ROOM_SHUKAN_DEFAULT_WAIT_TIME : MahjongConstant.MJ_ROOM_SHUKAN_WAIT_TIME);
            for (int i = 0; i < this.playerNum; ++i) {
                MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                shuKanWaitAction.addPlayer(temp.getUid());
            }
            this.addAction(shuKanWaitAction);
            return;
        }
        this.endShuKan();
    }

    /**
     * 结束数坎
     */
    public void endShuKan() {

    }

    @Override
    public ErrorCode shuKan(IPlayer player,List<Integer> point) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法数坎", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法数坎", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (0 != (this.shuKanType & EShuKanType.POINT.getValue()) ||0 != (this.shuKanType & EShuKanType.FIRST_POINT.getValue())) {
            for (int i = 0; i < point.size(); i++) {
                if (point.get(i) < 0 || point.get(i) > 9) {
                    Logs.ROOM.warn("%s %s 无效数据, 无法数坎", this, player);
                    return ErrorCode.REQUEST_INVALID;
                }
            }
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongShuKanWaitAction) {
            Logs.ROOM.debug("%s %s 数坎中", this, player);
            MahjongPlayer roomPlayer= (MahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == roomPlayer || roomPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法数坎", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongShuKanWaitAction) action).playerSelect(player.getUid(), point);
            if (ErrorCode.OK == err) {
                roomPlayer.setShuKanPoint(point);
                PCLIMahjongNtfShuKanValueInfo valueInfo = new PCLIMahjongNtfShuKanValueInfo();
                valueInfo.playerUid = player.getUid();
                valueInfo.value =roomPlayer.getShuKanPoint();
                this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SHU_KAN_VALUE, valueInfo);
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s %s 不是数坎动作, 无法数坎", this, player);
        return ErrorCode.REQUEST_INVALID;
    }


    /**
     * 开始选飘
     */
    protected void beginXuanPiao() {
        // 每局选漂为玩家自行在每局开始时进行选项，确定加漂多少（分类选项：麻将 0/1/2 跑得快0/1/2/3）
        // 玩家定漂为玩家自行在第一局开始时进行选项，确定之后所有局数加漂多少（分类选项：麻将 0/1/2 跑得快0/1/2/3）
        // 固定定漂为创建玩法或房间的玩家选择的固定的加漂数，之后进行游戏的所有玩家固定加漂
        // (1不漂,2每局选漂,3玩家定漂,4固定定漂)
        if (this.xuanPiaoType > 1) {
            // 2每局选漂
            if (this.xuanPiaoType == 2) {
                MahjongFlutterWaitAction flutterWaitAction = null;
                for (int i = 0; i < this.playerNum; ++i) {
                    MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    if (null == flutterWaitAction) {
                        flutterWaitAction = new MahjongFlutterWaitAction(this, null, -1 == this.timeout ? MahjongConstant.MJ_ROOM_FLUTTER_DEFAULT_WAIT_TIME : MahjongConstant.MJ_ROOM_FLUTTER_WAIT_TIME);
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
                MahjongFlutterWaitAction flutterWaitAction = null;
                List<Integer> playerIndexList = null;
                for (int i = 0; i < this.playerNum; ++i) {
                    MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    if (null == flutterWaitAction) {
                        flutterWaitAction = new MahjongFlutterWaitAction(this, null, -1 == this.timeout ? MahjongConstant.MJ_ROOM_FLUTTER_DEFAULT_WAIT_TIME : MahjongConstant.MJ_ROOM_FLUTTER_WAIT_TIME);
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
                        MahjongPlayer temp = (MahjongPlayer) this.allPlayer[playerIndex];
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
                    MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                    if (null == temp || temp.isGuest()) {
                        continue;
                    }
                    temp.setPiaoScore(xuanPiaoValue);
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

    /**
     * 结束选飘
     */
    public void endXuanPiao() {

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
        if (action instanceof MahjongFlutterWaitAction) {
            Logs.ROOM.debug("%s %s 选飘中", this, player);
            MahjongPlayer roomPlayer = (MahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == roomPlayer || roomPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法选飘", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongFlutterWaitAction) action).playerSelect(player.getUid(), value);
            if (ErrorCode.OK == err) {
                roomPlayer.setPiaoScore(value);
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

    /**
     * 开始换牌
     */
    protected void beginHuanPai() {
        PCLIMahjongNtfBeginHuanPai info = new PCLIMahjongNtfBeginHuanPai();
        MahjongHuanPaiWaitAction huanPaiWaitAction = new MahjongHuanPaiWaitAction(this, null, -1 == this.timeout ? MahjongConstant.MJ_ROOM_HUAN_PAI_DEFAULT_WAIT_TIME : MahjongConstant.MJ_ROOM_HUAN_PAI_WAIT_TIME);
        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            huanPaiWaitAction.addPlayer(temp.getUid());
        }
        this.addAction(huanPaiWaitAction);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_HUAN_PAI, info,false);
    }

    /**
     * 结束换牌
     */
    public void endHuanPai() {

    }

    @Override
    public ErrorCode huanPai(IPlayer player, List<Byte> cards) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法换牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法换牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongHuanPaiWaitAction) {
            MahjongPlayer roomPlayer= (MahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == roomPlayer || roomPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法换牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongHuanPaiWaitAction) action).huanPai(player.getUid(), cards);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是换牌动作, 无法换牌", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 开始甩牌
     */
    protected void beginShuaiPai() {
        PCLIMahjongNtfBeginShuaiPai info = new PCLIMahjongNtfBeginShuaiPai();
        MahjongShuaiPaiWaitAction shuaiPaiWaitAction = new MahjongShuaiPaiWaitAction(this, null, -1 == this.timeout ? MahjongConstant.MJ_ROOM_SHUAI_PAI_DEFAULT_WAIT_TIME : MahjongConstant.MJ_ROOM_SHUAI_PAI_WAIT_TIME);
        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            shuaiPaiWaitAction.addPlayer(temp.getUid());
        }
        this.addAction(shuaiPaiWaitAction);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_SHUAI_PAI, info);
    }

    /**
     * 结束算牌
     */
    public void endShuaiPai() {

    }

    @Override
    public ErrorCode shuaiPai(IPlayer player, List<Byte> cards) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法甩牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法甩牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongShuaiPaiWaitAction) {
            MahjongPlayer roomPlayer= (MahjongPlayer) this.getRoomPlayer(player.getUid());
            if (null == roomPlayer || roomPlayer.isGuest()) {
                Logs.ROOM.warn("%s %s 观察者, 无法甩牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ErrorCode err = ((MahjongShuaiPaiWaitAction) action).shuaiPai(player.getUid(), cards);
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是甩牌动作, 无法甩牌", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 开始定缺
     */
    public void beginDingQue() {
        PCLIMahjongNtfBeginDingQue info = new PCLIMahjongNtfBeginDingQue();
        MahjongDingQueWaitAction dingQueWaitAction = new MahjongDingQueWaitAction(this, null, -1 == this.timeout ? MahjongConstant.MJ_ROOM_DING_QUE_DEFAULT_WAIT_TIME : MahjongConstant.MJ_ROOM_DING_QUE_WAIT_TIME);
        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            dingQueWaitAction.addPlayer(temp.getUid());
        }
        this.addAction(dingQueWaitAction);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BEGIN_DING_QUE, info);
    }

    /**
     * 结束定缺
     */
    public void endDingQue() {

    }

    @Override
    public ErrorCode dingQue(IPlayer player, int color) {
        return null;
    }

    /**
     * 开始打牌
     */
    protected void doStartTake() {
        MahjongPlayer player = (MahjongPlayer) this.allPlayer[this.bankerIndex];
        MahjongTakeAction action = this.takeAction(player, this.lastFumbleCardValue);

        PCLIMahjongNtfCanBrightInfo canBrightInfo = new PCLIMahjongNtfCanBrightInfo();
        canBrightInfo.liangPai = action.isBright();
        if (action.isTing()) {
            canBrightInfo.brightInfo = player.getBrightInfo().to();
        }
        player.send(CommandId.CLI_NTF_MAHJONG_START_TAKE, canBrightInfo);
    }

    @Override
    public ErrorCode fumble(IPlayer player) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法摸牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法摸牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongFumbleAction) {
            if (player.getUid() != ((MahjongFumbleAction) action).getRoomPlayer().getUid()) {
                Logs.ROOM.warn("%s 当前轮摸牌人是:%s 而不是你:%s 无效摸牌", this, ((MahjongFumbleAction) action).getRoomPlayer(), player);
                return ErrorCode.REQUEST_INVALID;
            }
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s 本轮不是摸牌动作, 无法摸牌", this);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 摸牌
     * @param player
     * @param auto
     */
    public void onFumble(MahjongPlayer player, boolean auto) {
        byte card = -1;
        if (Config.checkWhiteHas(player.getUid(),1)) {
            card = (byte) player.getScore(Score.MJ_NEXT_CARD, false);
            player.setScore(Score.MJ_NEXT_CARD, -1, false);
        }
        if (-1 != card) {
            if (!this.allCard.remove((Byte) card)) {
                card = this.allCard.removeFirst();
            }
        } else {
            card = this.allCard.removeFirst();
        }

        this.lastFumbleCardValue = card;
        player.fumble(card);
        this.lastFumbleIndex = player.getIndex();
        Logs.ROOM.debug("%s %s 摸牌 card: %s", this, player, MahjongConstant.CARDS[card]);

        MahjongTakeAction action = this.takeAction(player, card);

        ((MahjongRecord) this.record).addFumbleRecordAction(player.getUid(), card, auto, action.isHu(), null != action.getBar(), action.isBright(), action.isTing() ? player.getBrightInfo().copy() : null);

        PCLIMahjongNtfFumbleInfo mahjongFubleInfo = new PCLIMahjongNtfFumbleInfoByKWX(player.getUid(), player.getIndex(), card, this.allCard.size(), auto, action.isBright(), action.isTing() ? player.getBrightInfo().to() : null);
        for (byte i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (player.getHandCard()[i] < 1) {
                continue;
            }
            for (byte j = (byte) (player.getBrightCardCnt()[i] + (i == card ? 1 : 0)), len = player.getHandCard()[i]; j < len; ++j) {
                mahjongFubleInfo.handCard.add(i);
            }
        }
        player.send(CommandId.CLI_NTF_MAHJONG_FUMBLE, mahjongFubleInfo);
        mahjongFubleInfo = new PCLIMahjongNtfFumbleInfoByKWX(player.getUid(), player.getIndex(), (byte)-1, this.allCard.size(), false, false, null);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_FUMBLE, mahjongFubleInfo, player.getUid());

        if (this.allCard.isEmpty()) {
            Logs.ROOM.debug("%s %s 最后一张牌只能自摸或打掉", this, player);
            // 最后一张牌
            action.setBar(null);
            if (action.isHu()) {
                action.setOp(EActionOp.HU);
            }
            //action.setTimeout(2000);
        }
        this.sendMyCard();

        this.last2OPType = this.lastOPType;
        this.lastOPType = this.curOPType;
        this.curOPType = -1;
    }

    @Override
    public ErrorCode take(IPlayer player, byte cardValue, byte last, byte index, byte outputCardIndex, int length) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法出牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法出牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongTakeAction) {
            if (player.getUid() != ((MahjongTakeAction) action).getRoomPlayer().getUid()) {
                Logs.ROOM.warn("%s 当前打牌牌人是:%s 而不是你:%s 无效打牌", this, ((MahjongTakeAction) action).getRoomPlayer(), player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (cardValue < 0 || cardValue >= MahjongConstant.MJ_CARD_KINDS) {
                Logs.ROOM.warn("%s 打牌子非法:%d 无效打牌", this, cardValue, ((MahjongTakeAction) action).getRoomPlayer(), player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (((MahjongTakeAction) action).getRoomPlayer().getHandCard()[cardValue] < 1) {
                Logs.ROOM.warn("%s %s 打牌:%s 手牌不足 无效打牌", this, ((MahjongTakeAction) action).getRoomPlayer(), MahjongConstant.CARDS[cardValue]);
                return ErrorCode.REQUEST_INVALID;
            }
            if (!this.checkCanTake(((MahjongTakeAction) action).getRoomPlayer(), cardValue)) {
                Logs.ROOM.warn("%s %s 打牌:%s 手牌不足 无效打牌", this, ((MahjongTakeAction) action).getRoomPlayer(), MahjongConstant.CARDS[cardValue]);
                return ErrorCode.REQUEST_INVALID;
            }
            if (((MahjongTakeAction) action).getRoomPlayer().isBright()) {
                Logs.ROOM.warn("%s %s 打牌:%s 亮牌玩家无法打牌只能系统自动打 无效打牌", this, ((MahjongTakeAction) action).getRoomPlayer(), MahjongConstant.CARDS[cardValue]);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.MUST_BAR == ((MahjongTakeAction) action).getOp()) {
                Logs.ROOM.warn("%s %s 打牌:%s 必须杠 无效打牌", this, ((MahjongTakeAction) action).getRoomPlayer(), MahjongConstant.CARDS[cardValue]);
                return ErrorCode.REQUEST_INVALID;
            }
            ((MahjongTakeAction) action).getRoomPlayer().clearOperationTimeoutCnt();
            ((MahjongTakeAction) action).setCardValue(cardValue);
            ((MahjongTakeAction) action).setLast(last);
            ((MahjongTakeAction) action).setIndex(index);
            ((MahjongTakeAction) action).setOutputCardIndex(outputCardIndex);
            ((MahjongTakeAction) action).setLength(length);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s %s 无效打牌", this, player);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 打牌
     * @param takePlayer
     * @param cardValue
     * @param last
     * @param index
     * @param outputCardIndex
     * @param length
     * @param auto
     */
    public void onTake(MahjongPlayer takePlayer, byte cardValue, byte last, byte index, byte outputCardIndex, int length, boolean auto) {
        takePlayer.takeCard(cardValue);
        ++this.allTakeCard[cardValue];
        this.lastTakeCardValue = cardValue;
        Logs.ROOM.debug("%s %s 打牌 card:%d last:%d index:%d outputCardIndex:%d length:%d", this, takePlayer, cardValue, last, index, outputCardIndex, length);

        ((MahjongRecord) this.record).addTakeRecordAction(takePlayer.getUid(), cardValue, last, index, outputCardIndex, length, auto);

        takePlayer.setLouHu(false);

        if (auto) {
            PCLIMahjongNtfTakeInfo mahjongTakeInfo = new PCLIMahjongNtfTakeInfo(takePlayer.getUid(), cardValue, last, outputCardIndex, length, index, auto);
            for (byte i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                if (takePlayer.getHandCard()[i] < 1) {
                    continue;
                }
                for (byte j = takePlayer.getBrightCardCnt()[i], len = takePlayer.getHandCard()[i]; j < len; ++j) {
                    mahjongTakeInfo.myHandCard.add(i);
                }
            }
            mahjongTakeInfo.myDeskCard.addAll(takePlayer.getDeskCards());
            takePlayer.send(CommandId.CLI_NTF_MAHJONG_TAKE, mahjongTakeInfo);
            mahjongTakeInfo = new PCLIMahjongNtfTakeInfo(takePlayer.getUid(), cardValue, last, outputCardIndex, length, index, auto);
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_TAKE, mahjongTakeInfo, takePlayer.getUid());
        } else {
            PCLIMahjongNtfTakeInfo mahjongTakeInfo = new PCLIMahjongNtfTakeInfo(takePlayer.getUid(), cardValue, last, outputCardIndex, length, index, auto);
            this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_TAKE, mahjongTakeInfo);
        }

        MahjongWaitAction waitAction = this.checkWait(takePlayer, cardValue, MahjongConstant.MJ_HU_TYPE_NONE);

        if (null == waitAction) {
            this.joinBarCnt = 0;
            // 没有等待
            this.canFumble((MahjongPlayer) this.getNextRoomPlayer(takePlayer.getIndex()), false);
        } else {
            this.addAction(waitAction);
        }

        this.last2OPType = this.lastOPType;
        this.lastOPType = this.curOPType;
        this.curOPType = -1;
    }

    @Override
    public ErrorCode bright(IPlayer player, List<Byte> kou, byte takeCard, byte takeCardIndex) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法亮牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法亮牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongTakeAction) {
            if (player.getUid() != ((MahjongTakeAction) action).getRoomPlayer().getUid()) {
                Logs.ROOM.warn("%s 当前打牌牌人是:%s 而不是你:%s 无效亮牌", this, ((MahjongTakeAction) action).getRoomPlayer(), player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (takeCard < 0 || takeCard >= MahjongConstant.MJ_CARD_KINDS) {
                Logs.ROOM.warn("%s 亮牌非法:%d 无效打牌", this, takeCard, ((MahjongTakeAction) action).getRoomPlayer(), player);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            if (((MahjongTakeAction) action).getRoomPlayer().getHandCard()[takeCard] < 1) {
                Logs.ROOM.warn("%s %s 打牌:%s 手牌不足 无效亮牌", this, ((MahjongTakeAction) action).getRoomPlayer(), MahjongConstant.CARDS[takeCard]);
                return ErrorCode.REQUEST_INVALID;
            }
            if (!((MahjongTakeAction) action).isBright()) {
                Logs.ROOM.warn("%s %s 不能亮牌 无效亮牌", this, ((MahjongTakeAction) action).getRoomPlayer());
                return ErrorCode.REQUEST_INVALID;
            }
            ((MahjongTakeAction) action).getRoomPlayer().clearOperationTimeoutCnt();
            ((MahjongTakeAction) action).setOp(EActionOp.BRIGHT);
            ((MahjongTakeAction) action).setKou(kou);
            ((MahjongTakeAction) action).setCardValue(takeCard);
            ((MahjongTakeAction) action).setIndex(takeCardIndex);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s %s 无效打牌", this, player);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 亮牌
     * @param takePlayer
     * @param kou
     * @param takeCard
     */
    public void onBright(MahjongPlayer takePlayer, List<Byte> kou, byte takeCard, byte takeCardIndex) {
        BrightInfo brightInfo = null;
        List<Byte> realKou = null == kou || kou.isEmpty() ? Collections.EMPTY_LIST : new ArrayList<>();
        List<BrightInfo> child = takePlayer.getBrightInfo().child;
        if (kou != null) {
            for (int i = 0, len = kou.size(); i < len; ++i) {
                Byte k = kou.get(i);
                for (int j = 0, len2 = child.size(); j < len2; ++j) {
                    if (k == child.get(j).kou) {
                        realKou.add(k);
                        brightInfo = child.get(j);
                        child = brightInfo.child;
                        break;
                    }
                }
            }
        }
        if (null != brightInfo) {
            takePlayer.setHalfBrightInfo(brightInfo.tingInfo.get(takeCard));
        }
        if (null == takePlayer.getHalfBrightInfo()) {
            Logs.ROOM.error("%s %s 亮牌信息为空, 扣:%s 打牌:%d, 原始亮牌信息:%s", this, takePlayer, kou, takeCard, takePlayer.getBrightInfo());
            MahjongTakeAction action = new MahjongTakeAction(this, takePlayer, takePlayer.getTimeout(this.timeout));
            this.addAction(action);
            return;
        }
        takePlayer.setBright(true);
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            takePlayer.getBrightCardCnt()[i] = takePlayer.getHandCard()[i];
        }
        --takePlayer.getBrightCardCnt()[takeCard];
        for (int i = 0, len = realKou.size(); i < len; ++i) {
            Byte k = kou.get(i);
            takePlayer.getBrightCardCnt()[k] -= 3;
            takePlayer.getKouCard()[k] = 1;
        }

        this.liangPaiPlayer.add(takePlayer);
        PCLIMahjongNtfBrightInfo ntfBrightInfo = new PCLIMahjongNtfBrightInfo();
        ntfBrightInfo.uid = takePlayer.getUid();
        ntfBrightInfo.takeCard = takeCard;
        ntfBrightInfo.takeCardIndex = takeCardIndex;
        if (null != takePlayer.getHalfBrightInfo()) {
            Iterator<Map.Entry<Byte, Integer>> it = takePlayer.getHalfBrightInfo().huCard.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Byte, Integer> entry = it.next();
                this.paoZi[entry.getKey()] = 1;
                this.paoZiCnt[entry.getKey()] = entry.getValue();
                ntfBrightInfo.ting.put(entry.getKey(), entry.getValue());
            }
            if (this.switchHalfBright) {
                HashSet<Pair> tempPair = new HashSet<>();
                int i = 0;
                Iterator<Map.Entry<Byte, HashSet<Pair>>> it4 = takePlayer.getHalfBrightInfo().split.entrySet().iterator();
                while (it4.hasNext()) {
                    Map.Entry<Byte, HashSet<Pair>> entry = it4.next();
                    if (0 == i) {
                        tempPair.addAll(entry.getValue());
                    } else {
                        Iterator<Pair> it5 = tempPair.iterator();
                        while (it5.hasNext()) {
                            Pair p1 = it5.next();
                            boolean have = false;
                            Iterator<Pair> it6 = entry.getValue().iterator();
                            while (it6.hasNext()) {
                                Pair p2 = it6.next();
                                if (p1.equals(p2)) {
                                    it6.remove();
                                    have = true;
                                    break;
                                }
                            }
                            if (!have) {
                                it5.remove();
                            }
                        }
                    }
                    ++i;
                }
                Iterator<Pair> it5 = tempPair.iterator();
                while (it5.hasNext()) {
                    Pair pair = it5.next();
                    if (-1 != pair.a) {
                        --takePlayer.getBrightCardCnt()[pair.a];
                    }
                    if (-1 != pair.b) {
                        --takePlayer.getBrightCardCnt()[pair.b];
                    }
                    if (-1 != pair.c) {
                        --takePlayer.getBrightCardCnt()[pair.c];
                    }
                }
            }
        }
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (takePlayer.getBrightCardCnt()[i] > 0) {
                this.allBrightCard[i] += takePlayer.getBrightCardCnt()[i];
                for (int j = 0; j < takePlayer.getBrightCardCnt()[i]; ++j) {
                    ntfBrightInfo.bright.add((byte) i);
                }
            }
        }

        ((MahjongRecord) this.record).addBrightRecordAction(takePlayer.getUid(), realKou, ntfBrightInfo.bright, ntfBrightInfo.ting, takeCard, takeCardIndex);

        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BRIGHT, ntfBrightInfo);

        takePlayer.takeCard(takeCard);
        ++this.allTakeCard[takeCard];
        this.lastTakeCardValue = takeCard;
        Logs.ROOM.debug("%s %s 亮牌打牌 card:%d", this, takePlayer, takeCard);

        takePlayer.setLouHu(false);

        MahjongWaitAction waitAction = this.checkWait(takePlayer, takeCard, MahjongConstant.MJ_HU_TYPE_NONE);

        if (null == waitAction) {
            this.joinBarCnt = 0;
            // 没有等待
            this.canFumble((MahjongPlayer) this.getNextRoomPlayer(takePlayer.getIndex()), false);
        } else {
            this.addAction(waitAction);
        }
    }

    @Override
    public ErrorCode bump(IPlayer player, byte index) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法碰牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法碰牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            Logs.ROOM.debug("%s %s 吃碰杠胡中断中", this, player);
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction) action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s %s 没有碰中断", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.op) {
                Logs.ROOM.warn("%s %s 已经操作过了 op:%s", this, player, waitInfo.op);
                return ErrorCode.REQUEST_INVALID;
            }
            if (!waitInfo.bump) {
                Logs.ROOM.warn("%s %s 不能碰操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.eat, waitInfo.bump, waitInfo.bar, waitInfo.hu);
                return ErrorCode.REQUEST_INVALID;
            }
            waitInfo.roomPlayer.clearOperationTimeoutCnt();
            waitInfo.op = EActionOp.BUMP;
            waitInfo.index = index;
            if (((MahjongWaitAction) action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s %s 无效碰, 碰不能自碰", this, player);
        return ErrorCode.REQUEST_INVALID;
    }

    /**
     * 碰牌
     * @param takePlayer
     * @param bumpPlayer
     * @param cardValue
     * @param index
     */
    public void onBump(MahjongPlayer takePlayer, MahjongPlayer bumpPlayer, byte cardValue, byte index) {
        this.joinBarCnt = 0;
        bumpPlayer.bumpCard(cardValue);
        takePlayer.getDeskCards().removeLast();
        this.allTakeCard[cardValue] += 2;
        Logs.ROOM.debug("%s 碰的人:%s 打牌的人:%s 碰牌 cardValue:%s index:%d", this, bumpPlayer, takePlayer, cardValue, index);
        //HashMap<Byte, HashMap<Byte, Integer>> tingInfo = this.getCanTingInfo(bumpPlayer);
        boolean bright = false;
        boolean ting = false;
        if (this.switchBright && !bumpPlayer.isBright()) {
            BrightInfo brightInfo = this.getBrightInfo(bumpPlayer);
            bumpPlayer.setBrightInfo(brightInfo);
            bright = bumpPlayer.isBright() ? false : this.isBright(brightInfo.child.size() > 0);
            ting = true;
        }
        if (!this.switchBright) {
            BrightInfo brightInfo = this.getBrightInfo(bumpPlayer);
            bumpPlayer.setBrightInfo(brightInfo);
            ting = true;
        }

        ((MahjongRecord) this.record).addBumpRecordAction(bumpPlayer.getUid(), takePlayer.getUid(), cardValue, index, bright, ting ? bumpPlayer.getBrightInfo().copy() : null);

        PCLIMahjongNtfBumpInfoByKWX mahjongBumpInfo = new PCLIMahjongNtfBumpInfoByKWX(bumpPlayer.getUid(), takePlayer.getUid(), cardValue, index, bright, ting ? bumpPlayer.getBrightInfo().to() : null);
        bumpPlayer.send(CommandId.CLI_NTF_MAHJONG_BUMP_OK, null);
        bumpPlayer.send(CommandId.CLI_NTF_MAHJONG_BUMP, mahjongBumpInfo);

        mahjongBumpInfo = new PCLIMahjongNtfBumpInfoByKWX(bumpPlayer.getUid(), takePlayer.getUid(), cardValue, index, false, null);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BUMP, mahjongBumpInfo, bumpPlayer.getUid());

        List<Byte> bar = this.barOnBump(bumpPlayer, cardValue);
        MahjongTakeAction action = new MahjongTakeAction(this, bumpPlayer, this.getTimeout(bumpPlayer, false, false, bright));
        action.setBright(bright);
        action.setTing(ting);
        action.setBar(bar);
        this.addAction(action);

        this.checkChangeLiangPaiTingCnt(bumpPlayer, cardValue);

        this.last2OPType = this.lastOPType;
        this.lastOPType = this.curOPType;
        this.curOPType = -1;
    }

    @Override
    public ErrorCode bar(IPlayer player, byte cardValue, byte startIndex, byte endIndex, byte insertIndex) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法杠牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法杠牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            Logs.ROOM.debug("%s %s 吃碰杠胡中断中", this, player);
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction) action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s %s 没有碰中断", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.op) {
                Logs.ROOM.warn("%s %s 已经操作过了 op:%s", this, player, waitInfo.op);
                return ErrorCode.REQUEST_INVALID;
            }
            if (null == waitInfo.bar) {
                Logs.ROOM.warn("%s %s 不能杠操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.eat, waitInfo.bump, waitInfo.bar, waitInfo.hu);
                return ErrorCode.REQUEST_INVALID;
            }
            if (-1 == waitInfo.bar.indexOf(cardValue)) {
                Logs.ROOM.warn("%s %s 不能杠操作2 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.eat, waitInfo.bump, waitInfo.bar, waitInfo.hu);
                return ErrorCode.REQUEST_INVALID;
            }
            waitInfo.roomPlayer.clearOperationTimeoutCnt();
            waitInfo.op = EActionOp.BAR;
            waitInfo.index = startIndex;
            waitInfo.endIndex = endIndex;
            waitInfo.insertIndex = insertIndex;
            if (((MahjongWaitAction) action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        if (action instanceof MahjongTakeAction) {
            if (null == ((MahjongTakeAction) action).getBar()) {
                Logs.ROOM.warn("%s %s 不能杠操作", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (-1 == ((MahjongTakeAction) action).getBar().indexOf(cardValue)) {
                Logs.ROOM.warn("%s %s 不能杠操作, 非法杠牌", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (cardValue < 0 || cardValue >= MahjongConstant.MJ_CARD_KINDS) {
                Logs.ROOM.warn("%s 杠非法:%d 无效打牌", this, cardValue, ((MahjongTakeAction) action).getRoomPlayer(), player);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            if (EActionOp.MUST_BAR == ((MahjongTakeAction) action).getOp()) {
                if (cardValue != ((MahjongTakeAction) action).getCardValue()) {
                    Logs.ROOM.warn("%s %s 不能杠操作, 非法杠牌", this, player);
                    return ErrorCode.REQUEST_INVALID;
                }
            }
            ((MahjongTakeAction) action).setOp(EActionOp.BAR);
            ((MahjongTakeAction) action).setCardValue(cardValue);
            ((MahjongTakeAction) action).setStartIndex(startIndex);
            ((MahjongTakeAction) action).setEndIndex(endIndex);
            ((MahjongTakeAction) action).setInsertIndex(insertIndex);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s %s 无效杠, 不能杠操作", this, player);
        return ErrorCode.REQUEST_INVALID;
    }

    public void onBar(MahjongPlayer takePlayer, MahjongPlayer barPlayer, byte cardValue, byte startIndex, byte endIndex, byte insertIndex, List<Byte> bar) {
        EBarType type = EBarType.NONE;
        this.last2OPType = this.lastOPType;
        this.lastOPType = this.curOPType;
        do {
            if (takePlayer.getUid() == barPlayer.getUid()) {
                // 自己杠
                type = 3 != barPlayer.getPgCardCnt()[cardValue] ? EBarType.BAR_AN : EBarType.BAR_MING;
                if (!this.isJoinBar(cardValue, EBarType.BAR_AN == type)) {
                    this.joinBarCnt = 0;
                }
                barPlayer.getHandCard()[cardValue] = 0;
                if (EBarType.BAR_MING == type) {
                    this.curOPType = 1;
                    // 明杠
                    ((MahjongRecord) this.record).addBarRecordAction(barPlayer.getUid(), takePlayer.getUid(), cardValue, EBarType.BAR_MING, startIndex, endIndex, insertIndex);
                    MahjongWaitAction waitAction = this.checkWait(barPlayer, cardValue, MahjongConstant.MJ_HU_TYPE_BAR);
                    if (null != waitAction) {
                        //this.addAction(waitAction);
                        Logs.ROOM.debug("%s 可以抢杠胡", this);
                        break;
                    }
                } else {
                    ((MahjongRecord) this.record).addBarRecordAction(barPlayer.getUid(), takePlayer.getUid(), cardValue, EBarType.BAR_AN, startIndex, endIndex, insertIndex);
                    this.curOPType = 3;
                }
                barPlayer.getPgCardCnt()[cardValue] = 4;
                boolean add = true;
                for (MahjongCardNode node : barPlayer.getCpgCards()) {
                    if (node.card == cardValue && MahjongConstant.MJ_NODE_TYPE_BUMP == node.type) {
                        add = false;
                        node.type = MahjongConstant.MJ_NODE_TYPE_BAR;
                        break;
                    }
                }
                if (add) {
                    barPlayer.getCpgCards().addLast(new MahjongCardNode(MahjongConstant.MJ_NODE_TYPE_BAR_DARK, cardValue));
                }
                this.calcGangScore(takePlayer, barPlayer, EBarType.BAR_AN == type ? MahjongConstant.MJ_BAR_TYPE_AN : MahjongConstant.MJ_BAR_TYPE_MING);
            } else {
                type = EBarType.BAR_FANG;
                // 放杠
                ((MahjongRecord) this.record).addBarRecordAction(barPlayer.getUid(), takePlayer.getUid(), cardValue, EBarType.BAR_FANG, startIndex, endIndex, insertIndex);
                barPlayer.getHandCard()[cardValue] = 0;
                barPlayer.getPgCardCnt()[cardValue] = 4;
                takePlayer.getDeskCards().removeLast();
                barPlayer.getCpgCards().addLast(new MahjongCardNode(MahjongConstant.MJ_NODE_TYPE_BAR, cardValue));
                this.calcGangScore(takePlayer, barPlayer, MahjongConstant.MJ_BAR_TYPE_FANG);

                this.curOPType = 2;
            }
            this.allTakeCard[cardValue] = 4;
            Logs.ROOM.debug("%s 杠的人:%s 打牌的人:%s 碰牌 cardValue:%s startIndex:%d endIndex:%d insertIndex:%d", this, barPlayer, takePlayer, cardValue, startIndex, endIndex, insertIndex);
            this.canFumble(barPlayer, true);
            this.checkChangeLiangPaiTingCnt(barPlayer, cardValue);
        } while (false);

        PCLIMahjongNtfBarInfo mahjongBarInfo = new PCLIMahjongNtfBarInfo();
        mahjongBarInfo.uid = barPlayer.getUid();
        mahjongBarInfo.cardValue = cardValue;
        mahjongBarInfo.takeUid = takePlayer.getUid();
        mahjongBarInfo.startIndex = startIndex;
        mahjongBarInfo.endIndex = endIndex;
        mahjongBarInfo.insertIndex = insertIndex;
        mahjongBarInfo.type = type.ordinal();
        barPlayer.send(CommandId.CLI_NTF_MAHJONG_BAR_OK, null);
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_BAR, mahjongBarInfo);

        //抢杠胡
        if (EBarType.BAR_MING == type) {
            MahjongWaitAction waitAction = this.checkWait(barPlayer, cardValue, MahjongConstant.MJ_HU_TYPE_BAR);
            if (null != waitAction) {
                this.addAction(waitAction);
            }
        }
    }

    @Override
    public ErrorCode eat(IPlayer player, byte cardValue) {
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode hu(IPlayer player) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法胡牌", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法胡牌", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            Logs.ROOM.debug("%s %s 吃碰杠胡中断中", this, player);
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction) action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s %s 没有碰中断", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.op) {
                Logs.ROOM.warn("%s %s 已经操作过了 op:%s", this, player, waitInfo.op);
                return ErrorCode.REQUEST_INVALID;
            }
            if (!waitInfo.hu) {
                Logs.ROOM.warn("%s %s 不能胡操作 吃:%s 碰:%s 杠:%s 胡:%s", this, player, waitInfo.eat, waitInfo.bump, waitInfo.bar, waitInfo.hu);
                return ErrorCode.REQUEST_INVALID;
            }
            waitInfo.roomPlayer.clearOperationTimeoutCnt();
            waitInfo.op = EActionOp.HU;
            if (((MahjongWaitAction) action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        if (action instanceof MahjongTakeAction) {
            if (!((MahjongTakeAction) action).isHu()) {
                Logs.ROOM.warn("%s %s 不能胡操作", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            ((MahjongTakeAction) action).setOp(EActionOp.HU);
            this.tick();
            return ErrorCode.OK;
        }
        Logs.ROOM.warn("%s %s 无效杠, 不能胡操作", this, player);
        return ErrorCode.REQUEST_INVALID;
    }

    public abstract void onHu(MahjongPlayer takeRoomPlayer, MahjongPlayer hu1RoomPlayer, MahjongPlayer hu2RoomPlayer, MahjongPlayer hu3RoomPlayer, byte huCard, int huType);

    // 荒庄
    public abstract void onHuangZhuang(boolean next);

    // 跳过
    @Override
    public ErrorCode pass(IPlayer player) {
        if (ERoomState.START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法跳过", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法跳过", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof MahjongWaitAction) {
            Logs.ROOM.debug("%s %s 吃碰杠胡中断中", this, player);
            MahjongWaitAction.WaitInfo waitInfo = ((MahjongWaitAction) action).getWaitInfo(player.getUid());
            if (null == waitInfo) {
                Logs.ROOM.warn("%s %s 没有碰中断", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
            if (EActionOp.NORMAL != waitInfo.op) {
                Logs.ROOM.warn("%s %s 已经操作过了 op:%s", this, player, waitInfo.op);
                return ErrorCode.REQUEST_INVALID;
            }
            if (waitInfo.roomPlayer.isBright()) {
                // 亮 胡不能跳过
                if (waitInfo.hu) {
                    Logs.ROOM.warn("%s %s 亮到必胡, 不能跳过", this, player);
                    return ErrorCode.REQUEST_INVALID;
                }
            }
            waitInfo.roomPlayer.clearOperationTimeoutCnt();
            if (waitInfo.hu) {
                waitInfo.roomPlayer.setLouHu(true);
                waitInfo.roomPlayer.setLouHuFang(waitInfo.fang);
            }
            waitInfo.op = EActionOp.PASS;
            if (((MahjongWaitAction) action).check()) {
                this.tick();
            }
            return ErrorCode.OK;
        }
        return ErrorCode.REQUEST_INVALID;
    }

    public void onPass(MahjongPlayer takeRoomPlayer, int huType, long huCard) {
        if (0 != (huType & (1 << MahjongConstant.MJ_HU_TYPE_BAR))) {
            byte tempHuCard = (byte) ((huCard >> (6 * MahjongConstant.MJ_HU_TYPE_BAR)) & 0x3f);
            Logs.ROOM.debug("%s %s 杠上开花跳过, huCard:%d, tempHuCard:%d", this, takeRoomPlayer, huCard, tempHuCard);
            this.opQiangGangHuFail(takeRoomPlayer, tempHuCard);
            this.calcGangScore(takeRoomPlayer, takeRoomPlayer, MahjongConstant.MJ_BAR_TYPE_MING);
            this.canFumble(takeRoomPlayer, false);
        } else if (0 != (huType & (1 << MahjongConstant.MJ_HU_TYPE_CHAO_TIAN))) {
            byte tempHuCard = (byte) ((huCard >> (6 * MahjongConstant.MJ_HU_TYPE_CHAO_TIAN)) & 0x3f);
            Logs.ROOM.debug("%s %s 朝天胡跳过, huCard:%d, tempHuCard:%d", this, takeRoomPlayer, huCard, tempHuCard);
            MahjongTakeAction action = new MahjongTakeAction(this, takeRoomPlayer, this.getTimeout(takeRoomPlayer, false, false, false));
            action.setTing(false);
            action.setBright(false);
            action.setBar(this.barOnBump(takeRoomPlayer, tempHuCard));
            this.addAction(action);
        } else {
            this.canFumble((MahjongPlayer) this.getNextRoomPlayer(takeRoomPlayer.getIndex()), false);
            this.joinBarCnt = 0;
        }

        this.last2OPType = this.lastOPType;
        this.lastOPType = this.curOPType;
        this.curOPType = -1;
    }

    protected abstract int hu(MahjongPlayer player, byte huCardValue, boolean addHuCard, byte lastTakeCard);

    protected boolean isHu(MahjongPlayer player, byte huCardValue) {
        return MahjongUtils.isHu(player.getHandCard(), player.getCpgCards().size(), true);
    }

    protected void canFumble(MahjongPlayer fumbleRoomPlayer, boolean isFumbleOnBar) {
        if (null == fumbleRoomPlayer) {
            Logs.ROOM.error("%s 无效摸牌, 摸牌人为空", this);
            return;
        }
        if (this.allCard.isEmpty()) {
            this.onHuangZhuang(this.curBureau < this.bureau);
            this.stop();
            return;
        }
        this.addAction(new MahjongFumbleAction(this, fumbleRoomPlayer, 2000/*fumbleRoomPlayer.isHosting(this.timeout) ? 2000 : this.getTimeout(fumbleRoomPlayer)*/, isFumbleOnBar));
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_CAN_FUMBLE, new PCLIMahjongNtfCanFumbleInfo(fumbleRoomPlayer.getUid(), isFumbleOnBar));
    }

    protected MahjongTakeAction takeAction(MahjongPlayer player, byte takeCard) {
        boolean bright = false;
        boolean ting = false;
        if (this.switchBright && !player.isBright()) {
            BrightInfo brightInfo = this.getBrightInfo(player);
            player.setBrightInfo(brightInfo);
            bright = player.isBright() ? false : this.isBright(brightInfo.child.size() > 0);
            ting = true;
        }
        if (!this.switchBright) {
            BrightInfo brightInfo = this.getBrightInfo(player);
            player.setBrightInfo(brightInfo);
            ting = true;
        }
        boolean hu = player.isBright() ? null != player.getHalfBrightInfo().huCard.get(takeCard) : this.isHu(player, takeCard);
        List<Byte> bar = this.barOnFumble(player, takeCard);
        MahjongTakeAction action = new MahjongTakeAction(this, player, this.getTimeout(player, hu, null != bar, bright));
        if (player.isBright()) {
            if (hu) {
                action.setOp(EActionOp.HU);
                bar = null;
            } else if (null != bar) {
                takeCard = bar.get(0);
                byte index = player.getCardIndex(bar.get(0));
                boolean mustBar = false;
                for (Byte c : bar) {
                    if (4 == player.getHandCard()[c]) {
                        continue;
                    }
                    if (0 != this.paoZi[c]) {
                        mustBar = true;
                        takeCard = c;
                        break;
                    }
                }
                action.setOp(mustBar ? EActionOp.MUST_BAR : EActionOp.BAR);
                if (mustBar) {
                    action.setTimeout(2000);
                }
                action.setInsertIndex((byte) 0);
                action.setStartIndex(index);
                action.setEndIndex((byte) (index + (bar.get(0) == takeCard ? 2 : 3)));
            }
        }
        if (player.isHosting(this.timeout)) {
            Logs.ROOM.warn("%s %s 托管, 不能主动碰,杠吃", this, player);
            bright = false;
            if (hu) {
                action.setOp(EActionOp.HU);
            }
        }
        action.setFumble(true);
        action.setCardValue(takeCard);
        action.setHu(hu);
        action.setBar(bar);
        action.setBright(bright);
        action.setTing(ting);
        this.addAction(action);
        return action;
    }

    /**
     * 检查是否可以打
     * @param player
     * @param card
     * @return
     */
    public boolean checkCanTake(MahjongPlayer player, byte card) {
        return player.getHandCard()[card] > 0;
    }

    /**
     * 检查是否需要等待
     * @param player
     * @param card
     * @param huType
     * @return
     */
    protected MahjongWaitAction checkWait(MahjongPlayer player, byte card, int huType) {
        MahjongWaitAction waitAction = null;
        WaitRecordAction waitRecordAction = null;

        for (int i = 0; i < this.playerNum; ++i) {
            MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            if (temp.getUid() == player.getUid()) {
                continue;
            }
            int fang = this.switchOnlyZiMo ? 0 : this.hu(temp, card, true, this.lastTakeCardValue);
            boolean hu = false;
            List<Byte> bar = null;
            boolean bump = false;
            boolean eat = false;
            if (player.isBright() || temp.isBright()) {
                fang *= 2;
            }
            if (temp.isLouHu()) {
                if (temp.getLouHuFang() < fang) {
                    hu = true;
                }
            } else {
                if ((this.joinBarCnt > 0 && fang > 0) || (MahjongConstant.MJ_HU_TYPE_BAR == huType && fang > 0) || fang >= minHuValue) {
                    hu = true;
                }
            }
            bar = this.barOnTake(temp, card);
            byte startIndex = 0;
            byte endIndex = 0;
            if (null != bar) {
                bump = true;
                startIndex = temp.getCardIndex(card);
                endIndex = (byte) (startIndex + temp.getHandCard()[card] - 1);
            } else if (MahjongUtils.isBump(temp.getHandCard(), card)) {
                bump = true;
            }
            if (temp.isBright()) {
                hu = null != temp.getHalfBrightInfo().huCard.get(card);
                if (null != bar && 0 == temp.getKouCard()[card]) {
                    bar = null;
                }
                bump = false;
            }
            if (MahjongConstant.MJ_HU_TYPE_NONE != huType) {
                bar = null;
                bump = false;
                eat = false;
            }
            if (hu || null != bar || bump || eat) {
                //if (temp.isOffline()) {
                //    Logs.ROOM.debug("%s %s 离线, 不能主动碰, 杠吃", this, temp);
                //    continue;
                //}
                if (null == waitAction) {
                    waitAction = new MahjongWaitAction(this, null, -1);
                    waitAction.setTakePlayer(player);
                }

                if (null == waitRecordAction) {
                    waitRecordAction = ((MahjongRecord) this.record).addWaitRecordAction();
                }

                long timeout = this.getTimeout(temp, hu, null != bar, false);
                if (this.allCard.isEmpty()) {
                    // 最后一张牌
                    bar = null;
                    if (hu) {
                        bump = false;
                        eat = false;
                        timeout = 1000;
                    }
                }

                if (!hu && !bump && null == bar) {
                    continue;
                }

                waitRecordAction.addWaitInfo(temp.getUid(), bump, bar, hu);

                MahjongWaitAction.WaitInfo waitInfo = waitAction.addWait(temp, eat, bump, bar, hu);
                waitInfo.setTimeout(timeout);
                waitInfo.index = startIndex;
                waitInfo.endIndex = endIndex;
                waitInfo.insertIndex = 0;
                waitInfo.fang = fang;
                waitInfo.cardValue = card;
                waitInfo.huType = huType;
                if (this.allCard.isEmpty() && hu) {
                    waitInfo.op = EActionOp.HU;
                }

                PCLIMahjongNtfCanOperateInfo canOperateInfo = new PCLIMahjongNtfCanOperateInfo(bump, null != bar, hu, eat, card);
                temp.send(CommandId.CLI_NTF_MAHJONG_CAN_OPERATE, canOperateInfo);
            }
        }
        return waitAction;
    }

    protected List<Byte> barOnBump(MahjongPlayer player, byte bumpCard) {
        List<Byte> bar = null;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (player.getHandCard()[i] >= 4) {
                if (null == bar) {
                    bar = new LinkedList<>();
                }
                bar.add((byte) i);
            }
        }
        return bar;
    }

    protected List<Byte> barOnTake(MahjongPlayer player, byte takeCard) {
        if (player.getHandCard()[takeCard] == 3) {
            List<Byte> bar = new LinkedList<>();
            bar.add(takeCard);
            return bar;
        }
        return null;
    }

    protected List<Byte> barOnFumble(MahjongPlayer player, byte fumbleCard) {
        List<Byte> bar = null;
        if (3 == player.getPgCardCnt()[fumbleCard] && (player.isBright() || 0 == this.paoZi[fumbleCard])) {
            // 明杠
            if (null == bar) {
                bar = new LinkedList<>();
            }
            bar.add(fumbleCard);
        }
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (4 == player.getHandCard()[i]) {
                if (player.isBright() && 0 == player.getKouCard()[i]) {
                    continue;
                }
                if (null == bar) {
                    bar = new LinkedList<>();
                }
                bar.add((byte) i);
            }
        }
        return bar;
    }

    protected int getRemainCarCnt(IMahjongPlayer player, byte cardValue) {
        int cnt = 0;
        cnt += this.allTakeCard[cardValue];
        cnt += player.getHandCard()[cardValue];
        cnt += this.allBrightCard[cardValue];
        cnt = 4 - cnt;
        if (cnt < 0) {
            cnt = 0;
        }
        return cnt;
    }

    protected void checkChangeLiangPaiTingCnt(MahjongPlayer player, byte card) {
        if (1 == this.paoZi[card]) {
            int temp = this.paoZiCnt[card];
            int newRemainCnt = this.getRemainCarCnt(player, card);
            if (newRemainCnt != (temp & 0x00ff)) {
                temp = ((temp >> 16) << 16) | newRemainCnt;
                this.paoZiCnt[card] = temp;
                // TODO notify to client
                PCLIMahjongNtfChangePaoCntInfo info = new PCLIMahjongNtfChangePaoCntInfo();
                info.card = card;
                info.newCnt = newRemainCnt;
                this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_CHANGEPAO_CNT, info);
            }
        }
    }

    /**
     * 抢杠胡失败
     * @param takePlayer
     * @param cardValue
     */
    public void opQiangGangHuFail(MahjongPlayer takePlayer, byte cardValue) {
        takePlayer.getPgCardCnt()[cardValue] = 4;
        this.allTakeCard[cardValue] = 4;
        for (MahjongCardNode node : takePlayer.getCpgCards()) {
            if (node.card == cardValue && MahjongConstant.MJ_NODE_TYPE_BUMP == node.type) {
                node.type = MahjongConstant.MJ_NODE_TYPE_BAR;
                break;
            }
        }
        this.checkChangeLiangPaiTingCnt(takePlayer, cardValue);
    }

    /**
     * 计算杠分
     * @param takePlayer
     * @param barPlayer
     * @param barType
     */
    public void calcGangScore(MahjongPlayer takePlayer, MahjongPlayer barPlayer, int barType) {

        BarScoreRecordAction barScoreRecordAction = ((MahjongRecord) this.record).addBarScoreRecordAction();

        PCLIMahjongNtfGangScoreInfo info = new PCLIMahjongNtfGangScoreInfo();
        int endPoint = this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1);
        if (MahjongConstant.MJ_BAR_TYPE_FANG == barType) {
            // 放杠
            int score = (int) (2 * Math.pow(2, this.joinBarCnt));
            takePlayer.addScore(Score.MJ_CUR_GANG_SCORE, -score, false);
            barPlayer.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
            barPlayer.addScore(Score.ACC_MJ_MING_GANG_CNT, 1, true);
            setTemporaryPropertyValue(barPlayer.getUid(), Score.ACC_MJ_MING_GANG_CNT, 1);
            info.totalScore.put(takePlayer.getUid(), this.getFormatScore(takePlayer.getScore() + takePlayer.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint));
            info.totalScore.put(barPlayer.getUid(), this.getFormatScore((barPlayer.getScore() + barPlayer.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint)));
            score *= endPoint;
            info.gangScore.put(takePlayer.getUid(), -score);
            info.gangScore.put(barPlayer.getUid(), score);
            barScoreRecordAction.addBarScore(takePlayer.getUid(), -score);
            barScoreRecordAction.addBarScore(barPlayer.getUid(), score);
        } else {
            for (int i = 0; i < this.allPlayer.length; ++i) {
                MahjongPlayer temp = (MahjongPlayer) this.allPlayer[i];
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                if (barPlayer.getUid() == temp.getUid()) {
                    int score = (int) ((MahjongConstant.MJ_BAR_TYPE_AN == barType ? 2 : 1) * (this.curPlayerCnt - 1) * Math.pow(2, this.joinBarCnt));
                    temp.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
                    info.totalScore.put(temp.getUid(), this.getFormatScore(temp.getScore() + temp.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint));
                    score *= endPoint;
                    info.gangScore.put(temp.getUid(), info.gangScore.getOrDefault(temp.getUid(), 0) + score);
                    barScoreRecordAction.addBarScore(temp.getUid(), score);
                } else {
                    int score = (int) ((MahjongConstant.MJ_BAR_TYPE_AN == barType ? -2 : -1) * Math.pow(2, this.joinBarCnt));
                    temp.addScore(Score.MJ_CUR_GANG_SCORE, score, false);
                    info.totalScore.put(temp.getUid(), this.getFormatScore(temp.getScore() + temp.getScore(Score.MJ_CUR_GANG_SCORE, false) * endPoint));
                    score *= endPoint;
                    info.gangScore.put(temp.getUid(), info.gangScore.getOrDefault(temp.getUid(), 0) + score);
                    barScoreRecordAction.addBarScore(temp.getUid(), score);
                }
            }
            if (MahjongConstant.MJ_BAR_TYPE_AN == barType) {
                barPlayer.addScore(Score.ACC_MJ_AN_GANG_CNT, 1, true);
                setTemporaryPropertyValue(barPlayer.getUid(), Score.ACC_MJ_AN_GANG_CNT, 1);
            } else {
                barPlayer.addScore(Score.ACC_MJ_MING_GANG_CNT, 1, true);
                setTemporaryPropertyValue(barPlayer.getUid(), Score.ACC_MJ_MING_GANG_CNT, 1);
            }
        }
        ++this.joinBarCnt;
        this.broadcast2Client(CommandId.CLI_NTF_MAHJONG_GANG_SCORE_INFO, info);
    }

    /**
     * 是否可以亮牌
     * @param bright
     * @return
     */
    protected boolean isBright(boolean bright) {
        return false;
    }

    /**
     * 是否连杠
     * @param card
     * @param darkBar
     * @return
     */
    protected boolean isJoinBar(byte card, boolean darkBar) {
        return this.lastFumbleCardValue == card;
    }


    /**
     * 发送游戏开始
     */
    @Override
    protected void doSendGameStart() {
        for (int i = 0; i < this.playerNum; ++i) {
            IMahjongPlayer player = (IMahjongPlayer) this.allPlayer[i];
            if (null == player || player.isGuest()) {
                continue;
            }
            PCLIRoomNtfBeginInfoByMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJ();
            roomBeginInfo.bankerIndex = this.bankerIndex;
            roomBeginInfo.crap1 = this.crap1;
            roomBeginInfo.crap2 = this.crap2;
            roomBeginInfo.myIndex = player.getIndex();
            if (i == this.bankerIndex) {
                player.addHandCardTo(roomBeginInfo.myCards, this.lastFumbleCardValue);
                roomBeginInfo.myCards.add(this.lastFumbleCardValue);
            } else {
                player.addHandCardTo(roomBeginInfo.myCards);
            }
            roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
            roomBeginInfo.bureau = player.getBureau();
            roomBeginInfo.d = Config.checkWhiteHas(player.getUid(),1);
            player.send(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
        }
        
        PCLIRoomNtfBeginInfoByMJ roomBeginInfo = new PCLIRoomNtfBeginInfoByMJ();
        roomBeginInfo.bankerIndex = this.bankerIndex;
        roomBeginInfo.crap1 = this.crap1;
        roomBeginInfo.crap2 = this.crap2;
        roomBeginInfo.myIndex = -1;
        roomBeginInfo.roomBriefInfo = this.getRoomBriefInfo();
        roomBeginInfo.bureau = 0;
        roomBeginInfo.d = false;
        this.broadcast2ClientWithWatch(CommandId.CLI_NTF_ROOM_BEGIN, roomBeginInfo);
     }

    public long getTimeout(MahjongPlayer player, boolean hu, boolean bar, boolean bright) {
        if (player.isBright()) {
            if (hu) {
                return 1000;
            }
            if (bar) {
                return player.isHosting(this.timeout) ? 10000 : (this.isOperationTimeout(player) ? 10000 : this.timeout);
            }
            return 800;
        }
        if (player.isHosting(this.timeout) || this.isOperationTimeout(player)) {
            return 10000;
        }
        if (-1 == this.timeout) {
            return -1;
        }
        if (player.isOffline()) {
            return 10000;
        }
//        return this.timeout + (bright ? 10000 : 0);
        return this.timeout;
    }

    protected BrightInfo getBrightInfo(MahjongPlayer player) {
        return null;
    }

    public List<MahjongPlayer> getLiangPaiPlayer() {
        return liangPaiPlayer;
    }

    public byte[] getPaoZi() {
        return this.paoZi;
    }

    public int getCrap1() {
        return this.crap1;
    }

    public int getCrap2() {
        return this.crap2;
    }

    @Override
    public void clear() {
        super.clear();
        this.joinBarCnt = 0;
        this.lastFumbleCardValue = -1;
        this.lastTakeCardValue = -1;
        this.lastFumbleIndex = -1;
        this.liangPaiPlayer.clear();
        this.playerGoodCards.clear();
        this.destroyType=ERoomDestroyType.UN_DESTROY;
        for (int i = 0; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            this.paoZi[i] = 0;
            this.paoZiCnt[i] = 0;
            this.allTakeCard[i] = 0;
            this.allBrightCard[i] = 0;
        }

        if (this.xuanPiaoType == 2) {
            // 每局选漂前重置
            for (int i = 0; i < this.playerNum; ++i) {
                MahjongPlayer player = (MahjongPlayer) this.allPlayer[i];
                if (null == player) {
                    continue;
                }
                player.setPiaoScore(0);
            }
        } else if((this.xuanPiaoType == 3 || this.xuanPiaoType == 4) && this.curBureau == 1) {
            for (int i = 0; i < this.playerNum; ++i) {
                MahjongPlayer player = (MahjongPlayer) this.allPlayer[i];
                if (null == player) {
                    continue;
                }
                // 是否选过漂
                if (getTemporaryPropertyValue(player.getUid(), RoomRule.RR_NONE) != 0) {
                    continue;
                }
                player.setPiaoScore(0);
            }
        }
    }

    @Override
    public Record getRecord() {
        if (null == this.record) {
            this.record = new MahjongRecord(this);
        }
        return this.record;
    }
    
}
