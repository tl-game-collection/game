package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendEveryDayRecord;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecord;

import java.util.List;

/**
 *
 */
public interface IMoneyExpendRecordDao extends IBaseDAO<MoneyExpendRecord> {

    /**
     * 大厅房、亲友圈、联盟 房卡消耗及退还
     *
     * @param startExpendType
     * @param endExpendType
     * @param startTime
     * @param endTime
     * @return
     */
    int loadMoneyExpendRecord(int startExpendType, int endExpendType, long startTime, long endTime);

    /**
     * 大厅房、亲友圈、联盟 房卡消耗及退还
     * @param fromUid
     * @param startExpendType
     * @param endExpendType
     * @param startTime
     * @param endTime
     * @return
     */
    int loadMoneyExpendRecordByFromUid(long fromUid, int startExpendType, int endExpendType, long startTime, long endTime);

    /**
     * 根据用户playerId、消耗类型（大厅、亲友圈、联盟），按天统计当前类型的消耗数量
     *
     * @param beginPag
     * @param endPag
     * @return
     */

    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecord(long playerUid,int roomType,int beginPag, int endPag);

    /**
     * 按天获取消耗数量
     * @param startTime
     * @param endTime
     * @return
     */
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecord2(long startTime, long endTime);

    /**
     *  获取 消耗类型（大厅、亲友圈、联盟）全部 按天统计当前类型的消耗数量
     * @param playerUid
     * @param beginPag
     * @param endPag
     * @return
     */
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByPlayerUid(long playerUid,int beginPag, int endPag);

    /**
     * 按天获取某个亲友圈的消耗数量
     * @param fromUid
     * @param beginPag
     * @param endPag
     * @return
     */
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByClubUid(long fromUid, int beginPag, int endPag);

    /**
     * 按天获取某个亲友圈的消耗数量
     * @param fromUid
     * @param startTime
     * @param endTime
     * @return
     */
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByClubUid2(long fromUid, long startTime, long endTime);

    /**
     * 按游戏类型按天分组获取消耗数量
     * @param gameType
     * @param startTime
     * @param endTime
     * @return
     */
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByGameType(int gameType, long startTime, long endTime);

    /**
     * 按游戏类型按天分组获取某个亲友圈消耗数量
     * @param gameType
     * @param clubUid
     * @param startTime
     * @param endTime
     * @return
     */
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByGameTypeAndClubUid(int gameType, long clubUid, long startTime, long endTime);
}
