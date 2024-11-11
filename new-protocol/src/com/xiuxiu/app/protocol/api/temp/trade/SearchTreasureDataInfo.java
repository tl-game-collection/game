package com.xiuxiu.app.protocol.api.temp.trade;

public class SearchTreasureDataInfo {
    public long clubUid;
    public String sign;// md5(clubUid + key)

    @Override
    public String toString() {
        return "ChangeTreasurerInfo{" +
                "clubUid=" + clubUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}