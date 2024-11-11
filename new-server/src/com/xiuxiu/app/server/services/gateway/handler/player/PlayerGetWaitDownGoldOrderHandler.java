package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfGetWaitDownGoldOrder;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqGetWaitDownGoldOrder;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.List;

/**
 * 获取财务是否有未审核的下分订单
 * @date 2020/1/17 10:09
 * @author luocheng
 */
public class PlayerGetWaitDownGoldOrderHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqGetWaitDownGoldOrder info = (PCLIPlayerReqGetWaitDownGoldOrder) request;
        //----------检查and判断----------begin
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_PLAYER_GET_WAIT_DOWN_GOLD_ORDER_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            return null;
        }
        //----------检查and判断----------end

        //----------数据and消息----------begin
        List<Long> orders = UpDownGoldTreasurerManager.I.getWaitDealOrderUidByTreasurerUid(player.getUid(), info.clubUid);
        PCLIPlayerNtfGetWaitDownGoldOrder resp = new PCLIPlayerNtfGetWaitDownGoldOrder();
        resp.clubUid = info.clubUid;
        resp.waitCount = orders.size();
        player.send(CommandId.CLI_NTF_PLAYER_GET_WAIT_DOWN_GOLD_ORDER_OK, resp);
        //----------数据and消息----------end
        return null;
    }
}
