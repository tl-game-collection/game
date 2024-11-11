package com.xiuxiu.app.server.services.account;

import com.alibaba.fastjson.TypeReference;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLAccountAuthInfo;
import com.xiuxiu.app.protocol.login.PLAccountAuthRespInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.account.EAccount;
import com.xiuxiu.app.server.account.TokenUtil;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.*;
import com.xiuxiu.app.server.services.account.dingding.DingDing;
import com.xiuxiu.app.server.services.account.xianliao.XianLiao;
import com.xiuxiu.app.server.services.account.xianliao.XianLiaoUserInfo;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.*;
import com.xiuxiu.sms.SMSManager;
import com.xiuxiu.wechat.WeChat;
import com.xiuxiu.wechat.WeChatUserInfo;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AuthHandler implements HttpHandler {
    private final ConcurrentHashSet<Long> curAuthUid = new ConcurrentHashSet<>();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PLAccountAuthInfo info = JsonUtil.fromJson(body, PLAccountAuthInfo.class);
        Logs.LOGIN.debug("收到帐号验证消息:%s", info);

        PLAccountAuthRespInfo resp = new PLAccountAuthRespInfo();
        Account account = null;
        do {
            if (null == info) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }

            String userName = "";
            String userPasswd = "";
            if (1 == info.type) {
                // 手机号登陆验证
                if (StringUtil.isEmptyOrNull(info.phone)) {
                    resp.ret = ErrorCode.ACCOUNT_PHONE_NULL.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PHONE_NULL.getMsg();
                    break;
                }
                if (StringUtil.isEmptyOrNull(info.passwd)) {
                    resp.ret = ErrorCode.ACCOUNT_PASSWD_NULL.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PASSWD_NULL.getMsg();
                    break;
                }
                String sign = MD5Util.getMD5(info.phone + info.passwd + Config.APP_KEY);
                if (!sign.equalsIgnoreCase(info.sign)) {
                    Logs.LOGIN.warn("账号验证内容被篡改, server sign:%s client sign:%s", sign, info.sign);
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                account = AccountManager.I.getAccountByPhone(info.phone);
                if (null == account) {
                    Logs.LOGIN.warn("验证登陆-手机号失败, 账号不存在 info:%s", info);
                    resp.ret = ErrorCode.ACCOUNT_NOT_EXISTS.getRet();
                    resp.msg = ErrorCode.ACCOUNT_NOT_EXISTS.getMsg();
                    break;
                }
                if (!info.passwd.equalsIgnoreCase(account.getPasswd())) {
                    Logs.LOGIN.warn("验证登陆-手机号失败, 密码不对 info:%s", info);
                    resp.ret = ErrorCode.ACCOUNT_PASSWD_ERROR.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PASSWD_ERROR.getMsg();
                    break;
                }
                userName = info.phone;
                userPasswd = info.passwd;
            } else if (2 == info.type) {
                // 快速登陆
                boolean iosTest = info.phone.equalsIgnoreCase("13688888888");
                if (!iosTest){
                    iosTest = info.phone.equalsIgnoreCase("13677777777");
                    if (!iosTest){
                        iosTest = info.phone.equalsIgnoreCase("13666666666");
                    }
                }
                if (!iosTest){
                    String authCode = SMSManager.I.getAuthCode(info.phone);
                    if (Switch.PHONE_AUTH && StringUtil.isEmptyOrNull(authCode)) {
                        Logs.LOGIN.warn("快速登陆-手机号失败, 验证码失效 info:%s", info);
                        resp.ret = ErrorCode.AUTH_CODE_INVALID.getRet();
                        resp.msg = ErrorCode.AUTH_CODE_INVALID.getMsg();
                        break;
                    }
                    if (Switch.PHONE_AUTH && !authCode.equals(info.authCode)) {
                        Logs.LOGIN.warn("快速登陆-手机号失败, 验证码不对 info:%s", info);
                        resp.ret = ErrorCode.AUTH_CODE_ERROR.getRet();
                        resp.msg = ErrorCode.AUTH_CODE_ERROR.getMsg();
                        break;
                    }
                }
                account = AccountManager.I.getAccountByPhone(info.phone);
                if (null == account) {
                    int passWd = RandomUtil.random(100000, 999999);
                    if (iosTest){
                        passWd = 1568749;
                    }
                    account = AccountManager.I.create((byte) 2, info.phone, MD5Util.getMD5(String.valueOf(passWd)), "", "", "");
                }
                if (null == account) {
                    Logs.LOGIN.warn("快速登陆, 保存账号信息失败:%s", account);
                    resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
                    resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
                    break;
                }
                userName = info.phone;
                userPasswd = account.getPasswd();
            } else if (4 == info.type) {
                // 游客登陆验证
                if (StringUtil.isEmptyOrNull(info.mac)) {
                    Logs.LOGIN.warn("游客登陆-手机号失败, 平台登陆token为空 info:%s", info);
                    resp.ret = ErrorCode.ACCOUNT_PLATFORM_TOKEN_NULL.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PLATFORM_TOKEN_NULL.getMsg();
                    break;
                }
                account = AccountManager.I.getAccountByOtherPlatformToken(info.mac);
                if (null == account) {
                    account = AccountManager.I.create((byte) 0, "", "", "", "", "", info.mac);
                }
                userName = info.mac;
                userPasswd = account.getPasswd();
            } else if (3 == info.type) {
                // 微信登陆
                LoginResult result = wechatLogin(info, resp, true);
                account = result.account;
                Logs.LOGIN.debug("微信登陆account:%s", account);
                userName = result.username;
                Logs.LOGIN.debug("微信登陆userName:%s", userName);
                userPasswd = result.password;
                Logs.LOGIN.debug("微信登陆userPasswd:%s", userPasswd);
            } else if (5 == info.type) {
                // token 登陆
                Map<String, String> tokenInfo = TokenUtil.getInfoByToken(info.token, Config.APP_KEY);
                if (tokenInfo.size() != 3) {
                    Logs.LOGIN.warn("token验证, 无效token:%s", info.token);
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                try {
                    int loginType = Integer.parseInt(tokenInfo.get(TokenUtil.KEY_LOGIN_TYPE));
                    if (loginType < 1 || loginType > 8) {
                        Logs.LOGIN.warn("token验证, 无效token:%s, 无效登陆类型", info.token);
                        resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                        resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                        break;
                    }
                    String userName0 = tokenInfo.get(TokenUtil.KEY_USER_NAME);
                    String userPasswd0 = tokenInfo.get(TokenUtil.KEY_USER_PASSWD);
                    if (StringUtil.isEmptyOrNull(userName0) || null == userPasswd0) {
                        Logs.LOGIN.warn("token验证, 无效token:%s, 无效用户名或密码", info.token);
                        resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                        resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                        break;
                    }
                    if (1 == loginType) {
                        account = AccountManager.I.getAccountByPhone(userName0);
                    } else if (2 == loginType) {
                        account = AccountManager.I.getAccountByPhone(userName0);
                    } else if (3 == loginType) {
                        account = AccountManager.I.getAccountByOtherPlatformToken(userName0);
                    } else if (4 == loginType) {
                        account = AccountManager.I.getAccountByOtherPlatformToken(userName0);
                    }else  if (7 == loginType || 8 == loginType){
                        account = AccountManager.I.getAccountByOtherPlatformToken(userName0);
                    }
                    if (null == account || !userPasswd0.equals(account.getPasswd())) {
                        Logs.LOGIN.warn("token验证, 无效token:%s, 无效用户名或密码", info.token);
                        resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                        resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                        break;
                    }
                } catch (NumberFormatException e) {
                    Logs.LOGIN.warn("token验证, 无效token:%s 无效登陆类型", info.token);
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
            } else if (6 == info.type) {
                LoginResult result = wechatLogin(info, resp, false);
                account = result.account;
                if (null == account) {
                    break;
                }
                userName = result.username;
                userPasswd = result.password;
            } else if (7 == info.type) {
                // 闲聊登陆
                LoginResult result = xianLiaoLogin(info, resp, true);
                account = result.account;
                userName = result.username;
                userPasswd = result.password;
            }else if (8 == info.type) {
                LoginResult result = dingDingLogin(info, resp);
                account = result.account;
                if (null == account) {
                    break;
                }
                userName = result.username;
                userPasswd = result.password;
            }else if (9 == info.type){
                if (StringUtil.isEmptyOrNull(info.phone)) {
                    resp.ret = ErrorCode.ACCOUNT_PHONE_NULL.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PHONE_NULL.getMsg();
                    break;
                }
                if (StringUtil.isEmptyOrNull(info.passwd)) {
                    resp.ret = ErrorCode.ACCOUNT_PASSWD_NULL.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PASSWD_NULL.getMsg();
                    break;
                }
                String sign = MD5Util.getMD5(info.phone + info.passwd + Config.APP_KEY);
                if (!sign.equalsIgnoreCase(info.sign)) {
                    Logs.LOGIN.warn("账号验证内容被篡改, server sign:%s client sign:%s", sign, info.sign);
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                info.authCode = StringUtil.isEmptyOrNull(info.authCode) ? "" : info.authCode;
                account = AccountManager.I.getAccountByPhone(info.phone);
                if (null == account || (account != null && !StringUtil.isEmptyOrNull(info.authCode))) {
                    String authCode = SMSManager.I.getAuthCode(info.phone);
                    if (Switch.PHONE_AUTH && StringUtil.isEmptyOrNull(authCode)) {
                        Logs.LOGIN.warn("账号密码登陆-手机号失败, 验证码失效 info:%s", info);
                        resp.ret = ErrorCode.AUTH_CODE_INVALID.getRet();
                        resp.msg = ErrorCode.AUTH_CODE_INVALID.getMsg();
                        break;
                    }

                    if (Switch.PHONE_AUTH && !authCode.equals(info.authCode)) {
                        Logs.LOGIN.warn("账号密码登陆-手机号失败, 验证码不对 info:%s", info);
                        resp.ret = ErrorCode.AUTH_CODE_ERROR.getRet();
                        resp.msg = ErrorCode.AUTH_CODE_ERROR.getMsg();
                        break;
                    }
                    if (account == null) {
                        account = AccountManager.I.create((byte) EAccount.PWD_LOGIN.getValue(), info.phone, MD5Util.getMD5(String.valueOf(info.passwd)), "", "", "");
                    }else{
                        if (!AccountManager.I.updatePasswdByUid(account.getUid(), MD5Util.getMD5(String.valueOf(info.passwd)))) {
                            Logs.LOGIN.warn("重置密码失败, 保存数据库失败info:%s", info);
                            resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
                            resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
                            break;
                        }
                    }
                }
                if (null == account) {
                    Logs.LOGIN.warn("账号密码登陆, 保存账号信息失败:%s", account);
                    resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
                    resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
                    break;
                }
                if (!account.getPasswd().equals(MD5Util.getMD5(String.valueOf(info.passwd)))){
                    Logs.LOGIN.warn("验证登陆-手机号失败, 密码不对 info:%s", info);
                    resp.ret = ErrorCode.ACCOUNT_PASSWD_ERROR.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PASSWD_ERROR.getMsg();
                    break;
                }
                userName = info.phone;
                userPasswd = account.getPasswd();
            }
            if (null == account) {
                resp.ret = ErrorCode.ACCOUNT_USERNAME_OR_PASSWD_ERROR.getRet();
                resp.msg = ErrorCode.ACCOUNT_USERNAME_OR_PASSWD_ERROR.getMsg();
                Logs.LOGIN.warn("账号获取失败, phone:%s passwd:%s type:%d", info.phone, info.passwd, info.type);
                break;
            }

            if (5 != info.type) {
                if (2 == info.type) {
                    //返还随机生成的密码给客户端方便下次直接登录
                    resp.token = AESEncode(info.passwd, userPasswd);
                } else {
                    resp.token = TokenUtil.getToken(info.type, userName, userPasswd, Config.APP_KEY);
                }
            } else {
                resp.token = info.token;
            }
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
            if (Config.DEVELOP) {
                resp.gateway = String.format("ws://%s:%d/game", Config.GATEWAY_SERVER_HOST, Config.GATEWAY_SERVER_PORT);
            } else {
                resp.gateway = String.format("ws://%s/game", Config.GATEWAY_SERVER_HOST);
            }
            resp.port = Config.GATEWAY_SERVER_PORT;
            String ip = httpExchange.getRequestHeaders().get("X-real-ip").get(0);
            Integer port = Config.TRANSFER.get(ip);
            if (null != port) {
                resp.gateway = ip;
                resp.port = port;
            }
            resp.uid = account.getUid();
            resp.realNameAuth = StringUtil.isEmptyOrNull(account.getIdentityCard());
            Logs.LOGIN.debug("帐号验证成功:%s resp:%s", info, resp);
        } while (false);

        // 判断账号是否封禁
        if (null != account && 0 != account.getState()) {
            resp.ret = ErrorCode.ACCOUNT_HAS_BEEN_BANNED.getRet();
            resp.msg = ErrorCode.ACCOUNT_HAS_BEEN_BANNED.getMsg();
            resp.port = 0;
            resp.gateway = "";
            resp.uid = -1;
            resp.token = "";
            resp.realNameAuth = false;
            Logs.LOGIN.warn("账号已被封禁, account:%s", info.phone);
        }
        if (null != account && 3 != info.type && 6 != info.type && 7 != info.type && 8 != info.type) {
            this.bindingRecommendAndAddGroup(account, account.getUid(), info.topUid, info.topGid, info.bizChannel);
        }
        String json = JsonUtil.toJson(resp);
        Logs.LOGIN.debug("JsonUtil.toJson:%s", json);
        byte[] respData = json.getBytes(Charsetutil.UTF8);
        HttpServer.sendOk(httpExchange, respData);
        httpExchange.close();
        Logs.LOGIN.debug("收到帐号验证消息OVER！");
    }

    private LoginResult dingDingLogin(PLAccountAuthInfo info, PLAccountAuthRespInfo resp) {

        LoginResult result = new LoginResult();

        // 钉钉登陆
        if (StringUtil.isEmptyOrNull(info.authCode)) {
            Logs.LOGIN.warn("钉钉验证, code为空");
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }
        Map<String,Object> userInfo = DingDing.I.getUserInfoByCode(info.authCode);
        if (null == userInfo) {
            Logs.LOGIN.warn("钉钉验证, 获取用户信息失败 code:%s", info.authCode);
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }
        Object tempObject = userInfo.get("errcode");
        if(null == tempObject || Integer.valueOf(tempObject.toString())!=0){
            Logs.LOGIN.warn("钉钉验证, 获取用户信息失败 code:%s", info.authCode);
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }
        tempObject = userInfo.get("user_info");
        Map<String, Object> tempInfo = JsonUtil.fromJson(tempObject.toString(), new TypeReference<HashMap<String, Object>>() {
        });
        String openid = tempInfo.get("openid").toString();

        Logs.LOGIN.debug("钉钉验证, 获取用户信息 %s %s", info,userInfo);
        result.account = AccountManager.I.getAccountByOtherPlatformToken(openid);

        if (null == result.account) {
            result.account = AccountManager.I.create((byte) 4, "", "", "", "", "", openid, tempInfo.get("nick").toString(), "", (byte)1, "",0);
        }
        if (null == result.account) {
            Logs.LOGIN.warn("钉钉验证, 保存账号信息失败:%s", result.account);
            resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
            resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
            return result;
        }

        Player player = PlayerManager.I.getPlayer(result.account.getUid());
        resp.status = null != player ? 0 : 1;
        if (this.curAuthUid.add(result.account.getUid())) {
            try {
                this.bindingRecommendAndAddGroup(result.account, result.account.getUid(), info.topUid, info.topGid, info.bizChannel);
                resp.status = 0;
            } finally {
                this.curAuthUid.remove(result.account.getUid());
            }
        }
        result.username = openid;
        result.password = result.account.getPasswd();
        result.err = false;
        return result;
    }


    private LoginResult wechatLogin(PLAccountAuthInfo info, PLAccountAuthRespInfo resp, boolean createIfNotExists) {
        LoginResult result = new LoginResult();

        // 微信登陆
        if (StringUtil.isEmptyOrNull(info.authCode)) {
            Logs.LOGIN.warn("微信验证, code为空");
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }
        String[] accessToken = WeChat.I.getAccessTokenAndOpenIdByCode(info.channel, info.authCode);
        if (null == accessToken) {
            Logs.LOGIN.warn("微信验证, 获取accessToken失败 code:%s", info.authCode);
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }

        WeChatUserInfo userInfo = WeChat.I.getWeChatUserInfoByAccessToken(accessToken[0], accessToken[1]);
        if (null == userInfo) {
            Logs.LOGIN.warn("微信验证, 获取微信用户失败");
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }
        Logs.LOGIN.debug("微信验证, 获取微信用户 %s %s", userInfo.getUid(), info);
        result.account = AccountManager.I.getAccountByOtherPlatformToken(userInfo.getUid());

        if (!createIfNotExists) {
            if (null != result.account && !result.account.getOtherPlatformToken().equals(userInfo.getUid())) {
                result.account = null;
            }
            if (null != result.account) {
                result.username = userInfo.getUid();
                result.password = result.account.getPasswd();
            }
            resp.token = userInfo.getUid();
            resp.status = null == result.account ? 3 : 2;
            result.err = false;
            return result;
        }
        if (null == result.account) {
            result.account = AccountManager.I.create((byte) 3, "", "", "", "", "", userInfo.getUid(), userInfo.getNick(), userInfo.getIcon(), userInfo.getSex(), userInfo.getCity(),0);
        }
        if (null == result.account) {
            Logs.LOGIN.warn("微信验证, 保存账号信息失败:%s", result.account);
            resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
            resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
            return result;
        }

        Player player = PlayerManager.I.getPlayer(result.account.getUid());
        resp.status = null != player ? 0 : 1;
        if (this.curAuthUid.add(result.account.getUid())) {
            try {
                this.bindingRecommendAndAddGroup(result.account, result.account.getUid(), info.topUid, info.topGid, info.bizChannel);
                resp.status = 0;
            } finally {
                this.curAuthUid.remove(result.account.getUid());
            }
        }
        result.username = userInfo.getUid();
        result.password = result.account.getPasswd();
        result.err = false;
        return result;
    }

    private LoginResult xianLiaoLogin(PLAccountAuthInfo info, PLAccountAuthRespInfo resp, boolean createIfNotExists) {
        LoginResult result = new LoginResult();

        // 闲聊登陆
        if (StringUtil.isEmptyOrNull(info.authCode)) {
            Logs.LOGIN.warn("闲聊验证, code为空");
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }
        String[] data = XianLiao.I.getAccessTokenByCode(info.authCode);//{"1450421aca3bcac4be72e0492a5db502"};
        if (null == data) {
            Logs.LOGIN.warn("闲聊验证, 获取accessToken失败 code:%s", info.authCode);
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }

        XianLiaoUserInfo userInfo = XianLiao.I.getXianLiaoUserInfoByAccessToken(data[0]);
        if (null == userInfo) {
            Logs.LOGIN.warn("闲聊验证, 获取闲聊用户失败");
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            return result;
        }
        Logs.LOGIN.debug("闲聊验证, 获取闲聊用户 %s %s", userInfo.getOpenId(), info);
        result.account = AccountManager.I.getAccountByOtherPlatformToken(userInfo.getOpenId());

        if (!createIfNotExists) {
            if (null != result.account && !result.account.getOtherPlatformToken().equals(userInfo.getOpenId())) {
                result.account = null;
            }
            if (null != result.account) {
                result.username = userInfo.getOpenId();
                result.password = result.account.getPasswd();
            }
            resp.token = userInfo.getOpenId();
            resp.status = null == result.account ? 3 : 2;
            result.err = false;
            return result;
        }
        if (null == result.account) {
            int m_sex = 1;
            if (userInfo.getGender() == 2) {
                m_sex = 0;
            }
            result.account = AccountManager.I.create((byte) 7, "", "", "", "", "", userInfo.getOpenId(), userInfo.getNickName(), userInfo.getSmallAvatar(), (byte) m_sex, "",0);
        }
        if (null == result.account) {
            Logs.LOGIN.warn("闲聊验证, 保存账号信息失败:%s", result.account);
            resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
            resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
            return result;
        }

        Player player = PlayerManager.I.getPlayer(result.account.getUid());
        resp.status = null != player ? 0 : 1;
        if (this.curAuthUid.add(result.account.getUid())) {
            try {
                this.bindingRecommendAndAddGroup(result.account,result.account.getUid(),info.topUid,info.topGid,info.bizChannel);
                resp.status = 0;
            } finally {
                this.curAuthUid.remove(result.account.getUid());
            }
        }
        result.username = userInfo.getOpenId();
        result.password = result.account.getPasswd();
        result.err = false;
        return result;
    }

    private void bindingRecommendAndAddGroup(Account account, long playerUid, long toPlayerUid, long toGroupUid, int bizChannel) {
        if (toGroupUid <= 0 && toPlayerUid <= 0) {
            return;
        }
        Player topPlayer = PlayerManager.I.getPlayer(toPlayerUid);
        if (null == topPlayer) {
            return;
        }
        if (topPlayer.getUid() == playerUid) {
            return;
        }
        Player player = PlayerManager.I.getPlayer(playerUid);
        if (null == player) {
            player = PlayerManager.I.createPlayer(account, bizChannel);
            player.init();
        }
        IClub club = ClubManager.I.getClubByUid(toGroupUid);
        if (null != club) {
            boolean isAddGroup = true;
            if (player.getRecommendInfo().getRecommendPlayerUid() > 0 && player.getRecommendInfo().getRecommendPlayerUid() != topPlayer.getUid()) {
                if (player.isClub() > 0) {
                    isAddGroup = false;
                }
            }

            if (isAddGroup) {
                club.addMember(topPlayer.getUid(), player, EClubJobType.NORMAL);
                Logs.LOGIN.debug("%d 邀请 %d 入群[%d] 成功", toPlayerUid, player.getUid(), toGroupUid);
            }
        } else {
            if (player.getRecommendInfo().getRecommendPlayerUid() < 0) {
                RecommendManager.I.recommend(topPlayer, player, toGroupUid);
                player.send(CommandId.CLI_NTF_PLAYER_RECOMMEND_OK, null);
            }
        }
        player.save();
    }

    private class LoginResult {
        Account account = null;
        String username = "";
        String password = "";
        boolean err = true;
    }

    private static String AESEncode(String key, String content) {
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, 16);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);
            byte[] byte_encode = content.getBytes("utf-8");
            byte[] byte_AES = cipher.doFinal(byte_encode);
            return Base64.getEncoder().encodeToString(byte_AES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
