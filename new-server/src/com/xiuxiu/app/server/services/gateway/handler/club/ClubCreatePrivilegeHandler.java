package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqCreatePrivilege;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubCreatePrivilegeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqCreatePrivilege info = (PCLIClubReqCreatePrivilege) request;
        if (!player.hasPrivilege(EPlayerPrivilege.GROUP_NUM)) {
            Logs.CLUB.warn("%s 没有创建群权限, 无法创建群", player);
            player.send(CommandId.CLI_NTF_CLUB_CREATE_PRIVILEGE_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }
        if (player.getOwnerClubCnt() >= EPlayerPrivilegeLevel.getValue(player.getPrivilege(), EPlayerPrivilege.GROUP_NUM)) {
            Logs.CLUB.warn("%s 群上限", player);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.PLAYER_CLUB_LIMIT : ErrorCode.PLAYER_CLUB_LIMIT_GOLD;
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ec);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubType:%d 正在操作", player, info.clubType);
            player.send(CommandId.CLI_NTF_CLUB_CREATE_PRIVILEGE_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            player.send(CommandId.CLI_NTF_CLUB_CREATE_PRIVILEGE_OK, null);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
