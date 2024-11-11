package com.xiuxiu.app.server.services.api.old.stat;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.account.GetAccountActions;
import com.xiuxiu.app.protocol.api.temp.account.GetAccountActionsResp;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.statistics.EAccountAction;
import com.xiuxiu.app.server.statistics.LogAccount;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class QueryAccountActionsHandler extends BaseStatHttpHandler {
    @Override
    public ErrorMsg doHandle(String data) {
        GetAccountActions req = JsonUtil.fromJson(data, GetAccountActions.class);
        if (req.page < 1 || req.perPage < 1 || req.perPage > 100) {
            return new ErrorMsg(ErrorCode.REQUEST_INVALID_DATA);
        }

        EAccountAction actionEnum = EAccountAction.parse(req.action);
        int action = actionEnum.code();
        int page = req.page > 0 ? req.page : 1;
        int perPage = req.perPage > 0 ? req.perPage : 10;
        return queryActions(req.targetUid, action, req.timeBegin, req.timeEnd, page, perPage);
    }

    private GetAccountActionsResp queryActions(long targetUid, int action, long timeBegin, long timeEnd, int page, int perPage) {
        GetAccountActionsResp resp = new GetAccountActionsResp(ErrorCode.OK);
        resp.data = new GetAccountActionsResp.Data();
        if (1 == page) {
            resp.data.totalCount = DBManager.I.getLogAccountDAO().count(targetUid, action, timeBegin, timeEnd);
        }

        long limitOffset = (page - 1) * perPage;
        List<LogAccount> records = DBManager.I.getLogAccountDAO().load(targetUid, action, timeBegin, timeEnd, limitOffset, perPage);
        resp.data.entities = new ArrayList<>(records.size());
        for (LogAccount rec : records) {
            GetAccountActionsResp.AccountAction accountAction = accountLogToAction(rec);
            resp.data.entities.add(accountAction);
        }
        return resp;
    }

    private GetAccountActionsResp.AccountAction accountLogToAction(LogAccount log) {
        GetAccountActionsResp.AccountAction action = new GetAccountActionsResp.AccountAction();
        action.actionUid = log.getUid();
        action.timestamp = log.getTimestamp();
        action.accountUid = log.getTargetUid();
        action.accountType = log.getAccountType();
        action.serverId = log.getServerId();
        action.device = log.getDeviceModel();
        action.deviceId = log.getDeviceSn();
        action.osVersion = log.getOsVersion();
        action.address = log.getAddress();
        action.clientVersion = log.getAppVersion();
        action.channelId = log.getChannelId();
        action.mobileNumber = log.getMobileNumber();
        return action;
    }
}
