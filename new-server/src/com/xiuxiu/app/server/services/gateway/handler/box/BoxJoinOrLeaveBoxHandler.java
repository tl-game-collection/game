package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqJoinOrLeaveBoxInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class BoxJoinOrLeaveBoxHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqJoinOrLeaveBoxInfo info = (PCLIBoxReqJoinOrLeaveBoxInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 请求加入/离开包厢失败, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_JOIN_OR_LEAVE_BOX_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 请求加入/离开包厢失败", player, info.clubUid);
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_BOX_JOIN_OR_LEAVE_BOX_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_BOX_JOIN_OR_LEAVE_BOX_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        // 判断是否加入主圈
        if (club.checkIsJoinInMainClub()) {
            long finalClubId = club.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 请求加入/离开包厢失败", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_JOIN_OR_LEAVE_BOX_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        }
        
        try {
            Box leaveBox = club.getBox(info.leaveBoxUid);
            if (null != leaveBox) {
                leaveBox.delWatchPlayer(player);
            }
            Box joinBox = club.getBox(info.joinBoxUid);
            if (null != joinBox) {
                joinBox.addWatchPlayer(player);
            }
        } finally {
        }
        return null;
    }
}
