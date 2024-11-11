package com.xiuxiu.app.server.club.activity;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.*;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.*;
import com.xiuxiu.app.server.club.activity.divide.ClubActivityDivideData;
import com.xiuxiu.app.server.club.activity.divide.ClubActivityDivideDataItem;
import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldData;
import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldDataItem;
import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldRewardRecord;
import com.xiuxiu.app.server.club.constant.EClubActivityType;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.BitOperationUtil;
import com.xiuxiu.core.utils.TimeUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 亲友圈活动
 * 
 * @author Administrator
 *
 */
public class ClubActivityManager {

    private static class ClubActivityManagerHolder {
        private static ClubActivityManager instance = new ClubActivityManager();
    }

    public static ClubActivityManager I = ClubActivityManagerHolder.instance;

    /**
     * 所有类型的活动数据，格式：map<亲友圈id,map<活动类型,活动实体>>
     */
    private Map<Long, Map<EClubActivityType, ClubActivity>> activitys = new ConcurrentHashMap<>();

    /**
     * 初始化
     */
    public void init() {
        Set<Long> ids = ClubManager.I.getAllClubIds();
        if (null == ids) {
            return;
        }
        for (Long clubUid : ids) {
            this.activitys.put(clubUid, new ConcurrentHashMap<>());
        }
        loadAll();

        // 初始化亲友圈昨日奖励分相关数据
        initRewardValue();
    }

    /**
     * 加载所有活动
     */
    private void loadAll() {
        List<ClubActivity> tempList = DBManager.I.getClubActivityDAO().loadAll();
        if (null == tempList || tempList.size() == 0) {
            return;
        }
        for (ClubActivity clubActivity : tempList) {
            EClubActivityType tempType = EClubActivityType.getType(clubActivity.getType());
            if (null == tempType) {
                continue;
            }
            clubActivity.setInfo(clubActivity.getInfo());
            clubActivity.init();
            Map<EClubActivityType, ClubActivity> tempMap = null;
            if (this.activitys.containsKey(clubActivity.getClubId())) {
                tempMap = this.activitys.get(clubActivity.getClubId());
            } else {
                tempMap = new ConcurrentHashMap<>();
                this.activitys.put(clubActivity.getClubId(), tempMap);
            }
            if (tempMap.containsKey(tempType)) {
                continue;
            }
            tempMap.put(tempType, clubActivity);
        }
    }

    public ClubActivity getActivity(long clubUid, EClubActivityType type) {
        if (this.activitys.containsKey(clubUid)) {
            return this.activitys.get(clubUid).get(type);
        }
        return null;
    }

    public ClubActivity getAndSetActivity(long clubUid, EClubActivityType type) {
        ClubActivity data = null;
        if (this.activitys.containsKey(clubUid)) {
            data =  this.activitys.get(clubUid).get(type);
        }
        if (null == data) {
            switch (type) {
            case GOLD:
                ClubActivity clubActivityGold = new ClubActivity();
                clubActivityGold.setUid(UIDManager.I.getAndInc(UIDType.CLUB_ACTIVITY));
                clubActivityGold.setClubId(clubUid);
                clubActivityGold.setType(EClubActivityType.GOLD.getType());
                clubActivityGold.setDirty(Boolean.TRUE);
                clubActivityGold.save();
                addActivity(clubUid, clubActivityGold);
                data = clubActivityGold;
                break;
            case DIVIDE:
                ClubActivity activity = new ClubActivity();
                activity.setUid(UIDManager.I.getAndInc(UIDType.CLUB_ACTIVITY));
                activity.setClubId(clubUid);
                activity.setType(EClubActivityType.DIVIDE.getType());
                activity.setDirty(Boolean.TRUE);
                activity.save();
                addActivity(clubUid, activity);
                data =  activity;
                break;
            default:
                break;
            }
        }
        return data;
    }

