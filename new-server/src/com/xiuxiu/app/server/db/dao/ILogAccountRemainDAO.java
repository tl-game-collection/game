package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.statistics.LogAccountRemain;

public interface ILogAccountRemainDAO extends IBaseDAO<LogAccountRemain> {
    List<LogAccountRemain> load(long timeBegin, long timeEnd);
}
