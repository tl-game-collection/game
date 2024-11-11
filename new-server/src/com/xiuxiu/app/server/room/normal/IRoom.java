package com.xiuxiu.app.server.room.normal;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomInfo;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.EDissolve;
import com.xiuxiu.app.server.room.ERoomDestroyType;
import com.xiuxiu.app.server.room.ERoomListState;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.score.IRoomScore;

public interface IRoom {
    
    /**
     * 初始化
     */
    void init();
    /**
     * 获取房间所属群uid
     *
     * @return
     */
    long getGroupUid();

    /**
     * 获取房间uid
     *
     * @return
     */
    long getRoomUid();

    /**
     * 获取房间id
     *
     * @return
     */
    int getRoomId();

    /**
     * 房间类型
     *
     * @return
     */
    ERoomType getRoomType();

    /**
     * 获取游戏类型
     *
     * @return
     */
    int getGameType();

    /**
     * 获取游戏类型子类
     *
     * @return
     */
    int getGameSubType();

    /**
     * 获取房间规则
     *
     * @return
     */
    HashMap<String, Integer> getRule();

    /**
     * 房间状态
     *
     * @return
     */
    ERoomState getRoomState();

    /**
     * 获取总局数
     *
     * @return
     */
    int getBureau();

    /**
     * 获取当前局数
     *
     * @return
     */
    int getCurBureau();

    /**
     * 创建房间玩家数据
     *
     * @return
     */
    IRoomPlayer createPlayer();

    /**
     * 开始
     */
    void start();

    /**
     * 结束
     */
    void stop();

    /**
     * 再来一局
     */
    void again();

    /**
     * 完成
     */
    void finish();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 清理
     */
    void clear();

    /**
     * 竞技场连续打结束
     */
    void clearByArenaOver();

    /**
     * 保存
     */
    boolean save();

    /**
     * 加入房间
     *
     * @param player
     * @return
     */
    ErrorCode join(Player player);

    /**
     * 换座位
     * @param player
     * @param newSeatIndex
     * @return
     */
    ErrorCode changeSeate(Player player,int newSeatIndex);

    /**
     * 坐下
     *
     * @param player
     * @param index
     * @return
     */
    ErrorCode sitDown(IPlayer player, int index);

    /**
     * 坐下
     *
     * @param player
     * @param index
     * @return
     */
    ErrorCode sitDown(IRoomPlayer player, int index);

    /**
     * 站起
     *
     * @param player
     * @return
     */
    ErrorCode sitUp(IPlayer player);

    /**
     * 站起
     *
     * @param player
     * @return
     */
    default ErrorCode sitUp(IRoomPlayer player) {
        return this.sitUp(player, false);
    }

    /**
     * 站起
     *
     * @param player
     * @param force
     * @return
     */
    ErrorCode sitUp(IRoomPlayer player, boolean force);

    /**
     * 离开房间
     *
     * @param player
     * @return
     */
    ErrorCode leave(Player player);

    /**
     * 准备
     *
     * @param player
     * @return
     */
    ErrorCode ready(Player player);
    
    /**
     * 踢人
     *
     * @param player
     * @param killPlayerUid
     * @return
     */
    ErrorCode kill(Player player, long killPlayerUid);

    /**
     * 请求解散
     *
     * @param player
     * @param force
     * @return
     */
    ErrorCode dissolve(Player player, boolean force);

    /**
     * 解散操作
     *
     * @param player
     * @param op
     * @return
     */
    ErrorCode dissolveOperate(Player player, EDissolve op);

    /**
     * 改变状态
     *
     * @param player
     * @param state
     * @return
     */
    ErrorCode changeState(Player player, EState state);

    /**
     * 改变状态
     *
     * @param player
     * @param state
     * @return
     */
    ErrorCode changeState(IRoomPlayer player, EState state);

    /**
     * 观察者同步桌子信息
     *
     * @param player
     * @return
     */
    void watchPlayerSyncDeskInfo(IRoomPlayer player);

    /**
     * 炫耀
     *
     * @param player
     * @return
     */
    ErrorCode showOff(IPlayer player);

    /**
     * 执行炫耀
     *
     * @param player
     * @return
     */
    ErrorCode onShowOff(IRoomPlayer player);

    /**
     * 炫耀完成
     */
    void doShowOffOver();

    /**
     * 执行动作
     *
     * @param op
     * @param info
     * @return
     */
    ErrorCode doHandler(EActionOp op, Object info);

