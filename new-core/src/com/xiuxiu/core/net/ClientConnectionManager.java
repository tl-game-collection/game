package com.xiuxiu.core.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientConnectionManager implements ConnectionManager {
    private ConcurrentHashMap<ChannelId, Connection> allConnection = new ConcurrentHashMap<>();

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        Iterator<Map.Entry<ChannelId, Connection>> it = this.allConnection.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ChannelId, Connection> entry = it.next();
            entry.getValue().close();
        }
        this.allConnection.clear();
    }

    @Override
    public Connection get(Channel channel) {
        return this.allConnection.get(channel.id());
    }

    @Override
    public Connection get(long sessionId) {
        return null;
    }

    @Override
    public Connection removeAndClose(Channel channel) {
        Connection conn = this.allConnection.remove(channel.id());
        if (null != conn) {
            conn.close();
        }
        return conn;
    }

    @Override
    public void add(Connection connection) {
        this.allConnection.putIfAbsent(connection.getChannel().id(), connection);
    }

    @Override
    public int getConnNum() {
        return this.allConnection.size();
    }
}
