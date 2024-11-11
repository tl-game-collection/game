package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfDelShowImageInfo {
    public byte showImageIndex;       // 展示图片索引

    public PCLIPlayerNtfDelShowImageInfo() {
    }

    public PCLIPlayerNtfDelShowImageInfo(byte showImageIndex) {
        this.showImageIndex = showImageIndex;
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfDelShowImageInfo{" +
                "showImageIndex=" + showImageIndex +
                '}';
    }
}
