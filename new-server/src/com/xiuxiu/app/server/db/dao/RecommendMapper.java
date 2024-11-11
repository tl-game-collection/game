package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.player.Recommend;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface RecommendMapper {
    @Insert("INSERT INTO `recommend` (`uid`, `recommendPlayerUid`, `recommendedPlayerUid`, `groupUid`, `state`, `diamond`, `diamondSum`, `bindingTime`)" +
            " VALUES (#{uid}, #{recommendPlayerUid}, #{recommendedPlayerUid}, #{groupUid}, #{state}, #{diamond}, #{diamondSum}, #{bindingTime})")
    int create(Recommend recommend);

    @Select("SELECT * FROM `recommend` WHERE `recommendPlayerUid` = #{recommendPlayerUid} AND `state` = 1 LIMIT #{begin}, #{size}")
    List<Recommend> loadByRecommendPlayerUid(@Param("recommendPlayerUid") long recommendPlayerUid, @Param("begin") int begin, @Param("size") int size);

    @Select("SELECT * FROM `recommend` WHERE `recommendPlayerUid` = #{recommendPlayerUid} AND `groupUid` = #{groupUid} AND `state` = 1 AND `diamond` = #{diamond} AND `diamondSum` = #{diamondSum} AND `bindingTime` = #{bindingTime}")
    List<Recommend> loadByRecommendPlayerUidAndGroupUid(@Param("recommendPlayerUid") long recommendPlayerUid, @Param("groupUid") long groupUid);

    @Select("SELECT * FROM `recommend` WHERE `groupUid` = #{groupUid} AND `state` = 1")
    List<Recommend> loadByGroupUid(@Param("groupUid") long groupUid);

    @Update("UPDATE `recommend` SET `state` = #{newState}" +
            " WHERE `groupUid` = #{groupUid} AND `recommendPlayerUid` = #{recommendPlayerUid} AND `state` = #{oldState} AND `diamond` = #{diamond} AND `diamondSum` = #{diamondSum} AND `bindingTime` = #{bindingTime}")
    void changeState(@Param("recommendPlayerUid") long recommendPlayerUid, @Param("groupUid") long groupUid,
                     @Param("oldState") long oldState, @Param("newState") long newState);

    @Select("SELECT recommendedPlayerUid FROM `recommend` WHERE `recommendPlayerUid` = #{referrerUid} AND `state` = 1 LIMIT #{begin}, #{size}")
    List<Long> loadByReferrerUid(@Param("referrerUid") long referrerUid, @Param("begin") int begin, @Param("size") int size);
}
