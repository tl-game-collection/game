package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AddUserArenaHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        AddUserArena info = JsonUtil.fromJson(body, AddUserArena.class);
//        Logs.API.debug("添加用户竞技值:%s", info);
//        String sign = MD5Util.getMD5(info.gid, info.uid, info.arenaValue, info.payType, Config.APP_KEY);
//        AddUserArenaResp resp = new AddUserArenaResp();
//        do {
//            if (!sign.equalsIgnoreCase(info.sign)) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            IClub club = ClubManager.I.getClubByUid(info.gid);
//            if (null == club) {
//                resp.ret = ErrorCode.GROUP_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.GROUP_NOT_EXISTS.getMsg();
//                break;
//            }
//            Player player = PlayerManager.I.getPlayer(info.uid);
//            if (null == player) {
//                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
//                break;
//            }
////            EArenaOptType type = EArenaOptType.UNKNOWN;
////            if (2 == info.payType) {
////                // 支付宝
////                type = info.arenaValue > 0 ? EArenaOptType.INC_SYS_ALIPAY : EArenaOptType.DEC_SYS_ALIPAY;
////            } else if (1 == info.payType) {
////                // 微信
////                if (info.arenaValue > 0) {
////                    type = EArenaOptType.INC_SYS_WECHAT;
////                }
////            } else if (3 == info.payType) {
////                // 银联
////                if (info.arenaValue > 0) {
////                    type = EArenaOptType.INC_SYS_UNION;
////                }
////            } else if (4 == info.payType) {
////                // 转账上分
////                if (info.arenaValue > 0) {
////                    type = EArenaOptType.INC_SYS_TRANSFER;
////                }
////            }
//            if (club.addMemberClubGold(info.uid, info.arenaValue, -1L, type)){
//                AddUserArenaResp.ArenaInfo arenaInfo = new AddUserArenaResp.ArenaInfo();
//                arenaInfo.groupUid = club.getClubUid();
//                arenaInfo.userUid = player.getUid();
//                arenaInfo.currentArenaValue = (int) club.getGold(player.getUid());
//                arenaInfo.sign = MD5Util.getMD5(arenaInfo.groupUid, arenaInfo.userUid, arenaInfo.currentArenaValue, Config.APP_KEY);
//                resp.data = arenaInfo;
//            } else {
//                resp.ret = ErrorCode.PLAYER_LACK_DIAMOND.getRet();
//                resp.msg = ErrorCode.PLAYER_LACK_DIAMOND.getMsg();
//            }
//        } while (false);
//
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
