package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberList;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberListResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;

/**
 * 获取自己所属群成员列表
 */
public class GetClubMemberListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到获取自己所属群成员列表请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubMemberList info = JsonUtil.fromJson(body, GetClubMemberList.class);
        String sign = MD5Util.getMD5(info.playerUid, info.clubUid, info.searchUid, info.page, info.pageSize, Config.APP_KEY);
        GetClubMemberListResp resp = new GetClubMemberListResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
            if (iClub == null) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (iClub.getOwnerId() != info.playerUid) {
                resp.ret = ErrorCode.CLUB_NO_PRIVILEGE.getRet();
                resp.msg = ErrorCode.CLUB_NO_PRIVILEGE.getMsg();
                break;
            }
            resp.playerUid = info.playerUid;
            resp.clubUid = info.clubUid;
            resp.page = info.page;
            resp.pageSize = info.pageSize;
            resp.next = true;
            if (info.searchUid == 0) {
                for (int i = info.page * info.pageSize; i < (info.page + 1)*info.pageSize; i++) {
                    if (i >= iClub.getAllMemberUids().size()) {
                        resp.next = false;
                        break;
                    }
                    Player tempPlayer = PlayerManager.I.getPlayer(iClub.getAllMemberUids().get(i));
                    GetClubMemberListResp.PlayerListInfo playerListInfo = new GetClubMemberListResp.PlayerListInfo();
                    playerListInfo.uid = iClub.getAllMemberUids().get(i);
                    if (tempPlayer != null) {
                        playerListInfo.name = tempPlayer.getName();
                        playerListInfo.icon = tempPlayer.getIcon();
                    }
                    ClubMemberExt clubMemberExt = iClub.getMemberExt(tempPlayer.getUid(),false);
                    if (clubMemberExt != null) {
                        playerListInfo.gold = clubMemberExt.getGold();//竞技分
                        playerListInfo.reward = clubMemberExt.getRewardValue();//奖励分
                    }
                    playerListInfo.clubName=iClub.getName();
                    playerListInfo.clubOwnerId=info.playerUid;

                    resp.list.add(playerListInfo);
                    resp.totalSize=iClub.getAllMemberUids().size();
                }
            } else {
                Player tempPlayer = PlayerManager.I.getPlayer(info.searchUid);
                
                
                
                if (tempPlayer == null) {
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                GetClubMemberListResp.PlayerListInfo playerListInfo = new GetClubMemberListResp.PlayerListInfo();
                ClubMemberExt clubMemberExt = iClub.getMemberExt(tempPlayer.getUid(),false);
                if (clubMemberExt != null) {
                    playerListInfo.gold = clubMemberExt.getGold();//竞技分
                    playerListInfo.reward = clubMemberExt.getRewardValue();//奖励分
                }
                playerListInfo.clubName=iClub.getName();
                playerListInfo.clubOwnerId=info.playerUid;
                playerListInfo.uid = info.searchUid;
                playerListInfo.name = tempPlayer.getName();
                playerListInfo.icon = tempPlayer.getIcon();
                resp.list.add(playerListInfo);
                resp.totalSize=1;
            }
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
