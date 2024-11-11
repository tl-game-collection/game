package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;

public class YXMJMahjongPlayer extends MahjongPlayer implements IYXMJMahjongPlayer {
    private int kaiKouCnt = 0;  // 开口次数
    private int chengBagPlayerIndex = -1;           // 承包玩家index
    private boolean isCanOperate = false;         // 是否操作过(打牌、吃、碰、杠、仰)       改为(打完或者仰过牌之后不能仰)
    private int haoHua = 0; // 豪华数量
    private int takeCardCnt=0;

    public YXMJMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public CPGNode addCPG(int takePlayerIndex, CPGNode.EType type, byte cardValue) {
        CPGNode node = super.addCPG(takePlayerIndex, type, cardValue);
        if (CPGNode.EType.BAR_AN != type && CPGNode.EType.BAR_LAIZI != type && CPGNode.EType.BAR_PI != type) {
            this.addScore(Score.MJ_CUR_KAI_KOU_CNT, 1, false);
            ++this.kaiKouCnt;
            if (3 == this.kaiKouCnt) {
                this.chengBagPlayerIndex = takePlayerIndex;
            }
        }
        return node;
    }

    public void setCanOperate(boolean isCanOperate) {
        this.isCanOperate = isCanOperate;
    }

    public boolean getCanOperate() {
        return isCanOperate;
    }

    public int getHao() {
        return haoHua;
    }

    public void setHao(int haoHua) {
        this.haoHua = haoHua;
    }

    public int getTakeCardCnt() {
        return takeCardCnt;
    }

    public void addTakeCardCnt() {
        this.takeCardCnt++;
    }

    @Override
    public int getChengBagPlayerIndex() {
        return this.chengBagPlayerIndex;
    }

    @Override
    public void clear() {
        super.clear();
        this.kaiKouCnt = 0;
        this.chengBagPlayerIndex = -1;
        this.isCanOperate = false;
        takeCardCnt=0;
    }
}
