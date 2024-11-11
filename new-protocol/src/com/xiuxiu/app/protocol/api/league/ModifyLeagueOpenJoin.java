package com.xiuxiu.app.protocol.api.league;

public class ModifyLeagueOpenJoin {
    public long uid;
    public int openJoin;//0不可见，1可见
    public String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getOpenJoin() {
        return openJoin;
    }

    public void setOpenJoin(int openJoin) {
        this.openJoin = openJoin;
    }

    @Override
    public String toString() {
        return "ModifyLeagueOpenJoin{" +
                "uid=" + uid +
                ", openJoin=" + openJoin +
                ", sign='" + sign + '\'' +

                '}';
    }
}
