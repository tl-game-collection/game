package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.player.Player;

import java.util.List;

public interface PlayerDAO extends IBaseDAO<Player> {
    List<Player> loadByUids(List<Long> playerUids);
    List<Long> loadAllUid();

    /**
     * 查询所有玩家
     * @param page  当前页数
     * @param pageSize  每页几条
     * @return  Player
     */
    List<Player> loadAllPlayer(int page, int pageSize);

    /**
     * 把所有在线机器人上次登出时间复位为当前时间
     * @param time
     */
    void resetAllRobotLoginOutTime(long time);
    
    /**
     * 查询所有机器人
     * @param page  当前页数
     * @param pageSize  每页几条
     * @return  Player
     */
    List<Player> loadAllRobot(int page, int pageSize);
    
}
