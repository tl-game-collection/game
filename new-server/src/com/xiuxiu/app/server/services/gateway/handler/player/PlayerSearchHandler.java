package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfSearch;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqSearch;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

/**
 *
 */
public class PlayerSearchHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqSearch search = (PCLIPlayerReqSearch) request;
        Player p = PlayerManager.I.getPlayer(search.uid);
        if (null == p) {
            Logs.PLAYER.warn("%s 玩家不存在 %d", player, search.uid);
            player.send(CommandId.CLI_NTF_PLAYER_SEARCH_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }
        //判断玩家每日搜索权限
        long now = System.currentTimeMillis();
        if (!TimeUtil.isSameDay(now, player.getLastSearchTime())) {
            player.setLastSearchTime(now);
            player.setTodayInviteCnt(0);
        }
        if (player.getTodayInviteCnt() >= EPlayerPrivilegeLevel.getValue(player.getPrivilege(), EPlayerPrivilege.SEARCH_PLAYER)) {
            Logs.CLUB.warn("%s 每日搜索上限", player);
            player.send(CommandId.CLI_NTF_PLAYER_SEARCH_FAIL, ErrorCode.PLAYER_SEARCH_LIMIT_TODAY);
            return null;
        }
        player.setTodayInviteCnt(player.getTodayInviteCnt() + 1);
        if (!PlayerManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s info:%s 正在操作", player, search);
            player.send(CommandId.CLI_NTF_PLAYER_SEARCH_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            PCLIPlayerNtfSearch info = new PCLIPlayerNtfSearch();
            info.uid = search.uid;
            info.name = p.getName();
            info.icon = p.getIcon();
            info.count = p.isClub();
            player.send(CommandId.CLI_NTF_PLAYER_SEARCH_OK, info);
            return null;
        } finally {
            PlayerManager.I.unlock(player.getUid());
        }

    }
}
