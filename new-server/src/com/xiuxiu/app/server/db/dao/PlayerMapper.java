package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.player.Player;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PlayerMapper {
    @Insert("INSERT INTO `player` " +
            "(`uid`, `name`, `icon`, `sex`, `zone`, `roomId`,  `createTimestamp`, `lastLoginTime`, `lastLogoutTime`, " +
            "`lastLoginIp`, `lat`, `lng`, `money`, `alias`, `tags`, `msgTopV2`, `msgMute`, `clubUids`, `recommend`, `visitCard`, " +
            "`born`, `signature`, `emotion`, `showImage`, `cover`, `privilege`, `ownerClubCnt`, `recharge`, `defaultIcon`, `wechat`, `bizChannel`, `isEmpower`, `isDoneGame`, `bankCard`, `bankCardHolder`) " +
            "VALUES " +
            "(#{uid}, #{name}, #{iconDb}, #{sex}, #{zone}, #{roomId}, #{createTimestamp}, #{lastLoginTime}, #{lastLogoutTime}, " +
            "#{lastLoginIp}, #{lat}, #{lng}, #{moneyDb}, #{alias}, #{tags}, #{msgTopV2}, #{msgMute}, #{clubUids}, #{recommend}, #{visitCard}, " +
            "#{born}, #{signature}, #{emotion}, #{showImageDb}, #{cover}, #{privilege}, #{ownerClubCnt}, #{recharge}, #{defaultIcon}, #{wechat}, #{bizChannel}, #{isEmpower}, #{isDoneGame}, #{bankCard}, #{bankCardHolder})")
    int create(Player player);

    @Select("SELECT * FROM `player` WHERE `uid` = #{playerUid} LIMIT 1")
    @Results({
            @Result(property = "moneyDb", column = "money"),
            @Result(property = "showImageDb", column = "showImage"),
            @Result(property = "iconDb", column = "icon"),
    })
    Player loadByUid(@Param("playerUid") long playerUid);

    @Select("SELECT * FROM `player` WHERE `uid` in #{playerUids}")
    @Results({
            @Result(property = "moneyDb", column = "money"),
            @Result(property = "showImageDb", column = "showImage"),
            @Result(property = "iconDb", column = "icon"),
    })
    List<Player> loadByUids(@Param("playerUids") List<Long> playerUids);

    @Select("SELECT `uid` FROM `player`")
    List<Long> loadAllUid();

    @Update("UPDATE `player` SET `name` = #{name}, `icon` = #{iconDb}, `sex` = #{sex}, `zone` = #{zone}, " +
            "`roomId` = #{roomId}, `lastLoginTime` = #{lastLoginTime}, " +
            "`lastLogoutTime` = #{lastLogoutTime}, `lastLoginIp` = #{lastLoginIp}, `lat` = #{lat}, `lng` = #{lng}, `money` = #{moneyDb}, " +
            "`alias` = #{alias}, `tags` = #{tags}, `msgTopV2` = #{msgTopV2}, " +
            "`msgMute` = #{msgMute}, `clubUids` = #{clubUids}, `recommend` = #{recommend}, `visitCard` = #{visitCard}, " +
            "`born` = #{born}, `signature` = #{signature}, `emotion` = #{emotion}, `showImage` = #{showImageDb}, `cover` = #{cover}, " +
            "`privilege` = #{privilege}, `ownerClubCnt` = #{ownerClubCnt}, " +
            "`recharge` = #{recharge}, `defaultIcon` = #{defaultIcon}, `wechat` = #{wechat}, `isEmpower` = #{isEmpower} , `isDoneGame` = #{isDoneGame}, `bankCard` = #{bankCard}, `bankCardHolder` = #{bankCardHolder} WHERE `uid` = #{uid}")
    int save(Player player);

    @Select("SELECT * FROM `player` LIMIT #{page}, #{pageSize}")
    @Results({
            @Result(property = "moneyDb", column = "money"),
            @Result(property = "showImageDb", column = "showImage"),
            @Result(property = "iconDb", column = "icon"),
    })
    List<Player> loadAllPlayer(@Param("page") int page,
                               @Param("pageSize") int pageSize);

    @Update("UPDATE `player` SET `lastLogoutTime` = #{time} WHERE `uid` >= 300000 AND `uid` <= 400000 AND `lastLogoutTime` = -1")
    void resetAllRobotLoginOutTime(@Param("time") long time);

    @Select("SELECT * FROM `player` WHERE `uid` >= 60000000 AND `uid` < 70000000 LIMIT #{page}, #{pageSize}")
    List<Player> loadAllRobot(@Param("page") int page,
                               @Param("pageSize") int pageSize);
    
}
