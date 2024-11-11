package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfWalletMonthlyRecord {
    public static class WalletMonthlyRecord {
        private long createdAt;               // 操作时间
        private int action;                  // 操作类型
        private int amount;                 // 操作金额

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "WalletMonthlyRecord{" +
                    "createdAt=" + createdAt +
                    ", action=" + action +
                    ", amount=" + amount +
                    '}';
        }
    }

    public List<WalletMonthlyRecord> recordList = new ArrayList<>();      // 记录列表

    @Override
    public String toString() {
        return "PCLIPlayerNtfWalletMonthlyRecord{" +
                "recordList=" + recordList +
                '}';
    }
}
