package com.xiuxiu.app.protocol.client.player;

// 修改免密支付状态返回
public class PCLIPlayerNtfModifyNoNeedPayPassword {
    public int noNeedPayPassword;      // 免密支付状态 0:关闭, 1:开启

    @Override
    public String toString() {
        return "PCLIPlayerNtfModifyNoNeedPayPassword{" +
                "noNeedPayPassword=" + noNeedPayPassword +
                '}';
    }
}
