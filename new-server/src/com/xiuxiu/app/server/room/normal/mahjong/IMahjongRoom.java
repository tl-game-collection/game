package com.xiuxiu.app.server.room.normal.mahjong;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.normal.IRoom;

import java.util.List;

public interface IMahjongRoom extends IRoom {
    /**
     * 选飘
     * @param player
     * @param value
     * @return
     */
    ErrorCode xuanPiao(IPlayer player, int value);

    /**
     * 数坎
     * @param player
     * @param value
     * @return
     */
    ErrorCode shuKan(IPlayer player, List<Integer> value);

    /**
     * 换牌
     * @param player
     * @param cards
     * @return
     */
    ErrorCode huanPai(IPlayer player, List<Byte> cards);

    /**
     * 甩牌
     * @param player
     * @param cards
     * @return
     */
    ErrorCode shuaiPai(IPlayer player, List<Byte> cards);

    /**
     * 定缺
     * @param player
     * @param color
     * @return
     */
    ErrorCode dingQue(IPlayer player, int color);

    /**
     * 摸牌
     * @param player
     * @return
     */
    ErrorCode fumble(IPlayer player);

    /**
     * 打牌
     * @param player
     * @param cardValue
     * @param last
     * @param index
     * @param outputCardIndex
     * @param length
     * @return
     */
    ErrorCode take(IPlayer player, byte cardValue, byte last, byte index, byte outputCardIndex, int length);

    /**
     * 亮牌
     * @param player
     * @param kou
     * @param takeCard
     * @param takeCardIndex
     * @return
     */
    ErrorCode bright(IPlayer player, List<Byte> kou, byte takeCard, byte takeCardIndex);

    /**
     * 碰牌
     * @param player
     * @param index
     * @return
     */
    ErrorCode bump(IPlayer player, byte index);

    /**
     * 杠牌
     * @param player
     * @param cardValue
     * @param startIndex
     * @param endIndex
     * @param insertIndex
     * @return
     */
    ErrorCode bar(IPlayer player, byte cardValue, byte startIndex, byte endIndex, byte insertIndex);

    /**
     * 吃
     * @param player
     * @param cardValue
     * @return
     */
    ErrorCode eat(IPlayer player, byte cardValue);

    /**
     * 胡
     * @param player
     * @return
     */
    ErrorCode hu(IPlayer player);

    /**
     * 跳过
     * @param player
     * @return
     */
    ErrorCode pass(IPlayer player);
}
