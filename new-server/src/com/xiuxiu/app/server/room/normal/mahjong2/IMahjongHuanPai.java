package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.List;

public interface IMahjongHuanPai {
    /**
     * 开始换牌
     */
    void beginHuanPai();

    /**
     * 结束换牌
     */
    void endHuanPai();

    /**
     * 换牌
     * @param player
     * @param cards
     * @return
     */
    ErrorCode huanPai(IPlayer player, List<Byte> cards);

    /**
     * 通知开始换牌信息
     * @param player
     * @param card
     * @param cnt
     */
    void doSendBeginHuanPai(IMahjongPlayer player, int card, int cnt);

    /**
     * 通知结束换牌
     * @param player
     * @param type
     * @param myCard
     * @param card
     * @param cnt
     */
    void doSendEndHuanPai(IMahjongPlayer player, int type, int myCard, int card, int cnt);

    /**
     * 通知换牌信息
     * @param player
     */
    void doSendHuanPaiInfo(IMahjongPlayer player);
}
