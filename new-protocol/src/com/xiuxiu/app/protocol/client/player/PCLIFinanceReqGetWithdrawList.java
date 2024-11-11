package com.xiuxiu.app.protocol.client.player;

// 平台财务分类获取记录列表
public class PCLIFinanceReqGetWithdrawList {
    public int listType;                    // 1：未处理，2：已处理

    @Override
    public String toString() {
        return "PCLIFinanceReqGetWithdrawList{" +
                "listType=" + listType +
                '}';
    }
}
