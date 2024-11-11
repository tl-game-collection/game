package com.xiuxiu.app.server.club;

import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.club.factory.ClubFactory;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.NumberUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 亲友圈信息管理器
 * 
 * @author Administrator
 *
 */
public class ClubManager extends BaseManager {
    private static class ClubManagerHolder {
        private static ClubManager instance = new ClubManager();
    }

    public static ClubManager I = ClubManagerHolder.instance;
    
    /**
     * 所有亲友圈
     */
    private Map<Long, IClub> clubs = new ConcurrentHashMap<Long, IClub>();

    /**
     * 存放可用的clubUid
     */
    private List<ClubUid> clubUidMakers = new ArrayList<>();

    /**
     * 启服时初始化
     */
    public void init() {
        loadAll();
    }

    /**
     * 启服时加载所有亲友圈信息
     */
    private void loadAll() {
        //  加载所有数据
        List<ClubInfo> tempList = DBManager.I.getClubInfoDAO().loadAll();
        if (tempList != null && tempList.size() > 0) {
            Iterator<ClubInfo> it = tempList.iterator();
            while (it.hasNext()) {
                ClubInfo clubInfo = it.next();
                EClubType clubType = EClubType.getType(clubInfo.getClubType());
                if (null == clubType) {
                    continue;
                }
                IClub club = ClubFactory.createClub(clubType);
                if (null == club) {
                    continue;
                }
                club.init(clubInfo);
                this.clubs.put(clubInfo.getUid(), club);
            }
        }
        loadClubUidMaker();
    }
    
    /**
     * 判断是否存在
     * @param uid
     * @return
     */
    public boolean isExist(long uid) {
        return this.clubs.containsKey(uid);
    }

    /**
     * 获取亲友圈
     * @param uid
     * @return
     */
    public IClub getClubByUid(long uid) {
        return this.clubs.get(uid);
    }
    
    /**
     * 判断亲友圈名称是否已存在
     * @param clubType
     * @param name
     * @return
     */
    public boolean isExistName(EClubType clubType, String name){
        for (IClub group : this.clubs.values()) {
            if (null == group) {
                continue;
            }
            if (group.getClubType().match(clubType) && group.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取亲友群总数
     * 
     * @return
     */
    public int size() {
        return this.clubs.size();
    }

    @Override
    public int save() {
        int count = 0;
        try {
            Iterator<Map.Entry<Long, IClub>> it = this.clubs.entrySet().iterator();
            while (it.hasNext()) {
                IClub club = it.next().getValue();
                if (club.save()) {
                    ++count;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return count;
    }
    
    /**
     * 0点刷新逻辑
     * @param now
     */
    public void zero(long now) {
        Iterator<Map.Entry<Long, IClub>> it = this.clubs.entrySet().iterator();
        while (it.hasNext()) {
            IClub club = it.next().getValue();
            club.zero(now);
        }
    }
    
    /**
     * 创建亲友圈
     * @param player
     * @param type
     * @param name
     * @param desc
     * @param icon
     * @param gameDesc
     * @return
     */
    public IClub create(Player player, Integer type, String name, String desc, String icon, String gameDesc,long clubUid) {
        EClubType clubType = EClubType.getType(type);
        if (null == clubType) {
            return null;
        }
        IClub club = ClubFactory.createClub(clubType);
        if (null == club) {
            return null;
        }
        clubUid = this.makeClubUid(clubUid);
        if (0 >= clubUid){
            return null;
        }
        ClubInfo clubInfo = new ClubInfo();
        clubInfo.setUid(clubUid);
        clubInfo.setName(name);
        clubInfo.setDesc(desc);
        clubInfo.setGameDesc(gameDesc);
        clubInfo.setIcon(icon);
        clubInfo.setOwnerId(player.getUid());
        clubInfo.setClubType(type);
        clubInfo.setState(0);
        clubInfo.setCreateTime(System.currentTimeMillis());
        clubInfo.setDirty(Boolean.TRUE);

        if (null != this.clubs.putIfAbsent(clubInfo.getUid(), club)){
            return null;
        }

        club.init(clubInfo);
        club.addMember(-1, player, EClubJobType.CHIEF);
        return club;
    }

    public void delClub(long uid) {
        this.clubs.remove(uid);
    }

    
    /**
     * 遍历执行一些事
     * @param cb
     */
    public void foreach(ICallback<IClub> cb) {
        Iterator<Map.Entry<Long, IClub>> it = this.clubs.entrySet().iterator();
        while (it.hasNext()) {
            cb.call(it.next().getValue());
        }
    }
    
    public Set<Long> getAllClubIds(){
        return this.clubs.keySet();
    }

    @Override
    public int shutdown() {
        return 0;
    }

    /**
     * 返还一个可用的clubUid
     * @param defaultClubUid 如果不需要自动生成，defaultClubUid > 0
     * @return 0 无效的clubUid
     */
    private long makeClubUid(long defaultClubUid){
        if (defaultClubUid > 0){
            ClubUid clubUidMaker = new ClubUid();
            clubUidMaker.setUid(defaultClubUid);
            clubUidMaker.setGood(NumberUtils.isGoodNumber(defaultClubUid) ? 1 : 0);
            clubUidMaker.setState(0);
            clubUidMaker.setDirty(true);
            clubUidMaker.save();
            return defaultClubUid;
        }
        synchronized (this.clubUidMakers) {
            int size = clubUidMakers.size();
            if (size <= 0) {
                loadClubUidMaker();
            }
            size = clubUidMakers.size();
            if (size <= 0) {
                return 0;
            }
            ClubUid clubUidMaker = this.clubUidMakers.remove(size - 1);
            clubUidMaker.setState(0);
            clubUidMaker.setDirty(true);
            clubUidMaker.save();
            return clubUidMaker.getUid();
        }
    }

    private void loadClubUidMaker(){
        clubUidMakers = DBManager.I.getClubUidDao().loadUnused(false,10000);
        if (null == clubUidMakers){
            clubUidMakers = new ArrayList<>();
        }
    }
    
    public IClub getMainClub(IBoxOwner boxOwner) {
        IClub mainClub = (IClub) boxOwner;
        if (mainClub.checkIsMainClub()) {
            return mainClub;
        } else {
            long finalClubId = mainClub.getFinalClubId();
            if (finalClubId == 0) {
                return mainClub;
            }
            return ClubManager.I.getClubByUid(finalClubId);
        }
    }


}
