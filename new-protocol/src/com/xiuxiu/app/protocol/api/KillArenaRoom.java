package com.xiuxiu.app.protocol.api;

public class KillArenaRoom {
    protected long playerUid;
    protected String sign;

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "KillArenaRoom{" +
                "playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
