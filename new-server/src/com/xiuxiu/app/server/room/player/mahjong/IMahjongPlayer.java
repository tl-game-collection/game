package com.xiuxiu.app.server.room.player.mahjong;

import com.xiuxiu.app.server.room.normal.IRoomPlayer;

import java.util.List;

public interface IMahjongPlayer extends IRoomPlayer {
    /**
     * 获取手牌
     * @return
     */
    byte[] getHandCard();

    /**
     * 添加手牌到list
     * @param list
     */
    void addHandCardTo(List<Byte> list);

    /**
     * 添加手牌到list
     * @param list
     * @param fumble
     */
    void addHandCardTo(List<Byte> list, byte fumble);

    /**
     * 添加亮牌到list
     * @param list
     */
    void addBrightCardTo(List<Byte> list);

    /**
     * 添加扣牌到list
     * @param list
     */
    void addKouCardTo(List<Byte> list);

    /**
     * 获取card第一个索引
     * @param card
     * @return
     */
    byte getCardIndex(byte card);

    /**
     * 摸牌
     * @param card
     */
    void fumble(byte card);

    /**
     * 打牌
     * @param card
     */
    void takeCard(byte card);

    /**
     * 打出癞子或痞子
     * @param card
     */
    void takeCardWithLaiZiOrPi(byte card);

    /**
     * 碰牌
     * @param card
     */
    void bumpCard(byte card);

    /**
     * 杠牌
     * @param card
     */
    void barCard(byte card);

    /**
     * 吃牌
     * @param card1
     * @param card2
     */
    void eatCard(byte card1, byte card2);
}
