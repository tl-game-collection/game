package com.xiuxiu.app.protocol.api.league;

public class LeagueGetByLeagueUid {
    public long leagueUid;
    public String sign; // med(page, pageSize, key)

    @Override
    public String toString() {
        return "LeagueGetByLeagueUid{" +
                ", leagueUid=" + leagueUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
