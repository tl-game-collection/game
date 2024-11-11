package com.xiuxiu.app.protocol.api.temp.player;

public class GetPlayerDownGoldRecord {
    //搜索条件
    public long playerUid;
    public long orderId;
    public long clubUid;
    //筛选条件(必选)
    public int state;
    public long time;
    public int page;
    public int pageSize;
    public String sign;//playerUid + orderId + clubUid + state + time + page + pageSize + key

    @Override
    public String toString() {
        return "GetPlayerDownGoldRecord{" +
                "playerUid=" + playerUid +
                ", orderId=" + orderId +
                ", clubUid=" + clubUid +
                ", state=" + state +
                ", time=" + time +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sign='" + sign + '\'' +
                '}';
    }
}
