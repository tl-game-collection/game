package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.box.Box;

import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IBoxMapper {
    @Insert("INSERT INTO `box` (`uid`, `boxName`, `boxType`, `floorUid`, `gameType`, `gameSubType`, `ownerUid`, `rule`, "
            + "`extra`, `ownerType`,`createTime`) " + "VALUES "
            + "(#{uid}, #{boxName}, #{boxType}, #{floorUid}, #{gameType},#{gameSubType}, #{ownerUid}, "
            + "#{ruleDb}, #{extraDb}, #{ownerType},#{createTime})")
    int create(Box box);
    
    @Update("UPDATE `box` SET `boxName` = #{boxName}, `boxType` = #{boxType}, `floorUid` = #{floorUid}, `gameType` = #{gameType}, `gameSubType` = #{gameSubType}, `ownerUid` = #{ownerUid}, `rule` = #{ruleDb},"
            + "`extra` = #{extraDb}, `ownerType` = #{ownerType}, `createTime` = #{createTime} WHERE `uid` = #{uid}")
    int save(Box box);

    @Select("SELECT * FROM `box`")
    @Results({ @Result(property = "boxType", column = "boxType"), @Result(property = "floorUid", column = "floorUid"),
            @Result(property = "gameType", column = "gameType"),
            @Result(property = "gameSubType", column = "gameSubType"),
            @Result(property = "ownerUid", column = "ownerUid"),
            @Result(property = "createTime", column = "createTime"),
            @Result(property = "ownerType", column = "ownerType"), @Result(property = "ruleDb", column = "rule"),
            @Result(property = "extraDb", column = "extra"), @Result(property = "uid", column = "uid"),
            @Result(property = "boxName", column = "boxName") })
    List<Box> loadAll();

    @Delete("DELETE FROM `box` WHERE `uid` = #{uid}")
    int delete(@Param("uid") long uid);

}
