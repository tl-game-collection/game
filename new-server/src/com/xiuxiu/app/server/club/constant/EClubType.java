package com.xiuxiu.app.server.club.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 亲友圈类型枚举
 * @author Administrator
 *
 */
public enum EClubType {
    /** 1房卡亲友圈 */
    CARD(1),
    /** 2金币亲友圈 */
    GOLD(2);

    private int type;

    EClubType(Integer type) {
        this.type = type;
    }

    private static Map<Integer, EClubType> id2type;

    static {
        id2type = new HashMap<>();
        for (EClubType type : EClubType.values()) {
            id2type.put(type.getType(), type);
        }
    }

    public static EClubType getType(Integer typeId) {
        return id2type.get(typeId);
    }

    public boolean match(EClubType type) {
        return this == type;
    }

    public boolean match(Integer key) {
        return match(getType(key));
    }

    public int getType() {
        return type;
    }

}
