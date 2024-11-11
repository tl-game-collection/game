package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.BoxArenaScore;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 17:54
 * @comment:
 */
public interface IBoxArenaScoreMapper {
    @Insert("INSERT INTO `boxArenaScore` (`uid`, `boxUid`, `gameType`, `gameSubType`, `clubUid`, `playerUid`, `beginTime`, " +
            "`endTime`, `bureau`, `score`, `recordUid`, `allCnt`) " +
            "VALUES " +
            "(#{uid}, #{boxUid}, #{gameType}, #{gameSubType}, #{clubUid}, #{playerUid}, #{beginTime}, " +
            "#{endTime}, #{bureau}, #{score}, #{recordUidDb}, #{allCntDb})")
    int create(BoxArenaScore boxArenaScore);

    @Update("UPDATE `boxArenaScore` SET `endTime` = #{endTime}, `bureau` = #{bureau}, `score` = #{score}, `recordUid` = #{recordUidDb}, `allCnt` = #{allCntDb} WHERE `uid` = #{uid}")
    int save(BoxArenaScore boxArenaScore);

    @Select("SELECT * FROM `boxArenaScore` WHERE `uid` = #{uid} LIMIT 1")
    @Results({
            @Result(property = "recordUidDb", column = "recordUid"),
            @Result(property = "allCntDb", column = "allCnt")
    })
    BoxArenaScore loadByUid(@Param("uid") long uid);
}
