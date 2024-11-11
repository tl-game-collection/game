package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.CreateClub;
import com.xiuxiu.app.protocol.api.temp.club.CreateClubResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.regex.Matcher;

/**
 * 创建俱乐部
 */
public class AddCreateClubHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        CreateClub info = JsonUtil.fromJson(body, CreateClub.class);
        Logs.API.debug(" 创建俱乐部 :%s", info);
        String sign = MD5Util.getMD5(info.clubUid, info.clubType, info.clubName, info.clubDesc, info.gameDesc, info.playerUid, Config.APP_KEY);
        CreateClubResp resp = new CreateClubResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (info.clubUid < 10000 || info.clubUid > 99999) {
                resp.ret = ErrorCode.API_UID_INVALID.getRet();
                resp.msg = ErrorCode.API_UID_INVALID.getMsg();
                break;
            }
            if (info.clubType < 1 || info.clubType > 2) {
                resp.ret = ErrorCode.API_TYPE_INVALID.getRet();
                resp.msg = ErrorCode.API_TYPE_INVALID.getMsg();
                break;
            }
            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (null != club) {
                resp.ret = ErrorCode.API_UID_ALREADY_EXIST.getRet();
                resp.msg = ErrorCode.API_UID_ALREADY_EXIST.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.playerUid);
            if (null == player) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            if (null == info.clubName) {
                resp.ret = ErrorCode.API_NAME_NOT_NULL.getRet();
                resp.msg = ErrorCode.API_NAME_NOT_NULL.getMsg();
                break;
            }
            if (info.clubName.length() > 14) {
                resp.ret = ErrorCode.API_NAME_LENGTH.getRet();
                resp.msg = ErrorCode.API_NAME_LENGTH.getMsg();
                break;
            }
            if (null == info.clubDesc) {
                resp.ret = ErrorCode.API_DESC_NOT_NULL.getRet();
                resp.msg = ErrorCode.API_DESC_NOT_NULL.getMsg();
                break;
            }
            if (info.clubDesc.length() > 100) {
                resp.ret = ErrorCode.API_DESC_LENGTH.getRet();
                resp.msg = ErrorCode.API_DESC_LENGTH.getMsg();
                break;
            }
//            if (null == info.gameDesc) {
//                resp.ret = ErrorCode.API_GAME_DESC_NOT_NULL.getRet();
//                resp.msg = ErrorCode.API_GAME_DESC_NOT_NULL.getMsg();
//                break;
//            }
            Matcher matcher = Constant.PATTERN_NAME.matcher(info.clubName);
            if (!matcher.matches()) {
                resp.ret = ErrorCode.API_NAME_INVALID.getRet();
                resp.msg = ErrorCode.API_NAME_INVALID.getMsg();
                break;
            }
            if (ClubManager.I.isExistName(EClubType.getType(info.clubType), info.clubName) || 0 != DBManager.I.getClubInfoDAO().isExistName(info.clubType, info.clubName)) {
                resp.ret = ErrorCode.API_NAME_ALREADY_EXIST.getRet();
                resp.msg = ErrorCode.API_NAME_ALREADY_EXIST.getMsg();
                break;
            }
            IClub iClub = ClubManager.I.create(player, info.clubType, info.clubName, info.clubDesc, "", info.gameDesc, info.clubUid);
            if (null == iClub) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            player.addOwnerClubCnt(true);
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
