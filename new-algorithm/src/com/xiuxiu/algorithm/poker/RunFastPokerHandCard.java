package com.xiuxiu.algorithm.poker;

public class RunFastPokerHandCard extends PokerHandCard {
    protected boolean fig = false;
    protected boolean eightPair = false;
    protected boolean redPeachTen = false;

    @Override
    public void init() {
        this.fig = true;
        for (int i = 8; i < 11; ++i) {
            if (this.aHandCardList[i] > 0) {
                this.fig = false;
                break;
            }
        }
//        this.eightPair = true;
//        for (int i = 0, len = this.aHandCardList.length; i < len; ++i) {
//            if (2 != this.aHandCardList[i]) {
//                this.eightPair = false;
//                break;
//            }
//        }
        int count = 0;
        for (int i = 0, len = this.aHandCardList.length; i < len; ++i) {
            if (2 == this.aHandCardList[i]) {
                ++count;
            }
        }
        this.eightPair = count == 8;
        this.redPeachTen = -1 != this.nHandCardList.indexOf(PokerUtil.TEN_RED_PEACH);
    }

    public boolean isFig() {
        return this.fig;
    }

    public boolean isEightPair() {
        return this.eightPair;
    }

    public boolean hasRedPeachTen() {
        return this.redPeachTen;
    }
}
