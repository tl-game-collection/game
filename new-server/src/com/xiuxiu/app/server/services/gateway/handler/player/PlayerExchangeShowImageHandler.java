package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfExchangeShowImageInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqExchangeShowImage;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerExchangeShowImageHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqExchangeShowImage info = (PCLIPlayerReqExchangeShowImage) request;

        if (info.fromShowImageIndex < 0 || info.fromShowImageIndex >= player.getShowImage().size()) {
            Logs.PLAYER.warn("%s exchange show image error index error from index:%d range:[%d, %d)", player, info.fromShowImageIndex, 0, player.getShowImage().size());
            player.send(CommandId.CLI_NTF_PLAYER_EXCHANGE_SHOW_IMAGE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }

        if (info.toShowImageIndex < 0 || info.toShowImageIndex >= player.getShowImage().size()) {
            Logs.PLAYER.warn("%s exchange show image error index error to index:%d range:[%d, %d)", player, info.toShowImageIndex, 0, player.getShowImage().size());
            player.send(CommandId.CLI_NTF_PLAYER_EXCHANGE_SHOW_IMAGE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        if (info.toShowImageIndex == info.fromShowImageIndex) {
            player.send(CommandId.CLI_NTF_PLAYER_EXCHANGE_SHOW_IMAGE_OK, new PCLIPlayerNtfExchangeShowImageInfo(info.fromShowImageIndex, info.toShowImageIndex));
        } else {
            player.exchangeShowImage(info.fromShowImageIndex, info.toShowImageIndex);
            player.send(CommandId.CLI_NTF_PLAYER_EXCHANGE_SHOW_IMAGE_OK, new PCLIPlayerNtfExchangeShowImageInfo(info.fromShowImageIndex, info.toShowImageIndex));
        }
        return null;
    }
}