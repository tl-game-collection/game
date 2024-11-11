package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetGroupInfoByGidHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetGroupInfoByGid info = JsonUtil.fromJson(body, GetGroupInfoByGid.class);
//        Logs.API.debug("根据群id及用户id查询群信息:%s", info);
//        String sign = MD5Util.getMD5(info.gid, info.uid, Config.APP_KEY);
//        GetGroupInfoByGidResp resp = new GetGroupInfoByGidResp();
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
//            Group group = GroupManager.I.getGroupByUid(info.gid);
//            BaseArena arena = (BaseArena) ArenaManager.I.getArena(info.gid);
//            Group arenaWithGroup = null == arena ? null : GroupManager.I.getGroupByUid(arena.getInfo().getGroupUid());
//            Group realGroup = null;
//            if (null != group) {
//                if (group.hasMember(player.getUid())) {
//                    realGroup = group;
//                }
//            }
//            if (null != arenaWithGroup && null == realGroup) {
//                if (arenaWithGroup.hasMember(player.getUid())) {
//                    realGroup = arenaWithGroup;
//                }
//            }
//            if (null == realGroup) {
//                Logs.API.warn("根据群id及用户id查询群信息 群不存在");
//                resp.ret = ErrorCode.GROUP_NOT_IN.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_IN.getMsg();
//                break;
//            }
//            if (!player.hasGroup(realGroup.getUid())) {
//                Logs.API.warn("根据群id及用户id查询群信息 不在群里");
//                resp.ret = ErrorCode.GROUP_NOT_IN.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_IN.getMsg();
//                break;
//            }
//            GetGroupInfoByGidResp.GroupInfo groupInfo = new GetGroupInfoByGidResp.GroupInfo();
//            Player ownerPlayer = PlayerManager.I.getPlayer(realGroup.getOwner());
//            groupInfo.gid = realGroup.getUid();
//            groupInfo.uid = player.getUid();
//            groupInfo.groupName = realGroup.getName();
//            groupInfo.groupOwner = realGroup.getOwner();
//            groupInfo.groupOwnerName = ownerPlayer.getName();
//            groupInfo.sign = MD5Util.getMD5(groupInfo.gid, groupInfo.uid, groupInfo.groupName, groupInfo.groupOwner, groupInfo.groupOwnerName, Config.APP_KEY);
//            resp.data = groupInfo;
//        } while (false);
//
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
