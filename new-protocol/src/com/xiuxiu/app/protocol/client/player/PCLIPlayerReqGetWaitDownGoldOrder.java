package com.xiuxiu.app.protocol.client.player;

/**
 * 获取财务是否有未审核的下分订单
 * @date 2020/1/17 10:17
 * @author luocheng
 */
public class PCLIPlayerReqGetWaitDownGoldOrder {
    public long clubUid;

    @Override
    public String toString() {
        return "PCLIPlayerReqGetWaitDownGoldOrder{" +
                "clubUid=" + clubUid +
                '}';
    }
}
