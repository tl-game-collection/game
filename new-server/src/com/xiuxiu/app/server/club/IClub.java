package com.xiuxiu.app.server.club;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubSingleInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerSmallInfo;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.constant.*;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.floor.IFloorOwner;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.rank.ERankType;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.statistics.TodayStatistics;
import com.xiuxiu.core.ICallback;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface IClub extends IFloorOwner, IBoxOwner {

    /**
     * 初始化亲友圈相关数据
     *
     * @param clubInfo
     */
    void init(ClubInfo clubInfo);

    /**
     * 保存数据
     *
     * @return
     */
    boolean save();

    /**
     * 0点刷新逻辑
     *
     * @param now
     */
    void zero(long now);

    long getClubUid();

    EClubType getClubType();

    long getOwnerId();

    String getName();

    String getIcon();

    long getCreateTime();

    String getDesc();

    String getGameDesc();

    /**
     * 获取俱乐部对象
     */
    ClubInfo getClubInfo();

    /**
     * 修改公告
     * @param content
     * @param expireSeconds
     */
    void changeAnnouncement(String content,int expireSeconds);

    /**
     * 申请进俱乐部
     *
     * @param player 申请人
     */
    void applyJoin(Player player);

    void addApplyListInfo(long playerUid, long oPlayerUid, EOpStateType type);

    /**
     * 操作申请列表
     *
     * @param oPlayer   // 操作人
     * @param playerUid // 被操作人
     * @param op        // 操作类型 0: 同意, 1: 拒绝
     */
    boolean opApplyList(Player oPlayer, long playerUid, int op);

    /**
     * 删除俱乐部
     */
    void dissolve();

    /**************俱乐部成员相关接口**************/
    ClubMember getMember(long playerUid);

    /**
     * 是否包含某玩家
     * @param playerUid
     * @return
     */
    boolean hasMember(long playerUid);

    /**
     * 是否匹配成员类型
     * @param memberType
     * @param playerUid
     * @return
     */
    boolean matchMemberType(EClubJobType memberType, long playerUid);

    List<Long> getAllMemberUids();

    /**
     * 获取邀请的玩家列表
     * @param player
     * @param room
     */
    List<Long> getInviteMemberUids(Player player, IRoom room);

    /**
     * 获取 成员数量
     * @return
     */
    int getMemberCnt();

    /**
     * 遍历所有成员执行一些事
     * @param callback
     */
    void foreach(ICallback<ClubMember> callback);

    /**
     * 遍历所有成员附件信息执行一些事
     * @param callback
     */
    void memberExtForeach(ICallback<ClubMemberExt> callback);

    /**
     * 获取亲友圈成员扩展信息
     * @param playerUid
     * @param isCreate
     * @return
     */
    ClubMemberExt getMemberExt(long playerUid, boolean isCreate);

    /**
     * 添加成员
     *
     * @param invitorPlayerUid  // 邀请的玩家
     * @param player            // 被邀请的玩家
     * @param jobType           // 成员 EClubJobType.NORMAL
     */
    void addMember(long invitorPlayerUid, Player player, EClubJobType jobType);

    /**
     * 删除成员
     * @param playerUid 被删除的人
     * @param opPlayerUid 操作删除的人
     */
    void delMember(long playerUid, long opPlayerUid);

    /**
     *  复制俱乐部成员
     * @param club      要复制的俱乐部
     * @param clubType  俱乐部类型
     * @return          ErrorCode
     */
    ErrorCode copyMember(IClub club,EClubType clubType);

    /**
     * 改变成员状态
     * @param opUid
     * @param playerUid
     * @param state             成员状态0.正常 1.禁玩 2.删除 3.离开
     */
    void changeMemberState(long opUid, long playerUid, int state);

    /**
     * 变更成员职位
     * @param opUid
     * @param playerUid
     */
    void changeMemberJob(long opUid,long playerUid,int jobType);

    /**
     * 设置玩家成员上线
     * @param member
     * @param upLineMember
     */
    void setMemberUpLine(ClubMember member,ClubMember upLineMember);

    boolean hasPrivilege(long memberPlayerUid, EClubPrivilege privilege);
    /********************竞技分,奖励分,房卡相关接口*************************/

    boolean addMemberClubGold(long playerUid, int value, long optPlayerUid, EClubGoldChangeType changeType);

    boolean addMemberClubRewardValue(long playerUid, int value, long optPlayerUid, EClubRVChangeType changeType);

    /**
     * 判断奖励分是否足够
     * @param playerUid
     * @param value
     * @return
     */
    boolean hasRewardValue(long playerUid, int value);

    /**
     * 判断金币(竞技值)是否足够
     * @param playerUid
     * @param value
     * @return
     */
    boolean hasGold(long playerUid, int value);

    long getGold(long playerUid);

    /**
     * 判断圈主房卡是否有足够的货币
     * @param moneyType
     * @param costDiamond
     * @return
     */
    boolean hasEnoughMoney(EMoneyType moneyType, Number costDiamond);

    /********************合圈相关接口*************************/
    /**
     * 申请合并亲友圈
     */
    void applyMerge(IClub mergeClub,long nowTime);

    /**
     * 申请退出主亲友圈
     */
    void applyLeave(IClub leaveClub,long nowTime);

    /**
     * 合并俱乐部
     */
    ErrorCode mergeClub(IClub mergeClub);

    /**
     * 离开主俱乐部
     */
    ErrorCode leaveMainClub(IClub leaveClub);

    /**
     * 设置俱乐部上级
     */
    boolean setParentUidByMerge(long parentUid);

    /**
     * 设置锁定俱乐部时间
     */
    boolean setLockClub(long lockTime,long lockByClubUid);

    /**
     * 获取最终加入的亲友圈id
     * @return 如果已经合圈返回主圈clubUid，如果没有合圈返回 0
     */
    long getFinalClubId();

    /**
     * 是否是主圈
     * @return
     */
    boolean checkIsMainClub();

    /**
     *是否已经加入主圈
     */
    boolean checkIsJoinInMainClub();

    /**
     * 是否是一级圈
     * @return
     */
    boolean checkIsLevelOneClub();

    /**
     * 是否是二级圈
     * @return
     */
    boolean checkIsLevelTwoClub();

    /**
     * 获取当前亲友圈所有下级亲友圈uid（一直找到没有下级的亲友圈）
     */
    void fillDepthChildClubUidList(List<Long> uidList);

    /**
     *  俱乐部总奖励分,和总竞技分不包含child俱乐部的值
     * @return [0]奖励分总值 [1]竞技分总值
     */
    long[] getTotalGoldAndRewardValueNoChild();

    /**
     *  俱乐部总奖励分,和总竞技分包含child俱乐部的值
     * @return [0]奖励分总值 [1]竞技分总值
     */
    long[] getTotalGoldAndRewardValue();

    /**
     *  俱乐部所有成员总上下分包含child俱乐部的值
     * @return [0]总上分 [1]总下分
     */
    long[] getAllMemberTotalUpAndDownScore();

    /**
     * 设置club管理员(合圈后)
     * @param playerUid
     * @param clubUidList
     */
    void setManager(long opUid, long playerUid, List<Long> clubUidList);

    /**
     * 是不是管理职位
     */
    boolean checkIsManager(long playerUid);

    /**
     * 根据clubUid判断是不是这个圈的管理
     * @param playerUid
     * @param clubUid
     * @return
     */
    boolean checkIsManagerInClubByClubUid(long playerUid, long clubUid);

    /**
     * 判断玩家是否下分财务
     * @param playerUid
     * @return
     */
    boolean checkIsDownTreasurer(long playerUid);

    /**
     * 判断玩家是否上分财务
     * @param playerUid
     * @return
     */
    boolean checkIsUpTreasurer(long playerUid);


    void playerEnterClub(long playerUid,long newClubUid);

    /**
     * 获取玩家最后次进入该主圈的亲友圈id
     * @param playerUid
     * @return
     */
    long getEnterFromClubUid(long playerUid);

    ConcurrentHashMap<Long,Long> getPlayerEnterClubMap();

    /***************打烊功能相关接口*******************/
    /**
     * 申请打烊/开放
     * @param closeStatus
     * @param
     */
    ErrorCode applyClose(EClubCloseStatus closeStatus);
    /**
     * 是否匹配指定打烊状态
     * @param closeStatus
     * @return
     */
    boolean matchCloseStatus(EClubCloseStatus closeStatus);

    /**
     * 检查是否改变打烊状态
     * @return
     */
    void checkChangeCloseStatus();
    /***************排行榜功能相关接口*******************/
    /**
     * 更新排行榜数据
     */
    void updateClubRank(ERankType rankType, long mainClubUid, long playerUid, int value, long nowTime);
    /**
     * 获取排行榜相关的统计数据
     */
    TodayStatistics getStatisticsByRankType(ERankType rankType, long playerUid);
    /***************屏蔽功能相关接口*******************/
    /**
     * 合群时删除防作弊信息
     * @param clubUid
     * @param type
     */
    void  removeForbid(long clubUid,EClubType type);

    /**
     * 退群时删除防作弊信息
     * @param playerUid
     * @param clubUid
     * @param type
     */
     void removeForbidByPlayerLeave(long playerUid, long clubUid, EClubType type);


     boolean isForbidPlay(long playerUid);

    /***************活动相关接口*******************/
    /**
     * 清空所有被合得圈得竞技分领取活动得相关数据
     */
    void clearGoldActivity();

    /***********其他接口*********************/
    PCLIClubInfo getClubInfoPCL(Player player);

    PCLIClubSingleInfo getClubSingleInfoPCL(Player player);

    /**
     * 根据玩家id或名称模糊查找成员列表
     * @param searchContent
     * @return
     */
    List<PCLIPlayerSmallInfo> search(String searchContent);

    /**
     * 机器人总数
     * @return
     */
    int allRobotCnt();

    /**
     * 剩余可用机器人数量
     * @return
     */
    int canUseRobotCnt();

    /**
     * 获取可用的机器人uid
     * @return
     */
    long getCanUseRobotUid();

    /**
     * 添加已使用的机器人
     * @param robotUid
     */
    void addUsedRobotUid(long robotUid);

    /**
     * 删除已使用的机器人
     * @param robotUid
     */
    void delUsedRobotUid(long robotUid);

    void broadcast(int commandId, Object message);
    void broadcast(int commandId, Object message, long playerUid);
    /**
     * 广播给club中所有玩家(包括合过圈的其他圈中玩家)
     * @param commandId
     * @param message
     */
    @Deprecated
    void broadcastToAllClub(int commandId, Object message);

    /**
     * 广播给当前圈以及当前圈的所有下级圈的玩家(不包含上级圈)
     * @param commandId
     * @param message
     */
    void broadcastAllLowClub(int commandId, Object message);
}
