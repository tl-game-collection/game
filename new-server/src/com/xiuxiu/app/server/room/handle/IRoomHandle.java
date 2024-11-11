package com.xiuxiu.app.server.room.handle;

import java.util.List;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public interface IRoomHandle {

    /**
     * 初始化
     */
    void init();
    
    /**
     * 获取房间
     * @return
     */
    IRoom getRoom();
    
    /**
     * 加入房间
     *
     * @param player
     * @return
     */
    ErrorCode join(Player player);
    
    /**
     * 离开房间
     *
     * @param player
     * @return
     */
    ErrorCode leave(Player player);
    
    /**
     * 销毁后处理
     */
    void destoryAfter();
    
    void doFinishAfter(boolean isNormal, boolean isNewBureau);
    
    void killAll(List<Long> killPlayerUids);
    
    /**
     * 开始之前处理
     */
    void startBefore();
    
    /**
     * 开始处理
     */
    void start();
    
    /**
     * 大局抽水,包厢竞技值计算处理(大局结算)
     */
    void destoryGoldHandle();
    
    /**
     * 竞技值计算处理
     */
    void calculateGold();
    
    /**
     * 再来一局
     */
    void again();
    
    /**
     * 检查是否再来一局
     * @param killPlayer
     * @return
     */
    boolean checkAgain(boolean killPlayer);
    
    ErrorCode readyHandle(long playerUid, boolean checkContains);
    
    void onSitup();
    
    void startCheckLeave();
    
    void doDestroy();
    
    long getFromClubUid(long playerUid);
    
    void saveRoomScore();
    
    /**
     * 判断能否发起解散
     * @param player
     * @return
     */
    ErrorCode canDissolve(Player player);
    
    IRoomPlayer createPlayer();
    
    /**
     * 记录战绩
     */
    void record();
    
    /**
     * 房间线程刷帧处理
     * @param curTime
     * @param delay
     */
    void tickHandle(long curTime, long delay);
    
    /**
     * 离线
     * @param player
     * @return
     */
    ErrorCode offline(Player player);
    
}
