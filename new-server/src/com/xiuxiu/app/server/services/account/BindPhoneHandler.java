package com.xiuxiu.app.server.services.account;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLBindPhoneInfo;
import com.xiuxiu.app.protocol.login.PLBindPhoneRespInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.sms.SMSManager;

import java.io.IOException;

public class BindPhoneHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PLBindPhoneInfo info = JsonUtil.fromJson(body, PLBindPhoneInfo.class);
        Logs.LOGIN.info("收到绑定手机号消息:%s", info);

        PLBindPhoneRespInfo resp = new PLBindPhoneRespInfo();
        do {
            if (null == info) {
                resp.ret = ErrorCode.REQUEST_INVALID.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID.getMsg();
                break;
            }
            if (StringUtil.isEmptyOrNull(info.phone)) {
                Logs.LOGIN.warn("绑定手机号失败, 手机号为空 info:%s", info);
                resp.ret = ErrorCode.PHONE_INVALID.getRet();
                resp.msg = ErrorCode.PHONE_INVALID.getMsg();
                break;
            }
            String authCode = SMSManager.I.getAuthCode(info.phone);
            if (Switch.PHONE_AUTH && StringUtil.isEmptyOrNull(authCode)) {
                Logs.LOGIN.warn("绑定手机号失败, 验证码失效 info:%s", info);
                resp.ret = ErrorCode.AUTH_CODE_INVALID.getRet();
                resp.msg = ErrorCode.AUTH_CODE_INVALID.getMsg();
                break;
            }
            if (Switch.PHONE_AUTH && !authCode.equals(info.authCode)) {
                Logs.LOGIN.warn("绑定手机号失败, 验证码不对 info:%s", info);
                resp.ret = ErrorCode.AUTH_CODE_ERROR.getRet();
                resp.msg = ErrorCode.AUTH_CODE_ERROR.getMsg();
                break;
            }
            Account account = AccountManager.I.getAccountByUid(info.accountUid);
            if (null == account) {
                Logs.LOGIN.warn("绑定手机号失败, 账号不存在info:%s", info);
                resp.ret = ErrorCode.ACCOUNT_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.ACCOUNT_NOT_EXISTS.getMsg();
                break;
            }
            if (!StringUtil.isEmptyOrNull(account.getPhone())) {
                Logs.LOGIN.warn("绑定手机号失败, 已经绑定过了 info:%s", info);
                resp.ret = ErrorCode.ALREADY_BIND_PHONE.getRet();
                resp.msg = ErrorCode.ALREADY_BIND_PHONE.getMsg();
                break;
            }
            if (null != AccountManager.I.getAccountByPhone(info.phone)) {
                Logs.LOGIN.warn("绑定手机号失败, 该手机号已被绑定 info:%s", info);
                resp.ret = ErrorCode.PHONE_ALREADY_USED.getRet();
                resp.msg = ErrorCode.PHONE_ALREADY_USED.getMsg();
                break;
            }
            if (!AccountManager.I.bindPhone(info.accountUid, info.phone)) {
                Logs.LOGIN.warn("绑定手机号失败, 保存数据库失败 info:%s", info);
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
