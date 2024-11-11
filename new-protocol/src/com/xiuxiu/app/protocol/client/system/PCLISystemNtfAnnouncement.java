package com.xiuxiu.app.protocol.client.system;

public class PCLISystemNtfAnnouncement {
    public long uid;           // 公告ID标识
    public String content;     // 公告内容
    public int repeatTimes;    // 重复次数
    public int repeatInterval; // 重复时间间隔，秒为单位

    @Override
    public String toString() {
        return "PCLISystemNTFAnnouncement{" +
                "uid=" + uid +
                ", content='" + content + '\'' +
                ", repeatTimes=" + repeatTimes +
                ", repeatInterval=" + repeatInterval +
                '}';
    }
}
