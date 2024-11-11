package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfGetManager;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 获取club管理员 (主圈才能获取)
 */
public class ClubGetMangerHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetManager info = (PCLIClubReqGetManager) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_MANAGER_INFO_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        //没有合过圈
        if (!club.checkIsJoinInMainClub()) {
            Logs.CLUB.warn("%s club:%d没有合过圈", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_MANAGER_INFO_FAIL, ErrorCode.CLUB_NOT_HAVE_MERGE);

            club.getClubInfo().getManagerInfo().clear();
            return null;
        }
        //不是总圈
        if (!club.checkIsMainClub()) {
            Logs.CLUB.warn("%s club:%d不是总圈", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_MANAGER_INFO_FAIL, ErrorCode.GM_INVALID_OPERATE);

            club.getClubInfo().getManagerInfo().clear();
            return null;
        }

        Map<Long,List<Long>> managerMap = club.getClubInfo().getManagerInfo();

        //自检一遍，看看人有没有变动，子圈有没有变动
        HashSet<Long> allPlayerUid = new HashSet<>();//存所有玩家uid
        allPlayerUid.addAll(club.getAllMemberUids());//添加主圈玩家
        List<Long> allClubUidList = new ArrayList<>();
        club.fillDepthChildClubUidList(allClubUidList);
        allClubUidList.add(0,club.getClubUid());
        //人员变动
        for (Long m_cludUid : allClubUidList) {
            IClub m_club = ClubManager.I.getClubByUid(m_cludUid);
            if (m_club == null) {
                continue;
            }
            allPlayerUid.addAll(m_club.getAllMemberUids());
        }
        for (Map.Entry<Long,List<Long>> entry : managerMap.entrySet()) {
            if (!allPlayerUid.contains(entry.getKey())) {
                managerMap.remove(entry.getKey());
            }
        }
        //子圈变动
        for (Map.Entry<Long,List<Long>> entry : managerMap.entrySet()) {
            for (Long tempClubUid : entry.getValue()) {
                if (!allClubUidList.contains(tempClubUid)) {
                    entry.getValue().remove(tempClubUid);
                }
            }
        }
        club.getClubInfo().setDirty(true);

        PCLIClubNtfGetManager respInfo = new PCLIClubNtfGetManager();
        respInfo.clubUid = info.clubUid;
        for (Map.Entry<Long,List<Long>> entry : managerMap.entrySet()) {
            PCLIClubNtfGetManager.mangerInfo mangerInfo = new PCLIClubNtfGetManager.mangerInfo();
            mangerInfo.playerUid = entry.getKey();
            mangerInfo.managerClubList = entry.getValue();

            respInfo.list.add(mangerInfo);
        }
        player.send(CommandId.CLI_NTF_CLUB_GET_MANAGER_INFO_OK, respInfo);
        return null;
    }
}
