package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetServiceChargeDivide;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ClubSetServiceChargeDivideHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetServiceChargeDivide info = (PCLIClubReqSetServiceChargeDivide) request;
        //check something
        if (info.divide < 0 || info.divide >= 100) {
            Logs.CLUB.warn("设置抽成比例数值异常 %s", info);
            player.send(CommandId.CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 群不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_FAIL, ErrorCode.GROUP_NOT_EXISTS);
            return null;
        }
        if (!club.matchMemberType(EClubJobType.CHIEF,player.getUid())) {
            Logs.CLUB.warn("%s 只有群主才有权限", player);
            player.send(CommandId.CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            club.getClubInfo().setServiceChargeDivide(info.divide);
            player.send(CommandId.CLI_NTF_CLUB_SERVICE_CHARGE_DIVIDE_OK, info);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
