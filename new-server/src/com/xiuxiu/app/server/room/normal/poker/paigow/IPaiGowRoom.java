package com.xiuxiu.app.server.room.normal.poker.paigow;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;

import java.util.List;

public interface IPaiGowRoom {

    /**
     * 抢庄
     * @param player
     * @param mul
     * @return
     */
    ErrorCode onRobBank(Player player, int mul);

    /**
     * 设置庄家
     * @param maxRobBanker
     * @param max
     */
    void setMaxRobBanker(Long[] maxRobBanker, int max);

    /**
     * 开牌结束
     */
    void onOpenOver();

    /**
     * 开牌
     * @param player
     * @param card1
     * @param card2
     * @return
     */
    ErrorCode onOpenCard(Player player, List<Byte> card1, List<Byte> card2);

    /**
     * 下注
     * @param player
     * @param rebet
     * @return
     */
    ErrorCode onRebet(Player player, List<Integer> rebet);

    /**
     * 下注结束
     */
    void onRebOver();

    /**
     * 获取默认锅底
     * @param player
     * @return
     */
    int getDefaultBet(IRoomPlayer player);

    /**
     * 揭锅/切锅
     * @param player
     * @param out
     * @return
     */
    ErrorCode onHotOut(IPlayer player, boolean out);

    /**
     * 续锅
     * @param player
     * @param again
     * @param score
     * @return
     */
    ErrorCode onHotAgain(IPlayer player, boolean again, int score);
}
