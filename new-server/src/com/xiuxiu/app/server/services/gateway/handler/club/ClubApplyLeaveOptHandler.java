package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfApplyLeave;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfUpdateMainClubInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqApplyLeaveOpt;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ApplyInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.club.constant.EOpStateType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class ClubApplyLeaveOptHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqApplyLeaveOpt info = (PCLIClubReqApplyLeaveOpt) request;

        //checkSomething
        if (info.leaveClubUid <= 0) {
            Logs.CLUB.warn("%s 参数错误", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        IClub leaveClub = ClubManager.I.getClubByUid(info.leaveClubUid);
        if (null == leaveClub) {
            Logs.CLUB.warn("%s 亲友圈不存在", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        long mainClubUid = leaveClub.getFinalClubId();
        IClub toClub = ClubManager.I.getClubByUid(mainClubUid);
        if (null == toClub) {
            if(leaveClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, ErrorCode.CLUB_CARD_NOT_EXISTS);
            }
            Logs.CLUB.warn("%s 亲友圈不存在", player);
            return null;
        }

        if (!toClub.matchMemberType(EClubJobType.CHIEF, player.getUid())) {
            Logs.CLUB.warn("%s 没有权限", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }

        ApplyInfo applyInfo = toClub.getClubInfo().getApplyLeaveInfo(leaveClub.getClubUid(), EOpStateType.NORMAL.ordinal());
        if (null == applyInfo) {
            Logs.CLUB.debug("%s 申请已经过期", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, ErrorCode.CLUB_APPLY_NOT_EXISTS);
            return null;
        }

        //拒绝
        if (info.op == 1) {
            applyInfo.setState(EOpStateType.REJECT.ordinal());
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_OK, null);
            return null;
        }

        //同意逻辑
        if (toClub.matchCloseStatus(EClubCloseStatus.OPEN)){
            Logs.CLUB.warn("%s 亲友圈未打样", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, ErrorCode.CLUB_NOT_CLOSE);
            return null;
        }

        ErrorCode err = toClub.leaveMainClub(leaveClub);
        if (ErrorCode.OK != err) {
            applyInfo.setState(EOpStateType.REJECT.ordinal());
            Logs.CLUB.debug("%s 处理离开请求错误", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_FAIL, err);
            return null;
        }
        applyInfo.setState(EOpStateType.AGREE.ordinal());
        player.send(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_OPT_OK, null);

        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                PCLIClubNtfApplyLeave leaveInfo = new PCLIClubNtfApplyLeave();
                leaveInfo.fromClubUid = leaveClub.getClubUid();
                leaveInfo.toClubUid = toClub.getClubUid();
                toClub.broadcast(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_NOTIFY, leaveInfo);
                leaveClub.broadcast(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_NOTIFY, leaveInfo);

                if (leaveClub.getClubInfo().getChildUid().size() > 0) {
                    PCLIClubNtfUpdateMainClubInfo updateMainClubInfo = new PCLIClubNtfUpdateMainClubInfo();
                    updateMainClubInfo.mainClubInfo = leaveClub.getClubSingleInfoPCL(null);
                    List<Long> leaveChildClubUidList = new ArrayList<>();
                    leaveClub.fillDepthChildClubUidList(leaveChildClubUidList);
                    for (long childClubUid :leaveChildClubUidList){
                        IClub childClub = ClubManager.I.getClubByUid(childClubUid);
                        if (null != childClub) {
                            updateMainClubInfo.clubUid = childClubUid;
                            childClub.broadcast(CommandId.CLI_NTF_CLUB_UP_MAIN_CLUB_INFO, updateMainClubInfo);
                        }
                    }
                }
            }
        });
        return null;
    }
}
