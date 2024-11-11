package com.xiuxiu.app.protocol.client.club;

import com.xiuxiu.app.protocol.client.PCLIPlayerBriefInfo;

/**
 *
 */
public class PCLIClubMemberInfo {
    public PCLIPlayerBriefInfo info;            // 成员基本信息
    public int showNick;                        // 显示昵称
    public int jobType;                         // 职位类型,
    public long uplinePlayerUid;                // 上线玩家uid
    public int forbidPlay;                      // 禁玩
    public long joinTime;                       // 加入群时间
    public long arenaValue;
    public long convert;
    public int divide;
    public int divideLine;
    public int onlyUpLineSetGold;             //是否只有上级才能上下分
    public long code;   //推荐码
    public boolean isUpGoldTreasurer;       //是否是上分财务
    public boolean isDownGoldTreasurer;       //是否是下分财务

    /**
     * 获取分成比例-成员
     */
    public int member;
    /**
     * 获取分成比例-一条线
     */
    public int line;

    /**
     * 下级个数
     */
    public int downLineNum;


    @Override
    public String toString() {
        return "PCLIClubMemberInfo{" +
                "info=" + info +
                ", showNick=" + showNick +
                ", jobType=" + jobType +
                ", uplinePlayerUid=" + uplinePlayerUid +
                ", forbidPlay=" + forbidPlay +
                ", joinTime=" + joinTime +
                ", arenaValue=" + arenaValue +
                ", convert=" + convert +
                ", divide=" + divide +
                ", divideLine=" + divideLine +
                ", onlyUpLineSetGold=" + onlyUpLineSetGold +
                ", code=" + code +
                ", isUpGoldTreasurer=" + isUpGoldTreasurer +
                ", isDownGoldTreasurer=" + isDownGoldTreasurer +
                ", member=" + member +
                ", line=" + line +
                ", downLineNum=" + downLineNum +
                '}';
    }
}
