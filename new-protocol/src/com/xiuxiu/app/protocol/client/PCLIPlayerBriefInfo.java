package com.xiuxiu.app.protocol.client;

import java.util.List;

public class PCLIPlayerBriefInfo {
    public long uid;
    public String name;
    public byte sex;
    public String alias;
    public String icon;
    public List<String> tags;
    public String zone;
    public long lastLogoutTime;                 // 上次登出时间(ms), -1: 表示在线

    @Override
    public String toString() {
        return "PCLIPlayerBriefInfo{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", alias='" + alias + '\'' +
                ", icon='" + icon + '\'' +
                ", tags=" + tags +
                ", zone='" + zone + '\'' +
                ", lastLogoutTime=" + lastLogoutTime +
                '}';
    }
}
