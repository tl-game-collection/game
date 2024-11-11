package com.xiuxiu.app.server.services.gateway.handler;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.system.PCLISystemNtfGameInfo;
import com.xiuxiu.app.protocol.client.system.PCLISystemReqGameInfo;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.core.net.message.Handler;

public class SystemGameInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLISystemReqGameInfo req = (PCLISystemReqGameInfo) request;
        if (req.containerType == 1) {
            IRoom room = RoomManager.I.getRoom(req.containerUid);
            if (room == null) {
                player.send(CommandId.CLI_NTF_SYSTEM_GAME_INFO_FAIL, ErrorCode.ROOM_NOT_EXISTS);
                return null;
            }
            PCLISystemNtfGameInfo ntf = new PCLISystemNtfGameInfo();
            ntf.containerType = req.containerType;
            ntf.containerUid = req.containerUid;
            ntf.gameType = room.getGameType();
            ntf.gameSubType = room.getGameSubType();
            player.send(CommandId.CLI_NTF_SYSTEM_GAME_INFO_OK, ntf);
        }else if (req.containerType == 3) {
            Box box = BoxManager.I.getBox(req.groupUid, req.containerUid);
            if (box == null) {
                player.send(CommandId.CLI_NTF_SYSTEM_GAME_INFO_FAIL, ErrorCode.GROUP_BOX_NOT_EXISTS);
                return null;
            }
            PCLISystemNtfGameInfo ntf = new PCLISystemNtfGameInfo();
            ntf.containerType = req.containerType;
            ntf.containerUid = req.containerUid;
            ntf.groupUid = req.groupUid;
            ntf.gameType = box.getGameType();
            ntf.gameSubType = box.getGameSubType();
            player.send(CommandId.CLI_NTF_SYSTEM_GAME_INFO_OK, ntf);
        } else {
            player.send(CommandId.CLI_NTF_SYSTEM_GAME_INFO_FAIL, ErrorCode.REQUEST_INVALID);
        }
        return null;
    }
}
