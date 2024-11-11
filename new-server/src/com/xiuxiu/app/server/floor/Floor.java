package com.xiuxiu.app.server.floor;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.xiuxiu.app.protocol.client.PCLIPlayerBriefInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxRoomStateInfo;
import com.xiuxiu.app.protocol.client.room.PCLIBoxRoomInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomPlayerInfo;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.constant.EBoxShowType;
import com.xiuxiu.app.server.box.constant.EBoxState;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Floor extends BaseTable {
    private long clubUid;
    private int floorType;
    private String name;
    private Set<Long> gameUid = new HashSet<>();
    private transient List<Long> showGameUid = new ArrayList<>();// (为了不影响之前的专供显示用)显示的竞技场游戏ID
    private int layoutType;// 客户端布局
    private int ownerType;// 拥有类型类型

    /**
     * 显示玩法桌的列表
     */
    private transient List<PCLIBoxRoomStateInfo> showPlayBoxList;
    /**
     * 不显示玩法桌的列表
     */
    private transient List<PCLIBoxRoomStateInfo> unShowPlayBoxList;
    /**
     * 机器人游戏桌列表
     */
    private transient List<PCLIBoxRoomStateInfo> robotBoxList = new ArrayList<>();
    private transient int curRobotDesk2;// 当前已有2人场机器人桌子数量
    private transient int curRobotDesk3;// 当前已有3人场机器人桌子数量
    private transient int curRobotDesk4;// 当前已有4人场机器人桌子数量
    
    private transient int setRobotDesk2Min;// 要设置的当前已有2人场机器人桌子最小数量
    private transient int setRobotDesk2Max;// 要设置的当前已有2人场机器人桌子最大数量
    private transient int setRobotDesk2;// 随机后的桌子数
    private transient int randomTime2;// 2人场随机时间
    private transient long lastRandomTime2;// 2人场上次随机时间
    private transient int setRobotDesk3Min;// 要设置的当前已有3人场机器人桌子最小数量
    private transient int setRobotDesk3Max;// 要设置的当前已有3人场机器人桌子最大数量
    private transient int setRobotDesk3;// 随机后的桌子数
    private transient int randomTime3;// 3人场随机时间
    private transient long lastRandomTime3;// 3人场上次随机时间
    
    
    private transient int setRobotDesk4Min;// 要设置的当前已有4人场机器人桌子最小数量
    private transient int setRobotDesk4Max;// 要设置的当前已有4人场机器人桌子最大数量
    private transient int setRobotDesk4;// 随机后的桌子数
    private transient int randomTime4;// 4人场随机时间
    private transient long lastRandomTime4;// 4人场上次随机时间
    
    

    /** 刷新锁 */
    private transient ReentrantLock refreshLock = new ReentrantLock();
    /** 最后次刷新时间 */
    private long lastRefreshTime;

    public Floor() {
        this.setTableType(ETableType.TB_FLOOR);
    }

    /**
     * 刷新机器人桌子
     * 
     * @param club
     */
    private void refreshRobotBoxList(IClub club) {
        List<Long> gameId = getShowGameUid();
        List<Box> tempPlayList2 = new ArrayList<>();
        List<Box> tempPlayList3 = new ArrayList<>();
        List<Box> tempPlayList4 = new ArrayList<>();
        // 固定模式玩法桌(按照人数分类)
        for (long id : gameId) {
            Box box = club.getBox(id);
            if (null == box) {
                continue;
            }
            if (EBoxType.NORMAL.match(box.getBoxType())) {
                if (box.getRule().get("playerMinNum") != null) {
                    continue;
                }
                int playerNum = box.getRule().get("playerNum");
                if (playerNum == 2) {
                    tempPlayList2.add(box);
                }
                if (playerNum == 3) {
                    tempPlayList3.add(box);
                }
                
                if (playerNum == 4) {
                    tempPlayList4.add(box);
                }
            }
        }

        // 当前时间 - 上次刷新时间 > 固定刷新时间，就刷新局数
        for (PCLIBoxRoomStateInfo tempInfo : robotBoxList) {
            if (System.currentTimeMillis() - tempInfo.lastRefreshTime >= tempInfo.refreshTime * 1000) {
                tempInfo.lastRefreshTime = System.currentTimeMillis();
                tempInfo.roomInfo.curBureau++;
                tempInfo.refreshTime = RandomUtil.random(20, 35);
            }
        }
        // 局数超过就删除
        Iterator<PCLIBoxRoomStateInfo> it = robotBoxList.iterator();
        while (it.hasNext()) {
            PCLIBoxRoomStateInfo tempInfo = it.next();
            if (tempInfo.roomInfo.curBureau > 8) {
                // 把这个机器人从已使用中移除
                for (int i = 0; i < tempInfo.roomInfo.players.size(); i++) {
                    long tempPlayerUid = tempInfo.roomInfo.players.get(i).playerInfo.uid;
                    club.delUsedRobotUid(tempPlayerUid);
                    // 设置为离线
                    Player tempPlayer = PlayerManager.I.getPlayer(tempPlayerUid);
                    if (tempPlayer != null) {
                        tempPlayer.setLastLogoutTime(System.currentTimeMillis());
                        tempPlayer.setDirty(true);
                        tempPlayer.save();
                    }
                }
                // 当前机器人桌子减少
                if (tempInfo.signType == 2) {
                    curRobotDesk2--;
                } else if(tempInfo.signType == 3) {
                    curRobotDesk3--;
                }else {
                    curRobotDesk4--;
                }
                it.remove();
            }
        }

        // 当前时间 - 上次随机时间 > 固定随机时间，就重新随机桌子数量
        if (System.currentTimeMillis() - this.lastRandomTime2 >= this.randomTime2 * 60 * 1000 && this.randomTime2 > 0) {
            this.lastRandomTime2 = System.currentTimeMillis();
            this.setRobotDesk2 = RandomUtil.random(this.setRobotDesk2Min, this.setRobotDesk2Max);
        }
        if (System.currentTimeMillis() - this.lastRandomTime3 >= this.randomTime3 * 60 * 1000 && this.randomTime3 > 0) {
            this.lastRandomTime3 = System.currentTimeMillis();
            this.setRobotDesk3 = RandomUtil.random(this.setRobotDesk3Min, this.setRobotDesk3Max);
        }
        
        if (System.currentTimeMillis() - this.lastRandomTime4 >= this.randomTime4 * 60 * 1000 && this.randomTime4 > 0) {
            this.lastRandomTime4 = System.currentTimeMillis();
            this.setRobotDesk4 = RandomUtil.random(this.setRobotDesk4Min, this.setRobotDesk4Max);
        }

        // 机器人的假桌子2人场
        if (curRobotDesk2 < setRobotDesk2) {
            int count = setRobotDesk2 - curRobotDesk2;
            if (tempPlayList2.size() != 0) {
                for (int i = 0; i < count; i++) {
                    // 随机一个玩法创建桌子出来
                    int random = RandomUtil.random(0, tempPlayList2.size() - 1);
                    PCLIBoxRoomStateInfo tempBox = new PCLIBoxRoomStateInfo();
                    tempBox.boxUid = -1;
                    tempBox.isStart = true;
                    tempBox.type = 3;
                    tempBox.roomIndex = -2;
                    tempBox.signType = 2;
                    tempBox.refreshTime = RandomUtil.random(20, 35);
                    tempBox.lastRefreshTime = System.currentTimeMillis();
                    tempBox.roomInfo = new PCLIBoxRoomInfo();
                    tempBox.roomInfo.roomId = -1;
                    tempBox.roomInfo.curBureau = 1;
                    tempBox.roomInfo.rule = new HashMap<>();
                    tempBox.roomInfo.rule.putAll(tempPlayList2.get(random).getRule());
                    tempBox.roomInfo.gameType = tempPlayList2.get(random).getGameType();
                    tempBox.roomInfo.gameSubType = tempPlayList2.get(random).getGameSubType();
                    tempBox.gameType =tempBox.roomInfo.gameType;
                    tempBox.gameSubType = tempBox.roomInfo.gameSubType;
                    tempBox.endPoint = Box.getRoomEndPoint(tempBox.gameType,tempBox.gameSubType,tempBox.roomInfo.rule);
                    tempBox.endPointMul =  tempBox.roomInfo.rule.getOrDefault(RoomRule.RR_END_POINT,0);//注释
                    tempBox.playType = Box.getRoomPlayTypeForClient(tempBox.gameType,tempBox.gameSubType,tempBox.roomInfo.rule);
                    for (int j = 0; j < tempBox.signType; j++) {
                        long tempPlayerUid = club.getCanUseRobotUid();
                        if (tempPlayerUid == -1) {
                            break;
                        }
                        Player tempPlayer = PlayerManager.I.getPlayer(tempPlayerUid);
                        if (tempPlayer == null) {
                            continue;
                        }
                        tempPlayer.setLastLogoutTime(-1);// 设置为在线
                        tempPlayer.setDirty(true);
                        tempPlayer.save();
                        PCLIRoomPlayerInfo playerInfo = new PCLIRoomPlayerInfo();
                        playerInfo.playerInfo = new PCLIPlayerBriefInfo();
                        playerInfo.playerInfo.uid = tempPlayerUid;
                        playerInfo.playerInfo.name = tempPlayer.getName();
                        playerInfo.playerInfo.sex = tempPlayer.getSex();
                        playerInfo.playerInfo.alias = tempPlayer.getAlias();
                        playerInfo.playerInfo.icon = tempPlayer.getIcon();
                        // playerInfo.playerInfo.tags
                        playerInfo.playerInfo.zone = tempPlayer.getZone();
                        playerInfo.playerInfo.lastLogoutTime = tempPlayer.getLastLogoutTime();

                        tempBox.roomInfo.players.add(playerInfo);
                        club.addUsedRobotUid(tempPlayerUid);
                    }
                    tempBox.roomInfo.extra = new HashMap<>();
                    tempBox.roomInfo.extra.putAll(tempPlayList2.get(random).getExtra());
                    // tempBox.roomInfo.remarks = tempPlayList2.get(random).roomInfo.remarks;
                    
                    robotBoxList.add(tempBox);
                    curRobotDesk2++;
                }
            }
        }
        // 机器人的假桌子3人场
        if (curRobotDesk3 < setRobotDesk3) {
            int count = setRobotDesk3 - curRobotDesk3;
            if (tempPlayList3.size() != 0) {
                for (int i = 0; i < count; i++) {
                    // 随机一个玩法创建桌子出来
                    int random = RandomUtil.random(0, tempPlayList3.size() - 1);
                    PCLIBoxRoomStateInfo tempBox = new PCLIBoxRoomStateInfo();
                    tempBox.boxUid = -1;
                    tempBox.isStart = true;
                    tempBox.type = 3;
                    tempBox.roomIndex = -2;
                    tempBox.signType = 3;
                    tempBox.refreshTime = RandomUtil.random(20, 35);
                    tempBox.lastRefreshTime = System.currentTimeMillis();
                    tempBox.roomInfo = new PCLIBoxRoomInfo();
                    tempBox.roomInfo.roomId = -1;
                    tempBox.roomInfo.curBureau = 1;
                    tempBox.roomInfo.rule = new HashMap<>();
                    tempBox.roomInfo.rule.putAll(tempPlayList3.get(random).getRule());
                    tempBox.roomInfo.gameType = tempPlayList3.get(random).getGameType();
                    tempBox.roomInfo.gameSubType = tempPlayList3.get(random).getGameSubType();
                    tempBox.gameType = tempBox.roomInfo.gameType;
                    tempBox.gameSubType = tempBox.roomInfo.gameSubType;
                    tempBox.endPoint = Box.getRoomEndPoint(tempBox.gameType,tempBox.gameSubType,tempBox.roomInfo.rule);
                    tempBox.endPointMul =  tempBox.roomInfo.rule.getOrDefault(RoomRule.RR_END_POINT,0);//注释
                    tempBox.playType = Box.getRoomPlayTypeForClient(tempBox.gameType,tempBox.gameSubType,tempBox.roomInfo.rule);
                    for (int j = 0; j < tempBox.signType; j++) {
                        long tempPlayerUid = club.getCanUseRobotUid();
                        if (tempPlayerUid == -1) {
                            break;
                        }
                        Player tempPlayer = PlayerManager.I.getPlayer(tempPlayerUid);
                        if (tempPlayer == null) {
                            continue;
                        }
                        tempPlayer.setLastLogoutTime(-1);// 设置为在线
                        tempPlayer.setDirty(true);
                        tempPlayer.save();
                        PCLIRoomPlayerInfo playerInfo = new PCLIRoomPlayerInfo();
                        playerInfo.playerInfo = new PCLIPlayerBriefInfo();
                        playerInfo.playerInfo.uid = tempPlayerUid;
                        playerInfo.playerInfo.name = tempPlayer.getName();
                        playerInfo.playerInfo.sex = tempPlayer.getSex();
                        playerInfo.playerInfo.alias = tempPlayer.getAlias();
                        playerInfo.playerInfo.icon = tempPlayer.getIcon();
                        // playerInfo.playerInfo.tags
                        playerInfo.playerInfo.zone = tempPlayer.getZone();
                        playerInfo.playerInfo.lastLogoutTime = tempPlayer.getLastLogoutTime();

                        tempBox.roomInfo.players.add(playerInfo);
                        club.addUsedRobotUid(tempPlayerUid);
                    }
                    tempBox.roomInfo.extra = new HashMap<>();
                    tempBox.roomInfo.extra.putAll(tempPlayList3.get(random).getExtra());
                    // tempBox.roomInfo.remarks = tempPlayList3.get(random).roomInfo.remarks;

                    robotBoxList.add(tempBox);
                    curRobotDesk3++;
                }
            }
            
            
            
        }
        
        // 机器人的假桌子4人场
        if (curRobotDesk4 < setRobotDesk4) {
            int count = setRobotDesk4 - curRobotDesk4;
            if (tempPlayList4.size() != 0) {
                for (int i = 0; i < count; i++) {
                    // 随机一个玩法创建桌子出来
                    int random = RandomUtil.random(0, tempPlayList4.size() - 1);
                    PCLIBoxRoomStateInfo tempBox = new PCLIBoxRoomStateInfo();
                    tempBox.boxUid = -1;
                    tempBox.isStart = true;
                    tempBox.type = 3;
                    tempBox.roomIndex = -2;
                    tempBox.signType = 4;
                    tempBox.refreshTime = RandomUtil.random(20, 35);
                    tempBox.lastRefreshTime = System.currentTimeMillis();
                    tempBox.roomInfo = new PCLIBoxRoomInfo();
                    tempBox.roomInfo.roomId = -1;
                    tempBox.roomInfo.curBureau = 1;
                    tempBox.roomInfo.rule = new HashMap<>();
                    tempBox.roomInfo.rule.putAll(tempPlayList4.get(random).getRule());
                    tempBox.roomInfo.gameType = tempPlayList4.get(random).getGameType();
                    tempBox.roomInfo.gameSubType = tempPlayList4.get(random).getGameSubType();
                    tempBox.gameType = tempBox.roomInfo.gameType;
                    tempBox.gameSubType = tempBox.roomInfo.gameSubType;
                    tempBox.endPoint = Box.getRoomEndPoint(tempBox.gameType,tempBox.gameSubType,tempBox.roomInfo.rule);
                    tempBox.endPointMul =  tempBox.roomInfo.rule.getOrDefault(RoomRule.RR_END_POINT,0);//注释
                    tempBox.playType = Box.getRoomPlayTypeForClient(tempBox.gameType,tempBox.gameSubType,tempBox.roomInfo.rule);
                    for (int j = 0; j < tempBox.signType; j++) {
                        long tempPlayerUid = club.getCanUseRobotUid();
                        if (tempPlayerUid == -1) {
                            break;
                        }
                        Player tempPlayer = PlayerManager.I.getPlayer(tempPlayerUid);
                        if (tempPlayer == null) {
                            continue;
                        }
                        tempPlayer.setLastLogoutTime(-1);// 设置为在线
                        tempPlayer.setDirty(true);
                        tempPlayer.save();
                        PCLIRoomPlayerInfo playerInfo = new PCLIRoomPlayerInfo();
                        playerInfo.playerInfo = new PCLIPlayerBriefInfo();
                        playerInfo.playerInfo.uid = tempPlayerUid;
                        playerInfo.playerInfo.name = tempPlayer.getName();
                        playerInfo.playerInfo.sex = tempPlayer.getSex();
                        playerInfo.playerInfo.alias = tempPlayer.getAlias();
                        playerInfo.playerInfo.icon = tempPlayer.getIcon();
                        // playerInfo.playerInfo.tags
                        playerInfo.playerInfo.zone = tempPlayer.getZone();
                        playerInfo.playerInfo.lastLogoutTime = tempPlayer.getLastLogoutTime();

                        tempBox.roomInfo.players.add(playerInfo);
                        club.addUsedRobotUid(tempPlayerUid);
                    }
                    tempBox.roomInfo.extra = new HashMap<>();
                    tempBox.roomInfo.extra.putAll(tempPlayList4.get(random).getExtra());
                    // tempBox.roomInfo.remarks = tempPlayList3.get(random).roomInfo.remarks;

                    robotBoxList.add(tempBox);
                    curRobotDesk4++;
                }
            }
            
            
            
        }
    }

    private void refreshShowPlayList(IClub club) {
        List<PCLIBoxRoomStateInfo> tempList = new ArrayList<>();
        List<Long> gameId = getShowGameUid();
        if (gameId.size() > 0) {
            // 是否显示玩法桌
            // 1、自定义玩法桌；
            for (long id : gameId) {
                Box box = club.getBox(id);
                if (null == box) {
                    continue;
                }
                if (EBoxType.CUSTOM.match(box.getBoxType())) {
                    box.getPlayRoomStateInfo(tempList, 1);
                }
            }
            // 2、固定模式玩法桌；
            for (long id : gameId) {
                Box box = club.getBox(id);
                if (null == box) {
                    continue;
                }
                if (!EBoxType.CUSTOM.match(box.getBoxType())) {
                    box.getPlayRoomStateInfo(tempList, 2);
                }
            }
            // 3、游戏桌显示规则：优先显示未开始的游戏桌，再显示已开始的游戏桌；
            for (long id : gameId) {
                Box box = club.getBox(id);
                if (null == box) {
                    continue;
                }
                if (box != null && EBoxState.INIT == box.getState()) {
                    box.getRoomState(tempList, 3);
                }
            }
            
        }
        showPlayBoxList = tempList;
    }
    
    private void refreshUnShowPlayList(IClub club) {
        List<PCLIBoxRoomStateInfo> tempList = new ArrayList<>();
        List<Long> gameId = getShowGameUid();
        if (gameId.size() > 0) {
            // 游戏桌显示规则：优先显示未开始的游戏桌，再显示已开始的游戏桌；
            for (long id : gameId) {
                Box box = club.getBox(id);
                if (null == box) {
                    continue;
                }
                if (box != null && EBoxState.INIT == box.getState()) {
                    box.getRoomState(tempList, 4);
                }
            }
        }
        int size = tempList.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                PCLIBoxRoomStateInfo temp1 = tempList.get(i);
                int sortValue1 = temp1.isStart ? 1 : 0;
                PCLIBoxRoomStateInfo temp2 = tempList.get(j);
                int sortValue2 = temp2.isStart ? 1 : 0;
                if (sortValue1 < sortValue2) {
                    tempList.set(i, temp2);
                    tempList.set(j, temp1);
                }
            }
        }
        unShowPlayBoxList = tempList;
    }

    public void refreshShowBoxList(IClub club, boolean force) {
        try {
            long now = System.currentTimeMillis();
            if (force) {
                if (refreshLock.tryLock() || refreshLock.tryLock(30, TimeUnit.MILLISECONDS)) {
                    refreshShowBoxListHandle(club, now);
                }
            } else {
                if (now > lastRefreshTime
                        && (refreshLock.tryLock() || refreshLock.tryLock(10, TimeUnit.MILLISECONDS))) {
                    refreshShowBoxListHandle(club, now);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (refreshLock.isHeldByCurrentThread()) {
                refreshLock.unlock();
            }
        }
    }

    private void refreshShowBoxListHandle(IClub club, long now) {
        lastRefreshTime = now + 5000L;
        refreshShowPlayList(club);
        refreshUnShowPlayList(club);
        refreshRobotBoxList(club);
    }

    public List<PCLIBoxRoomStateInfo> getAndSetShowBoxList(IClub club, boolean flag, int type, int gameType) {
        refreshShowBoxList(club, Boolean.FALSE);
        List<PCLIBoxRoomStateInfo> tempList = new ArrayList<>();
        List<PCLIBoxRoomStateInfo> sourceList = flag ? showPlayBoxList : unShowPlayBoxList;
        // 显示类型(0默认1竞技场2百人场)
        if (EBoxShowType.NORMAL.match(type)) {
            for (PCLIBoxRoomStateInfo tempInfo : sourceList) {
                if (!(tempInfo.boxType == EBoxType.ARENA.getType() || tempInfo.boxType == EBoxType.HUNDRED.getType())) {
                    if (gameType > 0 && gameType != tempInfo.gameType) {
                        continue;
                    }
                    
                    
                    tempList.add(tempInfo);
                }
            }
            
            
            //机器人筛选
            List<PCLIBoxRoomStateInfo> robotBoxList = new ArrayList<>();
            if(gameType==-1) {
                robotBoxList = this.robotBoxList;
            }else {
                List<PCLIBoxRoomStateInfo> robotBoxList1=this.robotBoxList;
                
                if(!this.robotBoxList.isEmpty()) {
                    for(PCLIBoxRoomStateInfo p :robotBoxList1) {
                        if(p.gameType==gameType) {
                            robotBoxList.add(p);
                        } 
                    }
                }
               
                
            }
            
            
            
            
            tempList.addAll(robotBoxList);
        } else if (EBoxShowType.ARENA.match(type)) {
            for (PCLIBoxRoomStateInfo tempInfo : sourceList) {
                if (tempInfo.boxType == EBoxType.ARENA.getType()) {
                    tempList.add(tempInfo);
                }
            }
        } else if (EBoxShowType.HUNDRED.match(type)) {
            for (PCLIBoxRoomStateInfo tempInfo : sourceList) {
                if (tempInfo.boxType == EBoxType.HUNDRED.getType()) {
                    tempList.add(tempInfo);
                }
            }
        }
        return tempList;
    }

    public List<PCLIBoxRoomStateInfo> getAndSetShowBoxList(IClub club, boolean flag, int gameType, int gameSubType, int endPoint, int playType) {
        refreshShowBoxList(club, Boolean.FALSE);
        List<PCLIBoxRoomStateInfo> tempList = new ArrayList<>();
        List<PCLIBoxRoomStateInfo> sourceList = flag ? showPlayBoxList : unShowPlayBoxList;

        for (PCLIBoxRoomStateInfo tempInfo : sourceList) {
            if (gameType > 0 && gameSubType >= 0 && (gameType != tempInfo.gameType
                    || gameSubType != tempInfo.gameSubType
                    || endPoint != tempInfo.endPoint
                    || playType != tempInfo.playType
//                    || endPointMul != tempInfo.endPointMul
            )) {
                continue;
            }
            if (gameType <= 0 && tempInfo.roomIndex != 0){
                continue;
            }
            if (gameSubType < 0 && tempInfo.gameType != gameType){
                continue;
            }
            tempList.add(tempInfo);
        }

        //机器人筛选
        List<PCLIBoxRoomStateInfo> robotBoxList = new ArrayList<>();
        if(gameType==-1) {
            robotBoxList = this.robotBoxList;
        }else {
            List<PCLIBoxRoomStateInfo> robotBoxList1=this.robotBoxList;
            if(!this.robotBoxList.isEmpty()) {
                for(PCLIBoxRoomStateInfo p :robotBoxList1) {
                    if (gameType > 0 && gameSubType >= 0 && (gameType != p.gameType
                            || gameSubType != p.gameSubType
                            || endPoint != p.endPoint
                            || playType != p.playType
//                            || endPointMul != p.endPointMul
                    )) {
                        continue;
                    }
                    if (gameType <= 0 && p.roomIndex != 0){
                        continue;
                    }
                    if (gameSubType < 0 && p.gameType != gameType){
                        continue;
                    }
                    robotBoxList.add(p);
                }
            }
        }
        tempList.addAll(robotBoxList);

        return tempList;
    }

    /**
     * 获取该楼层中已创建的包厢数量
     * 
     * @return
     */
    public synchronized int size() {
        return gameUid.size();
    }

    /**
     * 启服时初始化
     * 
     * @param uid
     */
    public void init(long uid) {
        this.gameUid.add(uid);
        if (!showGameUid.contains(uid)) {
            showGameUid.add(uid);
        }

    }

    public synchronized void addGame(long uid) {
        this.gameUid.add(uid);
        if (!showGameUid.contains(uid)) {
            showGameUid.add(uid);
            this.dirty = true;
            this.save();
        }

    }

    public synchronized void delGame(long uid) {
        this.gameUid.remove(uid);
        if (showGameUid.contains(uid)) {
            showGameUid.remove(uid);
            this.dirty = true;
            this.save();
        }

    }

    @JSONField(serialize = false)
    public synchronized Long[] getAllGameUid() {
        return this.gameUid.toArray(new Long[0]);
    }

    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    public int getFloorType() {
        return floorType;
    }

    public void setFloorType(int floorType) {
        this.floorType = floorType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getGameUid() {
        return gameUid;
    }

    public void setGameUid(Set<Long> gameUid) {
        this.gameUid = gameUid;
        if (this.gameUid.size() > 0) {
            this.gameUid.forEach((itemId) -> {
                this.showGameUid.add(itemId);
            });
            Collections.sort(this.showGameUid);
        }
    }

    public synchronized List<Long> getShowGameUid() {
        return showGameUid;
    }

    public void setShowGameUid(List<Long> showGameUid) {
        this.showGameUid = showGameUid;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public int getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }

    public String getGameUidDb() {
        return JsonUtil.toJson(this.gameUid);
    }

    public void setGameUidDb(String gameUid) {
        this.gameUid = JsonUtil.fromJson(gameUid, new TypeReference<Set<Long>>() {
        });
    }

    public int getAllPlayerCnt(IClub club) {
        int cnt = 0;
        List<Long> gameId = getShowGameUid();
        if (gameId.size() > 0) {
            // 游戏桌显示规则：优先显示未开始的游戏桌，再显示已开始的游戏桌；
            for (long id : gameId) {
                Box box = club.getBox(id);
                if (null == box) {
                    continue;
                }
                cnt += box.getAllPlayerCnt();
            }
        }
        return cnt;
    }

    public int getCurRobotDesk2() {
        return curRobotDesk2;
    }

    public void setCurRobotDesk2(int curRobotDesk2) {
        this.curRobotDesk2 = curRobotDesk2;
    }

    public int getCurRobotDesk3() {
        return curRobotDesk3;
    }

    public void setCurRobotDesk3(int curRobotDesk3) {
        this.curRobotDesk3 = curRobotDesk3;
    }

    public int getSetRobotDesk2Min() {
        return setRobotDesk2Min;
    }

    public void setSetRobotDesk2Min(int setRobotDesk2Min) {
        this.setRobotDesk2Min = setRobotDesk2Min;
    }

    public int getSetRobotDesk2Max() {
        return setRobotDesk2Max;
    }

    public void setSetRobotDesk2Max(int setRobotDesk2Max) {
        this.setRobotDesk2Max = setRobotDesk2Max;
    }

    public int getSetRobotDesk3Min() {
        return setRobotDesk3Min;
    }

    public void setSetRobotDesk3Min(int setRobotDesk3Min) {
        this.setRobotDesk3Min = setRobotDesk3Min;
    }

    public int getSetRobotDesk3Max() {
        return setRobotDesk3Max;
    }

    public void setSetRobotDesk3Max(int setRobotDesk3Max) {
        this.setRobotDesk3Max = setRobotDesk3Max;
    }

    public int getRandomTime2() {
        return randomTime2;
    }

    public void setRandomTime2(int randomTime2) {
        this.randomTime2 = randomTime2;
    }

    public int getRandomTime3() {
        return randomTime3;
    }

    public void setRandomTime3(int randomTime3) {
        this.randomTime3 = randomTime3;
    }

    public int getSetRobotDesk2() {
        return setRobotDesk2;
    }

    public int getSetRobotDesk3() {
        return setRobotDesk3;
    }
    
    public int getCurRobotDesk4() {
        return curRobotDesk4;
    }

    public void setCurRobotDesk4(int curRobotDesk4) {
        this.curRobotDesk4 = curRobotDesk4;
    }

    public int getSetRobotDesk4Min() {
        return setRobotDesk4Min;
    }

    public void setSetRobotDesk4Min(int setRobotDesk4Min) {
        this.setRobotDesk4Min = setRobotDesk4Min;
    }

    public int getSetRobotDesk4Max() {
        return setRobotDesk4Max;
    }

    public void setSetRobotDesk4Max(int setRobotDesk4Max) {
        this.setRobotDesk4Max = setRobotDesk4Max;
    }

    public int getSetRobotDesk4() {
        return setRobotDesk4;
    }

    public void setSetRobotDesk4(int setRobotDesk4) {
        this.setRobotDesk4 = setRobotDesk4;
    }

    public int getRandomTime4() {
        return randomTime4;
    }

    public void setRandomTime4(int randomTime4) {
        this.randomTime4 = randomTime4;
    }

    public long getLastRandomTime4() {
        return lastRandomTime4;
    }

    public void setLastRandomTime4(long lastRandomTime4) {
        this.lastRandomTime4 = lastRandomTime4;
    }

    /**
     * 是否存在已开始的游戏桌
     * 
     * @return
     */
    public boolean existStartedGameDesk(IClub club) {
        List<Long> gameId = showGameUid;
        if (gameId.size() > 0) {
            for (long id : gameId) {
                Box box = club.getBox(id);
                if (null == box) {
                    continue;
                }
                if (box.existStartedGameDesk()) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public void killAllIdleRoom(IClub club) {
        List<Long> gameId = showGameUid;
        if (gameId.size() > 0) {
            for (long id : gameId) {
                Box box = club.getBox(id);
                if (null == box) {
                    continue;
                }
                box.killAllIdleRoom();
            }
        }
    }
}
