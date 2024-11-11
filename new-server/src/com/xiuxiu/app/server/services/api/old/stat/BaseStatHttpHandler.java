package com.xiuxiu.app.server.services.api.old.stat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

import java.io.IOException;

public abstract class BaseStatHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        // TODO 签名校验

        // TODO 其它

        String reqData = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        Logs.API.debug("BaseStatHttpHandler.handle reqData:%s", reqData);
        ErrorMsg resp = doHandle(reqData);
        byte[] respData = JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8);
        HttpServer.sendOk(httpExchange, respData);
        httpExchange.close();
    }

    protected abstract ErrorMsg doHandle(String data);

    private void responseWithError(HttpExchange httpExchange, ErrorCode err) throws IOException {
        ErrorMsg errorMsg = new ErrorMsg(err);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(errorMsg).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
