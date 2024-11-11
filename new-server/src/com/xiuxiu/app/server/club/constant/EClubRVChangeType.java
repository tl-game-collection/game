package com.xiuxiu.app.server.club.constant;

import java.util.HashMap;
import java.util.Map;

public enum EClubRVChangeType {
    EXCHANGE_VALUE_DEC(1), //兑换成竞技值扣除
    CLUB_SERVICE_CHARGE_INC(2),  //群管理费添加
    UP_LINE_INC(3),    //一条线抽成添加
    DIRECTLY_UNDER_INC(4), //直属抽成添加
    CLUB_LEADER_INC(5), //其他抽成都是群主的
    CLUB_LEVEL_SERVICE_CHARGE_INC(6),  //主圈群管理费添加
    CLUB_LEVEL_ONE_SERVICE_CHARGE_INC(7),  //一级圈群管理费添加
    ;

    private int type;

    EClubRVChangeType(Integer type) {
        this.type = type;
    }

    private static Map<Integer, EClubRVChangeType> id2type;

    static {
        id2type = new HashMap<>();
        for (EClubRVChangeType type : EClubRVChangeType.values()) {
            id2type.put(type.getType(), type);
        }
    }

    public static EClubRVChangeType getType(Integer typeId) {
        return id2type.get(typeId);
    }


    public int getType() {
        return type;
    }

}
