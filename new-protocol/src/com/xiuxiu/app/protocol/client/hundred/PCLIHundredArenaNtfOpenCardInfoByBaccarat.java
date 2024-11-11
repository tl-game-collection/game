package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;
import java.util.List;

public class PCLIHundredArenaNtfOpenCardInfoByBaccarat  {
    public static class BaccaratCardInfo {
        public List<Byte> cards = new ArrayList<>();
        public int type;
        public boolean win;
        public int point;

        @Override
        public String toString() {
            return "BaccaratCardInfo{" +
                    "cards=" + cards +
                    ", type=" + type +
                    ", win=" + win +
                    ", point=" + point +
                    '}';
        }
    }

    public List<BaccaratCardInfo> data = new ArrayList<>();
    public boolean bankerValueOverFlow = false;
    public long boxId;

    @Override
    public String toString() {
        return "PCLIHundredArenaNtfOpenCardInfoByBaccarat{" +
                "data=" + data +
                ", bankerValueOverFlow=" + bankerValueOverFlow +
                ", boxId=" + boxId +
                '}';
    }

}
