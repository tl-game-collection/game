package com.xiuxiu.app.server.services.api.old.handler;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.account.CreateAccountInfo;
import com.xiuxiu.app.protocol.api.account.CreateAccountInfoResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.chat.CustomerServiceManager;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量创建账号
 *
 * request:
 * {
 *     "entities": [
 *         {
 *             "uid": 10001,
 *             "role": "customerService",
 *             "password": "96e79218965eb72c92a549dd5a330112",
 *             "name": "客服10001",
 *             "icon": "http://xxx.x.x/x.png"
 *          }
 *     ]
 * }
 */
public class CreateAccountsHandler extends BaseAdminHttpHandler {
    @Override
    public ErrorMsg doHandle(String data) {
        CreateAccountInfo req = JsonUtil.fromJson(data, CreateAccountInfo.class);
        CreateAccountInfoResp response = new CreateAccountInfoResp();
        do {
            if (null == response) {
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
            List<Account> accounts = new ArrayList<>(req.getEntities().size());
            for (int i = 0; i < req.getEntities().size(); i++) {
                CreateAccountInfo.AccountInfo accountInfo = req.getEntities().get(i);
                if (!"customerService".equals(accountInfo.getRole())) {
                    return new ErrorMsg(ErrorCode.REQUEST_INVALID_DATA);
                }
                long uid = accountInfo.getUid();
                if (!CustomerServiceManager.I.isCustomerService(uid) || AccountManager.I.getAccountByUid(uid) != null) {
                    return new ErrorMsg(ErrorCode.REQUEST_INVALID_DATA);
                }
                Account account = new Account();
                account.setUid(uid);
                account.setCreateTime(System.currentTimeMillis());
                account.setPasswd(accountInfo.getPassword());
                account.setName(accountInfo.getName());
                account.setIcon(accountInfo.getIcon());
                account.setMac(accountInfo.getMac());
                String phone = accountInfo.getPhone();
                account.setPhone(StringUtil.isEmptyOrNull(phone) ? "" + uid : phone);
                account.setPhoneVer(accountInfo.getPhoneVer());
                account.setPhoneOsVer(accountInfo.getPhoneOsVer());
                account.setCity(accountInfo.getCity());
                account.setOtherPlatformToken(accountInfo.getOtherPlatformToken());
                account.setType((byte)1);
                account.setDirty(true);
                accounts.add(account);
            }

            response.ret = ErrorCode.OK.getRet();
            response.msg = ErrorCode.OK.getMsg();
            for (Account account: accounts) {
                if (DBManager.I.update(account)) {
                    response.getCreated().add(account.getUid());
                }
            }

            response.setSign(MD5Util.getMD5(response.getCreated(), Config.APP_KEY));
        } while (false);

        return response;
    }
}
