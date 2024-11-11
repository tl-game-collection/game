package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqApplyLeave;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ApplyInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.club.constant.EOpStateType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ClubApplyLeaveHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqApplyLeave info = (PCLIClubReqApplyLeave) request;

        //checkSomething
        if (info.leaveClubUid <= 0 ){
            Logs.CLUB.warn("%s 参数错误", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        IClub leaveClub = ClubManager.I.getClubByUid(info.leaveClubUid);
        if (null == leaveClub){
            Logs.CLUB.warn("%s 亲友圈不存在", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        if (!leaveClub.checkIsJoinInMainClub()){
            Logs.CLUB.warn("%s 没有合圈不需要申请离开", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.CLUB_NOT_HAVE_MERGE);
            return null;
        }

        if (!leaveClub.matchMemberType(EClubJobType.CHIEF,player.getUid())){
            Logs.CLUB.warn("%s 没有权限", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }

        if (leaveClub.checkIsMainClub()){
            Logs.CLUB.warn("%s 没有合圈不需要申请离开", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        long toClubUid = leaveClub.getFinalClubId();
        IClub toClub = ClubManager.I.getClubByUid(toClubUid);
        if (null == toClub){
            Logs.CLUB.warn("%s 亲友圈不存在", player);
            if(leaveClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.CLUB_CARD_NOT_EXISTS);
            }
            return null;
        }

        ApplyInfo oldAppInfo = toClub.getClubInfo().getApplyLeaveInfo(leaveClub.getClubUid(), EOpStateType.NORMAL.ordinal());
        if (null != oldAppInfo) {
            Logs.CLUB.debug("%s 您有离开请求尚未处理，请等处理后再申请", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_FAIL, ErrorCode.CLUB_APPLY_LEAVE_AGAIN);
            return null;
        }

        //deal
        toClub.applyLeave(leaveClub,System.currentTimeMillis());
        player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OK,null);
        return null;
    }
}

