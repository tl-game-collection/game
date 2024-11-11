package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.club.ClubMemberExt;

public interface IClubMemberExtDAO extends IBaseDAO<ClubMemberExt>{

    List<ClubMemberExt> loadAll(long clubUid);
}
