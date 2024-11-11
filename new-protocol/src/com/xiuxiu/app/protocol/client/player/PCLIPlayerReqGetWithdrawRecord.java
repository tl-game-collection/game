package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqGetWithdrawRecord {
    public int type;        // 提现方式 0-全部 1-微信 2-支付宝 3-银行卡
    public int status;      // 状态 0-未审核 1-已审核

    @Override
    public String toString() {
        return "PCLIPlayerReqGetWithdrawRecord{" +
                "type=" + type +
                ", status=" + status +
                '}';
    }
}
