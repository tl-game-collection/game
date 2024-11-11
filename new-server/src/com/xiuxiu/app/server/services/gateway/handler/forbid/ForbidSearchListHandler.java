package com.xiuxiu.app.server.services.gateway.handler.forbid;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidNtfSearchList;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidReqSearchList;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class ForbidSearchListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIForbidReqSearchList info = (PCLIForbidReqSearchList) request;

        if (null == info){
            Logs.CLUB.warn("ForbidSearchListHandler参数错误");
            player.send(CommandId.CLI_NTF_CLUB_FORBID_SEARCH_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        String searchContent = info.search;
        if(StringUtil.isEmptyOrNull(searchContent)) {
            Logs.CLUB.warn("ForbidSearchListHandler参数错误");
            player.send(CommandId.CLI_NTF_CLUB_FORBID_SEARCH_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        IClub club= ClubManager.I.getClubByUid(info.clubUid);
        if (null == club){
            Logs.CLUB.warn("%s 亲友圈:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_FORBID_SEARCH_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (club.checkIsMainClub()) {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.checkIsManager(player.getUid()))) {
                Logs.CLUB.warn("%s 群:%d不是群主 或管理员, 权限不足, 无法添加防作弊", player, club.getClubUid());
                player.send(CommandId.CLI_NTF_CLUB_FORBID_SEARCH_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        } else {
            // 是否有权限创建
            if (!(club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                    || club.matchMemberType(EClubJobType.DEPUTY, player.getUid()))) {
                Logs.CLUB.warn("%s 群:%d不是群主 或者副圈主, 权限不足, 无法添加防作弊", player, club.getClubUid());
                player.send(CommandId.CLI_NTF_CLUB_FORBID_SEARCH_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        }
        PCLIForbidNtfSearchList result = new PCLIForbidNtfSearchList();
        result.players = club.search(searchContent);
        player.send(CommandId.CLI_NTF_CLUB_FORBID_SEARCH_OK, result);
        return null;
    }
}