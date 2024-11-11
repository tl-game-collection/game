package com.xiuxiu.app.server.services.account;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLAccountAuthRespInfo;
import com.xiuxiu.app.protocol.login.PLAccountTransferDataInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.account.TokenUtil;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.wechat.WeChat;
import com.xiuxiu.wechat.WeChatUserInfo;

import java.io.IOException;
import java.util.Map;

public class TransferDataHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PLAccountTransferDataInfo info = JsonUtil.fromJson(body, PLAccountTransferDataInfo.class);
        Logs.LOGIN.debug("收到数据迁移消息:%s", info);

        PLAccountAuthRespInfo resp = new PLAccountAuthRespInfo();
        Account account = null;
        do {
            if (StringUtil.isEmptyOrNull(info.token) || StringUtil.isEmptyOrNull(info.newAuthCode)) {
                Logs.LOGIN.warn("数据迁移, 无效的code或者token");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            // token 登陆
            Map<String, String> tokenInfo = TokenUtil.getInfoByToken(info.token, Config.APP_KEY);
            if (tokenInfo.size() != 3) {
                Logs.LOGIN.warn("数据迁移, token验证, 无效token:%s", info.token);
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            try {
                int loginType = Integer.parseInt(tokenInfo.get(TokenUtil.KEY_LOGIN_TYPE));
                if (6 != loginType) {
                    Logs.LOGIN.warn("数据迁移, token验证, 无效token:%s, 无效登陆类型", info.token);
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                String userName0 = tokenInfo.get(TokenUtil.KEY_USER_NAME);
                String userPasswd0 = tokenInfo.get(TokenUtil.KEY_USER_PASSWD);
                if (StringUtil.isEmptyOrNull(userName0) || null == userPasswd0) {
                    Logs.LOGIN.warn("数据迁移, token验证, 无效token:%s, 无效用户名或密码", info.token);
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                account = AccountManager.I.getAccountByOtherPlatformToken(userName0);
                if (null == account || !userPasswd0.equals(account.getPasswd())) {
                    Logs.LOGIN.warn("数据迁移, token验证, 无效token:%s, 无效用户名或密码", info.token);
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
            } catch (NumberFormatException e) {
                Logs.LOGIN.warn("数据迁移, token验证, 无效token:%s 无效登陆类型", info.token);
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (null == account) {
                Logs.LOGIN.warn("数据迁移, 获取原有账号失败");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            String[] newAccessToken = WeChat.I.getAccessTokenAndOpenIdByCode(info.newChannel, info.newAuthCode);
            if (null == newAccessToken) {
                Logs.LOGIN.warn("数据迁移, 获取accessToken失败");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            WeChatUserInfo newUserInfo = WeChat.I.getWeChatUserInfoByAccessToken(newAccessToken[0], newAccessToken[1]);
            if (null == newUserInfo) {
                Logs.LOGIN.warn("数据迁移, 获取微信用户失败");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (!AccountManager.I.updateOtherPlatformToken(account.getUid(), newUserInfo.getUid())) {
                Logs.LOGIN.warn("数据迁移, 更新token失败");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (0 != account.getState()) {
                resp.ret = ErrorCode.ACCOUNT_HAS_BEEN_BANNED.getRet();
                resp.msg = ErrorCode.ACCOUNT_HAS_BEEN_BANNED.getMsg();
                resp.port = 0;
                resp.gateway = "";
                resp.uid = -1;
                resp.token = "";
                resp.realNameAuth = false;
                Logs.LOGIN.warn("账号已被封禁, account:%s", account.getUid());
            } else {
                resp.token = TokenUtil.getToken(3, account.getOtherPlatformToken(), account.getPasswd(), Config.APP_KEY);
                resp.ret = ErrorCode.OK.getRet();
                resp.msg = ErrorCode.OK.getMsg();
                resp.gateway = Config.GATEWAY_SERVER_HOST;
                resp.port = Config.GATEWAY_SERVER_PORT;
                String ip = httpExchange.getRequestHeaders().get("X-real-ip").get(0);
                Integer port = Config.TRANSFER.get(ip);
                if (null != port) {
                    resp.gateway = ip;
                    resp.port = port;
                }
                resp.uid = account.getUid();
                resp.realNameAuth = StringUtil.isEmptyOrNull(account.getIdentityCard());
            }
        } while (false);

        byte[] respData = JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8);
        HttpServer.sendOk(httpExchange, respData);
        httpExchange.close();
    }
}
