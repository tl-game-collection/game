package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqRoomDismiss;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomDestroyType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubRoomDismissHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqRoomDismiss info = (PCLIClubReqRoomDismiss) request;
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (fromClub == null) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_DISMISS_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        IClub club = null;
        // 判断是否加入主圈
        if (fromClub.checkIsJoinInMainClub()) {
            long finalClubId = fromClub.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法关闭包厢", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
            if(!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid())
                    || club.checkIsManager(player.getUid()))){
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.FLOOR_NOT_PRIVILEGE);
                return null;
            }

        } else {
            club = fromClub;
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                player.send(CommandId.CLI_NTF_BOX_CLOSE_FAIL, ErrorCode.FLOOR_NOT_PRIVILEGE);
                return null;
            }
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("{} info:{} 正在操作", player, info);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_DISMISS_FAIL, ErrorCode.CLUB_IN_LOCK);
            return null;
        }
        try {
            Room room = RoomManager.I.getRoom(info.roomUid);
            if (null == room) {
                Logs.ROOM.warn("{} 没有这个房间", info.roomUid);
                player.send(CommandId.CLI_NTF_CLUB_ROOM_DISMISS_FAIL, ErrorCode.ROOM_NOT_EXISTS);
                return null;
            }
            room.setDestroyType(ERoomDestroyType.MANAGER_DESTROY);
            room.setIsDestroyUid(player.getUid());
            room.destroy();
            player.send(CommandId.CLI_NTF_CLUB_ROOM_DISMISS_OK, null);
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}
