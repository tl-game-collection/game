package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqClubOpApplyList;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubOpApplyListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqClubOpApplyList info = (PCLIClubReqClubOpApplyList) request;
        IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == iClub) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_OP_APPLY_LIST_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if(!iClub.hasMember(player.getUid())){
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
            ErrorCode ec = iClub.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_OP_APPLY_LIST_FAIL, ec);
            return null;
        }
        if (!iClub.matchMemberType(EClubJobType.CHIEF,player.getUid()) && !iClub.matchMemberType(EClubJobType.DEPUTY,player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 没有权限", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_OP_APPLY_LIST_FAIL, ErrorCode.FLOOR_NOT_PRIVILEGE);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_OP_APPLY_LIST_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        try {
            boolean isOp = iClub.opApplyList(player, info.playerUid, info.op);
            if (isOp) {
                player.send(CommandId.CLI_NTF_CLUB_OP_APPLY_LIST_OK, null);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_OP_APPLY_LIST_FAIL, ErrorCode.FLOOR_NOT_PRIVILEGE);
            }
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
