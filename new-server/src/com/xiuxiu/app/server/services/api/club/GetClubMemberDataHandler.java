package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.ClubMemberDataResp;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberDataReq;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetClubMemberDataHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubMemberDataReq info = JsonUtil.fromJson(body, GetClubMemberDataReq.class);

        Logs.API.warn("查询群成员:%s", info);

        //check something
        ClubMemberDataResp resp = new ClubMemberDataResp();
        String sign = MD5Util.getMD5(info.clubUid, info.playerUid, Config.APP_KEY);
        if (null == sign || !sign.equalsIgnoreCase(info.sign)) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange, resp);
            return;
        }

        if (0 >= info.clubUid || 0 >= info.playerUid || 0 > info.page || 0 >= info.pageSize) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange, resp);
            return;
        }

        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null || club.getClubType() != EClubType.GOLD) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange, resp);
            return;
        }

        if (!club.hasMember(info.playerUid)) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange, resp);
            return;
        }

        ClubMember clubMember = club.getMember(info.playerUid);
        if (null == clubMember) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange, resp);
            return;
        }

        Player player = PlayerManager.I.getPlayer(info.playerUid);
        if (null == player) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange, resp);
            return;
        }

        //deal
        if (info.searchUid > 0){
            ClubMember searchMember = club.getMember(info.searchUid);
            if (null == searchMember){
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                sendFailToClient(httpExchange, resp);
                return;
            }

            long upMemberUid = searchMember.getUplinePlayerUid();
            for (int i = 0; i < club.getMemberCnt(); i++){
                if (upMemberUid == info.playerUid){
                    break;
                }
                ClubMember tempMember = club.getMember(upMemberUid);
                if (null == tempMember){
                    break;
                }
                upMemberUid = tempMember.getUplinePlayerUid();
            }
            if (upMemberUid != info.playerUid){
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                sendFailToClient(httpExchange, resp);
                return;
            }
            fillMemberInfo(resp,searchMember,club);
            sendOkToClinet(httpExchange, resp);
            return;
        }

        if (club.getOwnerId() == info.playerUid){
            club.foreach((member) -> {
                ClubMember tempMember = member[0];
                fillMemberInfo(resp,tempMember,club);
            });
            sendOkToClinet(httpExchange, resp);
            return;
        }

        club.foreach((member) -> {
            ClubMember tempMember = member[0];
            if (tempMember.getUplinePlayerUid() == info.playerUid) {
                fillMemberInfo(resp,tempMember,club);
            }
        });

        sendOkToClinet(httpExchange, resp);
        return;
    }

    private void sendFailToClient(HttpExchange httpExchange, ClubMemberDataResp resp) throws IOException {
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }

    private void sendOkToClinet(HttpExchange httpExchange, ClubMemberDataResp resp) throws IOException {
        resp.ret = ErrorCode.OK.getRet();
        resp.msg = ErrorCode.OK.getMsg();
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }

    private void fillMemberInfo(ClubMemberDataResp resp,ClubMember member,IClub club){
        ClubMemberDataResp.MemberData data = new ClubMemberDataResp.MemberData();
        data.uid = member.getPlayerUid();
        Player player = PlayerManager.I.getPlayer(data.uid);
        data.name = player != null ? player.getName() : "";
        data.upUid = member.getUplinePlayerUid() <= 0 ? 0 : member.getUplinePlayerUid();
        if (data.upUid > 0){
            Player upPlayer = PlayerManager.I.getPlayer(data.upUid);
            data.upName = upPlayer != null ? upPlayer.getName() : "";
        }
        data.divide = member.getDivide() + ":" + member.getDivideLine();

        ClubMemberExt memberExt = club.getMemberExt(data.uid,true);
        data.curGold = memberExt.getGold();
        data.upTotalScore = memberExt.getUpTotalScore();
        data.downTotalScore = memberExt.getDownTotalScore();
        data.curRewardValue = memberExt.getRewardValue();

        data.costByGame = 0;
        data.totalReward = 0;
        data.totalExchange = 0;
        data.totalGameCnt = 0;
        data.totalGameValue = 0;
        resp.data.add(data);
    }
}