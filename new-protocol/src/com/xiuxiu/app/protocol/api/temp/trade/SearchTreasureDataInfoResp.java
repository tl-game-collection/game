package com.xiuxiu.app.protocol.api.temp.trade;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class SearchTreasureDataInfoResp extends ErrorMsg {
    public long clubUid;
    public boolean isFreeFirst; //
    public int serviceChargePercentage; //下分服务费
    public int canDownGoldMinValue; //最低下分数量
    public String desc; //描述

    @Override
    public String toString() {
        return "SearchTreasureDataInfoResp{" +
                "clubUid=" + clubUid +
                ", freeFirst=" + isFreeFirst +
                ", serviceChargePercentage='" + serviceChargePercentage +
                ", canDownGoldMinValue='" + canDownGoldMinValue +
                '}';
    }
}
