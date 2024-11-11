package com.xiuxiu.app.protocol.api;

public class SaveGameDownloadRecord {
    public long uid;
    public long gid;
    public String uuid;

    @Override
    public String toString() {
        return "SaveGameDownloadRecord{" +
                "uid=" + uid +
                ", gid=" + gid +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
