package com.xiuxiu.app.server.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xiuxiu.app.server.club.ClubGoldRecord;

public interface IClubGoldRecordMapper {
    @Insert("INSERT INTO `clubGoldRecord` " +
            "(`uid`, `playerUid`, `action`, `mount`, `inMoney`, `outMoney`, `beginAmount`, `optPlayerUid`, `mainClubUid`, `clubUid`, `createdAt`, `optTime`) " +
            "VALUES " +
            "(#{uid}, #{playerUid}, #{action}, #{mount}, #{inMoney}, #{outMoney}, #{beginAmount}, #{optPlayerUid}, #{mainClubUid}, #{clubUid}, #{createdAt}, #{optTime})")
    int create(ClubGoldRecord record);

    @Update("UPDATE `clubGoldRecord` SET" +
            " `playerUid` = #{playerUid}," +
            " `action` = #{action}," +
            " `mount` = #{mount}," +
            " `inMoney` = #{inMoney}," +
            " `outMoney` = #{outMoney}," +
            " `beginAmount` = #{beginAmount}," +
            " `optPlayerUid` = #{optPlayerUid}," +
            " `mainClubUid` = #{mainClubUid}," +
            " `clubUid` = #{clubUid}," +
            " `createdAt` = #{createdAt}," +
            " `optTime` = #{optTime}" +
            " WHERE `uid` = #{uid}")
    int save(ClubGoldRecord record);

    @Select("SELECT * FROM `clubGoldRecord` WHERE `uid` = #{uid}")
    ClubGoldRecord load(@Param("uid") long uid);

    @Select("SELECT SUM(inMoney) AS inMoney, createdAt FROM clubGoldRecord WHERE `clubUid` = #{clubUid} AND `action` = #{action} GROUP BY createdAt ORDER BY createdAt LIMIT #{begin}, #{pageSize} ")
    List<ClubGoldRecord> loadClubGoldRecordByClubUid(@Param("clubUid") long clubUid, @Param("action") int action, @Param("begin") int begin, @Param("pageSize") int pageSize);

    @Select(" SELECT playerUid, inMoney ,optTime FROM clubGoldRecord WHERE `clubUid` = #{clubUid} AND `action` = #{action} AND `createdAt` = #{time}  ORDER BY uid LIMIT #{begin}, #{pageSize} ")
    List<ClubGoldRecord> loadClubGoldRecordByClubUidAndTime(@Param("clubUid") long clubUid,
                                                            @Param("action") int action,
                                                            @Param("time") long time,
                                                            @Param("begin") int begin,
                                                            @Param("pageSize") int pageSize);
    @Select("SELECT * FROM `clubGoldRecord`" +
            " WHERE `clubUid` = #{clubUid} AND `playerUid` = #{playerUid} AND `createdAt` >= #{minTime} AND `action` NOT IN (-1,16,26,30)" +
            " ORDER BY `uid` DESC" +
            " LIMIT #{begin}, #{pageSize} ")
    List<ClubGoldRecord> loadSetGoldRecord(@Param("clubUid") long clubUid, @Param("playerUid") long playerUid, @Param("begin") int begin, @Param("pageSize") int pageSize, @Param("minTime") long minTime);

//    @Select(" SELECT SUM(inMoney) AS inMoney FROM clubGoldRecord WHERE `clubUid` = #{clubUid} AND `action` = #{action}")
//    Integer loadClubGoldRecordCounInMoney(@Param("clubUid") long clubUid,@Param("action") int action);
}
