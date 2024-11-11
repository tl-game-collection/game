package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfWalletWithdrawTypeListInfo {
    public static class WithdrawTypeInfo {
        public long id;
        public long uid;
        public int type;
        public String payeeAccount;
        public String payeeRealName;
        public String payeeAddress;
        public String payeeLocation;
        public int status;           // 0-停用; 1-可用; 2-默认选中

        @Override
        public String toString() {
            return "WithdrawTypeInfo{" +
                    "id=" + id +
                    ", uid=" + uid +
                    ", type=" + type +
                    ", payeeAccount='" + payeeAccount + '\'' +
                    ", payeeRealName='" + payeeRealName + '\'' +
                    ", payeeAddress='" + payeeAddress + '\'' +
                    ", payeeLocation='" + payeeLocation + '\'' +
                    ", status=" + status +
                    '}';
        }
    }


    public List<WithdrawTypeInfo> withdrawTypeList = new ArrayList<>();
    public int page;
    public boolean next;

    @Override
    public String toString() {
        return "PCLIPlayerNtfWalletWithdrawTypeListInfo{" +
                "withdrawTypeList=" + withdrawTypeList +
                ", page=" + page +
                ", next=" + next +
                '}';
    }
}
