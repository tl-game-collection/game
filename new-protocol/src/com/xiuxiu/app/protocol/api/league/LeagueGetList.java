package com.xiuxiu.app.protocol.api.league;

public class LeagueGetList {
    public int page;
    public int pageSize;
    public String sign; // med(page, pageSize, key)

    @Override
    public String toString() {
        return "LeagueGetList{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", sign='" + sign + '\'' +
                '}';
    }
}
