package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqUploadIconSucc;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerUploadIconSuccHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqUploadIconSucc info = (PCLIPlayerReqUploadIconSucc) request;
        if (StringUtil.isEmptyOrNull(info.fileName) || info.fileName.length() >= (Constant.LEN_IMAGE - Config.FILE_DOWNLOAD_SERVER_URL.length())) {
            Logs.PLAYER.warn("%s change icon error icon:%s", player, info.fileName);
            player.send(CommandId.CLI_NTF_PLAYER_UPLOAD_ICON_SUCC_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        ErrorCode err = player.changeIcon(Config.FILE_DOWNLOAD_SERVER_URL + info.fileName);
        if (ErrorCode.OK == err) {
            player.send(CommandId.CLI_NTF_PLAYER_UPLOAD_ICON_SUCC_OK, null);
        } else {
            player.send(CommandId.CLI_NTF_PLAYER_UPLOAD_ICON_SUCC_FAIL, err);
        }
        return null;
    }
}
