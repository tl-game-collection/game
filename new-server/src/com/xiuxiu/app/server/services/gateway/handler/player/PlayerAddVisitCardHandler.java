package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqSetVisitCardInfo;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerAddVisitCardHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqSetVisitCardInfo info = (PCLIPlayerReqSetVisitCardInfo) request;
        player.addVisitCard(info.desc, info.fileName, info.index);
        player.send(CommandId.CLI_NTF_PLAYER_VISIT_CARD_OK, null);
        return null;
    }
}