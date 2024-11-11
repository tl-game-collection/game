package com.xiuxiu.app.server.club;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.club.constant.EOpStateType;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClubInfo extends BaseTable {
    private String name;
    private String desc;
    private String icon;
    private int clubType;
    private long ownerId;
    private long createTime;
    private String gameDesc;
    private int state; // 状态 0: 正常, 1: 删除,
    /** 打烊状态,0未打烊1打烊中2已打烊 */
    private AtomicInteger closeStatus = new AtomicInteger(0);
    private AtomicLong lockTime = new AtomicLong(0); // 提交合并申请后锁定群部分功能
    private AtomicLong lockByClubUid = new AtomicLong(-1L); // 最后提交合并申请的圈uid

    /**
     * 父club uid
     */
    private AtomicLong parentUid = new AtomicLong(-1L);
    /**
     * 加入parentUid圈的时间
     */
    private long joinParentTime = 0;
    /**
     * 所有直属子club uid列表
     */
    private ConcurrentHashSet<Long> childUidSet = new ConcurrentHashSet<>();

    private LinkedList<ApplyInfo> memberApplyList = new LinkedList<>();
    /**
     * 申请合并列表
     */
    private LinkedList<ApplyInfo> mergeApplyList = new LinkedList<>();
    /**
     * 申请退主圈列表
     */
    private LinkedList<ApplyInfo> leaveApplyList = new LinkedList<>();
    /**
     * 管理员(合圈后)
     */
    private ConcurrentHashMap<Long, List<Long>> managerInfo = new ConcurrentHashMap<>();

    /**
     * 上分财务
     */
    private ConcurrentHashSet<Long> upGoldTreasurer = new ConcurrentHashSet<>();

    /**
     * 下分财务
     */
    private ConcurrentHashSet<Long> downGoldTreasurer = new ConcurrentHashSet<>();

    /**
     * 财务系统参数
     *
     */
    private ConcurrentHashMap<Integer,String> treasurerInfo = new ConcurrentHashMap<>();

    private String announcement = ""; // 公告

    private long announcementExpireAt; // 公告过期时间戳，毫秒单位
    private int serviceChargeDivide; // 竞技场里面管理费比例

    /**
     * 圈等级管理费
     * key:clubUid value:chargeDivide(服务费比例)
     */
    private ConcurrentHashMap<Long, Integer> serviceCharge = new ConcurrentHashMap<>();

    /**
     * 圈房卡兑换竞技分总值
     */
    private long dToGoldTotal;


    public ClubInfo() {
        this.setTableType(ETableType.TB_CLUB_INFO);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getClubType() {
        return clubType;
    }

    public String getGameDesc() {
        return gameDesc;
    }

    public void setGameDesc(String gameDesc) {
        this.gameDesc = gameDesc;
    }

    public void setClubType(int clubType) {
        this.clubType = clubType;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public long getAnnouncementExpireAt() {
        return announcementExpireAt;
    }

    public void setAnnouncementExpireAt(long announcementExpireAt) {
        this.announcementExpireAt = announcementExpireAt;
    }

    public int getServiceChargeDivide() {
        return serviceChargeDivide;
    }

    public void setServiceChargeDivide(int serviceChargeDivide) {
        this.serviceChargeDivide = serviceChargeDivide;
    }

    public List<ApplyInfo> getmemberApplyList() {
        return this.memberApplyList;
    }

    public String getMemberApplyListDb() {
        return JsonUtil.toJson(this.memberApplyList);
    }

    public void setMemberApplyListDb(String memberApplyList) {
        if (StringUtil.isEmptyOrNull(memberApplyList)) {
            return;
        }
        LinkedList<ApplyInfo> temp = JsonUtil.fromJson(memberApplyList, new TypeReference<LinkedList<ApplyInfo>>() {
        });
        if (null != temp) {
            this.memberApplyList = temp;
        }
    }

    public List<ApplyInfo> getApplyInfo() {
        synchronized (this.memberApplyList) {
            return this.memberApplyList;
        }
    }

    public void addApply(ApplyInfo info) {
        synchronized (this.memberApplyList) {
            this.memberApplyList.addFirst(info);
        }
    }

    public void removeApply() {
        synchronized (this.memberApplyList) {
            this.memberApplyList.removeLast();
        }
    }

    public long getParentUid() {
        return this.parentUid.get();
    }

    public void setParentUid(long parentUid) {
        this.parentUid.set(parentUid);
    }

    public long getJoinParentTime() {
        return joinParentTime;
    }

    public void setJoinParentTime(long joinParentTime) {
        this.joinParentTime = joinParentTime;
    }

    public ConcurrentHashSet<Long> getChildUid() {
        return childUidSet;
    }

    public String getChildUidSetDb() {
        return JsonUtil.toJson(this.childUidSet);
    }

    public void setChildUidSetDb(String childUidSet) {
        this.childUidSet = JsonUtil.fromJson(childUidSet, new TypeReference<ConcurrentHashSet<Long>>() {
        });
    }

    public List<ApplyInfo> getMergeApplyList() {
        return this.mergeApplyList;
    }

    public String getMergeApplyListDb() {
        return JsonUtil.toJson(this.mergeApplyList);
    }

    public void setMergeApplyListDb(String mergeApplyListStr) {
        if (StringUtil.isEmptyOrNull(mergeApplyListStr)) {
            return;
        }
        LinkedList<ApplyInfo> temp = JsonUtil.fromJson(mergeApplyListStr, new TypeReference<LinkedList<ApplyInfo>>() {
        });
        if (null != temp) {
            this.mergeApplyList = temp;
        }
    }

    public void addApplyMergeList(ApplyInfo info) {
        synchronized (this.mergeApplyList) {
            if (this.mergeApplyList.size() >= (Constant.CLUB_MAX_APPLY_MERGE_CNT + Constant.CLUB_APPLY_MERGE_EXT_CNT)) {
                int removeCnt = (this.mergeApplyList.size() - Constant.CLUB_MAX_APPLY_MERGE_CNT) + 1;
                for (int i = 0; i < removeCnt; i++) {
                    this.mergeApplyList.removeLast();
                }
            }
            this.mergeApplyList.add(info);
        }
    }

    public ApplyInfo getApplyMergeInfo(long mergeClubUid, long now, int applyState){
        synchronized (this.mergeApplyList) {
            for (ApplyInfo info : this.mergeApplyList) {
                if (info.getfUid() == mergeClubUid && applyState == info.getState()) {
                    if (applyState == EOpStateType.NORMAL.ordinal()
                            && (info.getaTime() + Constant.CLUB_APPLY_MERGE_INVALID_TIME) < now) {
                        return null;
                    }
                    return info;
                }
            }
            return null;
        }
    }

    public List<ApplyInfo> getLeaveApplyList() {
        return this.leaveApplyList;
    }

    public String getLeaveApplyListDb() {
        return JsonUtil.toJson(this.leaveApplyList);
    }

    public void setLeaveApplyListDb(String leaveApplyListStr) {
        if (StringUtil.isEmptyOrNull(leaveApplyListStr)) {
            return;
        }
        LinkedList<ApplyInfo> temp = JsonUtil.fromJson(leaveApplyListStr, new TypeReference<LinkedList<ApplyInfo>>() {
        });
        if (null != temp) {
            this.leaveApplyList = temp;
        }
    }

    public void addApplyLeaveList(ApplyInfo info) {
        synchronized (this.leaveApplyList) {
            if (this.leaveApplyList.size() >= (Constant.CLUB_MAX_APPLY_MERGE_CNT + Constant.CLUB_APPLY_MERGE_EXT_CNT)) {
                int removeCnt = (this.leaveApplyList.size() - Constant.CLUB_MAX_APPLY_MERGE_CNT) + 1;
                for (int i = 0; i < removeCnt; i++) {
                    this.leaveApplyList.removeLast();
                }
            }
            this.leaveApplyList.add(info);
        }
    }

    public ApplyInfo getApplyLeaveInfo(long leaveClubUid,int applyState){
        synchronized (this.leaveApplyList) {
            for (ApplyInfo info : this.leaveApplyList){
                if (info.getfUid() == leaveClubUid && applyState == info.getState()){
                    return info;
                }
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getManagerInfoDb() {
        return JsonUtil.toJson(this.managerInfo);
    }

    public void setManagerInfoDb(String managerInfo) {
        if (StringUtil.isEmptyOrNull(managerInfo)){
            return;
        }
        this.managerInfo = JsonUtil.fromJson(managerInfo, new TypeReference<ConcurrentHashMap<Long, List<Long>>>() {
        });
    }

    public Map<Long, List<Long>> getManagerInfo() {
        return managerInfo;
    }

    public String getUpGoldTreasurerDb() {
        return JsonUtil.toJson(this.upGoldTreasurer);
    }

    public void setUpGoldTreasurerDb(String upGoldTreasurer) {
        if (StringUtil.isEmptyOrNull(upGoldTreasurer)){
            return;
        }
        this.upGoldTreasurer = JsonUtil.fromJson(upGoldTreasurer, new TypeReference<ConcurrentHashSet<Long>>() {
        });
    }

    public ConcurrentHashSet<Long> getUpGoldTreasurer() {
        return upGoldTreasurer;
    }

    public String getDownGoldTreasurerDb() {
        return JsonUtil.toJson(this.downGoldTreasurer);
    }

    public void setDownGoldTreasurerDb(String downGoldTreasurer) {
        if (StringUtil.isEmptyOrNull(downGoldTreasurer)){
            return;
        }
        this.downGoldTreasurer = JsonUtil.fromJson(downGoldTreasurer, new TypeReference<ConcurrentHashSet<Long>>() {
        });
    }

    public ConcurrentHashSet<Long> getDownGoldTreasurer() {
        return downGoldTreasurer;
    }

    public String getTreasurerInfoDb() {
        return JsonUtil.toJson(this.treasurerInfo);
    }

    public void setTreasurerInfoDb(String treasurerInfo) {
        if (!StringUtil.isEmptyOrNull(treasurerInfo)){
            this.treasurerInfo = JsonUtil.fromJson(treasurerInfo, new TypeReference<ConcurrentHashMap<Integer,String>>() {
            });
        }
    }

    /**
     * 下分订单首笔是否免费
     * @return
     */
    public boolean checkTreasurerFirstFree(){
        return this.treasurerInfo.getOrDefault(1,"0").equals("1");
    }

    public void setTreasurerFirstFree(boolean isFree){
        this.treasurerInfo.put(1,isFree ? "1" : "0");
    }
    /**
     * 下分订单手续费百分比
     * @return
     */
    public int getTreasurerServiceChargePercentage(){
        return Integer.valueOf(this.treasurerInfo.getOrDefault(2,"2"));
    }

    public void setTreasurerServiceChargePercentage(int percentage){
        this.treasurerInfo.put(2,String.valueOf(percentage));
    }
    /**
     * 下分订单最小下分值
     * @return
     */
    public int getTreasurerCanDownGoldMinValue(){
        return Integer.valueOf(this.treasurerInfo.getOrDefault(3,"5000"));
    }

    public void setTreasurerCanDownGoldMinValue(int minValue){
        this.treasurerInfo.put(3,String.valueOf(minValue));
    }
    /**
     * 下分订单描述
     * @return
     */
    public String getTreasurerDesc(){
        return this.treasurerInfo.getOrDefault(4,"");
    }

    public void setTreasurerDesc(String desc){
        this.treasurerInfo.put(4,StringUtil.isEmptyOrNull(desc) ? "" : desc);
    }

    public long getLockTime() {
        return lockTime.get();
    }

    public void setLockTime(long lockTime) {
        this.lockTime.set(lockTime);
    }

    public long getLockByClubUid() {
        return lockByClubUid.get();
    }

    public void setLockByClubUid(long lockByClubUid) {
        this.lockByClubUid.set(lockByClubUid);
    }

    /**
     * 设置开放状态
     */
    public void setOpen() {
        this.closeStatus.set(EClubCloseStatus.OPEN.getType());
        this.setDirty(Boolean.TRUE);
    }

    /**
     * 设置打烊中状态
     */
    public void setClosing() {
        this.closeStatus.compareAndSet(EClubCloseStatus.OPEN.getType(), EClubCloseStatus.CLOSING.getType());
        this.setDirty(Boolean.TRUE);
    }

    /**
     * 设置已打烊状态
     */
    public void setClosed() {
        this.closeStatus.compareAndSet(EClubCloseStatus.CLOSING.getType(), EClubCloseStatus.CLOSED.getType());
        this.setDirty(Boolean.TRUE);
    }

    /**
     * 是否匹配指定打烊状态
     * 
     * @return
     */
    public boolean matchCloseStatus(EClubCloseStatus status) {
        return status.match(this.closeStatus.get());
    }

    public void setCloseStatusDb(Integer closeStatus) {
        this.closeStatus.set(closeStatus);
    }

    public int getCloseStatusDb() {
        return getCloseStatus();
    }

    public int getCloseStatus() {
        return closeStatus.get();
    }

    public String getServiceChargeDb() {
        return JsonUtil.toJson(this.serviceCharge);
    }

    public void setServiceChargeDb(String serviceChargeStr) {
        if (StringUtil.isEmptyOrNull(serviceChargeStr)){
            return;
        }
        this.serviceCharge = JsonUtil.fromJson(serviceChargeStr, new TypeReference<ConcurrentHashMap<Long, Integer>>() {
        });
    }

    public Map<Long,Integer> getServiceChargeMap() {
        return this.serviceCharge;
    }

    public long getdToGoldTotal() {
        return dToGoldTotal;
    }

    public void setdToGoldTotal(long dToGoldTotal) {
        this.dToGoldTotal = dToGoldTotal;
    }
}
