package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfGetDownGoldOrder;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqGetDownGoldOrder;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.order.EUpDownGoldOrderType;
import com.xiuxiu.app.server.order.UpDownGoldOrder;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

import java.util.*;

/**
 * 获取下分订单
 * @date 2020/1/8 16:49
 * @author luocheng
 */
public class PlayerGetDownGoldOrderHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqGetDownGoldOrder info = (PCLIPlayerReqGetDownGoldOrder) request;

        //check something
        IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
        if (iClub == null) {
            Logs.PLAYER.warn("%s type:%d亲友圈不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_PLAYER_ENTER_CLUB_FAIL, ErrorCode.CLUB_NOT_EXISTS);
            return null;
        }
        if (info.type != 1 && info.type != 2) {
            Logs.PLAYER.warn("%s type:%d无效请求数据", player, info.type);
            player.send(CommandId.CLI_NTF_PLAYER_ENTER_CLUB_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        if (info.state < 0 || info.state > 2) {
            Logs.PLAYER.warn("%s state:%d无效请求数据", player, info.state);
            player.send(CommandId.CLI_NTF_PLAYER_ENTER_CLUB_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        if (info.page < 0 || info.pageSize <= 0) {
            Logs.PLAYER.warn("%s page:%d pageSize:%d无效请求数据", player, info.page, info.pageSize);
            player.send(CommandId.CLI_NTF_PLAYER_ENTER_CLUB_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        IClub rootClub = iClub;
        if (iClub.checkIsJoinInMainClub() && !iClub.checkIsMainClub()) {
            rootClub = ClubManager.I.getClubByUid(iClub.getFinalClubId());
        }

        //deal
        PCLIPlayerNtfGetDownGoldOrder resp = new PCLIPlayerNtfGetDownGoldOrder();
        List<UpDownGoldOrder> list = new ArrayList<>();
        //申请人查询
        if (info.type == 1) {
            if (info.state == EUpDownGoldOrderType.WAIT.getValue()) {
                List<Long> orders = UpDownGoldTreasurerManager.I.getWaitDealOrderUidByPlayerUid(player.getUid(), info.clubUid);
                fillOrderByPage(list,orders,info.page,info.pageSize,false);
            } else {
                list = DBManager.I.getUpDownGoldOrderDao().loadByPlayerUidAndState(player.getUid(), info.clubUid, info.state,info.page * info.pageSize, info.pageSize + 1,getMinTime());
            }
        }
        //财务查询
        else {
            if (info.state == EUpDownGoldOrderType.WAIT.getValue()) {
                List<Long> orders = UpDownGoldTreasurerManager.I.getWaitDealOrderUidByTreasurerUid(player.getUid(), rootClub.getClubUid());
                fillOrderByPage(list,orders,info.page,info.pageSize,true);

                resp.waitCount = orders.size();
            } else {
                list = DBManager.I.getUpDownGoldOrderDao().loadByOptPlayerUidAndState(player.getUid(), rootClub.getClubUid(), info.state,info.page * info.pageSize, info.pageSize + 1,getMinTime());
            }
        }

        resp.type = info.type;
        resp.state = info.state;
        resp.page = info.page;
        resp.pageSize = info.pageSize;
        resp.next = list.size() == info.pageSize + 1;
        for (UpDownGoldOrder tempOrder : list) {
            PCLIPlayerNtfGetDownGoldOrder.DownGoldOrder downGoldOrder = new PCLIPlayerNtfGetDownGoldOrder.DownGoldOrder();
            downGoldOrder.orderId = tempOrder.getUid();
            downGoldOrder.clubUid = tempOrder.getClubUid();
            downGoldOrder.value = tempOrder.getValue();
            downGoldOrder.chargeValue = tempOrder.getChargeValue();
            downGoldOrder.createAt = tempOrder.getCreateAt();
            downGoldOrder.createAtDetail = tempOrder.getCreateAtDetail();
            downGoldOrder.optAt = tempOrder.getOptAt();
            downGoldOrder.optAtDetail = tempOrder.getOptAtDetail();
            downGoldOrder.playerUid = tempOrder.getPlayerUid();
            Player tempPlayer = PlayerManager.I.getPlayer(tempOrder.getPlayerUid());
            if (tempPlayer != null) {
                downGoldOrder.playerName = tempPlayer.getName();
            }
            downGoldOrder.optPlayerUid = tempOrder.getOptPlayerUid();
            Player optPlayer = PlayerManager.I.getPlayer(tempOrder.getOptPlayerUid());
            downGoldOrder.optName = optPlayer != null ? optPlayer.getName() : "";
            downGoldOrder.bankCard = tempOrder.getBankCard();
            downGoldOrder.bankCardHolder = tempOrder.getBankCardHolder();
            downGoldOrder.state = tempOrder.getState();

            resp.list.add(downGoldOrder);
        }
        player.send(CommandId.CLI_NTF_PLAYER_GET_DOWN_GOLD_ORDER_OK, resp);
        return null;
    }

    private void fillOrderByPage(List<UpDownGoldOrder> fillList,List<Long> orders,int page,int pageSize,boolean asc){
        if (null == orders) {
            return;
        }
        if (asc){
            Collections.sort(orders, new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return (int)(o1 - o2);
                }
            });
        }else{
            Collections.sort(orders, new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return (int)(o2 - o1);
                }
            });
        }

        int count = 0;
        for (Long orderUid : orders) {
            UpDownGoldOrder order = UpDownGoldTreasurerManager.I.getWaitDealOrderFromCache(orderUid);
            if (order == null) {
                continue;
            }
            count++;
            if (count <= page * pageSize) {
                continue;
            }
            if (fillList.size() == (pageSize + 1)) {
                break;
            }
            fillList.add(order);
        }
    }

    private long getMinTime(){
        return TimeUtil.getZeroTimestampWithToday() - 172800000;
    }
}
