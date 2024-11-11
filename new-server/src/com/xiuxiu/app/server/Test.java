package com.xiuxiu.app.server;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

public class Test {

    public static final Map<String, Object> data = new HashMap<String, Object>();
    static {
        // 是否使用牌库
        data.put("use", true);
        // 牌库
        String card =
            "10,19,10,19,10,19,11,20,12,21,13,22,14,23,15,24,16,25,17,26,18,27,18,27,18,27,12,14,22,12,7,21,14,4,13,20,21,27,9,9,32,26,13,22,14,32,3,15,16,15,25,6,8,24,1,23,6,3,4,11,26,6,13,4,6,23,17,20,5,2,12,32,24,5,17,16,25,7,10,18,5,22,8,20,32,25,23,21,26,2,2,1,4,11,24,17,9,3,8,16,2,9,7,11,7,3,5,8,1,1,19,15";
        data.put("card", card);
        
        
        
       
        
    }

    public static void main(String[] args) {
        Test client = new Test();
        try {
            client.connect("192.168.0.183", 2301);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connect(String host, int port) throws Exception {  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
  
        try {  
            Bootstrap b = new Bootstrap();  
            b.group(workerGroup);  
            b.channel(NioSocketChannel.class);  
            b.option(ChannelOption.SO_KEEPALIVE, true);  
            b.handler(new ChannelInitializer<SocketChannel>() {  
                @Override  
                public void initChannel(SocketChannel ch) throws Exception {  
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码  
                    ch.pipeline().addLast(new HttpResponseDecoder());  
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码  
                    ch.pipeline().addLast(new HttpRequestEncoder());  
                    ch.pipeline().addLast(new HttpClientInboundHandler());  
                }  
            });  
  
            // Start the client.  
            ChannelFuture f = b.connect(host, port).sync();  
  
            URI uri = new URI("http://192.168.0.183:2301/setMahjongCardLib");//扑克
     
        
            
            String msg = JSON.toJSONString(data);
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,  
                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));  
  
            // 构建http请求  
            request.headers().set(HttpHeaders.Names.HOST, host);  
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);  
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());  
            // 发送http请求  
            f.channel().write(request);  
            f.channel().flush();  
            f.channel().closeFuture().sync();  
        } finally {  
            workerGroup.shutdownGracefully();  
        }  
  
    }

    public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpResponse) {
                HttpResponse response = (HttpResponse)msg;
                System.out.println("CONTENT_TYPE:" + response.headers().get(HttpHeaders.Names.CONTENT_TYPE));
            }
            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent)msg;
                ByteBuf buf = content.content();
                System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
                buf.release();
            }
        }
    }
}
