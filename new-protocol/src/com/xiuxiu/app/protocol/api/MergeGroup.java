package com.xiuxiu.app.protocol.api;

public class MergeGroup {
    public long fromGroupUid;                     // 副群Uid
    public long toGroupUid;                       // 主群Uid
    public String sign;                             // md5(fromGroupUid + toGroupUid + key)

    @Override
    public String toString() {
        return "MergeGroup{" +
                "fromGroupUid=" + fromGroupUid +
                ", toGroupUid=" + toGroupUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
