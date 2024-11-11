package com.xiuxiu.app.protocol.client.poker;

import java.util.HashMap;

public class PCLIPokerNtfSGAllRebetInfo {
    public HashMap<Long/*playerUid*/, Integer/*rebet*/> allInfo = new HashMap<>();  // 所有已经下注过的玩家信息
    public boolean doubling = false;        // 加倍
    public int baseRebet = 0;               // 基础加注
    public int pushNote = 0;                // 推注数 0: 表示不能推注

    @Override
    public String toString() {
        return "PCLIPokerNtfSGAllRebetInfo{" +
                "allInfo=" + allInfo +
                ", doubling=" + doubling +
                ", baseRebet=" + baseRebet +
                ", pushNote=" + pushNote +
                '}';
    }
}
