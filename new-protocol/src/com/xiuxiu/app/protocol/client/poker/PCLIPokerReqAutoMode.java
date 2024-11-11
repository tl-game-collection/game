package com.xiuxiu.app.protocol.client.poker;

// 请求托管或解除托管
public class PCLIPokerReqAutoMode {
    public boolean auto; // true:托管，false:解除托管

    @Override
    public String toString() {
        return "PCLIPokerReqAutoMode{" +
                "auto=" + auto +
                '}';
    }
}
