package com.xiuxiu.app.server.room.handle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfLeaveStateInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMemberStateInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReadyFailInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.box.constant.EBoxState;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.box.handle.IBoxHandle;
import com.xiuxiu.app.server.chat.ChatManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomListState;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.EState;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.app.server.room.player.helper.IArenaRoomPlayerHelper;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.app.server.score.BoxRoomScore;
import com.xiuxiu.app.server.score.IRoomScore;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecordDetail;
import com.xiuxiu.app.server.table.DiamondCostManager;
import com.xiuxiu.core.net.Task;

/**
 * 包厢房间抽象实现
 *
 * @author Administrator
 *
 */
public abstract class AbstractBoxRoomHandle extends AbstractRoomHandle implements IBoxRoomHandle {

    /**
     * 包厢
     */
    protected Box box;

    protected int boxRoomIndex = -1;

    /**
     * 扣房卡安全锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 该游戏桌玩过游戏的亲友圈玩家,map<亲友圈id,玩家ids>
     */
    protected Map<Long, Set<Long>> playPlayerCountMap = new ConcurrentHashMap<Long, Set<Long>>();

    private long startReadyTime = 0L;
    private long lastCheckLeaveTime = 0;

    private long leaveTime = 8000L;//倒计时离开时间（毫秒）

    /** 是否已经扣除房卡 */
    protected boolean hasCostDiamond = Boolean.FALSE;
    /** 抽水类型(1每人每局，2赢家抽，3大赢家抽) */
    protected int costModel;
    public AbstractBoxRoomHandle(IRoom room, Box box) {
        super(room);
        this.box = box;
    }

    @Override
    public void init() {
        BoxRoomScore roomScore = (BoxRoomScore) getRoom().getRoomScore();
        roomScore.setBoxUid(this.box.getUid());
        Map<String, Integer> rule = getRoom().getRule();
        this.costModel = rule.getOrDefault(RoomRule.RR_COSTMODEL, 0);
    }

