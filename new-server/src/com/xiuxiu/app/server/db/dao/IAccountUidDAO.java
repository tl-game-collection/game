package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.account.AccountUid;

import java.util.List;

public interface IAccountUidDAO extends IBaseDAO<AccountUid> {
    List<AccountUid> loadUnused(boolean good,int cnt);
}
