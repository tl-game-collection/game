package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqRoomCardConvertGoldInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqRoomCardConvertGoldRecord;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubGoldRecord;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
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
public class ClubRoomCardConvertGoldRecordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqRoomCardConvertGoldRecord record = (PCLIClubReqRoomCardConvertGoldRecord) request;
        IClub club = ClubManager.I.getClubByUid(record.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, record.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, record.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_FAIL, ec);
            return null;
        }
        if (club.getOwnerId() != player.getUid()) {
            Logs.CLUB.warn("%s clubUid:%d 不是圈主没有权限", player, record.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s info:%s 正在操作", player, record);
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_FAIL, ErrorCode.CLUB_IN_LOCK);
            return null;
        }
        try {
            PCLIClubReqRoomCardConvertGoldInfo info = new PCLIClubReqRoomCardConvertGoldInfo();
            info.clubUid = record.clubUid;
            info.page = record.page;
            info.type = record.type;
            info.time = record.time;
            info.count = club.getClubInfo().getdToGoldTotal();
            int action = EClubGoldChangeType.EXCHANGE_CONVERT_VALUE_INC.getValue();
            if (1 == record.type) {
                List<ClubGoldRecord> list = DBManager.I.getClubGoldRecordDAO().loadClubGoldRecordByClubUid(info.clubUid, action, info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
                if (null != list) {
                    for (ClubGoldRecord goldRecord : list) {
                        PCLIClubReqRoomCardConvertGoldInfo.ConversionRecord conversionRecord = new PCLIClubReqRoomCardConvertGoldInfo.ConversionRecord();
                        conversionRecord.time = goldRecord.getCreatedAt();
                        conversionRecord.value = goldRecord.getInMoney();
                        info.record.add(conversionRecord);
                    }
                    info.next = list.size() >= Constant.PAGE_CNT_10;
                }
            } else if (2 == record.type) {
                List<ClubGoldRecord> list = DBManager.I.getClubGoldRecordDAO().loadClubGoldRecordByClubUidAndTime(info.clubUid, action, record.time, info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
                if (null != list) {
                    for (ClubGoldRecord goldRecord : list) {
                        PCLIClubReqRoomCardConvertGoldInfo.ConversionRecord conversionRecord = new PCLIClubReqRoomCardConvertGoldInfo.ConversionRecord();
                        conversionRecord.playerUid = goldRecord.getPlayerUid();
                        Player p = PlayerManager.I.getPlayer(goldRecord.getPlayerUid());
                        conversionRecord.name = null == p ? "" : p.getName();
                        conversionRecord.icon = null == p ? "" : p.getIcon();
                        conversionRecord.time = goldRecord.getOptTime();
                        conversionRecord.value = goldRecord.getInMoney();
                        info.record.add(conversionRecord);
                    }
                    info.next = list.size() >= Constant.PAGE_CNT_10;
                }
            }
            player.send(CommandId.CLI_NTF_CLUB_ROOM_CARD_CONVERT_GOLD_RECORD_OK, info);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
