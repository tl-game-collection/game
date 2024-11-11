package com.xiuxiu.app.protocol.client.hundred;

public class PCLIHundredNtfRebInfoByLhd {
    public long boixId;
    public long playerUid;
    public int value;
    public int index;
    public int type;
    public int remainReb;
    public int remainRebTotal;

    @Override
    public String toString() {
        return "PCLIHundredNtfRebInfoByLhd{" +
                "boixId=" + boixId +
                ", playerUid=" + playerUid +
                ", value=" + value +
                ", index=" + index +
                ", type=" + type +
                ", remainReb=" + remainReb +
                ", remainRebTotal=" + remainRebTotal +
                '}';
    }
}
