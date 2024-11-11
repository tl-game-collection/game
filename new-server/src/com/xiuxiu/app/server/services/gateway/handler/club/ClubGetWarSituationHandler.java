package com.xiuxiu.app.server.services.gateway.handler.club;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfWarSituationInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqWarSituation;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.IDBLoad;
import com.xiuxiu.app.server.db.dao.BoxRoomScoreDAO;
import com.xiuxiu.app.server.db.dao.IBaseDAO;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.score.ScoreInfo;
import com.xiuxiu.app.server.score.BoxRoomScore;
import com.xiuxiu.app.server.score.ScoreItemInfo;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

/**
 * 获取亲友圈战况
 * @author Administrator
 *
 */
public class ClubGetWarSituationHandler implements Handler {
    
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqWarSituation info = (PCLIClubReqWarSituation) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (!club.matchMemberType(EClubJobType.CHIEF, player.getUid()) && !club.matchMemberType(EClubJobType.DEPUTY, player.getUid())) {
            Logs.GROUP.warn("%s 没有权限查看战况:%s", player, info);
            player.send(CommandId.CLI_NTF_CLUB_GET_WARSITUATION_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }
        long end = System.currentTimeMillis();
        long start = end - TimeUtil.ONE_MONTH_MS;

        List<BoxRoomScore> lists = DBManager.I.loadBatch(ETableType.TB_BOX_SCORE, new IDBLoad<BoxRoomScore>() {
            @Override
            public String getRedisKey() {
                return ETableType.TB_BOX_SCORE.getRedisKey();
            }
            @Override
            public List<BoxRoomScore> load(IBaseDAO<BoxRoomScore> dao) {
                return ((BoxRoomScoreDAO) dao).loadByGroupUid(info.clubUid,-1, start,end,info.page,info.pageSize);
            }
        });

        PCLIClubNtfWarSituationInfo respInfo = new PCLIClubNtfWarSituationInfo();
        respInfo.clubUid = info.clubUid;
        respInfo.page = info.page;
        respInfo.pageSize = info.pageSize;
        for (int i = 0; i < lists.size(); i++) {
            PCLIClubNtfWarSituationInfo.RecordInfo recordInfo = new PCLIClubNtfWarSituationInfo.RecordInfo();
            BoxRoomScore boxRoomScore = lists.get(i);
            recordInfo.uid = boxRoomScore.getUid();
            recordInfo.roomId = boxRoomScore.getRoomId();
            //排序
            List<ScoreItemInfo> tempList = boxRoomScore.getTotalScore().getScore();
            tempList.sort(new Comparator<ScoreItemInfo>() {
                @Override
                public int compare(ScoreItemInfo o1, ScoreItemInfo o2) {
                    return o2.getScore() - o1.getScore();
                }
            });
            if (tempList.size() > 0) {
                String tempPlayerName = null;
                long tempPlayerUid = 0;
                for (ScoreItemInfo temp : tempList) {
                    Player tempPalyer = PlayerManager.I.getPlayer(temp.getPlayerUid());
                    if (tempPalyer.hasClub(boxRoomScore.getGroupUid())) {
                        tempPlayerUid = temp.getPlayerUid();
                        tempPlayerName = tempPalyer.getName();
                        break;
                    }
                }
                recordInfo.playerUid = tempPlayerUid;
                recordInfo.playerName = tempPlayerName;
            }
            recordInfo.clubUid = boxRoomScore.getGroupUid() ;
            recordInfo.score = boxRoomScore.getTotalScore().getScore().get(0).getScore();
            recordInfo.gameType = boxRoomScore.getGameType();
            recordInfo.gameSubType = boxRoomScore.getGameSubType();
            recordInfo.time = boxRoomScore.getBeginTime();
            recordInfo.markstate = boxRoomScore.getMark();

            //第一局都没打完解散，不计入战况
            LinkedList<ScoreInfo> m_records = boxRoomScore.getRecord();
            if (m_records.size() == 1 && m_records.get(0).checkIsDestroy()) {
                ScoreInfo m_record = m_records.get(0);
                boolean btemp = true;
                for (int j = 0; j < m_record.getScore().size(); j++) {
                    int m_score = Float.valueOf(m_record.getScore().get(j).getScore()).intValue();
                    if (m_score != 0) {
                        btemp = false;
                    }
                }
                if (btemp) {
                    continue;
                }
            }

            respInfo.list.add(recordInfo);
        }

        long startTime = TimeUtil.getZeroTimestamp(System.currentTimeMillis());
        //今天消耗
        respInfo.todayUse = Math.abs(DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendRecordByFromUid(info.clubUid,EMoneyExpendType.GROUP_EXPEND.getValue(), EMoneyExpendType.GROUP_EXPEND_RETURN.getValue(), startTime, startTime + TimeUtil.ONE_DAY_MS));
        //昨天消耗
        respInfo.yesterdayUse = Math.abs(DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendRecordByFromUid(info.clubUid,EMoneyExpendType.GROUP_EXPEND.getValue(), EMoneyExpendType.GROUP_EXPEND_RETURN.getValue(), startTime - TimeUtil.ONE_DAY_MS, startTime));

            //今天消耗
//        List<Integer> todayList = DBManager.I.getGroupArenaCostDiamondDailyDao().loadOneBoxCostDailyByGroupUid(TimeUtil.getZeroTimestampWithToday(),end,info.groupUid);
//        for (int i = 0; i < todayList.size(); i++) {
//            respInfo.todayUse += todayList.get(i);
//        }
            //昨天消耗
//        List<Integer> yesterdayList = DBManager.I.getGroupArenaCostDiamondDailyDao().loadOneBoxCostDailyByGroupUid(TimeUtil.getZeroTimestamp(end - TimeUtil.ONE_DAY_MS),end - TimeUtil.ONE_DAY_MS,info.groupUid);
//        for (int i = 0; i < yesterdayList.size(); i++) {
//            respInfo.yesterdayUse += yesterdayList.get(i);
//        }

        player.send(CommandId.CLI_NTF_CLUB_GET_WARSITUATION_OK, respInfo);
        return null;
    }
}
