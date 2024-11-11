package com.xiuxiu.app.server.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.xiuxiu.app.server.room.normal.Hundred.HundredBureauRecordInfo;

public interface IHundredBureauRecordMapper {
    @Insert("INSERT INTO `hundredBureauRecord` (`uid`, `time`, `endTime`, `cardInfo`, `bankerPlayerUid`, `bankerWinValue`, `roomUid`, `roomId`) " +
            "VALUES " +
            "(#{uid}, #{time}, #{endTime}, #{cardInfoDb}, #{bankerPlayerUid}, #{bankerWinValue}, #{roomUid}, #{roomId})")
    int create(HundredBureauRecordInfo rebRecordInfo);

    @Select("SELECT * FROM `hundredBureauRecord` WHERE `roomId` = #{roomId} ORDER BY `uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "cardInfoDb", column = "cardInfo")
    })
    List<HundredBureauRecordInfo> loadByRoomId(@Param("roomId") long roomId, @Param("begin") int begin, @Param("size") int size);
    
    
    @Select("SELECT `roomId`,`time`,`bankerWinValue`,`cardInfo` FROM `hundredBureauRecord` WHERE `roomId` = #{roomId} and `bankerPlayerUid`=#{bankerPlayerUid} ORDER BY `uid` DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "cardInfoDb", column = "cardInfo")
    })
    List<HundredBureauRecordInfo> loadBankerByRoomId(@Param("roomId") long roomId,@Param("bankerPlayerUid") long bankerPlayerUid, @Param("begin") int begin, @Param("size") int size);

}
