package com.xiuxiu.core.net;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.core.utils.StringUtil;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class HttpServer {
    private static com.sun.net.httpserver.HttpsServer createHttpsServer(String ip, int port) throws IOException {
        // TODO 需要ssl证书
        return com.sun.net.httpserver.HttpsServer.create(new InetSocketAddress(ip, port), 0);
    }

    private static com.sun.net.httpserver.HttpServer createHttpServer(String ip, int port) throws IOException {
        return com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(ip, port), 0);
    }

    public static com.sun.net.httpserver.HttpServer create(int port, boolean https) {
        return create("0.0.0.0", port, https);
    }

    public static com.sun.net.httpserver.HttpServer create(String ip, int port, boolean https) {
        try {
            return https ? createHttpsServer(ip, port) : createHttpServer(ip, port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> readParams(HttpExchange httpExchange) {
        if ("GET".equalsIgnoreCase(httpExchange.getRequestMethod())) {
            return StringUtil.queryString2HashMap(httpExchange.getRequestURI().getQuery());
        }
        return null;
    }

    public static byte[] readBody(HttpExchange httpExchange) throws IOException {
        InputStream in = httpExchange.getRequestBody();
        String length = httpExchange.getRequestHeaders().getFirst("content-length");
        if (!StringUtil.isEmptyOrNull(length) && !length.equals("0")) {
            byte[] buffer = new byte[Integer.parseInt(length)];
            in.read(buffer);
            in.close();
            return buffer;
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int ret = -1;
            while (-1 != (ret = in.read(buffer, 0, 1024))) {
                out.write(buffer, 0, ret);
            }
            in.close();
            return out.toByteArray();
        }
    }

    public static void sendOk(HttpExchange httpExchange, byte[] data) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        httpExchange.getResponseHeaders().set(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain; charset=UTF-8");
        httpExchange.getResponseHeaders().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString(), "*");
        httpExchange.getResponseHeaders().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString(), "GET POST");
        httpExchange.getResponseHeaders().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS.toString(), "true");
        httpExchange.getResponseHeaders().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString(), "Content-Type,X-Token");
        httpExchange.getResponseHeaders().set(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS.toString(), "*");
        httpExchange.sendResponseHeaders(200, null == data ? 0 : data.length);
        if (null != data) {
            OutputStream out = httpExchange.getResponseBody();
            out.write(data);
            out.close();
        }
    }
}
