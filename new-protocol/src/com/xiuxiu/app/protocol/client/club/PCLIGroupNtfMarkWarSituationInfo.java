package com.xiuxiu.app.protocol.client.club;

public class PCLIGroupNtfMarkWarSituationInfo {
    public long groupUid;
    public long uid;  //数据uid
    public int markstate; //是否标记 0.未标记 1.已标记

    @Override
    public String toString() {
        return "PCLIGroupNtfMarkWarSituationInfo{" +
                "groupUid=" + groupUid +
                ", uid=" + uid +
                ", markstate=" + markstate +
                '}';
    }
}
