package com.xiuxiu.app.server.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldRewardRecord;

public interface IClubActivityGoldRewardRecordMapper {

    @Insert("INSERT INTO `clubActivityGoldRewardRecord` (`uid`, `clubUid`, `playerUid`, `boxUid`, `gold`, `operatorTime`, `gameType`, `subType`, `bureau`, `startTime`, `endTime`, `period`, `param`) VALUES "
            + "(#{uid}, #{clubUid}, #{playerUid}, #{boxUid}, #{gold}, #{operatorTime}, #{gameType}, #{subType}, #{bureau}, #{startTime}, #{endTime}, #{period}, #{param})")
    int createGroupValueRecord(ClubActivityGoldRewardRecord info);

    @Select(" SELECT SUM(gold) AS gold FROM clubActivityGoldRewardRecord WHERE `clubUid` = #{clubUid}")
    Integer loadCountGold(@Param("clubUid") long clubUid);

    @Select("SELECT SUM(gold) AS gold, `boxUid`, `gameType`, `subType` FROM `clubActivityGoldRewardRecord` WHERE `clubUid` = #{clubUid} GROUP BY `boxUid` DESC LIMIT #{begin}, #{pageSize}")
    List<ClubActivityGoldRewardRecord> loadByClubUid(@Param("clubUid") long clubUid,
                                                     @Param("begin") int begin,
                                                     @Param("pageSize") int pageSize);

    @Select("SELECT SUM(gold) AS gold, `startTime`, `endTime` FROM `clubActivityGoldRewardRecord` WHERE `clubUid` = #{clubUid} AND `boxUid` = #{boxUid}  GROUP BY `startTime` DESC LIMIT #{begin}, #{pageSize}")
    List<ClubActivityGoldRewardRecord> loadByClubUidAndBoxUid(@Param("clubUid") long clubUid,
                                                              @Param("boxUid") long boxUid,
                                                              @Param("begin") int begin,
                                                              @Param("pageSize") int pageSize);

    @Select("SELECT `playerUid`, SUM(gold) AS gold, `bureau` FROM `clubActivityGoldRewardRecord` WHERE `clubUid` = #{clubUid} AND `boxUid` = #{boxUid} AND `startTime` = #{startTime} AND `endTime` = #{endTime}  GROUP BY `playerUid` DESC LIMIT #{begin}, #{pageSize} ")
    List<ClubActivityGoldRewardRecord> loadByClubUidAndBoxUidAndStartTimeAndEndTime(@Param("clubUid") long clubUid,
                                                                                    @Param("boxUid") long boxUid,
                                                                                    @Param("startTime") long startTime,
                                                                                    @Param("endTime") long endTime,
                                                                                    @Param("begin") int begin,
                                                                                    @Param("pageSize") int pageSize);

}
