package com.xiuxiu.app.server.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfAddBoxInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfChangeRuleInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfDelInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfNameInfo;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.constant.EBoxState;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.app.server.floor.FloorManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.score.BoxRoomScore;
import com.xiuxiu.app.server.score.ScoreItemInfo;
import com.xiuxiu.app.server.table.DiamondCostManager;
import com.xiuxiu.core.cache.Cache;
import com.xiuxiu.core.net.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 包厢管理器
 * 
 * @author Administrator
 *
 */
public class BoxManager extends BaseManager {

    private BoxManager() {
    }

    private static class BoxManagerHolder {
        private static BoxManager instance = new BoxManager();
    }

    public static BoxManager I = BoxManagerHolder.instance;

    private Cache<Long, BoxRoomScore> cacheByUid = new Cache<>(1, TimeUnit.DAYS, 10000,
            new Cache.Load<Long, BoxRoomScore>() {
                @Override
                public BoxRoomScore load(Long key) throws Exception {
                    if (null == key) {
                        return null;
                    }
                    BoxRoomScore roomScore = DBManager.I.load(key, ETableType.TB_BOX_SCORE);
                    if (null == roomScore) {
                        return null;
                    }
                    return roomScore;
                }
            });

    private ConcurrentHashMap<Long, BoxRoomScore> dirtyBoxRoomScore = new ConcurrentHashMap<>();

    /** 包厢容器 */
    private ConcurrentHashMap<Long, Box> allBox = new ConcurrentHashMap<>();

    /** 包厢创建通知，如果要通知的人大于 300 人就不通知了 */
    private static final int MAX_NOTICE_COUNT = 300;

    /**
     * 根据游戏大类型获取包厢类型
     * @param gameType
     * @return
     */
    private EBoxType getBoxType(int gameType) {
        if (GameType.isHundredGame(gameType)) {
            return EBoxType.HUNDRED;
        } else if(GameType.isArenaGame(gameType)) {
            if (gameType == GameType.GAME_TYPE_PAIGOW) {
                return EBoxType.NORMAL;
            }
            return EBoxType.ARENA;
        }
        return EBoxType.NORMAL;
    }
    /**
     * 创建包厢
     * 
     * @param player
     * @param club
     * @param floor
     * @param boxType
     * @param gameType
     * @param gameSubType
     * @param rule
     * @param extra
     * @return
     */
    public ErrorCode create(Player player, IClub club, Floor floor, int boxType, int gameType, int gameSubType, HashMap<String, Integer> rule, HashMap<String, String> extra) {
        // 判断该楼层玩法桌是否达到数量上限
        if (floor.size() >= 100) {
            // 一个楼层最多可以创建100个玩法桌
            return ErrorCode.FLOOR_BOX_MAX;
        }
        if (!EBoxType.CUSTOM.match(boxType)) {
            boxType = getBoxType(gameType).getType();
        }
        //牛牛端锅玩法开房面板检查
        if (gameType == GameType.GAME_TYPE_COW && gameSubType == 1){
            if (rule == null){
                return ErrorCode.REQUEST_INVALID;
            }
            if (rule.getOrDefault(RoomRule.RR_COW_PORKER_CARD_HOT_NOTE,0) > rule.getOrDefault(RoomRule.RR_MINGOLD,0)){
                return ErrorCode.REQUEST_INVALID;
            }
        }
        Box box = new Box();
        box.setUid(UIDManager.I.getAndInc(UIDType.BOX));
        box.setOwnerType(club.getClubType().getType());
        box.setOwnerUid(club.getClubUid());
        box.setFloorUid(floor.getUid());
        box.setBoxType(boxType);
        box.setGameType(gameType);
        box.setGameSubType(gameSubType);
        box.modifyRule(rule, extra);
        box.setCreateTime(System.currentTimeMillis());
        box.init();
        // 入到缓存容器
        addBox(box.getUid(), box);
        // 关联楼层
        floor.addGame(box.getUid());
        // 关联亲友圈
        club.addBox(box);
        // 通知创建包厢
        noticeCreateBox(club, box, player);
        // 判断是否是百人场
        if (EBoxType.HUNDRED.match(boxType)) {
            // 百人场创建玩法桌子box时，同时创建游戏桌子room
            onCreateHundredBox(player, box);
        }
        // 强制刷新显示包厢列表
        floor.refreshShowBoxList(club, Boolean.TRUE);
        return ErrorCode.OK;
    }
    
