package com.xiuxiu.app.server.floor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorNtfDel;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorNtfInfo;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorNtfList;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.StringUtil;

/**
 * 楼层管理器
 * 
 * @author Administrator
 *
 */
public class FloorManager extends BaseManager {
    private static class FloorManagerHolder {
        private static FloorManager instance = new FloorManager();
    }

    public static FloorManager I = FloorManagerHolder.instance;
    /**
     * 楼层容器，格式：map<楼层id,楼层对象>
     */
    private ConcurrentHashMap<Long, Floor> floors = new ConcurrentHashMap<>();

    public void loadAll() {
        List<Floor> all = DBManager.I.getFloorDAO().loadAll();
        if (all.size() > 0) {
            for (Floor floor : all) {
                IClub club = ClubManager.I.getClubByUid(floor.getClubUid());
                if (null == club) {
                    continue;
                }
                
                // 入到缓存容器
                this.floors.putIfAbsent(floor.getUid(), floor);
                // 关联亲友圈
                club.addFloor(floor);
            }
        }
    }
    
    public Floor getFloor(long uid) {
        return floors.get(uid);
    }

    /**
     * 创建楼层
     * 
     * @param player
     * @param club
     * @param ownerType
     * @param floorType
     * @param name
     * @param layoutType
     * @return
     */
    public ErrorCode create(Player player, IClub club, int ownerType, int floorType, String name, int layoutType) {
        Floor floor = new Floor();
        floor.setUid(UIDManager.I.getAndInc(UIDType.FLOOR));
        floor.setClubUid(club.getClubUid());
        floor.setFloorType(floorType);
        floor.setName(name);
        floor.setOwnerType(ownerType);
        floor.setLayoutType(layoutType);
        // 入到缓存容器
        addFloor(floor.getUid(), floor);
        // 关联亲友圈
        club.addFloor(floor);
        // 通知创建楼层
        noticeCreateFloor(player, club, floor);
        return ErrorCode.OK;
    }

    /**
     * 通知创建楼层
     * 
     * @param player
     * @param club
     * @param floor
     */
    private void noticeCreateFloor(Player player, IClub club, Floor floor) {
        PCLIFloorNtfInfo addFloorInfo = new PCLIFloorNtfInfo();
        addFloorInfo.uid = floor.getUid();
        addFloorInfo.clubUid = club.getClubUid();
        addFloorInfo.floorType = floor.getFloorType();
        addFloorInfo.name = floor.getName();
        addFloorInfo.ownerType = floor.getOwnerType();
        addFloorInfo.layoutType = floor.getLayoutType();
        player.send(CommandId.CLI_NTF_CLUB_FLOOR_CREATE_OK, addFloorInfo);
        club.broadcastToAllClub(CommandId.CLI_NTF_CLUB_FLOOR_CREATE_OK,addFloorInfo);
    }

    private void addFloor(long uid, Floor floor) {
        this.floors.putIfAbsent(uid, floor);
        floor.setDirty(Boolean.TRUE);
        floor.save();
    }

    /**
     * 关闭楼层
     * 
     * @param player
     * @param club
     * @param floor
     * @return
     */
    public ErrorCode close(Player player, IClub club, Floor floor) {
        // 亲友圈关联相关关系删除
        club.closeFloor(floor.getUid());
        // 从缓存容器中删除
        remove(floor.getUid());

        // 通知删除楼层
        noticeCloseFloor(player, club, floor);
        return ErrorCode.OK;
    }

    /**
     * 通知删除楼层
     * 
     * @param player
     * @param club
     * @param floor
     */
    private void noticeCloseFloor(Player player, IClub club, Floor floor) {
        PCLIFloorNtfDel delInfo = new PCLIFloorNtfDel();
        delInfo.uid = floor.getUid();
        delInfo.clubUid = club.getClubUid();

        player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_OK, delInfo);
        club.broadcastToAllClub(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_OK,delInfo);
    }

    private void remove(long uid) {
        Floor box = this.floors.remove(uid);
        if (null != box) {
            DBManager.I.save(() -> {
                DBManager.I.getFloorDAO().deleteFloorByUid(uid);
            });
        }
    }

    /**
     * 获取楼层列表信息
     * 
     * @param club
     * @return
     */
    public PCLIFloorNtfList getFloorList(IClub club) {
        return club.getFloorList();
    }

    @Override
    public int save() {
        int cnt = 0;
        try {
            Iterator<Map.Entry<Long, Floor>> it = this.floors.entrySet().iterator();
            while (it.hasNext()) {
                Floor floor = it.next().getValue();
                if (floor.save()) {
                    ++cnt;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return cnt;
    }

    @Override
    public int shutdown() {
        return 0;
    }

}
