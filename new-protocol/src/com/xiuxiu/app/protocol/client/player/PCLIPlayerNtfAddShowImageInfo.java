package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfAddShowImageInfo {
    public String showImageUrl;       // 展示图片url

    public PCLIPlayerNtfAddShowImageInfo() {
    }

    public PCLIPlayerNtfAddShowImageInfo(String showImageUrl) {
        this.showImageUrl = showImageUrl;
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfAddShowImageInfo{" +
                "showImageUrl='" + showImageUrl + '\'' +
                '}';
    }
}
