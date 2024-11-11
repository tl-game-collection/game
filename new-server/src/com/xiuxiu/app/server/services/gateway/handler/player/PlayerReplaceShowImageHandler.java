package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfReplaceShowImageInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqReplaceShowImage;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerReplaceShowImageHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqReplaceShowImage info = (PCLIPlayerReqReplaceShowImage) request;
        if (StringUtil.isEmptyOrNull(info.newShowImageFileName) || info.newShowImageFileName.length() >= (Constant.LEN_IMAGE - Config.FILE_DOWNLOAD_SERVER_URL.length())) {
            Logs.PLAYER.warn("%s replace show image error image:%s", player, info.newShowImageFileName);
            player.send(CommandId.CLI_NTF_PLAYER_REPLACE_SHOW_IMAGE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        if (info.showImageIndex < 0 || info.showImageIndex >= player.getShowImage().size()) {
            Logs.PLAYER.warn("%s replace show image error index error index:%d range:[%d, %d)", player, info.showImageIndex, 0, player.getShowImage().size());
            player.send(CommandId.CLI_NTF_PLAYER_REPLACE_SHOW_IMAGE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        player.replaceShowImage(info.showImageIndex, info.newShowImageFileName);
        player.send(CommandId.CLI_NTF_PLAYER_REPLACE_SHOW_IMAGE_OK, new PCLIPlayerNtfReplaceShowImageInfo(info.showImageIndex, Config.FILE_DOWNLOAD_SERVER_URL + info.newShowImageFileName));
        return null;
    }
}