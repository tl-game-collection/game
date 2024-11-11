package com.xiuxiu.core.net;

import io.netty.channel.Channel;

public interface ConnectionManager {
    void init();

    void destroy();

    Connection get(Channel channel);

    Connection get(long sessionId);

    Connection removeAndClose(Channel channel);

    void add(Connection connection);

    int getConnNum();
}
