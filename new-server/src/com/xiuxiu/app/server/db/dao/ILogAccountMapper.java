package com.xiuxiu.app.server.db.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.xiuxiu.app.server.statistics.LogAccount;

import java.util.List;
import java.util.Map;

public interface ILogAccountMapper {
    @Insert("INSERT INTO `logAccount`" +
            " (`uid`, `targetUid`, `action`, `timestamp`, `accountType`, `serverId`, `deviceModel`, `deviceSn`," +
            " `address`, `osVersion`, `appVersion`, `channelId`, `mobileNumber`)" +
            " VALUES(" +
            " #{uid}, #{targetUid}, #{action}, #{timestamp}, #{accountType}, #{serverId}, #{deviceModel}, #{deviceSn}," +
            " #{address}, #{osVersion}, #{appVersion}, #{channelId}, #{mobileNumber})")
    void create(LogAccount log);

    @Insert("<script>" +
            "INSERT INTO `logAccount`" +
            " (`uid`, `targetUid`, `action`, `timestamp`, `accountType`, `serverId`, `deviceModel`, `deviceSn`," +
            " `address`, `osVersion`, `appVersion`, `channelId`, `mobileNumber`)" +
            " VALUES" +
            "<foreach collection=\"list\" item=\"log\" separator=\",\">" +
            "  (#{log.uid}, #{log.targetUid}, #{log.action}, #{log.timestamp}, #{log.accountType}, #{log.serverId}," +
            "  #{log.deviceModel}, #{log.deviceSn}, #{log.address}, #{log.osVersion}, #{log.appVersion}," +
            "  #{log.channelId}, #{log.mobileNumber})" +
            "</foreach>" +
            "</script>")
    void createMultiple(List<LogAccount> logs);

    @Select("<script>" +
            " SELECT * FROM `logAccount`" +
            " <where>" +
            "  <if test='targetUid > 0'>`targetUid`=#{targetUid}</if>" +
            "  <if test='action > 0'>AND `action`=#{action}</if>" +
            "  <if test='timeBegin > 0'>AND `timestamp`&gt;=#{timeBegin}</if>" +
            "  <if test='timeEnd > 0'>AND `timestamp`&lt;=#{timeEnd}</if>" +
            " </where>" +
            " LIMIT #{limitOffset},#{limitCount}" +
            "</script>")
    List<LogAccount> load(@Param("targetUid") long targetUid, @Param("action") int action,
                          @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd,
                          @Param("limitOffset") long limitOffset, @Param("limitCount") int limitCount);

    @Select("<script>" +
            " SELECT COUNT(0) FROM `logAccount`" +
            " <where>" +
            "  <if test='targetUid > 0'>`targetUid`=#{targetUid}</if>" +
            "  <if test='action > 0'>AND `action`=#{action}</if>" +
            "  <if test='timeBegin > 0'>AND `timestamp`&gt;=#{timeBegin}</if>" +
            "  <if test='timeEnd > 0'>AND `timestamp`&lt;=#{timeEnd}</if>" +
            " </where>" +
            "</script>")
    long count(@Param("targetUid") long targetUid, @Param("action") int action,
               @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    @Select("<script>" +
            " SELECT `timestamp` FROM `logAccount`" +
            " <where>" +
            "  <if test='action > 0'>`action`=#{action}</if>" +
            "  <if test='timeBegin > 0'>AND `timestamp`&gt;=#{timeBegin}</if>" +
            "  <if test='timeEnd > 0'>AND `timestamp`&lt;=#{timeEnd}</if>" +
            " </where>" +
            "</script>")
    List<Long> loadTimeByAction(@Param("action") int action, @Param("timeBegin") long timeBegin,
                                @Param("timeEnd") long timeEnd);

    @Select("SELECT * FROM `logAccount` WHERE `action`=#{action} ORDER BY `uid` DESC LIMIT 1")
    LogAccount getLastRecordByAction(@Param("action") int action);

    @Select("SELECT " +
            "COUNT(DISTINCT `targetUid`) AS `times`, " +
            "FROM_UNIXTIME(`timestamp`,'%Y-%m-%d') AS `days` " +
            "FROM `logAccount` " +
            "WHERE `timestamp` BETWEEN #{timeBegin} AND #{timeEnd} " +
            "AND `action` = 2 " +
            "GROUP BY days")
    List<Map<String, Object>> getDailyActive(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    @Select("<script>" +
            "SELECT COUNT(uid) FROM `logAccount` " +
            "WHERE `action` = 2 " +
            "AND `timestamp` &gt;= #{timeBegin} " +
            "AND `timestamp` &lt; #{timeEnd} " +
            "AND `targetUid` IN " +
            "<foreach item = 'item' index = 'index' collection = 'targetUidList' open='(' separator = ',' close = ')'> #{targetUidList} </foreach>" +
            "GROUP BY `targetUid`" +
            "</script>")
    Integer loadLoginByTimeAndTargetUids(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd, @Param("targetUidList") List<Long> targetUidList);

    @Select("SELECT a.targetUid FROM " +
            "(SELECT `targetUid` FROM `logAccount` WHERE `action` = 2 AND `timestamp` >= #{timeBegin} GROUP BY `targetUid`) AS a " +
            "INNER JOIN " +
            "(SELECT `targetUid` FROM `logAccount` WHERE `action` = 1 OR `action` = 2 AND `timestamp` >= #{timeEnd} AND `timestamp` < #{timeBegin} GROUP BY `targetUid`) AS b " +
            "ON a.targetUid = b.targetUid")
    List<Long> loadYesterdayRemain(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    @Select("SELECT COUNT(uid) FROM `logAccount`" +
            "WHERE `action` = 1 AND `timestamp` >= #{timeBegin} and `timestamp` < #{timeEnd}")
    Integer loadRegisterNumByTime(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);
}
