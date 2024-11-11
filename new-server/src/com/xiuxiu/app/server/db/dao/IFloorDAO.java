package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.floor.Floor;

import java.util.List;

public interface IFloorDAO extends IBaseDAO<Floor> {
	
    List<Floor> loadAll();

    boolean deleteFloorByUid(long uid);
}
