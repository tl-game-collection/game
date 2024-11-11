package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqClubLeaveClub;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubLeaveHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqClubLeaveClub info = (PCLIClubReqClubLeaveClub) request;
        IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == iClub) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_LEAVE_CLUB_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if(!iClub.hasMember(player.getUid())){
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
            ErrorCode ec = iClub.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_LEAVE_CLUB_FAIL, ec);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_LEAVE_CLUB_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        try {
            if (iClub.getOwnerId() == player.getUid()) {
                if (iClub.checkIsJoinInMainClub()) {
                    Logs.CLUB.warn("%s clubUid:%d 已经合并俱乐部不能解散俱乐部", player, info.clubUid);
                    player.send(CommandId.CLI_NTF_CLUB_LEAVE_CLUB_FAIL, ErrorCode.GM_INVALID_OPERATE);
                    return null;
                }
                iClub.dissolve();
                ClubManager.I.delClub(iClub.getClubUid());
                Logs.CLUB.error("群主玩家：%d club:%d 主动离开时解散", player.getUid(), info.clubUid);
            } else {
                iClub.delMember(player.getUid(),-1);
                Logs.CLUB.error("玩家：%d club:%d 主动离开", player.getUid(), info.clubUid);
                player.addOwnerClubCnt(false);
            }
            player.send(CommandId.CLI_NTF_CLUB_LEAVE_CLUB_OK, null);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}