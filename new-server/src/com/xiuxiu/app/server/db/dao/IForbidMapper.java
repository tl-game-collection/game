package com.xiuxiu.app.server.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xiuxiu.app.server.forbid.Forbid;

public interface
IForbidMapper {
    @Insert("INSERT INTO `forbid`(`uid`,`clubType`, `clubUid`, `playerUids`, `flag`) VALUES (#{uid}, #{clubType}, #{clubUid}, #{playerUidsDb}, #{flag})")
    int create(Forbid info);

    @Update("UPDATE `forbid` SET `playerUids` = #{playerUidsDb} WHERE uid = #{uid}")
    int save(Forbid info);

    @Select("SELECT * FROM `forbid`")
    List<Forbid> loadAll();

    @Delete("DELETE FROM `forbid` WHERE `uid` = #{uid} ")
    int delByUid(long uid);
}
