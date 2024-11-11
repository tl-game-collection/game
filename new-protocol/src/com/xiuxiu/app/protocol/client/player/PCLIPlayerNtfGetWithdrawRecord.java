package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfGetWithdrawRecord {

    public static class WithdrawRecord {
        public long uid;                         // 提现记录Uid
        public long playerUid;                   // 玩家Uid
        public String account;                   // 提现账号
        public String address;                   // 开户行
        public String realName;                  // 真实姓名
        public long amount;                      // 申请金额（单位：分）
        public long fee;                         // 费率百分比
        public long getMoney;                    // 到账金额
        public int type;                         // 提现方式类型 1:微信  2:支付宝  3:银行卡
        public int status;                       // 提现状态：0-未审核; 1-通过; 2-拒绝;
        public long financeUid;                  // 平台财务Uid
        public long createdAt;                   // 申请时间 毫秒时间戳
        public long updatedAt;                   // 审核时间 毫秒时间戳

        @Override
        public String toString() {
            return "WithdrawRecord{" +
                    "uid=" + uid +
                    ", playerUid=" + playerUid +
                    ", account='" + account + '\'' +
                    ", address='" + address + '\'' +
                    ", realName='" + realName + '\'' +
                    ", amount=" + amount +
                    ", fee=" + fee +
                    ", getMoney=" + getMoney +
                    ", type=" + type +
                    ", status=" + status +
                    ", financeUid=" + financeUid +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    '}';
        }
    }

    public List<WithdrawRecord> list = new ArrayList<>();
    public int status;                                          // 状态 0-未审核 1-已审核

    @Override
    public String toString() {
        return "PCLIPlayerNtfGetWithdrawRecord{" +
                "list=" + list +
                ", status=" + status +
                '}';
    }
}
