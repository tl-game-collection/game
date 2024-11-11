package com.xiuxiu.app.server.room.normal.mahjong2.bird;

import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public class FumbleMahjongBird extends BaseMahjongBird {
    @Override
    public boolean enableHit() {
        return false;
    }

    @Override
    public boolean isAllHit(int index1, int index2) {
        return false;
    }

    @Override
    public boolean isAllMiss(int index1, int index2) {
        return false;
    }

    @Override
    public boolean isHit(IMahjongRoom room, IMahjongPlayer player, byte card) {
        player.addScore(Score.MJ_CUR_NIAO_HIT_SCORE, this.getScore(card), false);
        player.addScore(Score.MJ_CUR_NIAO_HIT_NUM, 1, false);
        return true;
    }
}
