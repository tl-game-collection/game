package com.xiuxiu.app.server.box.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义包厢客户端显示类型枚举
 * 
 * @author Administrator
 *
 */
public enum EBoxShowType {
    /** 默认 */
    NORMAL(0),
    /** 竞技场包厢 */
    ARENA(1),
    /** 百人场包厢 */
    HUNDRED(2);

    private int type;

    EBoxShowType(Integer type) {
        this.type = type;
    }

    private static Map<Integer, EBoxShowType> id2type;

    static {
        id2type = new HashMap<>();
        for (EBoxShowType type : EBoxShowType.values()) {
            id2type.put(type.getType(), type);
        }
    }

    public static EBoxShowType getType(Integer typeId) {
        return id2type.get(typeId);
    }

    public boolean match(EBoxShowType type) {
        return this == type;
    }

    public boolean match(Integer key) {
        return match(getType(key));
    }

    public int getType() {
        return type;
    }

}
