package com.xiuxiu.app.server.room.normal;

import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.helper.IRoomPlayerHelper;

public interface IRoomPlayer {
    /**
     * 设置玩家信息
     * @param player
     */
    void setPlayer(IPlayer player);

    /**
     * 获取玩家信息
     * @return
     */
    IPlayer getPlayer();

    /**
     * 玩家uid
     * @return
     */
    long getUid();

    /**
     * 设置房间
     * @param room
     */
    void setRoom(IRoom room);

    /**
     * 获取房间uid
     * @return
     */
    long getRoomUid();

    /**
     * 获取房间id
     * @return
     */
    int getRoomId();
    
    int getGameType();

    /**
     * 在房间里的位置
     * @return
     */
    int getIndex();

    /**
     * 设置在房间里的位置
     * @param index
     */
    void setIndex(int index);

    /**
     * 设置游客
      * @param guest
     */
    void setGuest(boolean guest);

    /**
     * 是否是游客
     * @return
     */
    boolean isGuest();

    /**
     * 设置是否结束
     * @param over
     */
    void setOver(boolean over);

    /**
     * 获取是否结束
     * @return
     */
    boolean isOver();

    /**
     * 开始
     */
    void doStart();

    /**
     * 切换状态
     * @param state
     * @return
     */
    boolean changeState(EState state);

    /**
     * 是否离线
     * @return
     */
    boolean isOffline();

    /**
     * 获取当前状态
     * @return
     */
    EState getState();

    /**
     * 当前局数
     * @return
     */
    int getBureau();

    /**
     * 设置是否托管
     * @param hosting
     */
    void setHosting(boolean hosting);

    /**
     * 是否托管
     * @param timeout
     * @return
     */
    boolean isHosting(int timeout);

    /**
     * 获取超时(应该放在IRoomPlayer接口中)
     * @param timeout
     * @return
     */
    long getTimeout(long timeout);

    /**
     * 操作超时
     */
    void operationTimeout();

    /**
     * 超时次数
     * @return
     */
    int getOperationTimeoutCnt();

    /**
     * 清理操作超时次数
     */
    void clearOperationTimeoutCnt();

    /**
     * 距离
     * @param other
     * @return
     */
    int betweenDistance(IRoomPlayer other);

    /**
     * 获取纬度
     * @return
     */
    double getLat();

    /**
     * 获取经度
     * @return
     */
    double getLng();

    /**
     * 清理
     */
    void clear();

    /**
     * 竞技场连续打结束
     */
    void clearByArenaOver();

    /**
     * 保持前一句信息
     */
    default void savePrevInfo() {

    }

    /**
     * 设置分数
     * @param score
     * @param value
     * @param isAcc
     */
    void setScore(String score, int value, boolean isAcc);

    /**
     * 添加分数
     * @param score
     * @param value
     * @param isAcc
     */
    void addScore(String score, int value, boolean isAcc);

    /**
     * 设置最大分数
     * @param score
     * @param value
     * @param isAcc
     */
    void maxScore(String score, int value, boolean isAcc);

    /**
     * 设置最小分数
     * @param score
     * @param value
     * @param isAcc
     */
    void minScore(String score, int value, boolean isAcc);

    /**
     * 获取分数
     * @param score
     * @param isAcc
     * @return
     */
    int getScore(String score, boolean isAcc);

    /**
     * 获取分数
     * @return
     */
    int getScore();

    /**
     * 发送数据
     * @param commandId
     * @param message
     */
    void send(int commandId, Object message);
    
    /**
     * 获取房间玩家助手
     * @return
     */
    IRoomPlayerHelper getRoomPlayerHelper();
}