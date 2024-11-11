package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetLikeGame;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 设置喜欢的游戏
 */
public class ClubSetLikeGameHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetLikeGame info = (PCLIClubReqSetLikeGame) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_LIKE_GAME_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if(!club.hasMember(player.getUid())){
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_LIKE_GAME_FAIL, ec);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_LIKE_GAME_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        try {
            ClubMember clubMember = club.getMember(player.getUid());
            if (null == clubMember) {
                Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
                ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
                player.send(CommandId.CLI_NTF_CLUB_LIKE_GAME_FAIL, ec);
                return null;
            }
            clubMember.changeLikeGames(info.gameType, info.gameSubTypes);
            player.send(CommandId.CLI_NTF_CLUB_LIKE_GAME_OK, null);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}