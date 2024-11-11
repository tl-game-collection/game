package com.xiuxiu.app.protocol.client.hundred;

import java.util.HashMap;

public class PCLIHundredNtfOverByLhd {
    public long boxId;
    public long roomId;
    public HashMap<Long, Integer> vipSeatArenaValue = new HashMap<>();
    public HashMap<Long, Integer> vipSeatWinOrLostArenaValue = new HashMap<>();
    public HashMap<Long, Integer> vipSeatFanliValue = new HashMap<>();
    public long bigWInPlayerUid = -1;
    public int bankerValue;
    public int myValue;

    @Override
    public String toString() {
        return "PCLIHundredNtfOverByLhd{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", vipSeatArenaValue=" + vipSeatArenaValue +
                ", vipSeatWinOrLostArenaValue=" + vipSeatWinOrLostArenaValue +
                ", vipSeatFanliValue=" + vipSeatFanliValue +
                ", bigWInPlayerUid=" + bigWInPlayerUid +
                ", bankerValue=" + bankerValue +
                ", myValue=" + myValue +
                '}';
    }
}
