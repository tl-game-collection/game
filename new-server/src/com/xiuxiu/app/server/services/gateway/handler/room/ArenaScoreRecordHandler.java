package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIArenaNtfReportInfo;
import com.xiuxiu.app.protocol.client.room.PCLIArenaReqReportInfo;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.IDBLoad;
import com.xiuxiu.app.server.db.dao.IBaseDAO;
import com.xiuxiu.app.server.db.dao.IBoxArenaScoreInfoDao;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.player.helper.ArenaRoomPlayerHelper;
import com.xiuxiu.app.server.score.BoxArenaScore;
import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.NumberUtils;

import java.util.HashMap;
import java.util.List;

public class ArenaScoreRecordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIArenaReqReportInfo info = (PCLIArenaReqReportInfo) request;
        if (!RoomManager.I.lock(player.getUid())) {
            Logs.ROOM.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_ARENA_REPORT_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            PCLIArenaNtfReportInfo reportInfo = new PCLIArenaNtfReportInfo();

            IRoom room = RoomManager.I.getRoom(player.getRoomId());
            if (null != room && null != room.getRoomScore()) {
                IRoomPlayer[] allPlayer = room.getAllPlayer();
                for (int i = 0; i < allPlayer.length; i++) {
                    IRoomPlayer temp = allPlayer[i];
                    if (null == temp) {
                        continue;
                    }
                    if (temp.getUid() == player.getUid()) {
                        ArenaRoomPlayerHelper roomPlayerHelper = (ArenaRoomPlayerHelper)temp.getRoomPlayerHelper();
                        BoxArenaScore boxArenaScore = roomPlayerHelper.getArenaScore();
                        reportInfo.allCnt = new HashMap<>();
                        if (boxArenaScore != null) {
                            reportInfo.bureau = boxArenaScore.getBureau();
                            reportInfo.score = NumberUtils.get2Decimals(boxArenaScore.getScore());
                            reportInfo.reportUid = boxArenaScore.getUid();
                            reportInfo.allCnt.putAll(boxArenaScore.getAllCnt());
                            int to = boxArenaScore.getRecordUid().size();
                            int from = boxArenaScore.getRecordUid().size() > 50 ? boxArenaScore.getRecordUid().size() - 50 : 0;

                            List<Long> recordUids = boxArenaScore.getRecordUid().subList(from, to);
                            List<BoxArenaScoreInfo> recordList = DBManager.I.loadBatch(ETableType.TB_BOX_ARENA_SCORE_INFO, new IDBLoad<BoxArenaScoreInfo>() {
                                @Override
                                public String getRedisKey() {
                                    return ETableType.TB_BOX_ARENA_SCORE_INFO.getRedisKey();
                                }

                                @Override
                                public List<BoxArenaScoreInfo> load(IBaseDAO<BoxArenaScoreInfo> dao) {
                                    return ((IBoxArenaScoreInfoDao) dao).loadAll(recordUids);
                                }
                            });
                            for (BoxArenaScoreInfo boxArenaScoreInfo : recordList) {
                                reportInfo.list.add(boxArenaScoreInfo.toProtocolScoreInfo(ERoomType.BOX));
                            }
                        }
                    }
                }
            }
            reportInfo.boxUid = info.boxUid;

            player.send(CommandId.CLI_NTF_ARENA_REPORT_OK, reportInfo);
            return null;
        } finally {
            RoomManager.I.unlock(player.getUid());
        }
    }
}
