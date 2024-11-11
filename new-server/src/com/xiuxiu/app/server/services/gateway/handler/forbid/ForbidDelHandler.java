package com.xiuxiu.app.server.services.gateway.handler.forbid;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidNtfDel;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidReqDel;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.forbid.Forbid;
import com.xiuxiu.app.server.forbid.ForbidManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ForbidDelHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIForbidReqDel info = (PCLIForbidReqDel) request;
        Forbid forbid = new Forbid();
        IClub club= ClubManager.I.getClubByUid(info.clubUid);
            if (null == club){
                Logs.CLUB.warn("%s 群:%d不存在", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_DEL_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
                return null;
            }
        if (club.checkIsMainClub()) {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.checkIsManager(player.getUid()))) {
                Logs.CLUB.warn("%s 群:%d不是群主 或管理员, 权限不足, 无法删除防作弊", player, club.getClubUid());
                player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_DEL_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        } else {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                Logs.CLUB.warn("%s 群:%d不是群主 或者副圈主, 权限不足, 无法删除防作弊", player, club.getClubUid());
                player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_DEL_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        }
            forbid.setClubType(club.getClubType().getType());

        //deal
        forbid.setUid(info.uid);
        forbid.setClubUid(info.clubUid);
        ForbidManager.I.remove(forbid);
        DBManager.I.save(() -> {
            DBManager.I.getForbidDAO().delByUid(info.uid);
        });

        PCLIForbidNtfDel result = new PCLIForbidNtfDel();
        result.uid = info.uid;
        result.clubUid = info.clubUid;
        player.send(CommandId.CLI_NTF_CLUB_FORBID_LIST_DEL_OK, result);
        return null;
    }
}