package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.box.Box;

import java.util.List;

public interface IBoxDAO extends IBaseDAO<Box> {

    List<Box> loadAll();

    void delete(long uid);
}
