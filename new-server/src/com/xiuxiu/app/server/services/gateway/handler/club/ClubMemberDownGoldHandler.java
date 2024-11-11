package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfDownGold;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfNewDownGoldOrder;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqDownGold;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.order.EUpDownGoldOrderType;
import com.xiuxiu.app.server.order.UpDownGoldOrder;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

/**
 * 请求财务下分
 * @date 2020/1/7 17:21
 * @author luocheng
 */
public class ClubMemberDownGoldHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqDownGold info = (PCLIPlayerReqDownGold) request;

        //----------检查and判断----------begin
        if (info.gold <= 0){
            Logs.CLUB.warn("%s player:%d无效请求数据", player, info.gold);
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s player:%d不在圈中", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            return null;
        }

        IClub rootClub = club;
        //合过圈
        if (club.checkIsJoinInMainClub()) {
            //不是主圈
            if (!club.checkIsMainClub()) {
                rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
            }
            //是主圈
            else {
                //大圈主不能下分
                if (rootClub.getOwnerId() == player.getUid()) {
                    Logs.CLUB.warn("%s player:%d大圈主不能下分", player, player.getUid());
                    player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.PLAY_OWNER_NO_DOWN_GOLD);
                    return null;
                }
            }
        }
        //没有合过圈
        else {
            //圈主不能下分
            if (club.getOwnerId() == player.getUid()) {
                Logs.CLUB.warn("%s player:%d圈主不能下分", player, player.getUid());
                player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.PLAY_OWNER_NO_DOWN_GOLD);
                return null;
            }
        }

        //下分分数小于设置最低值不能下分
        if (info.gold < rootClub.getClubInfo().getTreasurerCanDownGoldMinValue()){
            Logs.CLUB.warn("%s player:%d下分低于%d不能下分", player, player.getUid(),club.getClubInfo().getTreasurerCanDownGoldMinValue());
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.PLAY_NO_DOWN_GOLD_LESS_VALUE);
            return null;
        }

        int orderServiceCharge = UpDownGoldTreasurerManager.I.getNewUpOrderCharge(club, rootClub,player.getUid(), info.gold);
        info.gold = orderServiceCharge + info.gold;
        if (!club.hasGold(player.getUid(),info.gold)) {
            Logs.CLUB.warn("%s player:%d金币不足", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.CLUB_OWNER_NOT_ARENA_VALUE);
            return null;
        }

        //财务自身不能下分
        if (rootClub.getClubInfo().getDownGoldTreasurer().contains(player.getUid())) {
            Logs.CLUB.warn("%s player:%d财务不能下分", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.PLAY_TREASURER_NO_DOWN_GOLD);
            return null;
        }
        //本圈没有设置财务
        if (rootClub.getClubInfo().getDownGoldTreasurer().size() == 0) {
            Logs.CLUB.warn("%s club:%d本圈没有设置下分财务", player, rootClub.getClubUid());
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.CLUB_NOT_SET_DOWN_GOLD_TREASURER);
            return null;
        }
        //主圈打烊状态不能下分
        if (!rootClub.getClubInfo().matchCloseStatus(EClubCloseStatus.OPEN)) {
            Logs.CLUB.warn("%s 亲友圈打烊中", player);
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.CLUB_CLOSE_STATUS_LIMIT_ROOM_CARD);
            return null;
        }
        //没有下分财务在线
        Player respPlayer = null; //下分财务
        for (Long tempPlayerUid : rootClub.getClubInfo().getDownGoldTreasurer()) {
            Player tempPlayer = PlayerManager.I.getOnlinePlayer(tempPlayerUid);
            if (tempPlayer == null) {
                continue;
            }
            if (respPlayer == null) {
                respPlayer = tempPlayer;
            } else {
                long respPlayerTime = rootClub.getMemberExt(respPlayer.getUid(),true).getDownGoldOrderLastTime();
                long tempPlayerTime = rootClub.getMemberExt(tempPlayer.getUid(),true).getDownGoldOrderLastTime();
                if (respPlayerTime > tempPlayerTime) {
                    respPlayer = tempPlayer;
                }
            }
        }
        if (respPlayer == null) {
            Logs.CLUB.warn("%s player:%d没有下分财务在线", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.CLUB_NOT_DOWN_GOLD_TREASURER_ONLINE);
            return null;
        }
        //在本圈游戏中不能在本圈下分
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (room != null) {
            /*IBoxOwner boxOwner = room.getBoxOwner();
            if (boxOwner != null){
                if (boxOwner instanceof IClub && ((IClub) boxOwner).getEnterFromClubUid(player.getUid()) == info.clubUid) {
                    Logs.CLUB.warn("%s playerUid:%d 在游戏中不能上下分", player, player.getUid());
                    player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.SET_GOLD_DEC_ERROR_IN_GAME);
                    return null;
                }
            }*/
            Logs.CLUB.warn("%s playerUid:%d 在游戏中不能上下分", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.SET_GOLD_DEC_ERROR_IN_GAME);
        }

        //两次下分时间小于30min就不能下分
        long lastTime = club.getMemberExt(player.getUid(), true).getOrderCreateTime();
        long timeSpace = System.currentTimeMillis() - lastTime;
        if (timeSpace <= Constant.DOWN_GOLD_TIME_SPACE) {
            Logs.CLUB.warn("%s 下分间隔小于30min %d", player, lastTime);
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.PLAY_NO_DOWN_GOLD_LESS_TIME);
            return null;
        }

        //锁定状态不能下分(合圈申请中)
        if (club.getClubInfo().getLockTime() > System.currentTimeMillis()) {
            Logs.CLUB.warn("%s 锁定状态无法操作", player);
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.CLUB_IN_MERGE);
            return null;
        }
        //----------检查and判断----------end

        //----------数据and消息----------begin
        if (!club.addMemberClubGold(player.getUid(), -Math.abs(info.gold), respPlayer.getUid(), EClubGoldChangeType.INC_DOWN_TREASURER_DEC)){
            Logs.CLUB.warn("%s player:%d金币不足", player, player.getUid());
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.CLUB_OWNER_NOT_ARENA_VALUE);
            return null;
        }

        //保存下分订单
        long nowTime = System.currentTimeMillis();
        UpDownGoldOrder order = new UpDownGoldOrder();
        order.setUid(UIDManager.I.getAndInc(UIDType.UPDOWN_GOLD_ORDER));
        order.setClubUid(info.clubUid);
        order.setMainClubUid(rootClub.getClubUid());
        order.setValue(info.gold);
        order.setChargeValue(orderServiceCharge);
        order.setCreateAt(TimeUtil.getZeroTimestamp(nowTime));
        order.setCreateAtDetail(nowTime);
        order.setOptAt(-1);
        order.setOptAtDetail(-1);
        order.setPlayerUid(player.getUid());
        order.setOptPlayerUid(respPlayer.getUid());
        order.setBankCard(info.bankCard);
        order.setBankCardHolder(info.bankCardHolder);
        order.setState(EUpDownGoldOrderType.WAIT.getValue());

        if (!UpDownGoldTreasurerManager.I.saveUpDownGoldOrder(order, true)){
            Logs.CLUB.warn("%s 保存订单失败:%s", player, info);
            player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_FAIL, ErrorCode.PLAY_TRANSFER_CREATE_ORDER_FAIL);
            return null;
        }
        //返回请求成功消息
        PCLIPlayerNtfDownGold resp = new PCLIPlayerNtfDownGold();
        resp.clubUid = info.clubUid;
        resp.goldTotal = club.getGold(player.getUid());
        player.send(CommandId.CLI_NTF_PLAYER_DOWN_GOLD_OK, resp);

        club.getMemberExt(player.getUid(),true).setOrderCreateTime(nowTime);

        //通知财务有新的下分订单
        PCLIPlayerNtfNewDownGoldOrder newDownGoldOrderResp = new PCLIPlayerNtfNewDownGoldOrder();
        newDownGoldOrderResp.orderId = order.getUid();
        newDownGoldOrderResp.treasurerClubUid = rootClub.getClubUid();
        respPlayer.send(CommandId.CLI_NTF_PLAYER_NEW_DOWN_GOLD_ORDER, newDownGoldOrderResp);

        rootClub.getMemberExt(respPlayer.getUid(),true).setDownGoldOrderLastTime(nowTime);
        //----------数据and消息----------end
        return null;
    }
}
