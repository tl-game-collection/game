package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetGroupListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetGroupList info = JsonUtil.fromJson(body, GetGroupList.class);
//        Logs.API.debug("根据群id查询群列表:%s", info);
//        String sign = MD5Util.getMD5(info.page, info.pageSize, Config.APP_KEY);
//        GetGroupListResp resp = new GetGroupListResp();
//        do {
//            if (!sign.equalsIgnoreCase(info.sign)) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            if (0 == info.page) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            if (0 == info.pageSize) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            if (0 != info.groupUid) {
//                // 查询单个群
//                GetGroupListResp.GroupInfo groupInfo = new GetGroupListResp.GroupInfo();
//                Group temp = GroupManager.I.getGroupByUid(info.groupUid);
//                if (null != temp) {
//                    groupInfo.gid = temp.getUid();
//                    groupInfo.groupName = temp.getName();
//                    groupInfo.groupOwner = temp.getOwner();
//                    Player owner = PlayerManager.I.getPlayer(temp.getOwner());
//                    groupInfo.groupOwnerName = owner.getName();
//                    groupInfo.totalServiceValue = temp.getTotalServiceValue();
//                    groupInfo.totalArenaValue = temp.getTotalIncArenaValue() - temp.getTotalDecArenaValue();
//                    groupInfo.totalCostDiamond = DiamondCostManager.I.loadSumByGroupUid(temp.getUid());
//                    groupInfo.totalIncArenaValueByWallet = temp.getTotalIncArenaValueByWallet();
//                    groupInfo.totalDecArenaValueByWallet = temp.getTotalDecArenaValueByWallet();
//                    resp.data.list.add(groupInfo);
//                    resp.data.count = 1;
//                }
//                resp.data.count = 0;
//            } else {
//                List<Long> lists = GroupManager.I.loadGroupUidsByPage(info.page, info.pageSize);
//                for (Long uid : lists) {
//                    GetGroupListResp.GroupInfo groupInfo = new GetGroupListResp.GroupInfo();
//                    Group temp = GroupManager.I.getGroupByUid(uid);
//                    groupInfo.gid = temp.getUid();
//                    groupInfo.groupName = temp.getName();
//                    groupInfo.groupOwner = temp.getOwner();
//                    Player owner = PlayerManager.I.getPlayer(temp.getOwner());
//                    groupInfo.groupOwnerName = owner.getName();
//                    groupInfo.totalServiceValue = temp.getTotalServiceValue();
//                    groupInfo.totalArenaValue = temp.getTotalIncArenaValue() - temp.getTotalDecArenaValue();
//                    groupInfo.totalCostDiamond = DiamondCostManager.I.loadSumByGroupUid(temp.getUid());
//                    groupInfo.totalIncArenaValueByWallet = temp.getTotalIncArenaValueByWallet();
//                    groupInfo.totalDecArenaValueByWallet = temp.getTotalDecArenaValueByWallet();
//                    resp.data.list.add(groupInfo);
//                }
//                resp.data.count = GroupManager.I.countAll();
//            }
//        } while (false);
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
