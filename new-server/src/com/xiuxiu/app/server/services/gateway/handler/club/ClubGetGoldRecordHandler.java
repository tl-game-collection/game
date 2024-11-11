package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIPlayerNtfGetClubGoldRecord;
import com.xiuxiu.app.protocol.client.club.PCLIPlayerReqGetClubGoldRecord;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubGoldRecord;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ClubGetGoldRecordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqGetClubGoldRecord info = (PCLIPlayerReqGetClubGoldRecord) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_PLAYER_GET_CLUB_GOLDRECORD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        Player infoPlayer = PlayerManager.I.getPlayer(info.playerUid);
        if (infoPlayer == null) {
            Logs.CLUB.warn("%s 玩家不存在", player);
            player.send(CommandId.CLI_NTF_PLAYER_GET_CLUB_GOLDRECORD_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IClub rootClub = null;
        if (club.checkIsJoinInMainClub()) {
            if (club.checkIsMainClub()){
                rootClub = club;
            }else{
                rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
            }
        }
        //权限判断
        if (player.getUid() != info.playerUid) {
            boolean bCanCheck = false;//是否可以查看
            if (club.hasMember(info.playerUid)) {
                //圈主可以查看自己本圈玩家
                if (club.getOwnerId() == player.getUid()) {
                    bCanCheck = true;
                }
                //副圈主可以查看自己本圈玩家
                ClubMember clubMember = club.getMember(player.getUid());
                if (clubMember != null && clubMember.checkJobType(EClubJobType.DEPUTY)) {
                    bCanCheck = true;
                }
                //上级可以查看所在圈下级玩家
                if (clubMember != null && club.getMember(info.playerUid).getUplinePlayerUid() == player.getUid()) {
                    bCanCheck = true;
                }
            }
            //盟主可以看其他圈主
            if (null != rootClub && rootClub.getOwnerId() == player.getUid()) {
                List<Long> allClubUid = new ArrayList<>();
                rootClub.fillDepthChildClubUidList(allClubUid);
                for (int i = 0; i < allClubUid.size(); i++) {
                    IClub tempClub = ClubManager.I.getClubByUid(allClubUid.get(i));
                    if (tempClub.getOwnerId() == info.playerUid) {
                        bCanCheck = true;
                        break;
                    }
                }
            }
            if (!bCanCheck) {
                Logs.CLUB.warn("%s 没有权限", player);
                player.send(CommandId.CLI_NTF_PLAYER_GET_CLUB_GOLDRECORD_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
        }
        PCLIPlayerNtfGetClubGoldRecord respInfo = new PCLIPlayerNtfGetClubGoldRecord();
        respInfo.page = info.page;
        respInfo.playerUid = info.playerUid;
        respInfo.icon = infoPlayer.getIcon();
        respInfo.name = infoPlayer.getName();
        respInfo.totalScore = club.getMemberExt(info.playerUid,true).getGold();
        //如果是本圈圈主
        if (club.getOwnerId() == player.getUid()) {
            long[] totalValues = club.getTotalGoldAndRewardValueNoChild();
            respInfo.totalReward = totalValues[0];
            respInfo.totalGold = totalValues[1];
            respInfo.upTotalScore = club.getMemberExt(info.playerUid,true).getUpTotalScore();
            respInfo.downTotalScore = club.getMemberExt(info.playerUid,true).getDownTotalScore();
        }
        //如果合过圈
        if (null != rootClub) {
            //如果是总圈圈主
            if (rootClub.getOwnerId() == player.getUid()) {
                long[] totalValuesMainClub = rootClub.getTotalGoldAndRewardValue();
                respInfo.totalRewardMainClub = totalValuesMainClub[0];
                respInfo.totalGoldMainClub = totalValuesMainClub[1];
                respInfo.upTotalScore = club.getMemberExt(info.playerUid,true).getUpTotalScore();
                respInfo.downTotalScore = club.getMemberExt(info.playerUid,true).getDownTotalScore();
            }
        }
        List<ClubGoldRecord> list = DBManager.I.getClubGoldRecordDAO().loadSetGoldRecord(info.clubUid, info.playerUid, info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10,getMinTime());

        for (ClubGoldRecord clubGoldRecord : list) {
//            if (clubGoldRecord.getPlayerUid() != clubGoldRecord.getOptPlayerUid() && clubGoldRecord.getOptPlayerUid() == player.getUid()) {
//                continue;
//            }
            Player tempPlayer = PlayerManager.I.getPlayer(clubGoldRecord.getOptPlayerUid());
//            if (clubGoldRecord.getOptPlayerUid() <= 0 || tempPlayer == null) {
//                continue;
//            }
            PCLIPlayerNtfGetClubGoldRecord.ClubRecord clubRecord = new PCLIPlayerNtfGetClubGoldRecord.ClubRecord();
            clubRecord.uid = clubGoldRecord.getUid();
            clubRecord.playerUid = clubGoldRecord.getPlayerUid();
            clubRecord.amount = clubGoldRecord.getMount();
            clubRecord.action = clubGoldRecord.getAction();
            //clubRecord.month =
            clubRecord.inMoney = clubGoldRecord.getInMoney();
            clubRecord.outMoney = clubGoldRecord.getOutMoney();
            clubRecord.beginAmount = clubGoldRecord.getBeginAmount();
            clubRecord.optPlayer = clubGoldRecord.getOptPlayerUid();
            if (tempPlayer != null) {
                clubRecord.optIcon = tempPlayer.getIcon();
                clubRecord.optName = tempPlayer.getName();
            }
            clubRecord.createdAt = clubGoldRecord.getCreatedAt();
            clubRecord.optTime = clubGoldRecord.getOptTime();

            respInfo.list.add(clubRecord);
        }
        respInfo.next = list.size() == Constant.PAGE_CNT_10;
        player.send(CommandId.CLI_NTF_PLAYER_GET_CLUB_GOLDRECORD_OK,respInfo);

        return null;
    }

    private long getMinTime(){
        return TimeUtil.getZeroTimestampWithToday() - 518400000;
    }
}
