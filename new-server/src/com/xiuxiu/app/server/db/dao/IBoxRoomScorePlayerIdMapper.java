package com.xiuxiu.app.server.db.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import com.xiuxiu.app.server.score.BoxRoomScorePlayerId;

public interface IBoxRoomScorePlayerIdMapper {
    
    @Insert("INSERT INTO `boxRoomScorePlayerId` (`uid`, `scoreUid`, `playerUid`, `clubUid`) VALUES (#{uid}, #{scoreUid}, #{playerUid}, #{clubUid})")
    int create(BoxRoomScorePlayerId boxRoomScorePlayerId);

    @Update("UPDATE `boxRoomScorePlayerId` SET `scoreUid` = #{scoreUid}, `playerUid` = #{playerUid}, `clubUid` = #{clubUid} WHERE `uid` = #{uid}")
    int save(BoxRoomScorePlayerId boxRoomScorePlayerId);
}
