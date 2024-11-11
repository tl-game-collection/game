package com.xiuxiu.app.server.club.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 亲友圈打烊状态枚举
 * @author Administrator
 *
 */
public enum EClubCloseStatus {
    /** 0开放*/
    OPEN(0),
    /** 1打烊中 */
    CLOSING(1),
    /** 已打烊中 */
    CLOSED(2);

    private int type;

    EClubCloseStatus(Integer type) {
        this.type = type;
    }

    private static Map<Integer, EClubCloseStatus> id2type;

    static {
        id2type = new HashMap<>();
        for (EClubCloseStatus type : EClubCloseStatus.values()) {
            id2type.put(type.getType(), type);
        }
    }

    public static EClubCloseStatus getType(Integer typeId) {
        return id2type.get(typeId);
    }

    public boolean match(EClubCloseStatus type) {
        return this == type;
    }

    public boolean match(Integer key) {
        return match(getType(key));
    }

    public int getType() {
        return type;
    }

}
