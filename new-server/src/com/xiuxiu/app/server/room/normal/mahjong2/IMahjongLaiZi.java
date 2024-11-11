package com.xiuxiu.app.server.room.normal.mahjong2;

import java.util.Collections;
import java.util.List;

public interface IMahjongLaiZi {
    /**
     * 获取癞子
     * @return
     */
    byte getLaiZi();

    /**
     * 皮列表
     * @return
     */
    default List<Byte> getPiList() {
        return Collections.EMPTY_LIST;
    }

    /**
     * 是否是皮, 默认没有
     * @param card
     * @return
     */
    default boolean isPi(byte card) {
        return false;
    }

    /**
     * 是否癞子, 默认没有
     * @param card
     * @return
     */
    boolean isLaiZi(byte card);

    /**
     * 是否有皮子或癞子
     * @param card
     * @return
     */
    default boolean isPiOrLaiZi(byte card) {
        return this.isPi(card) || this.isLaiZi(card);
    }

    /**
     * 是否可以皮子杠, 默认不可以
     * @return
     */
    default boolean canPiBar() {
        return false;
    }

    /**
     * 是否可以癞子杠, 默认不可以
     * @return
     */
    default boolean canLaiZiBar() {
        return false;
    }
}
