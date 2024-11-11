package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfPlayerFailInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfPlayerInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqPlayerInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.concurrent.atomic.AtomicInteger;

public class ClubGetPlayerInfoHandle implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqPlayerInfo info = (PCLIClubReqPlayerInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_PLAYER_INFO_FAIL, this.setFailInfo(info.clubUid, info.playerUid, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS));
            return null;
        }
        Player getPlayer = PlayerManager.I.getPlayer(info.playerUid);
        if (getPlayer == null) {
            Logs.CLUB.warn("%s player:%d不存在", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_PLAYER_INFO_FAIL, this.setFailInfo(info.clubUid, info.playerUid, ErrorCode.REQUEST_INVALID_DATA));
            return null;
        }
        if (!club.hasMember(info.playerUid)) {
            Logs.CLUB.warn("%s player:%d玩家不在club中", player, info.playerUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_GET_PLAYER_INFO_FAIL, this.setFailInfo(info.clubUid, info.playerUid, ec));
            return null;
        }
        ClubMember respMember = club.getMember(info.playerUid);
        PCLIClubNtfPlayerInfo respInfo = new PCLIClubNtfPlayerInfo();
        AtomicInteger count= new AtomicInteger();
        club.foreach((member) -> {
            ClubMember tempMember = member[0];
            if(tempMember.getUplinePlayerUid()==info.playerUid){
                count.getAndIncrement();
            }
        });
        respInfo.subordinateCount=count;
        respInfo.playerUid = info.playerUid;
        respInfo.clubUid = info.clubUid;
        respInfo.name = getPlayer.getName();
        respInfo.icon = getPlayer.getIcon();
        respInfo.score = club.getMemberExt(info.playerUid,true).getGold();
        respInfo.jobType = respMember.getJobType();
        //身份标识
        respInfo.jobType2 = 0;//无
        ClubMember myClubMember = club.getMember(player.getUid());
        IClub rootClub = club;
        if (club.getOwnerId() == info.playerUid) {
            respInfo.jobType2 = club.checkIsMainClub() ? 1 : 2;//1总圈圈主 2本圈圈主
        } else if (respMember.checkJobType(EClubJobType.DEPUTY)) {
            respInfo.jobType2 = 3;//副圈主
        } else {
            //合过圈
            if (club.checkIsJoinInMainClub()) {
                if (!club.checkIsMainClub()) {
                    rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
                }
                if (rootClub.getOwnerId() == info.playerUid) {
                    respInfo.jobType2 = 1;//总圈圈主
                } else if (rootClub.getClubInfo().getManagerInfo().containsKey(info.playerUid)) {
                    respInfo.jobType2 = 4;//管理员
                }
            }
        }
        if (respInfo.jobType2 == 0 && (player.getUid() == club.getOwnerId() || myClubMember != null && myClubMember.checkJobType(EClubJobType.DEPUTY))) {
            if (info.playerUid != club.getOwnerId() && !respMember.checkJobType(EClubJobType.DEPUTY)) {
                respInfo.jobType2 = 7;//自己是圈主或副圈主，查看玩家是普通玩家
            }
        }
        if (myClubMember != null && info.playerUid == myClubMember.getUplinePlayerUid()) {
            respInfo.jobType3 = 5;//上级
        } else if (respMember.getUplinePlayerUid() == player.getUid()) {
            respInfo.jobType3 = 6;//下级
        }
        ClubActivityManager.I.getAndSetDivide(info.clubUid,info.playerUid);
        ClubActivityManager.I.getAndSetArenaDivideLine(info.clubUid,info.playerUid);
        respInfo.divide = respMember.getDivide();
        respInfo.divideLine = respMember.getDivideLine();
        respInfo.bankCard = getPlayer.getBankCard();
        respInfo.bankCardHolder = getPlayer.getBankCardHolder();
        respInfo.setGoldUpLine = respMember.getOnlyUpLineSetGold() == 1;
        respInfo.isOnline = getPlayer.isOnline();
        respInfo.isUpGoldTreasuer = rootClub.checkIsUpTreasurer(getPlayer.getUid());
        if (getPlayer.getVisitCardTo().list != null) {
            for (int i = 0; i < getPlayer.getVisitCardTo().list.size(); i++) {
                respInfo.showImage.add(getPlayer.getVisitCardTo().list.get(i).imgPath);
            }
        }

        player.send(CommandId.CLI_NTF_CLUB_GET_PLAYER_INFO_OK,respInfo);
        return null;
    }

    private PCLIClubNtfPlayerFailInfo setFailInfo(long clubUid, long playerUid, ErrorCode code) {
        PCLIClubNtfPlayerFailInfo failInfo = new PCLIClubNtfPlayerFailInfo();
        failInfo.clubUid = clubUid;
        failInfo.playerUid = playerUid;
        failInfo.errorCode = code;
        return failInfo;
    }
}
