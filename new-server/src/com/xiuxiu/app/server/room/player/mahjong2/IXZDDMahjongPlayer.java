package com.xiuxiu.app.server.room.player.mahjong2;

public interface IXZDDMahjongPlayer extends IMahjongPlayer {
    /**
     * 设置花猪
     * @param value
     */
    void setHuaZhu(boolean value);

    /**
     * 是否是花猪
     * @return
     */
    boolean isHuaZhu();

    /**
     * 添加花猪分数
     * @param value
     */
    void addHuaZhuValue(int value);

    /**
     * 获取花猪分数
     * @return
     */
    int getHuaZhuValue();

    /**
     * 设置查叫
     * @param value
     */
    void setChaJiao(boolean value);

    /**
     * 是否是查叫
     * @return
     */
    boolean isChaJiao();

    /**
     * 添加查叫分数
     * @param value
     */
    void addChaJiaoValue(int value);

    /**
     * 获取查叫分数
     * @return
     */
    int getChaJiaoValue();

    /**
     * 是否有某个颜色牌(手牌+吃碰杠区域)
     * @param color
     * @return
     */
    boolean hasAllCardWithColor(int color);
}
