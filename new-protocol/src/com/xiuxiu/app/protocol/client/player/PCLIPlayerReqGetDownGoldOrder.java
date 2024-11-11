package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqGetDownGoldOrder {
    public long clubUid;
    public int type;            //获取者 1.申请人 2.财务
    public int state;           //订单状态 0.未处理 1.已处理 2.已拒绝
    public int page;
    public int pageSize;

    @Override
    public String toString() {
        return "PCLIPlayerReqGetDownGoldOrder{" +
                "clubUid=" + clubUid +
                ", type=" + type +
                ", state=" + state +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
