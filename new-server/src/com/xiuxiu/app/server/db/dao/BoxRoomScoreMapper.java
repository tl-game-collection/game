package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.BoxRoomScore;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BoxRoomScoreMapper {
    @Insert("INSERT INTO `boxRoomScore` (`uid`, `roomUid`, `roomId`, `gameType`, `gameSubType`, `roomType`, `groupUid`, `boxUid`, `beginTime`, `endTime`, `totalScore`, `record`,  `playerUids`) VALUES " +
            "(#{uid}, #{roomUid}, #{roomId}, #{gameType}, #{gameSubType}, #{roomType}, #{groupUid}, #{boxUid}, #{beginTime}, #{endTime}, #{totalScoreDb}, #{recordDb}, #{playerUidsDb})")
    int create(BoxRoomScore boxRoomScore);

    @Select("SELECT * FROM `boxRoomScore` WHERE `uid` = #{uid} LIMIT 1")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    BoxRoomScore loadByUid(@Param("uid") long uid);

    @Select("SELECT * FROM `boxRoomScore` a INNER JOIN boxRoomScorePlayerId b ON a.uid = b.scoreUid WHERE b.playerUid = #{playerUid} AND a.`endTime` >= #{time} ORDER BY a.`uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByPlayerUid(@Param("playerUid") long playerUid, @Param("time") long time, @Param("begin") int begin, @Param("size") int size);

    @Select("SELECT a.* FROM `boxRoomScore` a INNER JOIN boxRoomScorePlayerId b ON a.uid = b.scoreUid WHERE b.playerUid = #{playerUid} AND a.`endTime` >= #{beginTime} AND a.`endTime` < #{endTime} ORDER BY a.`uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByPlayerUidWithTimeRange(@Param("playerUid") long playerUid, @Param("beginTime") long beginTime, @Param("endTime") long endTime, @Param("begin") int begin, @Param("size") int size);

    @Select("SELECT a.* FROM `boxRoomScore` a INNER JOIN boxRoomScorePlayerId b ON a.uid = b.scoreUid WHERE b.`clubUid` = #{clubUid} AND `endTime` >= #{beginTime} ORDER BY a.`uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByGroupUid(@Param("clubUid") long clubUid, @Param("beginTime") long beginTime, @Param("begin") int begin, @Param("size") int size);

    @Select("SELECT a.* FROM `boxRoomScore` a WHERE a.groupUid = #{clubUid} and `endTime` >= #{beginTime} AND `endTime` < #{endTime} ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByGroupUidWithTimeRange(@Param("clubUid") long groupUid, @Param("beginTime") long beginTime, @Param("endTime") long endTime, @Param("begin") int begin, @Param("size") int size);

    @Select("SELECT a.* FROM `boxRoomScore` a WHERE `endTime` >= #{time} AND a.uid in (SELECT b.scoreUid FROM boxRoomScorePlayerId b WHERE b.playerUid = #{playerUid} and b.`clubUid` = #{clubUid}  GROUP BY b.scoreUid) ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByGroupUidAndPlayerUid(@Param("clubUid") long groupUid, @Param("playerUid") long playerUid, @Param("time") long time, @Param("begin") int begin, @Param("size") int size);

    @Select("SELECT a.* FROM `boxRoomScore` a WHERE `endTime` >= #{beginTime} AND `endTime` < #{endTime} AND a.uid in (SELECT b.scoreUid FROM boxRoomScorePlayerId b WHERE b.playerUid = #{playerUid} and b.`clubUid` = #{clubUid}  GROUP BY b.scoreUid) ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByGroupUidAndPlayerUidWithTimeRange(@Param("clubUid") long clubUid, @Param("playerUid") long playerUid, @Param("beginTime") long beginTime, @Param("endTime") long endTime, @Param("begin") int begin, @Param("size") int size);

    @Update("UPDATE `boxRoomScore` SET `groupUid` =  #{groupUid}, `endTime` = #{endTime}, `totalScore` = #{totalScoreDb}, `record` = #{recordDb}, `mark` = #{mark}, `playerUids` = #{playerUidsDb} WHERE `uid` = #{uid}")
    int save(BoxRoomScore boxRoomScore);

    @Select("SELECT a.* FROM `boxRoomScore` a WHERE a.`roomId` = #{roomId} and `endTime` >= #{beginTime} AND `endTime` < #{endTime} AND a.uid in (SELECT b.scoreUid FROM boxRoomScorePlayerId b WHERE b.playerUid = #{playerUid} and b.`clubUid` = #{clubUid}  GROUP BY b.scoreUid) ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByClubUidAndPlayerUidWithTimeRange(@Param("clubUid") long groupUid,@Param("playerUid")long playerUid, @Param("beginTime")long beginTime,
            @Param("endTime")long endTime, @Param("begin")int begin, @Param("size")int pageSize, @Param("roomId")int roomId);

    @Select("SELECT a.* FROM `boxRoomScore` a WHERE a.`roomId` = #{roomId} and `endTime` >= #{time} AND a.uid in (SELECT b.scoreUid FROM boxRoomScorePlayerId b WHERE b.playerUid = #{playerUid} and b.`clubUid` = #{clubUid}  GROUP BY b.scoreUid) ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByClubUidAndPlayerUid(@Param("clubUid")long groupUid, @Param("playerUid")long playerUid, @Param("time")long beginTime, @Param("begin")int begin, @Param("size")int pageSize,
            @Param("roomId")int roomId);
    
    
    @Select("SELECT a.* FROM `boxRoomScore` a WHERE a.`roomId` = #{roomId} and a.`gameType` = #{gameType} and a.`gameSubType` = #{gameSubType} and `endTime` >= #{beginTime} AND `endTime` < #{endTime} AND a.uid in (SELECT b.scoreUid FROM boxRoomScorePlayerId b WHERE b.playerUid = #{playerUid} and b.`clubUid` = #{clubUid}  GROUP BY b.scoreUid) ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByRoomAndGameTypeWithEndTime(@Param("clubUid") long groupUid,@Param("beginTime")long beginTime,
            @Param("endTime")long endTime, @Param("begin")int begin, @Param("size")int pageSize, @Param("roomId")int roomId, @Param("gameType")int gameType, @Param("gameSubType")int gameSubType, @Param("playerUid")long playerUid);
    
    
    @Select("SELECT a.* FROM `boxRoomScore` a WHERE a.`roomId` = #{roomId} and a.`gameType` = #{gameType} and a.`gameSubType` = #{gameSubType} AND a.uid in (SELECT b.scoreUid FROM boxRoomScorePlayerId b WHERE b.playerUid = #{playerUid} and b.`clubUid` = #{clubUid}  GROUP BY b.scoreUid) ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByRoomAndGameType(@Param("clubUid") long groupUid, @Param("beginTime")long beginTime
           , @Param("begin")int begin, @Param("size")int pageSize, @Param("roomId")int roomId, @Param("gameType")int gameType, @Param("gameSubType")int gameSubType, @Param("playerUid")long playerUid);
    
    @Select("SELECT a.* FROM `boxRoomScore` a WHERE a.`gameType` = #{gameType} and a.`gameSubType` = #{gameSubType} and `endTime` >= #{beginTime} AND `endTime` < #{endTime} AND a.uid in (SELECT b.scoreUid FROM boxRoomScorePlayerId b WHERE b.playerUid = #{playerUid} and b.`clubUid` = #{clubUid}  GROUP BY b.scoreUid) ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByGameTypeWithEndTime(@Param("clubUid") long groupUid, @Param("beginTime") long beginTime, @Param("endTime") long endTime, @Param("begin") int begin, @Param("size") int size, @Param("gameType")int gameType, @Param("gameSubType")int gameSubType, @Param("playerUid")long playerUid);

    
    @Select("SELECT a.* FROM `boxRoomScore` a WHERE a.`gameType` = #{gameType} and a.`gameSubType` = #{gameSubType} AND a.uid in (SELECT b.scoreUid FROM boxRoomScorePlayerId b WHERE b.playerUid = #{playerUid} and b.`clubUid` = #{clubUid}  GROUP BY b.scoreUid) ORDER BY a.`uid` DESC  LIMIT #{begin}, #{size} ")
    @Results({
            @Result(property = "totalScoreDb", column = "totalScore"),
            @Result(property = "recordDb", column = "record"),
            @Result(property = "playerUidsDb", column = "playerUids")
    })
    List<BoxRoomScore> loadByGameType(@Param("clubUid") long groupUid, @Param("beginTime") long beginTime, @Param("begin") int begin, @Param("size") int size, @Param("gameType")int gameType, @Param("gameSubType")int gameSubType, @Param("playerUid")long playerUid);

}
