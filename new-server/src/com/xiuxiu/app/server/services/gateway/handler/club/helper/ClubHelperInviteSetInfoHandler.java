package com.xiuxiu.app.server.services.gateway.handler.club.helper;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubNtfInviteSetInfo;
import com.xiuxiu.app.server.club.helper.ClubHelperManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 获取今日是否接爱邀请状态
 * 
 * @author Administrator
 *
 */
public class ClubHelperInviteSetInfoHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubNtfInviteSetInfo message = new PCLIClubNtfInviteSetInfo();
        message.status = ClubHelperManager.I.isAllowInvite(player.getUid()) ? 1 : 0;
        player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_SET_INFO_OK, message);
        return null;

    }
}
