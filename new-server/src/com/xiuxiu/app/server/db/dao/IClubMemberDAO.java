package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubMember;

import java.util.List;

public interface IClubMemberDAO extends IBaseDAO<ClubMember>{
    boolean delByClubUidAndPlayerUid(long clubUid,long playerUid);
    List<ClubMember> loadAllMemberByClubUid(long clubUid);
    
    boolean deleteByUid(long uid);
    
    List<ClubMember> loadAllClubByPlayerUid(long playerUid);
}
