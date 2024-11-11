package com.xiuxiu.app.server.services.api.old.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import io.netty.handler.codec.http.HttpMethod;

import java.io.IOException;
import java.util.List;

public abstract class BaseAdminHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        List<String> list = httpExchange.getRequestHeaders().get("X-real-ip");
        String ip = (null == list || list.size() < 1) ? "unknown" : list.get(0);
        // TODO 暂时去除
        if (false && !Config.IP_WHITE.contains(ip)) {
            Logs.API.warn("未授权的请求，来自：%s", ip);
            this.responseWithError(httpExchange, ErrorCode.REQUEST_INVALID);
            return;
        }

        if (!httpExchange.getRequestMethod().equals(HttpMethod.POST.name())) {
            Logs.API.warn("不支持的HTTP Method: %s，来自：%s", httpExchange.getRequestMethod(), ip);
            this.responseWithError(httpExchange, ErrorCode.REQUEST_INVALID);
            return;
        }

        String reqData = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
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
