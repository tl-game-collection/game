package com.xiuxiu.app.server.db.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.xiuxiu.app.server.statistics.LogAccountRemain;

import java.util.List;

public interface ILogAccountRemainMapper {
    @Insert("INSERT INTO `logAccountRemain`" +
            " (`uid`, `date`, `registerNum`, `day_2`, `day_3`, `day_4`, `day_5`," +
            " `day_6`, `day_7`, `day_14`, `day_30`)" +
            " VALUES(" +
            " #{uid}, #{date}, #{registerNum}, #{day_2}, #{day_3}, #{day_4}, #{day_5}," +
            " #{day_6}, #{day_7}, #{day_14}, #{day_30})")
    void create(LogAccountRemain log);

    @Select("<script>" +
            " SELECT * FROM `logAccountRemain`" +
            " <where>" +
            "  <if test='timeBegin > 0'>AND `date` &gt;= #{timeBegin}</if>" +
            "  <if test='timeEnd > 0'>AND `date` &lt; #{timeEnd}</if>" +
            " </where>" +
            "</script>")
    List<LogAccountRemain> load(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

}
