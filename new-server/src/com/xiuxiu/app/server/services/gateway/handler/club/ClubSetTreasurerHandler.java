package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfSetTreasurer;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetTreasurer;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubTreasurerType;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.message.Handler;

/**
 * 设置club上下分财务
 * @date 2020/1/7 11:32
 * @author luocheng
 */
public class ClubSetTreasurerHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetTreasurer info = (PCLIClubReqSetTreasurer) request;

        //----------检查and判断----------begin
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            return null;
        }

        if (!club.hasMember(info.playerUid)) {
            Logs.CLUB.warn("%s player:%d不在圈中", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            return null;
        }

        Player treasurerPlayer = PlayerManager.I.getPlayer(info.playerUid);
        if (treasurerPlayer == null) {
            Logs.CLUB.warn("%s player:%d玩家不存在", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        if (info.type != EClubTreasurerType.UP.getValue() && info.type != EClubTreasurerType.DOWN.getValue()) {
            Logs.CLUB.warn("%s type:%d无效请求数据", player, info.type);
            player.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        if (player.getUid() == info.playerUid) {
            Logs.CLUB.warn("%s playerUid:%d不能设置自己为财务", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL, ErrorCode.PLAY_NO_SET_SELF_TREASURER);
            return null;
        }

        if (player.getUid() != club.getOwnerId()) {
            Logs.CLUB.warn("%s club:%d不是圈主不能设置", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL, ErrorCode.GROUP_CHIEF_NOT);
            return null;
        }

        //如果合过圈
        if (club.checkIsJoinInMainClub()) {
            if (!club.checkIsMainClub()) {
                Logs.CLUB.warn("%s club:%d不是主圈不能设置", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO_FAIL, ErrorCode.CLUB_NOT_MAIN_CLUB);
                return null;
            }
        }
        //----------检查and判断----------end

        //----------数据and消息----------begin
        ConcurrentHashSet<Long> treasurerMap = null;
        //上分财务
        if (info.type == EClubTreasurerType.UP.getValue()) {
            treasurerMap = club.getClubInfo().getUpGoldTreasurer();
        }
        //下分财务
        else{
            treasurerMap = club.getClubInfo().getDownGoldTreasurer();
        }
        //设置财务身份
        if (info.isSet) {
            treasurerMap.add(info.playerUid);
        }
        //取消财务身份
        else {
            if (treasurerMap.contains(info.playerUid)) {
                if (info.type == EClubTreasurerType.DOWN.getValue()) {
                    //被取消财务身份的玩家所有未处理的下分订单全部自动拒绝
                    UpDownGoldTreasurerManager.I.clearTreasurerAllOrder(info.playerUid, info.clubUid);
                }

                treasurerMap.remove(info.playerUid);
            }
        }
        club.getClubInfo().setDirty(true);

        //玩家设置财务成功返回
        PCLIClubNtfSetTreasurer resp = new PCLIClubNtfSetTreasurer();
        resp.clubUid = info.clubUid;
        resp.playerUid = info.playerUid;
        resp.type = info.type;
        resp.isSet = info.isSet;
        player.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO_OK, resp);
        //通知被设置的人
        treasurerPlayer.send(CommandId.CLI_NTF_CLUB_SET_TREASURER_INFO, resp);
        //----------数据and消息----------end
        return null;
    }
}
