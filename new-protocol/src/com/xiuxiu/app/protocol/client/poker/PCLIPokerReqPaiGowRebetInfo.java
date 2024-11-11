package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerReqPaiGowRebetInfo {
    public long playerUid;
    public List<Integer> rebetMap = new ArrayList();

    @Override
    public String toString() {
        return "PCLIPokerReqPaiGowRebetInfo{" +
                "playerUid=" + playerUid +
                ", rebetMap=" + rebetMap +
                '}';
    }
}
