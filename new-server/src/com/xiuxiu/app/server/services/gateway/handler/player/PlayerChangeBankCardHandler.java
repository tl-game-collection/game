package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangebankCardInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangeBankCard;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 修改银行卡号相关信息
 * 
 * @author Administrator
 *
 */
public class PlayerChangeBankCardHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangeBankCard info = (PCLIPlayerReqChangeBankCard) request;
        if (null == info.type || !(info.type == 0 || info.type == 1)) {
            Logs.PLAYER.warn("%s change bankcard error type%d,value:%s", player, info.type, info.value);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_BANKCARD_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        ErrorCode err = player.changeBankCard(info.type, info.value);
        if (ErrorCode.OK == err) {
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_BANKCARD_OK,
                    new PCLIPlayerNtfChangebankCardInfo(info.type, info.value));
        } else {
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_BANKCARD_FAIL, err);
        }
        return null;
    }
}
