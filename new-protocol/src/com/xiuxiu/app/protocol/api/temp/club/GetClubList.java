package com.xiuxiu.app.protocol.api.temp.club;

public class GetClubList {
    public int page;
    public int pageSize;
    public long clubUid;
    public String sign;     // md5(page + pageSize + clubUid + key)

    @Override
    public String toString() {
        return "GetClubList{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", clubUid=" + clubUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
