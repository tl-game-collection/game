package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.rank.RankData;
import com.xiuxiu.app.server.statistics.TodayStatistics;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IRankDataMapper {
    @Insert("INSERT INTO `rankData` " +
            "(`uid`,`fromUid`, `rankType`, `updateTime`, `todayRanks`, `yesterdayRanks`, `anteayerRanks`) VALUES " +
            "(#{uid},   #{fromUid}, #{rankType},  #{updateTime},  #{todayRanks}, #{yesterdayRanks}, #{anteayerRanks})")
    int create(RankData value);

    @Update("UPDATE `rankData` SET `todayRanks` = #{todayRanks}, `yesterdayRanks` = #{yesterdayRanks} , `anteayerRanks` = #{anteayerRanks}, `updateTime` = #{updateTime} "
            + " WHERE `uid` = #{uid}")
    int save(RankData value);
    
    @Select("SELECT * FROM rankData")
    List<RankData> loadAll();
}
