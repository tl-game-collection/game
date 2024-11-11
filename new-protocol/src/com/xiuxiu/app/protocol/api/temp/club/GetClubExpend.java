package com.xiuxiu.app.protocol.api.temp.club;

public class GetClubExpend {
    public long clubUid;
    public int page;
    public int pageSize;
    public String sign;     // md5(clubUid + page + pageSize + key)

    @Override
    public String toString() {
        return "GetClubExpend{" +
                "clubUid=" + clubUid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sign='" + sign + '\'' +
                '}';
    }
}
