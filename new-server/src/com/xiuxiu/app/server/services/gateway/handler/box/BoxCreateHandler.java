package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqCreateInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.box.constant.BoxConstants;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.List;

/**
 * 包厢创建
 * 
 * @author Administrator
 *
 */
public class BoxCreateHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqCreateInfo info = (PCLIBoxReqCreateInfo) request;
        if (info.clubUid <= 0 || info.floorUid <= 0 || info.boxType < 0 || info.gameType < 0 || info.gameSubType < 0) {
            player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == fromClub) {
            Logs.CLUB.warn("%s 创建包厢失败, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!fromClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 无法创建包厢", player, info.clubUid);
            if(fromClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        IClub club = null;
        // 判断是否加入主圈
        if (fromClub.checkIsJoinInMainClub()) {
            long finalClubId = fromClub.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法创建包厢", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
            if(!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid())
                    || club.checkIsManager(player.getUid()))){
                        player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.GROUP_NOT_PRIVILEGE_CREATE_BOX);
                return null;
            }

        } else {
            club = fromClub;
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.GROUP_NOT_PRIVILEGE_CREATE_BOX);
                return null;
            }
            // 判断房卡是否足够,圈主创建所有的亲友圈玩法桌之和
            Player chiefPlayer = PlayerManager.I.getPlayer(club.getOwnerId());
            int needCount = (chiefPlayer.getPlayDeskCount() + 1) * 50;
            if (chiefPlayer.getMoneyByType(EMoneyType.DIAMOND) < needCount) {
                Logs.CLUB.warn("圈主房卡不足");
                String str = "房卡不足,需圈主拥有" + needCount + "张房卡";
                ErrorCode code = ErrorCode.PLAYER_LACK_DIAMOND;
                code.setMsg(str);
                player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, code);
                return null;
            }
        }
        Floor floor = club.getFloor(info.floorUid);
        if (null == floor) {
            Logs.CLUB.warn("%s 创建包厢失败, 楼层不存在", player, info.floorUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.FLOOR_NOT_EXISTS);
            return null;
        }
        if (1 != floor.getFloorType()) {
            Logs.CLUB.warn("%s 创建包厢失败, 楼层分类不对", player, info.floorUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.FLOOR_FLOOR_TYPE_ERR);
            return null;
        }
        // 是否达到最大包厢数量
        if (floor.getShowGameUid().size() >= BoxConstants.MAX_BOX_COUNT) {
            Logs.CLUB.warn("%s 创建包厢失败, 群;%s 包厢已达上限", player, club);
            player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.GROUP_BOX_MAX);
            return null;
        }
        // 是否自定义包厢
        if (EBoxType.CUSTOM.match(info.boxType)) {
            List<Long> gameId = floor.getShowGameUid();
            for (long id : gameId) {
                Box box = BoxManager.I.getBox(id);
                if (EBoxType.CUSTOM.match(box.getBoxType())) {
                    Logs.CLUB.warn("%s 创建包厢失败, 群;%s 自定义包厢已达上限", player, club);
                    player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.GROUP_CUSTOM_ROOM_EXISTS);
                    return null;
                }
            }
        }
        Player ownerPlayer = PlayerManager.I.getPlayer(club.getOwnerId());
        if (null != ownerPlayer && club.getAllBox().size() >= EPlayerPrivilegeLevel.getValue(ownerPlayer.getPrivilege(),
                EPlayerPrivilege.ARENA_NUM)) {
            player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.GROUP_BOX_MAX);
            return null;
        }
        if (!BoxManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = BoxManager.I.create(player, club, floor, info.boxType, info.gameType, info.gameSubType,
                    info.rule, info.extra);
            if (err != null && ErrorCode.OK != err) {
                player.send(CommandId.CLI_NTF_BOX_CREATE_FAIL, err);
            }
        } finally {
            BoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