    @Override
    public ErrorCode join(Player player) {
        if (null == box) {
            Logs.ROOM.warn("%s 包厢不存在", getRoom());
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        Box tempBox = BoxManager.I.getBox(box.getUid());
        if (null == tempBox) {
            Logs.ROOM.warn("%s 包厢不存在", getRoom());
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        ErrorCode err = super.join(player);
        if (ErrorCode.OK == err) {
            box.join(getRoom().getRoomPlayer(player.getUid()));
        }
        return err;
    }

    @Override
    public ErrorCode leave(Player player) {
        ErrorCode err = super.leave(player);
        if (ErrorCode.OK == err || ErrorCode.PLAYER_ROOM_NOT_IN == err) {
            if (null != box) {
                box.leave(player.getUid(), this.getRoom().getRoomId());
            }
        }
        return err;
    }

    @Override
    public void destoryAfter() {
        super.destoryAfter();
        if (null == box) {
            return;
        }
        IClub club = ClubManager.I.getClubByUid(box.getOwnerUid());
        if (null == club) {
            return;
        }
        club.checkChangeCloseStatus();
    }

    @Override
    public long getBoxUid() {
        return null == box ? -1 : box.getUid();
    }

    @Override
    public int getRoomId() {
        return this.room.getRoomId();
    }

    @Override
    public ErrorCode join(IRoomPlayer player) {
        if (ERoomState.NEW != this.room.getRoomState() && ERoomState.AGAIN != this.room.getRoomState()
                && ERoomState.AUTO_START != this.room.getRoomState()) {
            Logs.ROOM.warn("%s %s 房间已经开始", String.valueOf(this.room.getRoomUid()), String.valueOf(player.getUid()));
            IRoomPlayer roomPlayer = this.room.getRoomPlayer(player.getUid());
            if (null == roomPlayer) {
                Logs.ROOM.warn("%s %s 房间已经开始无法加入", String.valueOf(this.room.getRoomUid()), String.valueOf(player.getUid()));
                return ErrorCode.ROOM_ALREADY_START;
            }
            this.room.changeState(roomPlayer, EState.ONLINE);
            return ErrorCode.OK;
        }
        if (ERoomState.AGAIN == this.room.getRoomState() || ERoomState.AUTO_START == this.room.getRoomState()) {
            if (!this.room.canWatch()) {
                Logs.ROOM.warn("%s %s 房间没有开启组图加入 无法加入", String.valueOf(this.room.getRoomUid()), String.valueOf(player.getUid()));
                return ErrorCode.ROOM_ALREADY_START;
            }
        }
        IRoomPlayer roomPlayer = this.room.getRoomPlayer(player.getUid());
        if (null != roomPlayer) {
            Logs.ROOM.warn("%s %s 已经在房间里", String.valueOf(this.room.getRoomUid()), String.valueOf(player.getUid()));
            return ErrorCode.PLAYER_ROOM_IN;
        }
        if (this.room.getPlayerNum() == this.room.getPlayerCnt()) {
            Logs.ROOM.warn("%s %s 房间已经满了", String.valueOf(this.room.getRoomUid()), String.valueOf(player.getUid()));
            return ErrorCode.ROOM_PLAYER_FULL;
        }

        boolean join = false;
        ReentrantReadWriteLock.WriteLock writeLock = this.room.getLock().writeLock();
        try {
            if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                IRoomPlayer[] allPlayer = this.room.getAllPlayer();
                Logs.ROOM.debug("%s %s 加入在房间里", this.room, roomPlayer);
                for (int i = 0; i < this.room.getPlayerNum(); ++i) {
                    if (null == allPlayer[i]) {
                        player.setIndex(i);
                        allPlayer[i] = player;
                        this.room.addPlayerCnt();
                        player.setRoom(this.room);
                        join = true;
                        this.startCheckLeave();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
        return join ? ErrorCode.OK : ErrorCode.ROOM_PLAYER_FULL;

    }

    /**
     * 服务费抽成
     *
     * @param start
     */
    public void serviceCharge(boolean start) {
        if (start) {
            return;
        }
        IBoxOwner boxOwner = this.room.getBoxOwner();
        if (null == boxOwner) {
            return;
        }
        doServiceCharge(boxOwner);
        /**
         List<Long> maxPlayerUidList = new ArrayList<>();
         int maxScore = 0;
         for (int i = 0, len = this.room.getMaxPlayerCnt(); i < len; ++i) {
         IRoomPlayer temp = this.room.getRoomPlayer(i);
         if (null == temp || temp.isGuest()) {
         continue;
         }

         int score = temp.getScore(Score.SCORE, false);
         if (score >= maxScore && score > 0) {
         if (score > maxScore) {
         maxPlayerUidList.clear();
         }
         maxPlayerUidList.add(temp.getUid());
         maxScore = score;
         }
         }
         // 大赢家抽水
         doServiceCharge(boxOwner, maxPlayerUidList, maxScore);
         */
    }

    protected void doServiceCharge(IBoxOwner boxOwner) {

    }

    /**
     * 大赢家抽水
     *
     * @param boxOwner
     * @param maxPlayerUidList
     * @param maxScore
     */
    /**
     protected void doServiceCharge(IBoxOwner boxOwner, List<Long> maxPlayerUidList, int maxScore) {

     }
     */

    @Override
    public void killAll(List<Long> killPlayerUids) {
        super.killAll(killPlayerUids);
        if (null != box) {
            box.killAll(this.boxRoomIndex, this.room.getRoomUid(), killPlayerUids);
        }
    }

    @Override
    public int getIndex() {
        return this.boxRoomIndex;
    }

    @Override
    public void setIndex(int index) {
        this.boxRoomIndex = index;
    }

    @Override
    public void clearIndex() {
        this.boxRoomIndex = -1;
    }

    @Override
    public void startBefore() {
        // 是否可少人模式
        if (BoxManager.I.isWatch(this.room.getGameType(), this.room.getRule())) {
            this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, this.room.getRoomInfo());
        }
    }

    @Override
    public void destoryGoldHandle() {
//        if (room.getGameType() == GameType.GAME_TYPE_KWX || room.getGameType() == GameType.GAME_TYPE_RUN_FAST || room.getGameType() == GameType.GAME_TYPE_WHMJ
//            || room.getGameType() == GameType.GAME_TYPE_HSMJ || room.getGameType() == GameType.GAME_TYPE_YXMJ || room.getGameType() == GameType.GAME_TYPE_LANDLORD 
//            || room.getGameType() == GameType.GAME_TYPE_LYKD ) {
        if (costModel == 3 || costModel == 2) {
            // 是否完成
            if (this.room.getFinishBureauCount() >= this.room.getBureau()) {
                doDestoryGoldHandle();
            }
        }
    }

    /**
     * 执行包厢竞技值计算处理(大局结算)处理
     */
    private void doDestoryGoldHandle() {
        if (((IClub) room.getBoxOwner()).getClubType().match(EClubType.GOLD)) {
            // 扣管理费用
            serviceCharge(false);
        }
    }

    @Override
    public void calculateGold() {
        IRoom room = getRoom();
        if (((IClub) room.getBoxOwner()).getClubType().match(EClubType.GOLD)) {
            //抽水计算
//            if (room.getGameType() == GameType.GAME_TYPE_KWX || room.getGameType() == GameType.GAME_TYPE_RUN_FAST || room.getGameType() == GameType.GAME_TYPE_WHMJ
//                    || room.getGameType() == GameType.GAME_TYPE_HSMJ || room.getGameType() == GameType.GAME_TYPE_YXMJ || room.getGameType() == GameType.GAME_TYPE_LANDLORD 
//                    || room.getGameType() == GameType.GAME_TYPE_LYKD ){
                // 第一局打完抽成
                if (costModel == 3 && room.getCurBureau() == 1 || costModel == 2) {
                    if (room.getRoomState() != ERoomState.DESTROY && !(room.getGameType() == GameType.GAME_TYPE_COW && room.getGameSubType() == 1)){
                        serviceCharge(false);
                    }
                }
//            }
            // 每小局结束时竞技分加/减
            if (room.getGameType() == GameType.GAME_TYPE_COW && room.getGameSubType() != 1) {
                cowGoldHandle();
            } else {
                goldHandle();
            }

            calculateGoldAfter();

            if (!room.checkIsDestroy()) {
                this.goldActivityCount();
            }
        }
        IRoomPlayer[] allPlayer = room.getAllPlayer();
        for (int i = 0; i < room.getPlayerNum(); ++i) {
            IRoomPlayer temp = allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            room.setTemporaryPropertyValue(temp.getUid(), RoomRule.RR_BUREAU, 1);
        }
    }

    protected void calculateGoldAfter() {

    }
    
    /**
     * 获取当前小局玩家变化的竞技分
     * @param temp
     * @return
     */
    protected int getScore(IRoomPlayer temp) {
        return getRoom().getRecordScore(temp);
    }
    
    /**
     * 重置当前小局玩家最终变化的竞技分
     * @param temp
     * @param finalScore
     * @return
     */
    protected void resetScore(IRoomPlayer temp, int finalScore) {
        temp.setScore(Score.SCORE, finalScore, false);
    }
    
    /**
     * 是否是庄家
     * @param bankerPlayerIndex
     * @return
     */
    protected boolean isBankerPlayer(int bankerPlayerIndex) {
        return Boolean.FALSE;
    }

    /**
     * 每小局结束时竞技分加/减
     */
    private void goldHandle() {
        IBoxOwner boxOwner = getRoom().getBoxOwner();
        if (null == boxOwner) {
            return;
        }
        // 实际扣的总竞技数
        int totalCostGold = 0, totalAddGold = 0;
        IClub mainClub = (IClub) boxOwner;
        // 先扣
        for (int i = 0, len = getRoom().getMaxPlayerCnt(); i < len; ++i) {
            IRoomPlayer temp = getRoom().getRoomPlayer(i);
            if (null == temp || temp.isGuest()) {
                continue;
            }
            int tempScore = getScore(temp);
            if (tempScore < 0) {
                int finalScore = 0;
                if (isBankerPlayer(i)) {
                    finalScore = -tempScore;
                } else {
                    finalScore = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                            temp.getUid(), tempScore, 0);
                    resetScore(temp, -finalScore);
                }
                totalCostGold += finalScore;
            } else {
                totalAddGold = tempScore;
            }
        }
        if (totalCostGold == 0) {
            for (int i = 0, len = getRoom().getMaxPlayerCnt(); i < len; ++i) {
                IRoomPlayer temp = getRoom().getRoomPlayer(i);
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                int tempScore = getScore(temp);
                if (tempScore > 0) {
                    resetScore(temp, 0);
                }
            }
            return;
        }
        // 再加
        if (totalAddGold == totalCostGold) {
            for (int i = 0, len = getRoom().getMaxPlayerCnt(); i < len; ++i) {
                IRoomPlayer temp = getRoom().getRoomPlayer(i);
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                int tempScore = getScore(temp);
                if (tempScore > 0) {
                    int finalScore = 0;
                    if (isBankerPlayer(i)) {
                        finalScore = tempScore;
                    } else {
                        finalScore = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                            temp.getUid(), tempScore, 0);
                        resetScore(temp, finalScore);
                    }
                }
            }
        } else {
            // 按赢的最多的排序
            Map<Integer, Integer> tempPlayerGoldMap = new HashMap<Integer, Integer>();
            for (int i = 0, len = getRoom().getMaxPlayerCnt(); i < len; ++i) {
                IRoomPlayer temp = getRoom().getRoomPlayer(i);
                if (null == temp || temp.isGuest()) {
                    continue;
                }
                int tempScore = getScore(temp);
                if (tempScore > 0) {
                    tempPlayerGoldMap.put(i, tempScore);
                }
            }
            List<Map.Entry<Integer, Integer>> tempList = new ArrayList<Map.Entry<Integer, Integer>>(
                    tempPlayerGoldMap.entrySet());
            Collections.sort(tempList, new Comparator<Map.Entry<Integer, Integer>>() {
                // 降序排序
                @Override
                public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            for (Map.Entry<Integer, Integer> entry : tempList) {
                IRoomPlayer temp = getRoom().getRoomPlayer(entry.getKey());
                if (totalCostGold <= 0) {
                    if (!isBankerPlayer(temp.getIndex())){
                        resetScore(temp, 0);
                    }
                    continue;
                }
                int addValue = totalCostGold > entry.getValue() ? entry.getValue() : totalCostGold;
                int finalValue = 0;
                if (isBankerPlayer(temp.getIndex())) {
                    finalValue = addValue;
                } else {
                    finalValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                            temp.getUid(), addValue, 0);
                    resetScore(temp, finalValue);
                }
                totalCostGold -= finalValue;
            }
        }
    }

    /**
     * 牛牛每小局结束时竞技分加/减
     */
    private void cowGoldHandle() {
        IBoxOwner boxOwner = getRoom().getBoxOwner();
        if (null == boxOwner) {
            return;
        }
        //庄家
        IRoomPlayer banker = getRoom().getRoomPlayer(getRoom().getBankerIndex());
        if (banker == null) {
            return;
        }
        IClub mainClub = (IClub) boxOwner;
        //庄家身上分
        int bankerHas = (int)ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(banker.getUid())).getGold(banker.getUid());
        Map<Integer, Integer> lostPlayerGoldMap = new HashMap<Integer, Integer>();//闲家输分最多能变化的分
        Map<Integer, Integer> winPlayerGoldMap = new HashMap<Integer, Integer>();//闲家赢分最多能变化的分
        int xianLoseTotal = 0;//输的闲家最多能输出去总数
        int xianWinTotal = 0;//赢的闲家最多能赢进来总数
        for (int i = 0, len = getRoom().getMaxPlayerCnt(); i < len; ++i) {
            IRoomPlayer temp = getRoom().getRoomPlayer(i);
            if (null == temp || temp.isGuest()) {
                continue;
            }
            //庄家
            if (temp.getUid() == banker.getUid()) {
                continue;
            }
            int tempScore = getRoom().getRecordScore(temp);
            if (tempScore < 0) {
                int finalScore = -tempScore;
                int tempHas = (int)ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(temp.getUid())).getGold(temp.getUid());//身上有多少
                int limitValue = bankerHas > tempHas ? tempHas : bankerHas;//限制值
                if (finalScore > limitValue) {
                    finalScore = limitValue;
                }
                lostPlayerGoldMap.put(i, -finalScore);
                xianLoseTotal += finalScore;
            } else {
                int finalScore = tempScore;
                int tempHas = (int)ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(temp.getUid())).getGold(temp.getUid());//身上有多少
                if (finalScore > tempHas) {
                    finalScore = tempHas;
                }
                winPlayerGoldMap.put(i, finalScore);
                xianWinTotal += finalScore;
            }
        }

        //庄家输
        if (xianWinTotal > xianLoseTotal) {
            //输的闲家输限制值
            for (Map.Entry<Integer, Integer> entry : lostPlayerGoldMap.entrySet()) {
                IRoomPlayer temp = getRoom().getRoomPlayer(entry.getKey());
                int tempValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                        temp.getUid(), entry.getValue(), 0);
                temp.setScore(Score.SCORE, -tempValue, false);
            }
            //庄家扣分
            int bankerWillLose = xianWinTotal - xianLoseTotal;//庄家将要输多少
            int bankerValue = bankerWillLose;
            if (bankerWillLose > bankerHas) {
                bankerValue = bankerHas;
            }
            int bankerFinalValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(banker.getUid()),
                    banker.getUid(), -bankerValue, 0);
            banker.setScore(Score.SCORE, -bankerFinalValue, false);
            //赢的闲家按牌型大到小排序
            List<Map.Entry<Integer, Integer>> tempWinList = new ArrayList<Map.Entry<Integer, Integer>>(
                    winPlayerGoldMap.entrySet());
            List<Map.Entry<Integer, Integer>> xianWinList = new ArrayList<>();
            for (int i = 0; i < tempWinList.size(); i++) {
                if (i == 0) {
                    xianWinList.add(tempWinList.get(0));
                    continue;
                }
                CowPlayer temp1 = (CowPlayer) getRoom().getRoomPlayer(tempWinList.get(i).getKey());
                for (int j = 0; j < xianWinList.size(); j++) {
                    CowPlayer temp2 = (CowPlayer) getRoom().getRoomPlayer(xianWinList.get(j).getKey());
                    if (((CowRoom)this.room).comparePlayer(temp1, temp2)) {
                        xianWinList.add(j, tempWinList.get(i));
                        break;
                    }
                }
                if (!xianWinList.contains(tempWinList.get(i))) {
                    xianWinList.add(tempWinList.get(i));
                }
            }
            //赢的闲家们总共能赢的分
            int xianRealWinTotal = xianLoseTotal + bankerValue;
            //赢的闲家算分，从牌型最大的开始+分
            for (Map.Entry<Integer, Integer> entry : xianWinList) {
                IRoomPlayer temp = getRoom().getRoomPlayer(entry.getKey());
                if (xianRealWinTotal <= 0) {
                    int finalValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                            temp.getUid(), 0, 0);
                    temp.setScore(Score.SCORE, finalValue, false);
                    continue;
                }
                int realValue = entry.getValue();// > 0
                int finalValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                        temp.getUid(), xianRealWinTotal > realValue ? realValue : xianRealWinTotal, 0);
                temp.setScore(Score.SCORE, finalValue, false);
                xianRealWinTotal -= finalValue;
            }
        }
        //庄家赢
        else {
            //赢的闲家赢限制值
            for (Map.Entry<Integer, Integer> entry : winPlayerGoldMap.entrySet()) {
                IRoomPlayer temp = getRoom().getRoomPlayer(entry.getKey());
                int tempValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                        temp.getUid(), entry.getValue(), 0);
                temp.setScore(Score.SCORE, tempValue, false);
            }
            //庄家赢分
            int bankerWillWin = xianLoseTotal - xianWinTotal;//庄家将要赢多少
            int bankerValue = bankerWillWin;
            if (bankerWillWin > bankerHas) {
                bankerValue = bankerHas;
            }
            int bankerFinalValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(banker.getUid()),
                    banker.getUid(), bankerValue, 0);
            banker.setScore(Score.SCORE, bankerFinalValue, false);
            //输的闲家牌型小到大排序
            List<Map.Entry<Integer, Integer>> tempLostList = new ArrayList<Map.Entry<Integer, Integer>>(
                    lostPlayerGoldMap.entrySet());
            List<Map.Entry<Integer, Integer>> xianLostList = new ArrayList<>();
            for (int i = 0; i < tempLostList.size(); i++) {
                if (i == 0) {
                    xianLostList.add(tempLostList.get(0));
                    continue;
                }
                CowPlayer temp1 = (CowPlayer) getRoom().getRoomPlayer(tempLostList.get(i).getKey());
                for (int j = 0; j < xianLostList.size(); j++) {
                    CowPlayer temp2 = (CowPlayer) getRoom().getRoomPlayer(xianLostList.get(j).getKey());
                    if (!((CowRoom)this.room).comparePlayer(temp1, temp2)) {
                        xianLostList.add(j, tempLostList.get(i));
                        break;
                    }
                }
                if (!xianLostList.contains(tempLostList.get(i))) {
                    xianLostList.add(tempLostList.get(i));
                }
            }
            //输的闲家们总共能输的分
            int xianRealLoseTotal = xianWinTotal + bankerValue;
            //输的闲家算分，从牌型最小的开始-分
            for (Map.Entry<Integer, Integer> entry : xianLostList) {
                IRoomPlayer temp = getRoom().getRoomPlayer(entry.getKey());
                if (xianRealLoseTotal <= 0) {
                    int finalValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                            temp.getUid(), 0, 0);
                    temp.setScore(Score.SCORE, -finalValue, false);
                    continue;
                }
                int realValue = entry.getValue();// < 0
                int finalValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(temp.getUid()),
                        temp.getUid(), xianRealLoseTotal > -realValue ? realValue : -xianRealLoseTotal, 0);
                temp.setScore(Score.SCORE, -finalValue, false);
                xianRealLoseTotal -= finalValue;
            }
        }
    }

    private void goldActivityCount() {
        Set<Long> playerUids = new HashSet<>();
        IRoomPlayer[] allPlayer = room.getAllPlayer();
        for (int i = 0; i < getRoom().getPlayerNum(); ++i) {
            IRoomPlayer temp = allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            playerUids.add(temp.getUid());
        }
        IBoxOwner boxOwner = room.getBoxOwner();
        if (boxOwner != null) {
            ClubManager.I.getMainClub(boxOwner).onFinishGame(getBoxUid(), playerUids);
        }
    }

    @Override
    public void again() {
        // 重置离开时间
        this.resetCheckLeaveTime();
        // 重置自动站起时间
        resetSitUpTime();
        //每小局扣房卡的游戏，重置房卡已扣除成未扣除
        if (GameType.isArenaGame(room.getGameType())) {
            this.hasCostDiamond = false;
        }
        super.again();
    }

    /**
     * 重置自动站起时间
     *
     */
    private void resetSitUpTime() {
        try {
            IClub club = ClubManager.I.getClubByUid(room.getGroupUid());
            if (null == club) {
                return;
            }
            if (BoxManager.I.isWatch(room.getGameType(), room.getRule())) {
                box.resetSitUpTime(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkAgain(boolean killPlayer) {
        if (box.getState() == EBoxState.WAIT_CLOSE || box.getState() == EBoxState.CLOSE) {
            return false;
        }
        IClub mainClub = ClubManager.I.getMainClub(room.getBoxOwner());
        // 是否已经申请打烊
        if (!mainClub.matchCloseStatus(EClubCloseStatus.OPEN)) {
            return false;
        }
        
        return doCheckAgain(mainClub, killPlayer);
    }
    
    protected boolean doCheckAgain(IClub mainClub, boolean killPlayer) {
        if (((IClub) room.getBoxOwner()).getClubType().match(EClubType.GOLD)) {
            // 竞技分足够的总人数
            int count = 0;
            IRoomPlayer[] allPlayer = this.room.getAllPlayer();
            for (int i = 0; i < this.room.getPlayerNum(); ++i) {
                IRoomPlayer tempRoomPlayer = allPlayer[i];
                if (null == tempRoomPlayer) {
                    continue;
                }
                long fromClubUid = mainClub.getEnterFromClubUid(tempRoomPlayer.getUid());
                IClub fromClub = ClubManager.I.getClubByUid(fromClubUid);
                if (tempRoomPlayer.isGuest()) {
                    if (BoxManager.I.checkEnoughGold(fromClub, room.getRule(), tempRoomPlayer.getUid(),
                            Boolean.FALSE)) {
                        ++count;
                    } else {
                        if (killPlayer) {
                            tempRoomPlayer.getPlayer().send(CommandId.CLI_NTF_GOLD_FAIL,
                                    ErrorCode.ARENA_LESS_THAN_MIN_VALUE);
                        }
                    }
                    continue;
                }
                if (null == fromClub) {
                    continue;
                }
                if (!BoxManager.I.checkEnoughGold(fromClub, room.getRule(), tempRoomPlayer.getUid(), Boolean.FALSE)) {
                    if (killPlayer) {
                        tempRoomPlayer.getPlayer().send(CommandId.CLI_NTF_GOLD_FAIL,
                                ErrorCode.ARENA_LESS_THAN_MIN_VALUE);
                        room.getRoomScore().addScoreItemInfo(tempRoomPlayer.getUid(),
                                tempRoomPlayer.getScore(Score.ACC_TOTAL_SCORE, true), room);
                    }
                } else {
                    ++count;
                }
            }
            if (count < this.room.getMinPlayerCnt()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public ErrorCode readyHandle(long playerUid, boolean checkContains) {
        try {
            if (checkContains && this.room.isReady(playerUid)) {
                return ErrorCode.OK;
            }
            lock.lock();
            // 是否是可以开局的最后一人准备
            IBoxHandle boxHandle = box.getBoxHandle(room.getRoomUid());
            Map<Long, Long> allSitDownPlayer = boxHandle.allSitDownPlayer();
            boolean canStart = Boolean.FALSE;
            // 判断坐下的人是否准备
            if (allSitDownPlayer != null && !allSitDownPlayer.isEmpty()) {
                if (allSitDownPlayer.containsKey(playerUid)) {
                    canStart = Boolean.TRUE;
                } else {
                    boolean flag = Boolean.TRUE;
                    Iterator<Map.Entry<Long, Long>> it = allSitDownPlayer.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Long, Long> item = it.next();
                        if (!this.room.isReady(item.getKey())) {
                            flag = Boolean.FALSE;
                            break;
                        }
                    }
                    // 没有找到坐下且没有准备的人
                    if (flag) {
                        canStart = Boolean.TRUE;
                    }
                }
            } else {
                canStart = Boolean.TRUE;
            }
            int needReadyCount = 0;
            IRoomPlayer[] allPlayer = this.room.getAllPlayer();
            for (int i = 0; i < this.room.getPlayerNum(); ++i) {
                IRoomPlayer tempPlayer = allPlayer[i];
                if (null == tempPlayer || tempPlayer.isGuest()) {
                    continue;
                }
                Player player = PlayerManager.I.getPlayer(tempPlayer.getUid());
                if (null == player) {
                    continue;
                }
                if (player.getRoomId() != room.getRoomId()) {
                    System.err.println("readyHandle异常，playerId="+player.getUid()+",roomId="+room.getRoomId()+",player.getRoomId()="+player.getRoomId());
                    continue;
                }
                ++needReadyCount;
            }
            needReadyCount = needReadyCount - 1;
            needReadyCount = needReadyCount < this.room.getPlayerMinNum() - 1 ? this.room.getPlayerMinNum() - 1
                    : needReadyCount;
            needReadyCount = !checkContains ? this.room.getPlayerMinNum() : needReadyCount;
            if (this.room.getReadySize() >= needReadyCount && canStart) {
                if (!this.hasCostDiamond) {
                    IBoxOwner boxOwner = this.room.getBoxOwner();
                    IClub mainClub = ClubManager.I.getMainClub(boxOwner);
                    // 判断所有人房卡是否足够扣除
                    if (!checkEnoughDiamond(mainClub)) {
                        return ErrorCode.REQUEST_INVALID;
                    }

                    // 扣钱
                    ErrorCode costErrorCode = costDiamond(mainClub);
                    if (costErrorCode != ErrorCode.OK) {
                        return costErrorCode;
                    }
                    this.hasCostDiamond = Boolean.TRUE;
                }
                this.room.addReadyPlayerUid(playerUid);
                PCLIRoomNtfMemberStateInfo memberState = new PCLIRoomNtfMemberStateInfo();
                memberState.playerUid = playerUid;
                memberState.gameType = this.room.getGameType();
                memberState.state = 1;
                this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_MEMBER_STATE, memberState);
                startBefore();
                this.resetCheckLeaveTime();
                this.room.start();
            } else {
                if (checkContains) {
                    this.room.addReadyPlayerUid(playerUid);
                    PCLIRoomNtfMemberStateInfo memberState = new PCLIRoomNtfMemberStateInfo();
                    memberState.playerUid = playerUid;
                    memberState.gameType = this.room.getGameType();
                    memberState.state = 1;
                    this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_MEMBER_STATE, memberState);
                }
                return ErrorCode.OK;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return ErrorCode.OK;
    }

    /**
     * 判断所有人房卡是否足够扣除
     * @param mainClub
     * @return
     */
    private boolean checkEnoughDiamond(IClub mainClub) {
        return mainClub.getClubType().match(EClubType.CARD) ? checkCardClubEnoughDiamond(mainClub) : checkGoldClubEnoughDiamond(mainClub);
    }

    /**
     * 判断所有人房卡是否足够扣除（比赛场）
     * @param mainClub
     * @return
     */
    private boolean checkGoldClubEnoughDiamond(IClub mainClub) {
        // 判断玩家圈主的房卡是否足够
        int costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ROOM, this.room.getBureau());
        return mainClub.hasEnoughMoney(EMoneyType.DIAMOND, costDiamond);
    }

    /**
     * 判断所有人房卡是否足够扣除（亲友圈）
     * @param mainClub
     * @return
     */
    private boolean checkCardClubEnoughDiamond(IClub mainClub) {
        List<String> diamondFailInfo = new ArrayList<>();
        IRoomPlayer[] allPlayer = this.room.getAllPlayer();
        for (int i = 0; i < this.room.getPlayerNum(); ++i) {
            IRoomPlayer tempPlayer = allPlayer[i];
            if (null == tempPlayer || tempPlayer.isGuest()) {
                continue;
            }
            IClub fromClub = null;
            if (mainClub.checkIsMainClub()) {
                fromClub = ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(tempPlayer.getUid()));
            } else {
                fromClub = mainClub;
            }
            if (null == fromClub) {
                return false;
            }
            ErrorCode tempErrorCode = hasEnoughDiamond(fromClub, tempPlayer.getUid());
            if (tempErrorCode != ErrorCode.OK) {
                diamondFailInfo.add(String.valueOf(tempPlayer.getUid()));
                diamondFailInfo.add(fromClub.getName());
            }
        }

        if (diamondFailInfo.size() > 0) {
            PCLIRoomReadyFailInfo failInfo = new PCLIRoomReadyFailInfo();
            failInfo.diamondFail = diamondFailInfo;
            this.room.broadcast2Client(CommandId.CLI_NTF_ROOM_READY_FAIL, failInfo, false);
            return false;
        }
        return true;
    }

    /**
     * 扣房卡
     * @param mainClub
     * @return
     */
    private ErrorCode costDiamond(IClub mainClub) {
        return mainClub.getClubType().match(EClubType.CARD) ? costCardClubDiamond(mainClub) : costGoldClubDiamond(mainClub);
    }

    /**
     * 扣亲友圈房卡
     * @param mainClub
     * @return
     */
    private ErrorCode costCardClubDiamond(IClub mainClub) {
        int costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ROOM, this.room.getBureau());
        IRoomPlayer[] allPlayer = this.room.getAllPlayer();
        for (int i = 0; i < this.room.getPlayerNum(); ++i) {
            IRoomPlayer tempPlayer = allPlayer[i];
            if (null == tempPlayer || tempPlayer.isGuest()) {
                continue;
            }
            IClub fromClub = null;
            if (mainClub.checkIsMainClub()) {
                fromClub = ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(tempPlayer.getUid()));
            } else {
                fromClub = mainClub;
            }
            if (null == fromClub) {
                return ErrorCode.REQUEST_INVALID;
            }

            boolean isCost = false;
            if (!playPlayerCountMap.containsKey(fromClub.getClubUid())) {
                isCost = true;
            }

            // 扣钱
            if (isCost && !this.playPlayerCountMap.containsKey(fromClub.getClubUid()) && !fromClub.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, -costDiamond, fromClub.getOwnerId(), fromClub.getClubUid(), EMoneyExpendType.GROUP_EXPEND, -1)) {
                Logs.ROOM.warn("%d 创建房间失败, 钻石不足", tempPlayer.getUid());
                return ErrorCode.GROUP_CHIEF_LACK_DIAMOND;
            }
            Set<Long> tempPlayerUids = null;
            if (this.playPlayerCountMap.containsKey(fromClub.getClubUid())) {
                tempPlayerUids = this.playPlayerCountMap.get(fromClub.getClubUid());
            } else {
                tempPlayerUids = new HashSet<Long>();
                this.playPlayerCountMap.put(fromClub.getClubUid(), tempPlayerUids);
            }
            tempPlayerUids.add(tempPlayer.getUid());
        }
        return ErrorCode.OK;
    }

    /**
     * 扣比赛场房卡
     * @param mainClub
     * @return
     */
    private ErrorCode costGoldClubDiamond(IClub mainClub) {
        float costDiamond;
        if (GameType.isArenaGame(room.getGameType())) {
            costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ARENA, 1) * 1f / 10f;
        } else {
            costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ROOM, this.room.getBureau()) * 1f;
        }
        // 扣钱
        if (!mainClub.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, -costDiamond, mainClub.getOwnerId(), mainClub.getClubUid(), EMoneyExpendType.GROUP_EXPEND, -1)) {
            Logs.ROOM.warn("%d 创建房间失败, 钻石不足", mainClub.getClubUid());
            return ErrorCode.GROUP_CHIEF_LACK_DIAMOND;
        }
        // 更新该游戏桌玩过游戏的亲友圈玩家,map<亲友圈id,玩家ids>
        updatePlayPlayerCount(mainClub);
        return ErrorCode.OK;
    }

    /**
     * 更新该游戏桌玩过游戏的亲友圈玩家,map<亲友圈id,玩家ids>
     * @param mainClub
     */
    private void updatePlayPlayerCount(IClub mainClub) {
        Map<Long, Set<Long>> tempMap = new HashMap<Long, Set<Long>>();
        IRoomPlayer[] allPlayer = this.room.getAllPlayer();
        for (int i = 0; i < this.room.getPlayerNum(); ++i) {
            IRoomPlayer tempPlayer = allPlayer[i];
            if (null == tempPlayer || tempPlayer.isGuest()) {
                continue;
            }
            IClub fromClub = null;
            if (mainClub.checkIsMainClub()) {
                fromClub = ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(tempPlayer.getUid()));
            } else {
                fromClub = mainClub;
            }
            if (null == fromClub) {
                continue;
            }
            Set<Long> tempPlayerUids = null;
            if (tempMap.containsKey(fromClub.getClubUid())) {
                tempPlayerUids = tempMap.get(fromClub.getClubUid());
            } else {
                tempPlayerUids = new HashSet<Long>();
                tempMap.put(fromClub.getClubUid(), tempPlayerUids);
            }
            tempPlayerUids.add(tempPlayer.getUid());
        }
        Iterator<Entry<Long, Set<Long>>> iter = tempMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, Set<Long>> entry = iter.next();
            Set<Long> tempPlayerUids = null;
            if (this.playPlayerCountMap.containsKey(entry.getKey())) {
                tempPlayerUids = this.playPlayerCountMap.get(entry.getKey());
            } else {
                tempPlayerUids = new HashSet<Long>();
                this.playPlayerCountMap.put(entry.getKey(), tempPlayerUids);
            }
            tempPlayerUids.addAll(entry.getValue());
        }
    }

    protected ErrorCode hasEnoughDiamond(IClub fromClub, long playerUid) {
        IBoxOwner boxOwner = room.getBoxOwner();
        if (null == boxOwner) {
            return ErrorCode.REQUEST_INVALID;
        }
        // 判断玩家圈主的房卡是否足够
        int costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ROOM, this.room.getBureau());
        if (!fromClub.hasEnoughMoney(EMoneyType.DIAMOND, costDiamond)) {
            Logs.ROOM.debug("%d 群主钻石不足,无法加入", playerUid);
            return ErrorCode.GROUP_CHIEF_LACK_DIAMOND;
        }
        return ErrorCode.OK;
    }

    @Override
    public void onSitup() {
        IRoomPlayer[] allPlayer = this.room.getAllPlayer();
        for (int i = 0; i < this.room.getPlayerNum(); ++i) {
            IRoomPlayer roomPlayer = allPlayer[i];
            if (null == roomPlayer) {
                continue;
            }
            
            if (room.isReady(roomPlayer.getUid())) {
                continue;
            }
            this.readyHandle(roomPlayer.getUid(), false);
            break;
        }
    }

    /**
     * 检查是否离开
     */
    protected void checkLeave() {
        if (room.getRoomState() != ERoomState.START && room.getRoomState() != ERoomState.DESTROY
                /*&& room.getCurBureau() == 0*/ && startReadyTime > 0 && System.currentTimeMillis() >= startReadyTime
                && this.room.getReadySize() > 0 && System.currentTimeMillis() >= lastCheckLeaveTime) {
            if (!GameType.isArenaGame(room.getGameType())) {
                if (room.getCurBureau() != 0) {
                    return;
                }
            }
            if (room.getGameSubType() == 1 && room.getGameType() == GameType.GAME_TYPE_COW){
                return;
            }
            lastCheckLeaveTime = System.currentTimeMillis() + leaveTime;
            boolean flag = Boolean.FALSE;
            Player someone = null;
            List<Long> leaveUids = null;
            ReentrantReadWriteLock.WriteLock writeLock = this.room.getLock().writeLock();
            try {
                if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                    IRoomPlayer[] allPlayer = this.room.getAllPlayer();
                    for (int i = 0; i < this.room.getPlayerNum(); ++i) {
                        IRoomPlayer roomPlayer = allPlayer[i];
                        if (null == roomPlayer) {
                            continue;
                        }
                        someone = null == someone ? PlayerManager.I.getPlayer(roomPlayer.getUid()) : someone;
                        if (room.isReady(roomPlayer.getUid())) {
                            continue;
                        }

                        if (room.getPlayerNum() == room.getAndDecrPlayerCnt()) {
                            room.changeState(ERoomListState.CAN_ADD);
                        }
                        allPlayer[roomPlayer.getIndex()] = null;
                        Player tempPlayer = PlayerManager.I.getPlayer(roomPlayer.getUid());
                        if (tempPlayer != null) {
                            if (tempPlayer.getRoomId() == this.getRoomId()){
                                tempPlayer.changeRoomId(-1, -1);
                            } else {
                                System.err.println("checklevel异常changeRoomId，playerId="+tempPlayer.getUid()+",roomId="+room.getRoomId()+",player.getRoomId()="+tempPlayer.getRoomId());
                            }
                        }
                        flag = flag || Boolean.TRUE;
                        PCLIRoomNtfLeaveStateInfo info = new PCLIRoomNtfLeaveStateInfo();
                        info.state = 1;
                        roomPlayer.send(CommandId.CLI_NTF_ROOM_LEAVE_V2_OK, info);
                        if (null == leaveUids) {
                            leaveUids = new ArrayList<Long>();
                        }
                        leaveUids.add(roomPlayer.getUid());

                        if (leaveUids.contains(someone.getUid())) {
                            someone = null;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }

            if (flag) {
                room.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, room.getRoomInfo());
                if (leaveUids != null) {
                    if (box != null) {
                        for (long playerUid : leaveUids) {
                            box.leave(playerUid, room.getRoomId());
                        }
                    }
                }

                if (someone != null) {
                    if (someone.getRoomId() == room.getRoomId()) {
                        readyHandle(someone.getUid(), Boolean.FALSE);
                    } else {
                        System.err.println("checklevel异常，playerId="+someone.getUid()+",roomId="+room.getRoomId()+",player.getRoomId()="+someone.getRoomId());
                    }
                }
                if (this.room.getPlayerCnt() < 2) {
                    resetCheckLeaveTime();
                }
            }
        }
    }

    private void resetCheckLeaveTime() {
        this.lastCheckLeaveTime = 0;
        this.startReadyTime = 0;
    }

    @Override
    public void startCheckLeave() {
        //牛牛金花十三水牌九
        if (GameType.isArenaGame(this.room.getGameType())) {
            if (this.room.getPlayerCnt() >= 3 && this.room.getReadySize() >= 2 && this.startReadyTime == 0) {
                startReadyTime = System.currentTimeMillis() + leaveTime;
                lastCheckLeaveTime = startReadyTime;
            }
            return;
        }
        if (this.room.getPlayerCnt() >= 3 && this.room.getReadySize() >= 1
                || (this.room.getReadySize() == 1 && this.room.getPlayerCnt() >= 2 && this.startReadyTime == 0)) {
            startReadyTime = System.currentTimeMillis() + leaveTime;
            lastCheckLeaveTime = startReadyTime;
        }
    }

    @Override
    public void doDestroy() {
        // 返钱
        returnDiamond();
    }

    protected void returnDiamond() {
        if (hasCostDiamond) {
            IBoxOwner boxOwner = this.room.getBoxOwner();
            IClub mainClub = ClubManager.I.getMainClub(boxOwner);
            if (this.room.getFinishBureauCount() < 5) {
                // 返钱
                returnDiamond(mainClub);
            } else {
                // 是否是金币亲友圈创建的游戏桌
                if (mainClub.getClubType().match(EClubType.GOLD)) {
                    // 金币亲友圈包厢游戏桌的房卡处理逻辑
                    // doGoldClubMoneyOnDestory();
                } else {
                    // 房卡亲友圈包厢游戏桌的房卡处理逻辑
                    doCardClubMoneyOnDestory();
                }
            }
        }
    }

    /**
     * 返钱
     * @param mainClub
     */
    private void returnDiamond(IClub mainClub) {
        if (mainClub.getClubType().match(EClubType.CARD) ) {
            returnDiamondByCardClub(mainClub);
        } else {
            returnDiamondByGoldClub(mainClub);
        }
    }

    private void returnDiamondByGoldClub(IClub mainClub) {
        if (playPlayerCountMap.isEmpty()) {
            return;
        }
        int costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ROOM, this.room.getBureau());
        // 加钱
        mainClub.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, costDiamond, mainClub.getOwnerId(), mainClub.getClubUid(), EMoneyExpendType.GROUP_EXPEND_RETURN, -1);
    }

    private void returnDiamondByCardClub(IClub mainClub) {
        if (playPlayerCountMap.isEmpty()) {
            return;
        }
        int costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ROOM, this.room.getBureau());
        Iterator<Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, Set<Long>> entry = iter.next();
            IClub club = ClubManager.I.getClubByUid(entry.getKey());
            if (null == club) {
                continue;
            }
            // 加钱
            club.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, costDiamond, club.getOwnerId(), club.getClubUid(), EMoneyExpendType.GROUP_EXPEND_RETURN, -1);
        }
    }

    /**
     * 金币亲友圈包厢游戏桌的房卡处理逻辑(比赛场)
     */
    private void doGoldClubMoneyOnDestory() {
        int totalCount = 0;
        Iterator<Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, Set<Long>> entry = iter.next();
            totalCount += entry.getValue().size();
        }
        if (totalCount > 0) {
            int costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ROOM, this.room.getBureau());
            float average = 1f * costDiamond / totalCount;
            iter = this.playPlayerCountMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iter.next();
                IClub club = ClubManager.I.getClubByUid(entry.getKey());
                if (null == club) {
                    continue;
                }
                float addDiamond = costDiamond - average * entry.getValue().size();
                // 加钱
                if (addDiamond > 0) {
                    club.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, addDiamond, club.getOwnerId(), club.getClubUid(),
                            EMoneyExpendType.GROUP_EXPEND_RETURN, -1);
                }
            }
            // 记录金币圈扣房卡记录
            saveGoldClubMoneyExpendRecordDetail(costDiamond, average);
        }
        // 添加金币亲友圈大赢家记录
        addGoldBoxFinalWinner();
    }

    private void addGoldBoxFinalWinner() {
        BoxRoomScore roomScore = (BoxRoomScore) this.room.getRoomScore();
        // 获取大赢家玩家Uid列表
        List<Long> winPlayerUids = roomScore.getWinPlayerUids();
        if (!winPlayerUids.isEmpty()) {
            IBoxOwner boxOwner = this.room.getBoxOwner();
            IClub mainClub = ClubManager.I.getMainClub(boxOwner);
            // 是否只有一个大赢家
            if (winPlayerUids.size() == 1) {
                long winPlayerUid = winPlayerUids.get(0);
                // 获取大赢家玩家参与游戏的亲友圈id
                long wingPlayerClubUid = mainClub.getEnterFromClubUid(winPlayerUid);
                mainClub.addBoxFinalWinner(wingPlayerClubUid, winPlayerUid);
                // 更新战况大赢家玩家所属的群uid
                updateScoreFinalWinnerClubUid(wingPlayerClubUid);
            } else {
                // 是否合圈
                if (mainClub.checkIsMainClub()) {
                    // 已合圈
                    // 判断是否有大赢家属于盟主圈
                    if (checkWinPlayerInMainClub(mainClub, winPlayerUids)) {
                        mainClub.addBoxFinalWinner(mainClub.getClubUid(), getFinalWinPlayerUid(mainClub, winPlayerUids));
                        // 更新战况大赢家玩家所属的群uid
                        updateScoreFinalWinnerClubUid(mainClub.getClubUid());
                    } else {
                        // 根据大赢家所在亲友圈加入主圈的时间（最早合圈时间）排序，取最早的为大赢家（房卡消耗统计使用）
                        long finalWinClubUid = getFinalWinClubUid(mainClub, winPlayerUids);
                        mainClub.addBoxFinalWinner(finalWinClubUid, getFinalWinPlayerUid(ClubManager.I.getClubByUid(finalWinClubUid), winPlayerUids));
                        // 更新战况大赢家玩家所属的群uid
                        updateScoreFinalWinnerClubUid(finalWinClubUid);
                    }
                } else {
                    // 未合圈
                    // 根据每个大赢家加入本亲友圈的时间（最早加入时间靠前）排序，取最早的为大赢家（房卡消耗统计使用）
                    long finalWinPlayerUid = getFinalWinPlayerUid(mainClub, winPlayerUids);
                    mainClub.addBoxFinalWinner(mainClub.getClubUid(), finalWinPlayerUid);
                    // 更新战况大赢家玩家所属的群uid
                    updateScoreFinalWinnerClubUid(mainClub.getClubUid());
                }
            }
        }
    }

    /**
     * 更新战况大赢家玩家所属的群uid
     *
     * @param finalWinnerClubUid
     */
    private void updateScoreFinalWinnerClubUid(long finalWinnerClubUid) {
        final BoxRoomScore tempRoomScore = (BoxRoomScore) this.room.getRoomScore();
        if (tempRoomScore instanceof BoxRoomScore) {
            tempRoomScore.setGroupUid(finalWinnerClubUid);
            DBManager.I.save(() -> {
                DBManager.I.getBoxRoomScoreDao().save((BoxRoomScore) tempRoomScore);
            });
        }
    }

    /**
     * 记录金币圈扣房卡记录
     *
     * @param costDiamond
     * @param average
     */
    private void saveGoldClubMoneyExpendRecordDetail(int costDiamond, float average) {
        if (average == 0) {
            return;
        }
        Iterator<Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
        List<MoneyExpendRecordDetail> details = new ArrayList<MoneyExpendRecordDetail>();
        long nowTime = System.currentTimeMillis();
        while (iter.hasNext()) {
            Map.Entry<Long, Set<Long>> entry = iter.next();
            for (long playerUid : entry.getValue()) {
                IClub club = ClubManager.I.getClubByUid(entry.getKey());
                MoneyExpendRecordDetail detail = new MoneyExpendRecordDetail();
                detail.setUid(UIDManager.I.getAndInc(UIDType.MONEY_EXPEND_RECORD_DETAIL));
                detail.setClubType(club.getClubType().getType());
                detail.setClubUid(entry.getKey());
                detail.setPlayerUid(playerUid);
                detail.setRoomUid(this.room.getRoomUid());
                detail.setValue(average);
                detail.setType(0);
                detail.setTime(nowTime);
                details.add(detail);
            }
        }
        DBManager.I.save(() -> {
            DBManager.I.getMoneyExpendRecordDetailDao().batchInsert(details);
        });
    }

    /**
     * 销毁房间时,房卡亲友圈包厢游戏桌的房卡处理逻辑
     *
     * <pre>
     *
     * if (是否只有一个大赢家){
     *  // 非大赢家房卡退还，大赢家玩家不退还房卡（房卡消耗统计使用）
     * } else{
     *    if(是否合圈){
     *      // 已合圈
     *      if(判断是否有大赢家属于盟主圈){
     *          // 有
     *          非盟主圈返还，盟主圈不返还（房卡消耗统计使用）
     *      } else{
     *          // 无
     *          根据大赢家所在亲友圈加入主圈的时间（最早合圈时间）排序，取最早的为大赢家（房卡消耗统计使用）
     *          大赢家所在亲友圈不返还，其他圈返还
     *      }
     *  } else{
     *      // 未合圈
     *      根据每个大赢家加入本亲友圈的时间（最早加入时间靠前）排序，取最早的为大赢家（房卡消耗统计使用）
     *  }
     * }
     * </pre>
     */
    private void doCardClubMoneyOnDestory() {
        BoxRoomScore boxRoomScore = (BoxRoomScore) this.room.getRoomScore();
        // 获取大赢家玩家Uid列表
        List<Long> winPlayerUids = boxRoomScore.getWinPlayerUids();
        if (winPlayerUids.isEmpty()) {
            return;
        }
        IBoxOwner boxOwner = this.room.getBoxOwner();
        IClub mainClub = ClubManager.I.getMainClub(boxOwner);
        int costDiamond = DiamondCostManager.I.getCostByGameType(this.room.getGameType(), DiamondCostManager.COST_TYPE_ROOM, this.room.getBureau());
        // 是否只有一个大赢家
        if (winPlayerUids.size() == 1) {
            long winPlayerUid = winPlayerUids.get(0);
            // 获取大赢家玩家参与游戏的亲友圈id
            long wingPlayerClubUid = mainClub.getEnterFromClubUid(winPlayerUid);
            // 非大赢家房卡退还，大赢家玩家不退还房卡（房卡消耗统计使用）
            Iterator<Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iter.next();
                // 是否是大赢家圈
                if (wingPlayerClubUid == entry.getKey()) {
                    continue;
                } else {
                    IClub club = ClubManager.I.getClubByUid(entry.getKey());
                    if (null == club) {
                        continue;
                    }
                    // 加钱
                    club.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, costDiamond, club.getOwnerId(),
                            club.getClubUid(), EMoneyExpendType.GROUP_EXPEND_RETURN, -1);
                }
            }
            // 记录扣房卡记录
            saveCardClubMoneyExpendRecordDetail(wingPlayerClubUid, costDiamond);
            mainClub.addBoxFinalWinner(wingPlayerClubUid, winPlayerUid);
            // 更新战况大赢家玩家所属的群uid
            updateScoreFinalWinnerClubUid(wingPlayerClubUid);
        } else {
            // 是否合圈
            if (mainClub.checkIsMainClub()) {
                // 已合圈
                // 判断是否有大赢家属于盟主圈
                if (checkWinPlayerInMainClub(mainClub, winPlayerUids)) {
                    // 非盟主圈返还，盟主圈不返还（房卡消耗统计使用）
                    Iterator<Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Long, Set<Long>> entry = iter.next();
                        if (entry.getKey() == mainClub.getClubUid()) {
                            continue;
                        }
                        IClub club = ClubManager.I.getClubByUid(entry.getKey());
                        if (null == club) {
                            continue;
                        }
                        // 加钱
                        club.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, costDiamond, club.getOwnerId(),
                                club.getClubUid(), EMoneyExpendType.GROUP_EXPEND_RETURN, -1);
                    }
                    // 记录扣房卡记录
                    saveCardClubMoneyExpendRecordDetail(mainClub.getClubUid(), costDiamond);
                    mainClub.addBoxFinalWinner(mainClub.getClubUid(), getFinalWinPlayerUid(mainClub, winPlayerUids));
                    // 更新战况大赢家玩家所属的群uid
                    updateScoreFinalWinnerClubUid(mainClub.getClubUid());
                } else {
                    // 根据大赢家所在亲友圈加入主圈的时间（最早合圈时间）排序，取最早的为大赢家（房卡消耗统计使用）
                    long finalWinClubUid = getFinalWinClubUid(mainClub, winPlayerUids);
                    // 大赢家所在亲友圈不返还，其他圈返还
                    Iterator<Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Long, Set<Long>> entry = iter.next();
                        if (entry.getKey() == finalWinClubUid) {
                            continue;
                        }
                        IClub club = ClubManager.I.getClubByUid(entry.getKey());
                        if (null == club) {
                            continue;
                        }
                        // 加钱
                        club.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, costDiamond, club.getOwnerId(),
                                club.getClubUid(), EMoneyExpendType.GROUP_EXPEND_RETURN, -1);
                    }
                    // 记录扣房卡记录
                    saveCardClubMoneyExpendRecordDetail(finalWinClubUid, costDiamond);
                    mainClub.addBoxFinalWinner(finalWinClubUid,
                            getFinalWinPlayerUid(ClubManager.I.getClubByUid(finalWinClubUid), winPlayerUids));
                    // 更新战况大赢家玩家所属的群uid
                    updateScoreFinalWinnerClubUid(finalWinClubUid);
                }
            } else {
                // 未合圈
                // 根据每个大赢家加入本亲友圈的时间（最早加入时间靠前）排序，取最早的为大赢家（房卡消耗统计使用）
                long finalWinPlayerUid = getFinalWinPlayerUid(mainClub, winPlayerUids);
                mainClub.addBoxFinalWinner(mainClub.getClubUid(), finalWinPlayerUid);
                // 记录扣房卡记录
                saveCardClubMoneyExpendRecordDetail(mainClub.getClubUid(), costDiamond);
                // 更新战况大赢家玩家所属的群uid
                updateScoreFinalWinnerClubUid(mainClub.getClubUid());
            }
        }
    }

    /**
     * 记录房卡圈扣房卡记录
     *
     * @param clubUid
     * @param costDiamond
     */
    private void saveCardClubMoneyExpendRecordDetail(long clubUid, int costDiamond) {
        IClub club = ClubManager.I.getClubByUid(clubUid);
        int clubType = club.getClubType().getType();
        Set<Long> playerUids = playPlayerCountMap.get(clubUid);
        int size = playerUids.size();
        if (size > 0) {
            float average = 1f * costDiamond / size;
            List<MoneyExpendRecordDetail> details = new ArrayList<MoneyExpendRecordDetail>();
            long nowTime = System.currentTimeMillis();
            for (long playerUid : playerUids) {
                MoneyExpendRecordDetail detail = new MoneyExpendRecordDetail();
                detail.setUid(UIDManager.I.getAndInc(UIDType.MONEY_EXPEND_RECORD_DETAIL));
                detail.setClubType(clubType);
                detail.setClubUid(clubUid);
                detail.setPlayerUid(playerUid);
                detail.setRoomUid(this.room.getRoomUid());
                detail.setValue(average);
                detail.setType(0);
                detail.setTime(nowTime);
                details.add(detail);
            }
            DBManager.I.save(() -> {
                DBManager.I.getMoneyExpendRecordDetailDao().batchInsert(details);
            });
        }
    }

    /**
     * 根据每个大赢家加入本亲友圈的时间（最早加入时间靠前）排序，取最早的为大赢家id
     *
     * @param mainClub
     * @param winPlayerUids
     * @return
     */
    private long getFinalWinPlayerUid(IClub mainClub, List<Long> winPlayerUids) {
        Map<Long, Long> joinParentTimes = new TreeMap<Long, Long>();
        for (long winPlayerUid : winPlayerUids) {
            ClubMember member = mainClub.getMember(winPlayerUid);
            if (null == member) {
                continue;
            }
            // 获取该圈加入主圈的时间
            joinParentTimes.put(winPlayerUid, member.getJoinTime());
        }
        List<Map.Entry<Long, Long>> joinParentTimeList = new ArrayList<Map.Entry<Long, Long>>(
                joinParentTimes.entrySet());
        Collections.sort(joinParentTimeList, new Comparator<Map.Entry<Long, Long>>() {
            // 升序排序
            @Override
            public int compare(Entry<Long, Long> o1, Entry<Long, Long> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });
        return joinParentTimeList.get(0).getKey();
    }

    /**
     * 根据大赢家所在亲友圈加入主圈的时间（最早合圈时间）排序，取最早的为大赢家圈id
     *
     * @param mainClub
     * @param winPlayerUids
     * @return
     */
    private long getFinalWinClubUid(IClub mainClub, List<Long> winPlayerUids) {
        Map<Long, Long> joinParentTimes = new TreeMap<Long, Long>();
        for (long winPlayerUid : winPlayerUids) {
            long wingPlayerClubUid = mainClub.getEnterFromClubUid(winPlayerUid);
            IClub winClub = ClubManager.I.getClubByUid(wingPlayerClubUid);
            // 获取该圈加入主圈的时间
            joinParentTimes.put(wingPlayerClubUid, winClub.getClubInfo().getJoinParentTime());
        }
        List<Map.Entry<Long, Long>> joinParentTimeList = new ArrayList<Map.Entry<Long, Long>>(
                joinParentTimes.entrySet());
        Collections.sort(joinParentTimeList, new Comparator<Map.Entry<Long, Long>>() {
            // 升序排序
            @Override
            public int compare(Entry<Long, Long> o1, Entry<Long, Long> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });
        return joinParentTimeList.get(0).getKey();
    }

    private boolean checkWinPlayerInMainClub(IClub mainClub, List<Long> winPlayerUids) {
        for (long winPlayerUid : winPlayerUids) {
            // 获取大赢家玩家参与游戏的亲友圈id
            long wingPlayerClubUid = mainClub.getEnterFromClubUid(winPlayerUid);
            if (wingPlayerClubUid == mainClub.getClubUid()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public void doFinishAfter(boolean isNormal, boolean isNewBureau) {
        if (EBoxType.NORMAL.match(box.getBoxType()) || EBoxType.CUSTOM.match(box.getBoxType())) {
            doFinishAfterHandle(isNormal, isNewBureau);

            ChatManager.I.notifyBoxRoomScore(this.room.getGroupUid(), (BoxRoomScore) this.room.getRoomScore());

            doBoxRoomFinishHandle();
        }
    }

    /**
     * 包厢游戏桌结束时调用 （大局结束或中途解散房间时）
     */
    protected void doBoxRoomFinishHandle() {
        IBoxOwner boxOwner = this.room.getBoxOwner();
        if (boxOwner != null && !this.playPlayerCountMap.isEmpty()) {
            Set<Long> playerUids = new HashSet<Long>();
            Iterator<Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iter.next();
                playerUids.addAll(entry.getValue());
            }

            BoxRoomScore boxRoomScore = (BoxRoomScore) this.room.getRoomScore();
            IClub club = ClubManager.I.getClubByUid(room.getGroupUid());
            if (club != null) {
                long now = System.currentTimeMillis();
                for (Long playerUid : playerUids) {
                    club.addBoxScoreAndBureau(club.getEnterFromClubUid(playerUid), playerUid,
                            boxRoomScore.getScore(playerUid), this.room.getCurBureau(), now);
                }
            }
        }
    }

    @Override
    public long getFromClubUid(long playerUid) {
        IBoxOwner boxOwner = this.room.getBoxOwner();
        if (null == boxOwner) {
            return -1;
        }
        IClub mainClub = ClubManager.I.getMainClub(boxOwner);
        return mainClub.getEnterFromClubUid(playerUid);
    }

    @Override
    public boolean hasPlayed(long playerUid) {
        return isPlayedPlayer(playerUid);
    }
    
    protected boolean isPlayedPlayer(long playerUid) {
        Iterator<Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, Set<Long>> entry = iter.next();
            if (entry.getValue().contains(playerUid)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public boolean noGuest(long playerUid) {
//        Iterator<Map.Entry<Long, Set<Long>>> iter = this.playPlayerCountMap.entrySet().iterator();
////        while (iter.hasNext()) {
////            Map.Entry<Long, Set<Long>> entry = iter.next();
////            if (entry.getValue().contains(playerUid)) {
////                return Boolean.TRUE;
////            }
////        }
////        return Boolean.FALSE;
        if (!GameType.isArenaGame(this.room.getGameType())) {
            return false;
        }
        for (int i = 0; i < this.room.getPlayerNum(); i++) {
            IRoomPlayer temp = this.room.getAllPlayer()[i];
            if (temp == null) {
                continue;
            }
            if (temp.getUid() == playerUid && temp.isGuest()) {
                if (ERoomState.START == this.room.getRoomState()) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public void saveRoomScore() {
        IRoomScore tempRoomScore = this.room.getRoomScore();
        if (null != tempRoomScore) {
            BoxRoomScore roomScore = (BoxRoomScore) tempRoomScore;
            saveRoomScore(roomScore);
            BoxManager.I.addScore((BoxRoomScore) tempRoomScore);
            if (this.room.getPlayerMinNum() == 2 && this.room.getPlayerNum() == 3) {
                tempRoomScore.setRoomType(1);
            }
            final BoxRoomScore saveBoxRoomScore = roomScore;
            DBManager.I.save(new Task() {
                @Override
                public void run() {
                    DBManager.I.getBoxRoomScoreDao().save(saveBoxRoomScore);
                }
            });
        }

    }

    @Override
    public ErrorCode canDissolve(Player player) {
        if (room.getFinishBureauCount() > 0 && !hasPlayed(player.getUid())) {
            return ErrorCode.REQUEST_INVALID;
        }
        return ErrorCode.OK;
    }

    @Override
    public EBoxType getBoxType() {
        return EBoxType.getType(this.box.getBoxType());
    }

    @Override
    public ErrorCode sitDown(IPlayer player, int index) {
        return this.room.sitDown(player, index);
    }

    protected ErrorCode hasMeetCondition(long playerUid){
        IRoomPlayer roomPlayer = this.box.getRoomPlayer(playerUid);
        if (null == roomPlayer) {
            Logs.ROOM.warn("%d 不在房间里，无法准备", playerUid);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        IArenaRoomPlayerHelper arenaRoomPlayerHelper = (IArenaRoomPlayerHelper) roomPlayer.getRoomPlayerHelper();
        int needArenaValu = this.room.getRule().getOrDefault(RoomRule.RR_MINGOLD, 0);
        if (null != arenaRoomPlayerHelper && arenaRoomPlayerHelper.getGold() < needArenaValu) {
            Logs.ROOM.warn("%s %s 竞技值不足， 无法准备 ， 需要[%d %d]", this, arenaRoomPlayerHelper, needArenaValu, arenaRoomPlayerHelper.getGold());
            return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
        }
        return ErrorCode.OK;
    }
    
    @Override
    public long getReadyTime() {
        long now = System.currentTimeMillis();
        if (startReadyTime > now) {
            return startReadyTime - now;
        }
        return 0L;
    }
    
    @Override
    public void tickHandle(long curTime, long delay) {
        if (this.box != null && this.room != null) {
            this.box.tick(curTime, delay, this.room);
            this.checkLeave();
        }
    }
    
}
