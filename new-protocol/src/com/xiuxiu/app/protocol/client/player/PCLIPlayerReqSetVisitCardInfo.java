package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqSetVisitCardInfo {
    public String desc;         // 简介
    public String fileName;     // 文件名
    public int index;           // -1: 添加, 其他的替换

    @Override
    public String toString() {
        return "PCLIPlayerReqSetVisitCardInfo{" +
                "desc='" + desc + '\'' +
                ", fileName='" + fileName + '\'' +
                ", index=" + index +
                '}';
    }
}
