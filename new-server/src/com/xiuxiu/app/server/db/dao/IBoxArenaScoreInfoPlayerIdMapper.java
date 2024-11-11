package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.BoxArenaScoreInfoPlayerId;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IBoxArenaScoreInfoPlayerIdMapper {
    @Insert("INSERT INTO `boxArenaScorePlayerId` (`uid`, `scoreUid`, `playerUid`) " +
            "VALUES " +
            "(#{uid}, #{scoreUid}, #{playerUid})")
    int create(BoxArenaScoreInfoPlayerId boxArenaScoreInfoPlayerId);

    @Update("UPDATE `boxArenaScorePlayerId` SET `scoreUid` = #{scoreUid}, `playerUid` = #{playerUid} WHERE `uid` = #{uid}")
    int save(BoxArenaScoreInfoPlayerId boxArenaScoreInfoPlayerId);

    @Select("select * from `boxArenaScorePlayerId`")
    @Results({
            @Result(property = "scoreUid", column = "scoreUid"),
            @Result(property = "playerUid", column = "playerUid")
    })
    List<BoxArenaScoreInfoPlayerId> loadAll(@Param("uid") long uid);
}
