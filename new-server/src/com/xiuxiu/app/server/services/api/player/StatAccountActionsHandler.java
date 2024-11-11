package com.xiuxiu.app.server.services.api.player;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.account.StatAccountActions;
import com.xiuxiu.app.protocol.api.temp.account.StatAccountActionsResp;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.services.api.old.stat.BaseStatHttpHandler;
import com.xiuxiu.app.server.statistics.EAccountAction;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class StatAccountActionsHandler extends BaseStatHttpHandler {
    @Override
    public ErrorMsg doHandle(String data) {
        StatAccountActions req = JsonUtil.fromJson(data, StatAccountActions.class);
        if (req == null || req.timeEnd <= req.timeBegin || req.period < 3600
                || (req.timeEnd - req.timeBegin) / req.period > 1000) {
            return new ErrorMsg(ErrorCode.REQUEST_INVALID_DATA);
        }

        if (req.action.equals(EAccountAction.REGISTER.string())) {
            return statRegistrations(req.timeBegin, req.timeEnd, req.period);
        }
        return new ErrorMsg(ErrorCode.REQUEST_INVALID_DATA);
    }

    private StatAccountActionsResp statRegistrations(long timeBegin, long timeEnd, int period) {
        List<Long> records = DBManager.I.getLogAccountDAO().loadTimeByAction(EAccountAction.REGISTER.ordinal(),
                timeBegin, timeEnd);
        StatAccountActionsResp result = new StatAccountActionsResp(ErrorCode.OK);
        result.data = new StatAccountActionsResp.Data(EAccountAction.REGISTER.string(), timeBegin, timeEnd, period);
        result.data.list = new ArrayList<>();
        for (int i = 0, len = (int) ((timeEnd - timeBegin) / period); i <= len; i++) {
            result.data.list.add(0L);
        }
        for (Long timestamp : records) {
            int idx = (int) (timestamp - timeBegin) / period;
            result.data.list.set(idx, result.data.list.get(idx) + 1);
        }
        return result;
    }
}
