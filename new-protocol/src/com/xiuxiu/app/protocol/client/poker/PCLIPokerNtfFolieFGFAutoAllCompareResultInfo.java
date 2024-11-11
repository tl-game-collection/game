package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfFolieFGFAutoAllCompareResultInfo {
    public long initiaorPlayerUid = -1;         // 发起方玩家uid
    public boolean isWin;                       // 是否赢了
    public int value;                           // 筹码
    
    @Override
    public String toString() {
        return "PCLIPokerNtfFolieFGFAutoAllCompareResultInfo{" +
                "initiaorPlayerUid=" + initiaorPlayerUid +
                ", isWin=" + isWin +
                ", value=" + value +
                '}';
    }

}
