package com.xiuxiu.app.server.room.player.mahjong2;

public interface IYCXLMahjongPlayer extends IMahjongPlayer {
    /**
     * 设置查大叫
     * @param chaDaJiao
     */
    void setChaDaJiao(boolean chaDaJiao);

    /**
     * 是否查大叫
     * @return
     */
    boolean isChaDaJiao();

    /**
     * 设置查花猪
     * @param chaHuaZhu
     */
    void setChaHuaZhu(boolean chaHuaZhu);

    /**
     * 是否查花猪
     * @return
     */
    boolean isChaHuZhu();

    /**
     * 添加查大叫/查花猪分数
     * @param value
     */
    void addChaValue(int value);

    /**
     * 获取查大叫/查花猪分数
     * @return
     */
    int getChaValue();
}
