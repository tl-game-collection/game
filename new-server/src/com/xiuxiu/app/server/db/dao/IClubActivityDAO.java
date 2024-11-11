package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.club.activity.ClubActivity;

public interface IClubActivityDAO extends IBaseDAO<ClubActivity> {

    /**
     * 加载所有亲友圈活动
     * 
     * @return
     */
    List<ClubActivity> loadAll();
}
