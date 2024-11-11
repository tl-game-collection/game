package com.xiuxiu.app.protocol.client.player;

import java.util.HashMap;

public class PCLIPlayerReqMineRedPacketSetConf {
    public long groupUid; // 群UID
    public int minAmount; // 红包最小金额，分为单位
    public int maxAmount; // 红包最大金额，分为单位
    public HashMap<Integer, Integer> types = new HashMap<>(); // 红包种类，1-10包1倍，2-9包1.2倍，3-8包1.4倍，4-7包1.6倍，5-6包1.8倍，6-6包2倍
    public HashMap<Integer, Integer> specialAwards = new HashMap<>(); // 特殊奖励，Key代表类型（1-豹子，2-顺子，3-1分钱），Value代表奖励多少分钱
    public int costModel;                            // 抽水类型
    public int costModelValue;                       // 抽水

    @Override
    public String toString() {
        return "PCLIPlayerReqMineRedPacketSetConf{" +
                "groupUid=" + groupUid +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                ", types=" + types +
                ", specialAwards=" + specialAwards +
                ", costModel=" + costModel +
                ", costModelValue=" + costModelValue +
                '}';
    }
}
