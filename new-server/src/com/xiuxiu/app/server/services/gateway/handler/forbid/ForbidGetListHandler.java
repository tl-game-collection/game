package com.xiuxiu.app.server.services.gateway.handler.forbid;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidNtfInfo;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidNtfList;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidReqList;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.forbid.Forbid;
import com.xiuxiu.app.server.forbid.ForbidManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ForbidGetListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIForbidReqList info = (PCLIForbidReqList) request;
        Map<Long, Forbid> idsMap = null;
        IClub club= ClubManager.I.getClubByUid(info.clubUid);
        if(club==null){
            Logs.CLUB.warn("%s 亲友圈:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        boolean checkPermission = (club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()));
        if (!checkPermission && club.checkIsJoinInMainClub()){
            IClub mainClub = club;
            if (!club.checkIsMainClub()){
                mainClub = ClubManager.I.getClubByUid(club.getFinalClubId());
                if (null != mainClub){
                    checkPermission = mainClub.matchMemberType(EClubJobType.CHIEF,player.getUid());
                }
            }
            if (!checkPermission && null != mainClub && mainClub.checkIsManager(player.getUid())){
                checkPermission = true;
            }
            if (!checkPermission&&club.checkIsMainClub()){
                List<Long> uidList=new ArrayList<>();
                club.fillDepthChildClubUidList(uidList);
                for (int i = 0; i < uidList.size(); i++) {
                    long uid = uidList.get(i);
                    IClub club1=ClubManager.I.getClubByUid(uid);
                    checkPermission = (club1.matchMemberType(EClubJobType.CHIEF, player.getUid())
                            || club1.matchMemberType(EClubJobType.DEPUTY, player.getUid()));
                    if(checkPermission){
                        break;
                    }
                }
            }
        }

        if (!checkPermission){
            Logs.CLUB.warn("%s 群:%d不是群主 或者副圈主, 权限不足, 无法获取防作弊列表", player, club.getClubUid());
            player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_ADD_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
            return null;
        }


        //TODO权限
        idsMap = ForbidManager.I.getForbidsByTypeAndUid(club.getClubType(),info.clubUid);

        //deal
        PCLIForbidNtfList result = new PCLIForbidNtfList();
        result.hasNext = false;
        result.page = info.page;
        if (null != idsMap) {
            for (Iterator<Map.Entry<Long, Forbid>> it = idsMap.entrySet().iterator(); it.hasNext(); ) {
                PCLIForbidNtfInfo forbidNtfInfo = new PCLIForbidNtfInfo();
                Map.Entry<Long, Forbid> tempEntry = it.next();
                forbidNtfInfo.uid = tempEntry.getKey();
                Long[] tempIds = tempEntry.getValue().getPlayerUidList();
                for (long tempPlayerUid : tempIds){
                    Player tempPlayer = PlayerManager.I.getPlayer(tempPlayerUid);
                    if (tempPlayer == null) {
                        continue;
                    }
                    forbidNtfInfo.players.add(tempPlayer.getPlayerSmallInfo());
                }
                result.list.add(forbidNtfInfo);
            }
        }

        player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_OK, result);
        return null;
    }
}