package com.xiuxiu.app.server.services.gateway;

import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.net.ServerConnectionManager;
import io.netty.channel.Channel;

public class GatewayServerConnectionManager extends ServerConnectionManager {
    @Override
    public Connection removeAndClose(Channel channel) {
        Connection conn = super.removeAndClose(channel);
        PlayerManager.I.logout(conn);
        return conn;
    }
}
