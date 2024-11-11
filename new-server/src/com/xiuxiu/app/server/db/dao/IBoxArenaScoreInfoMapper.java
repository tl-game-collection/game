package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 18:05
 * @comment:
 */
public interface IBoxArenaScoreInfoMapper {
    @Insert("INSERT INTO `boxArenaScoreInfo` (`uid`, `time`, `score`,`boxUid`) " +
            "VALUES " +
            "(#{uid}, #{time}, #{scoreDb},#{boxUid})")
    int create(BoxArenaScoreInfo boxArenaScoreInfo);

    @Update("UPDATE `boxArenaScoreInfo` SET `time` = #{time}, `score` = #{scoreDb} , `boxUid` = #{boxUid} WHERE `uid` = #{uid}")
    int save(BoxArenaScoreInfo boxArenaScoreInfo);

    @Select("<script>" +
            "SELECT * FROM `boxArenaScoreInfo` WHERE `uid` IN" +
            "<foreach item = 'item' index = 'index' collection = 'uidList' open='(' separator = ',' close = ')'> #{item} </foreach>" +
            "ORDER BY `uid` DESC" +
            "</script>")
    @Results({
            @Result(property = "scoreDb", column = "score")
    })
    List<BoxArenaScoreInfo> loadAll(@Param("uidList") List<Long> uidList);


    @Select("select a.* from `boxArenaScoreInfo` a left join `boxArenaScorePlayerId` b on a.uid=b.scoreUid where b.playerUid=#{playerUid} and a.boxUid=#{boxUid} ORDER BY a.time DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "scoreDb", column = "score"),
            @Result(property = "time", column = "time"),
            @Result(property = "boxUid", column = "boxUid")
    })
    List<BoxArenaScoreInfo> loadAllByBoxUid(@Param("playerUid")Long playerUid,@Param("boxUid")Long boxUid,@Param("begin")int begin,@Param("size")int size);
    
    @Select("select a.* from `boxArenaScoreInfo` a left join `boxArenaScorePlayerId` b on a.uid=b.scoreUid where b.playerUid=#{playerUid} GROUP BY a.boxUid ORDER BY a.time DESC LIMIT #{begin}, #{size}")
    @Results({
            @Result(property = "scoreDb", column = "score"),
            @Result(property = "time", column = "time"),
            @Result(property = "boxUid", column = "boxUid")
    })
    List<BoxArenaScoreInfo> loadAllByPlayerUid(@Param("playerUid")Long playerUid,@Param("begin")int begin,@Param("size")int size);
    
}
