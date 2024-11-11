package com.xiuxiu.app.server.uniquecode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xx
 */
public enum EUniqueCode {
    /**
     * 群推荐码
     */
    GROUP_RECOMMEND_COED(1),

    ;

    private static Map<Integer, EUniqueCode> id2type;

    static {
        id2type = new HashMap<>();
        for (EUniqueCode type : EUniqueCode.values()) {
            id2type.put(type.getValue(), type);
        }
    }

    public static EUniqueCode getType(Integer typeId) {
        return id2type.get(typeId);
    }

    private int value;

    EUniqueCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "EUniqueCode{" +
                "value=" + value +
                '}';
    }
}
