package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.floor.Floor;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IFloorMapper {
    @Insert("INSERT INTO `floor` (`uid`, `clubUid`, `floorType`, `name`,`ownerType`,`gameUid`,`layoutType`) " +
            "VALUES " +
            "(#{uid}, #{clubUid}, #{floorType}, #{name},#{ownerType},#{gameUidDb}, #{layoutType})")
    int create(Floor floor);

    @Select("SELECT * FROM `floor`")
    @Results({
            @Result(property = "uid", column = "uid"),
            @Result(property = "clubUid", column = "clubUid"),
            @Result(property = "floorType", column = "floorType"),
            @Result(property = "name", column = "name"),
            @Result(property = "gameUidDb", column = "gameUid"),
            @Result(property = "ownerType", column = "ownerType"),
            @Result(property = "layoutType", column = "layoutType"),
    })
    List<Floor> loadAll();

    @Delete("delete  from floor where uid=#{uid}")
    int deleteFloorByUid(long uid);

    @Update("UPDATE `floor` SET `gameUid` = #{gameUidDb} WHERE `uid` = #{uid}")
    int update(Floor floor);

}
