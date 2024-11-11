package com.xiuxiu.app.server.club.impl;

import com.google.common.primitives.Longs;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.*;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerSmallInfo;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.club.*;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.constant.*;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.app.server.forbid.ForbidManager;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.*;
import com.xiuxiu.app.server.rank.ERankType;
import com.xiuxiu.app.server.rank.NewRankManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.statistics.DownLineGameManager;
import com.xiuxiu.app.server.statistics.ETodayStatisticsType;
import com.xiuxiu.app.server.statistics.TodayStatistics;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 抽象亲友圈实现
 *
 * @author Administrator
 */
public abstract class AbstractClub implements IClub {
    /**
     * 亲友圈数据
     */
    protected ClubInfo clubInfo;
    /**
     * 亲友圈成员数据
     */
    private Map<Long, ClubMember> members = new ConcurrentHashMap<>();

    /**
     * 亲友圈成员额外数据
     */
    private Map<Long, ClubMemberExt> memberExtMap = new ConcurrentHashMap<>();

    protected transient ReentrantLock mergeLock = new ReentrantLock();

    private transient ConcurrentHashMap<Long, Long> playerEnterClubUidMap = new ConcurrentHashMap<>();

    /**
     * 统计信息用于排行榜
     */
    protected transient HashMap<ETodayStatisticsType,ConcurrentHashMap<Long, TodayStatistics>> todayStatisticsMap = new HashMap<>();

    /**
     *  已经使用过的机器人Uid
     */
    protected transient List<Long> usedRobotUid = new ArrayList<>();


    @Override
    public void init(ClubInfo clubInfo) {
        this.clubInfo = clubInfo;
        // 加载成员
        loadMembers();
        // 加载成员扩展信息
        loadMemberExts();
        // 加载统计信息
        loadStatisticsData();
    }

    private void loadMemberExts() {
        List<ClubMemberExt> tempList = DBManager.I.getClubMemberExtDAO().loadAll(this.getClubUid());
        if (tempList != null && tempList.size() > 0) {
            for (ClubMemberExt item : tempList) {
                memberExtMap.put(item.getPlayerUid(), item);
            }
        }
    }

    /**
     * 加载成员
     */
    private void loadMembers() {
        // 加载所有成员列表
        List<ClubMember> memberList = DBManager.I.getClubMemberDAO().loadAllMemberByClubUid(clubInfo.getUid());
        for (int i = 0; i < memberList.size(); i++) {
            ClubMember member = memberList.get(i);
            if (this.hasMember(member.getPlayerUid())) {
                ClubMember oldClubMember = this.members.get(member.getPlayerUid());
                DBManager.I.getClubMemberDAO().deleteByUid(oldClubMember.getUid());
            }
            this.putMembers(member.getPlayerUid(), member);
        }
    }

    private void loadStatisticsData(){
        todayStatisticsMap.put(ETodayStatisticsType.CLUB_GAME_NUM,new ConcurrentHashMap<>());
        todayStatisticsMap.put(ETodayStatisticsType.CLUB_GAME_SCORE,new ConcurrentHashMap<>());
        todayStatisticsMap.put(ETodayStatisticsType.CLUB_GAME_WINNER,new ConcurrentHashMap<>());

        ConcurrentHashMap<Long,TodayStatistics> statInfoMap = todayStatisticsMap.get(ETodayStatisticsType.CLUB_GAME_NUM);
        List<TodayStatistics> todayStatisticsList = DBManager.I.getTodayStatisticsDao().loadByFromUid(this.getClubUid(), ETodayStatisticsType.CLUB_GAME_NUM.ordinal());
        for (TodayStatistics todayStatistics : todayStatisticsList){
            statInfoMap.putIfAbsent(todayStatistics.getPlayerUid(), todayStatistics);
        }
        statInfoMap = todayStatisticsMap.get(ETodayStatisticsType.CLUB_GAME_SCORE);
        todayStatisticsList = DBManager.I.getTodayStatisticsDao().loadByFromUid(this.getClubUid(), ETodayStatisticsType.CLUB_GAME_SCORE.ordinal());
        for (TodayStatistics todayStatistics : todayStatisticsList){
            statInfoMap.putIfAbsent(todayStatistics.getPlayerUid(), todayStatistics);
        }
        statInfoMap = todayStatisticsMap.get(ETodayStatisticsType.CLUB_GAME_WINNER);
        todayStatisticsList = DBManager.I.getTodayStatisticsDao().loadByFromUid(this.getClubUid(), ETodayStatisticsType.CLUB_GAME_WINNER.ordinal());
        for (TodayStatistics todayStatistics : todayStatisticsList){
            statInfoMap.putIfAbsent(todayStatistics.getPlayerUid(), todayStatistics);
        }
    }

    private void putMembers(long uid, ClubMember member) {
        if (uid > 0 && null != member) {
            this.members.put(uid, member);
        }
    }

    @Override
    public boolean save() {
        boolean result = this.clubInfo.save();
        saveMembers();
        saveMemberExts();
        saveTodayStatistics();
        return result;
    }

    private void saveMemberExts() {
        for (Map.Entry<Long, ClubMemberExt> entry : this.memberExtMap.entrySet()) {
            entry.getValue().save();
        }
    }

    private void saveMembers() {
        for (Map.Entry<Long, ClubMember> entry : this.members.entrySet()) {
            entry.getValue().save();
        }
    }

    private void saveTodayStatistics(){
        for(ConcurrentHashMap<Long,TodayStatistics> dataMap : this.todayStatisticsMap.values()){
            Iterator<Map.Entry<Long,TodayStatistics>> it = dataMap.entrySet().iterator();
            while (it.hasNext()){
                it.next().getValue().save();
            }
        }
    }

    @Override
    public void zero(long now) {

    }

    @Override
    public long getClubUid() {
        return clubInfo.getUid();
    }

    @Override
    public EClubType getClubType() {
        return EClubType.getType(clubInfo.getClubType());
    }

    @Override
    public long getOwnerId() {
        return clubInfo.getOwnerId();
    }

    @Override
    public String getName() {
        return clubInfo.getName();
    }

    @Override
    public String getIcon() {
        return clubInfo.getIcon();
    }

    @Override
    public String getDesc() {
        return this.clubInfo.getDesc();
    }

    @Override
    public String getGameDesc() {
        return this.clubInfo.getGameDesc();
    }

    @Override
    public long getCreateTime() {
        return clubInfo.getCreateTime();
    }

    @Override
    public long getFinalClubId() {
        if (this.clubInfo.getParentUid() > 0) {
            IClub club = ClubManager.I.getClubByUid(this.clubInfo.getParentUid());
            if (null == club) {
                return 0;
            }
            long finalClubId = club.getFinalClubId();
            if (0 == finalClubId) {
                finalClubId = club.getClubUid();
            }
            return finalClubId;
        } else {
            return this.clubInfo.getChildUid().size() > 0 ? this.getClubUid() : 0;
        }
    }

    @Override
    public void fillDepthChildClubUidList(List<Long> uidList) {
        ConcurrentHashSet<Long> childUidSet = this.clubInfo.getChildUid();
        if (childUidSet.size() > 0) {
            uidList.addAll(childUidSet);
            for (Long childClubUid : childUidSet) {
                IClub club = ClubManager.I.getClubByUid(childClubUid);
                if (null != club) {
                    club.fillDepthChildClubUidList(uidList);
                }
            }
        }
    }

    @Override
    public boolean checkIsMainClub() {
        if (this.clubInfo.getParentUid() > 0) {
            return false;
        }
        return this.clubInfo.getChildUid().size() > 0;
    }

