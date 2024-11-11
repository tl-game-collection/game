package com.xiuxiu.app.server.db.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xiuxiu.app.server.statistics.consume.PlayerMoneyConsumeRecord;

public interface IPlayerMoneyConsumeRecordMapper {
    
    @Insert("INSERT INTO `playerMoneyConsumeRecord` (`uid`, `playerUid`, `value1`, `value2`, `value3`, `monthValue1`, `monthValue2`, `monthValue3`) " +
            "VALUES (#{uid}, #{playerUid}, #{value1}, #{value2}, #{value3}, #{monthValue1Db}, #{monthValue2Db}, #{monthValue3Db})")
    int create(PlayerMoneyConsumeRecord playerMoneyConsumeRecord);

    @Update("UPDATE `playerMoneyConsumeRecord` SET `value1` = #{value1}, `value2` = #{value2}, `value3` = #{value3}, `monthValue1` = #{monthValue1Db}, `monthValue2` = #{monthValue2Db}, `monthValue3` = #{monthValue3Db} WHERE `uid` = #{uid}")
    int save(PlayerMoneyConsumeRecord playerMoneyConsumeRecord);
    
    @Select("SELECT * FROM `playerMoneyConsumeRecord` WHERE `playerUid` = #{playerUid} LIMIT 1")
    @Results({
            @Result(property = "value1", column = "value1"),
            @Result(property = "value2", column = "value2"),
            @Result(property = "value3", column = "value3"),
            @Result(property = "monthValue1Db", column = "monthValue1"),
            @Result(property = "monthValue2Db", column = "monthValue2"),
            @Result(property = "monthValue3Db", column = "monthValue3")
    })
    PlayerMoneyConsumeRecord loadByPlayerUid(@Param("playerUid") long playerUid);
}
