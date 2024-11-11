package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfMemberCount;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqMemberCount;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubGetMemberCountHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqMemberCount info = (PCLIClubReqMemberCount) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_MEMBER_COUNT_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if(!club.hasMember(player.getUid())){
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_GET_MEMBER_COUNT_FAIL, ec);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s info:%s 正在操作", player, info);
            player.send(CommandId.CLI_NTF_CLUB_GET_MEMBER_COUNT_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            PCLIClubNtfMemberCount count = new PCLIClubNtfMemberCount();
            count.clubUid = info.clubUid;
            count.memberCnt = club.getMemberCnt();
            player.send(CommandId.CLI_NTF_CLUB_GET_MEMBER_COUNT_OK, count);
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}
