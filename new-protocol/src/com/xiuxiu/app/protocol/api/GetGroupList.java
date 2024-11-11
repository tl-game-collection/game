package com.xiuxiu.app.protocol.api;

public class GetGroupList {
    public int page;
    public int pageSize;
    public long groupUid;
    public String sign;     // md5(page + pageSize + key)

    @Override
    public String toString() {
        return "GetGroupList{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", groupUid=" + groupUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
