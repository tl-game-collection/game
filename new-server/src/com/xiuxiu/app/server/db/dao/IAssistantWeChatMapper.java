package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.system.AssistantWeChat;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IAssistantWeChatMapper {
    @Insert("INSERT INTO `assistantWeChat` (`uid`, `weChat`, `province`, `city`, `district`, `adCode`) VALUES " +
            "(#{uid}, #{weChat}, #{province}, #{city}, #{district}, #{adCode})")
    int create(AssistantWeChat info);

    @Update("UPDATE `assistantWeChat` SET " +
            "`weChat` = #{weChat}, " +
            "`province` = #{province}, " +
            "`city` = #{city}, " +
            "`district` = #{district}, " +
            "`adCode` = #{adCode} " +
            "WHERE `uid` = #{uid}")
    int save(AssistantWeChat info);

    @Select("SELECT * FROM `assistantWeChat` WHERE `uid` = #{uid}")
    AssistantWeChat load(long uid);

    @Select("SELECT * FROM `assistantWeChat` " +
            "WHERE `province` = #{province} " +
            "AND `city` = '' " +
            "AND `district` = '' LIMIT 1")
    AssistantWeChat loadByProvince(String province);

    @Select("SELECT * FROM `assistantWeChat` " +
            "WHERE `city` = #{city} " +
            "AND `district` = '' LIMIT 1")
    AssistantWeChat loadByCity(String city);

    @Select("SELECT * FROM `assistantWeChat` " +
            "WHERE `adCode` = #{adCode} LIMIT 1")
    AssistantWeChat loadByAdCode(long adCode);

    @Select("SELECT * FROM `assistantWeChat` " +
            "ORDER BY `adCode` ASC")
    List<AssistantWeChat> loadAll();

    @Delete("DELETE FROM `assistantWeChat` WHERE `adCode` = #{adCode} ")
    int remove(long adCode);
}
