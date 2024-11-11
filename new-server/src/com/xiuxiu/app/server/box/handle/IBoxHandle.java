package com.xiuxiu.app.server.box.handle;

import java.util.Map;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

public interface IBoxHandle {

    /**
     * 坐下
     * 
     * @param roomPlayer
     * @param sitIndex
     * @return
     */
    public ErrorCode sitDown(IRoomPlayer roomPlayer, int sitIndex);

    /**
     * 站起
     * 
     * @param roomPlayer
     * @return
     */
    public ErrorCode sitUp(IRoomPlayer roomPlayer);

    /**
     * 准备
     * 
     * @param player
     * @param roomPlayer
     * @return
     */
    public ErrorCode ready(Player player, IRoomPlayer roomPlayer);

    /**
     * 加入
     * @param roomPlayer
     * @return
     */
    public ErrorCode onJoin(IRoomPlayer roomPlayer);
    
    public void level(long playerUid);
    
    public void killAll(Box box);

    public void resetSitUpTime();
    
    public void tick();
    
    /**
     * 获取坐下的玩家
     * @return
     */
    public Map<Long, Long> allSitDownPlayer();

    public IRoom getRoom();
    
    /**
     * 获取观战名单
     * 
     * @param roomPlayer
     * @return
     */
    public ErrorCode getAllWatchPlayer(IRoomPlayer roomPlayer);
    
}
