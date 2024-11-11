package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangeSignatureInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeSignature;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerChangeSignatureHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeSignature info = (PCLIPlayerReqChangeSignature) request;
        if (null == info.signature || info.signature.length() >= Constant.LEN_SIGNATURE) {
            Logs.PLAYER.warn("%s change signature error signature:%d", player, info.signature);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_SIGNATURE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.changeSignature(info.signature);
        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_SIGNATURE_OK, new PCLIPlayerNtfChangeSignatureInfo(info.signature));
        return null;
    }
}