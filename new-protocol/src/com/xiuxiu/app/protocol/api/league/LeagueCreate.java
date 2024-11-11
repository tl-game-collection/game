package com.xiuxiu.app.protocol.api.league;

public class LeagueCreate {
    public long playerUid;
    public String sign; // md5(playerUid, key)

    @Override
    public String toString() {
        return "LeagueCreate{" +
                "playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
