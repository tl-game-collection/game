package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReqCreateInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.core.net.message.Handler;

public class RoomCreateHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIRoomReqCreateInfo info = (PCLIRoomReqCreateInfo) request;
        if (!RoomManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.groupUid);
            player.send(CommandId.CLI_NTF_ROOM_CREATE_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            RoomManager.I.create(player, info.groupUid, info.gameType, info.gameSubType, info.rule);
        } finally {
            RoomManager.I.unlock(player.getUid());
        }
        return null;
    }
}
