package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxNtfNameInfo {
    public long clubUid;           // 包厢所属群uid
    public long boxUid;             // 包厢uid
    public String boxName;          // 包厢新名字

    @Override
    public String toString() {
        return "PCLIBoxNtfNameInfo{" +
                "clubUid=" + clubUid +
                ", boxUid=" + boxUid +
                ", boxName='" + boxName + '\'' +
                '}';
    }
}
