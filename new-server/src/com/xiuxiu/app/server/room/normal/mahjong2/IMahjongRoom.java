package com.xiuxiu.app.server.room.normal.mahjong2;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.List;

public interface IMahjongRoom extends IRoom {
    /**
     * 获取色子1
     * @return
     */
    int getCrap1();

    /**
     * 获取色子2
     * @return
     */
    int getCrap2();

    /**
     * 获取手牌最后一张牌
     * @return
     */
    default byte getCanTakeHandCard(IMahjongPlayer player) {
        return this.getCanTakeHandCard(player, (byte) -1);
    }

    /**
     * 获取手牌最后一张牌
     * @param defaultCard
     * @return
     */
    default byte getCanTakeHandCard(IMahjongPlayer player, byte defaultCard) {
        if (this.isCanTakeCard(player, defaultCard)) {
            return defaultCard;
        }

        for (int i = MahjongUtil.MJ_CARD_KINDS - 1; i >= 0; --i) {
            if (player.hasHandCard((byte) i, 1) && this.isCanTakeCard(player, (byte) i)) {
                return (byte) i;
            }
        }
        return player.getLastFumbleCard();
    }

    /**
     * 是否可以打
     * @param player
     * @param card
     * @return
     */
    default boolean isCanTakeCard(IMahjongPlayer player, byte card) {
        if (card < MahjongUtil.MJ_1_WANG || card > MahjongUtil.MJ_CARD_KINDS) {
            return false;
        }
        return player.hasHandCard(card, 1);
    }

    /**
     * 是否最后一张牌选择操作
     * @return
     */
    default boolean isLastCardSelect() {
        return false;
    }

    /**
     * 是否可以多个胡, 默认是多胡
     * @return
     */
    default boolean isMoreHu() {
        return true;
    }

    /**
     * 是否必胡
     * @return
     */
    default boolean isMustHu() {
        return false;
    }

    /**
     * 是否起手胡
     * @param player
     * @return
     */
    default boolean isStartHu(IMahjongPlayer player) {
        return false;
    }

    /**
     * 是否计数杠分
     * @param takePlayer
     * @param player
     * @param type
     * @param barCard
     * @return
     */
    default boolean isCalcBarScore(IMahjongPlayer takePlayer, IMahjongPlayer player, EBarType type, byte barCard) {
        return true;
    }

    /**
     * 执行摸牌
     * @param player
     */
    void onFumble(IMahjongPlayer player);

    /**
     * 打牌
     * @param player
     * @param param
     * @return
     */
    ErrorCode take(IPlayer player, Object... param);

    /**
     * 执行打牌
     * @param takePlayer
     * @param auto
     * @param param
     */
    void onTake(IMahjongPlayer takePlayer, boolean auto, Object... param);

    /**
     * 碰牌
     * @param player
     * @param param
     * @return
     */
    ErrorCode bump(IPlayer player, Object... param);

    /**
     * 执行碰
     * @param takePlayer
     * @param player
     * @param param
     */
    void onBump(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param);

    /**
     * 杠牌
     * @param player
     * @param param
     * @return
     */
    ErrorCode bar(IPlayer player, Object... param);

    /**
     * 执行杠
     * @param takePlayer
     * @param player
     * @param param
     */
    void onBar(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param);

    /**
     * 吃牌
     * @param player
     * @param param
     * @return
     */
    ErrorCode eat(IPlayer player, Object... param);

    /**
     * 执行吃
     * @param takePlayer
     * @param player
     * @param param
     */
    void onEat(IMahjongPlayer takePlayer, IMahjongPlayer player, Object... param);

    /**
     * 胡牌
     * @param player
     * @return
     */
    ErrorCode hu(IPlayer player);

    /**
     * 执行胡
     * @param takePlayer
     * @param player1
     * @param huCard
     */
    void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, byte huCard);

    /**
     * 执行胡
     * @param takePlayer
     * @param player1
     * @param player2
     * @param huCard
     */
    void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, byte huCard);

    /**
     * 执行胡
     * @param takePlayer
     * @param player1
     * @param player2
     * @param player3
     * @param huCard
     */
    void onHu(IMahjongPlayer takePlayer, IMahjongPlayer player1, IMahjongPlayer player2, IMahjongPlayer player3, byte huCard);

    /**
     * 跳过
     * @param player
     * @return
     */
    ErrorCode pass(IPlayer player);

    /**
     * 执行跳过
     */
    void onPass();

    /**
     * 报听
     * @param player
     * @param param
     * @return
     */
    ErrorCode ting(IPlayer player, Object... param);

    /**
     * 执行报听
     * @param takePlayer
     * @param auto
     * @param param
     */
    void onTing(IMahjongPlayer takePlayer, boolean auto, Object... param);

    /**
     * 荒庄
     */
    void onHuangZhuang(boolean next);

    /**
     * 判断是否是当前动作操作者
     * @param player
     * @return
     */
    boolean isCurAction(IMahjongPlayer player, EActionOp op);

    /**
     * 通知摸牌信息
     * @param player
     * @param card
     */
    void doSendFumble(IMahjongPlayer player, byte card);

    /**
     * 通知可以打牌信息
     * @param player
     * @param broadcast
     */
    void doSendCanTake(IMahjongPlayer player, boolean broadcast);

    /**
     * 通知可以打牌信息
     * @param player
     * @param broadcast
     */
    void doSendCanTing(IMahjongPlayer player, boolean broadcast);

    /**
     * 通知可以操作信息
     * @param player
     * @param hu
     * @param bar
     * @param bump
     * @param eat
     * @param takeCard
     */
    void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat, byte takeCard);

    /**
     * 通知可以操作信息
     * @param player
     * @param hu
     * @param bar
     * @param bump
     * @param eat
     * @param takeCard
     */
    void doSendCanOperate(IMahjongPlayer player, boolean hu, boolean bar, boolean bump, boolean eat, List<Byte> takeCard);
 
    
}
