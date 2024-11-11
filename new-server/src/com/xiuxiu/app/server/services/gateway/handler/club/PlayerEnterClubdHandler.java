package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIPlayerReqEnterClub;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 修改 主亲友圈id
 * 
 * @author Administrator
 *
 */
public class PlayerEnterClubdHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqEnterClub info = (PCLIPlayerReqEnterClub) request;
        IClub club= ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.GROUP.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_PLAYER_ENTER_CLUB_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.GROUP.warn("%s info:%s 正在操作", player, info);
            player.send(CommandId.CLI_NTF_PLAYER_ENTER_CLUB_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            if (!club.hasMember(player.getUid())) {
                Logs.GROUP.warn("%s 不在群:%d里, 无法切换亲友圈", player, info.clubUid);
                ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
                player.send(CommandId.CLI_NTF_PLAYER_ENTER_CLUB_FAIL, ec);
                return null;
            }
            long clubUid=club.getFinalClubId();
            if(clubUid!=0){
                club=ClubManager.I.getClubByUid(clubUid);
            }
            club.playerEnterClub(player.getUid(),info.newClubUid);
            player.send(CommandId.CLI_NTF_PLAYER_ENTER_CLUB_OK, null);
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}