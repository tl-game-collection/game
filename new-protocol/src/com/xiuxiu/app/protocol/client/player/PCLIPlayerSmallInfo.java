package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerSmallInfo {
    public long uid;
    public String name;
    public byte sex;
    public String icon;

    @Override
    public String toString() {
        return "PCLIPlayerSmallInfo{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", icon='" + icon + '\'' +
                '}';
    }
}
