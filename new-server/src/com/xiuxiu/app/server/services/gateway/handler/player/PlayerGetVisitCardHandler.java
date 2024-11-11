package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqVisitCardInfo;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

public class PlayerGetVisitCardHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqVisitCardInfo reqInfo = (PCLIPlayerReqVisitCardInfo) request;
        Player tagPlayer = PlayerManager.I.getPlayer(reqInfo.playerUid);
        if (null == tagPlayer) {
            player.send(CommandId.CLI_NTF_PLAYER_VISIT_CARD_GET_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.send(CommandId.CLI_NTF_PLAYER_VISIT_CARD_GET_OK, tagPlayer.getVisitCardTo());
        return null;
    }
}