    /**
     * 是否新的
     *
     * @return
     */
    boolean isNew();

    /**
     * 是否开始
     *
     * @return
     */
    boolean isStart();

    /**
     * 是否完成
     *
     * @return
     */
    boolean isFinish();

    /**
     * 是否销毁
     *
     * @return
     */
    boolean isDestroy();

    /**
     * 是否再次开始
     *
     * @return
     */
    boolean isAgain();


    /**
     * 是否满了
     *
     * @return
     */
    boolean isFull();

    /**
     * 是否为空
     *
     * @return
     */
    boolean isEmpty();

    int getPlayerCnt();

    boolean isWatchEmpty();

    /**
     * 中途是否可加入
     *
     * @return
     */
    boolean canWatch();

    /**
     * 获取房间最大人数
     *
     * @return
     */
    int getMaxPlayerCnt();

    /**
     * 获取房间最小人数
     *
     * @return
     */
    int getMinPlayerCnt();

    /**
     * 当前房间人数
     *
     * @return
     */
    int getCurPlayerCnt();

    /**
     * 获取庄家索引
     *
     * @return
     */
    int getBankerIndex();

    /**
     * 根据playerUid获取roomPlayer
     *
     * @param playerUid
     * @return
     */
    IRoomPlayer getRoomPlayer(long playerUid);

    /**
     * 根据playerIndex获取roomPlayer
     *
     * @param playerIndex
     * @return
     */
    IRoomPlayer getRoomPlayer(int playerIndex);

    /**
     * 获取下一个索引roomPlayer
     *
     * @param index
     * @return
     */
    IRoomPlayer getNextRoomPlayer(int index);

    /**
     * 获取战绩
     *
     * @return
     */
    IRoomScore getRoomScore();

    /**
     * 获取回放记录
     *
     * @return
     */
    Record getRecord();

    /**
     * 广播
     *
     * @param commandId
     * @param message
     */
    void broadcast2Client(int commandId, Object message);

    /**
     * 广播
     *
     * @param commandId
     * @param message
     * @param syncWatch
     */
    void broadcast2Client(int commandId, Object message, boolean syncWatch);

    /**
     * 获取当前在玩列表
     *
     * @return
     */
    List<IRoomPlayer> getCurrPlayers();

    /**
     * 是否为观战玩家
     *
     * @param player
     * @return
     */
    boolean isWatchPlayer(IRoomPlayer player);
    
    /**
     * 获取当前在玩玩家id列表
     *
     * @return
     */
    List<Long> getCurrPlayerIds();
    
    /**
     * 获取该房间的亲友圈所有者
     *
     * @return
     */
    IBoxOwner getBoxOwner();
    
    /**
     * 获取来源亲友圈uid
     * @param playerUid
     * @return
     */
    long getFromClubUid(long playerUid);
    
    default int getRecordScore(IRoomPlayer player) {
        return player.getScore(Score.SCORE, false);
    }
    
    Set<Long> getGuestPlayerUids();
    
    IRoomHandle getRoomHandle();
    
    void setRoomHandle(IRoomHandle roomHandle);
    
    int getPlayerNum();
    
    ReentrantReadWriteLock getLock();
    
    IRoomPlayer[] getAllPlayer();
    
    int addPlayerCnt();
    
    int getAndDecrPlayerCnt();
    
    void addWatchPlayerUid(long uid);
    
    boolean removeWatch(long uid);
    
    boolean removeReady(long uid);
    void changeState(ERoomListState state);
    void checkStart();
    PCLIRoomInfo getRoomInfo();
    
    boolean checkIsDestroy();
    void setTemporaryPropertyValue(long playerUid, String propertyType, Integer value);
    Integer getTemporaryPropertyValue(long playerUid, String propertyType);
    void clearGuest(IRoomPlayer player);
    
    void addReadyPlayerUid(long playerUid);
    
    boolean isReady(long plauerUid);
    
    int getReadySize();
    
    int getPlayerMinNum();
    
    long getOwnerPlayerUid();
    
    int getCost();
    
    int getFinishBureauCount();
    
    void setRoomType(ERoomType roomType);

    Stack<IAction> getAction();

    void tick();

    boolean getDetectionIP();
    
    ERoomDestroyType getRoomDestoryType();
    
    long getDestroyUid();

    long getPlayerGold(long playerUid);

}
