package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLClubNtfChatPlayerList;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqChatPlayerList;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.*;

public class ClubGetChatPlayerListHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqChatPlayerList info = (PCLIClubReqChatPlayerList) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_CHAT_PLAYERLIST_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s club:%d玩家不在此圈中", player, info.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_GET_CHAT_PLAYERLIST_FAIL, ec);
            return null;
        }
        ClubMember clubMember = club.getMember(player.getUid());
        PCLClubNtfChatPlayerList respInfo = new PCLClubNtfChatPlayerList();
        //-----本圈列表-----
        //自己是本圈圈主,可以看到本圈副圈主
        if (club.getOwnerId() == player.getUid()) {
//            for (Long tempPlayerUid : club.getAllMemberUids()) {
//                if (tempPlayerUid == player.getUid()) {
//                    continue;
//                }
//                ClubMember tempMember = club.getMember(tempPlayerUid);
//                if (tempMember == null) {
//                    continue;
//                }
//                if (tempMember.checkJobType(EClubJobType.DEPUTY)) {
//                    this.setSelfClubMember(club,tempMember, 3, tempMember.getUplinePlayerUid() == player.getUid() ? 6 : 0, respInfo);
//                }
//            }
        }
        //自己是本圈副圈主,可以看到本圈圈主和自己的上级
        else if (clubMember.checkJobType(EClubJobType.DEPUTY)) {
            //本圈圈主
            ClubMember tempMember = club.getMember(club.getOwnerId());
            this.setSelfClubMember(club,tempMember, 2, clubMember.getUplinePlayerUid() == tempMember.getPlayerUid() ? 5 : 0, respInfo);
            //上级
            if (clubMember.getUplinePlayerUid() > 0 && clubMember.getUplinePlayerUid() != club.getOwnerId()) {
                tempMember = club.getMember(clubMember.getUplinePlayerUid());
                this.setSelfClubMember(club,tempMember, tempMember.checkJobType(EClubJobType.DEPUTY) ? 3 : 7, 5, respInfo);
            }
        }
        //自己是本圈长老和成员,可以看到本圈圈主、副圈主和自己的上级
        else if ( clubMember.checkJobType(EClubJobType.NORMAL) ||clubMember.checkJobType(EClubJobType.ELDER )) { //clubMember.checkJobType(EClubJobType.NORMAL) ||clubMember.checkJobType(EClubJobType.ELDER)
            //本圈圈主
            ClubMember tempMember = club.getMember(club.getOwnerId());
            if (clubMember.getUplinePlayerUid() > 0 && clubMember.getUplinePlayerUid() == club.getOwnerId()){
                this.setSelfClubMember(club,tempMember, 2, clubMember.getUplinePlayerUid() == tempMember.getPlayerUid() ? 5 : 0, respInfo);
            }
            //本圈副圈主
//            for (Long tempPlayerUid : club.getAllMemberUids()) {
//                ClubMember m_tempMember = club.getMember(tempPlayerUid);
//                if (m_tempMember == null) {
//                    continue;
//                }
//                if (m_tempMember.checkJobType(EClubJobType.DEPUTY)) {
//                    int m_jobType3 = 0;
//                    if (clubMember.getUplinePlayerUid() == m_tempMember.getPlayerUid()) {
//                        m_jobType3 = 5;
//                    } else if (m_tempMember.getUplinePlayerUid() == clubMember.getPlayerUid()) {
//                        m_jobType3 = 6;
//                    }
//                    this.setSelfClubMember(club,m_tempMember, 3, m_jobType3,respInfo);
//                }
//            }
            //上级
            if (clubMember.getUplinePlayerUid() > 0 && clubMember.getUplinePlayerUid() != club.getOwnerId()) {
                tempMember = club.getMember(clubMember.getUplinePlayerUid());
                this.setSelfClubMember(club,tempMember, tempMember.checkJobType(EClubJobType.DEPUTY) ? 3 : 7, 5, respInfo);
//                if (!tempMember.checkJobType(EClubJobType.DEPUTY)) {
//                    this.setSelfClubMember(club,tempMember, 7, 5, respInfo);
//                }
            }
        }
