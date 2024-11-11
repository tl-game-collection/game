package com.xiuxiu.app.protocol.client.poker;

/**
 *
 */
public class PCLIPokerNtfPlayerPrimulaResult {
    public int bankerIndex;
    public long bankerUid;
    public boolean isPrimula;

    @Override
    public String toString() {
        return "PCLIPokerNtfPlayerPrimulaResult{" +
                "bankerIndex=" + bankerIndex +
                ", bankerUid=" + bankerUid +
                ", isPrimula=" + isPrimula +
                '}';
    }
}
