package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubListInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        player.sendClubListInfo();
        return null;
    }
}
