package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqExchangeShowImage {
    public byte fromShowImageIndex;         // 交换展示图片索引1
    public byte toShowImageIndex;           // 交换展示图片索引2

    @Override
    public String toString() {
        return "PCLIPlayerReqExchangeShowImage{" +
                "fromShowImageIndex=" + fromShowImageIndex +
                ", toShowImageIndex=" + toShowImageIndex +
                '}';
    }
}
