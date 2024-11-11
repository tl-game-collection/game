package com.xiuxiu.app.protocol.client.player;

/**
 * 获取财务是否有未审核的下分订单成功返回
 * @date 2020/1/17 10:17
 * @author luocheng
 */
public class PCLIPlayerNtfGetWaitDownGoldOrder {
    public long clubUid;
    public int waitCount;       //等待处理订单数量

    @Override
    public String toString() {
        return "PCLIPlayerNtfGetWaitDownGoldOrder{" +
                "clubUid=" + clubUid +
                ", waitCount=" + waitCount +
                '}';
    }
}
