package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangeNameInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeName;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerChangeNameHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeName info = (PCLIPlayerReqChangeName) request;
        if (StringUtil.isEmptyOrNull(info.newName) || info.newName.length() >= Constant.LEN_NAME) {
            Logs.PLAYER.warn("%s change name error name:%s", player, info.newName);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_NAME_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        ErrorCode err = player.changeName(info.newName);
        if (ErrorCode.OK == err) {
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_NAME_OK, new PCLIPlayerNtfChangeNameInfo(info.newName));
        } else {
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_NAME_FAIL, err);
        }
        return null;
    }
}
