package com.xiuxiu.app.protocol.client.player;

/**
 * 获取club成员在本圈中下分所需相关参数
 * @date 2020/1/19 14:42
 * @author luocheng
 */
public class PCLIPlayerNtfGetDownGoldTime {
    public long clubUid;
    public long time;       //剩余时间戳
    public boolean isFreeFirst; //
    public int serviceChargePercentage; //下分服务费
    public int canDownGoldMinValue; //最低下分数量
    public String desc; //描述
    public long lastTime; //上次下订单的时间

    @Override
    public String toString() {
        return "PCLIPlayerNtfGetDownGoldTime{" +
                "clubUid=" + clubUid +
                ", time=" + time +
                ", isFreeFirst=" + isFreeFirst +
                ", serviceChargePercentage='" + serviceChargePercentage +
                ", canDownGoldMinValue='" + canDownGoldMinValue +
                ", desc='" + desc + '\'' +
                ", lastTime=" + lastTime +
                '}';
    }
}
