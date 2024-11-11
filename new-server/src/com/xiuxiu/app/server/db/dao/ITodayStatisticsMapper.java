package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.statistics.TodayStatistics;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ITodayStatisticsMapper {
    @Insert("INSERT INTO `todayStatistics` " +
            "(`uid`, `playerUid`, `fromUid`, `statisticsType`, `value`, `updateTime`) VALUES " +
            "(#{uid},   #{playerUid}, #{fromUid},  #{statisticsType},  #{value}, #{updateTime})")
    int create(TodayStatistics value);

    @Update("UPDATE `todayStatistics` SET `value` = #{value}, `updateTime` = #{updateTime}  WHERE `uid` = #{uid}")
    int save(TodayStatistics value);

    @Select("SELECT * FROM todayStatistics  WHERE `fromUid` = #{fromUid} AND `statisticsType` = #{statisticsType} ")
    List<TodayStatistics> loadByFromUid(@Param("fromUid") long fromUid, @Param("statisticsType") int statisticsType);
}
