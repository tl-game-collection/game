package com.xiuxiu.app.server.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.xiuxiu.app.server.room.normal.Hundred.HundredRebRecordInfo;

public interface IHundredRebRecordMapper {
    @Insert("INSERT INTO `hundredRebRecord` (`uid`, `rebPlayerUid`, `time`, `rebInfo`, `bankerCardType`, `rebValue`, `winValue`, `clubUid`, `fanliValue`, `gameType`, `roomUid`, `roomId`) " +
            "VALUES " +
            "(#{uid}, #{rebPlayerUid}, #{time}, #{rebInfoDb}, #{bankerCardType}, #{rebValue}, #{winValue}, #{clubUid}, #{fanliValue}, #{gameType}, #{roomUid}, #{roomId}")
    int create(HundredRebRecordInfo rebRecordInfo);

    @Insert("<script>" +
            "INSERT INTO `hundredRebRecord` (`uid`, `rebPlayerUid`, `time`, `rebInfo`, `bankerCardType`, `rebValue`, `winValue`, `clubUid`, `fanliValue`, `gameType`, `roomUid`, `roomId`) VALUES " +
            "   <foreach item = 'item' index = 'index' collection = 'rebRecordInfoLst' open='' separator = ',' close = ''> " +
            "       (#{item.uid}, #{item.rebPlayerUid}, #{item.time}, #{item.rebInfoDb}, #{item.bankerCardType}, #{item.rebValue}, #{item.winValue}, #{item.clubUid}, #{item.fanliValue}, #{item.gameType}, #{item.roomUid}, #{item.roomId})" +
            "   </foreach>" +
            "</script>")
    int createAll(@Param("rebRecordInfoLst") List<HundredRebRecordInfo> rebRecordInfoLst);

    @Select("SELECT * FROM `hundredRebRecord` WHERE `rebPlayerUid` = #{playerUid} AND `roomId` = #{roomId} ORDER BY `uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "rebInfoDb", column = "rebInfo")
    })
    List<HundredRebRecordInfo> loadByPlayerUid(@Param("playerUid") long playerUid, @Param("roomId") long roomId, @Param("begin") int begin, @Param("size") int size);

    @Select("SELECT * FROM `hundredRebRecord` WHERE `clubUid` = #{clubUid} and `time` >= #{startTime} AND `time` < #{endTime}  ORDER BY `uid` DESC")
    List<HundredRebRecordInfo> loadByClubUid(@Param("clubUid") long clubUid,
                                                   @Param("startTime") long startTime,
                                                   @Param("endTime") long endTime);

}
