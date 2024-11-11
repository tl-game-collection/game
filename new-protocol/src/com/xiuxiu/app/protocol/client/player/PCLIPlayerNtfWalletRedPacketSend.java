package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfWalletRedPacketSend {
    public long playerUid;          // 发起转账人Uid
    public int playerCurrentMoney;  // 发起转账人剩余金额 * 100
    public long groupUid;           // 接收转账群Uid
    public String groupName;        // 接收转账群昵称
    public String groupIcon;        // 接收转账群的头像
    public int amount;              // 红包总金额 * 100
    public int count;               // 红包总数量
    public long redPacketUid;       // 红包记录Uid
    public String content;          // 转账备注

    @Override
    public String toString() {
        return "PCLIPlayerNtfWalletRedPacketSend{" +
                "playerUid=" + playerUid +
                ", playerCurrentMoney=" + playerCurrentMoney +
                ", groupUid=" + groupUid +
                ", groupName='" + groupName + "\'" +
                ", groupIcon='" + groupIcon + "\'" +
                ", amount=" + amount +
                ", count=" + count +
                ", redPacketUid=" + redPacketUid +
                ", content='" + content + "\'" +
                '}';
    }
}
