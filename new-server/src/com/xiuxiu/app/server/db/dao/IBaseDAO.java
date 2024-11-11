package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.db.BaseTable;

public interface IBaseDAO<T extends BaseTable> {
    /**
     * 根据主键加载
     * @param uid
     * @return
     */
    T load(long uid);

    /**
     * 保存
     * @param value
     * @return
     */
    boolean save(T value);
}
