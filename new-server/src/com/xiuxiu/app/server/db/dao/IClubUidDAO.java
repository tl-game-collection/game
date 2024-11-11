package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubUid;

import java.util.List;

public interface IClubUidDAO extends IBaseDAO<ClubUid> {
    List<ClubUid> loadUnused(boolean good, int cnt);
}
