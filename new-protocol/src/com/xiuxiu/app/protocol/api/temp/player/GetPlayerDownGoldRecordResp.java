package com.xiuxiu.app.protocol.api.temp.player;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetPlayerDownGoldRecordResp extends ErrorMsg {
    public static class DownGoldOrder {
        public long orderId;
        public long playerUid;
        public String playerName;
        public long clubUid;
        public int value;
        public int chargeValue;
        public String bankCard;
        public String bankCardHolder;
        public long createAt;
        public int state;
        public long optPlayerUid;

        @Override
        public String toString() {
            return "DownGoldOrder{" +
                    "orderId=" + orderId +
                    ", playerUid=" + playerUid +
                    ", playerName='" + playerName + '\'' +
                    ", clubUid=" + clubUid +
                    ", value=" + value +
                    ", chargeValue=" + chargeValue +
                    ", bankCard='" + bankCard + '\'' +
                    ", bankCardHolder='" + bankCardHolder + '\'' +
                    ", createAt=" + createAt +
                    ", state=" + state +
                    ", optPlayerUid=" + optPlayerUid +
                    '}';
        }
    }

    public List<DownGoldOrder> list = new ArrayList<>();
    public int page;
    public int pageSize;
    public boolean next;

    @Override
    public String toString() {
        return "GetPlayerDownGoldRecordResp{" +
                "list=" + list +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", next=" + next +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
