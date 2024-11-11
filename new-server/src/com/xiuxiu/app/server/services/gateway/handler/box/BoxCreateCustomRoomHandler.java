package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqCreateCustomRoomInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class BoxCreateCustomRoomHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqCreateCustomRoomInfo info = (PCLIBoxReqCreateCustomRoomInfo) request;
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == fromClub) {
            Logs.CLUB.warn("%s 群:%d不存在, 无法创建自定义包厢房间", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!fromClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 无法创建自定义包厢房间", player, info.clubUid);
            if(fromClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        IClub club = null;
        // 判断是否加入主圈
        if (fromClub.checkIsJoinInMainClub()) {
            long finalClubId = fromClub.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法创建自定义包厢房间", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        } else {
            club = fromClub;
        }
        if (fromClub.isForbidPlay(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 玩家被禁玩了", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.ACCOUNT_GROUP_PLAYER_FORBIDO_LAY);
            return null;
        }


        // 金币亲友圈不能创建自定义玩法桌，房卡亲友圈可以创建自定义玩法桌
        if (club.getClubType().match(EClubType.GOLD)) {
            Logs.CLUB.warn("%s 群:%d是金币圈, 无法创建自定义包厢房间", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.CLUB_GOLD_CREATE_CUSTOM_LIMIT);
            return null;
        }
        Box box = club.getBox(info.boxUid);
        if (null == box) {
            Logs.CLUB.warn("%s 群:%d 包厢:%d 不存在, 无法创建自定义包厢房间", player, info.clubUid, info.boxUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.GROUP_BOX_NOT_EXISTS);
            return null;
        }
        
        if (!BoxManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = BoxManager.I.canJoin(player, club, box, Boolean.FALSE);
            if (ErrorCode.OK == err) {
                club.playerEnterClub(player.getUid(), fromClub.getClubUid());
                err = box.createCustomRoom(player, info.gameType, info.gameSubType, info.rule,info.remarks);
            }
            if (ErrorCode.OK != err) {
                player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, err);
            }
        } finally {
            BoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
