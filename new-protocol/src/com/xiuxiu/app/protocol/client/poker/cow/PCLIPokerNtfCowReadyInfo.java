package com.xiuxiu.app.protocol.client.poker.cow;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfCowReadyInfo {
    public List<Long> readyPlayerUids = new ArrayList<>();        // 参与游戏的玩家uid
    public int laiZiCard;                   // 赖子牌

    @Override
    public String toString() {
        return "PCLIPokerNtfCowReadyInfo{" +
                "readyPlayerUids=" + readyPlayerUids +
                '}';
    }
}
