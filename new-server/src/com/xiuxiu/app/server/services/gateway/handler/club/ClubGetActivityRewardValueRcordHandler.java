package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfGetActivityRewardValueInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetActivityRewardValueRecord;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldRewardRecord;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.List;

/**
 *
 */
public class ClubGetActivityRewardValueRcordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetActivityRewardValueRecord record = (PCLIClubReqGetActivityRewardValueRecord) request;
        IClub club = ClubManager.I.getClubByUid(record.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, record.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (club.getClubType() != EClubType.GOLD) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部类型不匹配", player, record.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_FAIL, ErrorCode.CLUB_TYPE_NOT);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, record.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_FAIL, ec);
            return null;
        }
        if (club.getOwnerId() != player.getUid()) {
            Logs.CLUB.warn("%s clubUid:%d 不是圈主没有权限", player, record.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s info:%s 正在操作", player, record);
            player.send(CommandId.CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_FAIL, ErrorCode.CLUB_IN_LOCK);
            return null;
        }
        try {
            PCLIClubNtfGetActivityRewardValueInfo info = new PCLIClubNtfGetActivityRewardValueInfo();
            info.clubUid = record.clubUid;
            info.page = record.page;
            info.type = record.type;
            info.count =  DBManager.I.getClubActivityGoldRewardRecordDAO().loadCountGold(info.clubUid);
            if (1 == record.type) {
                List<ClubActivityGoldRewardRecord> list = DBManager.I.getClubActivityGoldRewardRecordDAO().loadByClubUid(info.clubUid, info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
                if (null != list) {
                    for (ClubActivityGoldRewardRecord rewardRecord : list) {
                        PCLIClubNtfGetActivityRewardValueInfo.QuestArenaValueInfo quest = new PCLIClubNtfGetActivityRewardValueInfo.QuestArenaValueInfo();
                        quest.boxUid = rewardRecord.getBoxUid();
                        quest.gold = rewardRecord.getGold();
                        quest.gameType = rewardRecord.getGameType();
                        quest.gameSubType = rewardRecord.getSubType();
                        info.data.add(quest);
                    }
                    info.next = list.size() >= Constant.PAGE_CNT_10;
                }
            } else if (2 == info.type) {
                List<ClubActivityGoldRewardRecord> list = DBManager.I.getClubActivityGoldRewardRecordDAO().loadByClubUidAndBoxUid(record.clubUid, record.boxUid, info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
                if (null != list) {
                    for (ClubActivityGoldRewardRecord rewardRecord : list) {
                        PCLIClubNtfGetActivityRewardValueInfo.QuestArenaValueInfo quest = new PCLIClubNtfGetActivityRewardValueInfo.QuestArenaValueInfo();
                        quest.startTime = rewardRecord.getStartTime();
                        quest.endTime = rewardRecord.getEndTime();
                        quest.gold = rewardRecord.getGold();
                        info.data.add(quest);
                    }
                    info.next = list.size() >= Constant.PAGE_CNT_10;
                }
            } else if (3 == info.type) {
                List<ClubActivityGoldRewardRecord> list = DBManager.I.getClubActivityGoldRewardRecordDAO().loadByClubUidAndBoxUidAndStartTimeAndEndTime(record.clubUid, record.boxUid, record.startTime, record.endTime, info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
                if (null != list) {
                    for (ClubActivityGoldRewardRecord rewardRecord : list) {
                        PCLIClubNtfGetActivityRewardValueInfo.QuestArenaValueInfo quest = new PCLIClubNtfGetActivityRewardValueInfo.QuestArenaValueInfo();
                        quest.playerUid = rewardRecord.getPlayerUid();
                        Player p = PlayerManager.I.getPlayer(rewardRecord.getPlayerUid());
                        quest.playerName = null == p ? "" : p.getName();
                        quest.playerIcon = null == p ? "" : p.getIcon();
                        quest.gold = rewardRecord.getGold();
                        quest.bureau = rewardRecord.getBureau();
                        info.data.add(quest);
                    }
                    info.next = list.size() >= Constant.PAGE_CNT_10;
                }
            }
            player.send(CommandId.CLI_NTF_CLUB_GET_ACTIVITY_REWARD_VALUE_RCORD_OK, info);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
