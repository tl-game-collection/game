package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;

import java.util.ArrayList;
import java.util.List;

public class DYMJMahjongPlayer extends MahjongPlayer implements IDYMJMahjongPlayer {
    private boolean gold = false;
    private boolean prevGold = false;
    private boolean smallGold = false;
    private boolean fangTop = false;
    private int topType = -1;
    private int kaiKouCnt = 0;
    private int smallGoldPoint1 = 0;
    private int smallGoldPoint2 = 0;

    private int chengBagPlayerIndex = -1;           // 承包玩家index
    private int kaiKouCardColor = -1;               // 第3个开口的牌颜色
    private ArrayList<Integer> allQingYiSeFangBagIndex = new ArrayList<>();    // 清一色反包(反包)
    private ArrayList<Integer> allJiangYiSeFanBagIndex = new ArrayList<>();    // 将一色反包(反包)

    private boolean is258 = true;               // 将一色
    private boolean isQYS = true;               // 清一色

    public DYMJMahjongPlayer(int gameType, long roomUid, int roomId) {
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
            if (type.isEat() || !MahjongUtil.is258(cardValue)) {
                this.is258 = false;
                this.allJiangYiSeFanBagIndex.clear();
            }
            if (-1 == this.kaiKouCardColor) {
                this.kaiKouCardColor = MahjongUtil.getColor(cardValue);
            }
            if (this.kaiKouCnt > 1) {
                if (this.kaiKouCardColor != MahjongUtil.getColor(cardValue)) {
                    this.isQYS = false;
                    this.allQingYiSeFangBagIndex.clear();
                }
            }
        }
        return node;
    }

    @Override
    public void addBag(IMahjongPlayer takePlayer, byte takeCard, byte laiZi) {
        if (this.kaiKouCnt >= 3) {
            if (this.isQYS && this.kaiKouCardColor != MahjongUtil.getColor(takeCard)) {
                if (takePlayer.hasHandCardWithColor(this.kaiKouCardColor)) {
                    this.allQingYiSeFangBagIndex.remove((Integer) takePlayer.getIndex());
                    this.allQingYiSeFangBagIndex.add(takePlayer.getIndex());
                }
            }
            if (this.is258 && !MahjongUtil.is258(takeCard)) {
                if (takePlayer.hasHandCardWithout258(laiZi)) {
                    this.allJiangYiSeFanBagIndex.remove((Integer) takePlayer.getIndex());
                    this.allJiangYiSeFanBagIndex.add(takePlayer.getIndex());
                }
            }
        }
    }

    @Override
    public boolean isKaiKou(CPGNode.EType type) {
        return CPGNode.EType.BAR_AN != type && CPGNode.EType.BAR_LAIZI != type && CPGNode.EType.BAR_PI != type;
    }

    @Override
    public int getChengBagPlayerIndex() {
        return this.chengBagPlayerIndex;
    }

    @Override
    public int getChengColor() {
        return this.kaiKouCardColor;
    }

    @Override
    public boolean is258() {
        return this.is258;
    }

    @Override
    public boolean isQYS() {
        return this.isQYS;
    }

    @Override
    public List<Integer> getAllFangBagPlayerIndexWithQYS() {
        return this.allQingYiSeFangBagIndex;
    }

    @Override
    public List<Integer> getAllFangBagPlayerIndexWithJYS() {
        return this.allJiangYiSeFanBagIndex;
    }

    @Override
    public void setTopType(int value) {
        this.topType = value;
    }

    @Override
    public int getTopType() {
        return this.topType;
    }

    @Override
    public void setFangTop(boolean value) {
        this.fangTop = value;
    }

    @Override
    public boolean isFangTop() {
        return this.fangTop;
    }

    @Override
    public void setGold(boolean value) {
        this.gold = value;
    }

    @Override
    public boolean isGold() {
        return this.gold;
    }

    @Override
    public void setPrevGold(boolean value) {
        this.prevGold = value;
    }

    @Override
    public boolean isPrevGold() {
        return this.prevGold;
    }

    @Override
    public void setSmallGold(boolean value) {
        this.smallGold = value;
    }

    @Override
    public boolean isSmallGold() {
        return this.smallGold;
    }

    @Override
    public void setSmallGoldPoint(int p1, int p2) {
        this.smallGoldPoint1 = p1;
        this.smallGoldPoint2 = p2;
    }

    @Override
    public int getSmallGoldPoint1() {
        return this.smallGoldPoint1;
    }

    @Override
    public int getSmallGoldPoint2() {
        return this.smallGoldPoint2;
    }

    @Override
    public void clear() {
        super.clear();
        this.fangTop = false;
        this.gold = false;
        this.smallGold = false;
        this.kaiKouCnt = 0;
        this.chengBagPlayerIndex = -1;
        this.kaiKouCardColor = -1;
        this.isQYS= true;
        this.is258 = true;
        this.allQingYiSeFangBagIndex.clear();
        this.allJiangYiSeFanBagIndex.clear();
        this.smallGoldPoint1 = 0;
        this.smallGoldPoint2 = 0;
        this.topType = -1;
    }

    @Override
    public void clearByArenaOver() {
        super.clearByArenaOver();
        this.gold = false;
        this.prevGold = false;
    }

    @Override
    public void savePrevInfo() {
        this.prevGold = this.gold;
    }
}
