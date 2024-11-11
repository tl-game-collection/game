package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfGetDownGoldOrder {
    public static class DownGoldOrder {
        public long orderId;
        public long clubUid;
        public int value;
        public int chargeValue;
        public long createAt;          //订单创建时间(当天零点)
        public long createAtDetail;    //订单创建具体时间
        public long optAt;             //订单处理时间(当天零点)
        public long optAtDetail;       //订单处理具体时间
        public long playerUid;
        public String playerName;
        public long optPlayerUid;
        public String optName; //操作人姓名
        public String bankCard;
        public String bankCardHolder;
        public int state;

        @Override
        public String toString() {
            return "DownGoldOrder{" +
                    "orderId=" + orderId +
                    ", clubUid=" + clubUid +
                    ", value=" + value +
                    ", chargeValue=" + chargeValue +
                    ", createAt=" + createAt +
                    ", createAtDetail=" + createAtDetail +
                    ", optAt=" + optAt +
                    ", optAtDetail=" + optAtDetail +
                    ", playerUid=" + playerUid +
                    ", playerName='" + playerName + '\'' +
                    ", optPlayerUid=" + optPlayerUid +
                    ", optName='" + optName + '\'' +
                    ", bankCard='" + bankCard + '\'' +
                    ", bankCardHolder='" + bankCardHolder + '\'' +
                    ", state=" + state +
                    '}';
        }
    }

    public int type;            //获取者 1.申请人 2.财务
    public int state;           //订单状态 0.未处理 1.已处理 2.已拒绝
    public int waitCount;       //未处理的订单数量
    public int page;
    public int pageSize;
    public boolean next;
    public List<DownGoldOrder> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPlayerNtfGetDownGoldOrder{" +
                "type=" + type +
                ", state=" + state +
                ", waitCount=" + waitCount +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}
