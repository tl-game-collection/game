package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfWalletRedPacketLoad {
    public static class WalletRedPacketRecord{
        public long playerUid;                  // 领取红包记录Uid
        public String playerName;               // 领取者昵称
        public String playerIcon;               // 红包总金额 单位:分
        public long redPacketUid;               // 红包总数量
        public int amount;                      // 红包金额
        public long createdAt;                  // 发红包时间

        @Override
        public String toString() {
            return "WalletRedPacketRecord{" +
                    "playerUid=" + playerUid +
                    ", playerName='" + playerName + '\'' +
                    ", playerIcon='" + playerIcon + '\'' +
                    ", redPacketUid=" + redPacketUid +
                    ", amount=" + amount +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }

    public long redPacketUid;               // 红包记录Uid
    public long senderUid;                  // 发红包人Uid
    public String senderNickname;           // 发红包人昵称
    public String senderIcon;               // 发红包人头像
    public long groupUid;                   // 接收转账群Uid
    public int amount;                      // 红包总金额 单位:分
    public int count;                       // 红包总数量
    public int receivedAmount;              // 被领取总金额 单位:分
    public int receivedCount;               // 被领总数量
    public String content;                  // 红包备注
    public long createdAt;                  // 发红包时间 时间戳
    public long expiredAt;                  // 红包过期时间 时间戳
    public long clearAt;                    // 红包被领完时间 时间戳
    public int status;                      // 转账状态：0-转账中;1-已领完;2-超时;3-被退回
    public byte type;                       // 转账类型 0-普通红包 1-拼手气红包
    public List<WalletRedPacketRecord> receivedInfo = new ArrayList<>(); // 红包领取记录列表

    public PCLIPlayerNtfWalletRedPacketLoad() {
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfWalletRedPacketLoad{" +
                "redPacketUid=" + redPacketUid +
                ", senderUid=" + senderUid +
                ", senderNickname='" + senderNickname + '\'' +
                ", senderIcon='" + senderIcon + '\'' +
                ", groupUid=" + groupUid +
                ", amount=" + amount +
                ", count=" + count +
                ", receivedAmount=" + receivedAmount +
                ", receivedCount=" + receivedCount +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", expiredAt=" + expiredAt +
                ", clearAt=" + clearAt +
                ", status=" + status +
                ", type=" + type +
                ", receivedInfo=" + receivedInfo +
                '}';
    }
}
