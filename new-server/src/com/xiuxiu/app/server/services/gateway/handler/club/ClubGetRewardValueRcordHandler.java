package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfGetRewardValueRecordInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetRewardValueRecord;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.ClubRewardValueRecord;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

import java.util.List;

/**
 *
 */
public class ClubGetRewardValueRcordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetRewardValueRecord info = (PCLIClubReqGetRewardValueRecord) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_REWARD_VALUE_RCORD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if(!club.hasMember(player.getUid())){
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_GET_REWARD_VALUE_RCORD_FAIL, ec);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_REWARD_VALUE_RCORD_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            PCLIClubNtfGetRewardValueRecordInfo adminInfo = new PCLIClubNtfGetRewardValueRecordInfo();
            adminInfo.clubUid = info.clubUid;
            adminInfo.type = info.type;
            adminInfo.page = info.page;
            if (1 == info.type) {
                if (player.getUid() == club.getOwnerId()) {
                    adminInfo.managementCost = club.getClubInfo().getServiceChargeDivide();
                }
                ClubMemberExt clubMemberExt = club.getMemberExt(player.getUid(), true);
                if (null != clubMemberExt) {
                    adminInfo.awardScore = clubMemberExt.getRewardValue();
                }
                List<ClubRewardValueRecord> records = DBManager.I.getClubRewardValueRecordDAO().loadByClubUid(info.clubUid, player.getUid(),info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
                for (ClubRewardValueRecord rec : records) {
                    PCLIClubNtfGetRewardValueRecordInfo.DivideAdminInfo record = new PCLIClubNtfGetRewardValueRecordInfo.DivideAdminInfo();
                    record.value = rec.getInMoney();
                    record.time = rec.getCreatedAt();
                    adminInfo.adminInfos.add(record);
                }
                adminInfo.next = records.size() >= Constant.PAGE_CNT_10;
            } else if (2 == info.type) {
                List<ClubRewardValueRecord> records = DBManager.I.getClubRewardValueRecordDAO().loadByPage(info.clubUid, player.getUid(), info.time, info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
                for (ClubRewardValueRecord rec : records) {
                    PCLIClubNtfGetRewardValueRecordInfo.DivideAdminInfo record = new PCLIClubNtfGetRewardValueRecordInfo.DivideAdminInfo();
                    record.palyerUid = rec.getOptPlayerUid();
                    Player p = PlayerManager.I.getPlayer(rec.getOptPlayerUid());
                    record.name = null == p ? "" : p.getName();
                    record.icon = null == p ? "" : p.getIcon();
                    record.value = rec.getInMoney();
                    record.time = rec.getCreatedAt();
                    adminInfo.adminInfos.add(record);
                }
                adminInfo.next = records.size() >= Constant.PAGE_CNT_10;
            }
            player.send(CommandId.CLI_NTF_CLUB_GET_REWARD_VALUE_RCORD_OK, adminInfo);
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}