package com.xiuxiu.app.server.services.gateway.handler.room;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfScoreRecordInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReqScoreRecordInfo;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.score.BoxRoomScore;
import com.xiuxiu.app.server.score.ScoreInfo;
import com.xiuxiu.app.server.score.RoomScore;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

public class RoomScoreRecordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIRoomReqScoreRecordInfo info = (PCLIRoomReqScoreRecordInfo) request;
        if (!RoomManager.I.lock(player.getUid())) {
            Logs.ROOM.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_ROOM_SCORE_RECORD_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            List<RoomScore> list = Collections.EMPTY_LIST;
            List<BoxRoomScore> list2 = Collections.EMPTY_LIST;
            int type = 1;
            if (info.curRoom) {
                IRoom room = RoomManager.I.getRoom(player.getRoomId());
                if (null != room && null != room.getRoomScore()) {
                    if (room.getRoomType() == ERoomType.BOX) {
                        type = 2;
                        list2 = Arrays.asList((BoxRoomScore)room.getRoomScore());
                    } else if (room.getRoomType() == ERoomType.NORMAL) {
                        list = Arrays.asList((RoomScore)room.getRoomScore());
                    }
                }
            } else {
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
                if (-1 == endTime) {
                    list =DBManager.I.getRoomScoreDao().loadByPlayerUid(player.getUid(), beginTime, info.page, Constant.PAGE_CNT_10,info.gameType,info.gameSubType);
                } else {
                    list = DBManager.I.getRoomScoreDao().loadByPlayerUid(player.getUid(), beginTime, endTime, info.page, Constant.PAGE_CNT_10,info.gameType,info.gameSubType);
                }
            }

            PCLIRoomNtfScoreRecordInfo scoreRecordInfo = new PCLIRoomNtfScoreRecordInfo();
            scoreRecordInfo.page = info.page;
            if (type == 1) {
                scoreRecordInfo.hasNext = list.size() == Constant.PAGE_CNT_10;
                Iterator<RoomScore> it = list.iterator();
                while (it.hasNext()) {
                    RoomScore roomScore = it.next();
                    PCLIRoomNtfScoreRecordInfo.RoomScoreRecord roomScoreRecord = new PCLIRoomNtfScoreRecordInfo.RoomScoreRecord();
                    roomScoreRecord.uid = roomScore.getUid();
                    roomScoreRecord.beginTime = roomScore.getBeginTime();
                    roomScoreRecord.endTime = roomScore.getEndTime();
                    roomScoreRecord.roomUid = roomScore.getRoomUid();
                    roomScoreRecord.roomId = roomScore.getRoomId();
                    roomScoreRecord.gameType = roomScore.getGameType();
                    roomScoreRecord.gameSubType = roomScore.getGameSubType();
                    roomScoreRecord.totalScore = roomScore.getTotalScore().toProtocolScoreInfo(ERoomType.NORMAL);
                    if (null != roomScore.getRecord()) {
                        Iterator<ScoreInfo> it2 = roomScore.getRecord().iterator();
                        while (it2.hasNext()) {
                            roomScoreRecord.record.add(it2.next().toProtocolScoreInfo(ERoomType.NORMAL));
                        }
                    }
                    scoreRecordInfo.list.add(roomScoreRecord);
                }
            } else if (type == 2) {
                scoreRecordInfo.hasNext = list2.size() == Constant.PAGE_CNT_10;
                Iterator<BoxRoomScore> it = list2.iterator();
                while (it.hasNext()) {
                    BoxRoomScore roomScore = it.next();
                    PCLIRoomNtfScoreRecordInfo.RoomScoreRecord roomScoreRecord = new PCLIRoomNtfScoreRecordInfo.RoomScoreRecord();
                    roomScoreRecord.uid = roomScore.getUid();
                    roomScoreRecord.beginTime = roomScore.getBeginTime();
                    roomScoreRecord.endTime = roomScore.getEndTime();
                    roomScoreRecord.roomUid = roomScore.getRoomUid();
                    roomScoreRecord.roomId = roomScore.getRoomId();
                    roomScoreRecord.gameType = roomScore.getGameType();
                    roomScoreRecord.gameSubType = roomScore.getGameSubType();
                    roomScoreRecord.totalScore = roomScore.getTotalScore().toProtocolScoreInfo(ERoomType.BOX);
                    if (null != roomScore.getRecord()) {
                        Iterator<ScoreInfo> it2 = roomScore.getRecord().iterator();
                        while (it2.hasNext()) {
                            roomScoreRecord.record.add(it2.next().toProtocolScoreInfo(ERoomType.BOX));
                        }
                    }
                    scoreRecordInfo.list.add(roomScoreRecord);
                }
            }
            player.send(CommandId.CLI_NTF_ROOM_SCORE_RECORD_OK, scoreRecordInfo);
            return null;
        } finally {
            RoomManager.I.unlock(player.getUid());
        }
    }
}
