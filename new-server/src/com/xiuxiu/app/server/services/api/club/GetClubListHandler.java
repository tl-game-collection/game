package com.xiuxiu.app.server.services.api.club;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.ClubGetListResp;
import com.xiuxiu.app.protocol.api.temp.club.GetClubList;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

/**
 * 俱乐部列表
 * @author MyPC
 *
 */
public class GetClubListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubList info = JsonUtil.fromJson(body, GetClubList.class);
        Logs.API.debug("查询俱乐部列表:%s", info);
        ClubGetListResp resp = new ClubGetListResp();
        String sign = MD5Util.getMD5(info.page, info.pageSize, info.clubUid, Config.APP_KEY);
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (info.clubUid != 0) {
                IClub club = ClubManager.I.getClubByUid(info.clubUid);
                if (club == null) {
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                ClubGetListResp.ClubInfo temp = new ClubGetListResp.ClubInfo();
                temp.uid = info.clubUid;
                temp.name = club.getName();
                temp.type = club.getClubType().getType();
                temp.desc = club.getDesc();
                temp.gameDesc = club.getGameDesc();
                temp.ownerUid = club.getOwnerId();
                temp.ownerName = PlayerManager.I.getPlayer(club.getOwnerId()).getName();
                temp.clubSize = club.getMemberCnt();
                temp.canSetTreasureInfo = club.checkIsJoinInMainClub() ? club.checkIsMainClub() : true;
                temp.roomCards = DBManager.I.getMoneyExpendRecordDetailDao().getTotalConsumeByClubUid(info.clubUid);
                temp.ownerRoomCards=PlayerManager.I.getPlayer(club.getOwnerId()).getMoneyByType(EMoneyType.DIAMOND);
                for (Long memberUid : club.getAllMemberUids()) {
                    ClubMemberExt clubMemberExt = club.getMemberExt(memberUid,false);
                    if (clubMemberExt == null) {
                        continue;
                    }
                    temp.gold += clubMemberExt.getGold();
                    temp.reward += clubMemberExt.getRewardValue();
                }
                if (club.checkIsJoinInMainClub()) {
                    IClub rootClub;
                    if (!club.checkIsMainClub()) {
                        rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
                    } else {
                        rootClub = club;
                    }
                    List<Long> allClubList = new ArrayList<>();
                    rootClub.fillDepthChildClubUidList(allClubList);
                    allClubList.add(0,rootClub.getClubUid());
                    temp.fuseCount = allClubList.size();
                    for (Long tempClubUid : allClubList) {
                        float m_count = DBManager.I.getMoneyExpendRecordDetailDao().getTotalConsumeByClubUid(tempClubUid);
                        temp.fuseClubCards += m_count;
                    }
                } else {
                    temp.fuseCount = 1;
                }
                int peopleCount = club.getMemberCnt();
                if (club.getClubInfo().getParentUid() > 0) {
                    IClub tempClub = ClubManager.I.getClubByUid(club.getClubInfo().getParentUid());
                    peopleCount += tempClub.getMemberCnt();
                }
                for (Long tempClubUid : club.getClubInfo().getChildUid()) {
                    IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
                    peopleCount += tempClub.getMemberCnt();
                }
                temp.fuseClubSize = peopleCount;
                temp.createTime = club.getCreateTime();

                resp.list.add(temp);
                resp.page = info.page;
                resp.pageSize = info.pageSize;
                resp.totalSize = 1;
            } else{
                List<Long> list = DBManager.I.getClubInfoDAO().loadByPage(info.pageSize,info.page*info.pageSize);
                for (Long clubUid : list) {
                    IClub club = ClubManager.I.getClubByUid(clubUid);
                    if (club == null) {
                        continue;
                    }
                    ClubGetListResp.ClubInfo temp = new ClubGetListResp.ClubInfo();
                    temp.uid = clubUid;
                    temp.name = club.getName();
                    temp.type = club.getClubType().getType();
                    temp.desc = club.getDesc();
                    temp.gameDesc = club.getGameDesc();
                    temp.ownerUid = club.getOwnerId();
                    temp.ownerName = PlayerManager.I.getPlayer(club.getOwnerId()).getName();
                    temp.clubSize = club.getMemberCnt();
                    temp.canSetTreasureInfo = club.checkIsJoinInMainClub() ? club.checkIsMainClub() : true;
                    temp.roomCards = DBManager.I.getMoneyExpendRecordDetailDao().getTotalConsumeByClubUid(clubUid);
                    temp.ownerRoomCards=PlayerManager.I.getPlayer(club.getOwnerId()).getMoneyByType(EMoneyType.DIAMOND);
                    for (Long memberUid : club.getAllMemberUids()) {
                        ClubMemberExt clubMemberExt = club.getMemberExt(memberUid,false);
                        if (clubMemberExt == null) {
                            continue;
                        }
                        temp.gold += clubMemberExt.getGold();
                        temp.reward += clubMemberExt.getRewardValue();
                    }
                    if (club.checkIsJoinInMainClub()) {
                        IClub rootClub = club;
                        if (!club.checkIsMainClub()) {
                            rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
                        }
                        List<Long> allClubList = new ArrayList<>();
                        rootClub.fillDepthChildClubUidList(allClubList);
                        allClubList.add(0,rootClub.getClubUid());
                        temp.fuseCount = allClubList.size();
                        for (Long tempClubUid : allClubList) {
                            float m_count = DBManager.I.getMoneyExpendRecordDetailDao().getTotalConsumeByClubUid(tempClubUid);
                            temp.fuseClubCards += m_count;
                        }
                    } else {
                        temp.fuseCount = 1;
                    }
                    int peopleCount = club.getMemberCnt();
                    if (club.getClubInfo().getParentUid() > 0) {
                        IClub tempClub = ClubManager.I.getClubByUid(club.getClubInfo().getParentUid());
                        peopleCount += tempClub.getMemberCnt();
                    }
                    for (Long tempClubUid : club.getClubInfo().getChildUid()) {
                        IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
                        peopleCount += tempClub.getMemberCnt();
                    }
                    temp.fuseClubSize = peopleCount;
                    temp.createTime = club.getCreateTime();

                    resp.list.add(temp);
                }
                resp.page = info.page;
                resp.pageSize = info.pageSize;
                resp.totalSize = ClubManager.I.getAllClubIds().size();
            }
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
