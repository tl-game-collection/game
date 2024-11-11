package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqSyncGps;
import com.xiuxiu.app.server.manager.GeoManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerSyncGpsHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqSyncGps info = (PCLIPlayerReqSyncGps) request;
        GeoManager.I.add(player, info.lat, info.lng);
        player.send(CommandId.CLI_NTF_PLAYER_SYNC_GPS_OK, null);
        return null;
    }
}