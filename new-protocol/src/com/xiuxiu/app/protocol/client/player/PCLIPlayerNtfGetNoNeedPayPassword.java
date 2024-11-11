package com.xiuxiu.app.protocol.client.player;

// 获取免密支付状态
public class PCLIPlayerNtfGetNoNeedPayPassword {
    public int noNeedPayPassword;      // 免密支付状态 0:关闭, 1:开启

    @Override
    public String toString() {
        return "PCLIPlayerNtfGetNoNeedPayPassword{" +
                "noNeedPayPassword=" + noNeedPayPassword +
                '}';
    }
}
