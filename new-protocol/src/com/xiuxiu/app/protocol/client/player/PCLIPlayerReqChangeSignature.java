package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqChangeSignature {
    public String signature;       // 个性签名

    @Override
    public String toString() {
        return "PCLIPlayerReqChangeSignature{" +
                "signature='" + signature + '\'' +
                '}';
    }
}
