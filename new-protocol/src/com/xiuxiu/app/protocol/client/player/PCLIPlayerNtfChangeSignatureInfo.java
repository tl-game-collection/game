package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfChangeSignatureInfo {
    public String signature;       // 个性签名

    public PCLIPlayerNtfChangeSignatureInfo() {
    }

    public PCLIPlayerNtfChangeSignatureInfo(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfChangeSignatureInfo{" +
                "signature='" + signature + '\'' +
                '}';
    }
}
