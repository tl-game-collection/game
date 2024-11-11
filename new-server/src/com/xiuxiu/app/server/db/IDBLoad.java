package com.xiuxiu.app.server.db;

import com.xiuxiu.app.server.db.dao.IBaseDAO;

import java.util.List;

public interface IDBLoad<T extends BaseTable> {
    String getRedisKey();
    default T loadOne(IBaseDAO<T> dao) {
        return null;
    }
    default List<T> load(IBaseDAO<T> dao) {
        return null;
    }
}
