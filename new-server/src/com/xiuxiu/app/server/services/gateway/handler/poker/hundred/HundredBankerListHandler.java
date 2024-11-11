package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfBankerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqBankerList;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.Hundred.IHundredBanker;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.NumberUtils;

import java.util.Iterator;

/**
 * 请求获取上庄列表
 */
public class HundredBankerListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIHundredReqBankerList info = (PCLIHundredReqBankerList) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ARENA.warn("%s 百人场不存在, 无法获取庄家列表 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_BANKER_LIST_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        IHundredHandle hundredHandle = (IHundredHandle)room.getRoomHandle();
        PCLIHundredNtfBankerList bankerList = new PCLIHundredNtfBankerList();
        bankerList.boxId = info.boxId;
        bankerList.roomId = info.roomId;
        bankerList.page = info.page;

        int begin = Constant.PAGE_CNT_10 * info.page;
        int end = begin + Constant.PAGE_CNT_10;
        if (0 == info.page) {
            --end;
            IHundredBanker hundredBanker = hundredHandle.getCurBanker();
            if (null != hundredBanker) {
                Player tempPlayer = PlayerManager.I.getPlayer(hundredBanker.getUid());
                if (null != tempPlayer) {
                    PCLIHundredNtfBankerList.BankerInfo bankerInfo = new PCLIHundredNtfBankerList.BankerInfo();
                    bankerInfo.playerUid = tempPlayer.getUid();
                    bankerInfo.playerName = tempPlayer.getName();
                    bankerInfo.playerIcon = tempPlayer.getIcon();
                    bankerInfo.bankerUid = hundredBanker.getBankerUid();
                    bankerInfo.value = NumberUtils.get2Decimals(hundredBanker.getScore());
                    bankerList.list.add(bankerInfo);
                }
            }
        }
        int index = 0;
        Iterator<IHundredBanker> it = hundredHandle.getBankerList().iterator();
        while (it.hasNext()) {
            IHundredBanker banker = it.next();
            if (index < begin) {
                ++index;
                continue;
            }
            if (index >= end) {
                break;
            }
            Player tempPlayer = PlayerManager.I.getPlayer(banker.getUid());
            if (null == tempPlayer) {
                continue;
            }
            PCLIHundredNtfBankerList.BankerInfo bankerInfo = new PCLIHundredNtfBankerList.BankerInfo();
            bankerInfo.playerUid = tempPlayer.getUid();
            bankerInfo.playerName = tempPlayer.getName();
            bankerInfo.playerIcon = tempPlayer.getIcon();
            bankerInfo.bankerUid = banker.getBankerUid();
            bankerInfo.value = NumberUtils.get2Decimals(banker.getScore());
            bankerList.list.add(bankerInfo);
            ++index;
        }

        bankerList.next = bankerList.list.size() == Constant.PAGE_CNT_10;

        player.send(CommandId.CLI_NTF_ARENA_HUNDRED_BANKER_LIST_OK, bankerList);
        return null;
    }
}
