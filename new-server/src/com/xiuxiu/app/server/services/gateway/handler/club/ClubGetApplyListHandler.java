package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubMergeNtfApplyInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfApplyInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfClubGetApplyListInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqClubGetApplyList;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ApplyInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EApplyType;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.club.constant.EOpStateType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.Iterator;

/**
 *
 */
public class ClubGetApplyListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqClubGetApplyList info = (PCLIClubReqClubGetApplyList) request;
        IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == iClub) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_APPLY_LIST_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!iClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
            ErrorCode ec = iClub.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_GET_APPLY_LIST_FAIL, ec);
            return null;
        }
        if (!iClub.matchMemberType(EClubJobType.CHIEF, player.getUid()) && !iClub.matchMemberType(EClubJobType.DEPUTY, player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 没有权限", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_APPLY_LIST_FAIL, ErrorCode.FLOOR_NOT_PRIVILEGE);
            return null;
        }
        if (EApplyType.CLUB_MERGE.ordinal() == info.applyType && !iClub.matchMemberType(EClubJobType.CHIEF, player.getUid())){
            Logs.CLUB.warn("%s clubUid:%d 俱乐部合并申请类型 不是群猪没有权限", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_APPLY_LIST_FAIL, ErrorCode.FLOOR_NOT_PRIVILEGE);
            return null;
        }
        PCLIClubNtfClubGetApplyListInfo listInfo = new PCLIClubNtfClubGetApplyListInfo();
        listInfo.clubUid = info.clubUid;
        listInfo.applyType = info.applyType;
        if (EApplyType.CLUB.ordinal() == info.applyType) {
            Iterator<ApplyInfo> it = iClub.getClubInfo().getApplyInfo().iterator();
            while (it.hasNext()) {
                ApplyInfo temp = it.next();
                Player applyPlayer = PlayerManager.I.getPlayer(temp.getfUid());
                if (null == applyPlayer) {
                    continue;
                }
                if (iClub.getMember(applyPlayer.getUid()) != null && temp.getState() == EOpStateType.NORMAL.ordinal()) {
                    continue;
                }
                PCLIClubNtfApplyInfo applyInfo = new PCLIClubNtfApplyInfo();
                applyInfo.playerUid = applyPlayer.getUid();
                applyInfo.name = applyPlayer.getName();
                applyInfo.icon = applyPlayer.getIcon();
                applyInfo.opTime = temp.getaTime();
                applyInfo.state = temp.getState();
                Player opPlayer = PlayerManager.I.getPlayer(temp.gettUid());
                if (null != opPlayer) {
                    applyInfo.opUid = opPlayer.getUid();
                    applyInfo.opName = opPlayer.getName();
                    ClubMember member = iClub.getMember(temp.gettUid());
                    if (null != member) {
                        applyInfo.memberType = member.getJobType();
                    }
                }
                listInfo.list.add(applyInfo);
            }
        } else if (EApplyType.CLUB_MERGE.ordinal() == info.applyType) {
            long nowTime = System.currentTimeMillis();
            Iterator<ApplyInfo> it = iClub.getClubInfo().getMergeApplyList().iterator();
            while (it.hasNext()) {
                ApplyInfo temp = it.next();
                int state = temp.getState();
                if (EOpStateType.NORMAL.ordinal() == state || EOpStateType.WAIT_OTHER_DEAL.ordinal() == state) {
                    state = ((temp.getaTime() + Constant.CLUB_APPLY_MERGE_INVALID_TIME) > nowTime) ? temp.getState() : EOpStateType.WAIT_TIME_OUT.ordinal();
                    temp.setState(state);
                }
                if (EOpStateType.WAIT_OTHER_DEAL.ordinal() == state) {
                    continue;
                }

                IClub fromClub = null;
                IClub toClub = iClub;
                if (temp.getfUid() != iClub.getClubUid() && temp.gettUid() != iClub.getClubUid()) {
                    continue;
                }
                if (temp.gettUid() != iClub.getClubUid()) {
                    toClub = ClubManager.I.getClubByUid(temp.gettUid());
                    fromClub = iClub;
                } else {
                    fromClub = ClubManager.I.getClubByUid(temp.getfUid());
                }
                if (toClub == null || fromClub == null) {
                    continue;
                }

                listInfo.applyInfos.add(createNtfMergeApplyInfo(fromClub, toClub, temp, EApplyType.CLUB_MERGE));
            }
            it = iClub.getClubInfo().getLeaveApplyList().iterator();
            while (it.hasNext()) {
                ApplyInfo temp = it.next();
                IClub appClub = ClubManager.I.getClubByUid(temp.getfUid());
                if (null == appClub) {
                    continue;
                }
                listInfo.applyInfos.add(createNtfMergeApplyInfo(appClub, iClub, temp, EApplyType.CLUB_LEAVE));
            }
        }
        player.send(CommandId.CLI_NTF_CLUB_GET_APPLY_LIST_OK, listInfo);
        return null;
    }

    private PCLIClubMergeNtfApplyInfo createNtfMergeApplyInfo(IClub fromClub, IClub toClub, ApplyInfo applyInfo, EApplyType applyType) {
        PCLIClubMergeNtfApplyInfo ntfApplyInfo = new PCLIClubMergeNtfApplyInfo();
        ntfApplyInfo.clubUid = fromClub.getClubUid();
        ntfApplyInfo.name = fromClub.getName();
        ntfApplyInfo.opUid = toClub.getClubUid();
        ntfApplyInfo.opName = toClub.getName();
        ntfApplyInfo.state = applyInfo.getState();
        ntfApplyInfo.opTime = applyInfo.getaTime();
        ntfApplyInfo.clubTotalRewardValue = applyInfo.getpOne();
        ntfApplyInfo.clubTotalGold = applyInfo.getpTwo();
        ntfApplyInfo.applyType = applyType.ordinal();
        return ntfApplyInfo;
    }
}
