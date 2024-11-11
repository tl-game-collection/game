package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.room.normal.mahjong2.EPaiXing;

import java.util.List;

public interface ICSMJMahjongPlayer extends IMahjongPlayer, IXuanZeng {
    /**
     * 弃胡
     * @return
     */
    boolean isPassHu();

    /**
     * 是否开杠
     * @return
     */
    boolean isOpenBar();

    /**
     * 设置开杠
     * @param value
     */
    void  setOpenBar(boolean value);

    /**
     * 添加起手胡牌型
     * @param paiXing
     */
    void addStartHuPaiXing(EPaiXing paiXing);

    /**
     * 清理中途胡牌型
     */
    void clearMiddleHuPaiXing();

    /**
     * 添加中途胡牌型
     * @param paiXing
     */
    void addMiddleHuPaiXing(EPaiXing paiXing);

    /**
     * 是否
     * @param paiXing
     * @return
     */
    boolean hasStartHuPaiXing(EPaiXing paiXing);

    /**
     * 添加起手胡牌型到
     * @return
     */
    void addStartHuPaiXingTo(List<Integer> toStartHuPaiXing);

    /**
     * 添加中途胡牌型到
     * @param toMiddleHuPaiXing
     */
    void addMiddleHuPaiXingTo(List<Integer> toMiddleHuPaiXing);
}
