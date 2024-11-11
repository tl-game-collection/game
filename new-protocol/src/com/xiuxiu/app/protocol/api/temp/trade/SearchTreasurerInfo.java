package com.xiuxiu.app.protocol.api.temp.trade;

public class SearchTreasurerInfo {
    public long clubUid;
    public long playerUid;
    public String sign;// md5(clubUid + playerUid + key)

    @Override
    public String toString() {
        return "SearchTreasurerInfo{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
