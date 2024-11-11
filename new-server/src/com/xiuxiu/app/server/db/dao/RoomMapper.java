package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.room.normal.RoomInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

public interface RoomMapper {
    @Insert("INSERT INTO `room` (`uid`, `roomId`, `ownerPlayerUid`, `groupUid`, `createTime`, `endTime`, `state`, `gameType`, `gameSubType`, " +
            "`curBureau`, `cost`, `rule`) " +
            "VALUES " +
            "(#{uid}, #{roomId}, #{ownerPlayerUid}, #{groupUid}, #{createTime}, #{endTime}, #{state}, #{gameType}, #{gameSubType}, " +
            "#{curBureau}, #{cost}, #{ruleDb})")
    int create(RoomInfo room);

    @Update("UPDATE `room` SET `endTime` = #{endTime}, `state` = #{state}, `curBureau` = #{curBureau}, `rule` = #{ruleDb} WHERE `uid` = #{uid}")
    int save(RoomInfo room);
}
