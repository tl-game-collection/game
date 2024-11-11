package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubNtfTotalGoldRewardValueGet {
    public long clubUid;        // 俱乐部ID
    public long totalGold;      // 竞技分
    public long rewardValue;    // 奖励分
    public String name;

    @Override
    public String toString() {
        return "PCLIClubNtfTotalGoldRewardValueGet{" +
                "clubUid=" + clubUid +
                ", name=" + name +
                ", totalGold=" + totalGold +
                ", rewardValue=" + rewardValue +
                '}';
    }
}
