package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqHandleWithdraw {
    public long uid;        // 提现记录Uid
    public int status;      // 需要处理的提现状态: 1-通过; 2-拒绝;

    @Override
    public String toString() {
        return "PCLIPlayerReqHandleWithdraw{" +
                "uid=" + uid +
                ", status=" + status +
                '}';
    }
}