//            ClubMember tempMember = club.getMember(player.getUid());
//            if (clubMember.getUplinePlayerUid() > 0 && clubMember.getUplinePlayerUid() != club.getOwnerId()) {
//                tempMember = club.getMember(clubMember.getUplinePlayerUid());
//                this.setSelfClubMember(club,tempMember, tempMember.checkJobType(EClubJobType.DEPUTY) ? 3 : 7, 5, respInfo);
////                if (!tempMember.checkJobType(EClubJobType.DEPUTY)) {
////                    this.setSelfClubMember(club,tempMember, 7, 5, respInfo);
////                }
//            }
        
        
        
        //合过圈
        if (club.checkIsJoinInMainClub()) {
            IClub rootClub = club;
            if (!club.checkIsMainClub()) {
                rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
            }
            List<Long> allClubUid = new ArrayList<>();
            rootClub.fillDepthChildClubUidList(allClubUid);
            //-----圈主列表-----
            //自己是总圈主
            if (rootClub.getOwnerId() == player.getUid()) {
                respInfo.isShowOwnerList = true;
                for (Long tempClubUid : allClubUid) {
                    IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
                    if (tempClub == null) {
                        continue;
                    }
                    Player tempPlayer = PlayerManager.I.getPlayer(tempClub.getOwnerId());
                    if (tempPlayer == null) {
                        continue;
                    }
                    PCLClubNtfChatPlayerList.playerInfo playerInfo = new PCLClubNtfChatPlayerList.playerInfo();
                    playerInfo.uid = tempClub.getOwnerId();
                    playerInfo.name = tempPlayer.getName();
                    playerInfo.icon = tempPlayer.getIcon();
                    playerInfo.jobType2 = 2;
                    playerInfo.bankCard = tempPlayer.getBankCard();
                    playerInfo.bankCardHolder = tempPlayer.getBankCardHolder();
                    if (tempPlayer.getVisitCardTo().list != null) {
                        for (int i = 0; i < tempPlayer.getVisitCardTo().list.size(); i++) {
                            playerInfo.showImage.add(tempPlayer.getVisitCardTo().list.get(i).imgPath);
                        }
                    }
                    playerInfo.toGroupUid = tempClubUid;
                    playerInfo.score = tempClub.getMemberExt(tempPlayer.getUid(),true).getGold();
                    playerInfo.isOnline = tempPlayer.isOnline();
                    playerInfo.isUpGoldTreasuer = rootClub.checkIsUpTreasurer(tempPlayer.getUid());

                    respInfo.ownerList.add(playerInfo);
                }
            }
            //-----显示大圈主信息-----
            //自己是小圈主
            else {
                for (Long tempClubUid : allClubUid) {
                    IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
                    if (tempClub == null) {
                        continue;
                    }
                    if (tempClub.getOwnerId() == player.getUid()) {
                        Player tempPlayer = PlayerManager.I.getPlayer(rootClub.getOwnerId());
                        if (tempPlayer == null) {
                            continue;
                        }
                        respInfo.mainClubOwnerUid = rootClub.getOwnerId();
                        respInfo.mainClubUid = rootClub.getClubUid();
                        respInfo.mainClubOwnerName = tempPlayer.getName();
                        respInfo.mainClubOwnerIcon = tempPlayer.getIcon();
                        if (tempPlayer.getVisitCardTo().list != null) {
                            for (int i = 0; i < tempPlayer.getVisitCardTo().list.size(); i++) {
                                respInfo.mainClubOwnerShowImage.add(tempPlayer.getVisitCardTo().list.get(i).imgPath);
                            }
                        }
                        respInfo.mainClubOwnerGold = rootClub.getMemberExt(rootClub.getOwnerId(),true).getGold();
                        respInfo.mainClubOwnerIsOnline = tempPlayer.isOnline();
                        respInfo.mainClubOwnerBankCard = tempPlayer.getBankCard();
                        respInfo.mainClubOwnerBankCardHolder = tempPlayer.getBankCardHolder();
                        break;
                    }
                }
            }
        }
        player.send(CommandId.CLI_NTF_CLUB_GET_CHAT_PLAYERLIST_OK, respInfo);
        return null;
    }


    private void setSelfClubMember(IClub _club, ClubMember _clubMember, int _jobType2, int _jobType3, PCLClubNtfChatPlayerList _respInfo) {
        Player player = PlayerManager.I.getPlayer(_clubMember.getPlayerUid());
        if (player == null) {
            return;
        }
        PCLClubNtfChatPlayerList.playerInfo playerInfo = new PCLClubNtfChatPlayerList.playerInfo();
        playerInfo.uid = _clubMember.getPlayerUid();
        playerInfo.name = player.getName();
        playerInfo.icon = player.getIcon();
        playerInfo.jobType2 = _jobType2;
        playerInfo.jobType3 = _jobType3;
        playerInfo.bankCard = player.getBankCard();
        playerInfo.bankCardHolder = player.getBankCardHolder();
        if (player.getVisitCardTo().list != null) {
            for (int i = 0; i < player.getVisitCardTo().list.size(); i++) {
                playerInfo.showImage.add(player.getVisitCardTo().list.get(i).imgPath);
            }
        }
        playerInfo.score = _club.getMemberExt(player.getUid(),true).getGold();
        playerInfo.setGoldUpLine = _club.getMember(player.getUid()).getOnlyUpLineSetGold() == 1;
        playerInfo.isOnline = player.isOnline();
        playerInfo.isUpGoldTreasuer = _club.checkIsUpTreasurer(player.getUid());

        _respInfo.selfClubList.add(playerInfo);
    }
}
