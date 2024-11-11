package com.xiuxiu.app.server.room.normal.mahjong2.bird;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public abstract class BaseMahjongBird implements IMahjongBird {
    protected int birdCnt = 0;
    protected int hitCnt = 0;
    protected int allNiaoCnt = 0;

    @Override
    public void setCnt(int value) {
        this.birdCnt = value;
    }

    @Override
    public int getCnt() {
        return this.birdCnt;
    }

    @Override
    public int getAllNiaoCnt() {
        return this.allNiaoCnt;
    }

    @Override
    public void clear(IMahjongRoom room) {
        this.hitCnt = 0;
        this.allNiaoCnt = 0;
        for (int i = 0, len = room.getCurPlayerCnt(); i < len; ++i) {
            IRoomPlayer player = room.getRoomPlayer(i);
            if (null == player || player.isGuest()) {
                continue;
            }
            player.setScore(Score.MJ_CUR_NIAO_HIT_SCORE, 0, false);
            player.setScore(Score.MJ_CUR_NIAO_HIT_NUM, 0, false);
        }
    }

    @Override
    public boolean isHit(IMahjongRoom room, IMahjongPlayer player, byte card) {
        return false;
    }

    @Override
    public boolean isAllHit(int index1, int index2) {
        return this.hitCnt + this.allNiaoCnt == this.birdCnt;
    }

    @Override
    public boolean isAllMiss(int index1, int index2) {
        return 0 == this.hitCnt + this.allNiaoCnt;
    }

    protected int getScore(byte card) {
        int score = 0;
        if (card < MahjongUtil.MJ_D_FENG) {
            byte begin = (byte) (card - MahjongUtil.MJ_1_WANG);
            score = (begin % 9) + MahjongUtil.MJ_1_WANG;
        } else {
            score = 10;
        }
        return score;
    }
}
