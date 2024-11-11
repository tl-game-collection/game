package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.ApiUserLoginInfo;
import com.xiuxiu.app.protocol.api.ApiUserLoginInfoResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.api.ApiManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class ApiUserLoginHandler extends BaseHttpHandler {
    @Override
    protected void doHandler(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ApiUserLoginInfo info = JsonUtil.fromJson(body, ApiUserLoginInfo.class);
        Logs.API.debug("api用户登陆:%s", info);

        ApiUserLoginInfoResp resp = new ApiUserLoginInfoResp();

        do {
            if (!ApiManager.I.isVerifyLoginInfo(info.name, info.passwd)) {
                resp.setRet(ErrorCode.ACCOUNT_USERNAME_OR_PASSWD_ERROR);
                break;
            }
            if (!ApiManager.I.login()) {
                resp.setRet(ErrorCode.PLAYER_ALREADY_LOGIN);
                break;
            }
            resp.data = new ApiUserLoginInfoResp.ApiUserInfo();
            resp.data.token = ApiManager.I.getToken();
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
