package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.system.LocationInfo;


public interface ILocationInfoDAO extends IBaseDAO<LocationInfo> {
    @Override
    boolean save(LocationInfo info);
    LocationInfo loadByLocation(String location);
}
