package com.xiuxiu.app.server.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xiuxiu.app.server.club.ClubMemberExt;

public interface IClubMemberExtMapper {

    @Insert("INSERT INTO `clubMemberExt` (`uid`, `clubUid`, `playerUid`, `gold`, `rewardValue`, `upTotalScore`, `downTotalScore`, `goldActivityStatus`, `code`, `convert`) VALUES "
            + "(#{uid}, #{clubUid}, #{playerUid}, #{gold}, #{rewardValue}, #{upTotalScore}, #{downTotalScore}, #{goldActivityStatusDb}, #{code},#{convert})")
    int create(ClubMemberExt clubMemberExt);

    @Update("UPDATE `clubMemberExt` SET `gold` = #{gold}, `rewardValue` = #{rewardValue}, `upTotalScore` = #{upTotalScore}, `downTotalScore` = #{downTotalScore}, `goldActivityStatus` = #{goldActivityStatusDb}, `code` = #{code}, `convert` = #{convert} WHERE `uid` = #{uid}")
    int save(ClubMemberExt value);

    @Select("SELECT * FROM `clubMemberExt` WHERE `clubUid` = #{clubUid} ")
    @Results({ @Result(property = "goldActivityStatusDb", column = "goldActivityStatus") })
    List<ClubMemberExt> loadAll(@Param("clubUid") long clubUid);
}
