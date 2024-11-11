package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfWalletTransfer {
    public long playerUid;          // 发起转账人Uid
    public int playerCurrentMoney;  // 发起转账人剩余金额 * 100
    public long targetUid;          // 接收转账人Uid
    public String targetNickname;   // 接收转账人昵称
    public String targetAvatar;     // 接收转账人的头像
    public int transferAmount;      // 转账金额 * 100
    public long transferLogUid;     // 转账记录Uid
    public String content;          // 转账备注

    @Override
    public String toString() {
        return "PCLIPlayerNtfWalletTransfer{" +
                "playerUid=" + playerUid +
                ", playerCurrentMoney=" + playerCurrentMoney +
                ", targetUid=" + targetUid +
                ", targetNickname='" + targetNickname + "\'" +
                ", targetAvatar='" + targetAvatar + "\'" +
                ", transferAmount=" + transferAmount +
                ", transferLogUid=" + transferLogUid +
                ", content='" + content + "\'" +
                '}';
    }
}
