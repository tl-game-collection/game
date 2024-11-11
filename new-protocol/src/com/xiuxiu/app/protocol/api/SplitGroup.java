package com.xiuxiu.app.protocol.api;

import java.util.Arrays;

public class SplitGroup {
    public long fromGroupUid;                       // 群Uid
    public Long[] ids;                              // 需要分出来的成员ids
    public String groupName;                        // 群名称
    public long ownerUid;                           // 新群群主Uid
    public String sign;                             // md5(fromGroupUid + groupName + ownerUid + key)

    @Override
    public String toString() {
        return "SplitGroup{" +
                "fromGroupUid=" + fromGroupUid +
                ", ids=" + Arrays.toString(ids) +
                ", groupName='" + groupName + '\'' +
                ", ownerUid=" + ownerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
