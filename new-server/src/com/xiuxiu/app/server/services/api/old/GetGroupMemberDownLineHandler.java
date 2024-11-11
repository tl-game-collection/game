package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetGroupMemberDownLineHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        Logs.API.debug("收到查询群管理下线成员请求");
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetGroupMemberDownLine info = JsonUtil.fromJson(body, GetGroupMemberDownLine.class);
//        String sign = MD5Util.getMD5(info.groupUid, info.managerUid, Config.APP_KEY);
//        // System.out.println(sign);
//        GetGroupMemberDownLineResp resp = new GetGroupMemberDownLineResp();
//        do {
//            if (!sign.equalsIgnoreCase(info.sign)) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            Group group = GroupManager.I.getGroupByUid(info.groupUid);
//            if (null == group) {
//                resp.ret = ErrorCode.GROUP_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_EXISTS.getMsg();
//                break;
//            }
//            Player player = PlayerManager.I.getPlayer(info.managerUid);
//            if (null == player) {
//                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
//                break;
//            }
//            if (!group.hasMember(info.managerUid)) {
//                resp.ret = ErrorCode.GROUP_NOT_IN.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_IN.getMsg();
//                break;
//            }
//            if (!group.isManager(info.managerUid)) {
//                resp.ret = ErrorCode.GROUP_NOT_MANAGER.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_MANAGER.getMsg();
//                break;
//            }
//            group.foreach(new ICallback<GroupMemberInfo>() {
//                @Override
//                public void call(GroupMemberInfo... groupMemberInfos) {
//                    if (groupMemberInfos[0].getUplinePlayerUid() == info.managerUid) {
//                        Player temp = PlayerManager.I.getPlayer(groupMemberInfos[0].getUid());
//                        if (null == temp) {
//                            return;
//                        }
//                        GetGroupMemberDownLineResp.MemberInfo memberInfo = new GetGroupMemberDownLineResp.MemberInfo();
//
//                        memberInfo.avatar = temp.getIcon();
//                        memberInfo.nickName = temp.getName();
//                        memberInfo.playerUid = temp.getUid();
//                        memberInfo.upLinePlayerUid = groupMemberInfos[0].getUplinePlayerUid();
//                        resp.data.add(memberInfo);
//                    }
//                }
//            }, false);
//        } while (false);
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
