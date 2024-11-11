package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubMergeNtfApplyInfo {
    public long clubUid;                // 申请的俱乐部ID
    public String name;                 // 申请的俱乐部名称
    public long clubTotalGold;           // 申请的俱乐部总竞技分
    public long clubTotalRewardValue;    // 申请的俱乐部总奖励分
    public int state;                   // 状态
    public String opName;               // 操作的俱乐部名称
    public long opUid;                  // 操作的俱乐部ID
    public long opTime;                 // 操作的时间
    public int applyType;               //申请列表类型 EApplyType

    @Override
    public String toString() {
        return "PCLIClubMergeNtfApplyInfo{" +
                "clubUid=" + clubUid +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", opName='" + opName + '\'' +
                ", clubTotalGold=" + clubTotalGold +
                ", clubTotalRewardValue=" + clubTotalRewardValue +
                ", opUid=" + opUid +
                ", opTime=" + opTime +
                ", applyType=" + applyType +
                '}';
    }
}
