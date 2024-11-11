package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqRoomCardConvertGold;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.club.constant.EConvertType;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubRoomCardConvertGoldHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqRoomCardConvertGold info = (PCLIClubReqRoomCardConvertGold) request;

        //check something
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        if (player.getUid() == club.getOwnerId()){
            Logs.CLUB.warn("%s clubUid:%d 圈主不能兑换", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_OWNER_NOT_CONVERT);
            return null;
        }

        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ec);
            return null;
        }

        if (club.getClubType() != EClubType.GOLD) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部类型不匹配", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_TYPE_NOT);
            return null;
        }

        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s info:%s 正在操作", player, info);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_IN_LOCK);
            return null;
        }
        try {
            Player clubPlayer = PlayerManager.I.getPlayer(club.getOwnerId());
            if (null == clubPlayer) {
                Logs.CLUB.warn("%s info:%s 圈主不存在", player, info);
                player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
                return null;
            }

            if (!player.hasMoney(EMoneyType.DIAMOND, Constant.DIAMOND_CONVERSION)) {
                Logs.CLUB.warn("%s info:%s 玩家房卡不足", player, info);
                player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_PLAYER_NOT_DIAMOND);
                return null;
            }

            ClubMemberExt clubMemberExt = club.getMemberExt(player.getUid(), true);
            if(clubMemberExt.getConvert() != EConvertType.NORMAL.ordinal()){
                Logs.CLUB.warn("%s info:%s 玩家已兑换过", player, info);
                player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_PLAYER_CONVERT);
                return null;
            }

            //扣除圈主竞技分
            if (!club.addMemberClubGold(clubPlayer.getUid(), -Constant.DIAMOND_CONVERSION * 100, clubPlayer.getUid(), EClubGoldChangeType.EXCHANGE_CONVERT_VALUE_DEC)) {
                Logs.CLUB.warn("%s info:%s 群主竞技分不足", player, info);
                player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_OWNER_NOT_ARENA_VALUE);
                return null;
            }

            //扣除玩家房卡
            if (!player.addMoney(EMoneyType.DIAMOND, -Constant.DIAMOND_CONVERSION, player.getUid(), club.getClubUid(), EMoneyExpendType.CONVERSION, player.getUid())) {
                if (!club.addMemberClubGold(clubPlayer.getUid(), Constant.DIAMOND_CONVERSION * 100, clubPlayer.getUid(), EClubGoldChangeType.EXCHANGE_CONVERT_VALUE_RET)){
                    Logs.CLUB.error("%s info:%s 玩家房卡不足,退还群主竞技分失败", player, info);
                }
                Logs.CLUB.warn("%s info:%s 玩家房卡不足,退还群主竞技分", player, info);
                player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_PLAYER_NOT_DIAMOND);
                return null;
            }

            //添加圈主房卡
            if(!clubPlayer.addMoney(EMoneyType.DIAMOND, Constant.DIAMOND_CONVERSION, clubPlayer.getUid(), club.getClubUid(), EMoneyExpendType.CONVERSION_DONATE, player.getUid())){
                Logs.CLUB.error("%s info:%s 添加圈主房卡失败", player, info);
                player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_PLAYER_NOT_DIAMOND);
                return null;
            }

            //添加玩家竞技分
            if (!club.addMemberClubGold(player.getUid(), Constant.DIAMOND_CONVERSION * 100, clubPlayer.getUid(), EClubGoldChangeType.EXCHANGE_CONVERT_VALUE_INC)){
                Logs.CLUB.error("%s info:%s 添加玩家竞技分失败", player, info);
                player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_PLAYER_NOT_DIAMOND);
                return null;
            }

            ClubInfo clubInfo = club.getClubInfo();
            clubInfo.setdToGoldTotal(clubInfo.getdToGoldTotal() + Constant.DIAMOND_CONVERSION * 100);
            clubInfo.setDirty(true);

            clubMemberExt.setConvert(EConvertType.CONVERT.ordinal());
            clubMemberExt.setDirty(Boolean.TRUE);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_OK, null);
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}
