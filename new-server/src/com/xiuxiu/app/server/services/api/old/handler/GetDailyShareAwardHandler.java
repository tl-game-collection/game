package com.xiuxiu.app.server.services.api.old.handler;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetDailyShareAwardHandler extends BaseAdminHttpHandler {
    @Override
    public ErrorMsg doHandle(String data) {
//        DailyShareAward award = JsonUtil.fromJson(data, DailyShareAward.class);
//        if (award == null || !MD5Util.getMD5(award.groupUid, award.playerUid, Config.APP_KEY).equalsIgnoreCase(award.sign)) {
//            return new ErrorMsg(ErrorCode.REQUEST_INVALID_DATA);
//        }
//        Group group = GroupManager.I.getGroupByUid(award.groupUid);
//        if (group == null) {
//            return new ErrorMsg(ErrorCode.GROUP_NOT_EXISTS);
//        }
//        if (!group.hasMember(award.playerUid)) {
//            return new ErrorMsg(ErrorCode.GROUP_NOT_IN);
//        }
//        if (group.getShareSwitch() <= 0 || group.getShareAward() < 0) {
//            return new ErrorMsg(ErrorCode.OK);
//        }
//
//        if (!group.addArenaValue(award.playerUid, group.getShareAward(), -1L, EArenaOptType.INC_SHARE)){
//            return new ErrorMsg(ErrorCode.SERVER_INTERNAL_ERROR);
//        }
//
//        Logs.PLAYER.info("group.addArenaValue daily share, playerUid:%d, value:%d", award.playerUid, group.getShareAward());
//        DailyShareAwardResp resp = new DailyShareAwardResp();
//        resp.ret = ErrorCode.OK.getRet();
//        resp.groupUid = award.groupUid;
//        resp.playerUid = award.playerUid;
//        resp.arenaValue = group.getShareAward();
//        return resp;
        return new ErrorMsg(ErrorCode.OK);
    }
}
