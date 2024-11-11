package com.xiuxiu.app.server.services.gateway.handler.club;


import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqClubCaeateClub;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.regex.Matcher;

/**
 * 创建俱乐部
 */
public class ClubCreateHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqClubCaeateClub info = (PCLIClubReqClubCaeateClub) request;

        if (!player.hasPrivilege(EPlayerPrivilege.GROUP_NUM)) {
            Logs.CLUB.warn("%s 没有创建俱乐部权限, 无法创建俱乐部", player);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.PLAYER_PRIVILEGE_NOT_CREATE_GROUP : ErrorCode.PLAYER_PRIVILEGE_NOT_CREATE_GOLD;
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ec);
            return null;
        }

        if (player.getOwnerClubCnt() >= EPlayerPrivilegeLevel.getValue(player.getPrivilege(), EPlayerPrivilege.GROUP_NUM)) {
            Logs.CLUB.warn("%s 俱乐部上限", player);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.PLAYER_CLUB_LIMIT : ErrorCode.PLAYER_CLUB_LIMIT_GOLD;
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ec);
            return null;
        }

        if (null == info.name) {
            Logs.CLUB.warn("%s 俱乐部名称不能为空", player);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.ACCOUNT_GROUP_NAME_NULL : ErrorCode.ACCOUNT_GOLD_NAME_NULL;
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ec);
            return null;
        }

        if (info.name.length() > 14) {
            Logs.CLUB.warn("%s 俱乐部名称长度不符合", player);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.ACCOUNT_GROUP_NAME_NOT : ErrorCode.ACCOUNT_GOLD_NAME_NOT;
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ec);
            return null;
        }

        Matcher matcher = Constant.PATTERN_NAME.matcher(info.name);
        if (!matcher.matches()) {
            Logs.CLUB.warn("%s 俱乐部名称不合法", player);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.ACCOUNT_GROUP_NAME_NOT : ErrorCode.ACCOUNT_GOLD_NAME_NOT;
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ec);
            return null;
        }

        if (ClubManager.I.isExistName(EClubType.getType(info.clubType), info.name) || 0 != DBManager.I.getClubInfoDAO().isExistName(info.clubType, info.name)) {
            Logs.CLUB.warn("%s 俱乐部名称有相同, 无法创建俱乐部", player);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.PLAYER_GROUP_NAME_REPETITION : ErrorCode.PLAYER_GOLD_NAME_REPETITION;
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ec);
            return null;
        }

        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s 正在操作", player);
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        try {
            IClub iClub = ClubManager.I.create(player, info.clubType, info.name, info.desc, info.icon, info.gameDesc, 0);

            if (null == iClub) {
                Logs.CLUB.warn("%s iClub为空", player);
                ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.GROUP_IS_NOT_EXISTS : ErrorCode.GOLD_IS_NOT_EXISTS;
                player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_FAIL, ec);
                return null;
            }

            IClub iClubGold = ClubManager.I.getClubByUid(info.clubUid);
            iClub.copyMember(iClubGold, EClubType.getType(info.clubType));
            player.send(CommandId.CLI_NTF_CLUB_CREATE_CLUB_OK, iClub.getClubSingleInfoPCL(player));
            player.addOwnerClubCnt(true);
            // 创建亲友圈时初始化活动数据
            ClubActivityManager.I.init(iClub.getClubUid());
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}