    @Override
    public boolean checkIsJoinInMainClub() {
        if (this.clubInfo.getParentUid() > 0) {
            return true;
        }
        return this.clubInfo.getChildUid().size() > 0;
    }

    @Override
    public boolean checkIsLevelOneClub(){
        if (this.clubInfo.getParentUid() > 0){
            IClub parentClub = ClubManager.I.getClubByUid(this.clubInfo.getParentUid());
            if (null != parentClub && parentClub.checkIsMainClub()){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIsLevelTwoClub(){
        if (this.clubInfo.getParentUid() > 0){
            IClub parentClub = ClubManager.I.getClubByUid(this.clubInfo.getParentUid());
            if (null != parentClub && parentClub.checkIsLevelOneClub()){
                return true;
            }
        }
        return false;
    }

    @Override
    public ClubInfo getClubInfo() {
        return this.clubInfo;
    }

    @Override
    public ErrorCode mergeClub(IClub mergeClub) {
        if (mergeClub.getClubUid() == this.getClubUid()) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        try {
            if (!this.mergeLock.tryLock(10, TimeUnit.MILLISECONDS)) {
                return ErrorCode.CLUB_MERGE_FAIL;
            }
            if (this.clubInfo.getParentUid() > 0) {
                return ErrorCode.CLUB_HAVE_PARENT_NO_MERGER;
            }

            //set merge club data
            if (!mergeClub.setParentUidByMerge(this.getClubUid())) {
                return ErrorCode.CLUB_HAVE_PARENT_NO_MERGER;
            }

            //set main club data
            this.clubInfo.getChildUid().add(mergeClub.getClubUid());
            this.clubInfo.setDirty(true);
        } catch (Exception e) {
            return ErrorCode.CLUB_MERGE_FAIL;
        } finally {
            if (this.mergeLock.isHeldByCurrentThread()) {
                this.mergeLock.unlock();
            }
        }

        //set main club data
        //this.playerEnterClubUidMap.putAll(mergeClub.getPlayerEnterClubMap());

        //set merge club data
        //清空被合圈的机器人桌子
        for (Map.Entry<Long,Floor> entry : mergeClub.getFloor().entrySet()) {
            entry.getValue().setSetRobotDesk2Min(0);
            entry.getValue().setSetRobotDesk2Max(0);
            entry.getValue().setRandomTime2(0);
            entry.getValue().setSetRobotDesk3Min(0);
            entry.getValue().setSetRobotDesk3Max(0);
            entry.getValue().setRandomTime3(0);
        }

        //取消被合圈的上下分财务职位
        //通知被取消职位的人
        PCLIClubNtfSetTreasurer resp = new PCLIClubNtfSetTreasurer();
        resp.clubUid = mergeClub.getClubUid();
        resp.type = EClubTreasurerType.DOWN.getValue();
        resp.isSet = false;
        for (Long treasurerUid : mergeClub.getClubInfo().getDownGoldTreasurer()) {
            Player treasurerPlayer = PlayerManager.I.getOnlinePlayer(treasurerUid);
            if (treasurerPlayer == null) {
                continue;
            }
            resp.playerUid = treasurerUid;
            treasurerPlayer.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO, resp);
        }
        //清空财务缓存
        mergeClub.getClubInfo().getUpGoldTreasurer().clear();
        mergeClub.getClubInfo().getDownGoldTreasurer().clear();

        //清空所有被合得圈得竞技分领取活动得相关数据
        mergeClub.clearGoldActivity();
        //mergeClub.removeForbid(this.getClubUid(),this.getClubType());

        ClubInfo mergeClubInfo = mergeClub.getClubInfo();
        //清理退主圈申请列表
        Iterator<ApplyInfo> it = mergeClubInfo.getLeaveApplyList().iterator();
        while (it.hasNext()){
            ApplyInfo tempInfo = it.next();
            if (tempInfo.getState() == EOpStateType.NORMAL.ordinal()){
                it.remove();
            }
        }
        //清理合主圈申请列表
        it = mergeClubInfo.getMergeApplyList().iterator();
        while (it.hasNext()){
            ApplyInfo tempInfo = it.next();
            if (tempInfo.getState() == EOpStateType.NORMAL.ordinal()){
                it.remove();
            }
        }
        mergeClubInfo.setDirty(true);

        return ErrorCode.OK;
    }

    @Override
    public ErrorCode leaveMainClub(IClub leaveClub){
        if (leaveClub.getClubUid() == this.getClubUid()) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        //clear leave club data
        long parentClubUid = leaveClub.getClubInfo().getParentUid();
        leaveClub.getClubInfo().setParentUid(0);
        leaveClub.getClubInfo().setLockTime(0);
        leaveClub.getClubInfo().setDirty(true);
        leaveClub.clearGoldActivity();

        //clear parent club data
        IClub leaveParentClub = this;
        if (parentClubUid != this.getClubUid()){
            leaveParentClub = ClubManager.I.getClubByUid(parentClubUid);
        }
        if (null != leaveParentClub){
            leaveParentClub.getClubInfo().getChildUid().remove(leaveClub.getClubUid());
            leaveParentClub.getClubInfo().setDirty(true);
        }

        //clear main club data
        // 如果圈没有下线的小圈，则重置打烊为开放状态
        if (!checkIsMainClub()) {
            applyClose(EClubCloseStatus.OPEN);
        }

        List<Long> childUidList = new ArrayList<>();
        if (leaveClub.getClubInfo().getChildUid().size() > 0){
            leaveClub.fillDepthChildClubUidList(childUidList);
            Iterator<ApplyInfo> it = this.getClubInfo().getLeaveApplyList().iterator();
            while (it.hasNext()){
                ApplyInfo tempInfo = it.next();
                if (tempInfo.getState() == EOpStateType.NORMAL.ordinal() && childUidList.contains(tempInfo.getfUid())){
                    it.remove();
                }
            }
            leaveClub.getClubInfo().setDirty(true);
        }

        //离开主圈的圈和所有下级圈的圈相关下分未处理订单自动拒绝
        UpDownGoldTreasurerManager.I.clearClubAndChildAllOrder(leaveClub.getClubUid());

        return ErrorCode.OK;
    }

    @Override
    public boolean setParentUidByMerge(long parentUid) {
        try {
            if (this.mergeLock.tryLock(10, TimeUnit.MILLISECONDS)) {
                if (this.clubInfo.getParentUid() > 0) {
                    return false;
                }
                this.clubInfo.setParentUid(parentUid);
                this.clubInfo.setJoinParentTime(System.currentTimeMillis());
                this.clubInfo.setDirty(true);
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (this.mergeLock.isHeldByCurrentThread()) {
                this.mergeLock.unlock();
            }
        }
        return false;
    }

    @Override
    public boolean setLockClub(long lockTime, long lockByClubUid) {
        return true;
    }

    @Override
    public long[] getTotalGoldAndRewardValueNoChild() {
        long[] result = { 0, 0 };
        return result;
    }

    @Override
    public long[] getTotalGoldAndRewardValue() {
        long[] result = { 0, 0 };
        return result;
    }

    @Override
    public long[] getAllMemberTotalUpAndDownScore(){
        long[] result = { 0, 0 };
        return result;
    }

    @Override
    public void applyMerge(IClub mergeClub, long nowTime) {
        ApplyInfo applyInfo = ApplyInfo.createApplyInfoByMergeOrLeave(mergeClub,this,EOpStateType.NORMAL,nowTime);
        this.clubInfo.addApplyMergeList(applyInfo);
        this.sendApplyMessage(EApplyType.CLUB_MERGE.ordinal());

        ApplyInfo cpApplyInfo = ApplyInfo.copyApplyInfo(applyInfo);
        cpApplyInfo.setState(EOpStateType.WAIT_OTHER_DEAL.ordinal());
        mergeClub.getClubInfo().addApplyMergeList(cpApplyInfo);
    }

    @Override
    public void applyLeave(IClub leaveClub, long nowTime){
        ApplyInfo applyInfo = ApplyInfo.createApplyInfoByMergeOrLeave(leaveClub,this,EOpStateType.NORMAL,nowTime);
        this.clubInfo.addApplyLeaveList(applyInfo);
        this.sendApplyMessage(EApplyType.CLUB_MERGE.ordinal());
    }

    @Override
    public void applyJoin(Player player) {
        this.addApplyListInfo(player.getUid(), -1, EOpStateType.NORMAL);
        this.sendApplyMessage(EApplyType.CLUB.ordinal());
        player.send(CommandId.CLI_NTF_CLUB_APPLY_JOIN_CLUB, null);
    }

    @Override
    public void addApplyListInfo(long playerUid, long oPlayerUid, EOpStateType type) {
        if (this.getClubInfo().getmemberApplyList().size() >= Constant.MAX_APPLY_CNT){
            this.getClubInfo().removeApply();
        }
        ApplyInfo info = new ApplyInfo();
        info.setfUid(playerUid);
        info.setaTime(System.currentTimeMillis());
        info.setState(type.ordinal());
        info.settUid(oPlayerUid);
        this.getClubInfo().addApply(info);
        this.getClubInfo().setDirty(Boolean.TRUE);
    }

    private void sendApplyMessage(int applyType) {
        PCLIClubNtfApplyJoinClub joinClub = new PCLIClubNtfApplyJoinClub();
        joinClub.clubUid = this.getClubUid();
        joinClub.applyType = applyType;
        if (EApplyType.CLUB.ordinal() == applyType){
            for (ClubMember member : this.members.values()){
                if (member.getJobType() == EClubJobType.CHIEF.getType() || member.getJobType() == EClubJobType.DEPUTY.getType()){
                    Player player = PlayerManager.I.getOnlinePlayer(member.getPlayerUid());
                    if (null != player){
                        player.send(CommandId.CLI_NTF_CLUB_APPLY_JOIN_CLUB, joinClub);
                    }
                }
            }
        }else{
            Player player = PlayerManager.I.getOnlinePlayer(this.getOwnerId());
            if (null != player){
                player.send(CommandId.CLI_NTF_CLUB_APPLY_JOIN_CLUB, joinClub);
            }
        }
    }

    private void sendMemberJoinClubMessage(Player player){
        PCLIClubNtfAddMember ntfInfo = new PCLIClubNtfAddMember();
        ntfInfo.clubUid = this.getClubUid();
        ntfInfo.playerUid = player.getUid();
        ntfInfo.uplinePlayerUid = this.getMember(player.getUid()).getUplinePlayerUid();
        ntfInfo.memberCnt = this.getMemberCnt();
        if (player.isOnline()){
            player.send(CommandId.CLI_NTF_CLUB_ADDMEMBER, ntfInfo);
        }
    }

    @Override
    public boolean opApplyList(Player oPlayer, long playerUid, int op) {
        ApplyInfo applyInfo = null;
        Iterator<ApplyInfo> it = this.getClubInfo().getApplyInfo().iterator();
        while (it.hasNext()) {
            ApplyInfo temp = it.next();
            if (temp.getfUid() == playerUid) {
                applyInfo = temp;
                break;
            }
        }
        if (null == applyInfo || EOpStateType.NORMAL.ordinal() != applyInfo.getState()) {
            return false;
        }
        if (0 == op) {
            // 同意
            Player ownerPlayer = PlayerManager.I.getPlayer(this.getOwnerId());

            if (null == ownerPlayer || this.members.size() >= EPlayerPrivilegeLevel.getValue(ownerPlayer.getPrivilege(),
                    EPlayerPrivilege.GROUP_MEMBER_NUM)) {
                return false;
            }
            applyInfo.setState(EOpStateType.AGREE.ordinal());
            applyInfo.settUid(oPlayer.getUid());
            applyInfo.setaTime(System.currentTimeMillis());
            Player player = PlayerManager.I.getPlayer(playerUid);
            if (null != player) {
                this.addMember(this.getOwnerId(), player, EClubJobType.NORMAL);
                this.sendMemberJoinClubMessage(oPlayer);
            }
        } else if (1 == op) {
            // 拒绝
            applyInfo.setState(EOpStateType.REJECT.ordinal());
            applyInfo.settUid(oPlayer.getUid());
            applyInfo.setaTime(System.currentTimeMillis());
            Player p = PlayerManager.I.getOnlinePlayer(playerUid);
            if (null != p) {
                ErrorCode ec = this.getClubType() == EClubType.CARD ? ErrorCode.GROUP_APPLY_OPERATE_REJECT : ErrorCode.CLUB_APPLY_OPERATE_REJECT_GOLD;
                p.send(CommandId.CLI_NTF_CLUB_OP_APPLY_LIST_RESULT, ec);
            }
        }
        this.getClubInfo().setDirty(Boolean.TRUE);
        return true;
    }

    @Override
    public void addMember(long invitorPlayerUid, Player player, EClubJobType jobType) {
//        if (player.getRecommendInfo().getRecommendPlayerUid() < 0) {
//            if (invitorPlayerUid > 0) {
//                Player topPlayer = PlayerManager.I.getPlayer(invitorPlayerUid);
//                if (null != topPlayer) {
//                    RecommendManager.I.recommend(topPlayer, player, this.clubInfo.getUid());
//                }
//            }
//        } else {
//            if (this.hasMember(player.getRecommendInfo().getRecommendPlayerUid())) {
//                invitorPlayerUid = player.getRecommendInfo().getRecommendPlayerUid();
//            }
//        }

        if (!this.hasMember(invitorPlayerUid)){
            invitorPlayerUid = -1;
        }

        if (this.hasMember(player.getUid())){
            return;
        }

        ClubMember invitorMember = new ClubMember();
        invitorMember.setUid(UIDManager.I.getAndInc(UIDType.CLUB_MEMBER));
        invitorMember.setClubUid(this.getClubUid());
        invitorMember.setPlayerUid(player.getUid());
        invitorMember.setJobType(jobType, true);
        invitorMember.setPrivilege(0);
        invitorMember.setShowNick(1);
        ClubMemberExt clubMemberExt = this.getMemberExt(invitorMember.getPlayerUid(), true);
        if (jobType == EClubJobType.CHIEF) {
            clubMemberExt.setConvert(EConvertType.CONVERT.ordinal());
            clubMemberExt.setDirty(Boolean.TRUE);
        } else {
            ClubMember upLineMember = this.getMember(invitorPlayerUid <= 0 ? this.getOwnerId() : invitorPlayerUid);
            this.setMemberUpLine(invitorMember,upLineMember);
            // 初始设置活动奖励分成
            if (null != upLineMember && upLineMember.getPlayerUid() != this.getOwnerId()) {
                if (upLineMember.getDivide() == 0) {
                    //upLineMember.changeDivideAndDivideLine(ClubActivityManager.I.getDivide(this.getClubUid()),ClubActivityManager.I.getDivideLine(this.getClubUid()));
                }
            }
        }
        invitorMember.setState(EClubMemberStateType.ORMAL.ordinal());
        invitorMember.setJoinTime(System.currentTimeMillis());
        invitorMember.setDirty(true);
        this.putMembers(player.getUid(), invitorMember);
        this.DelApplyInfo(player);
        player.addClub(this);
        this.sendMemberJoinClubMessage(player);
    }

    private void DelApplyInfo(Player player){
        Iterator<ApplyInfo> it = this.getClubInfo().getApplyInfo().iterator();
        while (it.hasNext()) {
            ApplyInfo temp = it.next();
            Player applyPlayer = PlayerManager.I.getPlayer(temp.getfUid());
            if (null == applyPlayer) {
                continue;
            }
            if (player.getUid() == applyPlayer.getUid() && temp.getState() == EOpStateType.NORMAL.ordinal()){
                it.remove();
            }
        }
    }

    @Override
    public void delMember(long playerUid, long opPlayerUid) {
        ClubMember delMember = this.getMember(playerUid);
        if (null == delMember){
            return;
        }

        //离开圈的人的上级是否还有别的下级
        this.setMemberUpLine(delMember,null);

        //判断离开圈的人是否有下级
        for (Map.Entry<Long,ClubMember> entry : this.members.entrySet()) {
            if (entry.getValue().getUplinePlayerUid() == playerUid) {
                entry.getValue().setUplinePlayerUid(this.getOwnerId());
                entry.getValue().setDirty(true);
            }
        }
        final long temp = this.getClubUid();
        DBManager.I.save(new Task() {
            @Override
            public void run() {
                DBManager.I.getClubMemberDAO().delByClubUidAndPlayerUid(temp, playerUid);
            }
        });
        this.members.remove(playerUid);
        Player player = PlayerManager.I.getPlayer(playerUid);
        if (null != player) {
            player.leaveClub(temp);
        }
        PCLIClubNtfDelMember respInfo = new PCLIClubNtfDelMember();
        respInfo.clubUid = this.getClubUid();
        respInfo.playerUid = playerUid;
        respInfo.memberCnt = this.getMemberCnt();
        this.broadcast(CommandId.CLI_NTF_CLUB_DELMEMBER, respInfo);
        this.addApplyListInfo(playerUid, opPlayerUid, opPlayerUid > 0 ? EOpStateType.DELETE : EOpStateType.LEAVE);

        //判断是否是财务,从club财务列表中删除
        ClubInfo clubInfo = this.getClubInfo();
        if (clubInfo.getUpGoldTreasurer().contains(playerUid)) {
            clubInfo.getUpGoldTreasurer().remove(playerUid);
            clubInfo.setDirty(true);
        }
        if (clubInfo.getDownGoldTreasurer().contains(playerUid)) {
            UpDownGoldTreasurerManager.I.clearTreasurerAllOrder(playerUid, this.getClubUid());//把财务的未处理下分订单清除并拒绝

            clubInfo.getDownGoldTreasurer().remove(playerUid);
            clubInfo.setDirty(true);
        }

        //判断玩家身上是否有
    }

    @Override
    public void changeMemberState(long opUid, long playerUid, int state) {
        ClubMember member = this.members.get(playerUid);
        member.setState(state);
        member.setDirty(true);
        this.members.get(playerUid).setState(state);

        Player player = PlayerManager.I.getPlayer(playerUid);
        PCLIClubNtfProhibitInfo ntfInfo = new PCLIClubNtfProhibitInfo();
        ntfInfo.clubUid = this.getClubUid();
        ntfInfo.playerUid = player.getUid();
        ntfInfo.isProhibit = state != 0;
        this.broadcast(CommandId.CLI_NTF_CLUB_PROHIBITMEMBER, ntfInfo);
        EOpStateType stateType = state == 1 ? EOpStateType.FORBID : EOpStateType.NOT_FORBID;
        this.addApplyListInfo(playerUid, opUid, stateType);
    }

    @Override
    public void changeMemberJob(long opUid, long playerUid, int jobType) {
        ClubMember member = this.members.get(playerUid);
        member.setJobType(EClubJobType.getType(jobType), true);
        if (jobType != EClubJobType.NORMAL.getType()) {
            member.setJobType(EClubJobType.NORMAL, false);//取消普通成员
        } else {
            member.setJobType(EClubJobType.NORMAL, true);//设置普通成员
            //取消别的所有职位
           // member.setJobType(EClubJobType.CHIEF, false);
            member.setJobType(EClubJobType.DEPUTY, false);
            member.setJobType(EClubJobType.ELDER, false);
        }
        member.setDirty(true);

        PCLIClubNtfSetMemberJob ntfInfo = new PCLIClubNtfSetMemberJob();
        ntfInfo.clubUid = this.getClubUid();
        ntfInfo.playerUid = playerUid;
        ntfInfo.jobType = member.getJobType();

        // 广播
        this.broadcast(CommandId.CLI_NTF_CLUB_CHANGEMEMBERJOB, ntfInfo);
        if (EClubJobType.getType(jobType) == EClubJobType.DEPUTY || EClubJobType.getType(jobType) == EClubJobType.NORMAL){
            EOpStateType stateType = EClubJobType.getType(jobType) == EClubJobType.DEPUTY ? EOpStateType.PROMOTION : EOpStateType.DECLINE;
            this.addApplyListInfo(playerUid, opUid, stateType);
        }
    }

    private boolean checkHasDownLine(long playerUid){
        for (Map.Entry<Long,ClubMember> entry : this.members.entrySet()) {
            if (entry.getKey() == playerUid) {
                continue;
            }
            if (entry.getValue().getUplinePlayerUid() == playerUid) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setMemberUpLine(ClubMember member,ClubMember upLineMember){
        if (null == member){
            return;
        }

        if (null != upLineMember
                && (upLineMember.getPlayerUid() == member.getUplinePlayerUid() || upLineMember.getUplinePlayerUid() == member.getPlayerUid())){
            return;
        }

        if (member.getUplinePlayerUid() > 0) {
            ClubMember oldUpLineMember = this.getMember(member.getUplinePlayerUid());
            if (null != oldUpLineMember
                    && oldUpLineMember.getPlayerUid() != this.getOwnerId()
                    && oldUpLineMember.checkJobType(EClubJobType.ELDER)
                    && !checkHasDownLine(oldUpLineMember.getPlayerUid())) {
                oldUpLineMember.setJobType(EClubJobType.ELDER,false);
                if (!oldUpLineMember.checkJobType(EClubJobType.DEPUTY)) {
                    oldUpLineMember.setJobType(EClubJobType.NORMAL, true);
                }
                oldUpLineMember.setDirty(true);
            }
        }

        if (null == upLineMember) {
            member.setUplinePlayerUid(-1);
        } else {
            member.setUplinePlayerUid(upLineMember.getPlayerUid());
            if (upLineMember.getPlayerUid() != this.getOwnerId() && !upLineMember.checkJobType(EClubJobType.ELDER)) {
                this.changeMemberJob(this.getOwnerId(), upLineMember.getPlayerUid(), EClubJobType.ELDER.getType());
            }
        }

        member.setDirty(true);
    }

    @Override
    public void changeAnnouncement(String content, int expireSeconds) {
        this.getClubInfo().setAnnouncement(content);
        this.getClubInfo().setAnnouncementExpireAt(System.currentTimeMillis() + expireSeconds * 1000);
        this.getClubInfo().setDirty(true);

        final AbstractClub self = this;
        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                PCLIClubNtfAnnouncementInfo info = new PCLIClubNtfAnnouncementInfo();
                info.clubUid = self.getClubUid();
                info.content = content;
                info.expireSeconds = expireSeconds;
                self.broadcast(CommandId.CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT, info);
            }
        });
    }

    @Override
    public void dissolve() {
        Iterator<Map.Entry<Long, ClubMember>> it = this.members.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, ClubMember> entry = it.next();
            Player delPlayer = PlayerManager.I.getPlayer(entry.getValue().getPlayerUid());
            if (null == delPlayer) {
                continue;
            }
            delPlayer.leaveClub(this.getClubUid());
            delPlayer.addOwnerClubCnt(false);
        }
        this.getClubInfo().setState(1);
        this.getClubInfo().setDirty(Boolean.TRUE);
        this.save();
    }

    @Override
    public PCLIClubInfo getClubInfoPCL(Player player) {
        PCLIClubInfo clubInfo = new PCLIClubInfo();
        clubInfo.selfClubInfo = this.getClubSingleInfoPCL(player);
        long mainClubUid = this.getFinalClubId();
        if (mainClubUid != this.getClubUid() && mainClubUid > 0) {
            IClub mainClub = ClubManager.I.getClubByUid(mainClubUid);
            if (null != mainClub) {
                clubInfo.mainClubInfo = mainClub.getClubSingleInfoPCL(player);
            }
        }
        clubInfo.mainClubUid = mainClubUid;
        return clubInfo;
    }

    @Override
    public PCLIClubSingleInfo getClubSingleInfoPCL(Player player) {
        PCLIClubSingleInfo clubInfo = new PCLIClubSingleInfo();
        clubInfo.clubUid = this.getClubUid();
        clubInfo.ownerUid = this.getOwnerId();
        clubInfo.name = this.getName();
        clubInfo.icon = this.getIcon();
        clubInfo.desc = this.getDesc();
        clubInfo.gameDesc = this.getGameDesc();
        clubInfo.clubType = this.getClubType().getType();
        clubInfo.createTime = this.getCreateTime();
        Player ownerPlayer = PlayerManager.I.getPlayer(this.getOwnerId());
        clubInfo.creater = ownerPlayer.getPlayerBriefInfo(player);
        clubInfo.memberCnt = this.getMemberCnt();
        clubInfo.open= ClubActivityManager.I.getAndSetActivity(this.getClubUid(),EClubActivityType.DIVIDE).getDivideData().isOpen();
        clubInfo.status = this.getClubInfo().getCloseStatus();
        if (null != player) {
            ClubMember myMemberInfo = this.members.get(player.getUid());
            if (null != myMemberInfo) {
                PCLIClubMemberInfo memberInfo = new PCLIClubMemberInfo();
                memberInfo.info = player.getPlayerBriefInfo(player);
                memberInfo.showNick = myMemberInfo.getShowNick();
                memberInfo.jobType = myMemberInfo.getJobType();
                memberInfo.uplinePlayerUid = myMemberInfo.getUplinePlayerUid();
                memberInfo.forbidPlay = myMemberInfo.getState();
                memberInfo.joinTime = myMemberInfo.getJoinTime();
                memberInfo.downLineNum = this.checkHasDownLine(myMemberInfo.getPlayerUid()) ? 1 : 0;
                memberInfo.divide=myMemberInfo.getDivide();
                memberInfo.divideLine=myMemberInfo.getDivideLine();
                memberInfo.member=ClubActivityManager.I.getDivide(this.getClubUid());
                memberInfo.line=ClubActivityManager.I.getDivideLine(this.getClubUid());
                memberInfo.onlyUpLineSetGold = myMemberInfo.getOnlyUpLineSetGold();
                memberInfo.isUpGoldTreasurer = this.checkIsUpTreasurer(player.getUid());
                memberInfo.isDownGoldTreasurer = this.checkIsDownTreasurer(player.getUid());
                ClubMemberExt clubMemberExt = this.getMemberExt(myMemberInfo.getPlayerUid(), true);
                if (null != clubMemberExt) {
                    memberInfo.arenaValue = clubMemberExt.getGold();
                    memberInfo.code = clubMemberExt.getCode();
                    memberInfo.convert = clubMemberExt.getConvert();
                }
                clubInfo.myMemberInfo = memberInfo;
            }else {
            	if(player.getUid()==this.getOwnerId()) {
            		ClubMember invitorMember = new ClubMember();
            		invitorMember.setUid(UIDManager.I.getAndInc(UIDType.CLUB_MEMBER));
            		invitorMember.setClubUid(this.getClubUid());
            		invitorMember.setPlayerUid(player.getUid());
            		invitorMember.setJobType(EClubJobType.CHIEF, true);
            		invitorMember.setPrivilege(0);
                    invitorMember.setShowNick(1);
                    invitorMember.setState(EClubMemberStateType.ORMAL.ordinal());
                    invitorMember.setUplinePlayerUid(player.getUid());
                    invitorMember.setJoinTime(System.currentTimeMillis());
                    this.putMembers(player.getUid(), invitorMember);
                    
                    PCLIClubMemberInfo memberInfo = new PCLIClubMemberInfo();
                    memberInfo.info = player.getPlayerBriefInfo(player);
                    memberInfo.showNick = invitorMember.getShowNick();
                    memberInfo.jobType = invitorMember.getJobType();
                    memberInfo.uplinePlayerUid = invitorMember.getUplinePlayerUid();
                    memberInfo.forbidPlay = invitorMember.getState();
                    memberInfo.joinTime = invitorMember.getJoinTime();
                    memberInfo.downLineNum = this.checkHasDownLine(invitorMember.getPlayerUid()) ? 1 : 0;
                    memberInfo.divide=invitorMember.getDivide();
                    memberInfo.divideLine=invitorMember.getDivideLine();
                    memberInfo.member=ClubActivityManager.I.getDivide(this.getClubUid());
                    memberInfo.line=ClubActivityManager.I.getDivideLine(this.getClubUid());
                    memberInfo.onlyUpLineSetGold = invitorMember.getOnlyUpLineSetGold();
                    memberInfo.isUpGoldTreasurer = this.checkIsUpTreasurer(player.getUid());
                    memberInfo.isDownGoldTreasurer = this.checkIsDownTreasurer(player.getUid());
                    ClubMemberExt clubMemberExt = this.getMemberExt(invitorMember.getPlayerUid(), true);
                    if (null != clubMemberExt) {
                        memberInfo.arenaValue = clubMemberExt.getGold();
                        memberInfo.code = clubMemberExt.getCode();
                        memberInfo.convert = clubMemberExt.getConvert();
                    }
                    clubInfo.myMemberInfo = memberInfo;
            	}
            }
        }
        clubInfo.parentUid = this.getClubInfo().getParentUid();
        clubInfo.childUidList.addAll(this.clubInfo.getChildUid());
        return clubInfo;
    }

    @Override
    public ClubMember getMember(long playerUid) {
        return this.members.get(playerUid);
    }

    @Override
    public List<Long> getAllMemberUids() {
        List<Long> list = new ArrayList<>();
        for (Map.Entry<Long, ClubMember> entry : this.members.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    @Override
    public List<Long> getInviteMemberUids(Player player, IRoom room) {
        List<Long> result = null;
        for (Map.Entry<Long, ClubMember> entry : this.members.entrySet()) {
            if (player.getUid() == entry.getKey()) {
                continue;
            }
            if (!PlayerManager.I.isOnline(entry.getKey())) {
                continue;
            }
            ClubMember tempClubMember = entry.getValue();
            if (!tempClubMember.isLikeGame(room.getGameType(), room.getGameSubType())) {
                continue;
            }
            if (null == result) {
                result = new ArrayList<Long>();
            }
            result.add(entry.getKey());
        }
        return result;
    }

    @Override
    public boolean hasMember(long playerUid) {
        return this.members.containsKey(playerUid);
    }

    @Override
    public boolean matchMemberType(EClubJobType memberType, long playerUid) {
        ClubMember member = getMember(playerUid);
        if (null == member) {
            return Boolean.FALSE;
        }
        return member.checkJobType(memberType);
    }

    @Override
    public void foreach(ICallback<ClubMember> callback) {
        Iterator<Map.Entry<Long, ClubMember>> it = this.members.entrySet().iterator();
        while (it.hasNext()) {
            ClubMember temp = it.next().getValue();
            // 删除和离开的人不发消息
            if (temp.getState() > 1) {
                continue;
            }
            callback.call(temp);
        }
    }

    @Override
    public void memberExtForeach(ICallback<ClubMemberExt> callback) {
        Iterator<Map.Entry<Long, ClubMemberExt>> it = this.memberExtMap.entrySet().iterator();
        while (it.hasNext()) {
            ClubMemberExt temp = it.next().getValue();
            callback.call(temp);
        }
    }

    @Override
    public int getMemberCnt() {
        return this.members.size();
    }

    @Override
    public ClubMemberExt getMemberExt(long playerUid, boolean isCreate) {
        ClubMemberExt info = this.memberExtMap.get(playerUid);
        if (isCreate && null == info) {
            info = new ClubMemberExt();
            info.setUid(UIDManager.I.getAndInc(UIDType.CLUB_MEMBER_EXT));
            info.setClubUid(this.getClubUid());
            info.setPlayerUid(playerUid);
            info.setConvert(EConvertType.NORMAL.ordinal());
            info.setDirty(Boolean.TRUE);
            this.memberExtMap.putIfAbsent(playerUid, info);
            return this.memberExtMap.get(playerUid);
        }
        return info;
    }

    @Override
    public boolean addMemberClubGold(long playerUid, int value, long optPlayerUid, EClubGoldChangeType changeType) {
        return false;
    }

    @Override
    public boolean addMemberClubRewardValue(long playerUid, int value, long optPlayerUid,
            EClubRVChangeType changeType) {
        return true;
    }

    @Override
    public void broadcast(int commandId, Object message) {
        for (Map.Entry<Long, ClubMember> entry : this.members.entrySet()) {
            Player player = PlayerManager.I.getOnlinePlayer(entry.getKey());
            if (player == null) {
                continue;
            }
            player.send(commandId, message);
        }
    }

    @Override
    public void broadcast(int commandId, Object message, long playerUid) {
        for (Map.Entry<Long, ClubMember> entry : this.members.entrySet()) {
            Player player = PlayerManager.I.getOnlinePlayer(entry.getKey());
            if (player == null) {
                continue;
            }
            if (entry.getKey() == playerUid) {
                continue;
            }
            player.send(commandId, message);
        }
    }

    @Override
    public void broadcastToAllClub(int commandId, Object message) {
        //如果没合过圈
        if (!this.checkIsJoinInMainClub()) {
            this.broadcast(commandId,message);
        } else {
            HashSet<Long> tempHashSet = new HashSet<>();//临时存club中所有玩家(合过圈的所有)
            IClub rootClub = ClubManager.I.getClubByUid(this.getFinalClubId());
            tempHashSet.addAll(rootClub.getAllMemberUids());//添加主圈所有玩家
            //添加所有子圈玩家
            List<Long> allClubUidList = new ArrayList<>();
            rootClub.fillDepthChildClubUidList(allClubUidList);
            for (Long cludUid : allClubUidList) {
                IClub club = ClubManager.I.getClubByUid(cludUid);
                if (club == null) {
                    continue;
                }
                tempHashSet.addAll(club.getAllMemberUids());
            }
            //遍历HashSet,给所有人发消息
            for (Long playerUid : tempHashSet) {
                Player player = PlayerManager.I.getOnlinePlayer(playerUid);
                if (player == null) {
                    continue;
                }
                player.send(commandId,message);
            }
        }
    }

    @Override
    public void broadcastAllLowClub(int commandId, Object message){
        if (!this.checkIsJoinInMainClub()) {
            this.broadcast(commandId,message);
            return;
        }
        HashSet<Long> tempHashSet = new HashSet<>();//临时存club中所有玩家(合过圈的所有)
        tempHashSet.addAll(this.getAllMemberUids());//添加主圈所有玩家
        //添加所有子圈玩家
        List<Long> allClubUidList = new ArrayList<>();
        this.fillDepthChildClubUidList(allClubUidList);
        for (Long cludUid : allClubUidList) {
            IClub club = ClubManager.I.getClubByUid(cludUid);
            if (club == null) {
                continue;
            }
            tempHashSet.addAll(club.getAllMemberUids());
        }
        //遍历HashSet,给所有人发消息
        for (Long playerUid : tempHashSet) {
            Player player = PlayerManager.I.getOnlinePlayer(playerUid);
            if (player == null) {
                continue;
            }
            player.send(commandId,message);
        }
    }

    @Override
    public boolean isForbidPlay(long playerUid) {
        ClubMember member = getMember(playerUid);
        if (null == member) {
            return Boolean.FALSE;
        }
        //合过圈
        if (this.checkIsJoinInMainClub()) {
            IClub rootClub;
            if (this.checkIsMainClub()) {
                rootClub = this;
            } else {
                rootClub = ClubManager.I.getClubByUid(this.getFinalClubId());
            }
            List<Long> allClubUid = new ArrayList<>();
            rootClub.fillDepthChildClubUidList(allClubUid);
            allClubUid.add(rootClub.getClubUid());
            for (Long tempClubUid : allClubUid) {
                IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
                if (tempClub == null) {
                    continue;
                }
                if (!tempClub.hasMember(playerUid)) {
                    continue;
                }
                ClubMember tempMember = tempClub.getMember(playerUid);
                if (tempMember.isForbidPlay()) {
                    return true;
                }
            }
            return false;
        } else {
            return member.isForbidPlay();
        }
    }

    @Override
    public ErrorCode copyMember(IClub club, EClubType clubType) {
        if (null == club) {
            return ErrorCode.CLUB_CARD_NOT_EXISTS;
        }
        club.foreach(members -> {
            if (members[0].getJobType() != EClubJobType.CHIEF.getType()) {
                copyMember(members[0]);
            }
        });
        return ErrorCode.OK;
    }

    private void copyMember(ClubMember member) {
        ClubMember members = new ClubMember();
        members.setUid(UIDManager.I.getAndInc(UIDType.CLUB_MEMBER));
        members.setClubUid(this.getClubUid());
        members.setPlayerUid(member.getPlayerUid());
        members.setJobType(EClubJobType.getType(member.getJobType()), true);
        members.setPrivilege(member.getPrivilege());
        members.setShowNick(member.getShowNick());
        members.setUplinePlayerUid(member.getUplinePlayerUid());
        members.setState(EClubMemberStateType.ORMAL.ordinal());
        members.setJoinTime(System.currentTimeMillis());
        this.putMembers(member.getPlayerUid(), members);
        members.setDirty(true);
        PlayerManager.I.getPlayer(member.getPlayerUid()).addClub(this);
        PlayerManager.I.getPlayer(member.getPlayerUid()).addOwnerClubCnt(true);
    }


    @Override
    public void playerEnterClub(long playerUid, long newClubUid) {
        this.playerEnterClubUidMap.put(playerUid, newClubUid);
    }

    @Override
    public long getEnterFromClubUid(long playerUid) {
        return this.playerEnterClubUidMap.containsKey(playerUid) ? this.playerEnterClubUidMap.get(playerUid) : -1;
    }

    @Override
    public ConcurrentHashMap<Long,Long> getPlayerEnterClubMap(){
        return this.playerEnterClubUidMap;
    }

    @Override
    public boolean hasRewardValue(long playerUid, int value) {
        ClubMemberExt clubMemberExt = this.getMemberExt(playerUid, false);
        if (null == clubMemberExt) {
            return 0 >= value;
        }
        return clubMemberExt.getRewardValue() >= value;
    }

    @Override
    public boolean hasGold(long playerUid, int value) {
        ClubMemberExt clubMemberExt = this.getMemberExt(playerUid, false);
        if (null == clubMemberExt) {
            return 0 >= value;
        }
        return clubMemberExt.getGold() >= value;
    }

    @Override
    public long getGold(long playerUid) {
        ClubMemberExt clubMemberExt = this.getMemberExt(playerUid, false);
        if (null == clubMemberExt) {
            return 0L;
        }
        return clubMemberExt.getGold();
    }

    @Override
    public boolean hasEnoughMoney(EMoneyType moneyType, Number costDiamond) {
        IPlayer player = getOwnerPlayer();
        return player.hasMoney(EMoneyType.DIAMOND, costDiamond);
    }
    /**
     * 根据玩家id或名称模糊查找联盟成员列表
     * @param searchContent
     * @return
     */
    @Override
    public List<PCLIPlayerSmallInfo> search(String searchContent) {
        List<PCLIPlayerSmallInfo> result = new ArrayList<PCLIPlayerSmallInfo>();
        Set<Long> tempIds = null;
        Long playerId = Longs.tryParse(searchContent);
        // 成员id精确查询
        if (playerId != null) {
            Iterator<Map.Entry<Long, ClubMember>> it = this.members.entrySet().iterator();
            while (it.hasNext()) {
                IClub club = ClubManager.I.getClubByUid(it.next().getValue().getClubUid());
                if (null == club) {
                    continue;
                }
                if (!club.hasMember(playerId)) {
                    continue;
                }
                Player player = PlayerManager.I.getPlayer(playerId);
                if (null == player) {
                    continue;
                }
                result.add(player.getPlayerSmallInfo());
                if (null == tempIds) {
                    tempIds = new HashSet<>();
                }
                tempIds.add(playerId);
            }
        }
        // 群成员名称模糊查询
        Iterator<Map.Entry<Long, ClubMember>> it = this.members.entrySet().iterator();
        while (it.hasNext()) {
            IClub club = ClubManager.I.getClubByUid(it.next().getValue().getClubUid());
            if (null == club) {
                continue;
            }
            List<Long> ids = club.getAllMemberUids();
            for (Long tempId : ids) {
                if (tempIds != null && tempIds.contains(tempId)) {
                    continue;
                }
                Player player = PlayerManager.I.getPlayer(tempId);
                if (null == player) {
                    continue;
                }
                if (player.getName().indexOf(searchContent) == -1) {
                    continue;
                }
                result.add(player.getPlayerSmallInfo());
            }
        }
        return result;
    }

    @Override
    public int allRobotCnt() {
        List<Long> tempList = new ArrayList<>();
        for (Long memberUid : this.getAllMemberUids()) {
            if (memberUid >= 300000 && memberUid <= 400000 && !tempList.contains(memberUid)) {
                tempList.add(memberUid);
            }
        }
        return tempList.size();
    }

    @Override
    public int canUseRobotCnt() {
        List<Long> tempList = new ArrayList<>();
        for (Long memberUid : this.getAllMemberUids()) {
            if (memberUid >= 300000 && memberUid <= 400000 && !usedRobotUid.contains(memberUid) && !tempList.contains(memberUid)) {
                tempList.add(memberUid);
            }
        }
        return tempList.size();
    }

    @Override
    public long getCanUseRobotUid() {
        List<Long> canUseRobotList = new ArrayList<>();
        for (Long memberUid : this.getAllMemberUids()) {
            if (memberUid >= 300000 && memberUid <= 400000 && !usedRobotUid.contains(memberUid)) {
                canUseRobotList.add(memberUid);
            }
        }
        if (canUseRobotList.size() == 0) {
            return -1;
        }
        int index = RandomUtil.random(0,canUseRobotList.size() - 1);
        return canUseRobotList.get(index);
    }

    @Override
    public void addUsedRobotUid(long robotUid) {
        if (!this.usedRobotUid.contains(robotUid)) {
            this.usedRobotUid.add(robotUid);
        }
    }

    @Override
    public void delUsedRobotUid(long robotUid) {
        if (this.usedRobotUid.contains(robotUid)) {
            this.usedRobotUid.remove(robotUid);
        }
    }

    @Override
    public boolean hasPrivilege(long memberPlayerUid, EClubPrivilege privilege) {
        return false;
    }

    @Override
    public void  removeForbid(long clubUid,EClubType type) {
        ForbidManager.I.removeForbidByMerge(clubUid, type);
    }

    @Override
    public void removeForbidByPlayerLeave(long playerUid, long clubUid, EClubType type){
        ForbidManager.I.removeForbidByPlayerLeave(playerUid,clubUid, type);
    }

    @Override
    public void setManager(long opUid, long playerUid, List<Long> clubUidList) {
        Map<Long, List<Long>> manager = this.clubInfo.getManagerInfo();
        List<Long> mangerClubs = manager.get(playerUid);
        if (manager.containsKey(playerUid)) {
            /* 玩家是管理员 */
            if (clubUidList.size() > 0) {
                /* 修改管理的俱乐部 */
                for (int i = 0; i < clubUidList.size(); i++) {
                    /* 原来的管理群中不包含设置的管理群,添加一些管理俱乐部 */
                    if (!mangerClubs.contains(clubUidList.get(i))) {
                        IClub club = ClubManager.I.getClubByUid(clubUidList.get(i));
                        if (null == club) {
                            continue;
                        }
                        club.addApplyListInfo(playerUid, opUid, EOpStateType.PROMOTION_ADMIN);
                    }
                }
                for (int i = 0; i < mangerClubs.size(); i++) {
                    /* 设置的管理群中不包含原来的管理群,删除一些管理的俱乐部 */
                    if (!clubUidList.contains(mangerClubs.get(i))) {
                        IClub club = ClubManager.I.getClubByUid(mangerClubs.get(i));
                        if (null == club) {
                            continue;
                        }
                        club.addApplyListInfo(playerUid, opUid, EOpStateType.CANCEL_ADMIN);
                    }
                }
//                if(manager.get(playerUid).size() > clubUidList.size()) {
//                    /* 如果 manager 大于 clubUidList 就是删除一些管理的俱乐部 */
//                    for (Long uid : manager.get(playerUid)) {
//                        IClub club = ClubManager.I.getClubByUid(uid);
//                        if (null == club || clubUidList.contains(uid)) {
//                            continue;
//                        }
//                        club.addApplyListInfo(playerUid, opUid, EOpStateType.CANCEL_ADMIN);
//                    }
//                }else if (manager.get(playerUid).size() < clubUidList.size()){
//                    /* 如果 manager 小于 clubUidList 就是添加一些管理俱乐部 */
//                    for (Long uid : clubUidList) {
//                        IClub club = ClubManager.I.getClubByUid(uid);
//                        if (null == club || manager.get(playerUid).contains(uid)) {
//                            continue;
//                        }
//                        club.addApplyListInfo(playerUid, opUid, EOpStateType.PROMOTION_ADMIN);
//                    }
//                }else {
//                    /* 客户端误操作 */
//                    return;
//                }
                manager.put(playerUid, clubUidList);
            } else {
                /* 删除管理员的身份 */
                for (Long uid : manager.get(playerUid)) {
                    IClub club = ClubManager.I.getClubByUid(uid);
                    if (null == club) {
                        continue;
                    }
                    club.addApplyListInfo(playerUid, opUid, EOpStateType.CANCEL_ADMIN);
                }
                manager.remove(playerUid);
            }
        } else {
            /* 玩家不是管理员 */
            if (clubUidList.size() > 0) {
                /* 设置玩家为管理员 并赋值管理的俱乐部 */
                for (Long uid : clubUidList) {
                    IClub club = ClubManager.I.getClubByUid(uid);
                    if (null == club) {
                        continue;
                    }
                    club.addApplyListInfo(playerUid, opUid, EOpStateType.PROMOTION_ADMIN);
                }
                manager.put(playerUid, clubUidList);
            } else {
                /* 客户端误操作 */
                return;
            }
        }
        this.clubInfo.setDirty(true);
    }

    @Override
    public boolean checkIsManager(long playerUid){
        Map<Long, List<Long>> map=this.clubInfo.getManagerInfo();
        return null == map ? false :map.containsKey(playerUid);
    }
    @Override
    public boolean checkIsManagerInClubByClubUid(long playerUid, long clubUid){
        Map<Long, List<Long>> map=this.clubInfo.getManagerInfo();
        if(map!=null){
            List<Long> list=map.get(playerUid);
            if(list!=null&&list.size()>0){
                return list.contains(clubUid);
            }
        }
        return false;
    }

    @Override
    public boolean checkIsDownTreasurer(long playerUid) {
        if (this.checkIsJoinInMainClub()) {
            return this.checkIsMainClub() && this.getClubInfo().getDownGoldTreasurer().contains(playerUid);
        } else {
            return this.getClubInfo().getDownGoldTreasurer().contains(playerUid);
        }
    }

    @Override
    public boolean checkIsUpTreasurer(long playerUid) {
        if (this.checkIsJoinInMainClub()) {
            return this.checkIsMainClub() && this.getClubInfo().getUpGoldTreasurer().contains(playerUid);
        } else {
            return this.getClubInfo().getUpGoldTreasurer().contains(playerUid);
        }
    }

    @Override
    public ErrorCode applyClose(EClubCloseStatus closeStatus) {
        if (EClubCloseStatus.OPEN.match(closeStatus)) {
            this.clubInfo.setOpen();
        } else if (EClubCloseStatus.CLOSING.match(closeStatus)) {
            // 打烊时，未开始的游戏桌直接踢人处理
            this.killAllIdleRoom();
            // 设置打烊中状态
            this.clubInfo.setClosing();
            // 检查是否改变打烊状态
            this.checkChangeCloseStatus();
        } else if (EClubCloseStatus.CLOSED.match(closeStatus)) {
            this.clubInfo.setClosed();
        }
        // 给该群在线玩家广播打烊状态
        noticeCloseStatus();
        return ErrorCode.OK;
    }

    private void killAllIdleRoom() {
        ConcurrentHashMap<Long, Floor> allFloor = getFloor();
        if (allFloor.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Long, Floor>> it = allFloor.entrySet().iterator();
        while (it.hasNext()) {
            Floor floor = it.next().getValue();
            if (null == floor) {
                continue;
            }
            floor.killAllIdleRoom(this);
        }
    }

    private void noticeCloseStatus() {
        PCLIClubCloseStatus message = new PCLIClubCloseStatus();
        message.status =  this.clubInfo.getCloseStatus();
        message.clubUid =  this.clubInfo.getUid();
        IClub noticeClub = this;
        if (checkIsJoinInMainClub() && !checkIsMainClub()) {
            // 房间对应所有亲友圈所有在线的玩家（合圈之后所有的在线玩家）
            noticeClub = ClubManager.I.getClubByUid(getFinalClubId());
        }
        // 通知总圈所有在线玩家
        noticeClub.broadcastToAllClub(CommandId.CLI_NTF_CLUB_CLOSE_STATUS, message);
    }

    @Override
    public boolean matchCloseStatus(EClubCloseStatus closeStatus) {
        return this.clubInfo.matchCloseStatus(closeStatus);
    }

    @Override
    public void checkChangeCloseStatus() {
        // 是否正在打烊中
        if (matchCloseStatus(EClubCloseStatus.CLOSING)) {
            ConcurrentHashMap<Long, Floor> allFloor = getFloor();
            if (allFloor.isEmpty()) {
                return;
            }
            boolean changeStatus = Boolean.TRUE;
            Iterator<Map.Entry<Long, Floor>> it = allFloor.entrySet().iterator();
            while (it.hasNext()) {
                Floor floor = it.next().getValue();
                if (null == floor) {
                    continue;
                }
                if (floor.existStartedGameDesk(this)) {
                    changeStatus = Boolean.FALSE;
                    break;
                }
            }
            if (changeStatus) {
                this.clubInfo.setClosed();
                // 给该群在线玩家广播打烊状态
                noticeCloseStatus();
            }
        }
    }

    @Override
    public void updateClubRank(ERankType rankType,long mainClubUid, long playerUid, int value, long nowTime){
        TodayStatistics item = getStatisticsByRankType(rankType,playerUid);
        if (null == item){
            return;
        }
        item.addValue(value,nowTime);
        NewRankManager.I.updateClubGameRank(rankType,mainClubUid,this.getClubUid(),playerUid,item.getValue(),nowTime);

        //下级局数，分数统计
        int gameCnt = 0;
        int gameScore = 0;
        if (rankType == ERankType.CLUB_GAME_NUM){
            gameCnt = value;
        }else if (rankType == ERankType.CLUB_GAME_SCORE){
            gameScore = value;
        }
        if (gameCnt > 0 || gameScore != 0){
            ClubMember clubMember = this.getMember(playerUid);
            if (clubMember != null && clubMember.getUplinePlayerUid() > 0) {
                DownLineGameManager.I.updateDownLineGameInfo(playerUid, clubMember.getUplinePlayerUid(), this.getClubUid(), gameCnt, gameScore, nowTime);
            }
        }
    }

    @Override
    public TodayStatistics getStatisticsByRankType(ERankType rankType, long playerUid){
        ETodayStatisticsType statisticsType = null;
        if (ERankType.CLUB_GAME_SCORE == rankType){
            statisticsType = ETodayStatisticsType.CLUB_GAME_SCORE;
        }else if (ERankType.CLUB_GAME_NUM == rankType){
            statisticsType = ETodayStatisticsType.CLUB_GAME_NUM;
        }else if (ERankType.CLUB_GAME_WINNER == rankType){
            statisticsType = ETodayStatisticsType.CLUB_GAME_WINNER;
        }

        if (null == statisticsType){
            return null;
        }

        ConcurrentHashMap<Long,TodayStatistics> tempMap = this.todayStatisticsMap.get(statisticsType);
        TodayStatistics item = tempMap.get(playerUid);
        if (null == item){
            tempMap.putIfAbsent(playerUid, TodayStatistics.create(playerUid,this.getClubUid(), statisticsType.ordinal()));
            item = tempMap.get(playerUid);
        }
        return item;
    }

    @Override
    public void clearGoldActivity() {
    }
}
