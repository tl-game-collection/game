package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfFolieFGFNoteInfo {
    public long notePlayerUid;          // 下注玩家uid
    public int value;                   // 下注的值
    public boolean isLook;              // 是否看牌
    public int fillUp;                  // 是否压满
    public boolean isFollow;            // 是否跟注

    @Override
    public String toString() {
        return "PCLIPokerNtfFolieFGFNoteInfo{" +
                "notePlayerUid=" + notePlayerUid +
                ", value=" + value +
                ", isLook=" + isLook +
                ", fillUp=" + fillUp +
                ", isFollow=" + isFollow +
                '}';
    }
}
