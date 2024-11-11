package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfChangeCoverInfo {
    public String coverUrl;       // 封面图片url

    public PCLIPlayerNtfChangeCoverInfo() {
    }

    public PCLIPlayerNtfChangeCoverInfo(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfChangeCoverInfo{" +
                "coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
