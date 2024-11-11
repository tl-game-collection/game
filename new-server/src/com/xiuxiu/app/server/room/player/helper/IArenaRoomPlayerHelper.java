package com.xiuxiu.app.server.room.player.helper;

import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;

public interface IArenaRoomPlayerHelper extends IRoomPlayerHelper {

    /**
     * 获取玩家金币(竞技值)
     * 
     * @return
     */
    long getGold();

    /**
     * 是否有value竞技值
     * 
     * @param value
     * @return
     */
    boolean checkEnoughGold(long value);

    /**
     * 修改竞技值
     * 
     * @param playerUid
     * @param value
     * @param optPlayerUid
     * @param changeType
     * @return
     */
    boolean addGold(long playerUid, int value, long optPlayerUid, EClubGoldChangeType changeType);

    /**
     * 获取房间包厢拥有者
     * 
     * @return
     */
    IBoxOwner getBoxOwner();

    /**
     * 获取参与游戏选择的群
     * 
     * @return
     */
    IClub getFromClub();

    /**
     * 记录
     * 
     * @param score
     * @param recordUid
     * @param now
     */
    void record(int score, long recordUid, long now);
}
