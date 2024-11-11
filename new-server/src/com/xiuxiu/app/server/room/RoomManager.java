package com.xiuxiu.app.server.room;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfLeaveInfo;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.chat.ChatManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.handle.RoomHandleFactory;
import com.xiuxiu.app.server.room.normal.EState;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.app.server.table.DiamondCostManager;
import com.xiuxiu.core.IClassExecute;
import com.xiuxiu.core.utils.PackageUtil;
import com.xiuxiu.core.utils.RandomUtil;

public class RoomManager extends BaseManager implements IClassExecute {
    private static class RoomManagerHolder {
        private static RoomManager instance = new RoomManager();
    }

    public static RoomManager I = RoomManagerHolder.instance;

    private ConcurrentHashMap<Integer, IRoom> allRoom = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, ConcurrentHashMap<Integer, Room>> group2Room = new ConcurrentHashMap<>();
    private HashMap<ERoomType, HashMap<Long, Constructor<?>>> allRoomClass = new HashMap<>();
    private HashMap<Integer, Boolean> ignoreSubGameType = new HashMap<>();

    private RoomManager() {
    }

    public void init() {
        this.allRoomClass.clear();
        PackageUtil.scanPackage("com.xiuxiu.app.server.room", this);
    }

    @Override
    public void execute(Class<?> clazz) {
        try {
            GameInfo info = clazz.getDeclaredAnnotation(GameInfo.class);
            if (null == info) {
                return;
            }
            HashMap<Long, Constructor<?>> roomClass = this.allRoomClass.get(info.roomType());
            if (null == roomClass) {
                roomClass = new HashMap<>();
                this.allRoomClass.putIfAbsent(info.roomType(), roomClass);
                roomClass = this.allRoomClass.get(info.roomType());
            }
            Constructor<?> c = clazz.getConstructor(new Class[]{RoomInfo.class});
            long key;
            if (-1 == info.gameSubType()) {
                key = info.gameType() << 10;
            } else {
                key = (info.gameType() << 10) | info.gameSubType();
            }
            roomClass.putIfAbsent(key, c);
            ignoreSubGameType.putIfAbsent(info.gameType(), -1 == info.gameSubType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void create(Player player, long groupUid, int gameType, int gameSubType, HashMap<String, Integer> rule) {
        Room room;
        if (-1 != player.getRoomId()) {
            Logs.ROOM.error("%s 创建房间失败, 已经再房间里面无法创建房间", player);
            player.send(CommandId.CLI_NTF_ROOM_CREATE_FAIL, ErrorCode.PLAYER_ROOM_IN);
            return;
        }
        int diamondCost = DiamondCostManager.I.getCostByGameType(gameType, DiamondCostManager.COST_TYPE_ROOM, rule.getOrDefault(RoomRule.RR_BUREAU, 8));
        if(!player.addMoney(EMoneyType.DIAMOND, -diamondCost, player.getUid(), groupUid, EMoneyExpendType.LOBBY_EXPEND, -1)){
            Logs.ROOM.warn("%s 创建房间失败, 钻石不足", player);
            player.send(CommandId.CLI_NTF_ROOM_LACK_DIAMOND,null);
            return;
        }
        int roomId = this.getRoomId();
        RoomInfo info = new RoomInfo();
        info.setUid(UIDManager.I.getAndInc(UIDType.ROOM));
        info.setRoomId(roomId);
        info.setGroupUid(groupUid);
        info.setGameType(gameType);
        info.setGameSubType(gameSubType);
        info.setOwnerPlayerUid(player.getUid());
        info.setCreateTime(System.currentTimeMillis());
        info.setCost(diamondCost);
        info.getRule().putAll(rule);
        info.setDirty(true);

        room = this.createRoom(ERoomType.NORMAL, info);
        if (null == room) {
            Logs.ROOM.error("%s 创建房间失败, 无效数据没有改类型的房间 gameType:%d gameSubType:%d", player, gameType, gameSubType);
            player.send(CommandId.CLI_NTF_ROOM_CREATE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return;
        }
        // 创建房间业务逻辑处理器
        room.setRoomHandle(RoomHandleFactory.createRoomHandle(room));
        room.init();
        Logs.ROOM.debug("%s 创建房间成功 roomInfo:%s", player, room);
        this.allRoom.putIfAbsent(room.getRoomId(), room);
        ConcurrentHashMap<Integer, Room> rooms = this.group2Room.get(groupUid);
        if (null == rooms) {
            rooms = new ConcurrentHashMap<>();
            this.group2Room.putIfAbsent(groupUid, rooms);
            rooms = this.group2Room.get(groupUid);
        }
        rooms.putIfAbsent(room.getRoomId(), room);
        player.send(CommandId.CLI_NTF_ROOM_CREATE_OK, room.getRoomBriefInfo());
        this.joinByCreate(player, room.getRoomId());
        ChatManager.I.notifyCreateRoom(player, room);
    }

    public Room createBoxRoom(Player player, Box box, int gameType, int gameSubType, HashMap<String, Integer> rule,String remarks) {
        IClub club = ClubManager.I.getClubByUid(box.getOwnerUid());
        if (null == club) {
            Logs.ROOM.error("%s 创建包厢房间失败, 群不存在 groupUid:&d", player, box.getOwnerUid());
            return null;
        }
        Player groupChief = (Player) club.getOwnerPlayer();
        if (null == groupChief) {
            Logs.ROOM.error("%s 创建包厢房间失败, 群主不存在 groupUid:&d chiefPlayerUid:%d", player, box.getOwnerUid(), club.getOwnerId());
            return null;
        }
        int diamondCost = DiamondCostManager.I.getCostByGameType(gameType, DiamondCostManager.COST_TYPE_ROOM, rule.getOrDefault(RoomRule.RR_BUREAU, 8));
        int roomId = this.getRoomId();
        RoomInfo info = new RoomInfo();
        info.setUid(UIDManager.I.getAndInc(UIDType.ROOM));
        info.setRoomId(roomId);
        info.setGroupUid(box.getOwnerUid());
        info.setGameType(gameType);
        info.setGameSubType(gameSubType);
        info.setOwnerPlayerUid(null == player ? -1 : player.getUid());
        info.setCreateTime(System.currentTimeMillis());
        info.setCost(diamondCost);
        info.getRule().putAll(rule);
        info.setOwnerType(box.getOwnerType());
        info.setRemarks(remarks);
        info.setDirty(true);

        Room room = this.createRoom(ERoomType.BOX, info);
        if (null == room) {
            Logs.ROOM.error("创建包厢房间失败, 无效数据没有改类型的房间 gameType:%d gameSubType:%d", gameType, gameSubType);
            return null;
        }
       
        return room;
    }
    
    public void addBoxRoom(IRoom room, Box box) {
        room.init();
        Logs.ROOM.debug("%s 添加包厢房间 roomInfo:%s", box, room);
        this.allRoom.putIfAbsent(room.getRoomId(), room);
    }

    private int getRoomId() {
        int roomId = -1;
        int step = 0;
        do {
            roomId = RandomUtil.random(100000, 999999);
            ++step;
            if (step >= 10) {
                Logs.ROOM.error("房间号获取异常, 超过10次");
                step = 0;
            }
        } while (this.allRoom.containsKey(roomId));
        return roomId;
    }

    public ErrorCode joinByCreate(Player player, int roomId) {
        Room room = (Room) RoomManager.I.getRoom(roomId);
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            return ErrorCode.ROOM_NOT_EXISTS;
        }
        if (-1 != player.getRoomId()) {
            // 已经在房间里
            if (roomId != player.getRoomId()) {
                Logs.ROOM.warn("%s 已经在房间里无法计入其他房间", player);
                return ErrorCode.PLAYER_ROOM_IN;
            }
            room.join(player);
            return ErrorCode.OK;
        }
        ErrorCode err = room.join(player);
        if (ErrorCode.OK == err) {
            player.changeRoomId(room.getRoomId(), room.getGameType());
            player.send(CommandId.CLI_NTF_ROOM_JOIN_OK, room.getRoomBriefInfo());
            room.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, room.getRoomInfo());
        } else {
            Logs.ROOM.warn("加入房间失败 房间:%s err:%s", room, err);
        }
        return err;
    }

    public ErrorCode changeSeat(Player player,int newSeatIndex){
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            return ErrorCode.ROOM_NOT_EXISTS;
        }
        return room.changeSeate(player,newSeatIndex);
    }

    public ErrorCode join(Player player, int roomId) {
        Room room = (Room) RoomManager.I.getRoom(roomId);
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            return ErrorCode.ROOM_NOT_EXISTS;
        }
        ErrorCode ec = this.checkIpSame(player,room);
        if (ec != ErrorCode.OK){
            return ec;
        }
        
        if (-1 != player.getRoomId()) {
            // 已经在房间里
            if (roomId != player.getRoomId()) {
                Logs.ROOM.warn("%s 已经在房间里无法计入其他房间", player);
                return ErrorCode.PLAYER_ROOM_IN;
            }
            EState oldState = null;
            IRoomPlayer roomPlayer = room.getRoomPlayer(player.getUid());
            if (roomPlayer != null) {
                oldState = roomPlayer.getState();
            }
            player.send(CommandId.CLI_NTF_ROOM_JOIN_OK, room.getRoomBriefInfo());
            room.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, room.getRoomInfo());
            room.join(player);
            player.changeRoomId(room.getRoomId(), room.getGameType());
            if (null == oldState || oldState != EState.OFFLINE) {
                room.syncDeskInfo(player);
                room.syncCurState(player);
            }
            return ErrorCode.OK;
        }
        ErrorCode err = room.join(player);
        if (ErrorCode.OK == err) {
            player.changeRoomId(room.getRoomId(), room.getGameType());
            player.send(CommandId.CLI_NTF_ROOM_JOIN_OK, room.getRoomBriefInfo());
            room.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, room.getRoomInfo());
        } else if (ErrorCode.ROOM_MIDDLE_JOIN == err) {
            player.changeRoomId(room.getRoomId(), room.getGameType());
            player.send(CommandId.CLI_NTF_ROOM_JOIN_OK, room.getRoomBriefInfo());
            player.send(CommandId.CLI_NTF_ROOM_INFO, room.getRoomInfo());
            err = ErrorCode.OK;
        } else {
            Logs.ROOM.warn("加入房间失败 房间:%s err:%s", room, err);
        }
        return err;
    }
    
    public ErrorCode online(Player player) {
        Room room = (Room) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            return ErrorCode.ROOM_NOT_EXISTS;
        }
        if (!this.lock(player.getUid())) {
            Logs.ROOM.warn("%s 正在忙", player);
            return ErrorCode.PLAYER_BUSY;
        }
        try {
            // 已经在房间里
            ErrorCode err = room.changeState(player, EState.ONLINE);
            return err;
        } finally {
            this.unlock(player.getUid());
        }
    }

    /**
     * 离开房间
     * @param player
     * @return
     */
    public ErrorCode leave(Player player) {
        Room room = (Room) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.changeRoomId(-1, -1);
            return ErrorCode.ROOM_NOT_EXISTS;
        }
        ErrorCode err = ErrorCode.OK;
        if (GameType.isArenaGame(room.getGameType())) {
            err = this.arenaLeave(player, room);
        } else {
            IRoomHandle roomHandle = room.getRoomHandle();
            if (ERoomState.NEW == room.getRoomState()) {
                // 新房间
                if (roomHandle instanceof IBoxRoomHandle){
                    if(room.getPlayerCnt()==1&&room.isWatchEmpty()){
                        this.destroyRoom(room);
                    }else{
                        err = room.leave(player);
                    }
                }else{
                    if (room.isOwner(player.getUid())) {
                        // 房主离开 直接解散
                        this.destroyRoom(room);
                    } else {
                        err = room.leave(player);
                    }
                }
            } else {
                if (roomHandle instanceof IBoxRoomHandle){
                    IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle)roomHandle;
                    // 是不是观战玩家
                    if (!boxRoomHandle.hasPlayed(player.getUid())) {
                        IRoomPlayer roomPlayer = room.getRoomPlayer(player.getUid());
                        if (roomPlayer != null) {
                            BoxManager.I.sitUp(player, room.getRoomId(), (IClub) room.getBoxOwner());
                        }
                        err = room.leave(player);
                    } else {
                        IClub club = (IClub) room.getBoxOwner();
                        long fromClubUid = club.getEnterFromClubUid(player.getUid());
                        IClub fromClub = ClubManager.I.getClubByUid(fromClubUid);
                        if (fromClub != null) {
                            if (!BoxManager.I.checkEnoughGold(fromClub, room.getRule(), player.getUid(), false)) {
                                err = room.leave(player);
                            } else {
                                err = ErrorCode.ROOM_LEAVE;
                            }
                        } else {
                            err = ErrorCode.ROOM_LEAVE;
                        }
                    }
                } else {
                    err = room.leave(player);
                }
            }
            if (ErrorCode.OK == err) {
                player.changeRoomId(-1, -1);
                PCLIRoomNtfLeaveInfo leaveInfo = new PCLIRoomNtfLeaveInfo();
                leaveInfo.roomId = room.getRoomId();
                leaveInfo.playerUid = player.getUid();
                room.broadcast2Client(CommandId.CLI_NTF_ROOM_LEAVE, leaveInfo);
            }
        }

        return err;
    }

    /**
     * 竞技场离开房间（牛牛金花）
     * @param player
     * @return
     */
    public ErrorCode arenaLeave(Player player, Room room) {
        IRoomPlayer roomPlayer = room.getRoomPlayer(player.getUid());
        ErrorCode err = ErrorCode.OK;

        //玩家在房间中的状态 1.正常（坐下并准备） 2.游客（坐下未准备） 3.观察者（未坐下）,4.房间没有坐下的玩家时
        int state = 1;
        if (room.isWatchPlayer(player.getUid()) || roomPlayer == null) {
            if ((GameType.GAME_TYPE_PAIGOW == room.getGameType() || (GameType.GAME_TYPE_COW == room.getGameType() && room.getGameSubType() == 1))
                    && room.getPlayerCnt() == 0 && room.isWatchEmpty()) {
                state = 4;
            } else {
                state = 3;
            }
        } else if (roomPlayer.isGuest()) {
            state = 2;
            BoxManager.I.sitUp(player, room.getRoomId(), (IClub) room.getBoxOwner());
        }

        if (state == 1 || state == 2) {
            if (room.getPlayerCnt()==1) {
                this.destroyRoom(room);
            } else {
                if (state == 1 && room.getGameType() == GameType.GAME_TYPE_COW && room.getGameSubType() == 1 && room.getCurBureau() != 0) {
                    return ErrorCode.ROOM_LEAVE;
                }
                err = room.leave(player);
            }
        } else if (state == 4) {
            this.destroyRoom(room);
        } else {
            err = room.leave(player);
        }

//        else {
//            if (roomHandle instanceof IBoxRoomHandle){
//                if(room.getPlayerCnt()==1&&room.isWatchEmpty()){
//                    this.destroyRoom(room);
//                } else {
//                    IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle)roomHandle;
//                    // 是不是观战玩家
//                    if (!boxRoomHandle.hasPlayed(player.getUid())) {
//                        if(room.getPlayerCnt() == 0 && room.WatchSize() == 1){
//                            this.destroyRoom(room);
//                        } else {
//                            IRoomPlayer roomPlayer = room.getRoomPlayer(player.getUid());
//                            if (roomPlayer != null) {
//                                BoxManager.I.sitUp(player, room.getRoomId(), (IClub) room.getBoxOwner());
//                            }
//                            err = room.leave(player);
//                        }
//                    } else if (boxRoomHandle.noGuest(player.getUid())) {
//                        err = room.leave(player);
//                    } else {
//                        IClub club = (IClub) room.getBoxOwner();
//                        long fromClubUid = club.getEnterFromClubUid(player.getUid());
//                        IClub fromClub = ClubManager.I.getClubByUid(fromClubUid);
//                        if (fromClub != null) {
//                            if (!BoxManager.I.checkEnoughGold(fromClub, room.getRule(), player.getUid(), false)) {
//                                err = room.leave(player);
//                            } else {
//                                err = ErrorCode.ROOM_LEAVE;
//                            }
//                        } else {
//                            err = ErrorCode.ROOM_LEAVE;
//                        }
//                    }
//                }
//            } else {
//                err = room.leave(player);
//            }
//        }
        if (ErrorCode.OK == err || ErrorCode.PLAYER_ROOM_NOT_IN == err) {
            player.changeRoomId(-1, -1);
            PCLIRoomNtfLeaveInfo leaveInfo = new PCLIRoomNtfLeaveInfo();
            leaveInfo.roomId = room.getRoomId();
            leaveInfo.playerUid = player.getUid();
            room.broadcast2Client(CommandId.CLI_NTF_ROOM_LEAVE, leaveInfo);
        }
        return err;
    }

    public ErrorCode offline(Player player) {
        if (-1 == player.getRoomId()) {
            Logs.ROOM.warn("%s 不在房间里", player);
            return ErrorCode.ROOM_NOT_EXISTS;
        }
        while (!this.lock(player.getUid())) {
            Logs.ROOM.warn("%s room offline 正在忙", player);
            this.unlock(player.getUid());
            Thread.yield();
        }
        try {
            Room room = (Room) RoomManager.I.getRoom(player.getRoomId());
            if (null == room) {
                Logs.ROOM.warn("%s 不在房间里", player);
                player.changeRoomId(-1, -1);
                return ErrorCode.ROOM_NOT_EXISTS;
            }
            ErrorCode err = room.changeState(player, EState.OFFLINE);
            // 下线处理
            room.getRoomHandle().offline(player);
            return err;
        } finally {
            this.unlock(player.getUid());
        }
    }

    public void destroyRoom(Room room) {
        room.destroy();
        this.allRoom.remove(room.getRoomId());
        ConcurrentHashMap<Integer, Room> groupRoom = this.group2Room.get(room.getGroupUid());
        if (null != groupRoom) {
            groupRoom.remove(room.getRoomId());
        }
    }

    public void removeRoom(Room room) {
        this.allRoom.remove(room.getRoomId());
        ConcurrentHashMap<Integer, Room> groupRoom = this.group2Room.get(room.getGroupUid());
        if (null != groupRoom) {
            groupRoom.remove(room.getRoomId());
        }
    }

    @Override
    public int shutdown() {
        int cnt = 0;
        try {
            Iterator<Map.Entry<Integer, IRoom>> it = this.allRoom.entrySet().iterator();
            while (it.hasNext()) {
                IRoom room = it.next().getValue();
                room.destroy();
            }
        } catch (Throwable e) {
            Logs.CORE.error(e);
        }

        return cnt;
    }

    @Override
    public int save() {
        int cnt = 0;
        try {
            Iterator<Map.Entry<Integer, IRoom>> it = this.allRoom.entrySet().iterator();
            while (it.hasNext()) {
                IRoom room = it.next().getValue();
                if (room.save()) {
                    ++cnt;
                }
            }
        } catch (Throwable e) {
            Logs.CORE.error(e);
        }

        return cnt;
    }

    public Room getRoom(int roomId) {
        return (Room) this.allRoom.get(roomId);
    }

    public ConcurrentHashMap<Integer, Room> getRoomByGroupUid(long groupUid) {
        return this.group2Room.get(groupUid);
    }

    private Room createRoom(ERoomType roomType, RoomInfo roomInfo) {
        roomType = ERoomType.BOX == roomType ? ERoomType.NORMAL : roomType;
        HashMap<Long, Constructor<?>> roomClass = this.allRoomClass.get(roomType);
        if (null == roomClass) {
            return null;
        }
        long key;
        if (this.ignoreSubGameType.getOrDefault(roomInfo.getGameType(), false)) {
            key = roomInfo.getGameType() << 10;
        } else {
            key = (long) (roomInfo.getGameType() << 10 | roomInfo.getGameSubType());
        }
        Constructor<?> cls = roomClass.get(key);
        if (null == cls) {
            return null;
        }
        try {
            Room room = (Room) cls.newInstance(roomInfo);
            return room;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ErrorCode checkIpSame(Player player,Room room){
        if (null == room){
            Logs.ROOM.warn("%s 不在房间里", player);
            return ErrorCode.ROOM_NOT_EXISTS;
        }
        for (IRoomPlayer iRoomPlayer : room.getCurrPlayers()) {
            if (null == iRoomPlayer){
                continue;
            }
            Player player1 = PlayerManager.I.getPlayer(iRoomPlayer.getUid());
            if (null == player1) {
                continue;
            }
            if (player == player1){
                continue;
            }
            //百人场和竞技场牛牛不检测IP
            IRoomHandle roomHandle = room.getRoomHandle();
            if (roomHandle instanceof IBoxRoomHandle) {
                if (EBoxType.HUNDRED.match(((IBoxRoomHandle) roomHandle).getBoxType())) {
                    continue;
                }
                
                if (roomHandle.getRoom().getGameType() == GameType.GAME_TYPE_COW) {
                    continue;
                }
                if (roomHandle.getRoom().getGameType() == GameType.GAME_TYPE_THIRTEEN) {
                    continue;
                }
                if (roomHandle.getRoom().getGameType() == GameType.GAME_TYPE_PAIGOW) {
                    continue;
                }
                if (roomHandle.getRoom().getGameType() == GameType.GAME_TYPE_SG) {
                    continue;
                }
            }
            if (player.getLogngIp().equals(player1.getLogngIp()) && room.getDetectionIP()) {
                Logs.ROOM.warn("%s %s IP相同无法加入", this, player);
                return  ErrorCode.CHECK_IP_SAME;
            }
        }
        return  ErrorCode.OK;
    }
}
