package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.player.Nickname;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface INicknameMapper {
    @Update("UPDATE `nickname` SET `state`=#{state} WHERE `uid`=#{uid}")
    void save(Nickname nick);

    @Select("SELECT * FROM `nickname` WHERE `state`=(SELECT min(`state`) from `nickname`) ORDER BY uid LIMIT 1")
    Nickname loadOne();
}
