package com.xiuxiu.app.server.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecordDetail;

/**
 *
 */
public interface IMoneyExpendRecordDetailMapper {

    @Insert("INSERT INTO `moneyExpendRecordDetail` (`uid`, `clubType`, `clubUid`, `playerUid`, `roomUid`, `value`, `type`, `time`) VALUES (#{uid}, #{clubType}, #{clubUid}, #{playerUid}, #{roomUid}, #{value}, #{type}, #{time})")
    int create(MoneyExpendRecordDetail value);

    @Insert({
            "<script>INSERT INTO `moneyExpendRecordDetail` (`uid`, `clubType`, `clubUid`, `playerUid`, `roomUid`, `value`, `type`, `time`) VALUES ",
            "<foreach collection='valueList' item='item' index='index' separator=','>",
            "(#{item.uid}, #{item.clubType}, #{item.clubUid}, #{item.playerUid}, #{item.roomUid}, #{item.value}, #{item.type}, #{item.time})",
            "</foreach></script>" })
    int batchInsert(@Param(value = "valueList") List<MoneyExpendRecordDetail> valueList);

    @Update("UPDATE `moneyExpendRecordDetail` SET `clubUid` = #{clubUid}, `playerUid` = #{playerUid}, `roomUid` = #{roomUid}, `value` = #{value}, `type` = #{type}, `time` = #{time} WHERE `uid` = #{uid}")
    int save(MoneyExpendRecordDetail value);

    @Select("SELECT SUM(`value`) AS count FROM `moneyExpendRecordDetail` WHERE `clubUid` = #{clubUid}")
    Float getTotalConsumeByClubUid(@Param("clubUid") long clubUid);
    
    @Select("SELECT count(0) FROM `moneyExpendRecordDetail` WHERE `clubUid` = #{clubUid}")
    Integer countByClubUid(@Param("clubUid") long clubUid);

    @Select("SELECT SUM(`value`) as value,FROM_UNIXTIME(a.time/1000,'%Y-%m-%d') as showTime FROM `moneyExpendRecordDetail` a WHERE a.`clubUid` = #{clubUid} GROUP BY showTime ORDER BY time LIMIT #{beginPag}, #{endPag}")
    List<MoneyExpendRecordDetail> getByClubUid(@Param("clubUid") long clubUid, @Param("beginPag") int beginPag,
            @Param("endPag") int endPag);

    
    @Select("SELECT count(0) FROM `moneyExpendRecordDetail` WHERE `clubUid` = #{clubUid} AND FROM_UNIXTIME(time/1000,'%Y-%m-%d') = #{time}")
    Integer countByClubUidAndTime(@Param("clubUid") long clubUid, @Param("time") String time);
    
    @Select("SELECT * FROM (SELECT SUM(a.`value`) as value,b.name as playerName FROM `moneyExpendRecordDetail` a LEFT JOIN `player` b on b.uid = a.playerUid WHERE `clubUid` = #{clubUid} AND FROM_UNIXTIME(a.time/1000,'%Y-%m-%d') = #{time} GROUP BY a.playerUid ORDER BY a.time) t WHERE t.`value` !=-0 LIMIT #{beginPag}, #{endPag}")
    List<MoneyExpendRecordDetail> getByClubUidAndTime(@Param("clubUid") long clubUid, @Param("time") String time,
            @Param("beginPag") int beginPag, @Param("endPag") int endPag);

}
