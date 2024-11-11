package com.xiuxiu.app.server.manager;

public class GeoPlayer {
    protected long playerUid;
    protected byte sex;

    public GeoPlayer() {

    }

    public GeoPlayer(long playerUid, byte sex) {
        this.playerUid = playerUid;
        this.sex = sex;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }
}
