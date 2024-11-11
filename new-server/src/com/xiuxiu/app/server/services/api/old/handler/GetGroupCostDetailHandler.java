package com.xiuxiu.app.server.services.api.old.handler;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetGroupCostDetailHandler extends BaseAdminHttpHandler {
    @Override
    public ErrorMsg doHandle(String data) {
//        GetGroupCostDetail req = JsonUtil.fromJson(data, GetGroupCostDetail.class);
//        if (null == req || req.page < 1 || req.pageSize < 1 || req.pageSize > 100) {
//            return new ErrorMsg(ErrorCode.REQUEST_INVALID);
//        }
//        Group group = GroupManager.I.getGroupByUid(req.groupUid);
//        if (null == group) {
//            return new ErrorMsg(ErrorCode.GROUP_NOT_EXISTS);
//        }
//
//        GetGroupCostDetailResp resp = new GetGroupCostDetailResp();
//        resp.data = new GetGroupCostDetailResp.Data();
//        resp.data.groupUid = req.groupUid;
//        IGroupArenaCostDiamondDetailDAO dao = DBManager.I.getGroupArenaCostDiamondDetailDao();
//        resp.data.totalCount = dao.countByGroupUid(req.groupUid);
//        int size = Math.max(resp.data.totalCount - (req.page - 1) * req.pageSize, 0);
//        resp.data.list = new ArrayList<>(size);
//        if (size > 0) {
//            List<GroupArenaCostDiamondDetailInfo> records
//                    = dao.loadWithPageByGroupUid(req.groupUid, (req.page - 1) * req.pageSize, size);
//            for (GroupArenaCostDiamondDetailInfo rec : records) {
//                GetGroupCostDetailResp.ArenaCost cost = new GetGroupCostDetailResp.ArenaCost();
//                cost.arenaUid = rec.getGameUid();
//                cost.gameType = rec.getGameType();
//                cost.gameSubType = rec.getGameSubType();
//                cost.time = rec.getTime();
//                cost.cost = rec.getCost();
//                cost.bureau = rec.getBureau();
//                resp.data.list.add(cost);
//            }
//        }
//        return resp;
        return new ErrorMsg(ErrorCode.OK);
    }
}
