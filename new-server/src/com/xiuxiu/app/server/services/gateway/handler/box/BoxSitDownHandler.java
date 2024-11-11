package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqSitDown;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 亲友圈可少人模式-坐下
 * 
 * @author Administrator
 *
 */
public class BoxSitDownHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqSitDown info = (PCLIBoxReqSitDown) request;
        if (-1 == player.getRoomId()) {
            Logs.CLUB.warn("%s 在房间中, 无法坐下", player);
            player.send(CommandId.CLI_NTF_BOX_SIT_DOWN_FAIL, ErrorCode.PLAYER_ROOM_IN);
            return null;
        }
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == fromClub) {
            Logs.CLUB.warn("%s 无法坐下, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_SIT_DOWN_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        boolean flag=fromClub.getClubType()== EClubType.GOLD;
        if (!fromClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 无法坐下", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_SIT_DOWN_FAIL, ErrorCode.GROUP_NOT_IN);
            return null;
        }
        IClub club = null;
        // 判断是否加入主圈
        if (fromClub.checkIsJoinInMainClub()) {
            long finalClubId = fromClub.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法坐下", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_SIT_DOWN_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        } else {
            club = fromClub;
        }

        if (fromClub.isForbidPlay(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 玩家被禁玩了", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_SIT_DOWN_FAIL, ErrorCode.ACCOUNT_GROUP_PLAYER_FORBIDO_LAY);
            return null;
        }
        if (!BoxManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CHANGE_NAME_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = BoxManager.I.sitDown(player, info.roomId, club, fromClub, info.index);
            if (ErrorCode.OK == err) {
            } else if (ErrorCode.PLAYER_BOX_ROOM_IN != err) {
                player.send(CommandId.CLI_NTF_BOX_SIT_DOWN_FAIL, err);
            }
        } finally {
            BoxManager.I.unlock(player.getUid());
        }
        return null;
    }

}
