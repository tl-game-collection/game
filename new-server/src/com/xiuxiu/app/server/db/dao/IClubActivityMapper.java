package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.activity.ClubActivity;

import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IClubActivityMapper {

    @Insert("INSERT INTO `clubActivity` (`uid`, `clubId`, `type`, `info`) VALUES (#{uid}, #{clubId}, #{type}, #{info})")
    int create(ClubActivity clubActivity);

    @Select("SELECT * FROM `clubActivity`")
    List<ClubActivity> loadAll();

    @Update("UPDATE `clubActivity` SET `info` = #{info} WHERE `uid` = #{uid}")
    int update(ClubActivity clubInfo);

}
