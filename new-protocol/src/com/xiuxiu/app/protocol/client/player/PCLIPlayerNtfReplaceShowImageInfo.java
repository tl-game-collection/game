package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfReplaceShowImageInfo {
    public byte showImageIndex;         // 要替换展示图片索引
    public String newShowImageUrl;      // 新的展示图片url

    public PCLIPlayerNtfReplaceShowImageInfo() {
    }

    public PCLIPlayerNtfReplaceShowImageInfo(byte showImageIndex, String newShowImageUrl) {
        this.showImageIndex = showImageIndex;
        this.newShowImageUrl = newShowImageUrl;
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfReplaceShowImageInfo{" +
                "showImageIndex=" + showImageIndex +
                ", newShowImageUrl='" + newShowImageUrl + '\'' +
                '}';
    }
}
