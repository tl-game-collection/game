package com.xiuxiu.app.protocol.api.league;

public class LeagueJoinByGroupUid {
    public long leagueUid;
    public long groupUid;

    public String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public long getLeagueUid() {
        return leagueUid;
    }

    public void setLeagueUid(long leagueUid) {
        this.leagueUid = leagueUid;
    }

    public long getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(long groupUid) {
        this.groupUid = groupUid;
    }

    @Override
    public String toString() {
        return "LeagueJoinByGroupUid{" +
                "leagueUid=" + leagueUid +
                "groupUid=" + groupUid +
                ", sign='" + sign + '\'' +

                '}';
    }
}
