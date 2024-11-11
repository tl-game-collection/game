package com.xiuxiu.app.server.services.gateway.handler.floor;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorReqClose;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.app.server.floor.FloorManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 关闭楼层
 *
 * @author Administrator
 */
public class FloorCloseHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIFloorReqClose info = (PCLIFloorReqClose) request;
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == fromClub) {
            Logs.CLUB.warn("%s 关闭楼层失败, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        boolean flag=fromClub.getClubType()== EClubType.GOLD;
        if (!fromClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 关闭楼层失败", player, info.clubUid);
            if(flag){
                player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        IClub club = null;
        // 判断是否加入主圈
        if (fromClub.checkIsJoinInMainClub()) {
            long finalClubId = fromClub.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 关闭楼层失败", player, info.clubUid);
                if(flag){
                    player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
                }else{
                    player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
                }
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid())
                    || club.checkIsManager(player.getUid()))) {
                player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.FLOOR_NOT_PRIVILEGE);
                return null;
            }
        } else {
            club = fromClub;
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.FLOOR_NOT_PRIVILEGE);
                return null;            }
        }
        Floor floor = club.getFloor(info.uid);
        if (null == floor) {
            Logs.CLUB.warn("%s 关闭楼层失败, 楼层:%d 不存在", player, info.uid);
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.FLOOR_NOT_EXISTS);
            return null;
        }
        if (!FloorManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = FloorManager.I.close(player, club, floor);
            if (ErrorCode.OK != err) {
                player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, err);
            }
        } finally {
            FloorManager.I.unlock(player.getUid());
        }
        return null;
    }
}
