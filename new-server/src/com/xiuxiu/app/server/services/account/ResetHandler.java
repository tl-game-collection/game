package com.xiuxiu.app.server.services.account;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLResetAccPassRespInfo;
import com.xiuxiu.app.protocol.login.PLResetAccountPasswdInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.sms.SMSManager;

import java.io.IOException;

public class ResetHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PLResetAccountPasswdInfo info = JsonUtil.fromJson(body, PLResetAccountPasswdInfo.class);
        Logs.LOGIN.info("收到重置密码消息:%s", info);

        PLResetAccPassRespInfo resp = new PLResetAccPassRespInfo();
        do {
            if (null == info) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (StringUtil.isEmptyOrNull(info.phone)) {
                Logs.LOGIN.warn("重置密码失败, 手机号为空 info:%s", info);
                resp.ret = ErrorCode.ACCOUNT_PHONE_NULL.getRet();
                resp.msg = ErrorCode.ACCOUNT_PHONE_NULL.getMsg();
                break;
            }
            if (StringUtil.isEmptyOrNull(info.newPasswd)) {
                Logs.LOGIN.warn("重置密码失败, 密码为空 info:%s", info);
                resp.ret = ErrorCode.ACCOUNT_PASSWD_NULL.getRet();
                resp.msg = ErrorCode.ACCOUNT_PASSWD_NULL.getMsg();
                break;
            }
            String authCode = SMSManager.I.getAuthCode(info.phone);
            if (Switch.PHONE_AUTH && StringUtil.isEmptyOrNull(authCode)) {
                Logs.LOGIN.warn("重置密码失败, 验证码失效 info:%s", info);
                resp.ret = ErrorCode.AUTH_CODE_INVALID.getRet();
                resp.msg = ErrorCode.AUTH_CODE_INVALID.getMsg();
                break;
            }
            if (Switch.PHONE_AUTH && !authCode.equals(info.authCode)) {
                Logs.LOGIN.warn("重置密码失败, 验证码不对 info:%s", info);
                resp.ret = ErrorCode.AUTH_CODE_ERROR.getRet();
                resp.msg = ErrorCode.AUTH_CODE_ERROR.getMsg();
                break;
            }
            String sign = MD5Util.getMD5(info.phone + info.newPasswd + info.authCode + Config.APP_KEY);
            if (!sign.equalsIgnoreCase(info.sign)) {
                Logs.LOGIN.warn("账号验证内容被篡改, server sign:%s client sign:%s", sign, info.sign);
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Account account = AccountManager.I.getAccountByUid(info.accountUid);
            if (null == account) {
                Logs.LOGIN.warn("重置密码失败, 该手机号已被绑定 info:%s", info);
                resp.ret = ErrorCode.PHONE_ALREADY_USED.getRet();
                resp.msg = ErrorCode.PHONE_ALREADY_USED.getMsg();
                break;
            }
            if (!info.phone.equals(account.getPhone())) {
                Logs.LOGIN.warn("重置密码失败, 不是绑定的手机号 info:%s", info);
                resp.ret = ErrorCode.OLD_PHONE_INVALID.getRet();
                resp.msg = ErrorCode.OLD_PHONE_INVALID.getMsg();
                break;
            }
            if (!AccountManager.I.updatePasswdByUid(account.getUid(), info.newPasswd)) {
                Logs.LOGIN.warn("重置密码失败, 保存数据库失败info:%s", info);
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
