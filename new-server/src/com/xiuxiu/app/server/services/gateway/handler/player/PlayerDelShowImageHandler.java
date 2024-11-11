package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfDelShowImageInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqDelShowImage;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerDelShowImageHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqDelShowImage info = (PCLIPlayerReqDelShowImage) request;
        if (info.showImageIndex < 0 || info.showImageIndex >= player.getShowImage().size()) {
            Logs.PLAYER.warn("%s del show image error index error index:%d range:[%d, %d)", player, info.showImageIndex, 0, player.getShowImage().size());
            player.send(CommandId.CLI_NTF_PLAYER_DEL_SHOW_IMAGE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        player.delShowImage(info.showImageIndex);
        player.send(CommandId.CLI_NTF_PLAYER_DEL_SHOW_IMAGE_OK, new PCLIPlayerNtfDelShowImageInfo(info.showImageIndex));
        return null;
    }
}