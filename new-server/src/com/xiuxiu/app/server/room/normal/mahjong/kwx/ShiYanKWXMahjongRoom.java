package com.xiuxiu.app.server.room.normal.mahjong.kwx;

import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.RoomInfo;

@GameInfo(gameType = GameType.GAME_TYPE_KWX, gameSubType = 3)
public class ShiYanKWXMahjongRoom extends KWXMahjongRoom {
    public ShiYanKWXMahjongRoom(RoomInfo info) {
        super(info);
    }

    @Override
    protected int getScore(IRoomPlayer player) {
        int value = (player.getScore(Score.MJ_CALC_FANG_SCORE, false) + player.getScore(Score.MJ_CALC_GANG_SCORE, false)) * (this.isShangLou() ? 2 : 1) +
                    player.getScore(Score.MJ_CALC_HORSE_SCORE, false) +
                    player.getScore(Score.MJ_CALC_PIAO_SCORE, false) + player.getScore(Score.MJ_CALC_PQMB_SCORE, false) +
                    player.getScore(Score.MJ_CALC_SHU_KAN_SCORE, false);
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * value;
    }

    @Override
    protected int getScoreByHu(IRoomPlayer player) {
        int value = player.getScore(Score.MJ_CALC_PIAO_SCORE, false) +
                        player.getScore(Score.MJ_CALC_HORSE_SCORE, false) +
                        player.getScore(Score.MJ_CALC_FANG_SCORE, false) * (this.isShangLou() ? 2 : 1) +
                        player.getScore(Score.MJ_CALC_PQMB_SCORE, false) +
                        player.getScore(Score.MJ_CALC_SHU_KAN_SCORE, false);
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * value;
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
