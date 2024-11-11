package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfApplyMerge;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqApplyMerge;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ApplyInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.club.constant.EOpStateType;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ClubApplyMergeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqApplyMerge info = (PCLIClubReqApplyMerge) request;

        //checkSomething
        if (info.fromClubUid == info.toClubUid || info.fromClubUid <= 0 || info.toClubUid <= 0){
            Logs.CLUB.warn("%s 参数错误", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        IClub fromClub = ClubManager.I.getClubByUid(info.fromClubUid);
        if (null == fromClub){
            Logs.CLUB.warn("%s 亲友圈不存在", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        IClub toClub = ClubManager.I.getClubByUid(info.toClubUid);
        if (null == toClub){
            Logs.CLUB.warn("%s 亲友圈不存在", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        if (!fromClub.matchMemberType(EClubJobType.CHIEF,player.getUid())){
            Logs.CLUB.warn("%s 没有权限", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }

        if (fromClub.checkIsJoinInMainClub() && !fromClub.checkIsMainClub()){
            Logs.CLUB.warn("%s 不是主圈不能发起合并申请", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_NOT_MAIN_NO_MERGER);
            return null;
        }

        if ( fromClub.getClubType() != toClub.getClubType()){
            Logs.CLUB.debug("%s 参数错误", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_NOT_EXISTS);
            return null;
        }

        if (!fromClub.matchCloseStatus(EClubCloseStatus.OPEN)){
            Logs.CLUB.warn("%s 亲友圈打样中", player);
            if(fromClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_GOLD_IS_CLOSE);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_CARD_IS_CLOSE);
            }
            return null;
        }

        //deal
        PCLIClubNtfApplyMerge result = new PCLIClubNtfApplyMerge();
        if (toClub.checkIsJoinInMainClub() && !toClub.checkIsMainClub()){
            long mainClubUid = toClub.getFinalClubId();
            if (mainClubUid == fromClub.getClubUid()){
                Logs.CLUB.debug("%s 该圈已合并至自己圈中", player);
                player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_HAVE_MERGE_SELF);
                return null;
            }
            result.fromClubUid = info.fromClubUid;
            result.toClubUid = mainClubUid;
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OK,result);
            return null;
        }

        result.fromClubUid = info.fromClubUid;
        result.toClubUid = 0;
        long nowTime = System.currentTimeMillis();
        ApplyInfo oldAppInfo = toClub.getClubInfo().getApplyMergeInfo(fromClub.getClubUid(),nowTime, EOpStateType.NORMAL.ordinal());
        if (null != oldAppInfo) {
            Logs.CLUB.debug("%s 您有合并请求尚未处理，请处理后再申请", player);
            player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_APPLY_MERGE_AGAIN);
            return null;
        }

        //锁定发起方
        long lockTime = System.currentTimeMillis() + Constant.CLUB_APPLY_MERGE_LOCK_TIME;
        if (lockTime > fromClub.getClubInfo().getLockTime()) {
            if (!fromClub.setLockClub(lockTime, toClub.getClubUid())) {
                Logs.CLUB.warn("%s setLockClub fail", player);
                player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_FAIL, ErrorCode.CLUB_APPLY_FAIL);
                return null;
            }
        }
        toClub.applyMerge(fromClub, nowTime);
        //申请合并的亲友圈以及所有下级圈下分订单自动拒绝
        UpDownGoldTreasurerManager.I.clearClubAndChildAllOrder(fromClub.getClubUid());

        result.fromClubUid = info.fromClubUid;
        result.toClubUid = 0;
        player.send(CommandId.CLI_NTF_CLUB_APPLY_MERGE_OK,result);
        return null;
    }
}
