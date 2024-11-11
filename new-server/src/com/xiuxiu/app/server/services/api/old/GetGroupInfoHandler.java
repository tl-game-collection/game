package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetGroupInfoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetGroupInfo info = JsonUtil.fromJson(body, GetGroupInfo.class);
//        Logs.API.debug("根据群id查询群信息:%s", info);
//        String sign = MD5Util.getMD5(info.gid, Config.APP_KEY);
//        GetGroupInfoResp resp = new GetGroupInfoResp();
//        do {
//            if (!sign.equalsIgnoreCase(info.sign)) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            Group group = GroupManager.I.getGroupByUid(info.gid);
//            if (null == group) {
//                resp.ret = ErrorCode.GROUP_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_EXISTS.getMsg();
//                break;
//            }
//            GetGroupInfoResp.GroupInfo groupInfo = new GetGroupInfoResp.GroupInfo();
//            Player ownerPlayer = PlayerManager.I.getPlayer(group.getOwner());
//            groupInfo.gid = group.getUid();
//            groupInfo.groupName = group.getName();
//            groupInfo.groupIcon = group.getIcon0();
//            groupInfo.groupOwner = group.getOwner();
//            groupInfo.groupOwnerName = ownerPlayer.getName();
//            groupInfo.sign = MD5Util.getMD5(groupInfo.gid, groupInfo.groupName, groupInfo.groupIcon, groupInfo.groupOwner, groupInfo.groupOwnerName, Config.APP_KEY);
//            resp.data = groupInfo;
//        } while (false);
//
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
