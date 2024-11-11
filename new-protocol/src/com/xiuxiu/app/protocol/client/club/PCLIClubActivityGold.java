package com.xiuxiu.app.protocol.client.club;
public class PCLIClubActivityGold {
    public int index;
    public int param;           // 打满多少局
    public int reward;          // 奖励多少分
    public int curCount;        //当前计数
    public boolean isReward;    //是否领奖

    @Override
    public String toString() {
        return "PCLIClubActivityGold{" +
                "index=" + index +
                ", param=" + param +
                ", reward=" + reward +
                '}';
    }
}
