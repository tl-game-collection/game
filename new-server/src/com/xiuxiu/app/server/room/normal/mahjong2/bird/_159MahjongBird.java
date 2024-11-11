package com.xiuxiu.app.server.room.normal.mahjong2.bird;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public class _159MahjongBird extends BaseMahjongBird {
    @Override
    public boolean isHit(IMahjongRoom room, IMahjongPlayer player, byte card) {
        if (card == MahjongUtil.MJ_Z_FENG) {
            ++this.allNiaoCnt;
            return true;
        }
        boolean hit = MahjongUtil.is159(card);
        if (hit) {
            ++this.hitCnt;
            player.addScore(Score.MJ_CUR_NIAO_HIT_SCORE, 1, false);
            player.addScore(Score.MJ_CUR_NIAO_HIT_NUM, 1, false);
        }
        return hit;
    }
}
