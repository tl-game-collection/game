package com.xiuxiu.app.server.services.gateway.handler.forbid;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidNtfMemberList;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidReqMemberList;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.List;

public class ForbidGetMemberListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIForbidReqMemberList info = (PCLIForbidReqMemberList) request;
        //check something
        IClub club= ClubManager.I.getClubByUid(info.clubUid);
        if (null == club){
            Logs.CLUB.warn("%s 群:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_MEMBERLIST_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (club.checkIsMainClub()) {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.checkIsManager(player.getUid()))) {
                Logs.CLUB.warn("%s 群:%d不是群主 或管理员, 权限不足, 无法获取成员列表", player, club.getClubUid());
                player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_MEMBERLIST_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        } else {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                Logs.CLUB.warn("%s 群:%d不是群主 或者副圈主, 权限不足, 无法获取成员列表", player, club.getClubUid());
                player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_MEMBERLIST_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        }
        // deal
        List<Long> uidList=new ArrayList<>();
        club.fillDepthChildClubUidList(uidList);
        uidList.add(club.getClubUid());
        int beginIndex = (info.page - 1)* Constant.PAGE_CNT_100;
        int endIndex = beginIndex + Constant.PAGE_CNT_100;
        int curIndex = 0;
        PCLIForbidNtfMemberList result = new PCLIForbidNtfMemberList();
        result.hasNext = false;
        result.page = info.page;
        for (int i = 0; i < uidList.size(); i++) {
            long uid = uidList.get(i);
            IClub club1=ClubManager.I.getClubByUid(uid);
            if (null == club1) {
                continue;
            }

            if (curIndex > endIndex){
                result.hasNext = true;
                break;
            }

            List<Long> tempPlayerUids = club1.getAllMemberUids();
            if (beginIndex > (curIndex + tempPlayerUids.size())){
                curIndex += tempPlayerUids.size();
                continue;
            }

            for (int n = beginIndex > curIndex ? beginIndex - curIndex : 0 ; n < tempPlayerUids.size(); n++){
                Player tempPlayer = PlayerManager.I.getPlayer(tempPlayerUids.get(n));
                if (null == tempPlayer){
                    continue;
                }
                result.list.add(tempPlayer.getPlayerSmallInfo());
                curIndex++;
                if (curIndex > endIndex){
                    break;
                }
            }
        }
        player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_MEMBERLIST_OK, result);
        return null;
    }
}