package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeSex;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerChangeSexHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeSex info = (PCLIPlayerReqChangeSex) request;
        if (0 != info.newSex && 1 != info.newSex) {
            Logs.PLAYER.warn("%s chang sex invalid sex :%d", player, info.newSex);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_SEX_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.changeSex((byte) info.newSex);
        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_SEX_OK, null);
        return null;
    }
}
