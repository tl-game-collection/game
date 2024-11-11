package com.xiuxiu.app.server.room.player.mahjong2;

public interface IDingQue {
    /**
     * 设置缺一门颜色
     * @param color
     * @return
     */
    int setQue(int color);

    /**
     * 获取缺
     * @return
     */
    int getQue();

    /**
     * 判断是否是缺一门牌
     * @param card
     * @return
     */
    boolean isQueCard(byte card);

    /**
     * 判断手牌是否有缺的牌
     * @return
     */
    boolean hasQueCard();
}
