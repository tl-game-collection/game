package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendEveryDayRecord;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 *
 */
public interface IMoneyExpendRecordMapper {
    @Insert("INSERT INTO `moneyExpendRecord` " +
            "(`uid`, `fromUid`, `playerUid`, `operatorUid`, `value`, `expendType`, `expendTime`, `roomType`, `gameType`, `createTime`) VALUES " +
            "(#{uid}, #{fromUid}, #{playerUid}, #{operatorUid}, #{value}, #{expendType}, #{expendTime}, #{roomType}, #{gameType}, #{createTime})")
    int create(MoneyExpendRecord value);

    @Update("UPDATE `moneyExpendRecord` SET `fromUid` = #{fromUid}, `playerUid` = #{playerUid}, `operatorUid` = #{operatorUid}, `value` = #{value}, `expendType` = #{expendType}, `expendTime` = #{expendTime}, `roomType` = #{roomType}, `gameType` = #{gameType}, `createTime` = #{createTime}"
            + " WHERE `uid` = #{uid}")
    int save(MoneyExpendRecord value);

    @Select("SELECT SUM(VALUE) AS sum_value FROM moneyExpendRecord WHERE `expendType` >= #{startExpendType} AND `expendType` <= #{endExpendType} AND `expendTime` >= #{startTime} AND `expendTime` < #{endTime}")
    Integer loadMoneyExpendRecord(@Param("startExpendType") int startExpendType,
                                  @Param("endExpendType") int endExpendType,
                                  @Param("startTime") long startTime,
                                  @Param("endTime") long endTime);

    @Select("SELECT SUM(VALUE) AS sum_value FROM moneyExpendRecord WHERE `fromUid` = #{fromUid} AND `expendType` >= #{startExpendType} AND `expendType` <= #{endExpendType} AND `expendTime` >= #{startTime} AND `expendTime` < #{endTime}")
    Integer loadMoneyExpendRecordByFromUid(@Param("fromUid") long fromUid,
                                           @Param("startExpendType") int startExpendType,
                                           @Param("endExpendType") int endExpendType,
                                           @Param("startTime") long startTime,
                                           @Param("endTime") long endTime);

    @Select("SELECT roomType ,SUM(`value`) as count,FROM_UNIXTIME(expendTime/1000,'%Y-%m-%d') as time FROM moneyExpendRecord  WHERE playerUid = #{playerUid} AND roomType = #{roomType}  GROUP BY roomType,time ORDER BY time,roomType LIMIT #{beginPag}, #{endPag}")
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecord(@Param("playerUid") long playerUid,
                                                                  @Param("roomType") int roomType,
                                                                  @Param("beginPag") int beginPag,
                                                                  @Param("endPag") int endPag);

    @Select("SELECT SUM(`value`) as count,createTime FROM moneyExpendRecord  WHERE `expendTime` >= #{startTime} AND `expendTime` < #{endTime} GROUP BY createTime ORDER BY createTime")
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecord2(@Param("startTime") long startTime,
                                                                  @Param("endTime") long endTime);

    @Select("SELECT SUM(`value`) as count,FROM_UNIXTIME(expendTime/1000,'%Y-%m-%d') as time FROM moneyExpendRecord  WHERE playerUid = #{playerUid} AND roomType = 4 GROUP BY time ORDER BY time LIMIT #{beginPag}, #{endPag}")
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByPlayerUid(@Param("playerUid") long playerUid,
                                                                             @Param("beginPag") int beginPag,
                                                                             @Param("endPag") int endPag);

    @Select("SELECT SUM(`value`) as value,FROM_UNIXTIME(expendTime/1000,'%Y-%m-%d') as time FROM moneyExpendRecord  WHERE fromUid = #{fromUid} GROUP BY time ORDER BY time DESC LIMIT #{beginPag}, #{endPag}")
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByClubUid(@Param("fromUid") long fromUid,
                                                                           @Param("beginPag") int beginPag,
                                                                           @Param("endPag") int endPag);

    @Select("SELECT SUM(`value`) as count,createTime FROM moneyExpendRecord  WHERE fromUid = #{clubUid} AND `expendTime` >= #{startTime} AND `expendTime` < #{endTime} GROUP BY createTime ORDER BY createTime")
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByClubUid2(@Param("clubUid") long clubUid,
                                                                           @Param("startTime") long startTime,
                                                                           @Param("endTime") long endTime);

    @Select("SELECT SUM(`value`) as count,createTime FROM moneyExpendRecord  WHERE gameType = #{gameType} AND `expendTime` >= #{startTime} AND `expendTime` < #{endTime} GROUP BY createTime ORDER BY createTime")
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByGameType(@Param("gameType") int gameType,
                                                                  @Param("startTime") long startTime,
                                                                  @Param("endTime") long endTime);

    @Select("SELECT SUM(`value`) as count,createTime FROM moneyExpendRecord  WHERE gameType = #{gameType} AND fromUid = #{clubUid} AND `expendTime` >= #{startTime} AND `expendTime` < #{endTime} GROUP BY createTime ORDER BY createTime")
    List<MoneyExpendEveryDayRecord> loadMoneyExpendEveryDayRecordByGameTypeAndClubUid(@Param("gameType") int gameType,
                                                                                      @Param("clubUid") long clubUid,
                                                                                      @Param("startTime") long startTime,
                                                                                      @Param("endTime") long endTime);
}