    /**
     * 当创建亲友圈时处理
     * 
     * @param clubUid
     */
    public void init(long clubUid) {
        try {
            for (EClubActivityType type : EClubActivityType.values()) {
                switch (type) {
                case GOLD:
                    ClubActivity clubActivityGold = new ClubActivity();
                    clubActivityGold.setUid(UIDManager.I.getAndInc(UIDType.CLUB_ACTIVITY));
                    clubActivityGold.setClubId(clubUid);
                    clubActivityGold.setType(EClubActivityType.GOLD.getType());
                    clubActivityGold.setDirty(Boolean.TRUE);
                    clubActivityGold.save();
                    addActivity(clubUid, clubActivityGold);
                    break;
                case DIVIDE:
                    ClubActivity activity = new ClubActivity();
                    activity.setUid(UIDManager.I.getAndInc(UIDType.CLUB_ACTIVITY));
                    activity.setClubId(clubUid);
                    activity.setType(EClubActivityType.DIVIDE.getType());
                    activity.setDirty(Boolean.TRUE);
                    activity.save();
                    addActivity(clubUid, activity);
                    break;
                default:
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addActivity(long clubUid, ClubActivity clubActivity) {
        Map<EClubActivityType, ClubActivity> tempMap = null;
        if (this.activitys.containsKey(clubUid)) {
            tempMap = this.activitys.get(clubUid);
        } else {
            tempMap = new ConcurrentHashMap<>();
            this.activitys.put(clubUid, tempMap);
        }
        tempMap.put(EClubActivityType.getType(clubActivity.getType()), clubActivity);
    }

    /**
     * 检查是否到期(领取金币活动)
     * 
     * @param now
     */
    public void checkExpire(long now) {
        try {
            Set<Long> clubUids = ClubManager.I.getAllClubIds();
            if (null == clubUids || clubUids.size() == 0) {
                return;
            }
            for (Long clubUid : clubUids) {
                checkExpire(now, clubUid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkExpire(long now, long clubUid) {
        ClubActivity tempActivity = getActivity(clubUid, EClubActivityType.GOLD);
        if (null == tempActivity) {
            return;
        }
        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (null == club) {
            return;
        }
        if (club.getClubInfo().getState() != 0) {
            return;
        }
        boolean isSave = Boolean.FALSE;
        Map<Long, ClubActivityGoldData> tempMap = tempActivity.getGoldData();
        for (Map.Entry<Long, ClubActivityGoldData> items : tempMap.entrySet()) {
            ClubActivityGoldData data = items.getValue();
            if (data.getPeriod() > 0) {
                long endTime = data.getStartTime() + (data.getPeriod() * TimeUtil.ONE_DAY_MS);
                if (endTime <= now) {
                    data.setStartTime(now);
                    // 清除玩家任务
                    final long boxId = items.getKey();
                    club.foreach(new ICallback<ClubMember>() {
                        @Override
                        public void call(ClubMember... member) {
                            synchronized (member[0]) {
                                member[0].getGoldActivityCount().remove(boxId);
                            }
                            member[0].setDirty(Boolean.TRUE);

                            ClubMemberExt clubMemberExt = club.getMemberExt(member[0].getPlayerUid(), Boolean.TRUE);
                            synchronized (clubMemberExt) {
                                Map<Long, Integer> goldActivityStatus = clubMemberExt.getGoldActivityStatus();
                                goldActivityStatus.remove(boxId);
                            }
                            clubMemberExt.setDirty(Boolean.TRUE);
                        }
                    });
                    isSave = isSave || Boolean.TRUE;
                }
            }
        }
        if (isSave) {
            tempActivity.setDirty(Boolean.TRUE);
            tempActivity.save();
        }
    }

    /**
     * 亲友圈奖励分值（昨日）
     */
    private Map<Long, Long> clubRewardValue = new HashMap<>();

    /**
     * 0点刷新
     * 
     * @param now
     */
    public void zero(long now) {
        try {
            long begin = System.currentTimeMillis();
            clearRewardValue();
            initRewardValue();
            long cost = System.currentTimeMillis() - begin;
            Logs.CLUB.debug("亲友圈奖励分重置耗时:%d ms", cost);
        } catch (Exception e) {
            Logs.CLUB.error("亲友圈奖励分重置异常");
        }
    }

    private void initRewardValue() {
        // 查亲友圈昨日奖励分相关数据
        List<ClubRewardValueRecord> records = DBManager.I.getClubRewardValueRecordDAO()
                .loadDayDetails(TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS);
        Iterator<ClubRewardValueRecord> iter = records.iterator();
        while (iter.hasNext()) {
            ClubRewardValueRecord tempRecord = iter.next();
            addRewardValue(tempRecord.getClubUid(), tempRecord.getInMoney());
        }
    }

    private long getRewardValue(Long clubUid) {
        if (clubRewardValue.containsKey(clubUid)) {
            return clubRewardValue.get(clubUid);
        }
        return 0L;
    }

    public void addRewardValue(Long groupUid, Long rewardValue) {
        this.clubRewardValue.put(groupUid, rewardValue);
    }

    private void clearRewardValue() {
        this.clubRewardValue.clear();
    }

    public boolean isOpen(long clubUid) {
        ClubActivity activity = ClubActivityManager.I.getActivity(clubUid, EClubActivityType.DIVIDE);
        if (null == activity) {
            return Boolean.FALSE;
        }
        ClubActivityDivideData data = activity.getDivideData();
        return data.isOpen();
    }

    /**
     * 根据每天奖励值获取分成比例-成员(活动)
     * 
     * @param clubUid
     * @return
     */
    public int getDivide(long clubUid) {
        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (null == club) {
            return 0;
        }
        ClubActivity activity = ClubActivityManager.I.getAndSetActivity(clubUid, EClubActivityType.DIVIDE);
        if (null == activity) {
            return 0;
        }
        ClubActivityDivideData data = activity.getDivideData();
        if (!data.isOpen()) {
            return 0;
        }
        ClubActivityDivideDataItem dataItem = data.getBase();
        if (dataItem != null) {
           return dataItem.getMember();
        }
        return 0;
    }
    
    /**
     * 根据每天奖励值获取分成比例-成员
     * 
     * @param clubUid
     * @param playerUid
     * @return
     */
    public int getAndSetDivide(long clubUid, long playerUid) {
        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (null == club) {
            return 0;
        }
        ClubMember member = club.getMember(playerUid);
        if (null == member) {
            return 0;
        }
        ClubActivity activity = ClubActivityManager.I.getActivity(clubUid, EClubActivityType.DIVIDE);
        if (null == activity) {
            return member.getDivide();
        }
        ClubActivityDivideData data = activity.getDivideData();
        if (!data.isOpen()) {
            return member.getDivide();
        }
        if (member.getDivideTime() > 0 && TimeUtil.isOnDay(new Date(System.currentTimeMillis()), new Date(member.getDivideTime()))) {
            return member.getDivide();
        }
        ClubActivityDivideDataItem dataItem = data.getByRewardValue(getRewardValue(clubUid));
        if (dataItem != null) {
            if (dataItem.getMember() > member.getDivide()) {
                synchronized (member) {
                    if (dataItem.getMember() > member.getDivide()) {
                        member.setDivide(dataItem.getMember());
                        member.setDirty(Boolean.TRUE);
                    }
                }
            }
        }
        return member.getDivide();
    }

    /**
     * 根据每天奖励值获取分成比例-一条线
     * 
     * @param clubUid
     * @param playerUid
     * @return
     */
    public int getAndSetArenaDivideLine(long clubUid, long playerUid) {
        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (null == club) {
            return 0;
        }
        ClubMember member = club.getMember(playerUid);
        if (null == member) {
            return 0;
        }
        ClubActivity activity = ClubActivityManager.I.getActivity(clubUid, EClubActivityType.DIVIDE);
        if (null == activity) {
            return member.getDivideLine();
        }
        ClubActivityDivideData data = activity.getDivideData();
        if (!data.isOpen()) {
            return member.getDivideLine();
        }
        if (member.getDivideLineTime() > 0 && TimeUtil.isOnDay(new Date(System.currentTimeMillis()), new Date(member.getDivideLineTime()))) {
            return member.getDivideLine();
        }
        ClubActivityDivideDataItem dataItem = data.getByRewardValue(getRewardValue(clubUid));
        if (dataItem != null) {
            if (dataItem.getLine() > member.getDivideLine()) {
                synchronized (member) {
                    if (dataItem.getLine() > member.getDivideLine()) {
                        member.setDivideLine(dataItem.getLine());
                        member.setDirty(Boolean.TRUE);
                    }
                }
            }
        }
        return member.getDivideLine();
    }
    
    public int getDivideLine(long clubUid) {
        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (null == club) {
            return 0;
        }
        ClubActivity activity = ClubActivityManager.I.getActivity(clubUid, EClubActivityType.DIVIDE);
        if (null == activity) {
            return 0;
        }
        ClubActivityDivideData data = activity.getDivideData();
        if (!data.isOpen()) {
            return 0;
        }
        ClubActivityDivideDataItem dataItem = data.getBase();
        if (dataItem != null) {
            return dataItem.getLine();
        }
        return 0;
    }

    /**
     * 修改奖励分分成比例活动
     * 
     * @param clubUid
     * @param base
     * @param items
     * @return
     */
    public ErrorCode changeByClub(long clubUid, PCLIClubNtfActivityDivideInfoItem base,
            List<PCLIClubNtfActivityDivideInfoItem> items,boolean open) {
        ClubActivity activity = ClubActivityManager.I.getAndSetActivity(clubUid, EClubActivityType.DIVIDE);
        ClubActivityDivideData data = activity.getDivideData();
        data.setBase(ClubActivityDivideDataItem.valueOf(base));

        List<ClubActivityDivideDataItem> tempList = new ArrayList<>();
        for (PCLIClubNtfActivityDivideInfoItem tempItem : items) {
            tempList.add(ClubActivityDivideDataItem.valueOf(tempItem));
        }
        data.setItems(tempList);
        data.setOpen(open);
        activity.setDirty(Boolean.TRUE);
        activity.save();
        IClub club = ClubManager.I.getClubByUid(clubUid);
        PCLIClubNtfActivityDivideOpenNotice notice= new PCLIClubNtfActivityDivideOpenNotice();
        notice.open=open;
        notice.clubUid=clubUid;
        club.broadcast(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_OPEN,notice);
        return ErrorCode.OK;
    }

    /**
     * 领取金币活动
     * 
     * @param club
     * @param member
     * @return
     */
    public PCLIClubNtfActivityGoldInfo getActivityGoldInfo(IClub club, ClubMember member) {
        PCLIClubNtfActivityGoldInfo resp = new PCLIClubNtfActivityGoldInfo();
        ClubActivity activity = getActivity(club.getClubUid(), EClubActivityType.GOLD);
        if (activity != null) {
            Map<Long, ClubActivityGoldData> data = activity.getGoldData();
            if (data != null) {

                Map<Long, Integer> goldActivityCount = member.getGoldActivityCount();
                ClubMemberExt clubMemberExt = club.getMemberExt(member.getPlayerUid(), Boolean.FALSE);
                Map<Long, Integer> goldActivityStatus = null == clubMemberExt ? null
                        : clubMemberExt.getGoldActivityStatus();
                for (Map.Entry<Long, ClubActivityGoldData> entry : data.entrySet()) {
                    Box box = BoxManager.I.getBox(entry.getKey());
                    if (box == null) {
                       this.removeActivityGold(club,entry.getKey());
                       continue;
                    }
                    ClubActivityGoldData tempData = entry.getValue();
                    List<PCLIClubActivityGold> list = new ArrayList<>();
                    PCLIClubNtfActivityGoldInfo.ClubActivityGoldInfo info = new PCLIClubNtfActivityGoldInfo.ClubActivityGoldInfo();

                    Integer value = goldActivityCount.get(entry.getKey());
                    value = null == value ? 0 : value;
                    Integer oldStatus = null == goldActivityStatus ? 0 : goldActivityStatus.get(entry.getKey());
                    oldStatus = null == oldStatus ? 0 : oldStatus;

                    for (ClubActivityGoldDataItem item : tempData.getItems()) {
                        PCLIClubActivityGold inf = new PCLIClubActivityGold();
                        inf.index = item.getIndex();
                        inf.isReward = !BitOperationUtil.calState(oldStatus, inf.index);
                        inf.curCount = value == null ? 0 : value;
                        inf.param = item.getConditionParam();
                        inf.reward = item.getRewardValue();
                        list.add(inf);
                    }

                    info.items.addAll(list);
                    info.period = tempData.getPeriod();
                    info.startTime = tempData.getStartTime();
                    info.gameType = box.getGameType();
                    info.gameSubType = box.getGameSubType();
                    resp.data.put(entry.getKey(), info);
                }
            }
        }
        return resp;
    }

    /**
     * 删除某包厢的金币活动
     * 
     * @param club
     * @param boxUid
     * @return
     */
    public boolean removeActivityGold(IClub club, long boxUid) {
        ClubActivity activity = getActivity(club.getClubUid(), EClubActivityType.GOLD);
        if (activity != null) {
            Map<Long, ClubActivityGoldData> tempMap = activity.getGoldData();
            ClubActivityGoldData data = tempMap.remove(boxUid);
            if (data != null) {
                // 清除成员的任务和领奖列表
                club.foreach((member) -> {
                    synchronized (member[0]) {
                        member[0].getGoldActivityCount().remove(boxUid);
                    }
                    member[0].setDirty(Boolean.TRUE);

                    ClubMemberExt clubMemberExt = club.getMemberExt(member[0].getPlayerUid(), Boolean.TRUE);
                    synchronized (clubMemberExt) {
                        Map<Long, Integer> goldActivityStatus = clubMemberExt.getGoldActivityStatus();
                        goldActivityStatus.remove(boxUid);
                    }
                    clubMemberExt.setDirty(Boolean.TRUE);
                });
                return true;
            }
        }
        return false;
    }

    /**
     * 领取某包厢的金币活动档位奖励
     * 
     * @param player
     * @param club
     * @param boxUid
     * @param index
     * @return
     */
    public ErrorCode rewardActivityGold(Player player, IClub club, long boxUid, int index) {
        ClubMember clubMember = club.getMember(player.getUid());
        Map<Long, Integer> goldActivityCount = clubMember.getGoldActivityCount();
        Integer count = goldActivityCount.get(boxUid);
        if (null == count) {
            return ErrorCode.GROUP_QUEST_NOT_FIND;
        }
        ClubMemberExt clubMemberExt = club.getMemberExt(player.getUid(), Boolean.TRUE);
        Map<Long, Integer> goldActivityStatus = clubMemberExt.getGoldActivityStatus();
        Integer oldStatus = goldActivityStatus.get(boxUid);
        if (oldStatus != null && !BitOperationUtil.calState(oldStatus, index)) {
            Logs.PLAYER.warn("%s 重复领奖arenaId:%d,questIndex:%d", boxUid, index);
            return ErrorCode.GROUP_QUEST_REWARD_REPEAT;
        }
        ClubActivity activity = getActivity(club.getClubUid(), EClubActivityType.GOLD);
        if (null == activity) {
            return ErrorCode.GROUP_QUEST_NOT_FIND;
        }
        Map<Long, ClubActivityGoldData> tempMap = activity.getGoldData();
        ClubActivityGoldData tempData = tempMap.get(boxUid);
        List<ClubActivityGoldDataItem> items = tempData.getItems();
        if (items == null || items.size() == 0) {
            Logs.PLAYER.warn("%s 传入竞技场ID已经失效", boxUid);
            return ErrorCode.GROUP_QUEST_NOT_FIND;
        }
        ClubActivityGoldDataItem item = items.stream().filter(x -> x.getIndex() == index).findFirst().orElse(null);
        if (item == null) {
            Logs.PLAYER.warn("%s 任务列表没有任务boxUid:%d,index:%d", boxUid, index);
            return ErrorCode.GROUP_QUEST_NOT_FIND;
        }
        // 任务完成
        if (count >= item.getConditionParam() && item.getConditionParam() != 0) {
            int costValue = item.getRewardValue() * 100;
            if (!club.addMemberClubGold(club.getOwnerId(), -costValue, player.getUid(),
                    EClubGoldChangeType.EXCHANGE_REWARD_VALUE_DEC)) {
                return ErrorCode.GROUP_LEADER_ARENA_VALUE_NOT_ENOUGH;
            }

            synchronized (clubMemberExt) {
                // 更新玩家领取状态
                int newStatus = BitOperationUtil.chanageState(oldStatus == null ? 0 : oldStatus, index);
                goldActivityStatus.put(boxUid, newStatus);
            }
            clubMemberExt.setDirty(Boolean.TRUE);
            clubMemberExt.save();
            club.addMemberClubGold(player.getUid(), costValue, club.getOwnerId(),
                    EClubGoldChangeType.EXCHANGE_REWARD_VALUE_INC);
            Box box = BoxManager.I.getBox(boxUid);
            if (null != box) {
                ClubActivityGoldRewardRecord record = new ClubActivityGoldRewardRecord();
                record.setUid(UIDManager.I.getAndInc(UIDType.CLUB_ACTIVITY_GOLD_REWARD_RECORD));
                record.setClubUid(club.getClubUid());
                record.setPlayerUid(player.getUid());
                record.setBoxUid(boxUid);
                record.setGold(costValue);
                record.setOperatorTime(System.currentTimeMillis());
                record.setGameType(box.getGameType());
                record.setStartTime(tempData.getStartTime());
                record.setEndTime(tempData.getStartTime() + tempData.getPeriod() * TimeUtil.ONE_DAY_MS);
                record.setPeriod(tempData.getPeriod());
                record.setSubType(box.getGameSubType());
                record.setBureau(count);
                record.setParam(item.getConditionParam());
                record.setDirty(true);
                if (!record.save()) {
                    Logs.GROUP.warn("%s 保存认为领奖记录数据库失败", record);
                }
            }
        } else {
            return ErrorCode.GROUP_QUEST_GET_REWARD_NOT_CONDITION;
        }
        return ErrorCode.OK;

    }

    // 修改群组任务
    public ErrorCode modifyActivityGold(IClub club, PCLIClubReqActivityGoldModify info) {
        ClubActivity activity = getAndSetActivity(club.getClubUid(), EClubActivityType.GOLD);
        Map<Long, ClubActivityGoldData> data = activity.getGoldData();
        // 判断是否超过上限最多只能5个
        if (data.size() > Constant.GROUP_ARENA_LIMIT && !data.containsKey(info.boxUid)
                || info.items.size() > Constant.GROUP_QUEST_LIMIT) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        return modifyQuest(club, activity, info.boxUid, info.reset, info.period, info.items);
    }

    private ErrorCode modifyQuest(IClub club, ClubActivity activity, long boxUid, boolean reset, int period,
            List<PCLIClubActivityGold> tempList) {
        long startTime = 0;
        ClubActivityGoldData tempData = null;
        synchronized (activity) {
            Map<Long, ClubActivityGoldData> dataMap = activity.getGoldData();
            if (dataMap.size() > Constant.GROUP_QUEST_LIMIT && !dataMap.containsKey(boxUid)) {
                return ErrorCode.REQUEST_INVALID_DATA;
            }

            if (reset && period > 0) {
                startTime = System.currentTimeMillis();
            }
            if (dataMap.containsKey(boxUid)) {
                tempData = dataMap.get(boxUid);
            } else {
                tempData = new ClubActivityGoldData();
            }
            dataMap.put(boxUid, tempData);
        }
        List<ClubActivityGoldDataItem> list = tempData.getItems();
        if (list == null) {
            list = new ArrayList<>();
            tempData.setItems(list);
        }
        list.clear();
        for (PCLIClubActivityGold item : tempList) {
            ClubActivityGoldDataItem questItem = list.stream().filter(x -> x.getIndex() == item.index).findFirst()
                    .orElse(null);
            if (questItem == null) {
                questItem = new ClubActivityGoldDataItem();
            }
            questItem.setConditionParam(item.param);
            questItem.setIndex(item.index);
            questItem.setRewardValue(item.reward);

            list.add(questItem);
        }

        if (reset && period > 0) {
            tempData.setPeriod(period);
            tempData.setStartTime(startTime);
            // 清除成员的任务和领奖列表
            club.foreach((member) -> {
                synchronized (member[0]) {
                    member[0].getGoldActivityCount().remove(boxUid);
                }
                member[0].setDirty(Boolean.TRUE);

                ClubMemberExt clubMemberExt = club.getMemberExt(member[0].getPlayerUid(), Boolean.TRUE);
                synchronized (clubMemberExt) {
                    Map<Long, Integer> goldActivityStatus = clubMemberExt.getGoldActivityStatus();
                    goldActivityStatus.remove(boxUid);
                }
                clubMemberExt.setDirty(Boolean.TRUE);
            });
        }
        activity.setDirty(Boolean.TRUE);
        activity.save();

        return ErrorCode.OK;
    }

}
