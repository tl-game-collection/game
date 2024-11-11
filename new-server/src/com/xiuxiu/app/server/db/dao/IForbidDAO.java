package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.forbid.Forbid;

public interface IForbidDAO extends IBaseDAO<Forbid> {

    List<Forbid> loadAll();

    boolean delByUid(long uid);

}
