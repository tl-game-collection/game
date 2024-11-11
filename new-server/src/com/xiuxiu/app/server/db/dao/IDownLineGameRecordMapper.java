package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.statistics.DownLineGameRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IDownLineGameRecordMapper {
    @Insert("INSERT INTO `down_line_game_record` " +
            "(`player_uid`, `from_uid`, `down_line_uid`, `count`, `score`, `zero_time`, `name`) " +
            "VALUES " +
            "(#{playerUid}, #{fromUid}, #{downLineUid}, #{count}, #{score}, #{zeroTime}, #{name})")
    int create(DownLineGameRecord record);

    @Update("UPDATE `down_line_game_record` SET `count` = #{count} , `score` = #{score} , `name` = #{name} WHERE `player_uid` = #{playerUid} AND `from_uid` = #{fromUid} AND `down_line_uid` = #{downLineUid} AND `zero_time` = #{zeroTime}")
    int update(DownLineGameRecord record);

    @Select("SELECT * FROM `down_line_game_record` WHERE `zero_time` = #{zeroTime} ")
    @Results({@Result(property = "playerUid", column = "player_uid"),
            @Result(property = "fromUid", column = "from_uid"),
            @Result(property = "downLineUid", column = "down_line_uid"),
            @Result(property = "zeroTime", column = "zero_time"),
    })
    List<DownLineGameRecord> loadOneDayRecord(@Param("zeroTime") long zeroTime);
}
