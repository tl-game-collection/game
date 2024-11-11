package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.order.UpDownGoldOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface IUpDownGoldOrderMapper {
    @Insert("INSERT INTO `upDownGoldOrder` " +
            "(`uid`, `clubUid`, `mainClubUid`, `value`, `chargeValue`, `createAt`, `createAtDetail`, `optAt`, `optAtDetail`, `playerUid`, `optPlayerUid`, `bankCard`, `bankCardHolder`, `state`) " + "VALUES " +
            "(#{uid}, #{clubUid}, #{mainClubUid}, #{value}, #{chargeValue}, #{createAt}, #{createAtDetail}, #{optAt}, #{optAtDetail}, #{playerUid}, #{optPlayerUid}, #{bankCard}, #{bankCardHolder}, #{state})")
    int create(UpDownGoldOrder order);

    @Update("UPDATE `upDownGoldOrder` SET" +
            " `clubUid` = #{clubUid}," +
            " `mainClubUid` = #{mainClubUid}," +
            " `value` = #{value}," +
            " `chargeValue` = #{chargeValue}," +
            " `createAt` = #{createAt}," +
            " `createAtDetail` = #{createAtDetail}," +
            " `optAt` = #{optAt}," +
            " `optAtDetail` = #{optAtDetail}," +
            " `playerUid` = #{playerUid}," +
            " `optPlayerUid` = #{optPlayerUid}," +
            " `bankCard` = #{bankCard}," +
            " `bankCardHolder` = #{bankCardHolder}," +
            " `state` = #{state}" +
            " WHERE `uid` = #{uid}")
    int save(UpDownGoldOrder order);

    @Select("SELECT * FROM `upDownGoldOrder` WHERE `uid` = #{uid}")
    UpDownGoldOrder load(@Param("uid") long uid);

    @Select("<script>" +
            " SELECT * FROM `upDownGoldOrder`" +
            " <where>" +
            " <if test='playerUid > 0'>`playerUid`=#{playerUid}</if>" +
            " <if test='uid > 0'>AND `uid`=#{uid}</if>" +
            " <if test='clubUid > 0'>AND `clubUid`=#{clubUid}</if>" +
            " AND `state` = #{state} AND `createAt` = #{createAt}" +
            " </where>" +
            " LIMIT #{begin},#{size}" +
            "</script>")
    List<UpDownGoldOrder> loadByParms(@Param("playerUid") long playerUid, @Param("uid") long uid, @Param("clubUid") long clubUid,
                                      @Param("state") int state, @Param("createAt") long createAt, @Param("begin") int begin, @Param("size") int size);

    @Select("SELECT * FROM `upDownGoldOrder` WHERE `playerUid` = #{playerUid} AND `clubUid` = #{clubUid} AND `state` = #{state} AND `optAt` > #{minTime} ORDER BY createAtDetail DESC LIMIT #{begin},#{size}")
    List<UpDownGoldOrder> loadByPlayerUidAndState(@Param("playerUid") long playerUid, @Param("clubUid") long clubUid, @Param("state") long state, @Param("begin") int begin, @Param("size") int size, @Param("minTime") long minTime);

    @Select("SELECT * FROM `upDownGoldOrder` WHERE `optPlayerUid` = #{optPlayerUid} AND `mainClubUid` = #{mainClubUid} AND `state` = #{state} AND `optAt` > #{minTime} ORDER BY createAtDetail DESC LIMIT #{begin},#{size}")
    List<UpDownGoldOrder> loadByOptPlayerUidAndState(@Param("optPlayerUid") long optPlayerUid, @Param("mainClubUid") long mainClubUid, @Param("state") long state, @Param("begin") int begin, @Param("size") int size, @Param("minTime") long minTime);

    @Select("SELECT * FROM `upDownGoldOrder` WHERE `state` = #{state} ORDER BY createAtDetail DESC")
    List<UpDownGoldOrder> loadByState(@Param("state") int state);

    @Select("SELECT * FROM `upDownGoldOrder` WHERE `mainClubUid` = #{mainClubUid}")
    List<UpDownGoldOrder> loadByMainClubUid(@Param("mainClubUid") long mainClubUid);
}
