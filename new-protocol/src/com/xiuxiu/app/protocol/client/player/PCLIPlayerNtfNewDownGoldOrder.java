package com.xiuxiu.app.protocol.client.player;

/**
 * 通知有新的下分订单
 * @date 2020/1/10 15:15
 * @author luocheng
 */
public class PCLIPlayerNtfNewDownGoldOrder {
    public long orderId;
    public long treasurerClubUid;       //财务所在圈id(主圈id) (有可能在不同圈担任财务，所以加此数据)

    @Override
    public String toString() {
        return "PCLIPlayerNtfNewDownGoldOrder{" +
                "orderId=" + orderId +
                ", treasurerClubUid=" + treasurerClubUid +
                '}';
    }
}
