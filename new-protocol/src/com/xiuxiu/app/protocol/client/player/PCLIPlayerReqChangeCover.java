package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqChangeCover {
    public String coverFileName;       // 封面图片文件名

    @Override
    public String toString() {
        return "PCLIPlayerReqChangeCover{" +
                "coverFileName='" + coverFileName + '\'' +
                '}';
    }
}
