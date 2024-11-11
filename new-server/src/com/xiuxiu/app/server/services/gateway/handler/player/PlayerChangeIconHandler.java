package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangeIcon;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

public class PlayerChangeIconHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerNtfChangeIcon ntfChangeIcon = new PCLIPlayerNtfChangeIcon();
        ntfChangeIcon.url = Config.FILE_UPLOAD_SERVER_URL;
        ntfChangeIcon.fileName = player.getUid() + "/icon/" + TimeUtil.format("yyyyMMddHHmmssSSS", System.currentTimeMillis());
        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_ICON_OK, ntfChangeIcon);
        return null;
    }
}
