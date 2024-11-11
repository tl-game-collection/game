package com.xiuxiu.app.protocol.api.league;

public class LeagueKill {
    public long uid;
    public long groupUid;
    public String sign; // md5(uid, groupUid, key)

    @Override
    public String toString() {
        return "LeagueKill{" +
                "uid=" + uid +
                ", groupUid=" + groupUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
