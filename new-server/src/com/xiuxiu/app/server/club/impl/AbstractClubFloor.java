package com.xiuxiu.app.server.club.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xiuxiu.app.protocol.client.floor.PCLIFloorNtfInfo;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorNtfList;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.app.server.floor.IFloorOwner;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.PlayerManager;

public abstract class AbstractClubFloor extends AbstractClub implements IFloorOwner {

    /**
     * 楼层信息
     */
    protected ConcurrentHashMap<Long, Floor> floors = new ConcurrentHashMap<>();
    
    @Override
    public int getFloorSize() {
        return this.floors.size();
    }

    @Override
    public void addFloor(Floor floor) {
        this.floors.putIfAbsent(floor.getUid(), floor);
    }

    @Override
    public void closeFloor(long floorUid) {
        Floor floor = this.floors.get(floorUid);
        if (null == floor) {
            return;
        }
        Long[] boxUidWithFloor = floor.getAllGameUid();
        for (Long boxUid : boxUidWithFloor) {
            Box box = getBox(boxUid);
            if (null != box) {
                box.close();
            }
        }
        this.floors.remove(floorUid);
    }

    @Override
    public ConcurrentHashMap<Long, Floor> getFloor() {
        return floors;
    }

    @Override
    public Floor getFloor(long floorUid) {
        return this.floors.get(floorUid);
    }

    @Override
    public IPlayer getOwnerPlayer() {
        return PlayerManager.I.getPlayer(this.clubInfo.getOwnerId());
    }

    @Override
    public PCLIFloorNtfList getFloorList() {
        ConcurrentHashMap<Long, Floor> allFloor = this.floors;
        Iterator<Map.Entry<Long, Floor>> it = allFloor.entrySet().iterator();
        PCLIFloorNtfList list = new PCLIFloorNtfList();
        list.clubUid = this.getClubUid();
        while (it.hasNext()) {
            Floor floor = it.next().getValue();
            PCLIFloorNtfInfo floorInfo = new PCLIFloorNtfInfo();
            floorInfo.uid = floor.getUid();
            floorInfo.clubUid = floor.getClubUid();
            floorInfo.floorType = floor.getFloorType();
            floorInfo.ownerType = floor.getOwnerType();
            floorInfo.name = floor.getName();
            floorInfo.layoutType = floor.getLayoutType();
            List<Long> uidWithFloor = floor.getShowGameUid();
            IClub club = ClubManager.I.getClubByUid(this.getClubUid());
            if (null != club){
                floorInfo.allPlayerCnt = floor.getAllPlayerCnt(club);
            }
            if (1 == floor.getFloorType()) {
                // 包厢
                for (Long uid : uidWithFloor) {
                    Box box = getBox(uid);
                    if (null != box) {
                        floorInfo.boxList.add(box.getBoxInfo());
                    }
                }
            }
            list.list.add(floorInfo);
        }
        return list;
    }
}
