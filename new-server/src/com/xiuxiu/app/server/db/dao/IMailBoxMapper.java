package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.chat.MailBox;
import com.xiuxiu.app.server.chat.MailBoxUid;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IMailBoxMapper {
    @Insert("INSERT INTO `mailBox` (`uid`, `messageUid`, `messageUidByPlayer`, `toPlayerUid`, `tagPlayerUid`, `fromPlayerUid`, " +
            "`fromGroupUid`,`toGroupUid`, `fromLeagueUid`, `fromPavilionUid`, `messageType`, `contentType`, `message`, `sayTime`, `param`, `state`) VALUES (" +
            "#{uid}, #{messageUid}, #{messageUidByPlayer}, #{toPlayerUid}, #{tagPlayerUid}, #{fromPlayerUid}, " +
            "#{fromGroupUid},#{toGroupUid}, #{fromLeagueUid}, #{fromPavilionUid}, #{messageType}, #{contentType}, #{message}, #{sayTime}, #{paramDb}, #{state})")
    int createMailBox(MailBox mailBox);

    @Insert("INSERT INTO `mailBoxUid` (`uid`, `lastMsgUid`, `lastMsgUidByClient`, `recallMsgUid`) VALUES (#{uid}, #{lastMsgUid}, #{lastMsgUidByClient}, #{recallMsgUidDb})")
    int createMailBoxUid(MailBoxUid mailBoxUid);

    @Select("SELECT * FROM `mailBox` WHERE `messageUid` = #{messageUid}")
    @Results({
            @Result(property = "paramDb", column = "param")
    })
    List<MailBox> loadByMessageUid(@Param("messageUid") long messageUid);

    @Select("SELECT * FROM `mailBox` WHERE `toPlayerUid` = #{playerUid} AND `messageUidByPlayer` > #{beginMessageUid} AND `messageUidByPlayer` <= #{endMessageUid}  ORDER BY sayTime DESC LIMIT 50 ")
    @Results({
            @Result(property = "paramDb", column = "param")
    })
    List<MailBox> loadByPlayerUidWithBeginAndEnd(@Param("playerUid") long playerUid, @Param("beginMessageUid") long beginMessageUid, @Param("endMessageUid") long endMessageUid);

    @Select("<script>" +
            "SELECT * FROM `mailBox` WHERE `toPlayerUid` = #{playerUid} AND `messageUidByPlayer` IN" +
            "<foreach item = 'item' index = 'index' collection = 'msgUidList' open='(' separator = ',' close = ')'> #{item} </foreach>" +
            "ORDER BY sayTime DESC LIMIT 50"+
            "</script>")
    @Results({
            @Result(property = "paramDb", column = "param")
    })
    List<MailBox> loadByPlayerUidAndMsgUids(@Param("playerUid") long playerUid, @Param("msgUidList") List<Long> msgUidList);

    @Select("SELECT * FROM `mailBox` WHERE `toPlayerUid` = #{playerUid} AND `messageUidByPlayer` = #{messageUid} LIMIT 1")
    @Results({
            @Result(property = "paramDb", column = "param")
    })
    MailBox loadByPlayerUidAndMsgUid(@Param("playerUid") long playerUid, @Param("messageUid") long messageUid);

    @Select("SELECT * FROM `mailBoxUid` WHERE `uid` = #{playerUid} LIMIT 1")
    @Results({
            @Result(property = "recallMsgUidDb", column = "recallMsgUid")
    })
    MailBoxUid loadMailBoxUidByPlayerUid(long playerUid);

    @Update("UPDATE `mailBox` SET `state` = #{state}, `message` = #{message}, `contentType` = #{contentType}, `param` = #{paramDb} WHERE `uid` = #{uid}")
    int updateMailBox(MailBox mailBox);

    @Update("UPDATE `mailBoxUid` SET `lastMsgUid` = #{lastMsgUid}, `lastMsgUidByClient` = #{lastMsgUidByClient}, `recallMsgUid` = #{recallMsgUidDb} WHERE `uid` = #{uid}")
    int updateMailBoxUid(MailBoxUid mailBoxUid);

}
