package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangeWechat;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeWechat;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerChangeWechatHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeWechat info = (PCLIPlayerReqChangeWechat) request;
        if (info.wechat.length() >= Constant.LEN_NAME) {
            Logs.PLAYER.warn("%s change wechat error: content too long, wechat:%s", player, info.wechat);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_WECHAT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.changeWechat(info.wechat);
        PCLIPlayerNtfChangeWechat ntf = new PCLIPlayerNtfChangeWechat();
        ntf.wechat = info.wechat;
        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_WECHAT_OK, ntf);
        return null;
    }
}
