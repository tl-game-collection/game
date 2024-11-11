package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfGetMemberList;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetMemberList;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClubGetMemberListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetMemberList info = (PCLIClubReqGetMemberList) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.GROUP.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_CLUBMEMBER_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (info.pageSize > Constant.PAGE_CNT_MAX) {
            Logs.GROUP.warn("%s 无效请求", info.pageSize);
            player.send(CommandId.CLI_NTF_CLUB_GET_CLUBMEMBER_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        PCLIClubNtfGetMemberList respInfo = new PCLIClubNtfGetMemberList();
        respInfo.clubUid = info.clubUid;
        respInfo.page = info.page;
        respInfo.pageSize = info.pageSize;
        respInfo.allClubPlayerCount = club.getMemberCnt();
        IClub fromClub=null;
        if (club.checkIsJoinInMainClub()) {
            long finalClubId = club.getFinalClubId();
            if (finalClubId != 0) {
                fromClub = ClubManager.I.getClubByUid(finalClubId);
            }
        }else{
            fromClub=club;
        }

//        for (int i = 2; i < 5000; i++) {
//            club.addMember(1,PlayerManager.I.getPlayer(i),EClubJobType.NORMAL);
//        }
//        club.save();

        List<ClubMember> tempClubMemberList = new ArrayList<>();
        //0.自己放第一位
        ClubMember myClubMember = club.getMember(player.getUid());
        if (myClubMember != null) {
            tempClubMemberList.add(myClubMember);
        }
        //是否只看下级
        if (!info.onlyDownLine) {
            //1.群主在线
            Player tempOwner = PlayerManager.I.getOnlinePlayer(club.getOwnerId());
            if (tempOwner != null && club.getOwnerId() != player.getUid()) {
                tempClubMemberList.add(club.getMember(club.getOwnerId()));
            }
            Long tempUpLineUid = -1L;
            Player tempUpLine = null;
            //2.上级在线
            if (myClubMember != null) {
                tempUpLineUid = myClubMember.getUplinePlayerUid();
                tempUpLine = PlayerManager.I.getOnlinePlayer(tempUpLineUid);
                if (tempUpLine != null && tempUpLineUid != club.getOwnerId()) {
                    tempClubMemberList.add(club.getMember(tempUpLineUid));
                }
            }
            List<ClubMember> tempOnLineDeputyState0 = new ArrayList<>();//在线没有禁玩副群主
            List<ClubMember> tempOnLineNormalState0 = new ArrayList<>();//在线没有禁玩普通成员
            List<ClubMember> tempOnLineDeputyState1 = new ArrayList<>();//在线被禁玩副群主
            List<ClubMember> tempOnLineNormalState1 = new ArrayList<>();//在线被禁玩普通成员
            List<ClubMember> tempDownLineDeputyState0 = new ArrayList<>();//离线没有禁玩副群主
            List<ClubMember> tempDownLineNormalState0 = new ArrayList<>();//离线没有禁玩普通成员
            List<ClubMember> tempDownLineDeputyState1 = new ArrayList<>();//离线被禁玩副群主
            List<ClubMember> tempDownLineNormalState1 = new ArrayList<>();//离线被禁玩普通成员
            final long upLineUid = tempUpLineUid;
            club.foreach((member) -> {
                ClubMember tempMember = member[0];
                if (tempMember.getPlayerUid() != club.getOwnerId() && tempMember.getPlayerUid() != player.getUid() && tempMember.getPlayerUid() != upLineUid) {
                    if (PlayerManager.I.isOnline(tempMember.getPlayerUid())) {
                        if (tempMember.checkJobType(EClubJobType.DEPUTY) && tempMember.getState() == 0) {
                            tempOnLineDeputyState0.add(tempMember);
                        } else if ((tempMember.checkJobType(EClubJobType.NORMAL) || tempMember.checkJobType(EClubJobType.ELDER)) && tempMember.getState() == 0) {
                            tempOnLineNormalState0.add(tempMember);
                        } else if (tempMember.checkJobType(EClubJobType.DEPUTY) && tempMember.getState() == 1) {
                            tempOnLineDeputyState1.add(tempMember);
                        } else if ((tempMember.checkJobType(EClubJobType.NORMAL) || tempMember.checkJobType(EClubJobType.ELDER)) && tempMember.getState() == 1) {
                            tempOnLineNormalState1.add(tempMember);
                        }
                    } else {
                        if (tempMember.checkJobType(EClubJobType.DEPUTY) && tempMember.getState() == 0) {
                            tempDownLineDeputyState0.add(tempMember);
                        } else if ((tempMember.checkJobType(EClubJobType.NORMAL) || tempMember.checkJobType(EClubJobType.ELDER)) && tempMember.getState() == 0) {
                            tempDownLineNormalState0.add(tempMember);
                        } else if (tempMember.checkJobType(EClubJobType.DEPUTY) && tempMember.getState() == 1) {
                            tempDownLineDeputyState1.add(tempMember);
                        } else if ((tempMember.checkJobType(EClubJobType.NORMAL) || tempMember.checkJobType(EClubJobType.ELDER)) && tempMember.getState() == 1) {
                            tempDownLineNormalState1.add(tempMember);
                        }
                    }
                }
            });
            //3.在线其他人
            tempClubMemberList.addAll(tempOnLineDeputyState0);
            tempClubMemberList.addAll(tempOnLineNormalState0);
            tempClubMemberList.addAll(tempOnLineDeputyState1);
            tempClubMemberList.addAll(tempOnLineNormalState1);
            //4.群主离线
            if (tempOwner == null && club.getOwnerId() != player.getUid()) {
                tempClubMemberList.add(club.getMember(club.getOwnerId()));
            }
            //5.上级离线
            if (upLineUid != -1 && tempUpLine == null && upLineUid != club.getOwnerId()) {
                tempClubMemberList.add(club.getMember(upLineUid));
            }
            //6.离线其他人
            tempClubMemberList.addAll(tempDownLineDeputyState0);
            tempClubMemberList.addAll(tempDownLineNormalState0);
            tempClubMemberList.addAll(tempDownLineDeputyState1);
            tempClubMemberList.addAll(tempDownLineNormalState1);
        } else {
            List<ClubMember> tempOnLineState0 = new ArrayList<>();//在线没有禁玩下级
            List<ClubMember> tempOnLineState1 = new ArrayList<>();//在线被禁玩下级
            List<ClubMember> tempDownLineState0 = new ArrayList<>();//离线没有禁玩下级
            List<ClubMember> tempDownLineState1 = new ArrayList<>();//离线被禁玩下级
            club.foreach((member) -> {
                ClubMember tempMember = member[0];
                if (tempMember.getPlayerUid() != player.getUid() && tempMember.getUplinePlayerUid() == player.getUid()) {
                    if (PlayerManager.I.isOnline(tempMember.getPlayerUid())) {
                        if (tempMember.getState() == 0) {
                            tempOnLineState0.add(tempMember);
                        } else if (tempMember.getState() == 1) {
                            tempOnLineState1.add(tempMember);
                        }
                    } else {
                        if (tempMember.getState() == 0) {
                            tempDownLineState0.add(tempMember);
                        } else if (tempMember.getState() == 1) {
                            tempDownLineState1.add(tempMember);
                        }
                    }
                }
            });
            tempClubMemberList.addAll(tempOnLineState0);
            tempClubMemberList.addAll(tempOnLineState1);
            tempClubMemberList.addAll(tempDownLineState0);
            tempClubMemberList.addAll(tempDownLineState1);
        }

        if ((null != fromClub  && (fromClub.checkIsManagerInClubByClubUid(player.getUid(),club.getClubUid()) || fromClub.getOwnerId() == player.getUid()))
                || club.matchMemberType(EClubJobType.CHIEF, player.getUid())
                || club.matchMemberType(EClubJobType.DEPUTY, player.getUid())){
            for (int i = info.page * info.pageSize; i < (info.page + 1) * info.pageSize; i++) {
                if (i >= tempClubMemberList.size()) {
                    break;
                }
                ClubMember tempMember = tempClubMemberList.get(i);
                Player tempPlayer = PlayerManager.I.getPlayer(tempMember.getPlayerUid());
                PCLIClubNtfGetMemberList.memberList temp = new PCLIClubNtfGetMemberList.memberList();
                temp.playerUid = tempMember.getPlayerUid();
                temp.icon = tempPlayer.getIcon();
                temp.name = tempPlayer.getName();
                temp.joinTime = tempMember.getJoinTime();
                temp.jobType = tempMember.getJobType();
                temp.privilege = tempMember.getPrivilege();
                temp.showNick = tempMember.getShowNick();
                //查看是否在线
                if (tempPlayer.isOnline()) {
                    temp.offlineTime = 0;
                } else {
                    temp.offlineTime = tempPlayer.getLastLogoutTime();
                }
                temp.uplinePlayerUid = tempMember.getUplinePlayerUid();
                temp.state = tempMember.getState();
                ClubMemberExt clubMemberExt = club.getMemberExt(tempMember.getPlayerUid(),true);
                temp.score = clubMemberExt.getGold();
                temp.code = clubMemberExt.getCode();
                temp.divide = tempMember.getDivide();
                temp.divideLine = tempMember.getDivideLine();
                temp.onlyUpLineSetGold = tempMember.getOnlyUpLineSetGold();
                temp.isUpGoldTreasurer = club.checkIsUpTreasurer(tempMember.getPlayerUid());
                temp.isDownGoldTreasurer = club.checkIsDownTreasurer(tempMember.getPlayerUid());
                respInfo.lists.add(temp);
            }
            respInfo.next = respInfo.lists.size() == info.pageSize;
        } else {
            if (club.hasMember(player.getUid())) {
                ClubMember playerMember = club.getMember(player.getUid());
                int count = 0;
                for (ClubMember tempMember : tempClubMemberList) {
                    if (tempMember.getPlayerUid() != player.getUid()) {
//                        if (tempMember.getPlayerUid() != club.getOwnerId() && tempMember.getUplinePlayerUid() != player.getUid() ){//&& playerMember.getUplinePlayerUid() != tempMember.getPlayerUid()) {
                        if (tempMember.getPlayerUid() != club.getOwnerId() && tempMember.getUplinePlayerUid() != player.getUid() && playerMember.getUplinePlayerUid() != tempMember.getPlayerUid()) {
                            continue;
                        }
                    }
                    count++;
                    if (count <= info.page * info.pageSize) {
                        continue;
                    }
                    if (count > (info.page + 1) * info.pageSize) {
                        continue;
                    }
                    Player tempPlayer = PlayerManager.I.getPlayer(tempMember.getPlayerUid());
                    PCLIClubNtfGetMemberList.memberList temp = new PCLIClubNtfGetMemberList.memberList();
                    temp.playerUid = tempMember.getPlayerUid();
                    temp.icon = tempPlayer.getIcon();
                    temp.name = tempPlayer.getName();
                    temp.joinTime = tempMember.getJoinTime();
                    temp.jobType = tempMember.getJobType();
                    temp.privilege = tempMember.getPrivilege();
                    temp.showNick = tempMember.getShowNick();
                    //查看是否在线
                    if (tempPlayer.isOnline()) {
                        temp.offlineTime = 0;
                    } else {
                        temp.offlineTime = tempPlayer.getLastLogoutTime();
                    }
                    temp.uplinePlayerUid = tempMember.getUplinePlayerUid();
                    temp.state = tempMember.getState();
	                ClubMemberExt clubMemberExt = club.getMemberExt(tempMember.getPlayerUid(),true);
	                temp.score = clubMemberExt.getGold();
	                temp.code = clubMemberExt.getCode();
                    temp.divide = tempMember.getDivide();
                    temp.divideLine = tempMember.getDivideLine();
                    temp.onlyUpLineSetGold = tempMember.getOnlyUpLineSetGold();
                    temp.isUpGoldTreasurer = club.checkIsUpTreasurer(tempMember.getPlayerUid());
                    temp.isDownGoldTreasurer = club.checkIsDownTreasurer(tempMember.getPlayerUid());
                    respInfo.lists.add(temp);
                }
                respInfo.next = respInfo.lists.size() == info.pageSize;
            }
        }

        player.send(CommandId.CLI_NTF_CLUB_GET_CLUBMEMBER_OK, respInfo);
        return null;
    }
}
