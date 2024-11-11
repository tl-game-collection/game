package com.xiuxiu.core.net;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.message.CommandId;
import com.xiuxiu.core.thread.NameThreadFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerConnectionManager implements ConnectionManager {
    protected final ConcurrentHashMap<ChannelId, ConnectionHolder> allConnection = new ConcurrentHashMap<>(16);
    protected final ConcurrentHashMap<Long, ConnectionHolder> allConnection2 = new ConcurrentHashMap<>(16);
    protected final ConnectionHolder DEFAULT = new SimpleConnectionHolder(null);
    protected HashedWheelTimer timer;

    @Override
    public void init() {
        this.timer = new HashedWheelTimer(new NameThreadFactory("ServerConnectionManager"), 1, TimeUnit.SECONDS);
        //this.timer.start();
    }

    @Override
    public void destroy() {
        if (null != this.timer) {
            this.timer.stop();
        }
        Iterator<Map.Entry<ChannelId, ConnectionHolder>> it = this.allConnection.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ChannelId, ConnectionHolder> entry = it.next();
            entry.getValue().close();
        }
        this.allConnection.clear();
        this.allConnection2.clear();
    }

    @Override
    public Connection get(Channel channel) {
        return this.allConnection.getOrDefault(channel.id(), DEFAULT).get();
    }

    @Override
    public Connection get(long sessionId) {
        return this.allConnection2.getOrDefault(sessionId, DEFAULT).get();
    }

    @Override
    public Connection removeAndClose(Channel channel) {
        ConnectionHolder holder = this.allConnection.remove(channel.id());
        if (null != holder) {
            Connection connection = holder.get();
            this.allConnection2.remove(connection.getId());
            connection.close();
            return connection;
        }
        Connection connection = new NettyConnection();
        connection.init(channel);
        connection.close();
        this.allConnection2.remove(connection.getId());
        return connection;
    }

    @Override
    public void add(Connection connection) {
        this.allConnection.putIfAbsent(connection.getChannel().id(), new HeartbeatConnectionHolder(connection));
        this.allConnection2.putIfAbsent(connection.getId(), this.allConnection.get(connection.getChannel().id()));
    }

    @Override
    public int getConnNum() {
        return this.allConnection.size();
    }

    private interface ConnectionHolder {
        Connection get();

        void close();
    }

    private class SimpleConnectionHolder implements ConnectionHolder {
        private Connection connection;

        public SimpleConnectionHolder(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection get() {
            return this.connection;
        }

        @Override
        public void close() {
            if (null != this.connection) {
                this.connection.close();
            }
        }
    }

    private class HeartbeatConnectionHolder implements ConnectionHolder, TimerTask {
        private Connection connection;
        private byte timeoutTimes = 0;

        public HeartbeatConnectionHolder(Connection connection) {
            this.connection = connection;
            this.startTimer();
        }

        private void startTimer() {
            if (null != connection && connection.isConnected()) {
                timer.newTimeout(this, this.connection.heartbeat() - 1000, TimeUnit.MILLISECONDS);
            }
        }

        @Override
        public Connection get() {
            return this.connection;
        }

        @Override
        public void close() {
            if (null != this.connection) {
                this.connection.close();
            }
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            if (null == this.connection || !this.connection.isConnected()) {
                Logs.HB.warn("Heartbeat timeout timeoutTimes:%d Connection:%s is disconnected", this.timeoutTimes, this.connection);
                return;
            }
            if (this.connection.isReadTimeout()) {
                ++this.timeoutTimes;
                if (this.timeoutTimes > Connection.TIMEOUT_TIMES) {
                    Logs.HB.warn("Heartbeat timeout timeoutTimes:%d do close Connection:%s", this.timeoutTimes, this.connection);
                    this.close();
                    return;
                }
                Logs.HB.info("Heartbeat timeout timeoutTimes:%d Connection:%s", this.timeoutTimes, this.connection);
            } else {
                this.timeoutTimes = 0;
            }
            if (connection.isWriteTimeout()) {
                connection.send(CommandId.HEARTBEAT, null, connection.getProtocolVersion());
            }
            this.startTimer();
        }
    }
}
