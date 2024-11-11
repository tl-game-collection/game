package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubRewardValueRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IClubRewardValueRecordMapper {
    @Insert("INSERT INTO `clubRewardValueRecord` " +
            "(`uid`, `playerUid`, `action`, `mount`, `inMoney`,`outMoney`, `beginAmount`, `optPlayerUid`, `mainClubUid`, `clubUid`, `createdAt` ,`optTime`) " +
            "VALUES " +
            "(#{uid}, #{playerUid}, #{action}, #{mount}, #{inMoney}, #{outMoney}, #{beginAmount}, #{optPlayerUid}, #{mainClubUid}, #{clubUid}, #{createdAt}, #{optTime})")
    int create(ClubRewardValueRecord record);

    @Update("UPDATE `clubRewardValueRecord` SET `playerUid` = #{playerUid}, `action` = #{action}, `mount` = #{mount}, `inMoney` = #{inMoney}, `outMoney` = #{outMoney}, `beginAmount` = #{beginAmount}, " +
            "`optPlayerUid` = #{optPlayerUid}, `mainClubUid` = #{mainClubUid}, `clubUid` = #{clubUid}, `createdAt` = #{createdAt}, `optTime` = #{optTime} WHERE `uid` = #{uid}")
    int save(ClubRewardValueRecord record);

    @Select("SELECT * FROM `clubRewardValueRecord` WHERE `uid` = #{uid}")
    ClubRewardValueRecord load(@Param("uid") long uid);


    @Select("SELECT sum(inMoney) as inMoney, `optPlayerUid` FROM `clubRewardValueRecord` WHERE `clubUid` = #{clubUid} AND `playerUid` = #{playerUid} AND `createdAt` = #{time} AND `inMoney` > 0  GROUP BY `optPlayerUid` LIMIT #{begin}, #{pageSize}")
    List<ClubRewardValueRecord> loadByPage(@Param("clubUid") long clubUid,
                                           @Param("playerUid") long playerUid,
                                           @Param("time") long time,
                                           @Param("begin") int begin,
                                           @Param("pageSize") int pageSize);

    @Select("SELECT sum(inMoney) as inMoney , `createdAt` FROM `clubRewardValueRecord` WHERE `clubUid` = #{clubUid} AND `playerUid` = #{playerUid} AND `inMoney` > 0 GROUP BY `createdAt` ORDER BY `createdAt` DESC LIMIT #{begin}, #{pageSize}")
    List<ClubRewardValueRecord> loadByClubUid(@Param("clubUid") long clubUid,@Param("playerUid") long playerUid,@Param("begin") int begin, @Param("pageSize") int pageSize);

    @Select("SELECT sum(inMoney) as inMoney, `clubUid` FROM `clubRewardValueRecord` WHERE `action` in (2, 3, 4) AND `createdAt` = #{createdAt} GROUP BY `clubUid` ")
    List<ClubRewardValueRecord> loadDayDetails(@Param("createdAt") long createdAt);

}
