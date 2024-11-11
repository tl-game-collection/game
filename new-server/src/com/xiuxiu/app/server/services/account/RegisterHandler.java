package com.xiuxiu.app.server.services.account;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLRegAccRespInfo;
import com.xiuxiu.app.protocol.login.PLRegisterAccountInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.player.RecommendManager;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.sms.SMSManager;

import java.io.IOException;
import java.util.UUID;

public class RegisterHandler implements HttpHandler {
    private final ConcurrentHashSet<String> curRegisterPhone = new ConcurrentHashSet<>();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PLRegisterAccountInfo info = JsonUtil.fromJson(body, PLRegisterAccountInfo.class);
        Logs.LOGIN.debug("收到帐号注册消息:%s", info);

        PLRegAccRespInfo resp = new PLRegAccRespInfo();
        do {
            if (null == info) {
                Logs.LOGIN.warn("收到账号信息无效, conn:%s", httpExchange);
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (1 == info.type) {
                if (StringUtil.isEmptyOrNull(info.phone)) {
                    Logs.LOGIN.warn("手机号:%s 为空", info.phone);
                    resp.ret = ErrorCode.ACCOUNT_PHONE_NULL.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PHONE_NULL.getMsg();
                }
                if (11 != info.phone.length()) {
                    Logs.LOGIN.warn("手机号:%s 无效", info.phone);
                    resp.ret = ErrorCode.PHONE_INVALID.getRet();
                    resp.msg = ErrorCode.PHONE_INVALID.getMsg();
                    break;
                }
                if (StringUtil.isEmptyOrNull(info.passwd)) {
                    Logs.LOGIN.warn("密码:%s 为空", info.phone);
                    resp.ret = ErrorCode.ACCOUNT_PASSWD_NULL.getRet();
                    resp.msg = ErrorCode.ACCOUNT_PASSWD_NULL.getMsg();
                    break;
                }
                String sign = MD5Util.getMD5(info.phone + info.passwd + info.authCode + Config.APP_KEY);
                if (!sign.equalsIgnoreCase(info.sign)) {
                    Logs.LOGIN.warn("账号验证内容被篡改, server sign:%s client sign:%s", sign, info.sign);
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                if (this.curRegisterPhone.contains(info.phone)) {
                    Logs.LOGIN.warn("手机号:%s 正在注册", info.phone);
                    resp.ret = ErrorCode.ACCOUNT_REGISTERING.getRet();
                    resp.msg = ErrorCode.ACCOUNT_REGISTERING.getMsg();
                    break;
                }
                String authCode = SMSManager.I.getAuthCode(info.phone);
                if (Switch.PHONE_AUTH && StringUtil.isEmptyOrNull(authCode)) {
                    Logs.LOGIN.warn("注册账号-手机注册失败, 验证码失效 info:%s", info);
                    resp.ret = ErrorCode.AUTH_CODE_INVALID.getRet();
                    resp.msg = ErrorCode.AUTH_CODE_INVALID.getMsg();
                    break;
                }
                if (Switch.PHONE_AUTH && !authCode.equals(info.authCode)) {
                    Logs.LOGIN.warn("注册账号-手机号注册失败, 验证码不对 info:%s", info);
                    resp.ret = com.xiuxiu.app.protocol.ErrorCode.AUTH_CODE_ERROR.getRet();
                    resp.msg = com.xiuxiu.app.protocol.ErrorCode.AUTH_CODE_ERROR.getMsg();
                    break;
                }
                this.curRegisterPhone.add(info.phone);
            } else {
                info.passwd = MD5Util.getMD5(UUID.randomUUID().toString());
            }
            Account account = AccountManager.I.getAccountByPhone(info.phone);
            if (null != account) {
                Logs.LOGIN.warn("手机号:%s 已经注册", info.phone);
                resp.ret = ErrorCode.ACCOUNT_ALREADY_EXISTS.getRet();
                resp.msg = ErrorCode.ACCOUNT_ALREADY_EXISTS.getMsg();
                this.curRegisterPhone.remove(info.phone);
                break;
            }
            account = AccountManager.I.create((byte) (1 == info.type ? 1 : 2), info.phone, info.passwd, info.mac, info.phoneVer, info.phoneOsVer);
            if (null == account) {
                Logs.LOGIN.warn("创建账号失败, info:%s, conn:%s", info, httpExchange);
                resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
                resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
                this.curRegisterPhone.remove(info.phone);
                break;
            }
            //TBPlayer player = DBManager.I.createPlayer(account.uid, "游客" + account.uid);
            //if (null == player) {
            //    Logs.LOGIN.warn("创建玩家信息失败, account uid:%d", account.uid);
            //    resp.ret = ErrorCode.DB_ERROR.getErr();
            //    resp.msg = ErrorCode.DB_ERROR.getMsg();
            //    DBManager.I.removeAccount(account.uid, account.phone);
            //    this.curRegisterPhone.remove(info.phone);
            //    break;
            //}

            // 手机号注册成功后，如果topUid存在，则添加好友
            try {
                // 注册成功后，如果topUid存在，则添加好友
                if (-1 != info.bizChannel) {
                    long toGroupUid = Config.bizChannel2GroupUid.getOrDefault(info.bizChannel, -1L);
                    if (-1 != toGroupUid) {
                        info.topGid = toGroupUid;
                        if(info.topUid==0) {
                            info.topUid = 26128L;
                        }
                        //info.topUid = 0;
                    }
                }
                long uid = info.topUid;
                Player player = PlayerManager.I.createPlayer(account, info.bizChannel);
                player.init();
                // 加好友
                Player topPlayer = 0 == uid ? null : PlayerManager.I.getPlayer(uid);
                // 如果topGid不为0，则邀请进群
                long groupUid = info.topGid > 0 ? info.topGid : -1;
                if (0 != info.topGid) {
                    long gid = info.topGid;
                    // 加群
                    IClub club = ClubManager.I.getClubByUid(gid);

                    if (null != club) {
                        Player ownerPlayer = PlayerManager.I.getPlayer(club.getOwnerId());
                        if (club.getMemberCnt() < EPlayerPrivilegeLevel.getValue(ownerPlayer.getPrivilege(), EPlayerPrivilege.GROUP_MEMBER_NUM)) {
                            club.addMember(null == topPlayer ? club.getOwnerPlayer().getUid() : topPlayer.getUid(), player, EClubJobType.NORMAL);
                            Logs.LOGIN.debug("%d 邀请 %d 入群[%d] 成功", info.topUid, player.getUid(), info.topGid);
                        }
                        Logs.LOGIN.debug("%d 邀请 %d 入群[%d] 失败", info.topUid, player.getUid(), info.topGid);
                        Logs.LOGIN.debug(" 该群人员已满 ");
                    } else {
                        groupUid = -1;
                    }
                }
                if (null != topPlayer) {
                    // 绑定推荐用户
                    RecommendManager.I.recommend(topPlayer, player, groupUid);
                }
                player.save();
            } finally {
            }

            Logs.LOGIN.debug("手机号:%s 注册成功", info.phone);
            this.curRegisterPhone.remove(info.phone);
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
            resp.uid = account.getUid();
        } while (false);
        byte[] respData = JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8);
        HttpServer.sendOk(httpExchange, respData);
        httpExchange.close();
    }
}
