package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReadyFailInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomReadyHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        Room room = RoomManager.I.getRoom(player.getRoomId());

        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_ROOM_READY_FAIL,  getFailMsg(ErrorCode.ROOM_NOT_EXISTS));
            return null;
        }
        IRoomHandle roomHandle = room.getRoomHandle();
        if (roomHandle instanceof IBoxRoomHandle) {
            if (GameType.isArenaGame(room.getGameType())) {
                IClub club = ClubManager.I.getClubByUid(room.getGroupUid());
                long rootClubUid = club.getEnterFromClubUid(player.getUid());
                if (rootClubUid != room.getGroupUid()) {
                    IClub tempClub = ClubManager.I.getClubByUid(rootClubUid);
                    if (tempClub != null) {
                        club = tempClub;
                    }
                }
                if (club.getGold(player.getUid()) <= 0 || club.getGold(player.getUid()) < roomHandle.getRoom().getRule().getOrDefault(RoomRule.RR_MINGOLD, 0)) {
                    player.send(CommandId.CLI_NTF_ROOM_READY_FAIL, ErrorCode.ARENA_LESS_THAN_MIN_VALUE);
                    if (GameType.GAME_TYPE_PAIGOW == room.getGameType() || GameType.GAME_TYPE_COW == room.getGameType()) {
                        IClub mainClub = (IClub)room.getBoxOwner();
                        IClub fromClub = ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(player.getUid()));
                        BoxManager.I.sitUp(player, player.getRoomId(), fromClub);
                    }
                    return null;
                }
            }
            ErrorCode errorCode = BoxManager.I.ready(player, (IBoxRoomHandle) roomHandle);
            if (errorCode != null && ErrorCode.OK != errorCode) {
                if (errorCode == ErrorCode.ARENA_LESS_THAN_MIN_VALUE) {
                    IClub mainClub = (IClub)room.getBoxOwner();
                    IClub fromClub = ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(player.getUid()));
                    BoxManager.I.sitUp(player, player.getRoomId(), fromClub);
                } else {
                    player.send(CommandId.CLI_NTF_ROOM_READY_FAIL,  getFailMsg(errorCode));
                }
            }
        } else {
            ErrorCode errorCode = room.ready(player);
            if (errorCode != null && ErrorCode.OK != errorCode) {
                player.send(CommandId.CLI_NTF_ROOM_READY_FAIL, getFailMsg(errorCode));
            }
        }
        return null;
    }

    private PCLIRoomReadyFailInfo getFailMsg(ErrorCode errorCode){
        PCLIRoomReadyFailInfo readyFailInfo = new PCLIRoomReadyFailInfo();
        readyFailInfo.error = errorCode.getMsg();
        return readyFailInfo;
    }
}
