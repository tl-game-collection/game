package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqFolieFGFAddNoteInfo {
    public int value;                  // 加注的值
    public int fillUp;                 // 是否压满

    @Override
    public String toString() {
        return "PCLIPokerReqFolieFGFAddNoteInfo{" +
                "value=" + value +
                "fillUp=" + fillUp +
                '}';
    }
}
