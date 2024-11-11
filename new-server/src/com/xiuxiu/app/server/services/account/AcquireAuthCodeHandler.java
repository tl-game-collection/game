package com.xiuxiu.app.server.services.account;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLAcquireAuthCodeInfo;
import com.xiuxiu.app.protocol.login.PLAcquireAuthCodeRespInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.sms.SMSManager;

import java.io.IOException;

public class AcquireAuthCodeHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PLAcquireAuthCodeInfo info = JsonUtil.fromJson(body, PLAcquireAuthCodeInfo.class);
        Logs.LOGIN.info("收到获取验证码消息:%s", info);

        PLAcquireAuthCodeRespInfo resp = new PLAcquireAuthCodeRespInfo();
        do {
            if (null == info) {
                resp.ret = ErrorCode.REQUEST_INVALID.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID.getMsg();
                break;
            }
            if (StringUtil.isEmptyOrNull(info.phone)) {
                Logs.LOGIN.warn("获取验证码手机号失败, 手机号为空 info:%s", info);
                resp.ret = ErrorCode.PHONE_INVALID.getRet();
                resp.msg = ErrorCode.PHONE_INVALID.getMsg();
                break;
            }
            String ret = SMSManager.I.generateAuthCode(info.phone);
            if (StringUtil.isEmptyOrNull(ret)) {
                Logs.LOGIN.warn("生成验证码失败 info:%s", info);
                resp.ret = ErrorCode.GENERATE_AUTH_CODE_FAIL.getRet();
                resp.msg = ErrorCode.GENERATE_AUTH_CODE_FAIL.getMsg();
                break;
            }
            if ("FAIL".equals(ret)) {
                Logs.LOGIN.warn("生成验证码失败--短信内部错误 info:%s", info);
                resp.ret = ErrorCode.GENERATE_AUTH_CODE_FAIL.getRet();
                resp.msg = ErrorCode.GENERATE_AUTH_CODE_FAIL.getMsg();
                break;
            }
            if ("MOBILE_NUMBER_ILLEGAL".equals(ret)) {
                Logs.LOGIN.warn("生成验证码失败--手机号错误 info:%s", info);
                resp.ret = ErrorCode.PHONE_INVALID.getRet();
                resp.msg = ErrorCode.PHONE_INVALID.getMsg();
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
