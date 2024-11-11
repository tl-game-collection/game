package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.uniquecode.UniqueCode;

import java.util.List;

public interface IUniqueCodeDAO extends IBaseDAO<UniqueCode> {
    List<UniqueCode> loadUnused(int type, int cnt);
    UniqueCode loadByCodeAndType(long code,int type);
}
