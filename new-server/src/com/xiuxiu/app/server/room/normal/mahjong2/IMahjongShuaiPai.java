package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.List;

public interface IMahjongShuaiPai {
    /**
     * 开始甩牌
     */
    void beginShuaiPai();

    /**
     * 结束甩牌
     */
    void endShuaiPai();

    /**
     * 甩牌
     * @param player
     * @param cards
     * @return
     */
    ErrorCode shuaiPai(IPlayer player, List<Byte> cards);

    /**
     * 通知开始甩牌信息
     * @param player
     * @param card
     * @param cnt
     */
    void doSendBeginShuaiPai(IMahjongPlayer player, int card, int cnt);

    /**
     * 通知结束甩牌
     */
    void doSendEndShuaiPai();

    /**
     * 通知甩牌信息
     * @param player
     * @param card
     * @param cnt
     */
    void doSendShuaiPaiInfo(IMahjongPlayer player, int card, int cnt);
}
