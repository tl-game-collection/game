package com.xiuxiu.app.server.services.gateway.handler.floor;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.floor.PCLIFloorReqCreate;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.floor.FloorManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

/**
 * 创建楼层
 * 
 * @author Administrator
 *
 */
public class FloorCreateHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIFloorReqCreate info = (PCLIFloorReqCreate) request;
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == fromClub) {
            Logs.CLUB.warn("%s 创建楼层失败, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_CREATE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        boolean flag=fromClub.getClubType()== EClubType.GOLD;
        if (!fromClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 无法创建楼层", player, info.clubUid);
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
                Logs.CLUB.warn("%s 不在群:%d里, 无法创建楼层", player, info.clubUid);
                if(flag){
                    player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
                }else{
                    player.send(CommandId.CLI_NTF_CLUB_FLOOR_CLOSE_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
                }
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
            if(!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid())
                    || club.checkIsManager(player.getUid()))){
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
        if (StringUtil.isEmptyOrNull(info.name)) {
            Logs.CLUB.warn("%s 创建楼层失败, 名字为空", player);
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_CREATE_FAIL, ErrorCode.FLOOR_NAME_NULL);
            return null;
        }
        String name = info.name.trim();
        if (name.length() > 50) {
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_CREATE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        if (info.floorType < 1 || info.floorType > 2) {
            Logs.CLUB.warn("%s 创建楼层失败, 楼层类型错误, type;%d", player, info.floorType);
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_CREATE_FAIL, ErrorCode.FLOOR_FLOOR_TYPE_ERR);
            return null;
        }
        if (!FloorManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FLOOR_CREATE_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = FloorManager.I.create(player, club, info.ownerType, info.floorType, info.name,
                    info.layoutType);
            if (ErrorCode.OK != err) {
                player.send(CommandId.CLI_NTF_CLUB_FLOOR_CREATE_FAIL, err);
            }
        } finally {
            FloorManager.I.unlock(player.getUid());
        }
        return null;
    }
}
