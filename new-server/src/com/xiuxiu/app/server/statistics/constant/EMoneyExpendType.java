package com.xiuxiu.app.server.statistics.constant;

/**
 * 房卡消耗类型
 */
public enum EMoneyExpendType {
    NORMAL(                 0, "无"),
    STORE_BUY(              1, "商城购买"),
    AGENCY_BUY(             2, "代理后台购买"),
    AGENCY_EXCHANGE(        3, "代理后台兑换"),
    AGENCY_DONATE(          4, "代理赠送"),
    AGENCY_BY_DONATE(       5, "代理被赠送"),
    RECOMMEND(              6, "推荐获得"),
    RUN_DONATE(             7, "运营后台赠送"),
    RUN_DEDUCT(             8, "运营后台扣除"),
    LOBBY_EXPEND(           9, "大厅房卡消耗"),
    LOBBY_EXPEND_RETURN(    10, "大厅房卡消耗返还"),
    GROUP_EXPEND(           11, "亲友圈房卡消耗"),
    GROUP_EXPEND_RETURN(    12, "亲友圈房卡消耗返还"),
    LEAGUE_EXPEND(          13, "大联盟房卡消耗"),
    LEAGUE_EXPEND_RETURN(   14, "大联盟房卡消耗返还"),
    CONVERSION(             15, "房卡兑换竞技分消耗"),
    CONVERSION_DONATE(      16, "房卡兑换竞技分赠送")
    ;

    private String desc;
    private int value;

    EMoneyExpendType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        return "EMoneyExpendType{" +
                "desc='" + desc + '\'' +
                ", value=" + value +
                '}';
    }
}
