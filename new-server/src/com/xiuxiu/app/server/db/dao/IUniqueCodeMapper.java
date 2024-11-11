package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.uniquecode.UniqueCode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IUniqueCodeMapper {
    @Insert("INSERT INTO `uniqueCode` " +
            "(`type`, `code`, `state`, `param`) " +
            "VALUES " +
            "(#{type}, #{code}, #{state}, #{param})")
    int create(UniqueCode uniqueCode);

    @Update("UPDATE `uniqueCode` SET `state` = #{state} , `param` = #{param} WHERE `type` = #{type} and `code` = #{code}")
    int update(UniqueCode uniqueCode);

    @Select("SELECT * FROM `uniqueCode` WHERE `state` = 1 and `type`= #{type} LIMIT #{cnt}")
    List<UniqueCode> loadUnused(@Param("type") int type, @Param("cnt") int cnt);

    @Select("SELECT * FROM `uniqueCode` WHERE `code` = #{code} and `type`= #{type} LIMIT 1")
    UniqueCode loadByCodeAndType(@Param("code") long code,@Param("type") int type);
}