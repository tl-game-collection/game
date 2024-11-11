package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqChangeMoney {
    // 货币类型
    //NORMAL("无", 0),              // 无
    //DIAMOND("钻石", 1),           // 钻石
    //GOLD("金币", 2),              // 金币
    //REDPACKEG("红包", 3),         // 红包
    //COUPON("礼券", 4),            // 礼券
    //MONEY("现金", 5),             // 现金
    //XX("休休豆", 6),              // 休休豆
    //TICKET("门票", 7),            // 门票
    //WALLET("钱包", 8),            // 钱包
    //WALLETBANK("钱包银行", 9),    // 钱包银行
    //LEAGUE("联盟", 10),           // 联盟
    //ARENAVALUE("竞技值", 11),      // 竞技值
    //LEAGUE_SERVICE("联盟服务费", 12),// 联盟服务费
    //LEAGUE_SERVICE("联盟服务费", 13),// 联盟管理费
    public int fromType;            // 源货币类型
    public int toType;              // 目标获取类型
    public int value;               // 值
    public long fromGroupUid = -1;  // 如果是竞技值则源群uid
    public long toGroupUid = -1;    // 如果目标是竞技值则目标群uid

    @Override
    public String toString() {
        return "PCLIPlayerReqChangeMoney{" +
                "fromType=" + fromType +
                ", toType=" + toType +
                ", value=" + value +
                ", fromGroupUid=" + fromGroupUid +
                ", toGroupUid=" + toGroupUid +
                '}';
    }
}
