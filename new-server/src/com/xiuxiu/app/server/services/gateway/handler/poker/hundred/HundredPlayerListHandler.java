package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfPlayerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqPlayerList;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.player.IHundredPlayer;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.NumberUtils;

/**
 * 请求获取玩家列表
 */
public class HundredPlayerListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIHundredReqPlayerList info = (PCLIHundredReqPlayerList) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ARENA.warn("%s 百人场不存在, 无法获取庄家列表 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_PLAYER_LIST_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        PCLIHundredNtfPlayerList bankerList = new PCLIHundredNtfPlayerList();
        bankerList.boxId = info.boxId;
        bankerList.roomId = info.roomId;
        bankerList.page = info.page;

        IHundredHandle hundredHandle = (IHundredHandle)room.getRoomHandle();
        IHundredPlayer[] allPlayer = hundredHandle.getAllPlayerList();
        for (int begin = Constant.PAGE_CNT_10 * info.page, end = begin + Constant.PAGE_CNT_10, len = Math.min(end, allPlayer.length); begin < len; ++begin) {
            Player tempPlayer = PlayerManager.I.getPlayer(allPlayer[begin].getUid());
            if (null == tempPlayer) {
                continue;
            }
            PCLIHundredNtfPlayerList.PlayerInfo playerInfo = new PCLIHundredNtfPlayerList.PlayerInfo();
            playerInfo.playerUid = tempPlayer.getUid();
            playerInfo.playerName = tempPlayer.getName();
            playerInfo.playerIcon = tempPlayer.getIcon();
            playerInfo.value = NumberUtils.get2Decimals(allPlayer[begin].getGold(room));
            bankerList.list.add(playerInfo);
        }
        bankerList.next = bankerList.list.size() == Constant.PAGE_CNT_10;

        player.send(CommandId.CLI_NTF_ARENA_HUNDRED_PLAYER_LIST_OK, bankerList);
        return null;
    }
}
