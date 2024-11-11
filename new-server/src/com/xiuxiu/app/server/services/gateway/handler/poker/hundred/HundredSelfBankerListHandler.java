package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfSelfBankerList;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqSelfBankerList;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.Hundred.IHundredBanker;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.NumberUtils;

import java.util.Iterator;

/**
 * 请求获取自己的上庄记录
 */
public class HundredSelfBankerListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request){
        Player player = (Player) owner;
        PCLIHundredReqSelfBankerList info = (PCLIHundredReqSelfBankerList) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (room == null){
            Logs.ARENA.warn("%s 百人场不存在, 无法获取自己上庄列表 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_SELFBANKER_LIST_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        PCLIHundredNtfSelfBankerList selfBankerList = new PCLIHundredNtfSelfBankerList();
        selfBankerList.boxId = info.boxId;
        selfBankerList.roomId = info.roomId;
        selfBankerList.page = info.page;
        selfBankerList.pageSize = info.pageSize;

        IHundredHandle hundredHandle = (IHundredHandle)room.getRoomHandle();
        Iterator<IHundredBanker> it = hundredHandle.getBankerList().iterator();

        int count = 0;//条数
        int m_num = 1;//序号
        while (it.hasNext()){
            IHundredBanker banker = it.next();
            m_num++;
            if (banker.getUid() != player.getUid()) {
                continue;
            }
            count++;
            if (count <= info.page * info.pageSize) {
                continue;
            }
            if (count > (info.page + 1) * info.pageSize) {
                continue;
            }
            PCLIHundredNtfSelfBankerList.SelfBankerInfo selfBankerInfo = new PCLIHundredNtfSelfBankerList.SelfBankerInfo();
            selfBankerInfo.playerUid = player.getUid();
            selfBankerInfo.playerName = player.getName();
            selfBankerInfo.playerIcon = player.getIcon();
            selfBankerInfo.bankerUid = banker.getBankerUid();
            selfBankerInfo.value = NumberUtils.get2Decimals(banker.getScore());
            selfBankerInfo.num = m_num;
            selfBankerList.list.add(selfBankerInfo);
        }
        selfBankerList.next = selfBankerList.list.size() == info.pageSize;

        player.send(CommandId.CLI_NTF_ARENA_HUNDRED_SELFBANKER_LIST_OK, selfBankerList);
        return null;
    }
}
