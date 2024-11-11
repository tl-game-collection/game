package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetMyGroupCostSumByGroupIdHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetMyGroupCostSumByGroupId info = JsonUtil.fromJson(body, GetMyGroupCostSumByGroupId.class);
//        Logs.API.debug("根据用户id查询所有群分别钻石总消耗:%s", info);
//        String sign = MD5Util.getMD5(info.uid, info.groupId, info.limit, info.offset, Config.APP_KEY);
//        GetMyGroupCostSumByGroupIdResp resp = new GetMyGroupCostSumByGroupIdResp();
//        do {
//            if (!sign.equalsIgnoreCase(info.sign)) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            Player player = PlayerManager.I.getPlayer(info.uid);
//            if (null == player) {
//                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
//                break;
//            }
//            IGroupArenaCostDiamondDetailDAO dao = DBManager.I.getGroupArenaCostDiamondDetailDao();
//            List<GroupCostDiamondSumInfo> groupCostDiamondSumInfos = dao.sumCostByGroup(info.uid, info.groupId, info.limit, info.offset);
//            resp.data = new GetMyGroupCostSumByGroupIdResp.Data();
//            resp.data.list = new ArrayList<>();
//            for (GroupCostDiamondSumInfo groupCostDiamondSumInfo : groupCostDiamondSumInfos){
//                GetMyGroupCostSumByGroupIdResp.GroupCostDiamondSumInfo groupCostDiamondSumInfo1 = new GetMyGroupCostSumByGroupIdResp.GroupCostDiamondSumInfo();
//                groupCostDiamondSumInfo1.uid = groupCostDiamondSumInfo.getUid();
//                groupCostDiamondSumInfo1.name = groupCostDiamondSumInfo.getName();
//                groupCostDiamondSumInfo1.icon = groupCostDiamondSumInfo.getIcon();
//                groupCostDiamondSumInfo1.costSum = groupCostDiamondSumInfo.getGroupCostSum();
//                resp.data.list.add(groupCostDiamondSumInfo1);
//            }
//        } while (false);
//
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
