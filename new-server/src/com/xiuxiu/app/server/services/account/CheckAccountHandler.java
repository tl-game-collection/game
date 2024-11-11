package com.xiuxiu.app.server.services.account;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLCheckAccountInfo;
import com.xiuxiu.app.protocol.login.PLCheckAccountRespInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.sms.SMSManager;

import java.io.IOException;

public class CheckAccountHandler implements HttpHandler {
    private final ConcurrentHashSet<String> curRegisterPhone = new ConcurrentHashSet<>();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PLCheckAccountInfo info = JsonUtil.fromJson(body, PLCheckAccountInfo.class);
        Logs.LOGIN.debug("收到检查帐号注册消息:%s", info);

        PLCheckAccountRespInfo resp = new PLCheckAccountRespInfo();
        do {
            if (null == info) {
                Logs.LOGIN.warn("收到检查账号信息无效, conn:%s", httpExchange);
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (StringUtil.isEmptyOrNull(info.phone) || 11 != info.phone.length()) {
                Logs.LOGIN.warn("手机号:%s 无效", info.phone);
                resp.ret = ErrorCode.PHONE_INVALID.getRet();
                resp.msg = ErrorCode.PHONE_INVALID.getMsg();
                break;
            }
            String authCode = SMSManager.I.getAuthCode(info.phone);
            if (Switch.PHONE_AUTH && StringUtil.isEmptyOrNull(authCode)) {
                Logs.LOGIN.warn("检查账号-手机注册失败, 验证码失效 info:%s", info);
                resp.ret = ErrorCode.AUTH_CODE_INVALID.getRet();
                resp.msg = ErrorCode.AUTH_CODE_INVALID.getMsg();
                break;
            }
            if (Switch.PHONE_AUTH && !authCode.equals(info.authCode)) {
                Logs.LOGIN.warn("注册账号-手机号注册失败, 验证码不对 info:%s", info);
                resp.ret = ErrorCode.AUTH_CODE_ERROR.getRet();
                resp.msg = ErrorCode.AUTH_CODE_ERROR.getMsg();
                break;
            }
            Account account = AccountManager.I.getAccountByPhone(info.phone);
            if (null != account) {
                Logs.LOGIN.warn("检查账号 手机号:%s 已经注册", info.phone);
                resp.ret = ErrorCode.ACCOUNT_ALREADY_EXISTS.getRet();
                resp.msg = ErrorCode.ACCOUNT_ALREADY_EXISTS.getMsg();
                this.curRegisterPhone.remove(info.phone);
                break;
            }

            Logs.LOGIN.debug("检查账号 手机号:%s 可以注册", info.phone);
            this.curRegisterPhone.remove(info.phone);
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
        } while (false);
        byte[] respData = JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8);
        HttpServer.sendOk(httpExchange, respData);
        httpExchange.close();
    }
}
