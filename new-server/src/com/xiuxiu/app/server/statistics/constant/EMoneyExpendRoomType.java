package com.xiuxiu.app.server.statistics.constant;

/**
 *  房卡消耗渠道类型
 *  此类型更具后台对应()
 */
public enum EMoneyExpendRoomType {
    NORMAL(         0, "无"),
    BUY(            1, "购买房卡"),
    CONVERT(        2, "兑换房卡"),
    LOBBY(          3, "大厅"),
    GROUP(          4, "亲友圈"),
    LEAGUE(         5, "联盟"),
    DONOR(          6, "赠送房卡"),
    GIVING(         7, "被赠送房卡");
    private String desc;
    private int value;

    EMoneyExpendRoomType(int value, String desc) {
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
