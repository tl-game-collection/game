package com.xiuxiu.app.server.room.normal.mahjong.kwx;

import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongConstant;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;

import java.util.LinkedList;
import java.util.List;

@GameInfo(gameType = GameType.GAME_TYPE_KWX, gameSubType = 2)
public class XiangYangKWXMahjongRoom extends KWXMahjongRoom {
    public XiangYangKWXMahjongRoom(RoomInfo info) {
        super(info);
    }

    @Override
    protected List<Byte> barOnBump(MahjongPlayer player, byte bumpCard) {
        if (!this.isQuanPingDao()) {
            return super.barOnBump(player, bumpCard);
        }
        List<Byte> bar = null;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (0 == player.getHandCard()[i]) {
                continue;
            }
            if (i == bumpCard) {
                continue;
            }
            if (4 == player.getHandCard()[i]) {
                if (null == bar) {
                    bar = new LinkedList<>();
                }
                bar.add((byte) i);
            }
            if (3 == player.getPgCardCnt()[i] && 0 == this.paoZi[i]) {
                if (null == bar) {
                    bar = new LinkedList<>();
                }
                bar.add((byte) i);
            }
        }
        return bar;
    }

    @Override
    protected List<Byte> barOnFumble(MahjongPlayer player, byte fumbleCard) {
        if (!this.isQuanPingDao()) {
            return super.barOnFumble(player, fumbleCard);
        }
        List<Byte> bar = null;
        if (3 == player.getPgCardCnt()[fumbleCard] && (player.isBright() || 0 == this.paoZi[fumbleCard])) {
            if (null == bar) {
                bar = new LinkedList<>();
            }
            bar.add(fumbleCard);
        }
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (0 == player.getHandCard()[i]) {
                continue;
            }
            if (player.isBright() && 0 == player.getKouCard()[i]) {
                continue;
            }
            if (4 == player.getHandCard()[i] || (3 == player.getPgCardCnt()[i] && (player.isBright() || 0 == this.paoZi[i]))) {
                if (null == bar) {
                    bar = new LinkedList<>();
                }
                bar.add((byte) i);
            }
        }
        return bar;
    }

    @Override
    protected boolean isXiaoSanYuanQiDui() {
        return true;
    }

    @Override
    protected boolean isJoinBar(byte card, boolean darkBar) {
        if (!this.isQuanPingDao()) {
            return super.isJoinBar(card, darkBar);
        }
        if (this.lastFumbleCardValue == card) {
            return true;
        }
        return darkBar;
    }

    @Override
    protected boolean isHaiDiLaoYue() {
        return true;
    }

    @Override
    protected boolean isHaiDiPao() {
        return true;
    }

    @Override
    protected boolean isHuangZhuangPeiFu() {
        return true;
    }

    @Override
    protected boolean isBuyOneHorse() {
        return true;
    }

    @Override
    protected boolean isBuySixHorse() {
        return false;
    }

    @Override
    protected boolean isBuyOneGiveOneHorse() {
        return false;
    }
}
