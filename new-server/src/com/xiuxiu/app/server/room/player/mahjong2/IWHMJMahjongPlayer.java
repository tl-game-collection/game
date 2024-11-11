package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;

import java.util.List;

public interface IWHMJMahjongPlayer extends IMahjongPlayer {
    /**
     * 设置封顶类型
     * @param value
     */
    void setTopType(int value);

    /**
     * 获取封顶类型
     * @return
     */
    int getTopType();

    /**
     * 设置番封顶
     * @param value
     */
    void setFangTop(boolean value);

    /**
     * 获取番封顶
     * @return
     */
    boolean isFangTop();

    /**
     * 设置当前局是否金顶
     * @param value
     */
    void setGold(boolean value);

    /**
     * 获取当前局是否金顶
     * @return
     */
    boolean isGold();

    /**
     * 设置上一把是否是金顶
     * @param value
     */
    void setPrevGold(boolean value);

    /**
     * 获取上一把是否是金顶
     * @return
     */
    boolean isPrevGold();

    /**
     * 设置当前局小金顶
     * @param value
     */
    void setSmallGold(boolean value);

    /**
     * 获取当前局小金顶
     * @return
     */
    boolean isSmallGold();

    /**
     * 设置小金顶点数
     * @param p1
     * @param p2
     */
    void setSmallGoldPoint(int p1, int p2);

    /**
     * 获取小金顶点数1
     * @return
     */
    int getSmallGoldPoint1();

    /**
     * 获取小金顶点数1
     * @return
     */
    int getSmallGoldPoint2();

    /**
     * 判断是否开口
     * @param type
     * @return
     */
    boolean isKaiKou(CPGNode.EType type);

    /**
     * 添加陪包
     * @param takePlayer
     * @param takeCard
     * @param laiZi
     */
    void addBag(IMahjongPlayer takePlayer, byte takeCard, byte laiZi);

    /**
     * 获取承包玩家index
     * @return
     */
    int getChengBagPlayerIndex();

    /**
     * 承包颜色
     * @return
     */
    int getChengColor();

    /**
     * 是否承将一色
     * @return
     */
    boolean is258();

    /**
     * 是否清一色
     * @return
     */
    boolean isQYS();

    /**
     * 获取所有反包玩家index(清一色)
     * @return
     */
    List<Integer> getAllFangBagPlayerIndexWithQYS();

    /**
     * 获取所有反包玩家index(将一色)
     * @return
     */
    List<Integer> getAllFangBagPlayerIndexWithJYS();
}
