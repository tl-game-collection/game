package com.xiuxiu.app.server.services.api.old.handler;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.account.GetAccountInfo;
import com.xiuxiu.app.protocol.api.account.GetAccountInfoResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取账号信息
 * {
 *     "entities": [
 *         {
 *             "action": "register",
 *             "startUid": 1,
 *             "count": 10
 *         },
 *         {
 *             "action": "online"
 *         }
 *     ]
 * }
 */
public class GetAccountsHandler extends BaseAdminHttpHandler {
    @Override
    public ErrorMsg doHandle(String data) {
        GetAccountInfo req = JsonUtil.fromJson(data, GetAccountInfo.class);
        GetAccountInfoResp response = new GetAccountInfoResp();
        do {
            if (null == req) {
                response.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                response.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            String sign = MD5Util.getMD5(req.getEntities(), Config.APP_KEY);
            if (!sign.equals(req.getSign())) {
                response.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                response.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            long now = System.currentTimeMillis() / 1000;
            for (int i = 0; i < req.getEntities().size(); i++) {
                GetAccountInfo.ActionInfo info = req.getEntities().get(i);
                GetAccountInfoResp.ActionRespInfo respInfo = new GetAccountInfoResp.ActionRespInfo();
                respInfo.setAction(info.getAction());

                if ("register".equals(info.getAction())) {
                    List<Account> accounts = DBManager.I.getAccountDao().loadAccountsByUidStartFrom(info.getStartUid(), info.getCount());
                    List<GetAccountInfoResp.AccountInfo> accountInfos = new ArrayList<>();
                    for (Account acc: accounts) {
                        GetAccountInfoResp.AccountInfo accountInfo = new GetAccountInfoResp.AccountInfo();
                        accountInfo.setUid(acc.getUid());
                        accountInfo.setCreateTime(acc.getCreateTime());
                        accountInfo.setMac(acc.getMac());
                        accountInfo.setPhone(acc.getPhone());
                        accountInfo.setPhoneVer(acc.getPhoneVer());
                        accountInfo.setPhoneOsVer(acc.getPhoneOsVer());
                        accountInfo.setName(acc.getName());
                        accountInfo.setIcon(acc.getIcon());
                        accountInfo.setSex(acc.getSex());
                        accountInfo.setCity(acc.getCity());
                        accountInfo.setIdentityCard(acc.getIdentityCard());
                        accountInfo.setType(acc.getType());
                        accountInfos.add(accountInfo);
                    }
                    respInfo.setData(accountInfos);
                } else if ("online".equals(info.getAction())) {
                    int count = PlayerManager.I.countOfOnlinePlayers();
                    GetAccountInfoResp.OnlineInfo onlineInfo = new GetAccountInfoResp.OnlineInfo();
                    onlineInfo.setCount(count);
                    onlineInfo.setTimestamp(now);
                    respInfo.setData(onlineInfo);
                }
            }
            response.setSign(MD5Util.getMD5(response.getEntities(), Config.APP_KEY));
        } while (false);

        return response;
    }
}
