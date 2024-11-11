package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangeBornInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeBorn;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerChangeBornHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeBorn info = (PCLIPlayerReqChangeBorn) request;
        if (info.born < 0) {
            Logs.PLAYER.warn("%s change born error born:%d", player, info.born);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_BORN_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.changeBorn(info.born);
        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_BORN_OK, new PCLIPlayerNtfChangeBornInfo(info.born));
        return null;
    }
}
