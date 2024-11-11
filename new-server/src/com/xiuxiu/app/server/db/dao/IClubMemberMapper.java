package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubMember;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IClubMemberMapper {
    @Insert("INSERT INTO `clubmember`(`uid`,`clubUid`,`playerUid`,`jobType`,`privilege`,`showNick`,`uplinePlayerUid`,`state`,`joinTime`,`goldActivityCount`,`divide`,`divideLine`,`extra`,`convert`,`divideTime`,`divideLineTime`,`onlyUpLineSetGold`,`likeGames`,`treasurer_desc`) VALUES " +
            "(#{uid},#{clubUid},#{playerUid},#{jobType},#{privilege},#{showNick},#{uplinePlayerUid},#{state},#{joinTime},#{goldActivityCountDb},#{divide},#{divideLine},#{extra},#{convert},#{divideTime},#{divideLineTime},#{onlyUpLineSetGold},#{likeGamesDb},#{treasurerDesc})")
    int create(ClubMember info);

    @Update("UPDATE `clubmember` SET `jobType` = #{jobType}, `privilege` = #{privilege}, `showNick` = #{showNick}, `uplinePlayerUid` = #{uplinePlayerUid}, `state` = #{state}, `joinTime` = #{joinTime}"
            + ", `goldActivityCount` = #{goldActivityCountDb}, `divide` = #{divide}, `divideLine` = #{divideLine}, `extra` = #{extra}, `convert` = #{convert}, `divideTime` = #{divideTime}, `divideLineTime` = #{divideLineTime}, `onlyUpLineSetGold` = #{onlyUpLineSetGold}, `likeGames` = #{likeGamesDb}, `treasurer_desc` = #{treasurerDesc} WHERE uid = #{uid}")
    int save(ClubMember info);

    @Delete("DELETE FROM `clubmember` WHERE `clubUid` = #{clubUid} AND `playerUid` = #{playerUid}")
    int delByClubUidAndPlayerUid(@Param("clubUid") long clubUid,@Param("playerUid") long playerUid);

    @Select("SELECT * FROM `clubmember` WHERE `clubUid` = #{clubUid}")
    @Results({ @Result(property = "goldActivityCountDb", column = "goldActivityCount"),@Result(property = "treasurerDesc", column = "treasurer_desc") })
    List<ClubMember> loadAllMemberByClubUid(@Param("clubUid") long clubUid);
    
    @Delete("DELETE FROM `clubmember` WHERE `uid` = #{uid}")
    int deleteByUid(@Param("uid") long uid);
    
    @Select("SELECT * FROM `clubmember` WHERE `playerUid` = #{playerUid}")
    List<ClubMember> loadAllClubByPlayerUid(@Param("playerUid") long playerUid);
    
}
