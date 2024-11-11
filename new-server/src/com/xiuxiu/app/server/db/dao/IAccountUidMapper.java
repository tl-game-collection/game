package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.account.AccountUid;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IAccountUidMapper {
    @Insert("INSERT INTO `accountUid` " +
            "(`uid`, `good`, `state`) " +
            "VALUES " +
            "(#{uid}, #{good}, #{state})")
    int create(AccountUid clubUid);

    @Update("UPDATE accountUid SET `good` = #{good} , `state` = #{state} WHERE `uid` = #{uid}")
    int update(AccountUid accountUid);

    @Select("SELECT * FROM `accountUid` WHERE `state` = 1 AND `good` = #{good} LIMIT #{cnt}")
    List<AccountUid> loadUnused(@Param("good") int good,@Param("cnt") int cnt);
}
