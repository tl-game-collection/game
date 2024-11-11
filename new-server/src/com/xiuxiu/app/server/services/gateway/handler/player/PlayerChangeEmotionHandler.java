package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangeEmotionInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeEmotion;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PlayerChangeEmotionHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeEmotion info = (PCLIPlayerReqChangeEmotion) request;
        if (info.emotion < 0 || info.emotion > 5) {
            Logs.PLAYER.warn("%s change emotion error emotion:%d", player, info.emotion);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_EMOTION_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.changeEmotion(info.emotion);
        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_EMOTION_OK, new PCLIPlayerNtfChangeEmotionInfo(info.emotion));
        return null;
    }
}
