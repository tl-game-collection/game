package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.RoomScore;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RoomScoreMapper {
    @Insert("INSERT INTO `roomScore` (`uid`, `roomUid`, `roomId`, `gameType`, `gameSubType`, `groupUid`, `playerUid1`, `playerUid2`, " +
            "`playerUid3`, `playerUid4`, `playerUid5`, `playerUid6`, `playerUid7`, `playerUid8`, `playerUid9`, `playerUid10`, `playerUid11`, `beginTime`, `endTime`, `totalScore`, `record`) " +
            "VALUES " +
            "(#{uid}, #{roomUid}, #{roomId}, #{gameType}, #{gameSubType}, #{groupUid}, #{playerUid1}, #{playerUid2}, " +
            "#{playerUid3}, #{playerUid4}, #{playerUid5}, #{playerUid6}, #{playerUid7}, #{playerUid8}, #{playerUid9}, #{playerUid10}, #{playerUid11}, #{beginTime}, #{endTime}, #{totalScoreDb}, #{recordDb})")
    int create(RoomScore roomScore);

    @Select("SELECT * FROM `roomScore` WHERE (`playerUid1` = #{playerUid} OR `playerUid2` = #{playerUid} OR `playerUid3` = #{playerUid} OR `playerUid4` = #{playerUid} OR `playerUid5` = #{playerUid} OR `playerUid6` = #{playerUid} OR `playerUid7` = #{playerUid} OR `playerUid8` = #{playerUid} OR `playerUid9` = #{playerUid} OR `playerUid10` = #{playerUid} OR `playerUid11` = #{playerUid}) AND `endTime` >= #{time} and `gameType` = #{gameType} and `gameSubType` = #{gameSubType} ORDER BY `uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record")
    })
    List<RoomScore> loadByPlayerUid(@Param("playerUid") long playerUid, @Param("time") long time, @Param("begin") int begin, @Param("size") int size,@Param("gameType")int gameType, @Param("gameSubType")int gameSubType);

    @Select("SELECT * FROM `roomScore` WHERE (`playerUid1` = #{playerUid} OR `playerUid2` = #{playerUid} OR `playerUid3` = #{playerUid} OR `playerUid4` = #{playerUid} OR `playerUid5` = #{playerUid} OR `playerUid6` = #{playerUid} OR `playerUid7` = #{playerUid} OR `playerUid8` = #{playerUid} OR `playerUid9` = #{playerUid} OR `playerUid10` = #{playerUid} OR `playerUid11` = #{playerUid}) AND `endTime` >= #{beginTime} AND `endTime` < #{endTime} and `gameType` = #{gameType} and `gameSubType` = #{gameSubType} ORDER BY `uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record")
    })
    List<RoomScore> loadByPlayerUidWithTimeRange(@Param("playerUid") long playerUid, @Param("beginTime") long beginTime, @Param("endTime") long endTime, @Param("begin") int begin, @Param("size") int size,@Param("gameType")int gameType, @Param("gameSubType")int gameSubType);

    @Select("SELECT * FROM `roomScore` WHERE `groupUid` = #{groupUid} AND (`playerUid1` = #{playerUid} OR `playerUid2` = #{playerUid} OR `playerUid3` = #{playerUid} OR `playerUid4` = #{playerUid} OR `playerUid5` = #{playerUid} OR `playerUid6` = #{playerUid} OR `playerUid7` = #{playerUid} OR `playerUid8` = #{playerUid} OR `playerUid9` = #{playerUid} OR `playerUid10` = #{playerUid} OR `playerUid11` = #{playerUid}) AND `endTime` >= #{time} and `gameType` = #{gameType} and `gameSubType` = #{gameSubType} ORDER BY `uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record")
    })
    List<RoomScore> loadByGroupUidWithPlayerUid(@Param("groupUid") long groupUid, @Param("playerUid") long playerUid, @Param("time") long time, @Param("begin") int begin, @Param("size") int size,@Param("gameType")int gameType, @Param("gameSubType")int gameSubType);

    @Select("SELECT * FROM `roomScore` WHERE `groupUid` = #{groupUid} AND (`playerUid1` = #{playerUid} OR `playerUid2` = #{playerUid} OR `playerUid3` = #{playerUid} OR `playerUid4` = #{playerUid} OR `playerUid5` = #{playerUid} OR `playerUid6` = #{playerUid} OR `playerUid7` = #{playerUid} OR `playerUid8` = #{playerUid} OR `playerUid9` = #{playerUid} OR `playerUid10` = #{playerUid} OR `playerUid11` = #{playerUid}) AND `endTime` >= #{beginTime} AND `endTime` < #{endTime} and `gameType` = #{gameType} and `gameSubType` = #{gameSubType}  ORDER BY `uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record")
    })
    List<RoomScore> loadByGroupUidWithPlayerUidAndTimeRange(@Param("groupUid") long groupUid, @Param("playerUid") long playerUid, @Param("beginTime") long beginTime, @Param("endTime") long endTime, @Param("begin") int begin, @Param("size") int size,@Param("gameType")int gameType, @Param("gameSubType")int gameSubType);

    @Update("UPDATE `roomScore` SET `endTime` = #{endTime}, `totalScore` = #{totalScoreDb}, `record` = #{recordDb} WHERE `uid` = #{uid}")
    int save(RoomScore roomScore);
}
