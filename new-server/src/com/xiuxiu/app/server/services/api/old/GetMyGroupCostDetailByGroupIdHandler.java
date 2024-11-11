package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetMyGroupCostDetailByGroupIdHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetMyGroupCostDetailByGroupId info = JsonUtil.fromJson(body, GetMyGroupCostDetailByGroupId.class);
//        Logs.API.debug("根据群id查询钻石消耗记录:%s", info);
//        String sign = MD5Util.getMD5(info.groupId, info.limit, info.offset, Config.APP_KEY);
//        GetMyGroupCostDetailByGroupIdResp resp = new GetMyGroupCostDetailByGroupIdResp();
//        do {
//            if (!sign.equalsIgnoreCase(info.sign)) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            Group group = GroupManager.I.getGroupByUid(info.groupId);
//            if (null == group) {
//                resp.ret = ErrorCode.GROUP_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_EXISTS.getMsg();
//                break;
//            }
//            IGroupArenaCostDiamondDetailDAO dao = DBManager.I.getGroupArenaCostDiamondDetailDao();
//            List<GroupArenaCostDiamondDetailInfo> groupArenaCostDiamondDetailInfoList = dao.loadWithPageByGroupUid(info.groupId, info.offset, info.limit);
//            resp.data = new GetMyGroupCostDetailByGroupIdResp.Data();
//            resp.data.list = new ArrayList<>();
//            resp.data.groupId = group.getUid();
//            resp.data.groupIcon = group.getIcon0();
//            resp.data.groupName = group.getName();
//            for (GroupArenaCostDiamondDetailInfo groupArenaCostDiamondDetailInfo : groupArenaCostDiamondDetailInfoList){
//                GetMyGroupCostDetailByGroupIdResp.GroupCostDiamondDetailInfo groupCostDiamondDetailInfo = new GetMyGroupCostDetailByGroupIdResp.GroupCostDiamondDetailInfo();
//                groupCostDiamondDetailInfo.cost = groupArenaCostDiamondDetailInfo.getCost();
//                groupCostDiamondDetailInfo.time = groupArenaCostDiamondDetailInfo.getTime();
//                resp.data.list.add(groupCostDiamondDetailInfo);
//            }
//        } while (false);
//
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
