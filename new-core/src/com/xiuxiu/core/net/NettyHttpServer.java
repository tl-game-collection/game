package com.xiuxiu.core.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public abstract class NettyHttpServer extends NettyTcpServer implements Server {
    public NettyHttpServer(int port) {
        this("0.0.0.0", port);
    }

    public NettyHttpServer(String host, int port) {
        super(host, port);
    }

    @Override
    protected void initOptions(ServerBootstrap b) {
        super.initOptions(b);
    }

    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        //pipeline.addLast("http-aggregator", new HttpObjectAggregator(65535));
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
        pipeline.addLast("handler", this.getChannelHandler());
    }

    @Override
    protected ChannelHandler getDecoder() {
        return null;
    }

    @Override
    protected ChannelHandler getEncoder() {
        return null;
    }
}
