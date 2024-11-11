package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfFolieFGFAutoCompareResultInfo {
    public static class ResultInfo {
        public long winPlayerUid;                   // 比牌赢玩家uid
        public long lostPlayerUid;                  // 比牌输玩家uid

        @Override
        public String toString() {
            return "ResultInfo{" +
                    "winPlayerUid=" + winPlayerUid +
                    ", lostPlayerUid=" + lostPlayerUid +
                    '}';
        }
    }

    public List<ResultInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfFolieFGFAutoCompareResultInfo{" +
                "list=" + list +
                '}';
    }
}
