package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfSetDownGoldOrder {
    public long orderId; //订单号
    public int state;    //订单状态 1.通过 2.拒绝

    @Override
    public String toString() {
        return "PCLIPlayerNtfSetDownGoldOrder{" +
                "orderId=" + orderId +
                ", state=" + state +
                '}';
    }
}
