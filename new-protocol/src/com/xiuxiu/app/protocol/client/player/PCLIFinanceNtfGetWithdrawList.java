package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIFinanceNtfGetWithdrawList {
    public static class WithdrawRecord {
        public long uid;                         // 提现记录Uid
        public long playerUid;                   // 玩家Uid
        public String account;                   // 提现账号
        public String address;                   // 开户行
        public String realName;                  // 真实姓名
        public long amount;                      // 申请金额（单位：分）
        public long fee;                         // 费率百分比
        public long getMoney;                    // 到账金额（单位：分）
        public int type;                         // 提现方式类型 1:微信  2:支付宝  3:银行卡
        public int status;                       // 提现状态：0-未审核; 1-通过; 2-拒绝;
        public long financeUid;                  // 平台财务Uid
        public int day;                         // 0-三日前 1-今天；2-昨日；3-前日
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
                    ", day=" + day +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    '}';
        }
    }
    
    public List<WithdrawRecord> list = new ArrayList<>();       // 记录列表
    public int listType;                                        // 1：未处理，2：已处理
    public long sumDay1;                                        // 今日总和
    public long sumDay2;                                        // 昨日总和
    public long sumDay3;                                        // 前日总和

    @Override
    public String toString() {
        return "PCLIFinanceNtfGetWithdrawList{" +
                "list=" + list +
                ", listType=" + listType +
                ", sumDay1=" + sumDay1 +
                ", sumDay2=" + sumDay2 +
                ", sumDay3=" + sumDay3 +
                '}';
    }
}
