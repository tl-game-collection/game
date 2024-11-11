package com.xiuxiu.app.server.constant;

public enum EMoneyType {
    NORMAL("无", 0),              // 无
    DIAMOND("钻石", 1),           // 钻石
    GOLD("金币", 2),              // 金币
    REDPACKEG("红包", 3),         // 红包
    COUPON("礼券", 4),            // 礼券
    MONEY("现金", 5),             // 现金
    XX("休休豆", 6),              // 休休豆
    TICKET("门票", 7),            // 门票`
    WALLET("钱包", 8),            // 钱包
    WALLETBANK("钱包银行", 9),    // 钱包银行
    LEAGUE("联盟", 10),           // 联盟
    ARENAVALUE("竞技值", 11),      // 竞技值
    LEAGUE_SERVICE("联盟服务费", 12),// 联盟服务费
    LEAGUE_MANAGER("联盟管理费", 13),// 联盟管理费
    MAX("zuida", 14),               // 最大
    ;

    private String desc;
    private int value;

    EMoneyType(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return "EMoneyType{" +
                "desc='" + desc + '\'' +
                ", value=" + value +
                '}';
    }
}
