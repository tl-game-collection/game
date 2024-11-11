package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetGroupMembersHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetGroupMembers info = JsonUtil.fromJson(body, GetGroupMembers.class);
//        Logs.API.debug("根据群id查询群成员列表:%s", info);
//        String sign = MD5Util.getMD5(info.playerUid, info.groupUid, Config.APP_KEY);
//        GetGroupMembersResp resp = new GetGroupMembersResp();
//        do {
//            if (!sign.equalsIgnoreCase(info.sign)) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            if (0 == info.groupUid || 0 == info.playerUid) {
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
//            Player player = PlayerManager.I.getPlayer(info.playerUid);
//            if (null == player) {
//                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
//                break;
//            }
//            // 查询单个群
//            GetGroupMembersResp.GroupMember groupInfo = new GetGroupMembersResp.GroupMember();
//            GroupMemberInfo groupMemberInfo = group.getMemberInfo(info.playerUid);
//            groupInfo.playerUid = player.getUid();
//            groupInfo.uplinePlayerUid = groupMemberInfo.getUplinePlayerUid();
//            groupInfo.memberType = groupMemberInfo.getMemberType();
//            groupInfo.channelId = player.getBizChannel();
//            for(GroupMemberInfo memberInfo: group.getMemberMap().values()) {
//                if (memberInfo.getUplinePlayerUid() == player.getUid()) {
//                    groupInfo.downlinePlayer.add(memberInfo.getUid());
//                }
//            }
//            resp.data = groupInfo;
//        } while (false);
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
