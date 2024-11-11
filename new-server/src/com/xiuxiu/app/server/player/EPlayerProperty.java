package com.xiuxiu.app.server.player;

public enum EPlayerProperty {
    NONE(0, "占位"),              // 占位
    DIAMOND(1, "钻石"),           // 钻石
    GOLD(2, "金币"),              // 金币
    REDPACKEG(3, "红包"),         // 红包
    COUPON(4, "礼券"),            // 礼券
    MONEY(5, "现金"),             // 现金
    XX(6, "休休豆"),              // 休休豆
    TICKET(7, "门票"),            // 门票
    WALLET(8, "钱包"),            // 钱包
    WALLETBANK(9, "钱包银行"),    // 钱包银行
    LEAGUE(10, "联盟"),           // 联盟
    //ARENAVALUE(11, "竞技值"),     // 竞技值

    ROOM_UID(11, "房间uid"),
    ARENA_UID(12, "竞技场uid"),
    PLAY_FIELD_UID(13, "比赛场uid"),
    GAME_TYPE(14, "游戏类型"),

    RECOMMEND_PLAYER_UID(24, "推荐用户uid"),
    PRIVILEGE(25, "权限"),
    ;

    private int type;
    private String desc;

    EPlayerProperty(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "EPlayerProperty{" +
                "type=" + type +
                ", desc='" + desc + '\'' +
                '}';
    }
}