    private void onCreateHundredBox(Player player, Box box) {
        IBoxRoomHandle boxRoomHandle = box.createBoxRoom(player);
        if (null == boxRoomHandle) {
            Logs.ROOM.error("%s创建房间失败%d，onCreateHundredBox", player.getUid(), box.getGameType());
        }
    }

    /**
     * 通知创建包厢
     * 
     * @param club
     * @param box
     */
    private void noticeCreateBox(IClub club, Box box, Player player) {
        PCLIBoxNtfAddBoxInfo addBoxInfo = new PCLIBoxNtfAddBoxInfo();
        addBoxInfo.info = box.getBoxInfo();
        player.send(CommandId.CLI_NTF_BOX_ADD, addBoxInfo);

        long memberSize = club.getMemberCnt();
        List<Long> allClubUidList = new ArrayList<>();
        club.fillDepthChildClubUidList(allClubUidList);
        for (Long cludUid : allClubUidList) {
            IClub tempClub = ClubManager.I.getClubByUid(cludUid);
            if (tempClub == null) {
                continue;
            }
            memberSize += tempClub.getMemberCnt();
        }
        if (memberSize <= MAX_NOTICE_COUNT) {
            club.broadcastAllLowClub(CommandId.CLI_NTF_BOX_ADD, addBoxInfo);
        }
    }

    /**
     * 关闭包厢
     * 
     * @param player
     * @param club
     * @param box
     * @return
     */
    public ErrorCode close(Player player, IClub club, Box box) {
        if (box.close()) {
            Floor floor = club.getFloor(box.getFloorUid());
            // 从楼层关联关系中删除
            floor.delGame(box.getUid());
            // 亲友圈关联相关关系删除
            club.destroyBox(box.getUid());
            // 从缓存容器中删除
            remove(box.getUid());
            // 通知关闭包厢
            noticeCloseClubBox(club, box.getUid());
            // 强制刷新显示包厢列表
            floor.refreshShowBoxList(club, Boolean.TRUE);
        }
        return ErrorCode.OK;
    }

    /**
     * 通知关闭包厢
     * 
     * @param club
     * @param boxUid
     */
    private void noticeCloseClubBox(IClub club, long boxUid) {
        PCLIBoxNtfDelInfo delBoxInfo = new PCLIBoxNtfDelInfo();
        delBoxInfo.clubUid = club.getClubUid();
        delBoxInfo.boxUid = boxUid;
        club.broadcastToAllClub(CommandId.CLI_NTF_BOX_DEL, delBoxInfo);
    }

