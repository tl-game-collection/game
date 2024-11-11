package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.system.LocationInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ILocationInfoMapper {
    @Insert("INSERT INTO `locationInfo` (`uid`, `location`, `info`) VALUES " +
            "(#{uid}, #{location}, #{info})")
    int create(LocationInfo info);

    @Update("UPDATE `locationInfo` SET " +
            "`location` = #{location}, " +
            "`info` = #{info} " +
            "WHERE `uid` = #{uid}")
    int save(LocationInfo info);

    @Select("SELECT * FROM `locationInfo` WHERE `uid` = #{uid}")
    LocationInfo load(long uid);

    @Select("SELECT * FROM `locationInfo` " +
            "WHERE `location` = #{location} " +
            "LIMIT 1")
    LocationInfo loadByLocation(String location);
}
