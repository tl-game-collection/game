package com.xiuxiu.app.protocol.api.temp.player;

public class BanAccount {
    protected long playerUid;
    protected boolean ban;
    protected String sign;

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "BanAccount{" +
                "playerUid=" + playerUid +
                ", ban=" + ban +
                ", sign='" + sign + '\'' +
                '}';
    }
}