    /**
     * 修改包厢规则
     * 
     * @param player
     * @param club
     * @param box
     * @param extra
     * @return
     */
    public ErrorCode modifyRule(Player player, IClub club, Box box, HashMap<String, String> extra) {
        // 是否自定义包厢
        if (EBoxType.CUSTOM.match(box.getBoxType())) {
            Logs.GROUP.warn("%s 修改包厢规则失败, 群:%d 包厢:%d 自定义包厢无法修改规则", player, club.getClubUid(), box.getUid());
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (EBoxState.INIT != box.getState()) {
            Logs.GROUP.warn("%s 正处于关闭/关闭中, 无法修改包厢规则", this);
            return ErrorCode.GROUP_BOX_CLOSE;
        }
//        box.setGameSubType(gameSubType);
        box.modifyRule(null,extra);
        box.setDirty(Boolean.TRUE);
        box.save();

        // 通知修改包厢规则
        noticeModifyRule(club, box);

        return ErrorCode.OK;
    }

    /**
     * 通知修改包厢规则
     * 
     * @param club
     * @param box
     */
    private void noticeModifyRule(IClub club, Box box) {
        PCLIBoxNtfChangeRuleInfo changeRuleInfo = new PCLIBoxNtfChangeRuleInfo();
        changeRuleInfo.info = box.getBoxInfo();
        notice(club, CommandId.CLI_NTF_BOX_CHANGE_RULE, changeRuleInfo);
    }

    /**
     * 修改包厢名字
     * 
     * @param player
     * @param club
     * @param box
     * @param boxName
     * @return
     */
    public ErrorCode modifyName(Player player, IClub club, Box box, String boxName) {
        // 判断是否加入主圈
        if (club.checkIsJoinInMainClub()) {
            if (!club.checkIsMainClub()) {
                // 是能在主圈修改包厢名称
                return ErrorCode.REQUEST_OPERATE_ERROR;
            }
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid())
                    || club.checkIsManager(player.getUid()))) {
                Logs.GROUP.warn("%s 群:%d不是群主 或者长老或管理员, 权限不足, 无法修改包厢规则", player, club.getClubUid());
                return ErrorCode.GROUP_NOT_PRIVILEGE_CREATE_BOX;
            }
        } else {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                Logs.GROUP.warn("%s 群:%d不是群主 或者长老, 权限不足,  无法修改包厢规则", player, club.getClubUid());
                return ErrorCode.GROUP_NOT_PRIVILEGE_CREATE_BOX;
            }
        }
        box.setBoxName(boxName);
        box.setDirty(Boolean.TRUE);
        box.save();

        // 通知修改包厢名称
        noticeModifyName(player, club, box);
        return ErrorCode.OK;
    }

    /**
     * 通知修改包厢名称
     * 
     * @param player
     * @param club
     * @param box
     */
    private void noticeModifyName(Player player, IClub club, Box box) {
        PCLIBoxNtfNameInfo changeNameInfo = new PCLIBoxNtfNameInfo();
        changeNameInfo.boxName = box.getBoxName();
        changeNameInfo.boxUid = box.getUid();
        changeNameInfo.clubUid = box.getOwnerUid();
        player.send(CommandId.CLI_NTF_BOX_CHANGE_NAME, changeNameInfo);
        notice(club, CommandId.CLI_NTF_BOX_CHANGE_NAME, changeNameInfo, player.getUid());
    }

    public boolean isWatch(int gameType, Map<String, Integer> rule) {
        boolean isWatch = GameType.isWatchGame(gameType);
        if (isWatch) {
            int playerNum = rule.getOrDefault(RoomRule.RR_PLAYER_NUM, GameType.isEightGame(gameType) ? 8 : 4);
            isWatch = rule.getOrDefault(RoomRule.RR_PLAYER_MIN_NUM, playerNum) < playerNum;
        }
        return isWatch;
    }

    /**
     * 加入房间
     * @param player
     * @param club
     * @param box
     * @param roomIndex
     * @return
     */
    public ErrorCode join(Player player, IClub club, Box box, int roomIndex) {
        return box.join(player, roomIndex);
    }

    /**
     * 销毁包厢
     * 
     * @param clubUid
     * @param boxUid
     */
    public void destroyBox(long clubUid, long boxUid) {
        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (null == club) {
            return;
        }
        club.destroyBox(boxUid);
    }

    @Override
    public int save() {
        int cnt = 0;
        int maxCnt = 100;
        Iterator<Map.Entry<Long, BoxRoomScore>> it = this.dirtyBoxRoomScore.entrySet().iterator();
        while (it.hasNext()) {
            BoxRoomScore boxRoomScore = it.next().getValue();
            DBManager.I.save(new Task() {
                @Override
                public void run() {
                    DBManager.I.getBoxRoomScoreDao().save(boxRoomScore);
                }
            });
            it.remove();
            ++cnt;
            if (cnt >= maxCnt) {
                break;
            }
        }
        return cnt;
    }

    @Override
    public int shutdown() {
        int cnt = 0;
        try {
            Iterator<Map.Entry<Long, BoxRoomScore>> it = this.dirtyBoxRoomScore.entrySet().iterator();
            while (it.hasNext()) {
                BoxRoomScore boxRoomScore = it.next().getValue();
                DBManager.I.getBoxRoomScoreDao().save(boxRoomScore);
                ++cnt;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return cnt;
    }

    public void addScore(BoxRoomScore score) {
        this.cacheByUid.put(score.getUid(), score);
    }

    public BoxRoomScore getScore(long boxRoomScoreUid) {
        return this.cacheByUid.get(boxRoomScoreUid);
    }

    public BoxRoomScore getScoreByDirty(long boxRoomScoreUid) {
        return this.dirtyBoxRoomScore.get(boxRoomScoreUid);
    }

    public ErrorCode like(Player player, long boxRoomScoreUid, long playerUid) {
        BoxRoomScore boxRoomScore = this.cacheByUid.get(boxRoomScoreUid);
        if (null == boxRoomScore) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        for (ScoreItemInfo itemInfo : boxRoomScore.getTotalScore().getScore()) {
            if (itemInfo.getPlayerUid() == playerUid) {
                if (itemInfo.getLike().add(player.getUid())) {
                    this.dirtyBoxRoomScore.put(boxRoomScoreUid, boxRoomScore);
                } else {
                    return ErrorCode.GROUP_BOX_ALREADY_LIKE;
                }
                break;
            }
        }
        boxRoomScore.setDirty(true);
        return ErrorCode.OK;
    }
    
    public ErrorCode canJoin(Player player, IClub fromClub, Box box, boolean ignoreWatch) {
        return canJoin(player, fromClub, box.getGameType(), box.getRule(), box.getOwnerType(), ignoreWatch);
    }
    
    private ErrorCode canJoin(Player player, IClub fromClub, int gameType, HashMap<String, Integer> rule, int ownerType, boolean ignoreWatch) {
        int costDiamond = DiamondCostManager.I.getCostByGameType(gameType, DiamondCostManager.COST_TYPE_ROOM, rule.getOrDefault(RoomRule.RR_BUREAU, 8));
        if (!fromClub.hasEnoughMoney(EMoneyType.DIAMOND, costDiamond)) {
            Logs.ARENA.debug("%s 群主钻石不足,无法加入", player);
            return ErrorCode.GROUP_CHIEF_LACK_DIAMOND;
        }
        if (!checkEnoughGold(fromClub, rule, player.getUid(), Boolean.TRUE)) {
            Logs.ARENA.warn("%s %s 竞技值不足,无法加入", this, player);
            return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
        }
        return ErrorCode.OK;
    }
    
    public boolean checkEnoughGold(IClub fromClub, HashMap<String, Integer> rule, long playerUid, boolean join) {
        if (!fromClub.getClubType().match(EClubType.GOLD)) {
            return true;
        }
        int temp = 0;
//        // 是否是体验场(1普通2体验)
//        if (rule.getOrDefault(RoomRule.RR_TIYAN, 1) == 2) {
//            temp = 1;
//        } else {
            temp = (join ? rule.getOrDefault(RoomRule.RR_MINGOLD, 0) : rule.getOrDefault(RoomRule.RR_LEAVEGOLD,0));
       // }

        boolean ret = fromClub.hasGold(playerUid, temp);
        if (!ret) {
            Logs.ARENA.warn("%s playerUid:%d 竞技值不足 只有:%d 需要:%d", this, playerUid, fromClub.getGold(playerUid), temp);
        }
        return ret;
    }

    /**
     * 亲友圈可少人模式-坐下
     * 
     * @param player
     * @param roomId
     * @param mainClub
     * @param fromClub
     * @param index
     * @return
     */
    public ErrorCode sitDown(Player player, int roomId, IClub mainClub, IClub fromClub, int index) {
        Room room = (Room) RoomManager.I.getRoom(roomId);
        if (null == room) {
            Logs.GROUP.warn("%s 包厢房间Uid:%d 不存在", player, roomId);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        IRoomHandle roomHandle = room.getRoomHandle();
        if (!(roomHandle instanceof IBoxRoomHandle)) {
            Logs.GROUP.warn("%s 包厢房间Uid:%d 不存在", player, roomId);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) room;
        long boxUid = boxRoomHandle.getBoxUid();
        Box box = mainClub.getBox(boxUid);
        if (null == box) {
            Logs.GROUP.warn("%s 包厢Uid:%d 不存在", player, boxUid);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        if (!isWatch(room.getGameType(), room.getRule())) {
            Logs.ARENA.warn("%s 包厢Uid:%d 不是可旁观, 无法坐下", player, boxUid);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        if (!checkEnoughGold(fromClub, box.getRule(), player.getUid(), Boolean.FALSE)) {
            Logs.ARENA.warn("%s %s 竞技值不足,无法加入", this, player);
            return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
        }
        ErrorCode err = box.sitDown(player, index);
        return err;
    }

    /**
     * 亲友圈可少人模式-站起
     *
     * @param player
     * @param roomId
     * @param club
     * @return
     */
    public ErrorCode sitUp(Player player, int roomId, IClub club) {
        Room room = RoomManager.I.getRoom(roomId);
        if (null == room) {
            Logs.GROUP.warn("%s 包厢房间Uid:%d 不存在", player, roomId);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        IRoomHandle roomHandle = room.getRoomHandle();
        if (!(roomHandle instanceof IBoxRoomHandle)) {
            Logs.GROUP.warn("%s 包厢房间Uid:%d 不存在", player, roomId);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) roomHandle;
        long boxUid = boxRoomHandle.getBoxUid();
        Box box = club.getBox(boxUid);
        if (null == box) {
            Logs.GROUP.warn("%s 包厢Uid:%d 不存在", player, boxUid);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        if (!isWatch(room.getGameType(), room.getRule())) {
            Logs.ARENA.warn("%s 包厢Uid:%d 不是可旁观, 无法坐下", player, boxUid);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        if (BoxManager.I.checkEnoughGold(club, room.getRule(), player.getUid(), false) && room.getCurBureau() > 0 && boxRoomHandle.hasPlayed(player.getUid())) {
            return null;
        }
        ErrorCode err = box.sitUp(player);
        return err;
    }

    /**
     * 包厢准备
     * 
     * @param player
     * @return
     */
    public ErrorCode ready(Player player, IBoxRoomHandle boxRoomHandle) {
        IRoom room = boxRoomHandle.getRoom();
        if (null == room) {
            return null;
        }
        IClub club = ClubManager.I.getClubByUid(room.getGroupUid());
        if (null == club) {
            Logs.GROUP.warn("%s 群:%d不存在, 无法坐下", player, room.getGroupUid());
            player.send(CommandId.CLI_NTF_ROOM_READY_FAIL, ErrorCode.GROUP_NOT_EXISTS);
            return null;
        }
        // 是否打烊
        if (club.matchCloseStatus(EClubCloseStatus.CLOSING) || club.matchCloseStatus(EClubCloseStatus.CLOSED)) {
            Logs.GROUP.warn("%s groupUid:%d 已打烊，不能玩", player, room.getGroupUid());
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_ROOM_READY_FAIL, ErrorCode.CLUB_CLOSE_STATUS_LIMIT);
            }else{
                player.send(CommandId.CLI_NTF_ROOM_READY_FAIL, ErrorCode.CLUB_CLOSE_STATUS_LIMIT_ROOM_CARD);
            }
            return null;
        }
        Long boxUid = boxRoomHandle.getBoxUid();
        Box box = getBox(boxUid);
        if (null == box) {
            room.destroy();
            Logs.GROUP.warn("%s 包厢Uid:%d 不存在", player, boxUid);
            return ErrorCode.GROUP_BOX_NOT_EXISTS;
        }
        IClub fromClub = ClubManager.I.getClubByUid(club.getEnterFromClubUid(player.getUid()));
        if (!checkEnoughGold(fromClub, box.getRule(), player.getUid(), Boolean.FALSE)) {
            Logs.ARENA.warn("%s %s 竞技值不足,无法加入", this, player);
            player.send(CommandId.CLI_NTF_ROOM_READY_FAIL, ErrorCode.ARENA_LESS_THAN_MIN_VALUE);
            return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
        }
        ErrorCode err = box.ready(player);
        return err;
    }

    public Box getBox(long uid) {
        return allBox.get(uid);
    }

    public Box getBox(long clubUid, long boxUid) {
        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (null == club) {
            return null;
        }
        return club.getBox(boxUid);
    }

    private void addBox(long uid, Box box) {
        this.allBox.put(uid, box);
        box.setDirty(true);
        box.save();
    }

    private void remove(long uid) {
        Box box = this.allBox.remove(uid);
        if (null != box) {
            DBManager.I.save(() -> {
                DBManager.I.getBoxDAO().delete(uid);
            });
        }
    }

    public void loadAll() {
        List<Box> all = DBManager.I.getBoxDAO().loadAll();
        if (all.size() > 0) {
            for (Box box : all) {
                IClub club = ClubManager.I.getClubByUid(box.getOwnerUid());
                if (null == club) {
                    continue;
                }
                Floor floor = FloorManager.I.getFloor(box.getFloorUid());
                if (null == floor) {
                    continue;
                }
                // 入到缓存容器
                this.allBox.put(box.getUid(), box);
                // 关联楼层
                floor.init(box.getUid());
                // 关联亲友圈
                club.addBox(box);
                box.init();

                // 判断是否是百人场
                if (EBoxType.HUNDRED.match(box.getBoxType())) {
                    // 百人场创建玩法桌子box时，同时创建游戏桌子room
                    Player player = PlayerManager.I.getPlayer(club.getOwnerId());
                    onCreateHundredBox(player, box);
                }
            }
        }
    }

    public int getBoxCnt() {
        return allBox.size();
    }
    
}
