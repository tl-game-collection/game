package com.xiuxiu.app.server.club.constant;

import java.util.HashMap;
import java.util.Map;

public enum EClubJobType {
    /** 成员 */
    NORMAL(1),
    /** 群主 */
    CHIEF(2),
    /** 副群主 */
    DEPUTY(4),
    /** 长老 */
    ELDER(8);

    private int type;

    EClubJobType(Integer type) {
        this.type = type;
    }

    private static Map<Integer, EClubJobType> id2type;

    static {
        id2type = new HashMap<>();
        for (EClubJobType type : EClubJobType.values()) {
            id2type.put(type.getType(), type);
        }
    }

    public static EClubJobType getType(Integer typeId) {
        return id2type.get(typeId);
    }

    public boolean match(EClubJobType type) {
        return this == type;
    }

    public boolean match(Integer key) {
        return match(getType(key));
    }

    public int getType() {
        return type;
    }
}