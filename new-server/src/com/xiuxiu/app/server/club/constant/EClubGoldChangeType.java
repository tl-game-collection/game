package com.xiuxiu.app.server.club.constant;

public enum EClubGoldChangeType {
    NONE(-1, "[未知]"),
    INC_FROM_NULL(10, "[上分从无限库]"),
    DEC_TO_NULL(11, "[下分到无限库]"),
    INC_MANAGER(12, "[管理上分]"),
    DEC_MANAGER(13, "[管理下分]"),
    INC_MANAGER_DEC(14, "[下级上分]"),
    DEC_MANAGER_INC(15, "[下级下分]"),
    LEAGUE_REBACK(16, "[上下分失败返回]"),
    EXCHANGE_REWARD_VALUE(21, "[奖励分兑换成竞技分]"),
    EXCHANGE_REWARD_VALUE_DEC(22, "[群主活动兑换竞技分自身消耗]"),
    EXCHANGE_REWARD_VALUE_INC(23, "[成员活动兑换竞技分自身获取]"),
    EXCHANGE_CONVERT_VALUE_DEC(24, "[群主房卡兑换竞技分自身消耗]"),
    EXCHANGE_CONVERT_VALUE_INC(25, "[成员房卡兑换竞技分自身获取]"),
    EXCHANGE_CONVERT_VALUE_RET(26, "[成员房卡兑换竞技分失败返还群主]"),
    CHANGE_BY_BOX(30, "[包厢游戏产生]"),
    BACK_GROUND_CHANGE(31, "[后台添加]"),
    BACK_GROUND_RECHARGE(32, "[后台充值]"),
    INC_DOWN_TREASURER_DEC(33, "[财务下分]"),
    DEC_DOWN_TREASURER_INC(34, "[财务下分失败返回]"),
    GIVE_GOLD(35,"[赠送]"),
    ;

    private int value;
    private String desc;

    EClubGoldChangeType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static EClubGoldChangeType getDescByValue(int value) {
        for(EClubGoldChangeType type : EClubGoldChangeType.values()) {
            if(type.getValue() == value) {
                return type;
            }
        }
        return EClubGoldChangeType.NONE;
    }
}
