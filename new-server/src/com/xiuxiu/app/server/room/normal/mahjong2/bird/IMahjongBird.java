package com.xiuxiu.app.server.room.normal.mahjong2.bird;

import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public interface IMahjongBird {
    /**
     * 设置鸟数量
     * @param value
     */
    void setCnt(int value);

    /**
     * 获取鸟数量
     * @return
     */
    int getCnt();

    /**
     * 获取必加鸟
     * @return
     */
    int getAllNiaoCnt();

    /**
     * 清理
     * @param room
     */
    void clear(IMahjongRoom room);

    /**
     * 启动命中
     * @return
     */
    default boolean enableHit() {
        return true;
    }

    /**
     * 是否全中
     * @return
     */
    boolean isAllHit(int index1, int index2);

    /**
     * 是否全不命中
     * @param index1
     * @param index2
     * @return
     */
    boolean isAllMiss(int index1, int index2);

    /**
     * 是否命中
     * @param room
     * @param player
     * @param card
     * @return
     */
    boolean isHit(IMahjongRoom room, IMahjongPlayer player, byte card);

    default int calcNiaoScore(IMahjongPlayer player1, IMahjongPlayer player2) {
        return this.calcNiaoScore(player1, player2, false, false, 1);
    }

    default int calcNiaoScore(IMahjongPlayer player1, IMahjongPlayer player2, int niaoScore) {
        return this.calcNiaoScore(player1, player2, false, false, niaoScore);
    }

    default int calcNiaoScore(IMahjongPlayer player1, IMahjongPlayer player2, boolean allMissIsAllHit, boolean allHitMul, int niaoScore) {
        int niaoValue = this.getAllNiaoCnt() + player1.getScore(Score.MJ_CUR_NIAO_HIT_SCORE, false) + player2.getScore(Score.MJ_CUR_NIAO_HIT_SCORE, false);
        if (this.enableHit()) {
            if (allMissIsAllHit) {
                // 全部中双全中
                if (this.isAllMiss(player1.getIndex(), player2.getIndex())) {
                    niaoValue = this.getCnt();
                }
            }
            if (allHitMul) {
                if (this.isAllHit(player1.getIndex(), player2.getIndex())) {
                    niaoValue *= 2;
                }
            }
        }
        if (niaoScore > 0) {
            niaoValue *= niaoScore;
        }
        return niaoValue;
    }

    /**
     * 获取鸟具体实现
     * @param type
     * @return
     */
    static IMahjongBird get(int type) {
        if (1 == type) {
            return new HuMahjongBird();
        } else if (2 == type) {
            return new BankerMahjongBird();
        } else if (3 == type) {
            return new _159MahjongBird();
        } else if (4 == type) {
            return new PairMahjongBird();
        } else if (5 == type) {
            return new FumbleMahjongBird();
        }
        return null;
    }
}
