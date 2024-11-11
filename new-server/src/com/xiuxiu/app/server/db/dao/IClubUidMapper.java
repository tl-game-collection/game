package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubUid;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IClubUidMapper {
    @Insert("INSERT INTO `clubUid` " +
            "(`uid`, `good`, `state`) " +
            "VALUES " +
            "(#{uid}, #{good}, #{state})")
    int create(ClubUid clubUid);

    @Update("UPDATE `clubUid` SET `good` = #{good} , `state` = #{state} WHERE `uid` = #{uid}")
    int update(ClubUid clubUid);

    @Select("SELECT * FROM `clubUid` WHERE `state` = 1 AND `good` = #{good} LIMIT #{cnt}")
    List<ClubUid> loadUnused(@Param("good") int good, @Param("cnt") int cnt);
}
