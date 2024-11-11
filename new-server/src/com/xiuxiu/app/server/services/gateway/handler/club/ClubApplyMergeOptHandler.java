package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfMergeNewClubInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfUpdateMainClubInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqApplyMergeOpt;
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

public class ClubApplyMergeOptHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqApplyMergeOpt info = (PCLIClubReqApplyMergeOpt) request;

        //checkSomething
        if (info.fromClubUid == info.toClubUid || info.fromClubUid <= 0 || info.toClubUid <= 0) {
            Logs.CLUB.warn("%s 参数错误", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        IClub fromClub = ClubManager.I.getClubByUid(info.fromClubUid);
        if (null == fromClub) {
            Logs.CLUB.warn("%s 亲友圈不存在", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        IClub toClub = ClubManager.I.getClubByUid(info.toClubUid);
        if (null == toClub) {
            Logs.CLUB.warn("%s 亲友圈不存在", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        if (!toClub.matchMemberType(EClubJobType.CHIEF, player.getUid())) {
            Logs.CLUB.warn("%s 没有权限", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }

        long nowTime = System.currentTimeMillis();
        if (toClub.getClubInfo().getLockTime() >= nowTime) {
            Logs.CLUB.warn("%s 锁定状态无法操作", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.CLUB_IN_LOCK);
            return null;
        }

        //拒绝
        if (info.op == 1) {
            ApplyInfo applyInfo = toClub.getClubInfo().getApplyMergeInfo(fromClub.getClubUid(), nowTime, EOpStateType.NORMAL.ordinal());
            if (null != applyInfo) {
                if (fromClub.setLockClub(0, toClub.getClubUid())) {
                    applyInfo.setState(EOpStateType.REJECT.ordinal());
                    this.setFromClubApplyState(fromClub,nowTime,EOpStateType.REJECT);
                }
            }
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_OK, null);
            return null;
        }

        if (!toClub.matchCloseStatus(EClubCloseStatus.OPEN) || !fromClub.matchCloseStatus(EClubCloseStatus.OPEN)){
            Logs.CLUB.warn("%s 亲友圈打样中", player);
            if(toClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.CLUB_GOLD_IS_CLOSE);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.CLUB_CARD_IS_CLOSE);
            }
            return null;
        }

        //同意逻辑
        ApplyInfo applyInfo = toClub.getClubInfo().getApplyMergeInfo(fromClub.getClubUid(), nowTime, EOpStateType.NORMAL.ordinal());
        if (null == applyInfo) {
            Logs.CLUB.debug("%s 申请已经过期", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, ErrorCode.CLUB_APPLY_NOT_EXISTS);
            return null;
        }

        ErrorCode err = toClub.mergeClub(fromClub);
        if (ErrorCode.OK != err) {
            applyInfo.setState(EOpStateType.REJECT.ordinal());
            this.setFromClubApplyState(fromClub,nowTime,EOpStateType.REJECT);
            Logs.CLUB.debug("%s 合并错误", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_FAIL, err);
            return null;
        }
        //被合圈的锁定解除
        fromClub.setLockClub(0, toClub.getClubUid());

        applyInfo.setState(EOpStateType.AGREE.ordinal());
        this.setFromClubApplyState(fromClub,nowTime,EOpStateType.AGREE);
        player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OPT_OK, null);

        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                //广播给申请者的群成员
                PCLIClubNtfUpdateMainClubInfo updateMainClubInfo = new PCLIClubNtfUpdateMainClubInfo();
                updateMainClubInfo.clubUid = fromClub.getClubUid();
                updateMainClubInfo.mainClubInfo = toClub.getClubSingleInfoPCL(null);
                fromClub.broadcast(CommandId.CLI_NTF_CLUB_UP_MAIN_CLUB_INFO, updateMainClubInfo);
                if (fromClub.getClubInfo().getChildUid().size() > 0) {
                    List<Long> fromClubChildClubUidList = new ArrayList<>();
                    fromClub.fillDepthChildClubUidList(fromClubChildClubUidList);
                    for (long childClubUid : fromClubChildClubUidList){
                        IClub childClub = ClubManager.I.getClubByUid(childClubUid);
                        if (null != childClub) {
                            updateMainClubInfo.clubUid = childClubUid;
                            childClub.broadcast(CommandId.CLI_NTF_CLUB_UP_MAIN_CLUB_INFO, updateMainClubInfo);
                        }
                    }
                }

                //广播被申请者的群成员
                PCLIClubNtfMergeNewClubInfo newClubInfo = new PCLIClubNtfMergeNewClubInfo();
                newClubInfo.fromClubUid = toClub.getClubUid();
                newClubInfo.newClubUid = fromClub.getClubUid();
                toClub.broadcast(CommandId.CLI_NTF_CLUB_MERGE_NEW_CLUB_INFO, newClubInfo);
            }
        });
        return null;
    }

    private void setFromClubApplyState(IClub fromClub,long now, EOpStateType applyState){
        ApplyInfo applyInfo = fromClub.getClubInfo().getApplyMergeInfo(fromClub.getClubUid(),now,EOpStateType.WAIT_OTHER_DEAL.ordinal());
        if (null != applyInfo){
            applyInfo.setState(applyState.ordinal());
        }
    }
}
