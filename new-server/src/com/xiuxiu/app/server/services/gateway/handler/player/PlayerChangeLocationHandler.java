package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeLocation;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerChangeLocationHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeLocation info = (PCLIPlayerReqChangeLocation) request;
        if (StringUtil.isEmptyOrNull(info.newLocation) || info.newLocation.length() >= Constant.LEN_ZONE) {
            Logs.PLAYER.warn("%s change zone error zone:%s", player, info.newLocation);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_LOCATION_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.changeZone(info.newLocation);
        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_LOCATION_OK, null);
        return null;
    }
}
