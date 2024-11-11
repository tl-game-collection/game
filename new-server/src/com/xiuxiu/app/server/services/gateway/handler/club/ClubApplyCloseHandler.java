package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqApplyClose;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 申请打烊/开放
 * 
 * @author Administrator
 *
 */
public class ClubApplyCloseHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqApplyClose info = (PCLIClubReqApplyClose) request;
        IClub club = ClubManager.I.getClubByUid(info.id);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.id);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 已经在俱乐部中", player, info.id);
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        // 是否有权限申请打烊
        if (!club.matchMemberType(EClubJobType.CHIEF, player.getUid())) {
            Logs.CLUB.warn("%s 没有修改联盟奖励分成获取比例的权限", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_FAIL, ErrorCode.CLUB_NOT_PRIVILEGE_APPLY_CLOSE);
            return null;
        }
        // 判断是否向别的圈发起合并申请(未处理的)
        long nowTime = System.currentTimeMillis();
        ClubInfo clubInfo = club.getClubInfo();
        if (clubInfo.getLockTime() >= nowTime) {
            Logs.CLUB.warn("%s 锁定状态无法操作打烊", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_FAIL, ErrorCode.CLUB_IN_LOCK);
            return null;
        }
        EClubCloseStatus closeStatus = EClubCloseStatus.getType(info.status);
        if (null == closeStatus
                || !(closeStatus.match(EClubCloseStatus.CLOSING) || closeStatus.match(EClubCloseStatus.OPEN))) {
            return null;
        }
        // 是否已经申请打烊/开放
        if (club.matchCloseStatus(closeStatus)) {
            Logs.CLUB.warn("%s 无法操作打烊", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }

        if (!ClubManager.I.lock(player.getUid())) {
            Logs.GROUP.warn("%s clubUid:%d 正在操作", player, info.id);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        try {
            ErrorCode errorCode = club.applyClose(closeStatus);
            if (errorCode == ErrorCode.OK) {
                player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_OK, null);

                //亲友圈打烊时，自动拒绝所有下分订单
                UpDownGoldTreasurerManager.I.clearMainClubAllOrder(club.getClubUid());
            } else {
                player.send(CommandId.CLI_NTF_CLUB_APPLY_CLOSE_FAIL, errorCode);
            }
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
