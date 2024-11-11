package com.xiuxiu.app.protocol.api.league;

public class LeagueGet {
    public long uid;
    public int page;
    public int pageSize;
    public String sign; //md5(uid, page, pageSize, key)

    @Override
    public String toString() {
        return "LeagueGet{" +
                "uid=" + uid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sign='" + sign + '\'' +
                '}';
    }
}
