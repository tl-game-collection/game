package com.xiuxiu.app.server.services.gateway.handler.club.helper;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubReqHelperInviteSet;
import com.xiuxiu.app.server.club.helper.ClubHelperManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 设置今日不接受邀请
 * 
 * @author Administrator
 *
 */
public class ClubHelperInviteSetHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqHelperInviteSet info = (PCLIClubReqHelperInviteSet) request;

        ClubHelperManager.I.resetAllowInvite(player.getUid(), info.status == 1);

        player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_SET_OK, null);
        return null;

    }
}
