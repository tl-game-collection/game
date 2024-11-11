package com.xiuxiu.app.server.services.gateway;

import com.xiuxiu.core.net.SessionContext;

public class GatewaySessionContext implements SessionContext {
    private volatile long playerUid = -1;

    public GatewaySessionContext() {

    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    @Override
    public String toString() {
        return "PlayerUid:" + this.playerUid;
    }
}
