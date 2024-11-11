package com.xiuxiu.app.server.services.gateway.handler.club.helper;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubNtfHelperInfo;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

/**
 * 请求亲友圈小助手-获取小桌面信息
 * 
 * @author Administrator
 *
 */
public class HallHelperInfoHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        int roomId = player.getRoomId();
        if (-1 == roomId) {
            return null;
        }
        Room room = RoomManager.I.getRoom(roomId);

        if (null == room) {
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INFO_FAIL, -1);
            return null;
        }
        IRoomHandle roomHandle = room.getRoomHandle();
        if (!(roomHandle instanceof IBoxRoomHandle)) {
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INFO_FAIL, -1);
            return null;
        }
//        IBoxRoom boxRoom = (IBoxRoom) room;
//        Box box = BoxManager.I.getBox(boxRoom.getBoxUid());
//        if (null == box) {
//            return null;
//        }

//        if (!ClubManager.I.lock(player.getUid())) {
//            Logs.GROUP.warn("%s 正在操作", player);
//            player.send(CommandId.CLI_NTF_CLUB_HELPER_INFO_FAIL, ErrorCode.REPEAT_OPERATE);
//            return null;
//        }
//        try {
        PCLIClubNtfHelperInfo message = new PCLIClubNtfHelperInfo();
        message.roomId = roomId;
        message.gameType = room.getGameType();
        message.i = room.getCurBureau();
        message.j = room.getBureau();
        message.players = room.getHelperInfo();
        message.state = room.getCurBureau() > 0 || room.getFinishBureauCount() > 0 ? 1 : 0 ;
        message.count = room.getRule().getOrDefault(RoomRule.RR_PLAYER_NUM, 3);
        message.minCount = room.getRule().getOrDefault(RoomRule.RR_PLAYER_MIN_NUM, -1);
        player.send(CommandId.CLI_NTF_CLUB_HELPER_INFO_OK, message);
        return null;
//        } finally {
//            ClubManager.I.unlock(player.getUid());
//        }
    }
}
