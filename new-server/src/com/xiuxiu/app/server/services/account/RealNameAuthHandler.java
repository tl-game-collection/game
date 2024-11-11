package com.xiuxiu.app.server.services.account;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLRealNameInfo;
import com.xiuxiu.app.protocol.login.PLRealNameRespInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.IdentityCardUtil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.io.IOException;

public class RealNameAuthHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PLRealNameInfo info = JsonUtil.fromJson(body, PLRealNameInfo.class);
        Logs.LOGIN.info("收到实名认证消息:%s", info);

        PLRealNameRespInfo resp = new PLRealNameRespInfo();
        do {
            if (null == info) {
                resp.ret = ErrorCode.REQUEST_INVALID.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID.getMsg();
                break;
            }
            if (StringUtil.isEmptyOrNull(info.name) || StringUtil.isEmptyOrNull(info.idCard)) {
                Logs.LOGIN.warn("实名认证失败, 姓名/身份证为空 info:%s", info);
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (!IdentityCardUtil.validate(info.idCard)) {
                Logs.LOGIN.warn("实名认证失败, 身份证无效 info:%s", info);
                resp.ret = ErrorCode.IDENTITY_CARD_INVALID.getRet();
                resp.msg = ErrorCode.IDENTITY_CARD_INVALID.getMsg();
                break;
            }
            Account account = AccountManager.I.getAccountByUid(info.accountUid);
            if (null == account) {
                Logs.LOGIN.warn("实名认证失败, 账号不存在 info:%s", info);
                resp.ret = ErrorCode.ACCOUNT_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.ACCOUNT_NOT_EXISTS.getMsg();
                break;
            }
            if (!AccountManager.I.updateRealNameByUid(info.accountUid, info.name, info.idCard)) {
                Logs.LOGIN.warn("实名认证失败, 保存数据库失败 info:%s", info);
                resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
                resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
                break;
            }
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
        } while (false);
        byte[] respData = JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8);
        HttpServer.sendOk(httpExchange, respData);
        httpExchange.close();
    }
}
