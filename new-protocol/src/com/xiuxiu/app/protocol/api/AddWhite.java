package com.xiuxiu.app.protocol.api;

public class AddWhite {
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
        return "AddWhite{" +
                "playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
