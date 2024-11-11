package com.xiuxiu.app.server.box.constant;

import java.util.HashMap;
import java.util.Map;


/**
 * 定义包厢类型枚举
 * @author Administrator
 *
 */
public enum EBoxType {
    /** 正常包厢 */
    NORMAL(0, 200),
    /** 自定义包厢 */
    CUSTOM(1, 200),
    /** 竞技场包厢 */
    ARENA(2, 200),
    /** 百人场包厢 */
    HUNDRED(3, 1)
    ;
    
    private int type;
    /** 每个玩法桌最多可以创建n个游戏桌 */
    private int maxRoomSize;

    EBoxType(Integer type, int size) {
        this.type = type;
        this.maxRoomSize = size;
    }

    private static Map<Integer, EBoxType> id2type;

    static {
        id2type = new HashMap<>();
        for (EBoxType type : EBoxType.values()) {
            id2type.put(type.getType(), type);
        }
    }

    public static EBoxType getType(Integer typeId) {
        return id2type.get(typeId);
    }

    public boolean match(EBoxType type) {
        return this == type;
    }

    public boolean match(Integer key) {
        return match(getType(key));
    }

    public int getType() {
        return type;
    }

    public int getMaxRoomSize() {
        return maxRoomSize;
    }
}
