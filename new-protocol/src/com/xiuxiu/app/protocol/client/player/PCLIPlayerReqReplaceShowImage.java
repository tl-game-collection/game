package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqReplaceShowImage {
    public byte showImageIndex;         // 要替换展示图片索引
    public String newShowImageFileName; // 新的展示图片文件名

    @Override
    public String toString() {
        return "PCLIPlayerReqReplaceShowImage{" +
                "showImageIndex=" + showImageIndex +
                ", newShowImageFileName='" + newShowImageFileName + '\'' +
                '}';
    }
}
