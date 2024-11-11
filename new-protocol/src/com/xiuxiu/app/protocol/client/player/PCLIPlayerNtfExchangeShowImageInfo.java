package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfExchangeShowImageInfo {
    public byte fromShowImageIndex;         // 交换展示图片索引1
    public byte toShowImageIndex;           // 交换展示图片索引2

    public PCLIPlayerNtfExchangeShowImageInfo() {
    }

    public PCLIPlayerNtfExchangeShowImageInfo(byte fromShowImageIndex, byte toShowImageIndex) {
        this.fromShowImageIndex = fromShowImageIndex;
        this.toShowImageIndex = toShowImageIndex;
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfExchangeShowImageInfo{" +
                "fromShowImageIndex=" + fromShowImageIndex +
                ", toShowImageIndex=" + toShowImageIndex +
                '}';
    }
}
