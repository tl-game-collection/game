package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IClubInfoMapper {
    
    @Insert("INSERT INTO `clubInfo` (`uid`, `name`, `desc`, `icon`, `clubType`, `ownerId`, `createTime`, `gameDesc`, `parentUid`, `joinParentTime`, `childUidSet`, `memberApplyList`, `mergeApplyList`, `leaveApplyList`, `announcement`, `announcementExpireAt`, `serviceChargeDivide`, `state`, `managerInfo`, `upGoldTreasurer`, `downGoldTreasurer`, `closeStatus`, `lockTime`, `lockByClubUid`, `serviceCharge`, `dToGoldTotal`, `treasurerInfo`) VALUES "
            + "(#{uid}, #{name}, #{desc}, #{icon}, #{clubType}, #{ownerId}, #{createTime}, #{gameDesc}, #{parentUid}, #{joinParentTime}, #{childUidSetDb}, #{memberApplyListDb}, #{mergeApplyListDb}, #{leaveApplyListDb}, #{announcement}, #{announcementExpireAt}, #{serviceChargeDivide}, #{state}, #{managerInfoDb}, #{upGoldTreasurerDb}, #{downGoldTreasurerDb}, #{closeStatusDb}, #{lockTime}, #{lockByClubUid}, #{serviceChargeDb}, #{dToGoldTotal}, #{treasurerInfoDb})")
    int create(ClubInfo clubInfo);

    @Select("SELECT * FROM `clubInfo` WHERE `uid` = #{clubInfoUid}")
    @Results({@Result(property = "childUidSetDb", column = "childUidSet"),
            @Result(property = "memberApplyListDb", column = "memberApplyList"),
            @Result(property = "mergeApplyListDb", column = "mergeApplyList"),
            @Result(property = "leaveApplyListDb", column = "leaveApplyList"),
            @Result(property = "managerInfoDb", column = "managerInfo"),
            @Result(property = "upGoldTreasurerDb", column = "upGoldTreasurer"),
            @Result(property = "downGoldTreasurerDb", column = "downGoldTreasurer"),
            @Result(property = "closeStatusDb", column = "closeStatus"),
            @Result(property = "treasurerInfoDb", column = "treasurerInfo"),
            @Result(property = "serviceChargeDb", column = "serviceCharge")
    })
    ClubInfo loadByUid(@Param("clubInfoUid") long clubInfoUid);

    @Select("SELECT * FROM `clubInfo`")
    @Results({@Result(property = "childUidSetDb", column = "childUidSet"),
            @Result(property = "memberApplyListDb", column = "memberApplyList"),
            @Result(property = "mergeApplyListDb", column = "mergeApplyList"),
            @Result(property = "leaveApplyListDb", column = "leaveApplyList"),
            @Result(property = "managerInfoDb", column = "managerInfo"),
            @Result(property = "upGoldTreasurerDb", column = "upGoldTreasurer"),
            @Result(property = "downGoldTreasurerDb", column = "downGoldTreasurer"),
            @Result(property = "closeStatusDb", column = "closeStatus"),
            @Result(property = "treasurerInfoDb", column = "treasurerInfo"),
            @Result(property = "serviceChargeDb", column = "serviceCharge")
    })
    List<ClubInfo> loadAll();

    @Update("UPDATE `clubInfo` SET `name` = #{name}, `desc` = #{desc}, `icon` = #{icon}, `clubType` = #{clubType}, `ownerId` = #{ownerId}, `createTime` = #{createTime}, `gameDesc` = #{gameDesc}, `parentUid` = #{parentUid}, `joinParentTime` = #{joinParentTime}, `childUidSet` = #{childUidSetDb}, "
            + "`memberApplyList` = #{memberApplyListDb}, `mergeApplyList` = #{mergeApplyListDb}, `leaveApplyList` = #{leaveApplyListDb}, `announcement` = #{announcement}, `announcementExpireAt` = #{announcementExpireAt}, `serviceChargeDivide` = #{serviceChargeDivide}, `state` = #{state}, `managerInfo` = #{managerInfoDb}, `upGoldTreasurer` = #{upGoldTreasurerDb}, `downGoldTreasurer` = #{downGoldTreasurerDb}, `closeStatus` = #{closeStatusDb}, `lockTime` = #{lockTime}, `lockByClubUid` = #{lockByClubUid} , `serviceCharge` = #{serviceChargeDb}, `dToGoldTotal` = #{dToGoldTotal}, `treasurerInfo` = #{treasurerInfoDb} WHERE `uid` = #{uid}")
    int save(ClubInfo clubInfo);

    @Select("SELECT `uid` FROM `clubInfo` LIMIT #{limit} OFFSET #{offset}")
    List<Long> loadByPage(@Param("limit") long limit, @Param("offset") long offset);

    @Select("SELECT COUNT(1) FROM `clubInfo` ")
    Long countAll();

    @Select("SELECT COUNT(name) FROM `clubInfo` WHERE `clubType` = #{clubType} and `name` = #{name}")
    int isExistName(@Param("clubType") int clubType, @Param("name") String name);

    @Select("SELECT * FROM `clubInfo` WHERE `ownerId` = #{ownerId}")
    List<ClubInfo> loadByOwnerId(@Param("ownerId") long ownerId);
}
