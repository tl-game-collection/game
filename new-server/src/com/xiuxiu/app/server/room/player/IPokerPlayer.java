package com.xiuxiu.app.server.room.player;

import com.xiuxiu.app.server.room.normal.IRoomPlayer;

import java.util.List;

public interface IPokerPlayer extends IRoomPlayer,IPokerXuanPiaoPlayer {
    /**
     * 初始化手牌
     */
    void initHandCard();
    /**
     * 添加手牌
     * @param card
     */
    void addHandCard(byte card);

    /**
     * 设置手牌
     * @param card
     * @param newCard
     */
    boolean setHandCard(byte card, byte newCard);

    /**
     * 打牌
     * @param cards
     */
    void takeCard(List<Byte> cards);

    /**
     * 获取手牌
     * @return
     */
    List<Byte> getHandCard();

    /**
     * 获取手牌数量
     * @return
     */
    byte[] getHandCardCnt();

    /**
     * 获取桌上的牌
     * @return
     */
    List<Byte> getDeskCard();

    /**
     * 验证牌
     * @param cards
     * @return
     */
    boolean verifyCard(List<Byte> cards);

    /**
     * 是否还有手牌
     * @return
     */
    boolean hasHandCard();

    /**
     * 是否有炸弹
     * @return
     */
    boolean hasBomb();

    /**
     * 是否是炸弹
     * @param card
     * @return
     */
    boolean isBomb(byte card);

    /**
     * 获取打牌数量
     * @return
     */
    int getTakeCnt();

    /**
     * 是否是当前最大值
     * @param card
     * @return
     */
    boolean isCurMaxCard(byte card);

    /**
     * 是否有card
     * @param card
     * @param cnt
     * @return
     */
    boolean hasCardCnt(byte card, int cnt);
}
