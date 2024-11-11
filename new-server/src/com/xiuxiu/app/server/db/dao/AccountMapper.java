package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.account.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AccountMapper {
    @Insert("INSERT INTO `account` (`uid`, `passwd`, `createTime`, `mac`, `phone`, `phoneVer`, `phoneOsVer`, `name`, `icon`, `sex`, `city`, `identityCard`, `otherPlatformToken`, `type`) " +
            "VALUES " +
            "(#{uid}, #{passwd}, #{createTime}, #{mac}, #{phone}, #{phoneVer}, #{phoneOsVer}, #{name}, #{icon}, #{sex}, #{city}, #{identityCard}, #{otherPlatformToken}, #{type})")
    int create(Account account);

    @Update("UPDATE `account` SET " +
            "`passwd` = #{passwd}, " +
            "`phone` = #{phone}, " +
            "`identityCard` = #{identityCard}, " +
            "`payPassword` = #{payPassword}, " +
            "`name` = #{name}, " +
            "`state` = #{state}, " +
            "`otherPlatformToken` = #{otherPlatformToken}, " +
            "`noNeedPayPassword` = #{noNeedPayPassword} " +
            "WHERE `uid` = #{uid}")
    int save(Account account);

    @Select("SELECT * FROM `account` WHERE `phone` = #{phone} LIMIT 1")
    Account getAccountByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM `account` WHERE `otherPlatformToken` = #{platformToken} LIMIT 1")
    Account getAccountByOtherPlatformToken(@Param("platformToken") String platformToken);

    @Select("SELECT * FROM `account` WHERE `uid` = #{uid} LIMIT 1")
    Account getAccountByUid(@Param("uid") long uid);

    @Update("UPDATE `account` SET `payPassword` = #{payPassword} WHERE `uid` = #{uid}")
    int savePayPasswd(Account account);

    @Select("SELECT * FROM `account` WHERE `uid` >= #{uid} ORDER BY `uid` LIMIT #{limitSize}")
    List<Account> loadAccountsByUidStartFrom(@Param("uid") long uid, @Param("limitSize") int limitSize);

    @Update("UPDATE `account` SET `state` = 2 WHERE `uid` = #{uid} AND `state` = 0")
    int banAccount(long uid);

    @Update("UPDATE `account` SET `state` = 0 WHERE `uid` = #{uid} AND `state` = 2")
    int unbanAccount(long uid);

    @Update("UPDATE `account` SET " +
            "`passwd` = #{passwd}, " +
            "`phone` = #{phone}, " +
            "`identityCard` = #{identityCard}, " +
            "`payPassword` = #{payPassword}, " +
            "`name` = #{name}, " +
            "`state` = #{state}, " +
            "`otherPlatformToken` = #{otherPlatformToken}, " +
            "`noNeedPayPassword` = #{noNeedPayPassword} " +
            "WHERE `uid` = #{uid}")
    int update(Account account);
}
