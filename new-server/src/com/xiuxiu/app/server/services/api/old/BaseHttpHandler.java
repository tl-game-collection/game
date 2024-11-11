package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.core.net.HttpServer;

public abstract class BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(httpExchange.getRequestMethod())) {
            HttpServer.sendOk(httpExchange, null);
            httpExchange.close();
            return;
        }
        this.doHandler(httpExchange);
    }

    protected abstract void doHandler(HttpExchange httpExchange) throws IOException ;
}
