package com.xiuxiu.app.protocol.api;

public class CloseArenaMatch {
    protected long arenaUid;
    protected long arenaMatchUid;
    protected String sign;

    public long getArenaUid() {
        return arenaUid;
    }

    public void setArenaUid(long arenaUid) {
        this.arenaUid = arenaUid;
    }

    public long getArenaMatchUid() {
        return arenaMatchUid;
    }

    public void setArenaMatchUid(long arenaMatchUid) {
        this.arenaMatchUid = arenaMatchUid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "CloseArenaMatch{" +
                "arenaUid=" + arenaUid +
                ", arenaMatchUid=" + arenaMatchUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
