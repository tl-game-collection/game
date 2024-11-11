package com.xiuxiu.app.server.room.player;

import com.xiuxiu.app.server.room.normal.IRoom;

public interface IHundredPlayer extends IPokerPlayer{
    long getUid();
    /**
     * 增加上庄次数
     */
    void incBankerCnt();
    /**
     * 减少上庄次数
     */
    void decBankerCnt();
    /**
     * 是否是庄家
     * @return
     */
    boolean isBanker();
    /**
     * 设置vip座位索引
     * @param index
     */
    void setVipSeatIndex(int index);
    /**
     * 获取vip座位索引
     * @return
     */
    int getVipSeatIndex();
    /**
     * 设置连续没下注的轮数
     * @param round
     */
    void setNoRebRound(int round);
    /**
     * 获取连续没下注的轮数
     * @return
     */
    int getNoRebRound();
    /**
     * 设置VIP座位下注的返利值
     * @param value
     */
    void setVipSeatFanliValue(int value);
    /**
     * 获取VIP座位下注的返利值
     * @return
     */
    int getVipSeatFanliValue();
    /**
     * 获取玩家竞技值
     * @param room
     * @return
     */
    long getGold(IRoom room);
    /**
     * 检查玩家竞技值是否足够
     * @param room
     * @param value 实际值的100倍
     * @return
     */
    boolean checkEnoughGold(IRoom room, long value);
}
