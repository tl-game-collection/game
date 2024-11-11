package com.xiuxiu.app.server.services.gateway.handler.box;

import java.util.Iterator;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfScoreInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqScoreInfo;
import com.xiuxiu.app.protocol.client.room.PCLIArenaNtfCowRecordtInfo;
import com.xiuxiu.app.protocol.client.room.PCLIArenaNtfFgfRecordtInfo;
import com.xiuxiu.app.protocol.client.room.PCLIArenaNtfPaiCowRecordtInfo;
import com.xiuxiu.app.protocol.client.room.PCLIArenaNtfSGRecordInfo;
import com.xiuxiu.app.protocol.client.room.PCLIArenaNtfThirteenRecordtInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.score.ScoreInfo;
import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import com.xiuxiu.app.server.score.BoxRoomScore;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

public class BoxScoreHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqScoreInfo info = (PCLIBoxReqScoreInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 群:%d不存在, 无法获取包厢战绩", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(info.playerUid)) {
            Logs.CLUB.warn("%s playerUid:%d玩家不在圈中", player, info.playerUid);
            player.send(CommandId.CLI_NTF_BOX_CREATE_CUSTOM_ROOM_FAIL, ErrorCode.CLUB_NOT_PLAYER);
            return null;
        }
        try {
            long beginTime;
            long endTime = -1;
            if (0 == info.beforeDay) {
                beginTime = TimeUtil.getZeroTimestampWithToday() - info.beforeDay * TimeUtil.ONE_DAY_MS;
            } else if (1 == info.beforeDay) {
                beginTime = TimeUtil.getZeroTimestampWithToday() - info.beforeDay * TimeUtil.ONE_DAY_MS;
                endTime = TimeUtil.getZeroTimestampWithToday();
            } else if (2 == info.beforeDay) {
                beginTime = TimeUtil.getZeroTimestampWithToday() - info.beforeDay * TimeUtil.ONE_DAY_MS;
                endTime = TimeUtil.getZeroTimestampWithToday() - TimeUtil.ONE_DAY_MS;
            } else {
                beginTime = TimeUtil.getZeroTimestampWithToday() - info.beforeDay * TimeUtil.ONE_DAY_MS;
            }
            if(info.gameType==GameType.GAME_TYPE_COW){
                PCLIArenaNtfCowRecordtInfo recordInfo = new PCLIArenaNtfCowRecordtInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByPlayerUid(player.getUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolCowScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK_1, recordInfo);
                return null;
            }else if(info.gameType== GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER){
                PCLIArenaNtfFgfRecordtInfo recordInfo = new PCLIArenaNtfFgfRecordtInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByPlayerUid(player.getUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolFgfScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK_1, recordInfo);
                return null;
            } else if (info.gameType == GameType.GAME_TYPE_PAIGOW) {
                PCLIArenaNtfPaiCowRecordtInfo recordInfo = new PCLIArenaNtfPaiCowRecordtInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByPlayerUid(player.getUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolPaiCowScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK_1, recordInfo);
                return null;
            }else if(info.gameType== GameType.GAME_TYPE_SG){
                PCLIArenaNtfSGRecordInfo recordInfo = new PCLIArenaNtfSGRecordInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByPlayerUid(player.getUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolSGScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK_1, recordInfo);
                return null;
            }else if(info.gameType== GameType.GAME_TYPE_THIRTEEN){
                PCLIArenaNtfThirteenRecordtInfo recordInfo = new PCLIArenaNtfThirteenRecordtInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByPlayerUid(player.getUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolThirteenScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK_1, recordInfo);
                return null;
            }
            
            List<BoxRoomScore> list = null;
            if (info.roomId > 0) {
                if (-1 == endTime) {
                    list = DBManager.I.getBoxRoomScoreDao().loadByRoomAndGameType(info.clubUid, beginTime, info.size * info.page, info.size, info.roomId, info.gameType, info.gameSubType, info.playerUid);
                } else {
                    list = DBManager.I.getBoxRoomScoreDao().loadByRoomAndGameType(info.clubUid, beginTime, endTime, info.size * info.page, info.size, info.roomId, info.gameType, info.gameSubType, info.playerUid);
                }
            } else {
                if (-1 == endTime) {
                	//战绩列表
                    list = DBManager.I.getBoxRoomScoreDao().loadByGameType(info.clubUid, beginTime, info.size * info.page, info.size, info.gameType, info.gameSubType, info.playerUid);
                } else {
                    list = DBManager.I.getBoxRoomScoreDao().loadByGameType(info.clubUid,  beginTime, endTime, info.size * info.page, info.size, info.gameType, info.gameSubType, info.playerUid);
                }
            }
            
            PCLIBoxNtfScoreInfo scoreInfo = new PCLIBoxNtfScoreInfo();
            scoreInfo.playerUid = info.playerUid;
            scoreInfo.next = list.size() == info.size;
            scoreInfo.page = info.page;

            for (BoxRoomScore score : list) {
                BoxRoomScore modify = BoxManager.I.getScoreByDirty(score.getUid());
                if (null != modify) {
                    score = modify;
                }
                PCLIBoxNtfScoreInfo.BoxRoomScoreRecord roomScoreRecord = new PCLIBoxNtfScoreInfo.BoxRoomScoreRecord();
                roomScoreRecord.uid = score.getUid();
                roomScoreRecord.groupUid = score.getGroupUid();
                roomScoreRecord.boxUid = score.getBoxUid();
                roomScoreRecord.beginTime = score.getBeginTime();
                roomScoreRecord.endTime = score.getEndTime();
                roomScoreRecord.roomUid = score.getRoomUid();
                roomScoreRecord.roomId = score.getRoomId();
                roomScoreRecord.gameType = score.getGameType();
                roomScoreRecord.gameSubType = score.getGameSubType();
                roomScoreRecord.totalScore = score.getTotalScore().toProtocolScoreInfo(ERoomType.BOX);
                //房间类型 1.可少人 2.2人场 3.3人场
                roomScoreRecord.roomType = score.getRoomType();
                if (null != score.getRecord()) {
                    Iterator<ScoreInfo> it2 = score.getRecord().iterator();
                    while (it2.hasNext()) {
                        roomScoreRecord.record.add(it2.next().toProtocolScoreInfo(ERoomType.NORMAL));
                    }
                }
               
                scoreInfo.list.add(roomScoreRecord);
            }
            player.send(CommandId.CLI_NTF_BOX_SCORE_OK, scoreInfo);
            return null;
        } finally {
//            BoxManager.I.unlock(player.getUid());
        }
    }
}
