package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.mail.Mail;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface MailMapper {
    @Insert("INSERT INTO `mail` (`uid`, `senderPlayerUid`, `receivePlayerUid`, `title`, `content`, `item`, `state`, `sendTime`, `itemState`) " +
            "VALUES " +
            "(#{uid}, #{senderPlayerUid}, #{receivePlayerUid}, #{title}, #{content}, #{itemDb}, #{state}, #{sendTime}, #{itemState})")
    int create(Mail mail);

    @Select("SELECT * FROM `mail` WHERE `receivePlayerUid` = #{playerUid} AND `state` != 2 AND `sendTime` >= #{time}")
    @Results({
            @Result(property = "itemDb", column = "item")
    })
    List<Mail> loadByPlayerUid(@Param("playerUid") long playerUid, @Param("time") long time);

    @Update("UPDATE `mail` SET `state` = #{state}, `itemState` = #{itemState} WHERE `uid` = #{uid}")
    int save(Mail mail);
}
