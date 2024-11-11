package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqClubJoinClub;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ApplyInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EApplyType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.club.constant.EOpStateType;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubApplyJoinHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqClubJoinClub info = (PCLIClubReqClubJoinClub) request;
        IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == iClub) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_JOIN_CLUB_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (iClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 已经在俱乐部中", player, info.clubUid);
            if(iClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_JOIN_CLUB_FAIL, ErrorCode.CLUB_GOLD_ALREADY_IN);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_JOIN_CLUB_FAIL, ErrorCode.CLUB_CARD_ALREADY_IN);
            }
            return null;
        }
        Player ownerPlayer = PlayerManager.I.getPlayer(iClub.getOwnerId());
        if (iClub.getMemberCnt() > EPlayerPrivilegeLevel.getValue(ownerPlayer.getPrivilege(), EPlayerPrivilege.GROUP_MEMBER_NUM)) {
            Logs.CLUB.warn("%s iClub :%d 该俱乐部人员已满 ", iClub, info.clubUid);
            ErrorCode code = iClub.getClubType() == EClubType.GOLD ? ErrorCode.ROOM_GOLD_MEMBER_FULL : ErrorCode.CLUB_CARD_MEMBER_FULL;
            player.send(CommandId.CLI_NTF_CLUB_JOIN_CLUB_FAIL, code);
            return null;
        }
        for (ApplyInfo applyInfo : iClub.getClubInfo().getApplyInfo()) {
            if (applyInfo.getfUid() == player.getUid() && applyInfo.getState() == EOpStateType.NORMAL.ordinal()) {
                Logs.CLUB.warn("%s clubUid:%d 已经在申请列表中", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_JOIN_CLUB_FAIL, ErrorCode.ACCOUNT_GROUP_ISAPPLY);
                return null;
            }
        }

        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_JOIN_CLUB_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        try {
            iClub.applyJoin(player);
            player.send(CommandId.CLI_NTF_CLUB_JOIN_CLUB_OK, null);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
