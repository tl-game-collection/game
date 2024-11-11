package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.server.Logs;

public class SetGroupMemberUpLineHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到设置群成员上线请求");
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        SetGroupMembersUpLine info = JsonUtil.fromJson(body, SetGroupMembersUpLine.class);
//        String sign = MD5Util.getMD5(info.groupUid, info.sourceUid, info.targetUid, Config.APP_KEY);
//        System.out.println(sign);
//        ErrorMsg resp = new ErrorMsg();
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
//            Player sourcePlayer = PlayerManager.I.getPlayer(info.sourceUid);
//            if (null == sourcePlayer) {
//                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
//                break;
//            }
//            Player targetPlayer = PlayerManager.I.getPlayer(info.targetUid);
//            if (null == targetPlayer) {
//                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
//                break;
//            }
//            if (!group.hasMember(info.sourceUid) || !group.hasMember(info.targetUid)) {
//                resp.ret = ErrorCode.GROUP_NOT_IN.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_IN.getMsg();
//                break;
//            }
//            if (!group.isManager(info.sourceUid) || !group.isManager(info.targetUid)) {
//                resp.ret = ErrorCode.GROUP_NOT_MANAGER.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_MANAGER.getMsg();
//                break;
//            }
//            if (info.ids.length < 1) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            for( long memberUid : info.ids) {
//                group.setUpline(info.targetUid, memberUid);
//            }
//        } while (false);
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
