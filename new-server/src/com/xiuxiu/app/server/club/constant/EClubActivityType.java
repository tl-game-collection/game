package com.xiuxiu.app.server.club.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 亲友圈活动类型枚举
 * @author Administrator
 *
 */
public enum EClubActivityType {
    /** 1奖励分分成比例活动*/
    DIVIDE(1),
    /** 2领取金币活动 */
    GOLD(2);

    private int type;

    EClubActivityType(Integer type) {
        this.type = type;
    }

    private static Map<Integer, EClubActivityType> id2type;

    static {
        id2type = new HashMap<>();
        for (EClubActivityType type : EClubActivityType.values()) {
            id2type.put(type.getType(), type);
        }
    }

    public static EClubActivityType getType(Integer typeId) {
        return id2type.get(typeId);
    }

    public boolean match(EClubActivityType type) {
        return this == type;
    }

    public boolean match(Integer key) {
        return match(getType(key));
    }

    public int getType() {
        return type;
    }

}
