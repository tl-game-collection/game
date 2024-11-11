package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfAddShowImageInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerAddShowImageHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerNtfAddShowImageInfo info = (PCLIPlayerNtfAddShowImageInfo) request;
        if (StringUtil.isEmptyOrNull(info.showImageUrl) || info.showImageUrl.length() >= (Constant.LEN_IMAGE - Config.FILE_DOWNLOAD_SERVER_URL.length())) {
            Logs.PLAYER.warn("%s add show image error image:%s", player, info.showImageUrl);
            player.send(CommandId.CLI_NTF_PLAYER_ADD_SHOW_IMAGE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        if (player.getShowImage().size() >= Constant.SHOW_IMAGE_CNT) {
            Logs.PLAYER.warn("%s add show image error reason was max", player);
            player.send(CommandId.CLI_NTF_PLAYER_ADD_SHOW_IMAGE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        player.addShowImage(info.showImageUrl);
        player.send(CommandId.CLI_NTF_PLAYER_ADD_SHOW_IMAGE_OK, new PCLIPlayerNtfAddShowImageInfo(Config.FILE_DOWNLOAD_SERVER_URL + info.showImageUrl));
        return null;
    }
}