package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetGoldInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.services.gateway.handler.club.ClubSetGoldHandler;
import com.xiuxiu.core.net.message.Handler;

public class ClubGiveGoldHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetGoldInfo info = (PCLIClubReqSetGoldInfo) request;

        //check something
        if (info.changeArenaValue <= 0){
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        //目前客户端端只能同圈操作，不需要考虑打烊，合并锁圈
        IClub optClub = ClubManager.I.getClubByUid(info.optClubUid);
        if (null == optClub || optClub.getClubType() != EClubType.GOLD) {
            Logs.CLUB.warn("%s CLUB_UID:%d 群不存在", player, info.optClubUid);
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL, ErrorCode.GOLD_IS_NOT_EXISTS);
            return null;
        }

        ClubMember member = optClub.getMember(player.getUid());
        if (member == null){
            Logs.CLUB.warn("%s playerUid:%d 玩家不存在", player, player.getUid());
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        IClub otherClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == otherClub || otherClub.getClubType() != EClubType.GOLD) {
            Logs.CLUB.warn("%s CLUB_UID:%d 群不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL, ErrorCode.GOLD_IS_NOT_EXISTS);
            return null;
        }

        ClubMember otherMember = otherClub.getMember(info.playerUid);
        if (otherMember == null){
            Logs.CLUB.warn("%s playerUid:%d 玩家不存在", player, info.playerUid);
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        Player otherPlayer = PlayerManager.I.getPlayer(info.playerUid);
        if (null == otherPlayer){
            Logs.CLUB.warn("%s playerUid:%d 玩家不存在", player, info.playerUid);
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        ErrorCode errorCode = ClubSetGoldHandler.checkInGame(info.changeArenaValue,info.optClubUid,info.clubUid,player,otherPlayer);
        if (errorCode != ErrorCode.OK){
            Logs.CLUB.warn("%s playerUid:%d 玩家在房间里无法上下分", player, info.playerUid);
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL, errorCode);
            return null;
        }

        if (optClub.getClubUid() != otherClub.getClubUid()){
            if (!optClub.checkIsJoinInMainClub()
                    || !otherClub.checkIsJoinInMainClub()
                    || optClub.getFinalClubId() != otherClub.getFinalClubId()
            ){
                player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL, ErrorCode.REQUEST_INVALID_DATA);
                return null;
            }
        }

        //deal
        if (!optClub.addMemberClubGold(player.getUid(),-Math.abs(info.changeArenaValue),info.playerUid, EClubGoldChangeType.GIVE_GOLD)){
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL,ErrorCode.CLUB_OWNER_NOT_ARENA_VALUE);
            return null;
        }

        if (!otherClub.addMemberClubGold(otherPlayer.getUid(),Math.abs(info.changeArenaValue),player.getUid(),EClubGoldChangeType.GIVE_GOLD)){
            player.send(CommandId.CLI_NTF_GIVE_GOLD_FAIL,ErrorCode.FAIL);
            Logs.CLUB.error("trade give gold fail player uid:%d , req info %s ",player.getUid(),info);
            return null;
        }

        player.send(CommandId.CLI_NTF_GIVE_GOLD_OK,null);
        return null;
    }
}


