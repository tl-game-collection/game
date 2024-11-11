package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfSetDownGoldOrder;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqSetDownGoldOrder;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.order.EUpDownGoldOrderType;
import com.xiuxiu.app.server.order.UpDownGoldOrder;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

/**
 * 财务操作订单
 * @date 2020/1/8 17:50
 * @author luocheng
 */
public class PlayerSetDownGoldOrderHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqSetDownGoldOrder info = (PCLIPlayerReqSetDownGoldOrder) request;
        //----------检查and判断----------begin
        if (info.state != EUpDownGoldOrderType.DEAL.getValue() && info.state != EUpDownGoldOrderType.REFUSE.getValue()) {
            Logs.PLAYER.warn("%s state:%d无效请求数据", player, info.state);
            player.send(CommandId.CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        UpDownGoldOrder order = UpDownGoldTreasurerManager.I.getWaitDealOrderFromCache(info.orderId);
        if (null == order) {
            Logs.PLAYER.warn("%s orderId:%d订单不存在", player, info.orderId);
            player.send(CommandId.CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER_FAIL, ErrorCode.PLAY_DOWN_GOLD_ORDER_NO_EXIST);//下分订单不存在
            return null;
        }

        ErrorCode errorCode = UpDownGoldTreasurerManager.I.changeUpDownGoldOrderState(order, info.state, true);
        if (errorCode != ErrorCode.OK) {
            player.send(CommandId.CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER_FAIL, errorCode);
        }
        //----------检查and判断----------end

        //----------数据and消息----------begin
        //给操作订单的财务返回操作成功消息
        PCLIPlayerNtfSetDownGoldOrder resp = new PCLIPlayerNtfSetDownGoldOrder();
        resp.orderId = info.orderId;
        resp.state = info.state;
        player.send(CommandId.CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER, resp);
        //通知订单的申请人
        Player tempPlayer = PlayerManager.I.getOnlinePlayer(order.getPlayerUid());
        if (tempPlayer != null) {
            tempPlayer.send(CommandId.CLI_NTF_PLAYER_SET_DOWN_GOLD_ORDER, resp);
        }
        //----------数据and消息----------end
        return null;
    }
}
