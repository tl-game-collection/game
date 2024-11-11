package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.List;

/**
 * 麻将仰牌
 */
public interface IMahjongYangPai {
    /**
     * 开始仰牌
     */
    void beginYangPai();

    /**
     * 结束仰牌
     */
    void endYangPai();

    /**
     * 仰牌
     * @param player
     * @param cards
     * @return
     */
    ErrorCode yangPai(IPlayer player, List<Byte> cards);

    /**
     * 通知开始仰牌信息
     * @param player
     */
    void doSendBeginYangPai(IMahjongPlayer player);

    /**
     * 通知仰牌信息
     * @param player
     */
    void doSendYangPaiInfo(IMahjongPlayer player, List<Byte> cards);
}
