package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetMyGroupsInfoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetMyGroupsInfo info = JsonUtil.fromJson(body, GetMyGroupsInfo.class);
//        Logs.API.debug("获取用户群信息:%s", info);
//        String sign = MD5Util.getMD5(info.uid, info.gname, Config.APP_KEY);
//        GetMyGroupsInfoResp resp = new GetMyGroupsInfoResp();
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
//            player.forEachByGroup(new ICallback<Long>() {
//                @Override
//                public void call(Long... groupUid) {
//                    Group group = GroupManager.I.getGroupByUid(groupUid[0]);
//                    if (null != group) {
//                        GetMyGroupsInfoResp.GroupInfo groupInfo = new GetMyGroupsInfoResp.GroupInfo();
//                        groupInfo.groupUid = groupUid[0];
//                        groupInfo.groupName = group.getName();
//                        groupInfo.groupAvatar = group.getIcon0();
//                        groupInfo.totalCost = group.getTotalCostByType(EMoneyType.DIAMOND);
//                        groupInfo.sign = MD5Util.getMD5(groupInfo.groupUid, groupInfo.groupName, groupInfo.groupAvatar, groupInfo.totalCost, Config.APP_KEY);
//                        resp.data.add(groupInfo);
//                    }
//                }
//            });
//        } while (false);
//
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
