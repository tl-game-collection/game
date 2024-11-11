package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.GetClubAllMemberList;
import com.xiuxiu.app.protocol.api.temp.club.GetClubAllMemberListResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取亲友圈所有玩家列表
 * @date 2020/1/10 17:13
 * @author luocheng
 */
public class GetClubAllMemberListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到获取亲友圈所有玩家列表请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubAllMemberList info = JsonUtil.fromJson(body, GetClubAllMemberList.class);
        String sign = MD5Util.getMD5(info.clubUid, info.playerUid, info.upLinePlayerUid, info.page, info.pageSize, Config.APP_KEY);
        GetClubAllMemberListResp resp = new GetClubAllMemberListResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
            if (iClub == null) {
                resp.ret = ErrorCode.CLUB_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.CLUB_NOT_EXISTS.getMsg();
                break;
            }
            //筛选玩家id
            if (info.playerUid != 0) {
                GetClubAllMemberListResp.MemberInfo memberInfo = this.setMemberInfo(info.playerUid, iClub);
                if (memberInfo != null) {
                    resp.list.add(memberInfo);
                }
            } else {
                //筛选上级id
                if (info.upLinePlayerUid != 0) {
                    List<Long> allMemberUidsByParm = new ArrayList<>();
                    for (Long memberUid : iClub.getAllMemberUids()) {
                        ClubMember clubMember = iClub.getMember(memberUid);
                        if (clubMember == null) {
                            continue;
                        }
                        if (clubMember.getUplinePlayerUid() == info.upLinePlayerUid) {
                            allMemberUidsByParm.add(memberUid);
                        }
                    }
                    this.checkMemberList(allMemberUidsByParm, info.page, info.pageSize, resp, iClub);
                } else {
                    List<Long> allMemberUids = iClub.getAllMemberUids();
                    this.checkMemberList(allMemberUids, info.page, info.pageSize, resp, iClub);
                }
            }
            resp.page = info.page;
            resp.pageSize = info.pageSize;
            resp.next = resp.list.size() == info.pageSize;
            resp.clubUid = iClub.getClubUid();
            resp.clubName = iClub.getName();
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }

    private GetClubAllMemberListResp.MemberInfo setMemberInfo(long playerUid, IClub iClub) {
        ClubMember clubMember = iClub.getMember(playerUid);
        if (clubMember != null) {
            GetClubAllMemberListResp.MemberInfo memberInfo = new GetClubAllMemberListResp.MemberInfo();
            Player player = PlayerManager.I.getPlayer(playerUid);
            if (player != null) {
                memberInfo.playerUid = clubMember.getPlayerUid();
                memberInfo.playerName = player.getName();
                memberInfo.upLinePlayerUid = clubMember.getUplinePlayerUid();
                Player upLinePlayer = PlayerManager.I.getPlayer(clubMember.getUplinePlayerUid());
                if (upLinePlayer != null) {
                    memberInfo.upLinePlayerName = upLinePlayer.getName();
                }
                memberInfo.gold = iClub.getGold(playerUid);
                ClubMemberExt memberExt = iClub.getMemberExt(playerUid,true);
                memberInfo.reward = memberExt.getRewardValue();
                memberInfo.upGoldTotal = memberExt.getUpTotalScore();
                memberInfo.downGoldTotal = memberExt.getDownTotalScore();
                memberInfo.joinClubAt = clubMember.getJoinTime();

                return memberInfo;
            }
        }
        Logs.API.warn("%d玩家数据异常", playerUid);
        return null;
    }

    private void checkMemberList(List<Long> members, int page, int pageSize, GetClubAllMemberListResp resp, IClub iClub) {
        for (int i = page * pageSize; i < (page + 1) * pageSize; i++) {
            if (i >= members.size()) {
                break;
            }
            GetClubAllMemberListResp.MemberInfo memberInfo = this.setMemberInfo(members.get(i), iClub);
            if (memberInfo != null) {
                resp.list.add(memberInfo);
            }
        }
    }
}
