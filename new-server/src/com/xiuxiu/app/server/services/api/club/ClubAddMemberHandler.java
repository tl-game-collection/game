package com.xiuxiu.app.server.services.api.club;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.AddClubMember;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

public class ClubAddMemberHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到运营后台圈主 邀请玩家");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        AddClubMember info = JsonUtil.fromJson(body, AddClubMember.class);
        String sign = MD5Util.getMD5(info.playerUid,info.clubUid,Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            //俱乐部
            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            //群主
            Player player=(Player)club.getOwnerPlayer();
            
            Player infoPlayer = PlayerManager.I.getPlayer(info.playerUid);
            
            if (club == null) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break; 
            }
            
            if (!club.hasMember(player.getUid())){
                Logs.CLUB.warn("%s player:%d邀请人不在俱乐部中", player, info.clubUid);
                ErrorCode code = club.getClubType() == EClubType.GOLD ? ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER : ErrorCode.GROUP_NOT_IN;
                resp.ret = code.getRet();
                resp.msg = code.getMsg();
                break; 
            }
            
            if (null == infoPlayer) {
                Logs.CLUB.warn("%d clubUid:%d 被邀请玩家不存在", info.clubUid, info.playerUid);
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            
            if (club.hasMember(info.playerUid)) {
                Logs.CLUB.warn("%s infoPlayer:%d被邀请玩家已经在俱乐部中", infoPlayer, info.clubUid);
                ErrorCode code = club.getClubType() == EClubType.GOLD ? ErrorCode.CLUB_GOLD_ALREADY_IN : ErrorCode.CLUB_CARD_ALREADY_IN;
                resp.ret = code.getRet();
                resp.msg = code.getMsg();
               break;
            }
            
            Player ownerPlayer = PlayerManager.I.getPlayer(club.getOwnerId());
            if (club.getMemberCnt() > EPlayerPrivilegeLevel.getValue(ownerPlayer.getPrivilege(), EPlayerPrivilege.GROUP_MEMBER_NUM)) {
                Logs.CLUB.warn("%s infoPlayer:%d 该俱乐部人员已满 ", infoPlayer, info.clubUid);
                ErrorCode code = club.getClubType() == EClubType.GOLD ? ErrorCode.ROOM_GOLD_MEMBER_FULL : ErrorCode.CLUB_CARD_MEMBER_FULL;
                resp.ret = code.getRet();
                resp.msg = code.getMsg();
                break;
            }
            club.addMember(player.getUid(), infoPlayer, EClubJobType.NORMAL);
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();

        }while(false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
    
    
}
