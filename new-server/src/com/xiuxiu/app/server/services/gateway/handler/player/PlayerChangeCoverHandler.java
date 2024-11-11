package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangeCoverInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeCover;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerChangeCoverHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeCover info = (PCLIPlayerReqChangeCover) request;
        if (StringUtil.isEmptyOrNull(info.coverFileName) || info.coverFileName.length() >= (Constant.LEN_IMAGE - Config.FILE_DOWNLOAD_SERVER_URL.length())) {
            Logs.PLAYER.warn("%s change cover image error image:%s", player, info.coverFileName);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_COVER_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.changeCover(info.coverFileName);
        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_COVER_OK, new PCLIPlayerNtfChangeCoverInfo(Config.FILE_DOWNLOAD_SERVER_URL + info.coverFileName));
        return null;
    }
}