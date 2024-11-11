package com.xiuxiu.app.server.floor;

import com.xiuxiu.app.protocol.client.floor.PCLIFloorNtfList;
import com.xiuxiu.app.server.player.IPlayer;

import java.util.concurrent.ConcurrentHashMap;

public interface IFloorOwner {
    
    /**
     * 获取楼层数量
     * @return
     */
    int getFloorSize();
    void addFloor(Floor floor);
    void closeFloor(long floorUid);
    ConcurrentHashMap<Long, Floor> getFloor();
    Floor getFloor(long floorUid);
    IPlayer getOwnerPlayer();
    
    /**
     * 获取楼层信息列表
     * @return
     */
    PCLIFloorNtfList getFloorList();
}
