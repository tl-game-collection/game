package com.xiuxiu.app.protocol.api.temp.trade;

public class ChangeTreasurerDataInfo {
    public long clubUid;
    public boolean isFreeFirst; //
    public int serviceChargePercentage; //下分服务费
    public int canDownGoldMinValue; //最低下分数量
    public String desc; //描述
    public String sign;// md5(clubUid + serviceChargePercentage + key)

    @Override
    public String toString() {
        return "ChangeTreasurerInfo{" +
                "clubUid=" + clubUid +
                ", isFreeFirst=" + isFreeFirst +
                ", serviceChargePercentage='" + serviceChargePercentage +
                ", canDownGoldMinValue='" + canDownGoldMinValue +
                ", desc='" + desc + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}